<template>
  <a-layout-header class="header">
    <div class="logo">
      <img src="../assets/logo.png" alt="Logo" />
      <span>企业管理系统</span>
    </div>
    <a-menu
      v-model:selectedKeys="selectedKeys"
      theme="dark"
      mode="horizontal"
      :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="home">
        <router-link to="/">首页</router-link>
      </a-menu-item>
      <a-menu-item key="knowledge">
        <router-link to="/knowledge">知识库</router-link>
      </a-menu-item>
    </a-menu>
    <div class="user-info">
      <a-dropdown>
        <a class="ant-dropdown-link" @click.prevent>
          <a-avatar style="backgroundColor: #1890ff">
            {{ userInitial }}
          </a-avatar>
          <span style="margin-left: 8px">{{ username }}</span>
        </a>
        <template #overlay>
          <a-menu>
            <a-menu-item key="profile">
              <user-outlined />
              个人信息
            </a-menu-item>
            <a-menu-divider />
            <a-menu-item key="logout" @click="handleLogout">
              <logout-outlined />
              退出登录
            </a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
    </div>
  </a-layout-header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { UserOutlined, LogoutOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

const router = useRouter()
const selectedKeys = ref(['home'])

const username = computed(() => {
  const userInfo = localStorage.getItem('userInfo')
  return userInfo ? JSON.parse(userInfo).username : '用户'
})

const userInitial = computed(() => {
  return username.value.charAt(0).toUpperCase()
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  message.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  color: white;
  font-weight: bold;
  font-size: 18px;
}

.logo img {
  height: 32px;
  margin-right: 10px;
}

.user-info {
  color: white;
}
</style>