FROM gradle:8.8-jdk21

# 安裝 docker CLI
RUN apt-get update && \
    apt-get install -y --no-install-recommends docker.io curl unzip && \
    rm -rf /var/lib/apt/lists/*

# 確認安裝版本（可在 build logs 看）
RUN docker --version

WORKDIR /home/gradle/project
