import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
    const user = ref(null)
    const isAuthenticated = ref(false)
    const rememberMe = ref(false)

    const setUser = (userData ) => {
      user.value = userData
      isAuthenticated.value = !!userData
    }

    const setRememberMe = (value ) => {
      rememberMe.value = value
    }

    const logout = () => {
      user.value = null
      isAuthenticated.value = false
    }

    return {
      user,
      isAuthenticated,
      rememberMe,
      setUser,
      setRememberMe,
      logout
    }
  },
  {
     persist: true // 启用持久化
  }
)


 