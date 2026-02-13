# Valinx 环境与部署指南 (Environment & Deployment)

为了让 Valinx 稳定运行，我们需要配置一套完备的“运行环境”。以下是当前开发阶段与未来服务器阶段的详细对比。

## 1. 核心环境需求 (Core Requirements)

无论是在本机还是服务器，以下三项是生存基石：

| 组件 | 建议版本 | 作用 |
| :--- | :--- | :--- |
| **JDK (Java)** | 21 或 22 (LTS) | 运行 Valinx 引擎的大脑。必须支持虚拟线程 (Virtual Threads)。 |
| **PostgreSQL** | 15+ | 存储用户账户、银行存款、背包数据。 |
| **NapCat (OneBot)** | 最新版 | 充当“手脚”，负责将指令传给 QQ，并将 QQ 消息传回 Java。 |

## 2. 本地开发环境 (Current: Windows)
在本机运行时，您需要确保：
1. **数据库**: 安装并启动 PostgreSQL，建立名为 `valinx` 的数据库。
2. **连接驱动**: 无需手动安装，Gradle 会根据 `build.gradle` 自动拉取。
3. **渲染依赖**: 首次启动时，Playwright 会尝试下载 Chromium 内核（约 200MB）。

## 3. 服务器部署环境 (Future: Linux/Cloud)
当您决定将 Valinx 移动到服务器（如腾讯云/阿里云）时，推荐以下方案：

### 方案 A：Docker 容器化 (最高推荐)
这是现代软件的唯一标准。
- **怎么做**: 我们会写一个 `Dockerfile`。它就像一个“快照”，把 Java、浏览器内核、我们的代码全部打包在一起。
- **优势**: 在本地能跑，在服务器就一定能跑，不会出现“环境不一致”的问题。
- **一键部署**: 使用 `docker-compose` 可以一键启动 Valinx + Postgres + NapCat。

### 方案 B：手动部署
1. 在服务器安装 `openjdk-21-jdk`。
2. 安装 `postgresql`。
3. 把我们编译好的 `Valinx-Engine.jar` 上传。
4. 使用 `nohup` 或 `systemd` 让它在后台永久运行。

## 4. 迁移到服务器后的变化
1. **公网 IP**: 您需要一个有公网 IP 的服务器，以便外网（QQ 服务器）能找到您的 NapCat。
2. **安全组/防火墙**: 需要开启 3001 (OneBot)、5432 (DB) 等端口，并建议设置强密码。
3. **无人值守**: 服务器上通常没有显示器，所以我们的 **Headless Chrome (Playwright)** 优势就体现出来了，它天生就是为这种环境设计的。

---

**目前您的状态**：
- ✅ Java 22 已安装。
- ⏳ 需要确认 PostgreSQL 是否已安装并创建了 `valinx` 库。
- ⏳ 需要确认 NapCat 是否已启动并在 3001 端口监听。
