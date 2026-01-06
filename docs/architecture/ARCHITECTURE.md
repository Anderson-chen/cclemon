# cclemon 專案架構

## 版本資訊

| 項目 | 內容 |
|------|------|
| 文件版本 | 1.1 |
| 更新日期 | 2026-01-06 |
| 專案名稱 | cclemon 健康管理系統 |
| 技術框架 | Spring Boot 4.0.1 + Spring Modulith |

---

## 1. 專案概觀

**類型**: Spring Boot 多模組後端應用程式

### 技術堆疊

| 類別 | 技術 |
|------|------|
| **語言** | Java 25 |
| **核心框架** | Spring Boot 4.0.1, Spring Cloud 2025.1.0 |
| **模組化** | Spring Modulith |
| **資料庫** | MySQL, H2 (測試) |
| **快取** | Redis |
| **訊息佇列** | Kafka |
| **建置工具** | Gradle |
| **測試** | JUnit 5, Mockito, Testcontainers |

#### **架構特別說明：Spring Boot 4.x 測試框架遷移**
> **警告**: 本專案使用 Spring Boot 4.x，其測試 Mocking 機制與 3.x 有重大差異。所有開發與測試人員必須遵守以下規範：
> - **應使用**: `@MockitoBean`
> - **正確 Import**: `import org.springframework.test.context.bean.override.mockito.MockitoBean;`
> - **禁止使用**: 舊版 `@MockBean` (`org.springframework.boot.test.mock.mockito.MockBean`)
>

---

## 2. 系統架構圖

```
+===========================================================================+
|                           ENTRY POINTS (入口層)                            |
|  +---------------+    +---------------+    +---------------+              |
|  | cclemon-web   |    | cclemon-job   |    | cclemon-      |              |
|  | (REST API)    |    | (Scheduled)   |    | consumer      |              |
|  |               |    |               |    | (Kafka)       |              |
|  | - Controller  |    | - JobRunner   |    | - Listener    |              |
|  | - DTO         |    | - Scheduler   |    | - Dispatcher  |              |
|  +-------+-------+    +-------+-------+    +-------+-------+              |
|          |                    |                    |                      |
|          +--------------------+--------------------+                      |
|                               |                                           |
|                    Protocol Adapter (協議轉換)                             |
+===========================================================================+
                                |
                                v
+===========================================================================+
|                        HANDLER LAYER (Handler 介面層)                      |
|  +---------------------------------------------------------------------+  |
|  |                     cclemon-api (Handler 契約)                       |  |
|  |  +------------------+  +------------------+  +------------------+   |  |
|  |  | WeightHandler    |  | ExerciseHandler  |  | FoodHandler      |   |  |
|  |  | (interface)      |  | (interface)      |  | (interface)      |   |  |
|  |  +------------------+  +------------------+  +------------------+   |  |
|  +---------------------------------------------------------------------+  |
+===========================================================================+
                                |
                                v
+===========================================================================+
|                      BUSINESS MODULES (業務模組層)                         |
|                         Spring Modulith Managed                           |
|  +-------------------------+                                              |
|  |   health (身體組成管理)   |                                              |
|  | +---------------------+ |                                              |
|  | | api/                | | ← 公開 Handler 實作                           |
|  | | internal/           | | ← 私有 Service, Repository                   |
|  | | events/             | | ← 領域事件                                    |
|  | +---------------------+ |                                              |
|  +-------------------------+                                              |
+===========================================================================+
                                |
                                v
+===========================================================================+
|                    INFRASTRUCTURE (基礎設施層)                             |
|  +-------------+  +-------------+  +-------------+  +-------------+       |
|  | cclemon-    |  | cclemon-    |  | cclemon-    |  | cclemon-    |       |
|  | data        |  | cache       |  | kafka       |  | security    |       |
|  +-------------+  +-------------+  +-------------+  +-------------+       |
+===========================================================================+
```

### Handler 呼叫流程

```
+----------+     +----------+     +---------+     +----------+     +--------+
|  Web     |     | Protocol |     | Handler |     | Service  |     | Repo   |
| Request  | --> | Adapter  | --> | (API)   | --> | (Domain) | --> | (Data) |
+----------+     +----------+     +---------+     +----------+     +--------+
```

---

## 3. 模組分層架構

```
入口層 (Entry Points)     → cclemon-web, cclemon-job, cclemon-consumer
契約層 (API Contracts)    → cclemon-api
業務層 (Business Logic)   → cclemon-health
基礎設施層 (Infrastructure) → cclemon-data, cclemon-cache, cclemon-kafka, cclemon-security
工具層 (Utilities)        → cclemon-utils, cclemon-logging
```

---

## 4. 模組詳細說明

### 4.1 入口層模組

#### `cclemon-web` (Web API 入口)
- **職責**: REST API 入口層，協議轉換，將 HTTP 請求轉換為 Handler 呼叫
- **技術**: `spring-boot-starter-web`, `springdoc-openapi`
- **設計原則**: 無業務邏輯，Controller 只做協議轉換

#### `cclemon-job` (排程任務入口) [新增]
- **職責**: 排程任務入口層，定時觸發 Handler 執行批次作業
- **技術**: `spring-boot-starter`, `@Scheduled`
- **設計原則**: 獨立部署，僅負責觸發

#### `cclemon-consumer` (Kafka 消費者入口)
- **職責**: 監聽 Kafka Topic 並觸發 Handler 處理訊息
- **技術**: `spring-boot-starter-kafka`, `@KafkaListener`

### 4.2 契約層模組

#### `cclemon-api` (Handler 契約) [新增]
- **職責**: 定義業務模組對外暴露的 Handler 介面與 DTO
- **技術**: `jakarta.validation` (無 Spring 依賴)
- **內容**:
  - Handler 介面: `WeightHandler`, `ExerciseHandler`
  - Command 物件: `RecordWeightCommand`
  - Query 物件: `ListWeightsQuery`
  - Result 物件: `WeightRecordResult`
  - Event 定義: `WeightRecordedEvent`

### 4.3 業務層模組

#### `cclemon-health` (身體組成管理)
- **職責**: 身體組成管理業務核心（體重、運動、飲食記錄）
- **技術**: `spring-modulith`, `spring-boot-starter-data-jpa`
- **內部結構**:
  ```
  cclemon-health/
    └── org/cclemon/health/
        ├── api/        ← 公開的 Handler 實作
        ├── internal/   ← 私有的 Service, Repository, Entity
        └── events/     ← 領域事件定義
  ```

### 4.4 基礎設施層模組

| 模組 | 職責 |
|------|------|
| `cclemon-auth` | OAuth2 授權伺服器 |
| `cclemon-data` | JPA 實體、Repository、BaseEntity |
| `cclemon-security` | OAuth2 Resource Server 配置 |
| `cclemon-cache` | Redis 快取封裝 |
| `cclemon-kafka` | Kafka Producer 封裝 |
| `cclemon-interface` | 跨模組 DTO 與介面定義 |

### 4.5 工具層模組

| 模組 | 職責 |
|------|------|
| `cclemon-utils` | 純工具類（不依賴 Spring） |
| `cclemon-logging` | AOP 日誌切面 |

### 4.6 獨立應用模組

| 模組 | 職責 |
|------|------|
| `cclemon-config-server` | Spring Cloud Config 伺服器 |
| `cclemon-stream` | Kafka Streams 串流處理 |
| `cclemon-practice` | 練習模組（不納入生產） |

---

## 5. Handler 設計規範

### 5.1 設計原則

| 原則 | 說明 |
|------|------|
| **單一職責** | 一個 Handler 對應一個聚合根 |
| **無狀態** | Handler 實作必須是 Stateless |
| **同步執行** | Handler 方法同步執行，非同步由入口層決定 |
| **事務邊界** | Handler 方法即為事務邊界 |

### 5.2 Handler 介面範例

```java
public interface WeightHandler {
    // Command: 改變狀態
    WeightRecordResult record(RecordWeightCommand command);
    void delete(DeleteWeightCommand command);

    // Query: 查詢
    WeightRecord get(GetWeightQuery query);
    Page<WeightRecord> list(ListWeightsQuery query);
    WeightChartData getChart(GetWeightChartQuery query);
}
```

### 5.3 Command / Query 物件範例

```java
@Value
@Builder
public class RecordWeightCommand {
    @NotNull Long userId;
    @NotNull LocalDate measureDate;
    @NotNull @DecimalMin("20") @DecimalMax("300") BigDecimal weightKg;
    LocalTime measureTime;
    String note;
}
```

---

## 6. 模組依賴規則

### 6.1 依賴方向

```
cclemon-api (契約)
     ^
     |
入口層 (web, job, consumer)
     |
     v
業務層 (health) ← 實作 Handler
     |
     v
基礎設施層 (data, cache, kafka)
```

### 6.2 依賴規則表

| 規則 | 描述 | 違反後果 |
|------|------|---------|
| DEP-01 | 入口層只能依賴 `cclemon-api` | 編譯錯誤 |
| DEP-02 | 業務模組 internal 對外不可見 | ArchUnit 測試失敗 |
| DEP-03 | 業務模組間僅能透過 Event 通訊 | Spring Modulith 驗證失敗 |
| DEP-04 | 基礎設施層不可依賴業務模組 | 編譯錯誤 |
| DEP-05 | 禁止循環依賴 | Gradle 建置失敗 |

---

## 7. 啟動控制機制

### 7.1 模組啟用配置

```yaml
cclemon:
  modules:
    health:
      enabled: true
```

### 7.2 條件式 Bean 註冊

```java
@Component
@ConditionalOnProperty(
    prefix = "cclemon.modules.health",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class WeightHandlerImpl implements WeightHandler {
    // ...
}
```

### 7.3 啟動命令

```bash
# Web 應用（全功能）
java -jar cclemon-web.jar --spring.profiles.active=web,prod

# Web 應用（僅 health 模組）
java -jar cclemon-web.jar --cclemon.modules.health.enabled=true

# Job 應用
java -jar cclemon-job.jar --spring.profiles.active=job,prod
```

---

## 8. 事件持久化設計

為支援事件重播：

```java
@Entity
@Table(name = "domain_events")
public class StoredEvent {
    @Id @GeneratedValue
    private Long id;
    private String eventType;
    @Column(columnDefinition = "JSON")
    private String payload;
    private LocalDateTime occurredAt;
    private boolean published;
}
```

---

## 9. User Story

### US-001: Handler 模式重構 (8 SP)

```
作為 系統架構師，
我想要 將業務邏輯從 Controller 抽離至 Handler 介面，
以便 同一份業務邏輯可被 Web、Job、Consumer 呼叫。
```

### US-002: 模組化啟動控制 (5 SP)

```
作為 維運工程師，
我想要 透過啟動參數控制載入哪些業務模組，
以便 減少不必要的資源消耗。
```

### US-003: 跨模組事件通訊（含持久化）(5 SP)

```
作為 開發人員，
我想要 業務模組間透過事件非同步通訊，並支援重播，
以便 降低耦合度且確保事件不遺失。
```

### US-004: Spring Modulith 架構驗證 (3 SP)

```
作為 開發人員，
我想要 在 CI 流程中自動驗證模組邊界，
以便 及早發現違反架構規則的程式碼。
```

### US-005: Job 入口模組建立 (5 SP)

```
作為 系統管理員，
我想要 獨立部署的排程任務應用，
以便 執行批次作業。
```

### 實作優先順序

| 順序 | Story | 說明 |
|------|-------|------|
| 1 | US-005 | 建立可啟動的 cclemon-job |
| 2 | US-001 | Handler 模式重構 |
| 3 | US-002 | 模組化啟動控制 |
| 4 | US-004 | 架構驗證測試 |
| 5 | US-003 | 事件持久化 |

---

## 10. AI Agent 角色

本專案定義了三種 AI Agent 角色：

| 角色 | 文件 | 職責 |
|------|------|------|
| **系統分析師 (SA)** | [SA_GUIDELINES.md](../roles/SA_GUIDELINES.md) | 需求分析、User Story、API 契約 |
| **開發人員 (Developer)** | [DEVELOPER_GUIDELINES.md](../roles/DEVELOPER_GUIDELINES.md) | 架構實作、程式碼、Git 流程 |
| **品質保證 (QA)** | [QA_GUIDELINES.md](../roles/QA_GUIDELINES.md) | 測試策略、品質標準 |

### 角色協作流程

```
SA (做什麼) → Developer (怎麼做) → QA (做對了嗎)
```

### 通用準則

- **模組邊界**: 尊重模組依賴關係，嚴禁循環依賴
- **分層架構**: 維持 Controller → Handler → Service → Repository 流程
- **任務執行**: 先分析 → 定位檔案 → 規劃變更 → 執行修改

---

## 參考資料

- [Spring Modulith Documentation](https://docs.spring.io/spring-modulith/reference/)
- [Introduction to Spring Modulith | Baeldung](https://www.baeldung.com/spring-modulith)
