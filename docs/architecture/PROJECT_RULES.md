# 專案架構與 Agent 準則

## 1. 專案概觀
**名稱**: cclemon
**類型**: Spring Boot 多模組後端應用程式
**語言**: Java 25
**框架**: Spring Boot 4.0.1, Spring Cloud 2025.1.0
**建置工具**: Gradle

## 2. 模組結構
本專案劃分為以下模組。關於每個模組的詳細職責、技術與互動模式，請參閱 [模組詳細功能說明](./MODULE_DETAILS.md)。

| 模組 | 用途 |
| :--- | :--- |
| `cclemon-auth` | 授權伺服器 (OAuth2, Security) |
| `cclemon-data` | 資料存取層 (JPA Entities, Repositories) |
| `cclemon-web` | Web 層 (REST Controllers) |
| `cclemon-security` | 共用安全性配置 |
| `cclemon-cache` | 快取實作 (Redis) |
| `cclemon-config-server` | 集中式配置伺服器 |
| `cclemon-kafka` | Kafka 訊息基礎設施 |
| `cclemon-utils` | 通用工具類 |
| `cclemon-interface` | 共用介面與 DTOs |
| `cclemon-logging` | 集中式日誌 |
| `cclemon-health` | 健康檢查與監控 |
| `cclemon-stream` | 串流處理 |
| `cclemon-consumer` | 訊息消費者 |

## 3. 技術堆疊
- **核心**: Java 25
- **Spring**: Spring Boot 4.0.1, Spring Security, Spring Data JPA, Spring Cloud
- **資料庫**: MySQL, H2 (Runtime)
- **快取**: Redis
- **訊息佇列**: Kafka
- **工具**: Lombok

## 4. Agent (Gemini) 開發準則
協助此專案時，請遵守以下規則：

### A. 程式碼風格與標準
- **Java 25 特性**: 適當使用現代 Java 特性，如 Records, Pattern Matching, Virtual Threads。
- **Lombok**: 持續使用 Lombok 減少樣板程式碼 (`@Data`, `@RequiredArgsConstructor`, `@Slf4j`)。
- **依賴注入**: 優先使用建構子注入 (通常透過 `@RequiredArgsConstructor`)。
- **模組邊界**: 尊重模組依賴關係。嚴禁引入循環依賴。

### B. 檔案操作
- **驗證**: 讀寫前務必驗證檔案是否存在與路徑正確性。
- **增量變更**: 可能的話，使用 `replace_text` (若可用) 或在需要大幅變更時寫入完整檔案，但需確保上下文被保留。
- **路徑處理**: 使用環境提供的絕對路徑。

### C. 架構一致性
- **分層方法**: 維持 Controller -> Service -> Repository 的流程。
- **配置**: 將配置保留在相關模組的 `config` 套件中。
- **實體 (Entities)**: 將實體持久化於 `cclemon-data` 或特定的領域模組中 (若有分離)。

### D. 任務執行策略
1.  **分析**: 在多模組結構的背景下理解請求。
2.  **定位**: 使用 `list_files` 或 `grep` 尋找相關檔案。
3.  **計畫**: 決定哪些模組需要修改。
4.  **執行**: 使用 `write_file` 應用變更。
