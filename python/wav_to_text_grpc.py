"""This sample client reads from a wav file, translates it through XL8 E2E API, and outputs the translated text."""
import argparse
import time
import wave

from e2e_pipe.api.e2e_api_lib import Xl8E2eApiClient

CHUNK = 2000
LOOP_DELAY = 0.03

parser = argparse.ArgumentParser()
parser.add_argument("wave_file", help="Wave file to translate. Must be a 16Khz mono PCM format.")
parser.add_argument("--client_id", dest="client_id", help="Client ID.", required=True)
parser.add_argument("--api_key", dest="api_key", help="API Key.", required=True)
parser.add_argument("--host", dest="host", help="Server host.", required=True)
parser.add_argument("--port", dest="port", default=17777, type=int, help="Server port. (default: 17777)")
parser.add_argument("--source", dest="source_lang", default="en", help="Source language. (default: en)")
parser.add_argument("--target", dest="target_lang", default="ko", help="Target language. (default: ko)")

args = parser.parse_args()

xl8_client = Xl8E2eApiClient(
    args.host, args.port, 
    source_lang=args.source_lang, target_lang=args.target_lang,
    client_id=args.client_id, api_key=args.api_key,
    mode=Xl8E2eApiClient.SPEECH_TO_TEXT, timeliness=Xl8E2eApiClient.INTERPRETING,
)

wf = wave.open(args.wave_file, 'rb')

# read data
data = wf.readframes(CHUNK)
start = time.time()

while len(data) > 0:
    response = xl8_client.translate(data)
    if response.text and start:
        print("Latency: ", time.time() - start)
        start = None
    if response.text:
        print(f'[{"     " if response.is_partial else "Final"}] {response.text}\n        ({response.original})')
    data = wf.readframes(CHUNK)
    time.sleep(LOOP_DELAY)

print("Finished requesting")

response = xl8_client.close()
if response.text:
    print(f'[{"     " if response.is_partial else "Final"}] {response.text}\n        ({response.original})')
