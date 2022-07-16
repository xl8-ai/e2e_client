package example;

import e2e_pipe.api.E2EApiLayerProto;
import e2e_pipe.api.Xl8E2eApiClient;
import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.AudioSystem;


class MicToTextGrpc {
    static int CHUNK_SIZE = 4000;
    static String SERVER_HOST = "127.0.0.1";
    static String SOURCE_LANG = "ko";
    static String TARGET_LANG = "en";
    static String CLIENT_ID = "demo";
    static String API_KEY = "demo";
    
    private static class MicrophoneReader implements Runnable {
        private Thread thread;
        private ByteArrayOutputStream outputStream;
        private boolean isStopped = false;
        public MicrophoneReader(ByteArrayOutputStream outputStream) {
            super();
            this.outputStream = outputStream;
        }
        
        public void start() {
            this.thread = new Thread(this);
            this.thread.start();
        }
        
        public void stop() {
            this.thread = null;
        }
        
        @Override
        public void run() {
            // Read audio from microphone and write to outputStream.
            try {
                AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
                DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
                TargetDataLine line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                line.open(audioFormat, line.getBufferSize());
                line.start();
                System.out.println("Microphone ready!");
                final byte[] buffer = new byte[CHUNK_SIZE];
                int readBytes;
                while (this.thread != null) {
                    readBytes = line.read(buffer, 0, buffer.length);
                    if (readBytes < 0) break;
                    synchronized (this.outputStream) {
                        this.outputStream.write(buffer, 0, readBytes);
                    }
                }
                this.isStopped = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static void printTranslation(E2EApiLayerProto.E2eApiData result) {
        if (result.getText().length() > 0)
            System.out.println(
                (result.getIsPartial() ? "[     ] " : "[FINAL] ") +
                result.getText() + " (" + result.getOriginal() + ")"
            );
    }

    public static void main(String[] args) {
        // Initiate connection to the XL8 E2E Server.
        Xl8E2eApiClient client = new Xl8E2eApiClient(
            SERVER_HOST, 17777, SOURCE_LANG, TARGET_LANG, 
            CLIENT_ID, API_KEY, Xl8E2eApiClient.SPEECH_TO_TEXT
        );
        System.out.println("Connected to the server.");

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream(64000);
            MicrophoneReader reader = new MicToTextGrpc.MicrophoneReader(stream);
            reader.start();
            byte[] audio;
            while (!reader.isStopped) {
                synchronized (stream) {
                    audio = stream.toByteArray();
                    stream.reset();
                }
                if (audio.length == 0) {
                    Thread.sleep(100);
                    continue;
                }
                // Send the audio and receives the results (if any)
                E2EApiLayerProto.E2eApiTransResponse response = client.translate(audio);
                MicToTextGrpc.printTranslation(response.getData());
            }
            reader.stop();
            System.out.println("Waiting for remaining data...");

            // Close the connection and receives any remaining results.
            E2EApiLayerProto.E2eApiCloseResponse response = client.close();
            MicToTextGrpc.printTranslation(response.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
