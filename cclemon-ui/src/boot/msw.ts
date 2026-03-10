import { boot } from 'quasar/wrappers';

const SW_RETRY_KEY = 'msw-sw-retry';

export default boot(async () => {
  const isMswEnabled =
    import.meta.env.DEV ||
    import.meta.env.VITE_MSW_ENABLED === 'true';

  if (!isMswEnabled) return;

  const { worker } = await import('../mocks/browser');

  // ── 啟動 MSW Service Worker ──────────────────────────────
  // PWA SW（skipWaiting + clientsClaim）首次安裝時會搶佔控制權，
  // 導致 MSW SW 卡在 waiting 狀態無法啟動。
  // 策略：設定 2 秒超時，若超時則自動重新整理一次讓 SW 穩定，
  // 第二次載入 PWA SW 已就緒，MSW 可正常排隊啟動。
  try {
    await Promise.race([
      worker.start({
        onUnhandledRequest(request, print) {
          if (request.url.includes('/api/')) {
            print.warning();
          }
        },
        serviceWorker: {
          url: '/mockServiceWorker.js',
        },
      }),
      new Promise<never>((_, reject) =>
        setTimeout(() => reject(new Error('sw-timeout')), 2000),
      ),
    ]);

    // 啟動成功，清除重試旗標
    sessionStorage.removeItem(SW_RETRY_KEY);

    console.info(
      '%c[MSW] Mock Service Worker 已啟動（展示模式）',
      'color: #26a69a; font-weight: bold;',
    );
  } catch {
    // PWA SW 首次安裝衝突 → 自動重新整理一次
    if (!sessionStorage.getItem(SW_RETRY_KEY)) {
      sessionStorage.setItem(SW_RETRY_KEY, '1');
      location.reload();
      return;
    }
    // 第二次仍失敗（罕見）→ 忽略，讓 app 繼續載入
    console.warn('[MSW] Service Worker 啟動逾時，跳過 mock 模式');
  }
});
