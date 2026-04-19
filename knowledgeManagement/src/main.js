import { createApp } from 'vue'
//import './style.css'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import i18n from './locale'
//import { useBackendsStore } from './state/appstate'
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate) // 使用插件
const app = createApp(App)
app.use(pinia)
app.use(i18n)
 

//const backendStore = useBackendsStore()
//const baseUrl =  backendStore.endpoints.baseUrl
//console.log(baseUrl)
app.use(router)
app.mount('#app')
