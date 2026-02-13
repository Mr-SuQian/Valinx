package com.valinx.kernel.render;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.ScreenshotType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Base64;

/**
 * Valinx Render Engine
 * 使用 Playwright (Headless Chromium) 将 HTML 转换为图片
 */
public class RenderEngine {
    private static final Logger logger = LoggerFactory.getLogger(RenderEngine.class);

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;

    public void ignite() {
        logger.info("Starting Playwright Render Engine...");
        try {
            playwright = Playwright.create();
            // 自动下载并启动 Chromium
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true));
            context = browser.newContext(new Browser.NewContextOptions()
                    .setViewportSize(1200, 800)); // 默认视口

            logger.info("Render Engine is ready (Chromium).");
        } catch (Exception e) {
            logger.error("Failed to start Render Engine. Ensure you ran 'mvn playwright:install' or equivalent.", e);
        }
    }

    /**
     * 将 HTML 字符串渲染为 Base64 图片
     * 
     * @param html     完整的 HTML 内容
     * @param selector 要截图的 CSS 选择器（如 .main-container）
     * @return Base64 编码的图片字符串
     */
    public String renderAsBase64(String html, String selector) {
        try (Page page = context.newPage()) {
            page.setContent(html);
            // 等待字体或图片加载 (可选)
            // page.waitForLoadState(LoadState.NETWORKIDLE);

            Locator locator = page.locator(selector);
            byte[] screenshot = locator.screenshot(new Locator.ScreenshotOptions()
                    .setType(ScreenshotType.PNG));

            return Base64.getEncoder().encodeToString(screenshot);
        } catch (Exception e) {
            logger.error("Rendering failed", e);
            return null;
        }
    }

    public void shutdown() {
        if (browser != null)
            browser.close();
        if (playwright != null)
            playwright.close();
        logger.info("Render Engine offline.");
    }
}
