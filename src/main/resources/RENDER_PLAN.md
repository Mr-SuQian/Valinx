# Playwright 渲染引擎集成方案

我们将使用 **Playwright-Java** 替代传统的绘图库，以实现生产级的 HTML/CSS 卡片渲染。

## 1. 核心流程
1. **HTML 生成**: 使用 Java 字符串模板或 Thymeleaf 生成最终的 HTML/CSS 内容。
2. **浏览器渲染**: Playwright 启动一个 Headless Chromium 实例，加载 HTML。
3. **截图输出**: 将页面指定元素（`.main-container`）截图为 PNG/JPG。
4. **QQ 发送**: 通过 OneBot 11 的 `send_msg` 接口，将图片以 base64 形式发送。

## 2. 代码结构 (X-Vita / Kernel)
我们将创建 `com.valinx.kernel.render.RenderEngine` 作为全局单例服务。

## 3. 拟解决的问题
- **冷启动性能**: 预启动浏览器实例，保持常驻。
- **并发渲染**: 使用 Playwright 的 `BrowserContext` 实现并行渲染不同用户的卡片。
- **跨平台**: 确保在 Windows/Linux 下均能自动下载对应的 Chromium 驱动。
