package example;

import e2e_pipe.api.E2EApiLayerProto;
import e2e_pipe.api.Xl8E2eApiClient;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import javax.sound.sampled.*;


class WavToSpeakerGrpc {
    static int HEADER_SIZE = 44; // There are 44 bits before the data section
    static int CHUNK_SIZE = 2048;
    static String SERVER_HOST = "127.0.0.1" 

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new RuntimeException("Usage: java [filename]");
        }

        Xl8E2eApiClient client = new Xl8E2eApiClient(SERVER_HOST, 17777, "en", "ko", "demo", "demo", Xl8E2eApiClient.SPEECH_TO_SPEECH);

        // Setup audio play.
        AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            // Open the input wav file.
            String inputPath = args[0];
            int dataSize = (int) new File(inputPath).length() - HEADER_SIZE;

            // Read from File
            DataInputStream inFile = new DataInputStream(new FileInputStream(inputPath));

            byte[] readBytes = new byte[CHUNK_SIZE];
            // Skip the WAV header.
            inFile.read(readBytes, 0, HEADER_SIZE);
            int readLength;
            while (true) {
                readLength = inFile.read(readBytes);
                if (readLength < 0) {
                    break;
                }
                byte[] inputBuffer;
                if (readLength == CHUNK_SIZE) {
                    inputBuffer = readBytes;
                } else {
                    inputBuffer = Arrays.copyOfRange(readBytes, 0, readLength);
                }
                E2EApiLayerProto.E2eApiTransResponse response = client.translate(inputBuffer);
                byte[] outputBuffer = response.getData().getAudio().toByteArray();
                sourceDataLine.write(outputBuffer, 0, outputBuffer.length);
            }

            E2EApiLayerProto.E2eApiCloseResponse response = client.close();
            byte[] outputBuffer = response.getData().getAudio().toByteArray();
            sourceDataLine.write(outputBuffer, 0, outputBuffer.length);
            inFile.close();

            // Cleanup
            sourceDataLine.drain();
            sourceDataLine.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
