// 開單管理相關的類型定義

export type OrderStatus = 'PENDING' | 'IN_PROGRESS' | 'READY' | 'PICKED_UP' | 'CANCELLED';

export interface OrderItem {
  id?: number;
  serviceCode: string;
  serviceName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
  imageUrls?: string[];
  productName?: string;
  itemNote?: string;
  itemStorageLocation?: string;
}

export interface OrderResult {
  id: number;
  orderNo: string;
  customerId: number;
  customerName: string;
  customerPhone: string;
  status: OrderStatus;
  isUrgent: boolean;
  urgentDeadline?: string;
  items: OrderItem[];
  storageLocations: string[];
  totalAmount: number;
  urgentFee: number;
  estimatedPickupDate: string;
  actualPickupDate?: string;
  createTime: string;
}

export interface OrderCreateRequest {
  customerId: number;
  isUrgent: boolean;
  items: { serviceCode: string; quantity: number; unitPrice?: number; imageUrls?: string[]; productName?: string; itemNote?: string; itemStorageLocation?: string }[];
  storageLocations?: string[];
  estimatedPickupDate: string;
}

export interface OrderUpdateRequest {
  isUrgent?: boolean;
  items?: OrderCreateRequest['items'];
  storageLocations?: string[];
  estimatedPickupDate?: string;
}

export interface OrderStatusUpdateRequest {
  status: OrderStatus;
  actualPickupDate?: string;
}

export interface ListOrdersParams {
  keyword?: string;
  status?: OrderStatus;
  statuses?: OrderStatus[];
  isUrgent?: boolean;
  dateFrom?: string;
  dateTo?: string;
  page?: number;
  size?: number;
  sort?: string;
}

export interface PagedResult<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}