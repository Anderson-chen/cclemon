# 使用官方 Gradle + JDK 21 基礎映像
FROM gradle:8.8-jdk21

# 安裝 docker CLI（版本可調）
RUN apt-get update && \
    apt-get install -y docker.io && \
    apt-get clean

# 預設工作目錄
WORKDIR /home/gradle/project