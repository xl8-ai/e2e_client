"""This sample client reads from a wav file, translates it through XL8 E2E API, and plays the result to the speaker."""
import flask
from flask_cors import CORS
import logging
import json
import time
import threading
import traceback

import pyaudio
import wave
import sys
from e2e_pipe.api.e2e_api_lib import Xl8E2eApiClient

CHUNK = 4096
SAMPLE_WIDTH = 2
SAMPLE_RATE = 16000


app = flask.Flask(__name__)
app.config["DEBUG"] = False
CORS(app)

log = logging.getLogger('werkzeug')
log.setLevel(logging.ERROR)

result_queue = []

@app.route('/', methods=['GET'])
def fetch():
    data = []
    while result_queue:
        result = result_queue.pop(0)
        data.append(result)
    return json.dumps(data, ensure_ascii=False, indent=2)

thread_handle = threading.Thread(target=app.run)
thread_handle.start()

xl8_client = Xl8E2eApiClient("3.91.11.232", 17777, source_lang="en", target_lang="ar",
                             client_id="non-sis", mode=Xl8E2eApiClient.SPEECH_TO_TEXT, api_key="")

# instantiate PyAudio (1)
p = pyaudio.PyAudio()

# open stream (2)
stream = p.open(format=p.get_format_from_width(SAMPLE_WIDTH), channels=1, rate=SAMPLE_RATE, input=True)

start = time.time()
data = stream.read(CHUNK, exception_on_overflow=False)

while len(data) > 0:
    time.sleep(0.02)
    response, original, is_partial = xl8_client.translate(data)
    if response and start:
        print("Latency: ", time.time() - start)
        start = None
    if response:
        print(response, original, is_partial)
        result_queue.append({"response": response, "original": original, "is_partial": is_partial})
    try:
        data = stream.read(CHUNK, exception_on_overflow=False)
    except:
        traceback.print_exc()
        time.sleep(0.3)
        data = stream.read(CHUNK, exception_on_overflow=False)
print("Finished requesting")

response = xl8_client.close()
stream.write(response)

# stop stream (4)
stream.stop_stream()
stream.close()

# close PyAudio (5)
p.terminate()
