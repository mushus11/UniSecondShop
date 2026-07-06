// store/UseLogin.ts
import { defineStore } from "pinia";
import axios from "axios";
import { ElMessage } from "element-plus";

export const useLoginStore = defineStore('login', {
    state() {
        return {
            id: '' as string,
            password: '' as string,
            usertype: 'user' as string,
            btnText: '登录' as string,
            jwt: '' as string,
        }
    },
    actions: {
        async login() {
            if (!this.id || !this.password) {
                ElMessage.warning('请输入学号和密码')
                return false
            }

            this.btnText = '登录中...'

            try {
                const res = await axios({
                    url: "/api/auth/login",
                    method: "post",
                    data: {
                        id: parseInt(this.id),
                        password: this.password
                    }
                })

                console.log('登录响应:', res.data)

                if (res.data.code === 200) {
                    this.jwt = res.data.jwt
                    this.btnText = '登录'
                    ElMessage.success('登录成功')

                    // 保存到 localStorage
                    this.saveToLocalStorage()

                    return true
                } else {
                    this.btnText = '登录'
                    ElMessage.error(res.data.message || '登录失败，请检查学号和密码')
                    return false
                }

            } catch (error) {
                console.error('登录异常:', error)
                this.btnText = '登录'
                ElMessage.error('网络异常，请检查网络连接后重试')
                return false
            }
        },

        // 保存到 localStorage
        saveToLocalStorage() {
            localStorage.setItem('jwt', this.jwt)
            localStorage.setItem('id', this.id)
            localStorage.setItem('usertype', this.usertype)
        },

        // 从 localStorage 加载
        loadFromLocalStorage() {
            const jwt = localStorage.getItem('jwt')
            const id = localStorage.getItem('id')
            const usertype = localStorage.getItem('usertype')

            if (jwt) {
                this.jwt = jwt
            }
            if (id) {
                this.id = id
            }
            if (usertype) {
                this.usertype = usertype
            }
        },

        logout() {
            this.id = ''
            this.password = ''
            this.usertype = 'user'
            this.btnText = '登录'
            this.jwt = ''

            localStorage.removeItem('jwt')
            localStorage.removeItem('id')
            localStorage.removeItem('usertype')
        },

        // 检查 JWT 是否为空
        isJwtEmpty(): boolean {
            return !this.jwt || this.jwt.trim() === ''
        }
    },
    getters: {
        // 获取 JWT
        getJwt(): string {
            return this.jwt
        },

        // 是否已登录（JWT 不为空）
        isLoggedIn(): boolean {
            return this.jwt !== '' && this.jwt !== null && this.jwt !== undefined
        }
    }
})