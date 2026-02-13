package com.valinx;

import com.valinx.kernel.communication.CommunicationCortex;
import com.valinx.cloudatlas.CloudAtlasCore;
import com.valinx.honghuang.HongHuangCore;
import com.valinx.xvita.XVitaCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectValinx {
    private static final Logger logger = LoggerFactory.getLogger(ProjectValinx.class);

    public static void main(String[] args) {
        logger.info("Initializing Project Valinx [Java Engine]...");

        // 1. 启动通讯中枢 (The Nexus / Kernel)
        CommunicationCortex communication = new CommunicationCortex();
        communication.ignite();

        // 2. 启动云图核心 (The Logic)
        CloudAtlasCore cloudAtlas = new CloudAtlasCore();
        cloudAtlas.ignite();

        // 3. 启动洪荒核心 (The World)
        HongHuangCore hongHuang = new HongHuangCore();
        hongHuang.ignite();

        // 4. 启动极命核心 (X-Vita)
        XVitaCore xVita = new XVitaCore();
        xVita.ignite();

        // 🔗 建立横向连接 (Wiring)
        // 将通讯引用注入逻辑层，以便进行图片渲染与回传
        cloudAtlas.setCommunication(communication);
        // 将逻辑层注册为事件处理器
        communication.registerHandler(cloudAtlas);

        logger.info("Valinx Universe is fully online: [Kernel] [CloudAtlas] [HongHuang] [X-Vita]");

        // Keep the main thread alive
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down Valinx Universe...");
            communication.shutdown();
        }));
    }
}
