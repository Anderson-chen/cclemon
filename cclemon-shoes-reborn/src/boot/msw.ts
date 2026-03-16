import { boot } from 'quasar/wrappers';

export default boot(async () => {
  const isMswEnabled =
    import.meta.env.DEV ||
    import.meta.env.VITE_MSW_ENABLED === 'true';

  if (!isMswEnabled) return;

  const { worker } = await import('../mocks/browser');

  await worker.start({
    onUnhandledRequest(request, print) {
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
    'color: #26a69a; font-weight: bold;',
  );
});
