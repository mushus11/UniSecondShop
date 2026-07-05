import './assets/main.css'
import { createApp } from 'vue'
import App from './App.vue'
import {createRouter,createWebHistory} from 'vue-router'
import{createPinia} from "pinia";
import HomePage from "@/components/HomePage.vue";
import UsersPage from "@/components/UsersPage.vue";
import ReportsPage from "@/components/ReportsPage.vue";
import OrdersPage from "@/components/OrdersPage.vue";
import ListingsPage from "@/components/ListingsPage.vue";
import CategoriesPage from "@/components/CategoriesPage.vue";
import CatalogPage from "@/components/CatalogPage.vue";
import ProfilePage from "@/components/ProfilePage.vue";
import ShopPage from "@/components/ShopPage.vue";
import Login from './components/Login.vue';
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { useLoginStore } from "@/store/UseLogin"

//配置路由规则
const routes = [
    // 公开路由（不需要登录）
    { path: '/', component: Login },
    { path: '/Login', component: Login },

    // 需要登录的路由（添加 meta 标识）
    { path: '/Home', component: HomePage, meta: { requiresAuth: true } },
    { path: '/Users', component: UsersPage, meta: { requiresAuth: true } },
    { path: '/Reports', component: ReportsPage, meta: { requiresAuth: true } },
    { path: '/Orders', component: OrdersPage, meta: { requiresAuth: true } },
    { path: '/Listings', component: ListingsPage, meta: { requiresAuth: true } },
    { path: '/Categories', component: CategoriesPage, meta: { requiresAuth: true } },
    { path: '/Catalog', component: CatalogPage, meta: { requiresAuth: true } },
    { path: '/Profile', component: ProfilePage, meta: { requiresAuth: true } },
    { path: '/Shop', component: ShopPage, meta: { requiresAuth: true } },
]


//创建路由器
const router= createRouter({
    history:createWebHistory(),//路由工作模式
    routes//路由规则
})

// ============ 4. 路由守卫（权限控制） ============
router.beforeEach((to, from, next) => {
    // 获取 LoginStore 实例（使用同一个 pinia）
    const loginStore = useLoginStore(pinia)

    // 从 localStorage 恢复登录状态
    loginStore.loadFromLocalStorage()

    // 判断目标路由是否需要登录
    if (to.meta.requiresAuth) {
        // 需要登录的页面
        if (!loginStore.IfLogin) {
            // 未登录 → 跳转到登录页，携带原路径
            next({
                path: '/Login',
                query: { redirect: to.fullPath }
            })
        } else {
            // 已登录 → 正常放行
            next()
        }
    } else {
        // 公开页面 → 正常放行
        next()
    }
})

//加载路由器
const pinia = createPinia()
let app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.use(pinia)

app.mount('#app')
