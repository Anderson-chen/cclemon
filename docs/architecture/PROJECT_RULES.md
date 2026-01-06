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

## 4. AI Agent 角色

本專案定義了三種 AI Agent 角色，各有專屬準則文件：

| 角色 | 文件 | 職責 |
|------|------|------|
| **系統分析師 (SA)** | [SA_GUIDELINES.md](../roles/SA_GUIDELINES.md) | 需求分析、Use Case 撰寫、API 契約設計 |
| **開發人員 (Developer)** | [DEVELOPER_GUIDELINES.md](../roles/DEVELOPER_GUIDELINES.md) | 架構原則、程式碼實作、Git 工作流程 |
| **品質保證 (QA)** | [QA_GUIDELINES.md](../roles/QA_GUIDELINES.md) | 測試策略、品質標準、驗收流程 |

### 角色協作流程

```
SA (做什麼) → Developer (怎麼做) → QA (做對了嗎)
```

### 通用準則
- **模組邊界**: 尊重模組依賴關係，嚴禁循環依賴。
- **分層架構**: 維持 Controller → Service → Repository 的流程。
- **任務執行**: 先分析請求背景 → 定位相關檔案 → 規劃變更範圍 → 執行修改。
