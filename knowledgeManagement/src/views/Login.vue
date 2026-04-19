<template>
  <div class="login-container party-bg">
    <div class="login-content">
    <a-card title="登录" class="login-card party-card" :bordered="false">
        <template #cover>
          <div class="login-header">
            <div class="party-logo">
              <div class="logo-icon">
                <svg viewBox="0 0 1024 1024" width="40" height="40">
                  <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64z" fill="#c62f2f"/>
                  <path d="M512 140c205.4 0 372 166.6 372 372s-166.6 372-372 372S140 717.4 140 512 306.6 140 512 140z" fill="#fff"/>
                  <path d="M512 224c159.1 0 288 128.9 288 288s-128.9 288-288 288S224 671.1 224 512 352.9 224 512 224z" fill="#c62f2f"/>
                  <path d="M512 308c112.8 0 204 91.2 204 204s-91.2 204-204 204-204-91.2-204-204 91.2-204 204-204z" fill="#fff"/>
                  <path d="M512 392c66.3 0 120 53.7 120 120s-53.7 120-120 120-120-53.7-120-120 53.7-120 120-120z" fill="#c62f2f"/>
                </svg>
              </div>
              <h1>党建学习平台</h1>
            </div>
            <p class="login-subtitle">不忘初心，牢记使命</p>
          </div>
        </template>
      <a-form
        :model="formState"
        name="login"
        @finish="onFinish"
        @finishFailed="onFinishFailed"
      >
        <a-form-item
          name="username"
          :rules="[{ required: true, message: '请输入用户名!' }]"
        >
          <a-input :disabled="loading" v-model:value="formState.username" placeholder="用户名" size="large">
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item
          name="password"
          :rules="[{ required: true, message: '请输入密码!' }]"
        >
          <a-input-password  :disabled="loading" v-model:value="formState.password" placeholder="密码" size="large">
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item name="remember" :wrapper-col="{ offset: 0 }">
          <a-checkbox :disabled="loading" v-model:checked="formState.remember">记住我</a-checkbox>
        </a-form-item>

        <a-form-item>
          <a-button 
            type="primary" 
            html-type="submit" 
            :loading="loading" 
            :disabled="loading"
            size="large"
            block
          >
             {{ loading ? '登录中...' : '登录' }}
          </a-button>
        </a-form-item>
        
        <a-alert 
          v-if="errorMessage" 
          :message="errorMessage" 
          type="error" 
          show-icon 
          closable
        />

        <!-- <a-form-item>
          <div class="login-register">
            还没有账号? <a @click="goToRegister">立即注册</a>
          </div>
        </a-form-item> -->

        <!-- <div class="login-footer">
          <a-button type="link" @click="goToRegister" :disabled="loading">
            注册新账户
          </a-button>
          <a-divider type="vertical" />
          <a-button type="link" :disabled="loading">
            忘记密码
          </a-button>
        </div> -->
      </a-form>
    </a-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { login } from '../api/auth'

import { useBackendsStore } from '../state/appstate'
import { useAuthStore } from '../utils/auth'
import { storeToRefs } from 'pinia' 

const backendsStore = useBackendsStore()
const { endpoints } = storeToRefs(backendsStore)
console.log(endpoints.value.baseUrl)
 
const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
 
const errorMessage = ref('')

const formState = reactive({
  username: '',
  password: '',
  remember: authStore.rememberMe
})

const onFinish = async values => {
  loading.value = true
   errorMessage.value = ''
  try {
     const response =  await login( values)
     console.log(response)
     authStore.setRememberMe(values.remember)
     message.success('登录成功')
     router.push({name:"app"})
  } catch (error) {
    if (error.response && error.response.data && error.response.data.message) {
      errorMessage.value = error.response.data.message
    } else {
      errorMessage.value = '登录失败，请检查网络连接后重试'
    }
    console.error('Login error:', error)
  } finally {
    loading.value = false
  }
}

const onFinishFailed = errorInfo => {
  console.log('Failed:', errorInfo)
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  /*background: #f0f2f5;*/
}
.login-content {
  width: 100%;
  max-width: 400px;
}

.login-card {
  width: 400px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
.login-header {
  text-align: center;
  padding: 20px 0;
  color: #c62f2f;
}

.party-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
}

.party-logo h1 {
  margin: 0 0 0 10px;
  font-size: 24px;
  font-weight: bold;
}

.login-subtitle {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.login-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 16px;
}
.login-register {
  text-align: center;
}
</style>