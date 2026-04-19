<template>
    <div class="threadbar-row"> 
        <a-breadcrumb>
            <a-breadcrumb-item 
                v-for="(item, index) in breadcrumbList" 
                :key="index"
            >
                <a v-if="item.icon" class="breadcrumb-icon">
                    <component :is="item.icon" />
                </a>
                <router-link 
                    v-if="index < breadcrumbList.length - 1 && item.path" 
                    :to="item.path"
                >
                {{ item.title }}
               </router-link>
               <span v-else>{{ item.title }}</span>
            </a-breadcrumb-item>
        </a-breadcrumb>
    </div>
</template>

<script>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  HomeOutlined, 
  UserOutlined, 
  AppstoreOutlined 
} from '@ant-design/icons-vue'

export default {
  name: 'AdvancedBreadcrumb',
  components: {
    HomeOutlined,
    UserOutlined,
    AppstoreOutlined
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    // 图标映射
    const iconMap = {
      HomeOutlined,
      UserOutlined,
      AppstoreOutlined
    }
    
    const breadcrumbList = computed(() => {
      const breadcrumbs = []
      
      // 方法1：优先使用路由匹配（嵌套路由）
      console.log(route.matched)
      const matchedRoutes = route.matched.filter(item => item.meta?.breadcrumb)
      
    //   if (matchedRoutes.length > 1) {
    //     // 使用嵌套路由自动识别
    //     breadcrumbs.push(...matchedRoutes.map((item, index) => ({
    //       title: getDynamicTitle(item, route),
    //       path: index < matchedRoutes.length - 1 ? getResolvedPath(item, route) : null,
    //       icon: iconMap[item.meta?.icon]
    //     })))
    //   } 
    //   else 
      {
        // 方法2：使用显式父子关系
        let currentRoute = route
        const tempBreadcrumbs = []
        
        while (currentRoute && currentRoute.meta?.breadcrumb) {
          tempBreadcrumbs.unshift({
            title: getDynamicTitle(currentRoute, route),
            path: getResolvedPath(currentRoute, route),
            icon: iconMap[currentRoute.meta?.icon]
          })
          
          // 查找父级
          const parentName = currentRoute.meta?.parent
          if (parentName) {
            const parentRoute = router.getRoutes().find(r => r.name === parentName)
            currentRoute = parentRoute
          } else {
            currentRoute = null
          }
        }
        
        // 处理最后一项不可点击
        if (tempBreadcrumbs.length > 0) {
          tempBreadcrumbs[tempBreadcrumbs.length - 1].path = null
        }
        
        breadcrumbs.push(...tempBreadcrumbs)
      }
      
      return breadcrumbs
    })
    
    // 获取动态标题
    const getDynamicTitle = (routeRecord, currentRoute) => {
      const title = routeRecord.meta?.title
      
      if (typeof title === 'function') {
        return title(currentRoute)
      }
      
      if (title && currentRoute.params) {
        return title.replace(/\$\{(\w+)\}/g, (match, p1) => {
          return currentRoute.params[p1] || match
        })
      }
      
      return title || routeRecord.name
    }
    
    // 解析路径（处理动态参数）
    const getResolvedPath = (routeRecord, currentRoute) => {
      let path = routeRecord.path
      
      if (path.includes(':') && currentRoute.params) {
        Object.keys(currentRoute.params).forEach(param => {
          if (path.includes(`:${param}`)) {
            path = path.replace(`:${param}`, currentRoute.params[param])
          }
        })
      }
      
      return path
    }
    
    return {
      breadcrumbList
    }
  }
}
</script>

<style scoped>
.breadcrumb-icon {
  margin-right: 4px;
  color: #c62f2f;
}

.threadbar-row {
    padding: 18px 28px;
    border-bottom: 2px solid #c62f2f;
    background: linear-gradient(135deg, #fff5f5 0%, #ffffff 100%);
    position: relative;
}

.threadbar-row::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    /*width: 4px;*/
    background: linear-gradient(180deg, #c62f2f 0%, #8b1a1a 100%);
}

.threadbar-row :deep(.ant-breadcrumb) {
    font-size: 15px;
}

.threadbar-row :deep(.ant-breadcrumb-link) {
    color: #5e6d82;
    transition: color 0.3s ease;
}

.threadbar-row :deep(.ant-breadcrumb-link:hover) {
    color: #c62f2f;
}

.threadbar-row :deep(.ant-breadcrumb-separator) {
    color: #c62f2f;
}

.threadbar-row :deep(.ant-breadcrumb-item:last-child) {
    color: #c62f2f;
    font-weight: 600;
}
</style>