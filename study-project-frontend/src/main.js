
import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import 'element-plus/dist/index.css' // css不会自动导入

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
