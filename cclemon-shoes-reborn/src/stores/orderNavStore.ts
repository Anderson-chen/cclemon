import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { OrderStatus } from '../api/order/types';

export interface PendingFilters {
  status?: OrderStatus;
  isUrgent?: boolean;
  dateFrom?: string;
  dateTo?: string;
}

export const useOrderNavStore = defineStore('orderNav', () => {
  const pendingOrderId = ref<number | null>(null);
  const returnTo = ref<string | null>(null);
  const pendingFilters = ref<PendingFilters | null>(null);

  function setPendingOrderId(id: number, from?: string) {
    pendingOrderId.value = id;
    returnTo.value = from ?? null;
  }

  function consumePendingOrderId(): number | null {
    const id = pendingOrderId.value;
    pendingOrderId.value = null;
    return id;
  }

  function consumeReturnTo(): string | null {
    const path = returnTo.value;
    returnTo.value = null;
    return path;
  }

  function setFilters(filters: PendingFilters) {
    pendingFilters.value = filters;
  }

  function consumeFilters(): PendingFilters | null {
    const f = pendingFilters.value;
    pendingFilters.value = null;
    return f;
  }

  return { setPendingOrderId, consumePendingOrderId, consumeReturnTo, setFilters, consumeFilters };
});
