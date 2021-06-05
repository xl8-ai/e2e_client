"""This sample client reads from a wav file, translates it through XL8 E2E API, and plays the result to the speaker."""
import time

import pyaudio
import wave
import sys
from e2e_pipe.api.e2e_api_lib import Xl8E2eApiClient

CHUNK = 2048

if len(sys.argv) < 2:
    print("Usage: %s filename.wav" % sys.argv[0])
    sys.exit(-1)

xl8_client = Xl8E2eApiClient("3.91.11.232", 17777, source_lang="en", target_lang="ko", client_id="sis", api_key="sis")

wf = wave.open(sys.argv[1], 'rb')

# instantiate PyAudio (1)
p = pyaudio.PyAudio()

# open stream (2)
stream = p.open(format=p.get_format_from_width(wf.getsampwidth()),
                channels=wf.getnchannels(),
                rate=wf.getframerate(),
                output=True)

# read data
data = wf.readframes(CHUNK)

start = time.time()

while len(data) > 0:
    time.sleep(0.03)
    response = xl8_client.translate(data)
    if response and start:
        print("Latency: ", time.time() - start)
        start = None
    stream.write(response)
    data = wf.readframes(CHUNK)
print("Finished requesting")

response = xl8_client.close()
stream.write(response)

# stop stream (4)
stream.stop_stream()
stream.close()

# close PyAudio (5)
p.terminate()
