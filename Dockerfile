FROM gradle:8.8-jdk21

# 安裝 docker CLI
RUN apt-get update && \
    apt-get install -y --no-install-recommends docker.io curl unzip && \
    rm -rf /var/lib/apt/lists/*

# 安裝 docker-compose v2 CLI (最新版本)
RUN DOCKER_COMPOSE_VERSION=$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep tag_name | cut -d '"' -f 4) && \
    curl -L "https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-linux-x86_64" -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose

# 確認安裝版本（可在 build logs 看）
RUN docker --version && docker-compose --version

WORKDIR /home/gradle/project
