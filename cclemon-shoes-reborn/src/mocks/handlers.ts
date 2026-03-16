import { http, HttpResponse, delay } from 'msw';
import { mockCustomers, mockOrders, getNextId, mockOrderList, getNextOrderId, mockServices } from './data';
import type { CustomerResult } from '../api/customer/types';
import type { OrderResult } from '../api/order/types';
import type { ServiceTypeResult } from '../api/service/types';

// ── 動態讀取 baseURL，確保和 axios 發出的 URL 一致 ────────
// Vite 在 build time 會把 process.env.VUE_APP_HEALTH_BASE_URL
// 替換成實際值（undefined 或字串）；用 ?? '' 保持相對路徑作為 fallback
const _rawBase = process.env.VUE_APP_HEALTH_BASE_URL as string | undefined;
const API_ORIGIN = (_rawBase && _rawBase !== 'undefined')
  ? _rawBase.replace(/\/$/, '')  // 去掉尾端斜線
  : '';

const BASE = `${API_ORIGIN}/api/v1/customers`;
const ORDER_BASE = `${API_ORIGIN}/api/v1/orders`;
const SERVICE_SETTINGS_BASE = `${API_ORIGIN}/api/v1/settings/services`;
const URGENT_FEE_RATE_URL = `${API_ORIGIN}/api/v1/settings/urgent-fee-rate`;
const UPLOAD_URL = `${API_ORIGIN}/api/v1/upload`;



function genOrderNo(): string {
  const now = new Date();
  const date = `${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}`;
  const seq = String(mockOrderList.filter((o) => o.orderNo.includes(date)).length + 1).padStart(4, '0');
  return `SR-${date}-${seq}`;
}

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
          (c.phone && c.phone.includes(keyword))
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

  // ── GET /api/v1/customers/search （關鍵字搜尋）─────────
  http.get(`${BASE}/search`, async ({ request }) => {
    await delay(DELAY);
    const url = new URL(request.url);
    const keyword = url.searchParams.get('keyword')?.toLowerCase() ?? '';
    const results = mockCustomers.filter(
      (c) =>
        c.name.toLowerCase().includes(keyword) ||
        (c.phone && c.phone.includes(keyword))
    );
    return HttpResponse.json(results);
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

  // ── GET /api/v1/orders （訂單列表 + 分頁 + 篩選）────────
  http.get(ORDER_BASE, async ({ request }) => {
    await delay(DELAY);
    const url = new URL(request.url);
    const keyword = url.searchParams.get('keyword')?.toLowerCase() ?? '';
    const status = url.searchParams.get('status') ?? '';
    const isUrgentStr = url.searchParams.get('isUrgent');
    const dateFrom = url.searchParams.get('dateFrom') ?? '';
    const dateTo = url.searchParams.get('dateTo') ?? '';
    const page = Number(url.searchParams.get('page') ?? 0);
    const size = Number(url.searchParams.get('size') ?? 20);

    let filtered = [...mockOrderList];

    if (keyword) {
      filtered = filtered.filter(
        (o) =>
          o.orderNo.toLowerCase().includes(keyword) ||
          o.customerName.toLowerCase().includes(keyword) ||
          o.customerPhone.includes(keyword)
      );
    }
    const statuses = url.searchParams.getAll('statuses');
    if (status) {
      filtered = filtered.filter((o) => o.status === status);
    } else if (statuses.length > 0) {
      filtered = filtered.filter((o) => statuses.includes(o.status));
    }
    if (isUrgentStr !== null && isUrgentStr !== '') {
      const isUrgent = isUrgentStr === 'true';
      filtered = filtered.filter((o) => o.isUrgent === isUrgent);
    }
    if (dateFrom) {
      filtered = filtered.filter((o) => o.estimatedPickupDate >= dateFrom);
    }
    if (dateTo) {
      filtered = filtered.filter((o) => o.estimatedPickupDate <= dateTo);
    }

    const sort = url.searchParams.get('sort') ?? 'createTime,desc';
    const [sortField, sortDir] = sort.split(',');

    filtered.sort((a, b) => {
      let diff = 0;
      if (sortField === 'createTime') {
        diff = new Date(a.createTime).getTime() - new Date(b.createTime).getTime();
      } else if (sortField === 'estimatedPickupDate') {
        // 預計取件日可能為空，將空值排在最後
        const timeA = a.estimatedPickupDate ? new Date(a.estimatedPickupDate).getTime() : Infinity;
        const timeB = b.estimatedPickupDate ? new Date(b.estimatedPickupDate).getTime() : Infinity;
        diff = timeA - timeB;
      } else if (sortField === 'totalAmount') {
        diff = a.totalAmount - b.totalAmount;
      }
      return sortDir === 'desc' ? -diff : diff;
    });

    const totalElements = filtered.length;
    const totalPages = Math.ceil(totalElements / size) || 1;
    const content = filtered.slice(page * size, page * size + size);

    return HttpResponse.json({ content, page, size, totalElements, totalPages });
  }),

  // ── POST /api/v1/orders （新增訂單）─────────────────────
  http.post(ORDER_BASE, async ({ request }) => {
    await delay(DELAY);
    const body = (await request.json()) as {
      customerId: number;
      isUrgent: boolean;
      items: {
        serviceCode: string;
        quantity: number;
        unitPrice?: number;
        imageUrls?: string[];
        productName?: string;
        itemNote?: string;
        itemStorageLocation?: string;
      }[];
      storageLocations?: string[];
      estimatedPickupDate: string;
      note?: string;
    };

    const customer = mockCustomers.find((c) => c.id === body.customerId);
    if (!customer) {
      return HttpResponse.json({ message: '會員不存在' }, { status: 404 });
    }

    let serviceSubtotal = 0;
    const orderItems = body.items.map((i, idx) => {
      const svc = mockServices.find((s) => s.code === i.serviceCode);
      // 如果 body 有傳 unitPrice 優先使用，否則才用 svc.defaultPrice
      const finalPrice = i.unitPrice ?? svc?.defaultPrice ?? 0;
      const subtotal = finalPrice * i.quantity;
      serviceSubtotal += subtotal;
      return {
        id: idx + 1,
        serviceCode: i.serviceCode,
        serviceName: svc?.name ?? i.serviceCode,
        quantity: i.quantity,
        unitPrice: finalPrice,
        subtotal,
        imageUrls: i.imageUrls ?? [],
        productName: i.productName,
        itemNote: i.itemNote,
        itemStorageLocation: i.itemStorageLocation,
      };
    });

    const urgentFee = body.isUrgent ? Math.round(serviceSubtotal * 0.5) : 0;
    const newOrder: OrderResult = {
      id: getNextOrderId(),
      orderNo: genOrderNo(),
      customerId: customer.id,
      customerName: customer.name,
      customerPhone: customer.phone ?? '',
      status: 'PENDING',
      isUrgent: body.isUrgent,
      items: orderItems,
      storageLocations: body.storageLocations ?? [],
      totalAmount: serviceSubtotal + urgentFee,
      urgentFee,
      estimatedPickupDate: body.estimatedPickupDate,
      createTime: new Date().toISOString(),
    };

    mockOrderList.unshift(newOrder);
    return HttpResponse.json(newOrder, { status: 201 });
  }),

  // ── POST /api/v1/upload （模擬圖片上傳） ─────────────────
  http.post(UPLOAD_URL, async () => {
    await delay(1000); // 模擬上傳延遲
    // 隨機返回一個 Unsplash 鞋子/包包 圖片
    const mockImages = [
      'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80', // 紅色運動鞋
      'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=600&q=80', // 棕色皮革鞋
      'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=600&q=80', // 時尚球鞋
      'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600&q=80', // 皮革包包
    ];
    const url = mockImages[Math.floor(Math.random() * mockImages.length)];
    return HttpResponse.json({ url });
  }),

  // ── PUT /api/v1/orders/:id （更新訂單）──────────────────
  http.put(`${ORDER_BASE}/:id`, async ({ params, request }) => {
    await delay(DELAY);
    const id = Number(params.id);
    const body = (await request.json()) as {
      isUrgent?: boolean;
      items?: {
        serviceCode: string;
        quantity: number;
        unitPrice?: number;
        imageUrls?: string[];
        productName?: string;
        itemNote?: string;
        itemStorageLocation?: string;
      }[];
      storageLocations?: string[];
      estimatedPickupDate?: string;
      note?: string;
    };

    const idx = mockOrderList.findIndex((o) => o.id === id);
    if (idx === -1) {
      return HttpResponse.json({ message: '訂單不存在' }, { status: 404 });
    }

    const order = mockOrderList[idx];
    const isUrgent = body.isUrgent ?? order.isUrgent;

    let orderItems = order.items;
    let serviceSubtotal = 0;

    if (body.items) {
      orderItems = body.items.map((i, iIdx) => {
        const svc = mockServices.find((s) => s.code === i.serviceCode);
        const finalPrice = i.unitPrice ?? svc?.defaultPrice ?? 0;
        const subtotal = finalPrice * i.quantity;
        serviceSubtotal += subtotal;
        return {
          id: iIdx + 1,
          serviceCode: i.serviceCode,
          serviceName: svc?.name ?? i.serviceCode,
          quantity: i.quantity,
          unitPrice: finalPrice,
          subtotal,
          imageUrls: i.imageUrls ?? [],
          productName: i.productName,
          itemNote: i.itemNote,
          itemStorageLocation: i.itemStorageLocation,
        };
      });
    } else {
      serviceSubtotal = order.totalAmount - order.urgentFee;
    }

    const urgentFee = isUrgent ? Math.round(serviceSubtotal * 0.5) : 0;

    const updated: OrderResult = {
      ...order,
      isUrgent,
      items: orderItems,
      storageLocations: body.storageLocations ?? order.storageLocations,
      estimatedPickupDate: body.estimatedPickupDate ?? order.estimatedPickupDate,
      urgentFee,
      totalAmount: serviceSubtotal + urgentFee,
    };

    mockOrderList[idx] = updated;
    return HttpResponse.json(updated);
  }),

  // ── PATCH /api/v1/orders/:id/status （更新狀態）─────────
  http.patch(`${ORDER_BASE}/:id/status`, async ({ params, request }) => {
    await delay(DELAY);
    const id = Number(params.id);
    const body = (await request.json()) as { status: OrderResult['status']; actualPickupDate?: string };

    const idx = mockOrderList.findIndex((o) => o.id === id);
    if (idx === -1) {
      return HttpResponse.json({ message: '訂單不存在' }, { status: 404 });
    }

    const updated: OrderResult = {
      ...mockOrderList[idx],
      status: body.status,
      actualPickupDate: body.actualPickupDate ?? mockOrderList[idx].actualPickupDate,
    };

    mockOrderList[idx] = updated;
    return HttpResponse.json(updated);
  }),

  // ── GET /api/v1/orders/:id （單筆訂單）──────────────────
  http.get(`${ORDER_BASE}/:id`, async ({ params }) => {
    await delay(DELAY);
    const id = Number(params.id);
    const order = mockOrderList.find((o) => o.id === id);
    if (!order) {
      return HttpResponse.json({ message: '訂單不存在' }, { status: 404 });
    }
    return HttpResponse.json(order);
  }),

  // ── 服務設定 ─────────────────────────────────────────────

  // GET /api/v1/settings/services
  http.get(SERVICE_SETTINGS_BASE, async ({ request }) => {
    await delay(DELAY);
    const url = new URL(request.url);
    const includeInactive = url.searchParams.get('includeInactive') === 'true';
    const result = includeInactive
      ? mockServices
      : mockServices.filter((s) => s.isActive);
    return HttpResponse.json(result);
  }),

  // POST /api/v1/settings/services
  http.post(SERVICE_SETTINGS_BASE, async ({ request }) => {
    await delay(DELAY);
    const body = (await request.json()) as ServiceTypeResult;
    const exists = mockServices.find((s) => s.code === body.code);
    if (exists) {
      return HttpResponse.json({ message: '服務代碼已存在' }, { status: 409 });
    }
    const newService: ServiceTypeResult = {
      code: body.code,
      name: body.name,
      defaultPrice: body.defaultPrice,
      urgentFeeRate: body.urgentFeeRate ?? null,
      isActive: true,
    };
    mockServices.push(newService);
    return HttpResponse.json(newService, { status: 201 });
  }),

  // PUT /api/v1/settings/services/:code
  http.put(`${SERVICE_SETTINGS_BASE}/:code`, async ({ params, request }) => {
    await delay(DELAY);
    const code = params.code as string;
    const body = (await request.json()) as Partial<ServiceTypeResult>;
    const idx = mockServices.findIndex((s) => s.code === code);
    if (idx === -1) {
      return HttpResponse.json({ message: '服務不存在' }, { status: 404 });
    }
    mockServices[idx] = { ...mockServices[idx], ...body };
    return HttpResponse.json(mockServices[idx]);
  }),

  // PUT /api/v1/settings/urgent-fee-rate
  http.put(URGENT_FEE_RATE_URL, async () => {
    await delay(DELAY);
    return HttpResponse.json({ message: 'ok' });
  }),
];
