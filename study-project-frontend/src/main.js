
import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import 'element-plus/dist/index.css'
import axios from 'axios' // css不会自动导入

const app = createApp(App)

axios.defaults.baseURL = 'http://127.0.0.1:8080'

app.use(createPinia())
app.use(router)

app.mount('#app')
