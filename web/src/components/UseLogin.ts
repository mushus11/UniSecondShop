import {ref} from "vue";

export default function (){
    const username = ref<string>("")
    const password = ref<string>("")
    const usertype = ref<string>("user")
    const btnText = ref<string>("登录")
    const iflogin = ref<boolean>(true)
    function login() {
        btnText.value = "登录中..."
        iflogin.value = false
        console.log(username, password, usertype)
    }
    return {username, password, usertype, btnText,iflogin, login}
}
