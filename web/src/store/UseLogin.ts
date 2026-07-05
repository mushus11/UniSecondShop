import {defineStore} from "pinia";

export const useLoginStore = defineStore('login', {
    actions: {
        login() {
            this.btnText = "登录中..."
            this.IfLogin = false
            console.log(this.username, this.password, this.usertype)

            // 模拟登录请求
            setTimeout(() => {
                // 假设登录成功
                this.IfLogin = true
                this.btnText = "登录"

                // 根据用户类型设置管理员状态
                this.Ifadmin = this.usertype === 'admin'

                // 保存状态到 localStorage
                this.saveToLocalStorage()
            }, 2000)
        },

        // 退出登录
        logout() {
            this.IfLogin = false
            this.Ifadmin = false
            this.username = ''
            this.password = ''
            this.usertype = 'user'
            this.btnText = '登录'

            // 清除 localStorage
            this.clearLocalStorage()
        },

        // 保存登录和管理员状态到 localStorage
        saveToLocalStorage() {
            try {
                const loginInfo = {
                    IfLogin: this.IfLogin,
                    Ifadmin: this.Ifadmin,
                    usertype: this.usertype,
                    username: this.username
                }
                localStorage.setItem('loginInfo', JSON.stringify(loginInfo))
            } catch (error) {
                console.error('保存状态失败:', error)
            }
        },

        // 从 localStorage 加载状态
        loadFromLocalStorage() {
            try {
                const saved = localStorage.getItem('loginInfo')
                if (saved) {
                    const loginInfo = JSON.parse(saved)
                    this.IfLogin = loginInfo.IfLogin || false
                    this.Ifadmin = loginInfo.Ifadmin || false
                    this.usertype = loginInfo.usertype || 'user'
                    this.username = loginInfo.username || ''
                }
            } catch (error) {
                console.error('加载状态失败:', error)
            }
        },

        // 清除 localStorage
        clearLocalStorage() {
            try {
                localStorage.removeItem('loginInfo')
            } catch (error) {
                console.error('清除状态失败:', error)
            }
        },

        // 设置管理员状态
        setAdminStatus(isAdmin: boolean) {
            this.Ifadmin = isAdmin
            this.saveToLocalStorage()
        },

        // 设置登录状态
        setLoginStatus(isLogin: boolean) {
            this.IfLogin = isLogin
            this.saveToLocalStorage()
        }
    },
    getters: {
        // 获取登录状态
        getIfLogin(): boolean {
            return this.IfLogin
        },

        // 获取管理员状态
        getIfAdmin(): boolean {
            return this.Ifadmin
        },

        // 是否已登录
        isLoggedIn(): boolean {
            return this.IfLogin
        },

        // 是否是管理员
        isAdmin(): boolean {
            return this.Ifadmin && this.IfLogin
        }
    },
    state(){
        return {
            username: '' as string,
            password: '' as string,
            usertype: 'user' as string,
            btnText: '登录' as string,
            IfLogin: false as boolean,
            Ifadmin: false as boolean
        }
    }
})