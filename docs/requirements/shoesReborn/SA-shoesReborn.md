# ShoesReborn 洗鞋店管理系統 - 系統分析文件 (SA)

**版本**: v1.0.0
**日期**: 2026-03-06
**狀態**: 草稿 (Draft)
**關聯 PRD**: [PRD-shoesReborn.md](./PRD-shoesReborn.md)

---

## 目錄

1. [系統架構概述](#1-系統架構概述)
2. [API 契約設計](#2-api-契約設計)
3. [資料模型](#3-資料模型)

---

## 1. 系統架構概述

### 1.1 整合現有平台

ShoesReborn 作為 cclemon 平台的新業務模組，遵循現有架構規範：

| 層級 | 位置 | 說明 |
|------|------|------|
| API 契約 | `cclemon-api` | Handler 介面 + DTO |
| 業務邏輯 | `cclemon-health`（或新模組） | Handler 實作 + Service + Entity |
| REST 入口 | `cclemon-web` | Controller |
| 認證授權 | `cclemon-auth`（port 9000） | OAuth2 + JWT，RBAC 老闆 / 員工角色 |
| 前端 | `cclemon-ui`（port 5173） | Vue 3 + Quasar |
| 資料庫 | MySQL | 獨立 schema `cclemon-shoes` |

### 1.2 模組對應

| 業務模組 | Handler | Controller 路徑 |
|---------|---------|----------------|
| 訂單管理 | `OrderHandler` | `/api/v1/orders` |
| 鞋物位置 | `LocationHandler` | `/api/v1/locations` |
| 急件管理 | （附屬於 OrderHandler） | `/api/v1/orders/urgent` |
| 會員管理 | `CustomerHandler` | `/api/v1/customers` |
| 財務報表 | `ReportHandler` | `/api/v1/reports` |
| 系統設定 | `SettingsHandler` | `/api/v1/settings` |

---

## 2. API 契約設計

遵循 cclemon 平台 REST API 慣例：基本路徑 `/api/v1/{resource}`。

### 2.1 訂單管理 API

| Method | URL | 說明 | 角色 |
|--------|-----|------|------|
| `POST` | `/api/v1/orders` | 建立訂單 | 老闆、員工 |
| `GET` | `/api/v1/orders` | 查詢訂單列表（分頁、篩選） | 老闆、員工 |
| `GET` | `/api/v1/orders/{id}` | 查詢單筆訂單 | 老闆、員工 |
| `PUT` | `/api/v1/orders/{id}` | 更新訂單資訊 | 老闆、員工 |
| `PATCH` | `/api/v1/orders/{id}/status` | 更新訂單狀態 | 老闆、員工 |
| `DELETE` | `/api/v1/orders/{id}` | 取消 / 軟刪除訂單 | 老闆 |

**GET /api/v1/orders 查詢參數**

| 參數 | 型別 | 說明 |
|------|------|------|
| `page` | Integer | 頁碼（從 0 開始） |
| `size` | Integer | 每頁筆數（預設 20） |
| `status` | String | 狀態篩選（如 `PENDING`） |
| `isUrgent` | Boolean | 急件篩選 |
| `keyword` | String | 顧客姓名 / 電話 / 訂單編號 |
| `dateFrom` | Date | 建立日期起（yyyy-MM-dd） |
| `dateTo` | Date | 建立日期迄 |

**POST /api/v1/orders Request Body**

```json
{
  "customerId": 1001,
  "items": [
    {
      "serviceCode": "SVC-WASH",
      "itemDescription": "Nike Air Max 黑色",
      "quantity": 1,
      "unitPrice": 500
    }
  ],
  "isUrgent": true,
  "urgentDeadline": "2026-03-05",
  "estimatedPickupDate": "2026-03-05",
  "staffNote": "右鞋底有黃漬，需特別注意",
  "locations": [
    { "locationCode": "A-3-2", "note": "左鞋" },
    { "locationCode": "A-3-3", "note": "右鞋" }
  ]
}
```

**POST /api/v1/orders Response (201 Created)**

```json
{
  "success": true,
  "data": {
    "orderId": 5001,
    "orderNo": "SR-20260304-0001",
    "status": "PENDING",
    "totalAmount": 750,
    "urgentFee": 250,
    "locations": [
      { "locationCode": "A-3-2", "note": "左鞋" },
      { "locationCode": "A-3-3", "note": "右鞋" }
    ],
    "createdAt": "2026-03-04T10:30:00"
  }
}
```

---

### 2.2 鞋物位置 API

| Method | URL | 說明 | 角色 |
|--------|-----|------|------|
| `PUT` | `/api/v1/orders/{id}/location` | 更新訂單物品存放位置（支援多位置） | 老闆、員工 |
| `GET` | `/api/v1/locations` | 查詢所有位置標籤 | 老闆、員工 |
| `POST` | `/api/v1/locations` | 新增位置標籤 | 老闆 |
| `DELETE` | `/api/v1/locations/{code}` | 停用位置標籤 | 老闆 |

**PUT /api/v1/orders/{id}/location Request Body**

```json
{
  "locations": [
    { "locationCode": "A-3-2", "note": "左鞋" },
    { "locationCode": "A-3-3", "note": "右鞋" }
  ]
}
```

---

### 2.3 急件管理 API

| Method | URL | 說明 | 角色 |
|--------|-----|------|------|
| `GET` | `/api/v1/orders/urgent` | 急件列表（依截止日排序） | 老闆、員工 |
| `GET` | `/api/v1/orders/urgent/alerts` | 即將超時警示列表 | 老闆、員工 |

---

### 2.4 會員管理 API

| Method | URL | 說明 | 角色 |
|--------|-----|------|------|
| `POST` | `/api/v1/customers` | 建立會員 | 老闆、員工 |
| `GET` | `/api/v1/customers` | 查詢會員列表 | 老闆、員工 |
| `GET` | `/api/v1/customers/{id}` | 查詢單筆會員資料 | 老闆、員工 |
| `PUT` | `/api/v1/customers/{id}` | 更新會員資料 | 老闆 |
| `GET` | `/api/v1/customers/{id}/orders` | 查詢會員歷史訂單 | 老闆、員工 |
| `GET` | `/api/v1/customers/search` | 依電話 / 姓名搜尋會員 | 老闆、員工 |

---

### 2.5 財務報表 API

| Method | URL | 說明 | 角色 |
|--------|-----|------|------|
| `GET` | `/api/v1/reports/daily` | 每日營收摘要 | 老闆 |
| `GET` | `/api/v1/reports/monthly` | 月度報表 | 老闆 |
| `GET` | `/api/v1/reports/services` | 服務項目收益分析 | 老闆 |
| `GET` | `/api/v1/reports/export` | 匯出報表 CSV | 老闆 |

---

### 2.6 系統設定 API

**服務項目**

| Method | URL | 說明 | 角色 |
|--------|-----|------|------|
| `GET` | `/api/v1/settings/services` | 查詢服務項目清單 | 老闆、員工 |
| `POST` | `/api/v1/settings/services` | 新增服務項目 | 老闆 |
| `PUT` | `/api/v1/settings/services/{code}` | 更新服務項目 | 老闆 |
| `PUT` | `/api/v1/settings/urgent-fee-rate` | 設定系統全域急件費率 | 老闆 |

**PUT /api/v1/settings/services/{code} 可更新欄位**

| 欄位 | 型別 | 說明 |
|------|------|------|
| name | String | 服務名稱 |
| defaultPrice | Decimal | 預設售價 |
| urgentFeeRate | Decimal \| null | 該服務專屬急件費率；傳 null 表示沿用全域設定 |
| isActive | Boolean | 是否啟用 |

**員工管理**

| Method | URL | 說明 | 角色 |
|--------|-----|------|------|
| `GET` | `/api/v1/settings/staff` | 查詢員工列表 | 老闆 |
| `POST` | `/api/v1/settings/staff` | 建立員工帳號 | 老闆 |
| `PUT` | `/api/v1/settings/staff/{id}` | 更新員工資料 | 老闆 |
| `DELETE` | `/api/v1/settings/staff/{id}` | 停用員工帳號（軟刪除） | 老闆 |

### 2.7 錯誤回應格式（RFC 7807）

```json
{
  "type": "/errors/order-not-found",
  "title": "Order Not Found",
  "status": 404,
  "detail": "訂單 SR-20260304-0001 不存在",
  "instance": "/api/v1/orders/SR-20260304-0001"
}
```

---

## 3. 資料模型

### 3.1 實體關係

```
Customer ──(1:N)──> Order ──(1:N)──> OrderItem ──(N:1)──> ServiceType
                      │
                      └──(1:N)──> OrderLocation ──(N:1)──> Location

Customer ──(N:1)──> MemberTier
```

> **設計說明**：一筆訂單支援多個存放位置（如左鞋與右鞋存於不同格位），
> 透過 `OrderLocation` 中間表關聯，取代單一 `locationCode` 欄位。

---

### 3.2 Customer（顧客 / 會員）

| 欄位名稱 | 型別 | 必填 | 說明 |
|---------|------|:----:|------|
| id | Long | Y | 主鍵（繼承 BaseEntity） |
| name | VARCHAR(50) | Y | 顧客姓名 |
| phone | VARCHAR(20) | Y | 手機號碼（唯一值） |
| email | VARCHAR(100) | N | 電子郵件 |
| tierCode | VARCHAR(20) | Y | 會員等級代碼（FK to MemberTier） |
| totalSpend | DECIMAL(10,2) | Y | 累積消費金額（預設 0） |
| visitCount | Integer | Y | 到店次數（預設 0） |
| note | TEXT | N | 特殊偏好 / 注意事項 |
| deleted | Boolean | Y | 軟刪除旗標（繼承 BaseEntity） |
| createTime | DATETIME | Y | 建立時間（繼承 BaseEntity） |

---

### 3.3 Order（訂單）

| 欄位名稱 | 型別 | 必填 | 說明 |
|---------|------|:----:|------|
| id | Long | Y | 主鍵 |
| orderNo | VARCHAR(30) | Y | 訂單編號 `SR-YYYYMMDD-XXXX`（唯一值） |
| customerId | Long | Y | 顧客 ID（FK to Customer） |
| status | VARCHAR(20) | Y | 訂單狀態 |
| isUrgent | Boolean | Y | 是否為急件（預設 false） |
| urgentDeadline | DATE | N | 急件截止日（isUrgent = true 時必填） |
| estimatedPickupDate | DATE | Y | 預計取件日 |
| actualPickupDate | DATE | N | 實際取件日 |
| totalAmount | DECIMAL(10,2) | Y | 訂單總金額（含急件費） |
| urgentFee | DECIMAL(10,2) | N | 急件費用（預設 0） |
| staffNote | TEXT | N | 員工備註 |
| createUserId | Long | Y | 建立員工 ID（繼承 BaseEntity） |

---

### 3.4 OrderItem（訂單明細）

| 欄位名稱 | 型別 | 必填 | 說明 |
|---------|------|:----:|------|
| id | Long | Y | 主鍵 |
| orderId | Long | Y | 訂單 ID（FK to Order） |
| serviceCode | VARCHAR(20) | Y | 服務代碼（FK to ServiceType） |
| itemDescription | VARCHAR(200) | Y | 物品描述（品牌 / 款式 / 顏色） |
| quantity | Integer | Y | 數量（最小值 1） |
| unitPrice | DECIMAL(10,2) | Y | 單價 |
| subTotal | DECIMAL(10,2) | Y | 小計（quantity × unitPrice） |

---

### 3.5 ServiceType（服務項目）

| 欄位名稱 | 型別 | 必填 | 說明 |
|---------|------|:----:|------|
| code | VARCHAR(20) | Y | 服務代碼（主鍵，如 SVC-WASH） |
| name | VARCHAR(50) | Y | 服務名稱 |
| defaultPrice | DECIMAL(10,2) | Y | 預設售價 |
| urgentFeeRate | DECIMAL(5,4) | N | 該服務專屬急件費率（如 0.5 = 50%）；null 時沿用系統全域設定 |
| isActive | Boolean | Y | 是否啟用（預設 true） |

---

### 3.6 Location（存放位置）

| 欄位名稱 | 型別 | 必填 | 說明 |
|---------|------|:----:|------|
| code | VARCHAR(20) | Y | 位置代碼（主鍵，如 A-3-2） |
| areaCode | VARCHAR(10) | Y | 區域代碼 |
| shelfNo | VARCHAR(10) | Y | 層架號碼 |
| slotNo | VARCHAR(10) | Y | 格位號碼 |
| displayName | VARCHAR(50) | Y | 顯示名稱（如 A 區第 3 層架第 2 格） |
| isActive | Boolean | Y | 是否啟用 |

---

### 3.7 OrderLocation（訂單存放位置）

> 一筆訂單可對應多個存放位置。

| 欄位名稱 | 型別 | 必填 | 說明 |
|---------|------|:----:|------|
| id | Long | Y | 主鍵（繼承 BaseEntity） |
| orderId | Long | Y | 訂單 ID（FK to Order） |
| locationCode | VARCHAR(20) | Y | 位置代碼（FK to Location） |
| note | VARCHAR(200) | N | 位置備註（如「左鞋」、「右鞋」） |

---

### 3.8 MemberTier（會員等級）

| 欄位名稱 | 型別 | 必填 | 說明 |
|---------|------|:----:|------|
| code | VARCHAR(20) | Y | 等級代碼（主鍵，如 GOLD） |
| name | VARCHAR(30) | Y | 等級名稱 |
| spendThreshold | DECIMAL(10,2) | Y | 升級所需累積消費門檻 |
| benefits | TEXT | N | 等級說明 / 優惠描述 |