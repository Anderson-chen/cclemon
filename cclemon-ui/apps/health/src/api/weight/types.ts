// 體重記錄相關的類型定義

export interface WeightUpsertRequest {
  measureDate?: string; // YYYY-MM-DD
  weightKg: number;
  measureTime?: string; // HH:mm:ss
  note?: string;
}

export interface WeightRecordResult {
  id: number;
  userId: number;
  measureDate: string;
  weightKg: number;
  measureTime?: string;
  note?: string;
  createdAt: string;
  updatedAt: string;
}

export interface PagedResult<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface WeightChartData {
  dates: string[];
  weights: number[];
  avgWeight?: number;
  maxWeight?: number;
  minWeight?: number;
  weightChange?: number;
}

export interface ListWeightsParams {
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
}

export interface GetChartParams {
  range?: '7d' | '30d' | '90d' | 'custom';
  startDate?: string;
  endDate?: string;
}
