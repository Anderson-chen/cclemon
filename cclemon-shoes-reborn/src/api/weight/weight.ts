import { healthApi } from '../../boot/axios';
import type {
  WeightUpsertRequest,
  WeightRecordResult,
  PagedResult,
  WeightChartData,
  ListWeightsParams,
  GetChartParams,
} from './types';

const BASE_PATH = '/api/v1/weights';

/**
 * 記錄或更新體重
 */
export async function upsertWeight(
  data: WeightUpsertRequest
): Promise<WeightRecordResult> {
  const response = await healthApi.post<WeightRecordResult>(BASE_PATH, data);
  return response.data;
}

/**
 * 查詢體重記錄列表
 */
export async function listWeights(
  params: ListWeightsParams = {}
): Promise<PagedResult<WeightRecordResult>> {
  const response = await healthApi.get<PagedResult<WeightRecordResult>>(
    BASE_PATH,
    { params }
  );
  return response.data;
}

/**
 * 獲取體重圖表數據
 */
export async function getWeightChart(
  params: GetChartParams = {}
): Promise<WeightChartData> {
  const response = await healthApi.get<WeightChartData>(
    `${BASE_PATH}/chart`,
    { params }
  );
  return response.data;
}

/**
 * 刪除體重記錄
 */
export async function deleteWeight(id: number): Promise<void> {
  await healthApi.delete(`${BASE_PATH}/${id}`);
}
