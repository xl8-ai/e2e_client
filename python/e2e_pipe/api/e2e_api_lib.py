"""This sample client reads from a wav file, translates it through XL8 E2E API, and plays the result to the speaker."""
import grpc
from typing import Tuple
from typing import Union
from e2e_pipe.api.e2e_api_layer_proto_pb2 import ApiType, Timeliness
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiCloseRequest
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiInitRequest
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiResponseType
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiTransRequest
from e2e_pipe.api.e2e_api_layer_proto_pb2_grpc import E2eApiServiceStub


class Xl8E2eApiClient:
    """A end-to-end translation client."""

    SPEECH_TO_SPEECH = 1
    SPEECH_TO_TEXT = 2

    def __init__(self, address: str, port: int, source_lang: str, target_lang: str,
                 client_id: str, api_key: str, mode: int = SPEECH_TO_SPEECH) -> None:
        """Initialize E2eApiClient."""
        channel = grpc.insecure_channel(f"{address}:{port}")
        self.stub = E2eApiServiceStub(channel)
        if mode == Xl8E2eApiClient.SPEECH_TO_SPEECH:
            request = E2eApiInitRequest(client_id=client_id, api_type=ApiType.SPEECH_TO_SPEECH, timeliness=Timeliness.REALTIME)
            request.target_data_format.audio_format.sample_rate = 16000
            request.target_data_format.audio_format.channels = 1
        elif mode == Xl8E2eApiClient.SPEECH_TO_TEXT:
            request = E2eApiInitRequest(client_id=client_id, api_type=ApiType.SPEECH_TO_TEXT, timeliness=Timeliness.INTERPRETING)
        else:
            raise RuntimeError(f"Invalid mode: {mode}")

        request.source_data_format.language_code = source_lang
        request.source_data_format.audio_format.sample_rate = 16000
        request.source_data_format.audio_format.channels = 1
        request.target_data_format.language_code = target_lang
        request.api_key = api_key

        response = self.stub.InitE2e(request)
        if response.type != E2eApiResponseType.E2E_API_RESPONSE_SUCCESS:
            raise RuntimeError("Error while initializing")
        self.session_id = response.session_id
        self.mode = mode

    def translate(self, audio_data: bytes) -> Union[bytes, Tuple[str, str, bool]]:
        """Translate an input audio chunk and return a translated audio chunk."""
        request = E2eApiTransRequest(session_id=self.session_id)
        request.data.audio = audio_data

        response = self.stub.TransE2e(request)
        if response.type != E2eApiResponseType.E2E_API_RESPONSE_SUCCESS:
            raise RuntimeError("Error while translating")

        if self.mode == Xl8E2eApiClient.SPEECH_TO_SPEECH:
            return response.data.audio
        return response.data.text, response.data.original, response.data.is_partial

    def close(self, wait_to_drain: bool = True) -> bytes:
        """Close the session and return the remaining translated audio."""
        request = E2eApiCloseRequest(session_id=self.session_id, wait_to_drain=wait_to_drain)

        response = self.stub.CloseE2e(request)
        if response.type != E2eApiResponseType.E2E_API_RESPONSE_SUCCESS:
            raise RuntimeError("Error while translating")

        return response.data.audio

