// 會員管理相關的類型定義

export type MemberTierCode = 'STANDARD' | 'SILVER' | 'GOLD';

export interface CustomerResult {
  id: number;
  name: string;
  phone?: string;
  email?: string;
  tierCode: MemberTierCode;
  totalSpend: number;
  visitCount: number;
  note?: string;
  createTime: string;
}

export interface CustomerCreateRequest {
  name: string;
  phone?: string;
  email?: string;
  note?: string;
}

export interface CustomerUpdateRequest {
  name: string;
  phone?: string;
  email?: string;
  note?: string;
}

export interface CustomerOrderSummary {
  id: number;
  orderNo: string;
  status: string;
  isUrgent: boolean;
  totalAmount: number;
  estimatedPickupDate: string;
  actualPickupDate?: string;
  createTime: string;
}

export interface ListCustomersParams {
  keyword?: string;
  tierCode?: MemberTierCode;
  page?: number;
  size?: number;
}

export interface PagedResult<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
