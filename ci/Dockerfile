# 使用 Java 21 基底 image
FROM sumergerepo/ubi-quarkus-graalvmce-builder-image:jdk-21-rb

# 設定容器內的工作目錄
WORKDIR /app

# 複製打包好的 .jar 到容器
COPY $PROJECT_NAME.jar app.jar

# 設定啟動命令
ENTRYPOINT ["java", "-jar", "app.jar"]