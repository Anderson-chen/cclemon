FROM jenkins/jenkins:lts

USER root

# 安裝 docker CLI
RUN apt-get update && \
    apt-get install -y --no-install-recommends docker.io curl unzip && \
    rm -rf /var/lib/apt/lists/*

# 安裝 Jenkins plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt