"""This sample client reads from a wav file, translates it through XL8 E2E API, and plays the result to the speaker."""
import time

import threading
import pyaudio
import wave
import sys
from e2e_pipe.api.e2e_api_lib import Xl8E2eApiClient

CHUNK = 2048
SAMPLE_WIDTH = 2
SAMPLE_RATE = 16000

BYTES_PER_SECOND = SAMPLE_WIDTH * SAMPLE_RATE

if len(sys.argv) < 4:
    print("Usage: %s server-host source-lang target-lang" % sys.argv[0])
    sys.exit(-1)

host = sys.argv[1]
source_lang = sys.argv[2]
target_lang = sys.argv[3]

xl8_client = Xl8E2eApiClient(host, 17777, source_lang=source_lang, target_lang=target_lang,
                             client_id="client_id", api_key="api_key", mode=Xl8E2eApiClient.SPEECH_TO_TEXT)

wf = wave.open("mic_output.wav", "wb")
wf.setnchannels(1)
wf.setsampwidth(SAMPLE_WIDTH)
wf.setframerate(SAMPLE_RATE)
queues = []
lock = threading.Lock()
terminated = False
def record_thread():
    # instantiate PyAudio (1)
    p = pyaudio.PyAudio()

    # open stream (2)
    stream = p.open(format=p.get_format_from_width(SAMPLE_WIDTH),
                    channels=1,
                    rate=SAMPLE_RATE,
                    input=True)
    print("Open straem")
    while not terminated:
        data = stream.read(CHUNK, exception_on_overflow=False)
        with lock:
            queues.append(data)

t = threading.Thread(target=record_thread)
t.start()
start = time.time()

try:
    total_sent = 0
    created = time.time()
    while True:
        if queues:
            with lock:
                data = b''.join(queues)
                queues.clear()
            wf.writeframes(data)
            total_sent += len(data)
            print("Sent: %d (%ss)" % (len(data), total_sent /BYTES_PER_SECOND))
            response, original, is_partial, start_msec, end_msec = xl8_client.translate(data)
            if response and start:
                print("Latency: ", time.time() - start)
                start = None
            if response:
                print("Translated:", response, original, is_partial, start_msec, end_msec)
        else:
            time.sleep(0.03)    
finally:
    terminated = True
print("Finished requesting")

response = xl8_client.close()
stream.write(response)

# stop stream (4)
stream.stop_stream()
stream.close()

# close PyAudio (5)
p.terminate()
