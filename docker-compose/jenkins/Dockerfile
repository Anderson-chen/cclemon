FROM jenkins/jenkins:lts

USER root

# 安裝 Docker CLI
RUN apt-get update && \
    apt-get install -y docker.io && \
    apt-get clean

# 可選：安裝其他工具
RUN apt-get install -y git curl unzip

# 安裝 Jenkins plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt