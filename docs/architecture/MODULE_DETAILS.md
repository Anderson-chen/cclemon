# 模組詳細功能說明

本文件深入探討 `cclemon` 專案中每個模組的具體職責、關鍵技術與互動模式。

---

### 1. `cclemon-auth` (授權伺服器)
*   **主要職責**: 作為 OAuth2 / OIDC 授權中心，管理客戶端 (Client) 與使用者 (User) 的認證與授權。
*   **關鍵技術**: `spring-boot-starter-oauth2-authorization-server`, `spring-boot-starter-oauth2-client`, `spring-security`.
*   **核心功能**:
    *   **Token 端點**: 提供 `/oauth2/token` 端點，用於發行 Access Token 和 Refresh Token。
    *   **OIDC UserInfo**: 提供 `/userinfo` 端點，讓客戶端能取得使用者基本資料。
    *   **身分認證**: 整合本地帳號密碼登入 (`formLogin`) 與第三方社交登入 (Google)。
    *   **實體管理**: 包含 OAuth2 協議所需的 `Client`, `Authorization`, `AuthorizationConsent` 等 JPA 實體。
*   **互動模式**:
    *   依賴 `cclemon-data` 取得 `User` 實體進行認證。
    *   前端應用 (UI) 會導向此服務進行登入，並取得 Token。
    *   其他後端服務 (如 `cclemon-web`) 會將收到的 Token 轉交此服務或其公開的 JWK Set URI 進行驗證。

### 2. `cclemon-data` (資料存取層)
*   **主要職責**: 專案的核心資料存取層，定義所有共用的 JPA 實體與其對應的 Repository。
*   **關鍵技術**: `spring-boot-starter-data-jpa`, `hibernate`, `mysql-connector-j`.
*   **核心功能**:
    *   **共用實體**: 定義核心業務實體，例如 `User`。
    *   **基礎實體**: 提供 `BaseEntity`，包含 `id`, `version`, `createTime` 等通用稽核欄位。
    *   **資料庫操作**: 提供 Spring Data JPA Repositories 介面，如 `UserRepository`。
*   **互動模式**:
    *   是多數業務模組 (如 `cclemon-auth`, `cclemon-web`) 的底層依賴，提供資料庫 CRUD 功能。
    *   本身不應依賴任何上層業務模組。

### 3. `cclemon-web` (Web 應用層)
*   **主要職責**: 作為主要的 API Gateway 或 BFF (Backend for Frontend)，提供對外的 RESTful API。
*   **關鍵技術**: `spring-boot-starter-web`, `springdoc-openapi` (API 文件), `spring-cloud-starter-config`.
*   **核心功能**:
    *   **REST Controllers**: 包含所有業務邏輯的 API 端點。
    *   **DTOs**: 定義 API 的請求 (Request) 與回應 (Response) 資料傳輸物件。
    *   **API 文件**: 自動生成 OpenAPI (Swagger) 文件。
    *   **服務聚合**: 呼叫其他內部服務或模組，組合後回傳給前端。
*   **互動模式**:
    *   依賴 `cclemon-security` 來保護其 API 端點。
    *   依賴 `cclemon-data` 或其他服務模組來取得資料。
    *   透過 `cclemon-kafka` 發送非同步訊息。

### 4. `cclemon-security` (共用安全性)
*   **主要職責**: 提供給資源伺服器 (Resource Server) 共用的安全性配置。
*   **關鍵技術**: `spring-boot-starter-oauth2-resource-server`.
*   **核心功能**:
    *   **Token 驗證**: 配置如何解析與驗證傳入的 JWT Access Token。
    *   **權限控制**: 可能包含共用的權限表達式或方法級安全 (`@PreAuthorize`) 的配置。
*   **互動模式**:
    *   被 `cclemon-web` 等需要保護 API 的模組所依賴。

### 5. `cclemon-kafka` & `cclemon-consumer`
*   **`cclemon-kafka` (Kafka 基礎設施)**:
    *   **職責**: 提供 Kafka 生產者 (Producer) 的配置與封裝。
    *   **技術**: `spring-boot-starter-kafka`.
    *   **功能**: 封裝 `KafkaTemplate`，提供統一的訊息發送介面。
*   **`cclemon-consumer` (訊息消費者)**:
    *   **職責**: 監聽 Kafka Topic 並處理訊息。
    *   **技術**: `@KafkaListener`.
    *   **功能**: 包含多個消費者群組，處理如訂單完成、使用者註冊等非同步任務。
*   **互動模式**:
    *   `cclemon-web` 依賴 `cclemon-kafka` 來發送訊息。
    *   `cclemon-consumer` 依賴 `cclemon-interface` 來取得訊息的 DTO 定義。

### 6. `cclemon-interface` (共用介面)
*   **主要職責**: 解除模組間的直接依賴，定義跨模組的契約 (Contracts)。
*   **核心功能**:
    *   **DTOs**: 定義 Kafka 訊息的資料結構或模組間呼叫的資料物件。
    *   **介面**: 定義需由其他模組實現的介面，例如 `AuditorProvider`，讓 `cclemon-data` 的稽核功能可以取得當前使用者 ID，而不用直接依賴 `cclemon-security`。
*   **互動模式**:
    *   通常只有 Java 原始碼，不含框架依賴。
    *   被需要共享資料結構或行為的模組所依賴。

### 7. 其他模組
*   **`cclemon-utils`**: 存放如字串處理、日期轉換等與業務無關的純工具類。
*   **`cclemon-logging`**: 透過 AOP (`@Aspect`) 提供集中式的日誌功能，如記錄方法執行時間、追蹤請求參數等。
*   **`cclemon-cache`**: 整合 Redis (`spring-boot-starter-data-redis`)，提供快取相關的配置與工具類。
*   **`cclemon-config-server`**: Spring Cloud Config 伺服器，用於從 Git 倉庫等外部源讀取並集中管理所有模組的設定檔。
*   **`cclemon-health`**: 整合 Spring Boot Actuator，提供健康檢查端點，用於監控服務狀態。
*   **`cclemon-stream`**: 可能用於複雜的事件串流處理，例如使用 Kafka Streams API 進行即時的資料聚合與分析。
*   **`cclemon-practice`**: 練習或展示用的模組，不應包含在生產建置中。
