"""
An API layer for Speech-to-Speech translations.

This module works as:
1) an interface to clients (either through gRPC or REST),
2) a launcher that executes Pipelines, and
3) a Step component that sends requests to the first Step component and receives responses from the last Step component
   in the pipeline.
"""
import logging
import sys
import time
import uuid
from concurrent import futures
from threading import Thread
from typing import Dict

import grpc

from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiCloseRequest
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiCloseResponse
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiData
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiInitRequest
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiInitResponse
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiResponseType
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiTransRequest
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiTransResponse
from e2e_pipe.api.e2e_api_layer_proto_pb2_grpc import add_E2eApiServiceServicer_to_server
from e2e_pipe.api.e2e_api_layer_proto_pb2_grpc import E2eApiServiceServicer
from e2e_pipe.api.pipe_config_generate import PipeConfigGenerator
from e2e_pipe.core.base import Component
from e2e_pipe.core.base import LoopResult
from e2e_pipe.core.base import Source
from e2e_pipe.core.base import Step
from e2e_pipe.core.errors import InternalError
from e2e_pipe.core.protos.audio_pb2 import TimedAudioData
from e2e_pipe.core.protos.base_pb2 import PacketType
from e2e_pipe.core.protos.base_pb2 import PipeConfig
from e2e_pipe.core.protos.text_pb2 import TimedTextData
from e2e_pipe.executions.local_pipeline import LocalPipeline

# TODO: Think about how to make AWS Load Balancer to launch a layer and route to it.
# https://aws.amazon.com/blogs/aws/new-application-load-balancer-support-for-end-to-end-http-2-and-grpc/


# pylint: disable=too-many-ancestors
class RelayStep(Step):
    """A Step that relays the pipeline."""

    def __init__(self, pipe_config: PipeConfig) -> None:
        """Initialize SessionConnectionStep."""
        Step.__init__(self, pipe_config)

    def handle_single_loop(self) -> LoopResult:
        """Handle one single loop."""
        return LoopResult(delay=True)

    def wait_for_termination(self) -> None:
        """Wait until queues are cleared up."""
        Source.wait_for_termination(self)


class PipeSession:
    """A class to keep the pipeline process and the thread."""

    GRACE_PERIOD_SECONDS = 60

    def __init__(self, pipe_config: PipeConfig) -> None:
        """Initialize PipeSession."""
        self.logger = logging.getLogger(self.__class__.__name__)
        self.logger.info("%%% Starting a session %%%")

        self.pipeline = LocalPipeline(pipe_config)

        self.relay_step = RelayStep(pipe_config)
        self.relay_step_thread = Thread(target=self.relay_step.main_loop)
        self.relay_step_thread.start()

        self.pipeline.start()
        self.pipeline_thread = Thread(target=self.pipeline.main_loop)
        self.pipeline_thread.start()
        pipe_config.pipeline_server_info.port = self.pipeline.port

        self.logger.info("%%% Started a session %%%")

    def close(self, wait_to_drain: bool) -> None:
        """Close the session."""
        self.logger.info("%%% Closing a session %%%")

        if not wait_to_drain:
            self.pipeline.close(source_only=False)
        self.relay_step_thread.join(timeout=self.GRACE_PERIOD_SECONDS)
        self.pipeline_thread.join(timeout=self.GRACE_PERIOD_SECONDS)

        self.logger.info("%%% Closed a session %%%")


class S2sApiLayer(E2eApiServiceServicer):
    """The Speech-to-Speech API Layer class."""

    DEFAULT_MAX_WORKERS = 64
    DEFAULT_PORT = 17777
    GRACE_PERIOD_SECONDS = 60
    LOOP_DELAY_SECONDS = 0.3

    def __init__(self) -> None:
        """Initialize S2sApiLayer."""
        E2eApiServiceServicer.__init__(self)

        self.server = grpc.server(futures.ThreadPoolExecutor(max_workers=self.DEFAULT_MAX_WORKERS))
        add_E2eApiServiceServicer_to_server(self, self.server)
        self.server.add_insecure_port(f"[::]:{self.DEFAULT_PORT}")
        self.server.start()

        self.sessions: Dict[str, PipeSession] = {}

        self.termination_requested = False

    def main_loop(self) -> int:
        """Run the main loop."""
        while not self.termination_requested:
            time.sleep(self.LOOP_DELAY_SECONDS)
        return 0

    def close(self) -> None:
        """Close the layer."""
        self.server.stop(self.GRACE_PERIOD_SECONDS)
        self.server.wait_for_termination(self.GRACE_PERIOD_SECONDS)

    def InitE2e(self, request: E2eApiInitRequest, context: grpc.ServicerContext) -> E2eApiInitResponse:
        """Initialize the API service."""
        session_id = uuid.uuid4().hex
        pipe_config = PipeConfigGenerator.generate_pipe_config(request)
        self.sessions[session_id] = PipeSession(pipe_config)

        return E2eApiInitResponse(session_id=session_id)

    @staticmethod
    def fill_response(session: PipeSession, response_data: E2eApiData) -> None:
        """Fill the response_data from the queue in the session.relay_step."""
        while session.relay_step.upstream_queue:
            packet = session.relay_step.upstream_queue.pop(0)
            audio_data = session.relay_step.find_message(packet.data, TimedAudioData)
            text_data = session.relay_step.find_message(packet.data, TimedTextData)

            if audio_data:
                response_data.audio += audio_data.data
            elif text_data:
                joiner = " " if response_data.data.text and text_data.data else ""
                response_data.text += joiner + text_data.data
            else:
                raise InternalError("Expected audio or text data")

    def TransE2e(self, request: E2eApiTransRequest, context: grpc.ServicerContext) -> E2eApiTransResponse:
        """Process a translation request."""
        if request.session_id not in self.sessions:
            return E2eApiTransResponse(
                type=E2eApiResponseType.E2E_API_RESPONSE_ERROR,
                error_message=f"Non-existing session id {request.session_id}",
            )
        session = self.sessions[request.session_id]

        packet = session.relay_step.new_packet(PacketType.REQUEST_FLOW)
        if request.data.audio:
            audio_data = TimedAudioData()
            audio_data.data = request.data.audio
            Component.add_message(packet.data, audio_data)
        elif request.data.text:
            text_data = TimedTextData()
            text_data.text = request.data.text
            Component.add_message(packet.data, text_data)

        session.relay_step.downstream_queue.append(packet)

        response = E2eApiTransResponse(session_id=request.session_id)
        self.fill_response(session, response.data)

        return response

    def CloseE2e(self, request: E2eApiCloseRequest, context: grpc.ServicerContext) -> E2eApiCloseResponse:
        """Close the API service."""
        if request.session_id not in self.sessions:
            return E2eApiCloseResponse(
                type=E2eApiResponseType.E2E_API_RESPONSE_ERROR,
                error_message=f"Non-existing session id {request.session_id}",
            )
        session = self.sessions[request.session_id]

        if request.wait_to_drain:
            packet = session.relay_step.new_packet(PacketType.REQUEST_FLOW)
            packet.end_of_stream = True
            session.relay_step.downstream_queue.append(packet)

        # TODO: This should close the downstream of the relay step first. While the relay step is waiting for the
        # downstream queue to be empty, the fill_response() below should read and drain the queue.
        session.close(request.wait_to_drain)

        response = E2eApiCloseResponse(session_id=request.session_id)
        self.fill_response(session, response.data)
        return response


if __name__ == "__main__":
    sys.exit(S2sApiLayer().main_loop())
