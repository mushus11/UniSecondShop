// store/UseLogin.ts
import { defineStore } from "pinia";
import axios from "axios";
import { ElMessage } from "element-plus";

export const useLoginStore = defineStore('login', {
    state() {
        return {
            id: '' as string,
            password: '' as string,
            username: '' as string,
            access: 0 as number,
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

            const idNum = parseInt(this.id)
            if (isNaN(idNum)) {
                ElMessage.warning('学号必须为数字')
                return false
            }

            this.btnText = '登录中...'

            try {
                const res = await axios({
                    url: "/api/user/login",
                    method: "post",
                    data: {
                        id: idNum,
                        password: this.password
                    }
                })

                console.log('登录响应:', res.data)

                if (res.data.code === 200) {
                    this.jwt = res.data.jwt
                    this.username = res.data.username || ''
                    this.access = res.data.access || 0
                    this.btnText = '登录'
                    ElMessage.success('登录成功')
                    this.saveToLocalStorage()
                    return true
                } else {
                    this.btnText = '登录'
                    ElMessage.error('登录失败，请检查学号和密码')
                    return false
                }

            } catch (error) {
                console.error('登录异常:', error)
                this.btnText = '登录'
                ElMessage.error('网络异常，请检查网络连接后重试')
                return false
            }
        },

        saveToLocalStorage() {
            localStorage.setItem('jwt', this.jwt)
            localStorage.setItem('id', this.id)
            localStorage.setItem('username', this.username)
            localStorage.setItem('access', String(this.access))
        },

        loadFromLocalStorage() {
            const jwt = localStorage.getItem('jwt')
            const id = localStorage.getItem('id')
            const username = localStorage.getItem('username')
            const access = localStorage.getItem('access')

            if (jwt) this.jwt = jwt
            if (id) this.id = id
            if (username) this.username = username
            if (access) this.access = parseInt(access)
        },

        logout() {
            this.id = ''
            this.password = ''
            this.username = ''
            this.access = 0
            this.btnText = '登录'
            this.jwt = ''

            localStorage.removeItem('jwt')
            localStorage.removeItem('id')
            localStorage.removeItem('username')
            localStorage.removeItem('access')
        },

        isJwtEmpty(): boolean {
            return !this.jwt || this.jwt.trim() === ''
        }
    },
    getters: {
        getJwt(): string {
            return this.jwt
        },
        isLoggedIn(): boolean {
            return this.jwt !== '' && this.jwt !== null && this.jwt !== undefined
        }
    }
})