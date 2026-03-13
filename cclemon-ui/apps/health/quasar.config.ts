/* eslint-env node */

// Configuration for your app
// https://v2.quasar.dev/quasar-cli-vite/quasar-config-js

import { configure } from 'quasar/wrappers';

export default configure(() => {
  return {
    // app boot file (/src/boot)
    // --> boot files are part of "main.js"
    // https://v2.quasar.dev/quasar-cli-vite/boot-files
    boot: ['axios'],

    // https://v2.quasar.dev/quasar-cli-vite/quasar-config-js#css
    css: ['app.scss'],

    // https://github.com/quasarframework/quasar/tree/dev/extras
    extras: [
      'roboto-font',
      'material-icons',
    ],

    // Full list of options: https://v2.quasar.dev/quasar-cli-vite/quasar-config-js#build
    build: {
      target: {
        browser: ['es2022', 'firefox115', 'chrome115', 'safari14'],
        node: 'node20',
      },

      vueRouterMode: 'history', // available values: 'hash', 'history'

      /**
       * 注意事項:
       * 會依照打包時的順序覆蓋環境變數（後蓋前）
       */
      envFiles: [`.env.${process.env.ENV_FILE}`],

      vitePlugins: [
        {
          name: 'print-env',
          config() {
            console.log('Vite Environment Variables:', process.env);
          },
        },
        [
          'vite-plugin-checker',
          {
            vueTsc: {
              tsconfigPath: 'tsconfig.vue-tsc.json',
            },
            eslint: {
              lintCommand: 'eslint "./**/*.{js,ts,mjs,cjs,vue}"',
            },
          },
          { server: false },
        ],
      ],
    },

    // Full list of options: https://v2.quasar.dev/quasar-cli-vite/quasar-config-js#devServer
    devServer: {
      port: 5174,
      open: false,
    },

    // https://v2.quasar.dev/quasar-cli-vite/quasar-config-js#framework
    framework: {
      config: {},

      lang: 'zh-TW', // Quasar language pack

      // Quasar plugins
      plugins: ['LocalStorage', 'SessionStorage', 'Loading', 'Dialog', 'Notify'],
    },

    animations: [],

    ssr: {
      prodPort: 3000,
      middlewares: [
        'render', // keep this as last one
      ],
      pwa: false,
    },

    pwa: {
      workboxMode: 'GenerateSW',
    },

    cordova: {},

    capacitor: {
      hideSplashscreen: true,
    },

    electron: {
      preloadScripts: ['electron-preload'],
      inspectPort: 5858,
      bundler: 'packager',
      packager: {},
      builder: {
        appId: 'cclemon-health',
      },
    },

    bex: {
      contentScripts: ['my-content-script'],
    },
  };
});
