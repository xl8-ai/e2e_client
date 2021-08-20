import axios from "axios";

interface InitResponse {
  session_id: string;
}

interface CloseResponse {
  session_id: string;
}

interface TransResponse {
  session_id: string;
  data: {
    text: string;
    original: string;
    time_start_msec: string;
    time_end_msec: string;
    is_partial: boolean;
  };
}

const DEFAULT_ENDPOINT = "https://poc1.worker.xl8.ai/v1/e2e/";
let API_ENDPOINT = DEFAULT_ENDPOINT;

export const setupEndpoint = (endpoint: string) => {
  API_ENDPOINT = endpoint;
};

export const initE2E = async (clientId: string, apiKey: string) => {
  const resp = await axios.post<InitResponse>(API_ENDPOINT + "init", {
    client_id: clientId,
    api_key: apiKey,
    api_type: "SPEECH_TO_TEXT",
    timeliness: "INTERPRETING",
    source_data_format: {
      language_code: "ko",
      audio_format: {
        sample_rate: 16000,
        channels: 1,
      },
    },
    target_data_format: {
      language_code: "en",
      audio_format: {
        sample_rate: 16000,
        channels: 1,
      },
    },
  });
  return resp.data.session_id;
};

export const transE2E = async (sessionId: string, base64Data: string) => {
  const resp = await axios.post<TransResponse>(API_ENDPOINT + "trans", {
    session_id: sessionId,
    data: {
      audio: base64Data,
    },
  });

  return resp.data;
};

export const closeE2E = async (sessionId: string, waitToDrain: boolean) => {
  const resp = await axios.post<CloseResponse>(API_ENDPOINT + "close", {
    session_id: sessionId,
    wait_to_drain: waitToDrain,
  });
};
