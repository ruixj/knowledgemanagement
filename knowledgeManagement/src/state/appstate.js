import { defineStore } from 'pinia'
import { reactive, ref } from 'vue'
import env from '../config/env.js'

export const useBackendsStore = defineStore('BackendsStore',  () => {
    let backendServices = {
        "baseUrl": "",
    }
    if (env == "local"){
        backendServices  = {
           "baseUrl": "",  // 本地开发走 Vite 代理，使用相对路径
        }
    }
    else{
       backendServices = {
            "baseUrl": "http://127.0.0.1:8080",  // 生产环境直连后端
        }
    }
    const endpoints = reactive(backendServices)

  return { endpoints }
})

export const useCurrentKBStore = defineStore('CurrentKBStore', () => {
    const currentKB = ref(null)

    const setCurrentKB = (kb) => {
        currentKB.value = kb
    }

    const clearCurrentKB = () => {
        currentKB.value = null
    }

    return { currentKB, setCurrentKB, clearCurrentKB }
}, { persist: true })