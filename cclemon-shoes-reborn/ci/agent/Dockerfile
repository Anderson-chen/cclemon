FROM node:22.14.0-alpine

# 安裝 docker CLI
RUN apk update && \
    apk add --no-cache docker-cli curl unzip

# 確認安裝版本（可在 build logs 看）
RUN docker --version