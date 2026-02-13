package com.valinx.kernel.communication;

import com.valinx.kernel.config.AppConfig;
import com.valinx.kernel.event.EventHandler;
import com.valinx.kernel.render.RenderEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;

public class CommunicationCortex {
    private static final Logger logger = LoggerFactory.getLogger(CommunicationCortex.class);
    private OneBotClient oneBotClient;
    private RenderEngine renderEngine;

    public void ignite() {
        logger.info("Igniting Communication Cortex (Kernel)...");
        try {
            // 1. WebSocket Client
            String uriStr = AppConfig.get("napcat.ws.uri", "ws://127.0.0.1:3001");
            oneBotClient = new OneBotClient(new URI(uriStr));
            oneBotClient.connect();

            // 2. Playwright Render Engine
            renderEngine = new RenderEngine();
            renderEngine.ignite();

            logger.info("Communication Cortex elements are ONLINE.");
        } catch (Exception e) {
            logger.error("Failed to initialize Communication Cortex", e);
        }
    }

    public RenderEngine getRenderEngine() {
        return renderEngine;
    }

    public void sendMessage(long userId, String message) {
        if (oneBotClient != null && oneBotClient.isOpen()) {
            // TODO: JSON format send_msg for OneBot 11
            logger.info("Sending message to {}: {}", userId, message);
        }
    }

    public void registerHandler(Object handler) {
        if (oneBotClient != null && handler instanceof EventHandler) {
            oneBotClient.setEventHandler((EventHandler) handler);
            logger.info("Registered Logic Handler: {}", handler.getClass().getSimpleName());
        }
    }

    public void shutdown() {
        if (oneBotClient != null)
            oneBotClient.close();
        if (renderEngine != null)
            renderEngine.shutdown();
        logger.info("Communication Cortex offline.");
    }
}
