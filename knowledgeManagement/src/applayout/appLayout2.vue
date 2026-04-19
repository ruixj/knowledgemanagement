<template>
  <a-layout style="min-height: 100vh">
    <a-layout-header class="header party-header">
      <div class="logo">
        <img src="/vite.svg" alt="Logo" />
        <span>华电党建知识库</span>
      </div>
      <!-- <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
      >
        <a-menu-item key="home">首页</a-menu-item>
        <a-menu-item key="knowledge">知识库</a-menu-item>
      </a-menu> -->

      <a-menu
              v-model:selectedKeys="selectedKeys"
              theme="dark"
              mode="horizontal"
              class="header-menu"
              :items="items"
      />
      <a-dropdown v-if="isLoggedIn" class="user-menu">
        <a class="ant-dropdown-link" @click.prevent>
          <UserOutlined />
          {{ username }}
        </a>
        <template #overlay>
          <a-menu>
            <a-menu-item key="profile">
              <UserOutlined /> 个人信息
            </a-menu-item>
            <a-menu-item key="logout" @click="handleLogout">
              <LogoutOutlined /> 退出登录
            </a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
    </a-layout-header>
    <NavBar   />
    <a-layout-content  class="main-content" style="padding: 0 0px">
      <div class="content">
        <router-view />
      </div>
    </a-layout-content>

    <a-layout-footer  class="party-footer" style="text-align: center">
      企业系统 ©2025 版权所有
    </a-layout-footer>
  </a-layout>
</template>

<script setup>
import NavBar from '../components/NavBar.vue'
import { ref, computed, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { UserOutlined, LogoutOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
 
import { useAuthStore } from '../utils/auth'
const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()
const selectedKeys = ref(['home'])
const matchedRoutes = route.matched
console.log(matchedRoutes)

const isLoggedIn = computed(() => {
  console.log('isAuthenticated:', authStore.isAuthenticated)
  return authStore.isAuthenticated
}
)

const username = computed(() =>{
  console.log('username:', authStore.user.data?.username)
  return authStore.user.data?.username || '游客'
} )

const items = [
          { key: '1', label: '首页', path: '/app' },
        //   { key: '2', label: '文件管理', path: '/app/fileManager' },
          { key: '2', label: '知识库', path: '/app/knowledge' }
        ].map(item => ({
          key: item.key,
          label: h('a', { onClick: () => router.push(item.path) }, item.label),
 }));

 

const handleLogout = () => {
  authStore.logout()
   
  message.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>

:deep(.ant-layout-header).party-header {
  background: linear-gradient(135deg, #c62f2f 0%, #8b1a1a 100%) !important;
  
}

/* 确保导航菜单在党建主题下正确显示 */
:deep(.ant-layout-header) .ant-menu-dark {
  background: transparent !important;
}

:deep(.ant-layout-header) .ant-menu-dark.ant-menu-horizontal {
  border-bottom: none !important;
}

:deep(.ant-layout-header) .ant-menu-dark .ant-menu-item-selected {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  color: white;
}

.logo img {
  width: 32px;
  height: 32px;
  margin-right: 8px;
}

.user-menu {
  color: white;
  cursor: pointer;
}

.content {
  background: #fff;
  padding: 24px;
  /*margin: 24px 0;*/
  min-height: 280px;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    height: auto;
    padding: 10px;
  }
  
  .logo {
    margin-bottom: 10px;
  }
  
  .ant-layout-content {
    padding: 0 20px !important;
  }
  
  .content {
    padding: 15px;
    /*margin: 15px 0;*/
  }
}
</style>