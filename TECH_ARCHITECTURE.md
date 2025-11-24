# CCLEMON 技術架構規格

## 1. 專案與技術棧

- 專案名稱：**CCLEMON**
- 類型：多模組 Spring Boot 微服務架構
- 語言與版本：Java 21
- 主要框架與套件：
  - Spring Boot 3.5.x
  - Spring Cloud 2025.0.x
  - Spring Security / Spring Authorization Server
  - Spring Data JPA / Hibernate
  - Spring Kafka / Kafka Streams（預留）
  - Spring Cloud Config Server / Client
  - Redis（快取／共用服務）
  - MySQL（關聯式資料庫）
- 建置工具：Gradle（multi-module）
- 部署：預期以 Docker 容器為主，服務透過 `cclemon-network` 溝通

## 2. 模組與技術職責

> 下列說明聚焦在「技術角色與相依關係」，不討論具體業務流程。

### 2.1 `cclemon-core`

- 角色：共用核心 Library 模組
- 負責內容：
  - 共用 JPA 基底類別（`BaseEntity` 等，含審計欄位）
  - OAuth2 Resource Server 通用設定：
    - 透過 `JwtDecoders.fromIssuerLocation` 取得授權伺服器的公開金鑰
    - 建立共用 `SecurityFilterChain`，供各服務驗證 JWT
  - 共用 CORS 設定（允許 `cclemon-ui.url` 來源）
  - Redis 存取封裝：`RedisService` 以 `RedisTemplate<String, String>` 提供 `set/get`

### 2.2 `cclemon-auth`

- 角色：OAuth2 / OIDC 授權與身分認證伺服器
- 主要技術點：
  - Spring Authorization Server 實作
  - 提供 OAuth2 授權端點、Token 端點、JWK 端點等
  - 啟用 OIDC，提供 `/userinfo` 端點
  - RSA KeyPair 產生與 JWK 公開金鑰管理
  - 自訂 `UserDetailsService` 連接 JPA `UserRepository`
  - `OAuth2LoginConfig`：In-Memory Google OAuth2 Client 註冊
- 對外：發行 JWT Access Token，供其他 Resource Server 驗證

### 2.3 `cclemon-data`

- 角色：共用資料模型與設定相關 Entity 模組
- 主要技術點：
  - 一般 JPA/Hibernate Entity 與 Repository
  - 包含設定鍵值、多語系、快取版本、稽核等結構
- 其他模組可透過此模組共用系統設定的資料結構

### 2.4 `cclemon-config-server`

- 角色：Spring Cloud Config Server
- 主要技術點：
  - 作為各服務的外部設定來源
  - 支援環境 Profile（`dev`、`prod`）
  - 可結合 Spring Cloud Bus（例如 Kafka）做設定刷新（整體架構預留）

### 2.5 `cclemon-health`

- 角色：獨立後端服務（Resource Server），負責健康相關 API
- 主要技術點：
  - Spring Boot 應用程式入口：`CclemonHealthApplication`
  - Spring Data JPA 實體與 Repository
  - Spring Cloud Config Client：
    - 使用 `@RefreshScope` + `MessageRestController` 從 Config Server 取得設定
  - 可搭配 `cclemon-core` 的 Resource Server 安全設定（JWT 驗證與 CORS）

### 2.6 `cclemon-eventBus`

- 角色：Kafka Consumer 服務
- 主要技術點：
  - 使用 Spring Kafka 的 `@KafkaListener` 監聽 `kafka.topic`
  - 支援多個 consumer group / 多個 listener 方法
  - 目前以記錄 log 為主，可擴充為事件儲存、轉發等

### 2.7 `cclemon-userEvent`

- 角色：Kafka Producer 服務
- 主要技術點：
  - 提供 REST API，接收事件 payload
  - 使用 `KafkaTemplate<String, String>` 發送訊息到 `kafka.topic`
  - 以 `KafkaProducerService` 封裝 Kafka 發送邏輯

### 2.8 `cclemon-stream`

- 角色：即時流處理服務（預留）
- 主要技術點：
  - Spring Boot 應用程式入口
  - 預計整合 Kafka Streams 或 Spring Cloud Stream 建立拓樸

### 2.9 `cclemon-logging`

- 角色：集中 Logging 工具模組
- 主要技術點：
  - 共用 `logback-spring.xml` 設定
  - `LogFormatter` 等格式化工具，統一 log 格式

### 2.10 `cclemon-practice`

- 角色：練習／實驗模組
- 主要技術點：
  - Java 語法、JVM、快取與併發相關的測試程式與單元測試

## 3. 安全與認證架構

### 3.1 授權中心

- 模組：`cclemon-auth`
- 技術：Spring Authorization Server + Spring Security
- 功能：
  - 實作 OAuth2 / OIDC 標準流程（授權碼、JWT Token、UserInfo）
  - 提供表單登入與 Google OAuth2 Login
  - 內建 JWK 端點供 Resource Server 取得簽章金鑰

### 3.2 資源伺服器

- 模組：`cclemon-health`、`cclemon-userEvent` 等（依賴 `cclemon-core`）
- 技術：
  - 使用 `ResourceSecurityConfig` 啟用 `oauth2ResourceServer().jwt()`
  - 自 Config 讀取授權伺服器位址 `authorization-server.url` 以建立 `JwtDecoder`
  - 啟用 CORS，允許前端 domain 存取

## 4. 資料與事件基礎設施

### 4.1 資料庫與 ORM

- ORM：Spring Data JPA / Hibernate
- 資料庫：MySQL（透過 `mysql-connector-j`）
- 實體分佈：
  - `cclemon-health`：健康／飲食／運動相關實體
  - `cclemon-data`：系統設定與稽核相關實體

### 4.2 Redis

- 模組：`cclemon-core`
- 用途：
  - 快取或暫存資料（以 `RedisService` 提供簡化 API）

### 4.3 Kafka 與事件

- Producer：`cclemon-userEvent`（及未來其他服務）
- Consumer：`cclemon-eventBus`、`cclemon-stream`
- 技術：
  - Spring Kafka（`KafkaTemplate`, `@KafkaListener`）
  - 預留 Kafka Streams / Spring Cloud Stream 作即時流處理

### 4.4 設定管理

- Config Server：`cclemon-config-server`
- Config Client：其他服務的 `application.yml` / `application-*.yml` + `@RefreshScope`

## 5. 開發與部署流程（技術面）

### 5.1 建置與測試

- 建置全部模組：
  - `./gradlew clean build`
- 不跑測試建置：
  - `./gradlew clean build -x test`
- 建置單一模組：
  - `./gradlew :cclemon-auth:build`
- 執行單一 Spring Boot 應用：
  - `./gradlew :cclemon-auth:bootRun`

### 5.2 Docker 與網路

- 建立專案共用 Docker network（只需執行一次）：
  - `docker network create cclemon-network`
- 各服務容器加入 `cclemon-network`，以 service name 互相呼叫

### 5.3 版本與相依管理

- 使用 Spring Cloud BOM 管理 Spring Cloud 相關相依版本
- 所有子模組共用：
  - `commons-lang3`
  - `commons-collections4`
  - Lombok（`compileOnly` + `annotationProcessor`）
  - 測試框架：Spring Boot Test + JUnit Platform
