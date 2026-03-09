import type { CustomerResult, CustomerOrderSummary } from '../api/customer/types';
import type { OrderResult } from '../api/order/types';
import type { ServiceTypeResult } from '../api/service/types';

// ── 模擬會員資料 ─────────────────────────────────────────
export const mockCustomers: CustomerResult[] = [
  {
    id: 1,
    name: '陳雅婷',
    phone: '0912345678',
    email: 'yating.chen@example.com',
    tierCode: 'GOLD',
    totalSpend: 15800,
    visitCount: 23,
    note: '偏好快乾型處理，對羊毛材質過敏',
    createTime: '2023-03-10T08:30:00',
  },
  {
    id: 2,
    name: '林建宏',
    phone: '0923456789',
    email: 'chien.lin@example.com',
    tierCode: 'GOLD',
    totalSpend: 12400,
    visitCount: 18,
    createTime: '2023-05-22T10:15:00',
  },
  {
    id: 3,
    name: '王美玲',
    phone: '0934567890',
    tierCode: 'SILVER',
    totalSpend: 6200,
    visitCount: 9,
    note: '請輕柔處理皮革製品',
    createTime: '2023-08-14T14:00:00',
  },
  {
    id: 4,
    name: '張志明',
    phone: '0945678901',
    email: 'chiming.chang@example.com',
    tierCode: 'SILVER',
    totalSpend: 4500,
    visitCount: 7,
    createTime: '2023-09-01T09:00:00',
  },
  {
    id: 5,
    name: '李淑芬',
    phone: '0956789012',
    tierCode: 'SILVER',
    totalSpend: 3800,
    visitCount: 5,
    note: '通常週末來取件',
    createTime: '2023-11-05T11:30:00',
  },
  {
    id: 6,
    name: '吳俊達',
    phone: '0967890123',
    email: 'junda.wu@example.com',
    tierCode: 'STANDARD',
    totalSpend: 1200,
    visitCount: 2,
    createTime: '2024-01-18T16:45:00',
  },
  {
    id: 7,
    name: '周雅文',
    phone: '0978901234',
    tierCode: 'STANDARD',
    totalSpend: 800,
    visitCount: 1,
    createTime: '2024-02-20T13:00:00',
  },
  {
    id: 8,
    name: '劉宗翰',
    phone: '0989012345',
    email: 'zonghan.liu@example.com',
    tierCode: 'STANDARD',
    totalSpend: 2500,
    visitCount: 3,
    note: '皮革靴子需特別護理',
    createTime: '2024-03-05T10:00:00',
  },
];

// ── 模擬訂單資料 ─────────────────────────────────────────
export const mockOrders: Record<number, CustomerOrderSummary[]> = {
  1: [
    {
      id: 101,
      orderNo: 'ORD-2024-0101',
      status: 'PICKED_UP',
      isUrgent: false,
      totalAmount: 1200,
      estimatedPickupDate: '2024-01-15',
      actualPickupDate: '2024-01-15',
      createTime: '2024-01-10T09:00:00',
    },
    {
      id: 102,
      orderNo: 'ORD-2024-0215',
      status: 'PICKED_UP',
      isUrgent: true,
      totalAmount: 2500,
      estimatedPickupDate: '2024-02-18',
      actualPickupDate: '2024-02-17',
      createTime: '2024-02-15T14:30:00',
    },
    {
      id: 103,
      orderNo: 'ORD-2024-0312',
      status: 'READY',
      isUrgent: false,
      totalAmount: 980,
      estimatedPickupDate: '2024-03-15',
      createTime: '2024-03-12T10:00:00',
    },
  ],
  2: [
    {
      id: 201,
      orderNo: 'ORD-2024-0120',
      status: 'PICKED_UP',
      isUrgent: false,
      totalAmount: 1500,
      estimatedPickupDate: '2024-01-25',
      actualPickupDate: '2024-01-25',
      createTime: '2024-01-20T11:00:00',
    },
    {
      id: 202,
      orderNo: 'ORD-2024-0301',
      status: 'IN_PROGRESS',
      isUrgent: true,
      totalAmount: 3200,
      estimatedPickupDate: '2024-03-05',
      createTime: '2024-03-01T09:30:00',
    },
  ],
  3: [
    {
      id: 301,
      orderNo: 'ORD-2024-0205',
      status: 'PICKED_UP',
      isUrgent: false,
      totalAmount: 650,
      estimatedPickupDate: '2024-02-08',
      actualPickupDate: '2024-02-09',
      createTime: '2024-02-05T15:00:00',
    },
    {
      id: 302,
      orderNo: 'ORD-2024-0310',
      status: 'PENDING',
      isUrgent: false,
      totalAmount: 880,
      estimatedPickupDate: '2024-03-14',
      createTime: '2024-03-10T10:00:00',
    },
  ],
  4: [
    {
      id: 401,
      orderNo: 'ORD-2024-0228',
      status: 'IN_PROGRESS',
      isUrgent: false,
      totalAmount: 1100,
      estimatedPickupDate: '2024-03-05',
      createTime: '2024-02-28T13:00:00',
    },
  ],
  5: [
    {
      id: 501,
      orderNo: 'ORD-2024-0308',
      status: 'READY',
      isUrgent: true,
      totalAmount: 760,
      estimatedPickupDate: '2024-03-13',
      createTime: '2024-03-08T09:00:00',
    },
  ],
  6: [
    {
      id: 601,
      orderNo: 'ORD-2024-0225',
      status: 'PICKED_UP',
      isUrgent: false,
      totalAmount: 1200,
      estimatedPickupDate: '2024-02-28',
      actualPickupDate: '2024-02-28',
      createTime: '2024-02-25T14:00:00',
    },
  ],
  7: [
    {
      id: 701,
      orderNo: 'ORD-2024-0220',
      status: 'PICKED_UP',
      isUrgent: false,
      totalAmount: 800,
      estimatedPickupDate: '2024-02-23',
      actualPickupDate: '2024-02-24',
      createTime: '2024-02-20T13:00:00',
    },
  ],
  8: [
    {
      id: 801,
      orderNo: 'ORD-2024-0115',
      status: 'PICKED_UP',
      isUrgent: false,
      totalAmount: 900,
      estimatedPickupDate: '2024-01-18',
      actualPickupDate: '2024-01-18',
      createTime: '2024-01-15T10:00:00',
    },
    {
      id: 802,
      orderNo: 'ORD-2024-0307',
      status: 'CANCELLED',
      isUrgent: false,
      totalAmount: 500,
      estimatedPickupDate: '2024-03-10',
      createTime: '2024-03-07T11:00:00',
    },
    {
      id: 803,
      orderNo: 'ORD-2024-0313',
      status: 'IN_PROGRESS',
      isUrgent: false,
      totalAmount: 1100,
      estimatedPickupDate: '2024-03-18',
      createTime: '2024-03-13T09:30:00',
    },
  ],
};

// ── 模擬訂單列表（開單管理）──────────────────────────────────
export const mockOrderList: OrderResult[] = [
  {
    id: 1001,
    orderNo: 'SR-20260301-0001',
    customerId: 1,
    customerName: '陳雅婷',
    customerPhone: '0912345678',
    status: 'READY',
    isUrgent: false,
    items: [
      { id: 1, serviceCode: 'SVC-WASH', serviceName: '洗鞋', quantity: 2, unitPrice: 350, subtotal: 700 },
      { id: 2, serviceCode: 'SVC-COATING', serviceName: '鍍膜', quantity: 1, unitPrice: 500, subtotal: 500 },
    ],
    storageLocations: ['A-2-1'],
    totalAmount: 1200,
    urgentFee: 0,
    estimatedPickupDate: '2026-03-10',
    note: 'Nike Air Max，請輕柔處理',
    createTime: '2026-03-01T09:00:00',
  },
  {
    id: 1002,
    orderNo: 'SR-20260302-0001',
    customerId: 2,
    customerName: '林建宏',
    customerPhone: '0923456789',
    status: 'IN_PROGRESS',
    isUrgent: true,
    urgentDeadline: '2026-03-08',
    items: [
      { id: 3, serviceCode: 'SVC-WASH', serviceName: '洗鞋', quantity: 1, unitPrice: 350, subtotal: 350 },
      { id: 4, serviceCode: 'SVC-RECOLOR', serviceName: '補色', quantity: 1, unitPrice: 800, subtotal: 800 },
    ],
    storageLocations: ['B-1-3'],
    totalAmount: 1725,
    urgentFee: 575,
    estimatedPickupDate: '2026-03-08',
    note: 'Jordan 1，補色部位：前後鞋頭',
    createTime: '2026-03-02T14:30:00',
  },
  {
    id: 1003,
    orderNo: 'SR-20260302-0002',
    customerId: 3,
    customerName: '王美玲',
    customerPhone: '0934567890',
    status: 'PENDING',
    isUrgent: false,
    items: [
      { id: 5, serviceCode: 'SVC-BAG', serviceName: '洗包', quantity: 1, unitPrice: 600, subtotal: 600 },
    ],
    storageLocations: [],
    totalAmount: 600,
    urgentFee: 0,
    estimatedPickupDate: '2026-03-12',
    note: 'LV 帆布包，請特別注意五金配件',
    createTime: '2026-03-02T16:00:00',
  },
  {
    id: 1004,
    orderNo: 'SR-20260303-0001',
    customerId: 4,
    customerName: '張志明',
    customerPhone: '0945678901',
    status: 'PENDING',
    isUrgent: true,
    urgentDeadline: '2026-03-09',
    items: [
      { id: 6, serviceCode: 'SVC-WASH', serviceName: '洗鞋', quantity: 3, unitPrice: 350, subtotal: 1050 },
    ],
    storageLocations: ['C-3-2'],
    totalAmount: 1575,
    urgentFee: 525,
    estimatedPickupDate: '2026-03-09',
    createTime: '2026-03-03T10:00:00',
  },
  {
    id: 1005,
    orderNo: 'SR-20260303-0002',
    customerId: 5,
    customerName: '李淑芬',
    customerPhone: '0956789012',
    status: 'PICKED_UP',
    isUrgent: false,
    items: [
      { id: 7, serviceCode: 'SVC-WASH', serviceName: '洗鞋', quantity: 2, unitPrice: 350, subtotal: 700 },
    ],
    storageLocations: ['A-1-1'],
    totalAmount: 700,
    urgentFee: 0,
    estimatedPickupDate: '2026-03-06',
    actualPickupDate: '2026-03-06',
    createTime: '2026-03-03T11:30:00',
  },
  {
    id: 1006,
    orderNo: 'SR-20260304-0001',
    customerId: 6,
    customerName: '吳俊達',
    customerPhone: '0967890123',
    status: 'IN_PROGRESS',
    isUrgent: false,
    items: [
      { id: 8, serviceCode: 'SVC-COATING', serviceName: '鍍膜', quantity: 2, unitPrice: 500, subtotal: 1000 },
    ],
    storageLocations: ['D-2-1'],
    totalAmount: 1000,
    urgentFee: 0,
    estimatedPickupDate: '2026-03-11',
    createTime: '2026-03-04T09:00:00',
  },
  {
    id: 1007,
    orderNo: 'SR-20260305-0001',
    customerId: 8,
    customerName: '劉宗翰',
    customerPhone: '0989012345',
    status: 'CANCELLED',
    isUrgent: false,
    items: [
      { id: 9, serviceCode: 'SVC-RECOLOR', serviceName: '補色', quantity: 1, unitPrice: 800, subtotal: 800 },
    ],
    storageLocations: [],
    totalAmount: 800,
    urgentFee: 0,
    estimatedPickupDate: '2026-03-12',
    note: '顧客臨時取消',
    createTime: '2026-03-05T13:00:00',
  },
];

let nextOrderId = 2000;

export function getNextOrderId() {
  return ++nextOrderId;
}

// ── 自動計算會員等級 ──────────────────────────────────────
function calcTier(totalSpend: number): CustomerResult['tierCode'] {
  if (totalSpend >= 10000) return 'GOLD';
  if (totalSpend >= 3000) return 'SILVER';
  return 'STANDARD';
}

let nextId = 100;

export function getNextId() {
  return ++nextId;
}

export function calcTierCode(totalSpend: number) {
  return calcTier(totalSpend);
}

// ── 模擬服務項目資料 ──────────────────────────────────────
export const mockServices: ServiceTypeResult[] = [
  { code: 'SVC-WASH',    name: '洗鞋',  defaultPrice: 350, urgentFeeRate: null, isActive: true },
  { code: 'SVC-COATING', name: '鍍膜',  defaultPrice: 500, urgentFeeRate: null, isActive: true },
  { code: 'SVC-BAG',     name: '洗包',  defaultPrice: 600, urgentFeeRate: null, isActive: true },
  { code: 'SVC-RECOLOR', name: '補色',  defaultPrice: 800, urgentFeeRate: null, isActive: true },
];
