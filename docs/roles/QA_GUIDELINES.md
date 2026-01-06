# 企業級 QA 與測試準則

本文件作為 AI Agent 扮演**品質保證工程師 (QA Engineer)** 時的「**品質聖經 (Quality Bible)**」。它定義了 `cclemon` 專案的測試策略、品質標準與驗收流程，旨在確保交付的軟體穩定、可靠且符合預期。

---

## 1. 測試金字塔策略

### 1.1. 單元測試 (Unit Tests - L1)
*   **範圍**: 個別類別 (Services, Utils)。
*   **工具**: JUnit 5, Mockito。
*   **涵蓋率目標**: 業務邏輯 > 80% 行涵蓋率。
*   **規則**: Mock 所有外部依賴 (Repositories, API Clients)。
*   **速度**: 必須在毫秒級完成。

### 1.2. 整合測試 (Integration Tests - L2)
*   **範圍**: 元件互動 (Controller + Service + DB)。
*   **工具**: `@SpringBootTest`, Testcontainers (用於 DB/Redis/Kafka)。
*   **重點**:
    *   資料庫查詢如預期運作。
    *   交易 rollback 運作正常。
    *   安全性規則被強制執行。
*   **資料**: 每個測試類別使用 H2 或 Dockerized DB 並保持乾淨狀態。

### 1.3. 端對端測試 (E2E Tests - L3)
*   **範圍**: 完整使用者流程 (登入 -> 加入購物車 -> 結帳)。
*   **工具**: Postman / Newman, Cucumber (選用)。
*   **環境**: Staging / QA 環境。

---

## 2. 測試程式碼標準

### 2.1. 命名
*   **類別**: `[ClassName]Test` (例如 `UserServiceTest`)。
*   **方法**: `should[ExpectedBehavior]_when[Condition]`
    *   範例: `shouldThrowException_whenUserNotFound()`
    *   範例: `shouldReturnUser_whenIdIsValid()`

### 2.2. 結構 (AAA 模式)
*   **Arrange (安排)**: 準備資料與 Mocks。
*   **Act (執行)**: 呼叫受測方法。
*   **Assert (斷言)**: 使用 AssertJ (`assertThat(...)`) 驗證結果。

### 2.3. Spring Boot 4.x 測試 Mocking **[重要]**
*   **規則**: 在 Spring Boot 整合測試中，必須使用 `@MockitoBean` 來建立 Mock 物件。
*   **註解**: `@org.springframework.test.context.bean.override.mockito.MockitoBean`
*   **Import 語句**: `import org.springframework.test.context.bean.override.mockito.MockitoBean;`
*   **注意**: **嚴禁**使用舊版 Spring Boot 的 `@MockBean` (`org.springframework.boot.test.mock.mockito.MockBean`)。這是確保測試與框架版本一致的關鍵。

### 2.4. JSON 處理
*   **工具**: 一律使用 `com.fasterxml.jackson.databind.json.JsonMapper`。
*   **禁止**: 避免直接實例化 `ObjectMapper`，以確保配置一致性。

### 2.5. 最佳實踐
*   **測試中無邏輯**: 避免在測試程式碼中使用迴圈或複雜條件。
*   **獨立性**: 測試不得依賴執行順序。
*   **清理**: 執行後重置 Mocks 與資料 (`@AfterEach` 或 `@Transactional`)。

---

## 3. Bug 回報與分類

### 3.1. Bug 回報模板
*   **標題**: [元件] 簡潔描述問題。
*   **嚴重性**: Critical (嚴重) / Major (主要) / Minor (次要) / Trivial (微不足道)。
*   **環境**: Dev / QA / Prod。
*   **重現步驟**:
    1.  以使用者 A 登入。
    2.  點擊 X。
    3.  ...
*   **預期結果**: ...
*   **實際結果**: ...
*   **日誌/截圖**: 附上相關 stack traces。

### 3.2. 完成定義 (Definition of Done - DoD)
一個功能在以下情況視為「完成」：
*   [ ] 程式碼已合併至 `develop`。
*   [ ] 單元測試通過 (>80% 涵蓋率)。
*   [ ] 整合測試通過。
*   [ ] 程式碼審查完成。
*   [ ] 無開啟的 Critical/Major bugs。

---

## 4. 自動化與 CI/CD 閘門

*   **Commit Hook**: Pre-commit 檢查格式化與基本編譯。
*   **建置 Pipeline**:
    1.  編譯。
    2.  執行單元測試。
    3.  靜態分析 (SonarQube/Checkstyle)。
    4.  建置 Artifact (Jar/Docker Image)。
*   **品質閘門 (Quality Gate)**: 若涵蓋率下降或發現嚴重漏洞，建置失敗。
