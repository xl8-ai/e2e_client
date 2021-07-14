import axios from "axios";

interface InitResponse {
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

const API_ENDPOINT = "http://3.91.88.129/v1/e2e/";

export const initE2E = async () => {
  const resp = await axios.post<InitResponse>(API_ENDPOINT + "init", {
    client_id: "sis",
    api_key: "sis",
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
