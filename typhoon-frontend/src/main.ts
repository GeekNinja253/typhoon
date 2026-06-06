import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import * as Cesium from 'cesium'

Cesium.Ion.defaultAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI3MDVlODgwNS0yOWU4LTQ0NTUtYmVhOC01NTBhYTIzODNmZjEiLCJpZCI6NDM5NzUwLCJzdWIiOiJrZXZlciIsImlzcyI6Imh0dHBzOi8vYXBpLmNlc2l1bS5jb20iLCJhdWQiOiJ0eXBob29uIiwiaWF0IjoxNzgwNDY4NTc0fQ.E645lrOnlBbGtPilNVdaPNMD8KhgLbEJehigiJSGrKI"
const app = createApp(App)
app.use(router)
app.mount('#app')
