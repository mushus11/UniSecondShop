import { defineStore } from "pinia";
import axios from "axios";
import { ElMessage } from "element-plus";

export const useLoginStore = defineStore('login', {
    state() {
        return {
            username: '' as string,
            password: '' as string,
            usertype: 'user' as string,
            btnText: '登录' as string,
            IfLogin: false as boolean,
            Ifadmin: false as boolean,
            jwt: '' as string,
            userId: null as number | null,
            loading: false as boolean
        }
    },
    actions: {
        async login() {
            // 表单验证
            if (!this.username || !this.password) {
                ElMessage.warning('请输入学号和密码')
                return false
            }

            this.btnText = '登录中...'
            this.loading = true

            try {
                // 调用登录接口
                const res = await axios({
                    url: "/api/auth/login",
                    method: "post",
                    data: {
                        id: parseInt(this.username),  // 转为 int
                        password: this.password
                    }
                })

                console.log('登录响应:', res.data)

                // 判断返回码
                if (res.data.code === 200) {
                    // 登录成功
                    this.jwt = res.data.jwt
                    this.userId = res.data.id
                    this.username = res.data.username || this.username
                    this.IfLogin = true
                    this.btnText = '登录'

                    // 根据业务判断是否为管理员（可根据返回数据中的角色判断）
                    // 如果接口返回了角色字段，可以在这里设置
                    // this.Ifadmin = res.data.role === 'admin'

                    // 保存到 localStorage
                    this.saveToLocalStorage()

                    ElMessage.success('登录成功')
                    return true

                } else if (res.data.code === 201) {
                    // 登录失败
                    this.IfLogin = false
                    this.btnText = '登录'
                    ElMessage.error('登录失败，请检查学号和密码')
                    return false

                } else {
                    this.IfLogin = false
                    this.btnText = '登录'
                    ElMessage.error(res.data.message || '登录失败，请稍后重试')
                    return false
                }

            } catch (error) {
                console.error('登录异常:', error)
                this.IfLogin = false
                this.btnText = '登录'
                ElMessage.error('网络异常，请检查网络连接后重试')
                return false
            } finally {
                this.loading = false
            }
        },

        // 退出登录
        logout() {
            this.IfLogin = false
            this.Ifadmin = false
            this.username = ''
            this.password = ''
            this.usertype = 'user'
            this.btnText = '登录'
            this.jwt = ''
            this.userId = null

            // 清除 localStorage
            this.clearLocalStorage()
        },

        // 保存登录状态到 localStorage
        saveToLocalStorage() {
            try {
                const loginInfo = {
                    IfLogin: this.IfLogin,
                    Ifadmin: this.Ifadmin,
                    usertype: this.usertype,
                    username: this.username,
                    jwt: this.jwt,
                    userId: this.userId
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
                    this.jwt = loginInfo.jwt || ''
                    this.userId = loginInfo.userId || null
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
        },

        // 获取 JWT
        getJwt(): string {
            return this.jwt
        },

        // 获取用户ID
        getUserId(): number | null {
            return this.userId
        }
    }
})