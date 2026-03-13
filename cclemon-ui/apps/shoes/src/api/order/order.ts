import { healthApi } from '../../boot/axios';
import type {
  OrderResult,
  OrderCreateRequest,
  OrderUpdateRequest,
  OrderStatusUpdateRequest,
  ListOrdersParams,
  PagedResult,
} from './types';

const BASE_PATH = '/api/v1/orders';

/**
 * 查詢訂單列表（分頁 + 篩選）
 */
export async function listOrders(
  params: ListOrdersParams = {}
): Promise<PagedResult<OrderResult>> {
  const response = await healthApi.get<PagedResult<OrderResult>>(BASE_PATH, { params });
  return response.data;
}

/**
 * 查詢單筆訂單
 */
export async function getOrder(id: number): Promise<OrderResult> {
  const response = await healthApi.get<OrderResult>(`${BASE_PATH}/${id}`);
  return response.data;
}

/**
 * 建立訂單
 */
export async function createOrder(data: OrderCreateRequest): Promise<OrderResult> {
  const response = await healthApi.post<OrderResult>(BASE_PATH, data);
  return response.data;
}

/**
 * 更新訂單（位置、備註等）
 */
export async function updateOrder(
  id: number,
  data: OrderUpdateRequest
): Promise<OrderResult> {
  const response = await healthApi.put<OrderResult>(`${BASE_PATH}/${id}`, data);
  return response.data;
}

/**
 * 更新訂單狀態
 */
export async function updateOrderStatus(
  id: number,
  data: OrderStatusUpdateRequest
): Promise<OrderResult> {
  const response = await healthApi.patch<OrderResult>(`${BASE_PATH}/${id}/status`, data);
  return response.data;
}