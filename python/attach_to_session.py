"""This sample client reads from a wav file, translates it through XL8 E2E API, and plays the result to the speaker."""
import time

import wave
import sys
from e2e_pipe.api.e2e_api_lib import Xl8E2eApiClient

CHUNK = 2048

if len(sys.argv) < 4:
    print("Usage: %s filename.wav host session_id" % sys.argv[0])
    sys.exit(-1)

server_host = sys.argv[2]
session_id = sys.argv[3]

xl8_client = Xl8E2eApiClient(server_host, 17777, source_lang="en", target_lang="ko", client_id="client_id", api_key="api_key", mode=Xl8E2eApiClient.SPEECH_TO_TEXT, session_id=session_id)

wf = wave.open(sys.argv[1], 'rb')

# read data
data = wf.readframes(CHUNK)

start = time.time()

while len(data) > 0:
    time.sleep(0.03)
    response = xl8_client.translate(data)
    if response[0] and start:
        print("Latency: ", time.time() - start)
        start = None
    if response[0]:
        print(response)
    data = wf.readframes(CHUNK)
print("Finished requesting")

response = xl8_client.close()
print("sending close")
print(response)
