```mermaid
flowchart TD
    subgraph ADAPTERS["Adapter 層（入口適配器）"]
        direction LR
        WEB["cclemon-web
        REST API
        port 8888"]
        JOB["cclemon-job
        排程任務"]
        CONSUMER["cclemon-consumer
        Kafka 消費"]
        LAMBDA["cclemon-lambda
        AWS Lambda
        （未來）"]
    end

    subgraph PORT["MODULE: cclemon-api  ← Port 層"]
        HANDLER["WeightHandler  &lt;interface&gt;
        ───────────────────────────────────────────────────────
        record(RecordWeightCommand)   → WeightRecordResult
        delete(DeleteWeightCommand)   → void
        list(ListWeightsQuery)        → PagedResult&lt;WeightRecordResult&gt;
        getChart(GetWeightChartQuery) → WeightChartData
        ───────────────────────────────────────────────────────
        零 Spring 依賴，純 Java 介面
        所有入口層只依賴此介面，不依賴實作"]
    end

    subgraph DOMAIN["MODULE: cclemon-health  ← Domain 層"]
        IMPL["WeightHandlerImpl  &lt;Application Service&gt;
        ───────────────────────────────────────────────────────
        @Transactional / @Transactional(readOnly=true)
        協調 Service 呼叫
        轉換 Command/Query ↔ Entity ↔ Result VO"]

        SERVICE["WeightService  &lt;Domain Service, internal&gt;
        ───────────────────────────────────────────────────────
        upsertManualWeight()  —  新增或更新體重記錄
        listWeights()         —  分頁 / 日期區間查詢
        softDelete()          —  軟刪除 (deleted = true)
        hasRecord()           —  檢查當日是否已有紀錄"]

        ENTITY["UserWeightLog  &lt;Entity / Aggregate Root&gt;
        ───────────────────────────────────────────────────────
        id, user, measureDate, measureTime, weightKg, source, deleted
        unique: (user_id, measure_date, source)"]
    end

    DB[("MySQL  (port 3306)
        table: user_weight_logs")]

    WEB      -- "implementation project(:cclemon-api)\nruntimeOnly project(:cclemon-health)" --> PORT
    JOB      -- "implementation project(:cclemon-api)\nruntimeOnly project(:cclemon-health)" --> PORT
    CONSUMER -- "implementation project(:cclemon-kafka)"                                      --> PORT
    LAMBDA   -- "implementation project(:cclemon-api)\nruntimeOnly project(:cclemon-health)" --> PORT

    PORT   -- "runtimeOnly implementation" --> DOMAIN
    DOMAIN -- "JPA / Hibernate"            --> DB
```

---

## DDD 核心用意

> 讓程式碼說「業務的語言」，不是「技術的語言」。  
> 業務邏輯集中，技術細節（HTTP / DB / Kafka / Lambda）向外推，邊界清楚。

---

## 各入口方案

| 入口 | 狀態 | 作法 |
|------|------|------|
| REST API (cclemon-web) | ✅ 已支援 | Controller 注入 WeightHandler 介面 |
| 排程 Job (cclemon-job) | ✅ 已支援 | Job 直接注入 WeightHandler，不需 HTTP |
| Kafka Consumer | ✅ 已支援 | 實作 KafkaMessageHandler，宣告 topic |
| AWS Lambda | 🔧 需新增模組 | Spring Cloud Function Adapter，Handler 介面不動 |
| Cloudflare Workers | ⚠️ 不直接支援 Java | Workers 透過 HTTP 呼叫 cclemon-web REST API |

---

## 設計原則

```
Handler 介面 (cclemon-api)  =  Port
  → 新增入口 = 新增 Adapter 模組，業務層完全不動

runtimeOnly 依賴策略
  implementation project(":cclemon-api")    // 編譯期只看介面
  runtimeOnly   project(":cclemon-health")  // 執行期注入實作
```
