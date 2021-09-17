"""This sample client reads from a wav file, translates it through XL8 E2E API, and plays the result to the speaker."""
from dataclasses import dataclass
from typing import Optional
from typing import Union

import grpc
from e2e_pipe.api.e2e_api_layer_proto_pb2 import ApiType
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiCloseRequest
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiInitRequest
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiResponseType
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiTransRequest
from e2e_pipe.api.e2e_api_layer_proto_pb2 import Timeliness
from e2e_pipe.api.e2e_api_layer_proto_pb2_grpc import E2eApiServiceStub

@dataclass
class Xl8E2eApiTextResponse:
    text: str
    original: str = None
    is_partial: bool = False
    time_start_msec: int = 0
    time_end_msec: int = 0

class Xl8E2eApiClient:
    """A end-to-end translation client."""

    SPEECH_TO_SPEECH = 1
    SPEECH_TO_TEXT = 2

    REALTIME = Timeliness.REALTIME
    BATCH = Timeliness.BATCH
    INTERPRETING = Timeliness.INTERPRETING

    def __init__(
            self,
            address: str,
            port: int,
            source_lang: str = "",
            target_lang: str = "",
            client_id: str = "",
            api_key: str = "",
            mode: int = SPEECH_TO_SPEECH,
            timeliness: Optional[int] = None,
            session_id: str = None,
    ) -> None:
        """Initialize E2eApiClient."""
        channel = grpc.insecure_channel(f"{address}:{port}")
        self.stub = E2eApiServiceStub(channel)
        if not session_id:
            request = E2eApiInitRequest(client_id=client_id, api_key=api_key)
            request.source_data_format.language_code = source_lang
            request.source_data_format.audio_format.sample_rate = 16000
            request.source_data_format.audio_format.channels = 1
            request.target_data_format.language_code = target_lang
            
            if mode == Xl8E2eApiClient.SPEECH_TO_SPEECH:
                request.api_type = ApiType.SPEECH_TO_SPEECH
                request.timeliness = Timeliness.REALTIME if timeliness is None else timeliness
                request.target_data_format.audio_format.sample_rate = 16000
                request.target_data_format.audio_format.channels = 1
            elif mode == Xl8E2eApiClient.SPEECH_TO_TEXT:
                request.api_type = ApiType.SPEECH_TO_TEXT
                request.timeliness = Timeliness.INTERPRETING if timeliness is None else timeliness
            else:
                raise RuntimeError(f"Invalid mode: {mode}")

            request.api_key = api_key

            response = self.stub.InitE2e(request)
            if response.type != E2eApiResponseType.E2E_API_RESPONSE_SUCCESS:
                raise RuntimeError("Error while initializing: " + response.error.error_message)
            session_id = response.session_id

        self.session_id = session_id
        self.mode = mode

    def translate(self, audio_data: bytes) -> Union[bytes, Xl8E2eApiTextResponse]:
        """Translate an input audio chunk and return a translated audio chunk."""
        request = E2eApiTransRequest(session_id=self.session_id)
        request.data.audio = audio_data

        response = self.stub.TransE2e(request)

        if response.type != E2eApiResponseType.E2E_API_RESPONSE_SUCCESS:
            raise RuntimeError("Error while translating: " + response.error.error_message)

        if self.mode == Xl8E2eApiClient.SPEECH_TO_SPEECH:
            return response.data.audio

        return Xl8E2eApiTextResponse(
            text=response.data.text,
            original=response.data.original,
            is_partial=response.data.is_partial,
            time_start_msec=response.data.time_start_msec, 
            time_end_msec=response.data.time_end_msec
        )
        
    def close(self, wait_to_drain: bool = True) -> Union[bytes, Xl8E2eApiTextResponse]:
        """Close the session and return the remaining translated audio."""
        request = E2eApiCloseRequest(session_id=self.session_id, wait_to_drain=wait_to_drain)

        response = self.stub.CloseE2e(request)
        if response.type != E2eApiResponseType.E2E_API_RESPONSE_SUCCESS:
            raise RuntimeError("Error while closing" + response.error.error_message)

        if self.mode == Xl8E2eApiClient.SPEECH_TO_SPEECH:
            return response.data.audio

        return Xl8E2eApiTextResponse(
            text=response.data.text,
            original=response.data.original,
            is_partial=response.data.is_partial,
            time_start_msec=response.data.time_start_msec, 
            time_end_msec=response.data.time_end_msec
        )
