import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useOrderNavStore = defineStore('orderNav', () => {
  const pendingOrderId = ref<number | null>(null);

  function setPendingOrderId(id: number) {
    pendingOrderId.value = id;
  }

  function consumePendingOrderId(): number | null {
    const id = pendingOrderId.value;
    pendingOrderId.value = null;
    return id;
  }

  return { setPendingOrderId, consumePendingOrderId };
});
