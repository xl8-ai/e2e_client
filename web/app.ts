import RecordRTC, { StereoAudioRecorder } from "recordrtc";
import { initE2E, transE2E } from "./api";
import { findLastMessageElement, appendMessage, scrollMessages } from "./html";
import { blobToBase64 } from "./utils";

type onMicrophone = (mic: MediaStream) => void;

const CHUNK = 4096;
const PERSON_IMG = "https://image.flaticon.com/icons/svg/145/145867.svg";

let audio: HTMLAudioElement = document.querySelector("audio")!;
let recorder: RecordRTC | null;
let SESSION_ID = "";
let wasLastMessagePartial = false;

function captureMicrophone(callback: onMicrophone) {
  if (
    typeof navigator.mediaDevices === "undefined" ||
    !navigator.mediaDevices.getUserMedia
  ) {
    alert("This browser does not supports WebRTC getUserMedia API.");

    if (!!navigator.getUserMedia) {
      alert("This browser seems supporting deprecated getUserMedia API.");
    }
  }

  navigator.mediaDevices
    .getUserMedia({
      audio: {
        echoCancellation: false,
      },
    })
    .then(function (mic) {
      callback(mic);
    })
    .catch(function (error) {
      alert("Unable to capture your microphone. Please check console logs.");
      console.error(error);
    });
}

function startRecording(microphone: MediaStream) {
  audio.muted = true;
  audio.srcObject = microphone;

  if (recorder) {
    recorder.destroy();
    recorder = null;
  }

  recorder = new RecordRTC(microphone, {
    type: "audio",
    mimeType: "audio/wav",
    numberOfAudioChannels: 1,
    recorderType: StereoAudioRecorder,
    desiredSampRate: 16000,
    bufferSize: 2048,
    timeSlice: 500,
    ondataavailable: onRecordDataAvailable,
  });

  console.log("startRecording");
  recorder.startRecording();
}

async function onRecordDataAvailable(blob: Blob) {
  const base64Data = (await blobToBase64(blob)) as string;
  const resp = await transE2E(SESSION_ID, base64Data);
  if (!resp.data || !resp.data.text) {
    return;
  }

  const text = resp.data.text;
  const lastTextElem = findLastMessageElement() as HTMLElement;
  if (wasLastMessagePartial && lastTextElem) {
    lastTextElem.innerText = text;
    scrollMessages();
  } else {
    appendMessage("Speaker", PERSON_IMG, "left", text);
  }

  wasLastMessagePartial = resp.data.is_partial || false;
}

console.log("init");

initE2E("sis", "sis").then((id) => {
  console.log(`got session id ${id}`);
  SESSION_ID = id;
  captureMicrophone(function (mic) {
    startRecording(mic);
  });
});
