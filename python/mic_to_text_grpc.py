"""This sample client translates the audio from microphone through XL8 E2E API, and plays the result to the speaker."""
import argparse
import threading
import time

import pyaudio
from e2e_pipe.api.e2e_api_lib import Xl8E2eApiClient

CHUNK = 2000
SAMPLE_WIDTH = 2
SAMPLE_RATE = 16000
BYTES_PER_SECOND = SAMPLE_WIDTH * SAMPLE_RATE

parser = argparse.ArgumentParser()
parser.add_argument("--client_id", dest="client_id", help="Client ID.", required=True)
parser.add_argument("--api_key", dest="api_key", help="API Key.", required=True)
parser.add_argument("--host", dest="host", help="Server host.", required=True)
parser.add_argument("--port", dest="port", default=17777, type=int, help="Server port. (default: 17777)")
parser.add_argument("--source", dest="source_lang", default="en", help="Source language. (default: en)")
parser.add_argument("--target", dest="target_lang", default="ko", help="Target language. (default: ko)")
parser.add_argument("--session_id", dest="session_id", default=None, help="Attach to an existing session.")
parser.add_argument("--token", dest="user_token", default=None, help="User token.")
parser.add_argument(
    "--max_sentence_duration", dest="max_sentence_duration", default=10, help="Maximum duration of a sentence."
)

args = parser.parse_args()

xl8_client = Xl8E2eApiClient(
    args.host,
    args.port,
    source_lang=args.source_lang,
    target_langs=args.target_lang.split(","),
    client_id=args.client_id,
    api_key=args.api_key,
    mode=Xl8E2eApiClient.SPEECH_TO_TEXT,
    timeliness=Xl8E2eApiClient.INTERPRETING,
    session_id=args.session_id,
    user_token=args.user_token,
    max_sentence_duration=args.max_sentence_duration,
)

print("Connected to the server.")

queues = []
lock = threading.Lock()
terminated = False


def read_from_microphone():
    """Retrieve audio from microphone."""
    audio = pyaudio.PyAudio()
    stream = audio.open(format=audio.get_format_from_width(SAMPLE_WIDTH), channels=1, rate=SAMPLE_RATE, input=True)
    print("Microphone is now recording.")
    while not terminated:
        data = stream.read(CHUNK, exception_on_overflow=False)
        with lock:
            queues.append(data)

    audio.terminate()


record_thread = threading.Thread(target=read_from_microphone)
record_thread.start()
start = time.time()

try:
    total_sent = 0
    created = time.time()
    while True:
        if queues:
            with lock:
                data = b"".join(queues)
                queues.clear()
            total_sent += len(data)
            response = xl8_client.translate(data)
            if response.text and start:
                print("Latency: ", time.time() - start)
                start = None
            if response.text:
                print(
                    f'[{"     " if response.is_partial else "Final"}] {response.multilingual_text} ({response.original})'
                )
        else:
            time.sleep(0.05)
except KeyboardInterrupt:
    print("Interrupted!")
finally:
    terminated = True

print("Waiting for remaining data...")

response = xl8_client.close()
if response.text:
    print(f'[{"     " if response.is_partial else "Final"}] {response.multilingual_text} ({response.original})')
record_thread.join()
