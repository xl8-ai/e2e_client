import time

import grpc
import pyaudio
import wave
import sys
from e2e_pipe.api.e2e_api_layer_proto_pb2 import ApiType, Timeliness
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiCloseRequest
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiInitRequest
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiResponseType
from e2e_pipe.api.e2e_api_layer_proto_pb2 import E2eApiTransRequest
from e2e_pipe.api.e2e_api_layer_proto_pb2_grpc import E2eApiServiceStub


class Xl8E2eApiClient:
    """A end-to-end translation client."""

    def __init__(self) -> None:
        """Initialize E2eApiClient."""
        channel = grpc.insecure_channel("localhost:17777")
        self.stub = E2eApiServiceStub(channel)
        request = E2eApiInitRequest(client_id="sis", api_type=ApiType.SPEECH_TO_SPEECH, timeliness=Timeliness.REALTIME)
        request.source_data_format.language_code = "en"
        request.source_data_format.audio_format.sample_rate = 16000
        request.source_data_format.audio_format.channels = 1
        request.target_data_format.language_code = "ko"
        request.target_data_format.audio_format.sample_rate = 16000
        request.target_data_format.audio_format.channels = 1

        response = self.stub.InitE2e(request)
        if response.type != E2eApiResponseType.E2E_API_RESPONSE_SUCCESS:
            raise RuntimeError("Error while initializing")
        self.session_id = response.session_id

    def translate(self, audio_data: bytes) -> bytes:
        """Translate an input audio chunk and return a translated audio chunk."""
        request = E2eApiTransRequest(session_id=self.session_id)
        request.data.audio = audio_data

        response = self.stub.TransE2e(request)
        if response.type != E2eApiResponseType.E2E_API_RESPONSE_SUCCESS:
            raise RuntimeError("Error while translating")

        return response.data.audio

    def close(self, wait_to_drain: bool = True) -> bytes:
        """Close the session and return the remaining translated audio."""
        request = E2eApiCloseRequest(session_id=self.session_id, wait_to_drain=wait_to_drain)

        response = self.stub.CloseE2e(request)
        if response.type != E2eApiResponseType.E2E_API_RESPONSE_SUCCESS:
            raise RuntimeError("Error while translating")

        return response.data.audio


CHUNK = 2048

if len(sys.argv) < 2:
    print("Plays a wave file.\n\nUsage: %s filename.wav" % sys.argv[0])
    sys.exit(-1)

xl8_client = Xl8E2eApiClient()

wf = wave.open(sys.argv[1], 'rb')
ww = wave.open(sys.argv[1] + ".translated.wav", 'wb')
ww.setsampwidth(wf.getsampwidth())
ww.setnchannels(wf.getnchannels())
ww.setframerate(wf.getframerate())

# instantiate PyAudio (1)
p = pyaudio.PyAudio()

# open stream (2)
stream = p.open(format=p.get_format_from_width(wf.getsampwidth()),
                channels=wf.getnchannels(),
                rate=wf.getframerate(),
                output=True)

# read data
data = wf.readframes(CHUNK)

while len(data) > 0:
    time.sleep(0.03)
    response = xl8_client.translate(data)
    ww.writeframes(response)
    stream.write(response)
    data = wf.readframes(CHUNK)
print("Finished requesting")

response = xl8_client.close()
ww.writeframes(response)
ww.close()
stream.write(response)

# stop stream (4)
stream.stop_stream()
stream.close()

# close PyAudio (5)
p.terminate()
