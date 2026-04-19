<template>
  <div class="register-container">
    <a-card title="注册" class="register-card">
      <a-form
        :model="formState"
        name="register"
        @finish="onFinish"
        @finishFailed="onFinishFailed"
      >
        <a-form-item
          name="username"
          :rules="[
            { required: true, message: '请输入用户名!' },
            { min: 4, message: '用户名至少4个字符!' }
          ]"
        >
          <a-input v-model:value="formState.username" placeholder="用户名" size="large">
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item
          name="password"
          :rules="[
            { required: true, message: '请输入密码!' },
            { min: 6, message: '密码至少6个字符!' }
          ]"
        >
          <a-input-password v-model:value="formState.password" placeholder="密码" size="large">
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item
          name="confirm"
          :rules="[
            { required: true, message: '请确认密码!' },
            { validator: validatePass }
          ]"
        >
          <a-input-password v-model:value="formState.confirm" placeholder="确认密码" size="large">
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item>
          <a-button 
            type="primary" 
            html-type="submit" 
            :loading="loading" 
            size="large"
            block
          >
            注册
          </a-button>
        </a-form-item>

        <a-form-item>
          <div class="register-login">
            已有账号? <a @click="goToLogin">立即登录</a>
          </div>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import api from '../utils/request'

const router = useRouter()
const loading = ref(false)

const formState = reactive({
  username: '',
  password: '',
  confirm: ''
})

const validatePass = async (rule, value) => {
  if (value !== formState.password) {
    throw new Error('两次输入的密码不一致!')
  }
}

const onFinish = async values => {
  loading.value = true
  try {
    await api.post('/auth/register', {
      username: values.username,
      password: values.password
    })
    message.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error('Register error:', error)
  } finally {
    loading.value = false
  }
}

const onFinishFailed = errorInfo => {
  console.log('Failed:', errorInfo)
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f0f2f5;
}

.register-card {
  width: 400px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.register-login {
  text-align: center;
}
</style>