import './assets/main.css'
import { createApp } from 'vue'
import App from './App.vue'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia } from "pinia"
import HomePage from "@/components/HomePage.vue"
import UsersPage from "@/components/UsersPage.vue"
import ReportsPage from "@/components/ReportsPage.vue"
import OrdersPage from "@/components/OrdersPage.vue"
import ListingsPage from "@/components/ListingsPage.vue"
import CategoriesPage from "@/components/CategoriesPage.vue"
import CatalogPage from "@/components/CatalogPage.vue"
import ProfilePage from "@/components/ProfilePage.vue"
import ShopPage from "@/components/ShopPage.vue"
import SignPage from "@/components/SignPage.vue"
import Login from './components/Login.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { useLoginStore } from "@/store/UseLogin"

const routes = [
    { path: '/', component: Login },
    { path: '/Login', component: Login },
    { path: '/Sign', component: SignPage },
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

const router = createRouter({
    history: createWebHistory(),
    routes
})

const pinia = createPinia()
const app = createApp(App)
app.use(pinia)
app.use(router)
app.use(ElementPlus)

router.beforeEach((to, _from, next) => {
    const loginStore = useLoginStore()
    loginStore.loadFromLocalStorage()
    const isLoggedIn = loginStore.isLoggedIn

    if (to.meta.requiresAuth && !isLoggedIn) {
        next({ path: '/Login', query: { redirect: to.fullPath } })
    } else {
        next()
    }
})

app.mount('#app')