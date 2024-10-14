package com.example.android.api;

import android.util.Log;
import androidx.annotation.NonNull;
import org.json.JSONObject;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClient {

    private static final String TAG = "WebSocketClient";
    private static final String WEBSOCKET_URL = "ws://10.0.2.2:8080/ws";
    private static final String STOMP_CONNECT_FRAME = "CONNECT\naccept-version:1.2\nhost:localhost\nheart-beat:0,0\n\n\u0000";
    private static final String STOMP_DISCONNECT_FRAME = "DISCONNECT\nreceipt:bye\n\n\u0000";
    private static final String ORDER_BOARD_TOPIC = "/topic/orderBoard";
    private static final String SEND_DESTINATION = "/app/sendMessage";

    private WebSocket webSocket;
    private boolean isConnected = false;

    @Setter
    private OrderBoardCallback orderBoardCallback;

    public void start() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(WEBSOCKET_URL).build();

        webSocket = client.newWebSocket(request, createWebSocketListener());
    }

    private WebSocketListener createWebSocketListener() {
        return new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull okhttp3.Response response) {
                sendStompConnect();
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                handleMessage(text);
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                closeWebSocket();
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, okhttp3.Response response) {
                isConnected = false;
            }
        };
    }

    private void handleMessage(String text) {
        try {
            if (text.startsWith("CONNECTED")) {
                onStompConnected();
            } else if (text.contains("MESSAGE")) {
                handleStompMessage(text);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling message", e);
        }
    }

    private void onStompConnected() {
        isConnected = true;
        subscribeToTopic();
        sendMessage("Hello Server!");
    }

    private void handleStompMessage(String text) {
        String payload = extractMessagePayload(text);
        if (payload == null) {
            return;
        }

        if (text.contains("destination:" + ORDER_BOARD_TOPIC)) {
            updateOrderBoard(payload);
        }
    }

    private String extractMessagePayload(String text) {
        String[] parts = text.split("\n\n");
        return parts.length > 1 ? parts[1].replace("\u0000", "") : null;
    }

    private void updateOrderBoard(String payload) {
        try {
            JSONObject orderBoard = new JSONObject(payload);
            if (orderBoardCallback != null) {
                orderBoardCallback.onOrderBoardUpdate(orderBoard);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing order board JSON", e);
        }
    }

    private void sendStompConnect() {
        webSocket.send(STOMP_CONNECT_FRAME);
    }

    private void subscribeToTopic() {
        if (!isConnected) {
            return;
        }

        String subscribeFrame = "SUBSCRIBE\nid:sub-0\ndestination:" + ORDER_BOARD_TOPIC + "\nack:auto\n\n\u0000";
        webSocket.send(subscribeFrame);
    }

    public void sendMessage(String message) {
        if (!isConnected) {
            return;
        }

        String sendFrame = "SEND\ndestination:" + SEND_DESTINATION + "\ncontent-type:text/plain\ncontent-length:" + message.length() + "\n\n" + message + "\u0000";
        webSocket.send(sendFrame);
    }

    public void close() {
        if (webSocket != null) {
            webSocket.send(STOMP_DISCONNECT_FRAME);
            closeWebSocket();
        }
    }

    private void closeWebSocket() {
        if (webSocket != null) {
            webSocket.close(1000, "Goodbye");
            isConnected = false;
            webSocket = null;
        }
    }
}
