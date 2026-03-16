import { SessionStorage } from 'quasar';
const { VUE_APP_SYSTEM_CODE } = process.env;
function formatKey(key: string): string {
  return `${VUE_APP_SYSTEM_CODE}_${key?.toUpperCase()}`;
}
export default {
  getItem<T>(key: string): T {
    return SessionStorage.getItem(formatKey(key)) as T;
  },
  setItem<T>(key: string, value: T) {
    SessionStorage.set(formatKey(key), value);
  },
  removeItem(key: string) {
    SessionStorage.remove(formatKey(key));
  },
  clear() {
    SessionStorage.getAllKeys()
      .filter((key) => key.includes(VUE_APP_SYSTEM_CODE))
      .forEach(SessionStorage.remove);
  },
};
