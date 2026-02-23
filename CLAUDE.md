# CLAUDE.md - cclemon 專案指南

## 專案概覽

cclemon 是一個 Spring Boot 多模組後端應用程式（健康管理系統），搭配 Vue 3 + Quasar 前端。

**技術堆疊**: Java 25, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Gradle, MySQL, Redis, Kafka, Vue 3, Quasar 2, TypeScript

---

## 常用指令

### 後端
```bash
# 建置（跳過測試）
./gradlew clean build -x test

# 建置單一模組
./gradlew :cclemon-web:build -x test

# 執行測試
./gradlew test

# 執行單一模組測試
./gradlew :cclemon-web:test
./gradlew :cclemon-health:test
```

### 前端 (cclemon-ui/)
```bash
cd cclemon-ui

# 本地開發（port 5173）
npm run dev

# 建置
npm run build
npm run build:dev    # dev 環境
npm run build:sit    # SIT 環境
npm run build:prod   # 正式環境

# Lint
npm run lint
```

### Docker
```bash
# 建立 Docker 網路（首次）
docker network create cclemon-network
```

---

## 模組架構

```
入口層 (Entry Points)       → cclemon-web (REST, port 8888), cclemon-job (排程), cclemon-consumer (Kafka)
契約層 (API Contracts)      → cclemon-api (Handler 介面 + DTO，無 Spring 依賴)
業務層 (Business Logic)     → cclemon-health (Handler 實作 + Service + Entity + Repository)
基礎設施層 (Infrastructure)  → cclemon-data, cclemon-cache, cclemon-kafka, cclemon-security
工具層 (Utilities)          → cclemon-utils, cclemon-logging
獨立應用                     → cclemon-auth (OAuth2, port 9000), cclemon-config-server, cclemon-stream
前端                        → cclemon-ui (Vue 3 + Quasar + TypeScript, port 5173)
```

### 依賴規則
- 入口層只依賴 `cclemon-api`（`runtimeOnly` 依賴業務層）
- 業務模組 internal 對外不可見
- 業務模組間僅透過 Event 通訊
- 基礎設施層不可依賴業務模組
- 禁止循環依賴

---

## Handler 模式（核心架構模式）

請求流程: `Controller → Handler (interface) → Service → Repository`

- **Handler 介面**: 定義在 `cclemon-api/src/main/java/org/cclemon/api/`
- **Handler 實作**: 放在 `cclemon-health/src/main/java/org/cclemon/health/api/`
- **Service**: 放在 `cclemon-health/src/main/java/org/cclemon/health/internal/service/`
- **Controller**: 放在 `cclemon-web/src/main/java/org/cclemon/controller/`

### DTO 結構 (cclemon-api)
```
org.cclemon.api/
├── WeightHandler.java          ← Handler 介面
└── vo/
    ├── WeightRecordResult.java ← Result DTO (@Value @Builder)
    ├── PagedResult.java        ← 通用分頁結果
    ├── command/
    │   ├── RecordWeightCommand.java    ← Command DTO (@Builder @AllArgsConstructor @NoArgsConstructor @Getter)
    │   └── DeleteWeightCommand.java
    └── query/
        ├── ListWeightsQuery.java       ← Query DTO
        └── GetWeightChartQuery.java
```

### 設計原則
- 一個 Handler 對應一個聚合根
- Handler 無狀態、同步執行
- Handler 方法即為事務邊界
- Controller 不含業務邏輯，只做協議轉換

---

## Entity 模式

所有 Entity 繼承 `BaseEntity`（`cclemon-data`），自動提供：
- `id` (Long, auto-generated)
- `version` (Integer, @Version)
- `createUserId`, `createTime` (審計欄位)
- `lastModifiedUserId`, `lastModifiedTime` (審計欄位)
- `deleted` (Boolean, 軟刪除)

Entity 放在 `cclemon-health/src/main/java/org/cclemon/entity/`

### Lombok 用法
- Entity: `@Data`, `@EqualsAndHashCode(callSuper = true)`
- Service: `@RequiredArgsConstructor`
- Command/Query DTO: `@Builder @AllArgsConstructor @NoArgsConstructor @Getter`
- Result DTO: `@Value @Builder`

---

## 測試規範

### Spring Boot 4.x 重要變更
- **使用** `@MockitoBean`（不是 `@MockBean`）
- **Import**: `org.springframework.test.context.bean.override.mockito.MockitoBean`
- **禁止**: `org.springframework.boot.test.mock.mockito.MockBean`（已移除）

### 整合測試模式
```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class XxxControllerIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @MockitoBean private KafkaProducerService kafkaProducerService;
}
```

### 單元測試模式
```java
@ExtendWith(MockitoExtension.class)
class XxxServiceTest {
    @Mock private XxxRepository repository;
    @InjectMocks private XxxService service;
}
```

### 命名慣例
- 方法名: `should[Expected]_when[Condition]`
- 結構: AAA (Arrange, Act, Assert)
- 斷言: AssertJ (`assertThat(...)`)

---

## 前端模式 (cclemon-ui)

### 技術: Vue 3 + Quasar 2 + TypeScript + Pinia + Axios

### API 模組結構
```
src/api/{module}/
├── types.ts        ← TypeScript 介面定義
└── {module}.ts     ← API 函式（使用 healthApi axios 實例）
```

### 頁面模式
- Vue 3 Composition API + `<script setup lang="ts">`
- Quasar 元件 (`q-table`, `q-form`, `q-input`, `q-btn`, `q-dialog`, `q-pagination`)
- 路由使用 lazy loading: `component: () => import('pages/XxxPage.vue')`

### 導航
- 左側抽屜在 `src/layouts/MainLayout.vue` 的 `linksList` 陣列中新增項目
- 路由在 `src/router/routes.ts` 中新增（放在 catch-all 路由之前）

---

## REST API 慣例

- 基本路徑: `/api/v1/{resource}`
- CRUD 端點:
  - `POST /api/v1/{resource}` — 新增
  - `GET /api/v1/{resource}` — 列表查詢（分頁: `?page=0&size=20`）
  - `GET /api/v1/{resource}/{id}` — 單筆查詢
  - `PUT /api/v1/{resource}/{id}` — 更新
  - `DELETE /api/v1/{resource}/{id}` — 刪除（軟刪除）

---

## AI Agent 角色

本專案定義三種 AI Agent（見 `.claude/agents/`）：

| 角色 | 職責 |
|------|------|
| **SA（系統分析師）** | 需求分析、User Story、API 契約設計 |
| **Developer（開發人員）** | 架構實作、程式碼、Git 流程 |
| **QA（品質保證）** | 測試策略、品質標準、測試撰寫 |

協作流程: `SA (做什麼) → Developer (怎麼做) → QA (做對了嗎)`

---

## 關鍵配置

| 服務 | Port | 資料庫 |
|------|------|--------|
| cclemon-web / cclemon-health | 8888 | mysql://localhost:3306/cclemon-health |
| cclemon-auth | 9000 | mysql://localhost:3306/cclemon-auth |
| cclemon-ui (dev) | 5173 | — |
| Redis | 6379 | — |
| Kafka | 9093, 9095, 9097 | — |
