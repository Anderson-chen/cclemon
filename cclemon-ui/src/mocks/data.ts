import type { CustomerResult, CustomerOrderSummary } from '../api/customer/types';

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
