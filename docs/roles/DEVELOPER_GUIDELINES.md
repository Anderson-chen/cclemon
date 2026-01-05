# 企業級開發人員準則

本文件作為 AI Agent 扮演**開發人員 (Developer)** 時的「**實戰手冊 (Field Manual)**」。它定義了在 `cclemon` 專案中撰寫程式碼的具體規則、風格與日常工作流程，確保產出的程式碼具有一致性、可讀性與高品質。

---

## 1. 程式碼風格與慣例

### 1.1. 命名
*   **類別 (Classes)**: `PascalCase` (例如 `UserService`, `OrderRepository`)。
*   **方法/變數 (Methods/Variables)**: `camelCase` (例如 `findUserById`, `totalAmount`)。
*   **常數 (Constants)**: `UPPER_SNAKE_CASE` (例如 `MAX_RETRY_COUNT`)。
*   **介面 (Interfaces)**: **不要** 使用 `I` 前綴 (例如 `UserService`，而不是 `IUserService`)。若只有一個實作，實作類別應使用 `Impl` 後綴 (例如 `UserServiceImpl`)。
*   **布林值 (Boolean)**: 使用 `is`, `has`, `can`, `should` 作為前綴 (例如 `isActive`, `hasPermission`)。

### 1.2. Lombok 使用
*   **偏好**: `@Data` (用於 DTOs), `@Value` (用於不可變 DTOs), `@Builder`。
*   **注意**: 避免在 JPA Entities 上使用 `@Data` (會有 equals/hashCode 問題)。改用 `@Getter`, `@Setter`, `@ToString`。
*   **日誌**: 使用 `@Slf4j`。

### 1.3. Java 25 特性
*   **Records**: 用於 DTOs, Event payloads, 和 Map keys 使用 `record`。
*   **Var**: 當型別從右側顯而易見時，對區域變數使用 `var`。
    ```java
    var user = userRepository.findById(id); // Good
    var result = service.process(); // Bad (result 是什麼?)
    ```
*   **Switch Expressions**: 使用現代 switch 語法。

---

## 2. API 開發 (REST)

### 2.1. Controller 層
*   **瘦 Controller**: Controller 應只處理 HTTP 請求解析與回應格式化。邏輯屬於 Services。
*   **DTOs**: 絕不直接接收或回傳 Entity 類別。務必映射至 Request/Response DTOs。
*   **驗證**: 在 DTOs 上使用 `jakarta.validation` 註解 (`@NotNull`, `@Email`)。

### 2.2. Service 層
*   **交易管理**: 在類別層級使用 `@Transactional(readOnly = true)`，並在寫入方法上覆寫為 `@Transactional`。
*   **無狀態**: Services 必須是無狀態的。

### 2.3. Repository 層
*   **命名**: `findBy[Property]`, `countBy[Property]`。
*   **JPQL/SQL**: 將複雜查詢放在 `@Query` 中或使用 Specifications。

---

## 3. Git 工作流程

### 3.1. 分支策略
*   `main`: 生產環境就緒程式碼。
*   `develop`: 整合分支。
*   `feature/[ticket-id]-[description]`: 功能開發 (例如 `feature/JIRA-123-user-login`)。
*   `fix/[ticket-id]-[description]`: Bug 修復。

### 3.2. Commit 訊息 (Conventional Commits)
格式: `<type>(<scope>): <subject>`
*   `feat`: 新功能
*   `fix`: Bug 修復
*   `docs`: 僅文件變更
*   `style`: 格式化、缺少分號等
*   `refactor`: 既非修復 bug 也未增加功能的程式碼變更
*   `test`: 增加缺失的測試
*   `chore`: 維護工作

範例: `feat(auth): add google oauth2 login support`

---

## 4. 錯誤處理與日誌

### 4.1. 日誌
*   **層級**:
    *   `ERROR`: 系統故障，需立即關注。
    *   `WARN`: 意外但已處理 (例如使用了 fallback)。
    *   `INFO`: 關鍵業務事件 (例如 "Order placed")。
    *   `DEBUG`: 開發人員診斷 (payloads, 流程追蹤)。
*   **內容**: 在日誌中包含上下文 (userId, orderId) (偏好使用 MDC)。

### 4.2. 例外
*   拋出具體例外 (例如 `UserNotFoundException`) 而非通用的 `RuntimeException`。

---

## 5. 效能檢查清單
*   [ ] 是否為查詢欄位新增了資料庫索引？
*   [ ] 列表端點是否使用了分頁？
*   [ ] N+1 查詢問題是否已解決？
*   [ ] 是否對讀取頻繁、變更少的資料應用了快取？
