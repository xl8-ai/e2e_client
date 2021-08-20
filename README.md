# Sample clients for XL8 E2E API

## Introduction

This repository contains sample clients for XL8 E2E API in different languages.

 * `python`: Python gRPC clients.
 * `web`: Typescript REST API client.
 * `java`: Java gRPC client.

## Downloading gRPC protocol files

To run a gRPC client, you must download the gRPC protocol files from a separate location. `fetch_proto.sh` script will download the required files and put them in the correct locations.

```
# sh fetch_proto.sh
```

You may also download the zip files manually.

* [Python](https://s3.amazonaws.com/static.xl8.ai/proto/master/e2e_api_python.zip)
* [Java](https://s3.amazonaws.com/static.xl8.ai/proto/master/e2e_api_java.zip)
* [Go](https://s3.amazonaws.com/static.xl8.ai/proto/master/e2e_api_golang.zip)


Please contact us if you need to use a different language.

## Protocol documentation

[Click here](https://s3.amazonaws.com/static.xl8.ai/proto/master/index.html) to view the gRPC protocol documentation.

## Running Python gRPC clients

To run the Python clients, you need to install the required libraries in `requirements.txt`. We recommend creating a separate virtualenv.

```
# cd python
# virtualenv venv
# source venv/bin/activate
# pip install -r requirements.txt
# python mic_to_text_grpc.py --client_id=CLIENTID --api_key=APIKEY --host SERVER_HOST --source=en --target=ko
```

Some clients use PyAudio, which may require you to install some packages first. Please refer to [the installation section of the PyAudio website](https://people.csail.mit.edu/hubert/pyaudio/).

There are 3 sample clients in the `python` directory.

* `mic_to_text_grpc.py` records and sends the audio from your microphone to the server, and outputs the translated text to the console in real-time.
* `wav_to_text_grpc.py` sends a WAV file to the server and outputs the translated text to the console.
* `wav_to_speaker_grpc.py` sends a WAV file to the server and plays the translated audio to the speaker directly.
