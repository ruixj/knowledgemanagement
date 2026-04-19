import { createMemoryHistory, createRouter } from 'vue-router'


import { useAuthStore} from '../utils/auth'
import {routes} from '../routes'
const router = createRouter({
  history: createMemoryHistory(),
  routes,
})

// 路由守卫
router.beforeEach((to, from, next) => {
   const authStore = useAuthStore()
   const isAuthenticated = authStore.isAuthenticated
   console.log(isAuthenticated)
  if (/*to.meta.requiresAuth && */!isAuthenticated && !(to.name === 'Login' || to.name === 'Register')) {
     //next('/login')
     router.push({name:'Login'})
  } else if ((to.name === 'Login' || to.name === 'Register') && isAuthenticated) {
     router.push({name:'app'})
  } else {
    next()
  }
})
export default router
 