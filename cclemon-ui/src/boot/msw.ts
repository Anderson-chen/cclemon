import { boot } from 'quasar/wrappers';

export default boot(async () => {
  const isMswEnabled =
    import.meta.env.DEV ||
    import.meta.env.VITE_MSW_ENABLED === 'true';

  if (!isMswEnabled) return;

  const { worker } = await import('../mocks/browser');

  // ── 啟動 MSW Service Worker ──────────────────────────────
  await worker.start({
    // bypass：不匹配的請求（Vite HMR、靜態檔案等）照常放行
    // 若後端沒開，passthrough 的 fetch 會 fail；
    // 用 onUnhandledRequest 攔截後記錄 warning，而非拋出 uncaught error
    onUnhandledRequest(request, print) {
      // 只警告 API 路徑，靜態資源靜默略過
      if (request.url.includes('/api/')) {
        print.warning();
      }
    },
    serviceWorker: {
      url: '/mockServiceWorker.js',
    },
  });

  console.info(
    '%c[MSW] Mock Service Worker 已啟動（展示模式）',
    'color: #26a69a; font-weight: bold;'
  );
});
