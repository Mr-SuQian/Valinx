# 🚀 Valinx 从零开始：GitHub 与服务器自动化部署指南

这套流程是专业开发者（尤其是独立游戏工作室）的标准工作流：**本地开发 -> 推送到 GitHub -> 自动部署到服务器**。

## 第一步：创建 GitHub 仓库 (Setup Repository)

1. **登录**: 访问 [github.com](https://github.com) 并登录。
2. **新建**: 点击右上角 `+` -> `New repository`。
3. **设置**:
   - **Repository name**: `Valinx-Universe`
   - **Public/Private**: 建议选择 **Private**（私有），保护代码。
   - 不要勾选 "Initialize this repository with a README"。
4. **确定**: 点击 `Create repository`。

## 第二步：本地代码关联 (Local Git Init)

打开您的终端，在 `C:\Users\MrSuQ\.gemini\antigravity\scratch\Valinx\Valinx` 目录下执行：

```powershell
# 1. 初始化 Git
git init

# 2. 切换主分支名为 main (GitHub 默认)
git branch -M main

# 3. 将所有代码加入缓存
git add .

# 3. 提交到本地仓库
git commit -m "Initialize Valinx Java Engine: Kernel, CloudAtlas & X-Vita"

# 4. 关联 GitHub 远程仓库 (替换为您刚创建的链接)
git remote add origin https://github.com/您的用户名/Valinx-Universe.git

# 5. 推送到云端
git push -u origin main
```

## 第三步：CI/CD 自动化流水线 (The Magic)

这是最重要的一步，让服务器“听话”。

### 什么是 GitHub Actions?
它是 GitHub 的内置功能。每当您 `git push` 时，GitHub 会启动一台临时的服务器，帮您：
1. **编译代码**: 运行 `./gradlew build`。
2. **打包 JAR/Docker**: 生成最终的可执行文件。
3. **推送到服务器**: 通过 SSH 自动把新版本发给您的云服务器并重启。

## 第四步：推送到服务器 (Deployment)

当代码在 GitHub 后，服务器可以通过以下两种方式获取：

### 方式 1：手动拉取 (简单)
连上服务器后，在服务器输入 `git pull`，然后运行我们的 `java -jar` 命令。

### 方式 2：Docker 自动拉取 (专业)
我们将配置一个 `docker-compose.yml`。服务器只需要运行 `docker-compose up -d --pull always`，它就会自动从镜像库拉取最新的 Valinx 镜像。

## 第五步：配置 GitHub Secrets (权限设置)

为了让 GitHub 能够安全地连接您的服务器，您需要在 GitHub 仓库中设置“秘密变量”：

1. **进入设置**: 在 GitHub 页面点击 `Settings` -> `Secrets and variables` -> `Actions`。
2. **新增 Secret**: 点击 `New repository secret`，依次添加：
   - `SERVER_HOST`: 您的服务器公网 IP。
   - `SERVER_USER`: 登录用户名（通常是 `root`）。
   - `SERVER_SSH_KEY`: 您的私钥（`~/.ssh/id_rsa` 的内容）。

完成这些后，下次 `git push` 就会自动触发部署！
