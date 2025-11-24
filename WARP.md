# WARP.md

此檔案用來說明在本儲存庫中工作時，WARP（warp.dev 的 AI 助手）應該如何操作與思考。

## 專案概觀

CCLEMON 是一個使用 Java 21 開發的 Spring Boot 3.5.3 微服務架構，並採用 Spring Cloud 2025.0.0。專案以多模組 Gradle 結構組織，搭配 Docker 容器化與 Jenkins CI/CD 管線。

## 常用指令

### 建置與測試
```bash
# 建置所有模組
./gradlew build

# 建置但略過測試
./gradlew clean build -x test

# 建置特定模組
./gradlew :cclemon-auth:build

# 執行所有模組的測試
./gradlew test

# 僅執行特定模組的測試
./gradlew :cclemon-core:test

# 清除並重新建置全部
./gradlew clean build
```

### 開發模式
```bash
# 執行單一 Spring Boot 應用程式（以 auth 服務為例）
./gradlew :cclemon-auth:bootRun

# 檢查相依套件
./gradlew dependencies

# 檢視專案模組結構
./gradlew projects
```

### Docker 環境
```bash
# 建立專案所需的 Docker network（第一次使用時執行）
docker network create cclemon-network

# 為單一模組手動建置 Docker image 的範例
# 範例：cd cclemon-auth && cp build/libs/cclemon-auth.jar build/libs/app.jar
# docker build -f ../ci/Dockerfile -t cclemon-auth:dev build/libs
```

## 架構概觀

### 模組結構
本專案採用模組化微服務架構，各模組職責清晰分工：

- **cclemon-core**：共用核心模組，包含：
  - 具 JPA 審計欄位的 `BaseEntity`（id、version、建立／修改時間、軟刪除）
  - Redis 整合與相關設定
  - OAuth2 Resource Server 安全性設定
  - OpenFeign 客戶端設定

- **cclemon-auth**：OAuth2 授權伺服器
  - Spring Authorization Server 實作
  - 透過 JPA Repository 進行使用者管理
  - OAuth2 client 與授權同意（consent）管理
  - 第三方身分整合（Federated identity）
  - Thymeleaf UI 模板

- **cclemon-data**：共用資料模型與實體
  - 系統設定相關實體（`ConfigKey`、`ConfigValue` 等）
  - 多語系支援實體（`ConfigKeyI18n`）
  - 設定變更稽核實體（`ConfigValueAudit`）

- **cclemon-config-server**：Spring Cloud Config Server
  - 集中化設定管理
  - 透過 Kafka Bus 進行設定刷新（整體架構預留）
  - 健康檢查與監控

- **cclemon-health**：健康相關服務
  - Spring Cloud Config Client
  - Kafka Bus 整合
  - Actuator 健康檢查端點

- **cclemon-eventBus**：事件匯流與通訊模組
  - WebSocket 支援（設計層面）
  - Kafka 整合，進行訊息收發

- **cclemon-stream**：資料流處理模組
  - Kafka Streams 相關處理（預期用途）

- **cclemon-userEvent**：使用者事件處理服務
- **cclemon-logging**：集中式記錄（logging）工具模組
- **cclemon-practice**：練習與範例程式模組

### 主要技術
- **框架**：Spring Boot 3.5.3 + Spring Cloud 2025.0.0
- **語言**：Java 21（搭配 Lombok）
- **資料**：JPA/Hibernate + MySQL，Redis 作為快取
- **安全**：Spring Security + OAuth2 授權伺服器
- **訊息**：Apache Kafka + Spring Kafka
- **建置**：多模組 Gradle 專案
- **部署**：Docker 容器，透過 Jenkins CI/CD pipeline

### 模組相依關係
- 大多數服務模組依賴 `cclemon-core` 以取得共用功能
- `cclemon-auth` 依賴 `cclemon-data` 取得使用者／授權相關實體
- 全部模組都會使用一些共用相依：Commons Lang3、Commons Collections4、Lombok

### 部署架構
- 每個微服務以獨立 Docker 容器執行
- 服務彼此透過共用 Docker 網路（`cclemon-network`）溝通
- 使用 Actuator `/actuator/health` 進行健康檢查
- 透過 Spring Profile（dev, prod 等）切換環境設定

### 設定管理
- 使用 `cclemon-config-server` 作為集中設定中心
- 各服務透過 Spring Profile 取得環境對應設定
- 預留透過 Kafka 進行設定刷新通知
- 支援機密／加密設定值的管理

## 開發模式與慣例

### 實體設計
- 所有 JPA 實體應繼承 `BaseEntity` 以統一審計欄位
- 建議使用 Lombok 註解：`@Builder`、`@Data`、`@NoArgsConstructor`、`@AllArgsConstructor`
- 依循既有的資料庫欄位命名慣例

### 模組間溝通
- 同步服務呼叫建議使用 OpenFeign client
- 非同步、解耦溝通建議透過 Kafka 事件驅動
- 所有對外 API 應正確實作 OAuth2 Resource Server 安全性設定

### Docker 整合
- 預期所有服務都在 `cclemon-network` 中執行
- 每個服務都必須實作健康檢查端點 `/actuator/health`
- 透過環境變數控制 Spring Profile 與連線設定

### CI/CD 流程
- Jenkins 依照參數建置個別模組
- 支援 `dev` 與 `prod` 等多環境
- 非正式環境可設定自動部署流程
- 部署完成前需通過健康檢查驗證

## 對 AI 助手的專案特定指引

- 當你在本儲存庫中回答「架構設計、技術設計、業務／領域建模」相關問題時：
  - **請優先閱讀並遵守根目錄的 `TECH_ARCHITECTURE.md` 與 `BUSINESS_ARCHITECTURE.md` 內容。**
  - 將這兩個檔案視為：模組職責、技術邊界與業務背景的主要依據。
  - 若有任何模糊或多種做法，請優先讓建議方案與這兩份文件中已記載的意圖與慣例保持一致。

- 當你在本儲存庫中「產生或修改程式碼、測試、設定」時：
  - **請一併閱讀並遵守根目錄的 `CODING_GUIDELINES.md`。**
  - 產生的程式碼與測試應符合其中規定的穩定性、可讀性、測試覆蓋與效能等原則。
  - 在多種實作方案中，應優先選擇與本守則及既有程式風格一致的方案。

- 若發現文件內容（例如 `TECH_ARCHITECTURE.md`、`BUSINESS_ARCHITECTURE.md`、`CODING_GUIDELINES.md`）與實際程式碼明顯不一致時：
  - 以實際程式碼為最終事實來源進行分析與建議。
  - 應明確提醒使用者考慮同步更新相關文件，以維持文件與程式碼的一致性。
