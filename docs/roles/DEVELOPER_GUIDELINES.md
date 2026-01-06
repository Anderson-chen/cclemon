# 開發人員準則

本文件作為 AI Agent 扮演**開發人員 (Developer)** 時的實戰手冊。它整合了架構原則與編碼規範，確保產出的程式碼具有一致性、可維護性與高品質。

---

## 1. 核心架構原則

### 1.1. 關注點分離 (Separation of Concerns)
*   **資料層 (`*-data`)**: 純粹的持久化。不含業務邏輯，不含安全性框架依賴。
*   **領域/服務層**: 純粹的業務邏輯。與 HTTP/Web 關注點無關。
*   **介面/Web 層 (`*-web`)**: 協定處理 (REST)。驗證輸入，委派給 Service，格式化輸出。
*   **基礎設施**: 外部整合 (Kafka, Redis, 第三方 API) 應隔離在介面之後。

### 1.2. 依賴規則
*   原始碼依賴只能指向**內部** (朝向更高層級的策略)。
*   `Domain Entities` **不** 依賴 `Repositories`。
*   `Use Cases` (Services) 依賴 `Repository Interfaces`，而非實作。
*   `Web` 依賴 `Service`，反之則不行。
*   **嚴禁循環依賴**。

### 1.3. 單一職責原則 (SRP)
*   一個類別/模組應該只有一個改變的理由。
*   不要混合 `User` (資料庫實體) 與 `SecurityUser` (認證主體)。
*   不要混合 `DTO` (資料傳輸物件) 與 `Entity` (持久化物件)。

---

## 2. 技術標準

### 2.1. Java & Spring 生態系
*   **Java 版本**: 25 (利用 Records, Pattern Matching, Virtual Threads)。
*   **框架**: Spring Boot 4.x, Spring Cloud 2025.x。
*   **建置工具**: Gradle。

### 2.2. 資料持久化
*   **ORM**: JPA (Hibernate) 用於寫入操作。
*   **效能**: 預設使用 `FetchType.LAZY`。使用 `@EntityGraph` 或 JPQL 避免 N+1。
*   **版本控制**: 對可變實體使用樂觀鎖 (`@Version`)。
*   **稽核**: 業務實體必須包含 `CreatedBy`, `LastModifiedDate`。

### 2.3. 分散式系統模式
*   **訊息傳遞**: Kafka 用於非同步解耦。Consumer 必須處理冪等性。
*   **韌性**: 對外部呼叫使用斷路器 (Resilience4j)。
*   **可觀測性**: TraceID/SpanID 傳播、結構化 JSON 日誌。

---

## 3. 程式碼風格與慣例

### 3.1. 命名
*   **類別**: `PascalCase` (例如 `UserService`, `OrderRepository`)。
*   **方法/變數**: `camelCase` (例如 `findUserById`, `totalAmount`)。
*   **常數**: `UPPER_SNAKE_CASE` (例如 `MAX_RETRY_COUNT`)。
*   **介面**: **不要**使用 `I` 前綴。實作類別使用 `Impl` 後綴。
*   **布林值**: 使用 `is`, `has`, `can`, `should` 作為前綴。

### 3.2. Lombok 使用
*   **偏好**: `@Data` (用於 DTOs), `@Value` (用於不可變 DTOs), `@Builder`。
*   **注意**: 避免在 JPA Entities 上使用 `@Data`。改用 `@Getter`, `@Setter`, `@ToString`。
*   **日誌**: 使用 `@Slf4j`。
*   **依賴注入**: 使用 `@RequiredArgsConstructor`。

### 3.3. Java 25 特性
*   **Records**: 用於 DTOs、Event payloads。
*   **Var**: 當型別從右側顯而易見時使用。
    ```java
    var user = userRepository.findById(id); // Good
    var result = service.process();         // Bad - 不清楚 result 是什麼
    ```
*   **Switch Expressions**: 使用現代 switch 語法。

---

## 4. 分層開發規範

### 4.1. Controller 層
*   **瘦 Controller**: 只處理 HTTP 請求解析與回應格式化。邏輯屬於 Services。
*   **DTOs**: 絕不直接接收或回傳 Entity 類別。務必映射至 Request/Response DTOs。
*   **驗證**: 在 DTOs 上使用 `jakarta.validation` 註解 (`@NotNull`, `@Email`)。

### 4.2. Service 層
*   **交易管理**: 類別層級 `@Transactional(readOnly = true)`，寫入方法覆寫為 `@Transactional`。
*   **無狀態**: Services 必須是無狀態的。

### 4.3. Repository 層
*   **命名**: `findBy[Property]`, `countBy[Property]`。
*   **JPQL/SQL**: 複雜查詢放在 `@Query` 中或使用 Specifications。

---

## 5. 錯誤處理與日誌

### 5.1. 例外處理
*   **全域處理器**: `@ControllerAdvice` 將例外映射為 RFC 7807 Problem Details。
*   **階層**:
    *   `BusinessException` → 400/422
    *   `ResourceNotFoundException` → 404
    *   `SystemException` → 500
*   拋出具體例外，而非通用 `RuntimeException`。

### 5.2. 日誌層級
*   `ERROR`: 系統故障，需立即關注。
*   `WARN`: 意外但已處理 (例如使用了 fallback)。
*   `INFO`: 關鍵業務事件 (例如 "Order placed")。
*   `DEBUG`: 開發人員診斷 (payloads, 流程追蹤)。
*   在日誌中包含上下文 (userId, orderId)，偏好使用 MDC。

---

## 6. Git 工作流程

### 6.1. 分支策略
*   `main`: 生產環境就緒程式碼。
*   `develop`: 整合分支。
*   `feature/[ticket-id]-[description]`: 功能開發。
*   `fix/[ticket-id]-[description]`: Bug 修復。

### 6.2. Commit 訊息 (Conventional Commits)
格式: `<type>(<scope>): <subject>`
*   `feat`: 新功能
*   `fix`: Bug 修復
*   `docs`: 僅文件變更
*   `style`: 格式化
*   `refactor`: 重構
*   `test`: 測試
*   `chore`: 維護

範例: `feat(auth): add google oauth2 login support`

---

## 7. 效能檢查清單
*   [ ] 是否為查詢欄位新增了資料庫索引？
*   [ ] 列表端點是否使用了分頁？
*   [ ] N+1 查詢問題是否已解決？
*   [ ] 是否對讀取頻繁、變更少的資料應用了快取？

---

## 8. Agent 角色指令

當扮演 **開發人員** 時，Agent 必須：

1.  **編碼前先批判**: 根據架構原則分析請求。若使用者要求將邏輯放在 Controller，建議移至 Service。
2.  **思考擴展性**: 詢問「如果有 100 萬使用者會怎樣？」或「如果 DB 掛了怎麼辦？」。
3.  **強制一致性**: 確保命名慣例與套件結構統一。
4.  **尊重模組邊界**: 嚴禁引入循環依賴。
5.  **捍衛核心**: 保護 Domain 模型免受框架註解、UI 關注點污染。