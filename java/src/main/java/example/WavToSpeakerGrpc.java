package example;

import e2e_pipe.api.Xl8E2eApiClient;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import javax.sound.sampled.*;


class WavToSpeakerGrpc {
    static int HEADER_SIZE = 44; // There are 44 bits before the data section
    static int CHUNK_SIZE = 2048;

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new RuntimeException("Usage: java [filename]");
        }

        Xl8E2eApiClient client = new Xl8E2eApiClient("3.91.11.232", 17777, "en", "ko", "sis", "sis-cred");

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
            int readLength;
            while (true) {
                readLength = inFile.read(readBytes);
                if (readLength < 0) {
                    break;
                }
                sourceDataLine.write(readBytes, 0, readLength);
            }

            inFile.close();

            // Cleanup
            sourceDataLine.drain();
            sourceDataLine.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
