import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import {createRouter,createWebHistory} from 'vue-router'
import HomePage from "@/components/HomePage.vue";
import ShopPage from "@/components/ShopPage.vue";
import BackPage from "@/components/BackPage.vue";
import Login from './components/Login.vue';
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

//配置路由规则
const routes = [
    { path: '/', component:Login},
    { path: '/Login', component:Login},
    { path: '/Home', component:HomePage},
    { path: '/Shop', component:ShopPage},
    { path: '/Back', component:BackPage},
]

//创建路由器
const router= createRouter({
    history:createWebHistory(),//路由工作模式
    routes//路由规则
})

//加载路由器
let app = createApp(App)
app.use(router)
app.use(ElementPlus)

app.mount('#app')
