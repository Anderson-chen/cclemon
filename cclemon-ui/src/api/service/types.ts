export interface ServiceTypeResult {
  code: string;
  name: string;
  defaultPrice: number;
  urgentFeeRate: number | null;
  isActive: boolean;
}

export interface ServiceTypeCreateRequest {
  code: string;
  name: string;
  defaultPrice: number;
  urgentFeeRate?: number | null;
}

export interface ServiceTypeUpdateRequest {
  name?: string;
  defaultPrice?: number;
  urgentFeeRate?: number | null;
  isActive?: boolean;
}
