import { healthApi } from '../../boot/axios';
import type { ServiceTypeResult, ServiceTypeCreateRequest, ServiceTypeUpdateRequest } from './types';

const BASE_PATH = '/api/v1/settings/services';

export const listServices = async (params?: { includeInactive?: boolean }): Promise<ServiceTypeResult[]> => {
  const { data } = await healthApi.get<ServiceTypeResult[]>(BASE_PATH, { params });
  return data;
};

export const createService = async (payload: ServiceTypeCreateRequest): Promise<ServiceTypeResult> => {
  const { data } = await healthApi.post<ServiceTypeResult>(BASE_PATH, payload);
  return data;
};

export const updateService = async (code: string, payload: ServiceTypeUpdateRequest): Promise<ServiceTypeResult> => {
  const { data } = await healthApi.put<ServiceTypeResult>(`${BASE_PATH}/${code}`, payload);
  return data;
};

export const updateGlobalUrgentFeeRate = async (payload: { rate: number }): Promise<void> => {
  await healthApi.put('/api/v1/settings/urgent-fee-rate', payload);
};
