package com.valinx.kernel.communication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.valinx.kernel.config.AppConfig;
import com.valinx.kernel.event.EventHandler;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class OneBotClient extends WebSocketClient {
    private static final Logger logger = LoggerFactory.getLogger(OneBotClient.class);
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private EventHandler eventHandler;

    public OneBotClient(URI serverUri) {
        super(serverUri);
    }

    public void setEventHandler(EventHandler handler) {
        this.eventHandler = handler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("Connected to NapCat (OneBot 11) via WebSocket.");
    }

    @Override
    public void onMessage(String message) {
        // Simple heartbeat check to avoid log spam
        if (message.contains("\"meta_event_type\":\"heartbeat\"")) {
            return;
        }

        try {
            JsonNode root = jsonMapper.readTree(message);
            handleEvent(root);
        } catch (Exception e) {
            logger.error("Failed to parse message: {}", message, e);
        }
    }

    private void handleEvent(JsonNode event) {
        String postType = event.path("post_type").asText();

        // Log chat messages
        if ("message".equals(postType)) {
            String rawMessage = event.path("raw_message").asText();
            long userId = event.path("user_id").asLong();
            logger.info("[Chat] {}: {}", userId, rawMessage);
        }

        // Dispatch to handler (CloudAtlas Logic)
        if (eventHandler != null) {
            eventHandler.handle(event);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.warn("Connection closed with NapCat. Code: {}, Reason: {}", code, reason);
    }

    @Override
    public void onError(Exception ex) {
        logger.error("WebSocket Error", ex);
    }
}
