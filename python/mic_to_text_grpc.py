"""This sample client reads from a wav file, translates it through XL8 E2E API, and plays the result to the speaker."""
import time

import pyaudio
import wave
import sys
from e2e_pipe.api.e2e_api_lib import Xl8E2eApiClient

CHUNK = 2048
SAMPLE_WIDTH = 2
SAMPLE_RATE = 16000

xl8_client = Xl8E2eApiClient("localhost", 17777, source_lang="en", target_lang="ko",
                             client_id="stt-demo", mode=Xl8E2eApiClient.SPEECH_TO_TEXT)

# instantiate PyAudio (1)
p = pyaudio.PyAudio()

# open stream (2)
stream = p.open(format=p.get_format_from_width(SAMPLE_WIDTH),
                channels=1,
                rate=SAMPLE_RATE,
                input=True)

start = time.time()
data = stream.read(CHUNK)

while len(data) > 0:
    time.sleep(0.03)
    response, is_partial = xl8_client.translate(data)
    if response and start:
        print("Latency: ", time.time() - start)
        start = None
    if response:
        print("Translated:", response, is_partial)
    data = stream.read(CHUNK)
print("Finished requesting")

response = xl8_client.close()
stream.write(response)

# stop stream (4)
stream.stop_stream()
stream.close()

# close PyAudio (5)
p.terminate()
