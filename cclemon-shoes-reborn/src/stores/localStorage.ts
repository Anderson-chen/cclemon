import { LocalStorage } from 'quasar';
const { VUE_APP_SYSTEM_CODE } = process.env;
function formatKey(key: string): string {
  return `${VUE_APP_SYSTEM_CODE}_${key?.toUpperCase()}`;
}
export default {
  getItem<T>(key: string): T {
    return LocalStorage.getItem(formatKey(key)) as T;
  },
  setItem<T>(key: string, value: T) {
    LocalStorage.set(formatKey(key), value);
  },
  clear() {
    LocalStorage.getAllKeys()
      .filter((key) => key.includes(VUE_APP_SYSTEM_CODE))
      .forEach(LocalStorage.remove);
  },
};
