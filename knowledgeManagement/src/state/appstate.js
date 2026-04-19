import { defineStore } from 'pinia'
import {  reactive } from 'vue'
import env from '../config/env.js'

export const useBackendsStore = defineStore('BackendsStore',  () => {
    let backendServices = {
        "baseUrl": "http://127.0.0.1:8000",
    }
    if (env == "local"){
        backendServices  = {
           "baseUrl": "http://127.0.0.1:8000",
        }
    }
    else{
       backendServices = {
            "baseUrl": "http://127.0.0.1:8000",
        }
    }
    const endpoints = reactive(backendServices)

  return { endpoints }
})