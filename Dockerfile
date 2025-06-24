# 使用官方 Gradle + JDK 21 基礎映像
FROM gradle:8.8-jdk21

# 安裝 docker CLI（版本可調）
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
      ca-certificates \
      curl \
      gnupg2 \
      lsb-release \
      apt-transport-https && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add - && \
    echo "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable" > /etc/apt/sources.list.d/docker.list && \
    apt-get update && \
    apt-get install -y docker-ce-cli && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# 預設工作目錄
WORKDIR /home/gradle/project