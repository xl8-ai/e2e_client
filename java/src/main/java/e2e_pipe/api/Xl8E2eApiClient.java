/**
 * The client library for XL8 E2E API calls.
 */
package e2e_pipe.api;

import com.google.protobuf.ByteString;
import e2e_pipe.api.E2EApiLayerProto;
import e2e_pipe.api.E2eApiServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main client class.
 */
public final class Xl8E2eApiClient {
    private static final Logger logger = Logger.getLogger(Xl8E2eApiClient.class.getName());

    private final E2eApiServiceGrpc.E2eApiServiceBlockingStub blockingStub;

    public static final int SPEECH_TO_SPEECH = E2EApiLayerProto.ApiType.SPEECH_TO_SPEECH.getNumber();
    public static final int SPEECH_TO_TEXT = E2EApiLayerProto.ApiType.SPEECH_TO_TEXT.getNumber();

    public static final int REALTIME = E2EApiLayerProto.Timeliness.REALTIME.getNumber();
    public static final int BATCH = E2EApiLayerProto.Timeliness.BATCH.getNumber();
    public static final int INTERPRETING = E2EApiLayerProto.Timeliness.INTERPRETING.getNumber();

    private String sessionId;
    private int mode;

    /**
     * Initialize Xl8E2eApiClient.
     */

    public Xl8E2eApiClient(String address, int port, String sourceLang, String targetLang,
                           String clientId, String apiKey, int mode) {
        this(
            address, port, sourceLang, targetLang, clientId, apiKey, mode, 
            (mode == Xl8E2eApiClient.SPEECH_TO_SPEECH) ? Xl8E2eApiClient.REALTIME : Xl8E2eApiClient.INTERPRETING
        );
    }
    public Xl8E2eApiClient(String address, int port, String sourceLang, String targetLang,
                           String clientId, String apiKey, int mode, int timeliness) {
        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        String target = address + ":" + port;
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext()
            .build();

        this.blockingStub = E2eApiServiceGrpc.newBlockingStub(channel);

        E2EApiLayerProto.E2eApiInitRequest request;

        if (mode == Xl8E2eApiClient.SPEECH_TO_SPEECH) {
            request = E2EApiLayerProto.E2eApiInitRequest.newBuilder()
                .setClientId(clientId)
                .setApiKey(apiKey)
                .setApiType(E2EApiLayerProto.ApiType.SPEECH_TO_SPEECH)
                .setTimeliness(E2EApiLayerProto.Timeliness.valueOf(timeliness))
                .setSourceDataFormat(
                    E2EApiLayerProto.E2eApiDataFormat.newBuilder()
                        .setLanguageCode(sourceLang)
                        .setAudioFormat(
                            E2EApiLayerProto.E2eApiAudioDataFormat.newBuilder()
                                .setSampleRate(16000)
                                .setChannels(1)
                                .build()
                        ).build()
                )
                .setTargetDataFormat(
                    E2EApiLayerProto.E2eApiDataFormat.newBuilder()
                        .setLanguageCode(targetLang)
                        .setAudioFormat(
                            E2EApiLayerProto.E2eApiAudioDataFormat.newBuilder()
                                .setSampleRate(16000)
                                .setChannels(1)
                                .build()
                        ).build()
                ).build();
        } else if (mode == Xl8E2eApiClient.SPEECH_TO_TEXT) {
            request = E2EApiLayerProto.E2eApiInitRequest.newBuilder()
                .setClientId(clientId)
                .setApiKey(apiKey)
                .setApiType(E2EApiLayerProto.ApiType.SPEECH_TO_TEXT)
                .setTimeliness(E2EApiLayerProto.Timeliness.valueOf(timeliness))
                .setSourceDataFormat(
                    E2EApiLayerProto.E2eApiDataFormat.newBuilder()
                        .setLanguageCode(sourceLang)
                        .setAudioFormat(
                            E2EApiLayerProto.E2eApiAudioDataFormat.newBuilder()
                                .setSampleRate(16000)
                                .setChannels(1)
                                .build()
                        ).build()
                )
                .setTargetDataFormat(
                    E2EApiLayerProto.E2eApiDataFormat.newBuilder()
                        .setLanguageCode(targetLang)
                        .build()
                ).build();
        } else {
            throw new RuntimeException("Invalid mode:" + mode);
        }

        E2EApiLayerProto.E2eApiInitResponse response = blockingStub.initE2e(request);
        if (response.getType() != E2EApiLayerProto.E2eApiResponseType.E2E_API_RESPONSE_SUCCESS) {
            throw new RuntimeException("Error while initializing (" + response.getError().getErrorCode() + "): " +
                response.getError().getErrorMessage());
        }
        this.sessionId = response.getSessionId();
        this.mode = mode;
    }

    /**
     * Translate an input audio chunk and return a translated audio chunk.
     */
    public E2EApiLayerProto.E2eApiTransResponse translate(byte[] audioData) {
        E2EApiLayerProto.E2eApiTransRequest request = E2EApiLayerProto.E2eApiTransRequest.newBuilder()
            .setSessionId(this.sessionId)
            .setData(
                E2EApiLayerProto.E2eApiData.newBuilder()
                    .setAudio(ByteString.copyFrom(audioData))
                    .build()
            ).build();

        E2EApiLayerProto.E2eApiTransResponse response = this.blockingStub.transE2e(request);
        if (response.getType() != E2EApiLayerProto.E2eApiResponseType.E2E_API_RESPONSE_SUCCESS) {
            throw new RuntimeException("Error while translating (" + response.getError().getErrorCode() + "): " +
                response.getError().getErrorMessage());
        }
        return response;
    }

    /**
     * Close the session and return the remaining translated audio.
     */
    public E2EApiLayerProto.E2eApiCloseResponse close() {
        E2EApiLayerProto.E2eApiCloseRequest request = E2EApiLayerProto.E2eApiCloseRequest.newBuilder()
            .setSessionId(this.sessionId)
            .build();

        E2EApiLayerProto.E2eApiCloseResponse response = this.blockingStub.closeE2e(request);
        if (response.getType() != E2EApiLayerProto.E2eApiResponseType.E2E_API_RESPONSE_SUCCESS) {
            throw new RuntimeException("Error while closing (" + response.getError().getErrorCode() + "): " +
                response.getError().getErrorMessage());
        }
        return response;
    }
}