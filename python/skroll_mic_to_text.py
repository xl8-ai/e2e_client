"""This sample client that launches a E2E server via XL8 Skroll E2E API and translates audio from micrphone."""
import argparse
import json
import sys
import threading
import time

import pyaudio
import requests
from e2e_pipe.api.e2e_api_lib import Xl8E2eApiClient

CHUNK = 2000
SAMPLE_WIDTH = 2
SAMPLE_RATE = 16000
BYTES_PER_SECOND = SAMPLE_WIDTH * SAMPLE_RATE

parser = argparse.ArgumentParser()
parser.add_argument("--api-key", dest="api_key", help="XL8 Skroll API Key.", required=True)
parser.add_argument("--source", dest="source_lang", default="en", help="Source language. (default: en)")
parser.add_argument("--target", dest="target_lang", default="ko", help="Target language. (default: ko)")
parser.add_argument("--endpoint", dest="endpoint", default="https://api.xl8.ai", help="XL8 Skroll API Endpoint.")
parser.add_argument("--callback", dest="callback", default="", help="Callback URL to receive server status updates.")

args = parser.parse_args()

headers = {
    "Authorization": f"Bearer {args.api_key}"
}

api_request_url = f"{args.endpoint}/v1/e2e/request"
print(f"Requesting for an e2e session to {api_request_url}")
response = requests.post(
    api_request_url,
    headers=headers,
    json={
        "source_language": args.source_lang,
        "target_language": args.target_lang,
        "request_type": "SpeechToText",
        "timeliness": "Interpreting",
        "callback_url": args.callback or "",
    }
)
response_json = response.json()
if response.status_code != 200 or response_json["status"] != 0:
    print("Request failed: ", response_json)
    sys.exit(1)

request_id = response_json["request_id"]
api_status_url = f"{args.endpoint}/v1/e2e/requests/{request_id}"
print(f"Request successful - request_id={request_id}")

print("Waiting for the server to be ready.", end="")
sys.stdout.flush()
fail_count = 0
while True:
    time.sleep(1)
    print(".", end="")
    sys.stdout.flush()
    status_response = requests.get(api_status_url, headers=headers)
    if status_response.status_code != 200:
        print(f"\nStatus API failed ({status_response.status_code}) - {status_response.text}")
        fail_count += 1
        if fail_count >= 3:
            print("Failed 3 times. Aborting..")
            sys.exit(1)

    e2e_status = status_response.json()
    session_status = e2e_status.get("session_status")
    if session_status == "active":
        print("\nServer is now ready!")
        print(json.dumps(e2e_status, indent=4))
        print()
        break
    if session_status != "initializing":
        print("\nFailed to initialize a server:", e2e_status)
        sys.exit(1)

xl8_client = Xl8E2eApiClient(
    e2e_status["host"], e2e_status["port"],
    mode=Xl8E2eApiClient.SPEECH_TO_TEXT,
    session_id=e2e_status["session_id"],
)

queues = []
lock = threading.Lock()
terminated = False


def read_from_microphone():
    """Retrieve audio from microphone."""
    audio = pyaudio.PyAudio()
    stream = audio.open(
        format=audio.get_format_from_width(SAMPLE_WIDTH),
        channels=1,
        rate=SAMPLE_RATE,
        input=True
    )
    print("Microphone is now recording.")
    while not terminated:
        chunk = stream.read(CHUNK, exception_on_overflow=False)
        with lock:
            queues.append(chunk)

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
                data = b''.join(queues)
                queues.clear()
            total_sent += len(data)
            response = xl8_client.translate(data)
            if response.text and start:
                print("Latency: ", time.time() - start)
                start = None
            if response.text:
                print(f'[{"     " if response.is_partial else "Final"}] {response.text} ({response.original})')
        else:
            time.sleep(0.05)
except KeyboardInterrupt:
    print("Interrupted!")
finally:
    terminated = True

print("Waiting for remaining data...")

response = xl8_client.close()
if response.text:
    print(f'[{"     " if response.is_partial else "Final"}] {response.text} ({response.original})')
record_thread.join()

print("Connection has been closed.")
status_response = requests.get(api_status_url, headers=headers)
print(json.dumps(status_response.json(), indent=4))
print()
