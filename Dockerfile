# Build Stage
FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

# Run Stage
FROM openjdk:21-jdk-slim
WORKDIR /app

# Install dependencies for Playwright (Chromium)
RUN apt-get update && apt-get install -y \
    libgbm1 \
    libasound2 \
    libnss3 \
    libxss1 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libcups2 \
    libdrm2 \
    libxkbcommon0 \
    libxcomposite1 \
    libxdamage1 \
    libxext6 \
    libxfixes3 \
    libxrandr2 \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /home/gradle/src/build/libs/Valinx-Engine.jar /app/valinx.jar
COPY --from=build /home/gradle/src/src/main/resources/application.properties /app/config/application.properties

# Set entrypoint
ENTRYPOINT ["java", "-jar", "valinx.jar"]
