import { http, HttpResponse, delay } from 'msw';
import { mockCustomers, mockOrders, getNextId } from './data';
import type { CustomerResult } from '../api/customer/types';

// ── 動態讀取 baseURL，確保和 axios 發出的 URL 一致 ────────
// Vite 在 build time 會把 process.env.VUE_APP_HEALTH_BASE_URL
// 替換成實際值（undefined 或字串）；用 ?? '' 保持相對路徑作為 fallback
const _rawBase = process.env.VUE_APP_HEALTH_BASE_URL as string | undefined;
const API_ORIGIN = (_rawBase && _rawBase !== 'undefined')
  ? _rawBase.replace(/\/$/, '')  // 去掉尾端斜線
  : '';

const BASE = `${API_ORIGIN}/api/v1/customers`;

// ── 模擬網路延遲（ms）────────────────────────────────────
const DELAY = 400;

function calcTier(totalSpend: number): CustomerResult['tierCode'] {
  if (totalSpend >= 10000) return 'GOLD';
  if (totalSpend >= 3000) return 'SILVER';
  return 'STANDARD';
}

export const handlers = [
  // ── GET /api/v1/customers （列表 + 分頁 + 篩選）────────
  http.get(BASE, async ({ request }) => {
    await delay(DELAY);
    const url = new URL(request.url);
    const keyword = url.searchParams.get('keyword')?.toLowerCase() ?? '';
    const tierCode = url.searchParams.get('tierCode') ?? '';
    const page = Number(url.searchParams.get('page') ?? 0);
    const size = Number(url.searchParams.get('size') ?? 20);

    let filtered = [...mockCustomers];

    if (keyword) {
      filtered = filtered.filter(
        (c) =>
          c.name.toLowerCase().includes(keyword) ||
          c.phone.includes(keyword)
      );
    }

    if (tierCode) {
      filtered = filtered.filter((c) => c.tierCode === tierCode);
    }

    const totalElements = filtered.length;
    const totalPages = Math.ceil(totalElements / size) || 1;
    const content = filtered.slice(page * size, page * size + size);

    return HttpResponse.json({
      content,
      page,
      size,
      totalElements,
      totalPages,
    });
  }),

  // ── POST /api/v1/customers （新增會員）─────────────────
  http.post(BASE, async ({ request }) => {
    await delay(DELAY);
    const body = (await request.json()) as {
      name: string;
      phone: string;
      email?: string;
      note?: string;
    };

    // 檢查電話重複
    const exists = mockCustomers.find((c) => c.phone === body.phone);
    if (exists) {
      return HttpResponse.json(
        { message: '電話號碼已存在' },
        { status: 409 }
      );
    }

    const newCustomer: CustomerResult = {
      id: getNextId(),
      name: body.name,
      phone: body.phone,
      email: body.email,
      note: body.note,
      tierCode: 'STANDARD',
      totalSpend: 0,
      visitCount: 0,
      createTime: new Date().toISOString(),
    };

    mockCustomers.unshift(newCustomer);
    return HttpResponse.json(newCustomer, { status: 201 });
  }),

  // ── PUT /api/v1/customers/:id （更新會員）──────────────
  http.put(`${BASE}/:id`, async ({ params, request }) => {
    await delay(DELAY);
    const id = Number(params.id);
    const body = (await request.json()) as {
      name: string;
      phone: string;
      email?: string;
      note?: string;
    };

    const idx = mockCustomers.findIndex((c) => c.id === id);
    if (idx === -1) {
      return HttpResponse.json({ message: '會員不存在' }, { status: 404 });
    }

    // 檢查電話重複（排除自身）
    const phoneConflict = mockCustomers.find(
      (c) => c.phone === body.phone && c.id !== id
    );
    if (phoneConflict) {
      return HttpResponse.json(
        { message: '電話號碼已被其他會員使用' },
        { status: 409 }
      );
    }

    const updated: CustomerResult = {
      ...mockCustomers[idx],
      name: body.name,
      phone: body.phone,
      email: body.email,
      note: body.note,
      tierCode: calcTier(mockCustomers[idx].totalSpend),
    };

    mockCustomers[idx] = updated;
    return HttpResponse.json(updated);
  }),

  // ── GET /api/v1/customers/:id/orders （歷史訂單）───────
  http.get(`${BASE}/:id/orders`, async ({ params }) => {
    await delay(DELAY);
    const id = Number(params.id);
    const orders = mockOrders[id] ?? [];
    return HttpResponse.json(orders);
  }),

  // ── GET /api/v1/customers/:id （單筆）─────────────────
  http.get(`${BASE}/:id`, async ({ params }) => {
    await delay(DELAY);
    const id = Number(params.id);
    const customer = mockCustomers.find((c) => c.id === id);
    if (!customer) {
      return HttpResponse.json({ message: '會員不存在' }, { status: 404 });
    }
    return HttpResponse.json(customer);
  }),
];
