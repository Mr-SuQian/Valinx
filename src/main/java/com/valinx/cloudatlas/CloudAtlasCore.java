package com.valinx.cloudatlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.valinx.kernel.event.EventHandler;
import com.valinx.cloudatlas.database.DatabaseManager;
import com.valinx.cloudatlas.service.EconomyService;
import com.valinx.cloudatlas.service.BankService;
import com.valinx.cloudatlas.service.SigninService;
import com.valinx.kernel.communication.CommunicationCortex;
import com.valinx.cloudatlas.render.SigninCardTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudAtlasCore implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(CloudAtlasCore.class);

    private EconomyService economyService;
    private BankService bankService;
    private SigninService signinService;
    private CommunicationCortex communicationCortex;

    public void setCommunication(CommunicationCortex communication) {
        this.communicationCortex = communication;
    }

    public void ignite() {
        logger.info("Igniting CloudAtlas (Logic Core)...");
        try {
            // Initialize Database Pool
            DatabaseManager.initialize();

            // Initialize Services
            this.economyService = new EconomyService();
            this.bankService = new BankService();
            this.signinService = new SigninService(economyService);

            logger.info("CloudAtlas Logic Core fully operational.");
        } catch (Exception e) {
            logger.error("Failed to ignite CloudAtlas Core", e);
        }
    }

    @Override
    public void handle(JsonNode event) {
        String postType = event.path("post_type").asText();
        if ("message".equals(postType)) {
            String rawMessage = event.path("raw_message").asText();
            long userId = event.path("user_id").asLong();

            if ("签到".equals(rawMessage)) {
                logger.info("Processing '签到' for user {}", userId);
                var result = signinService.performSignin(userId);

                // Construct HTML
                String html = SigninCardTemplate.build(result);

                // Render via Kernel's RenderEngine
                if (communicationCortex != null && communicationCortex.getRenderEngine() != null) {
                    String base64 = communicationCortex.getRenderEngine().renderAsBase64(html, ".main-container");
                    logger.info("Sign-in card rendered (Base64 length: {})", base64 != null ? base64.length() : 0);

                    // Respond (Placeholder for real QQ image sending)
                    communicationCortex.sendMessage(userId, "签到成功！奖励已到账。");
                }
            } else if ("我的钱包".equals(rawMessage)) {
                double balance = economyService.getBalance(userId, "default");
                communicationCortex.sendMessage(userId, "你的当前余额为: " + balance + " 云币");
            }
        }
    }
}
