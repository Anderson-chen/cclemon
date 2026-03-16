import { healthApi } from '../../boot/axios';
import type {
  CustomerResult,
  CustomerCreateRequest,
  CustomerUpdateRequest,
  CustomerOrderSummary,
  ListCustomersParams,
  PagedResult,
} from './types';

const BASE_PATH = '/api/v1/customers';

/**
 * 查詢會員列表
 */
export async function listCustomers(
  params: ListCustomersParams = {}
): Promise<PagedResult<CustomerResult>> {
  const response = await healthApi.get<PagedResult<CustomerResult>>(BASE_PATH, { params });
  return response.data;
}

/**
 * 依電話 / 姓名搜尋會員
 */
export async function searchCustomers(
  keyword: string
): Promise<CustomerResult[]> {
  const response = await healthApi.get<CustomerResult[]>(`${BASE_PATH}/search`, {
    params: { keyword },
  });
  return response.data;
}

/**
 * 查詢單筆會員資料
 */
export async function getCustomer(id: number): Promise<CustomerResult> {
  const response = await healthApi.get<CustomerResult>(`${BASE_PATH}/${id}`);
  return response.data;
}

/**
 * 建立會員
 */
export async function createCustomer(
  data: CustomerCreateRequest
): Promise<CustomerResult> {
  const response = await healthApi.post<CustomerResult>(BASE_PATH, data);
  return response.data;
}

/**
 * 更新會員資料
 */
export async function updateCustomer(
  id: number,
  data: CustomerUpdateRequest
): Promise<CustomerResult> {
  const response = await healthApi.put<CustomerResult>(`${BASE_PATH}/${id}`, data);
  return response.data;
}

/**
 * 查詢會員歷史訂單
 */
export async function getCustomerOrders(
  id: number
): Promise<CustomerOrderSummary[]> {
  const response = await healthApi.get<CustomerOrderSummary[]>(
    `${BASE_PATH}/${id}/orders`
  );
  return response.data;
}
