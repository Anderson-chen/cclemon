import { boot } from 'quasar/wrappers'
import { track } from 'src/utils/track'

export default boot(({ router }) => {
    router.afterEach((to, from) => {
        track('ROUTE', to.fullPath, {
            from: from.fullPath,
            to: to.fullPath
        })
    })
})
