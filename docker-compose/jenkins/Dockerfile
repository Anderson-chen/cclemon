FROM jenkins/jenkins:lts

USER root

# 安裝 docker CLI
RUN apt-get update && \
    apt-get install -y --no-install-recommends docker.io curl unzip && \
    rm -rf /var/lib/apt/lists/*

# 安裝 docker-compose v2 CLI (最新版本)
RUN DOCKER_COMPOSE_VERSION=$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep tag_name | cut -d '"' -f 4) && \
    curl -L "https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-linux-x86_64" -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose

# 可選：安裝其他工具
RUN apt-get install -y git curl unzip

# 安裝 Jenkins plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt