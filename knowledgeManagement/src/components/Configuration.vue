<template>
    <div class="config-container">
      <a-card>
        <!-- 上部分：标题和介绍 -->
        <div class="header-section">
          <h1 class="header-title">系统配置中心</h1>
          <p class="header-description">
            在这里您可以对系统的各项功能进行个性化配置。配置项按照功能模块进行了分组，方便您快速找到需要调整的设置。
          </p>
        </div>

        <!-- 下部分：标签页 -->
        <a-tabs v-model:activeKey="activeTab" type="card">
          <!-- 基本设置标签页 -->
          <a-tab-pane key="basic" tab="基本设置">
            <div class="form-section">
              <h3 class="form-group-header">常规设置</h3>
              <div class="form-row">
                <div class="form-item">
                  <a-form-item label="系统名称">
                    <a-input v-model:value="formData.basic.systemName" placeholder="请输入系统名称" />
                  </a-form-item>
                </div>
                <div class="form-item">
                  <a-form-item label="系统语言">
                    <a-select v-model:value="formData.basic.language" placeholder="请选择系统语言">
                      <a-select-option value="zh-CN">中文(简体)</a-select-option>
                      <a-select-option value="en-US">English</a-select-option>
                      <a-select-option value="ja-JP">日本語</a-select-option>
                    </a-select>
                  </a-form-item>
                </div>
              </div>
              <div class="form-row">
                <div class="form-item">
                  <a-form-item label="时区设置">
                    <a-select v-model:value="formData.basic.timezone" placeholder="请选择时区">
                      <a-select-option value="UTC+8">UTC+8 (北京时间)</a-select-option>
                      <a-select-option value="UTC+0">UTC+0 (格林威治时间)</a-select-option>
                      <a-select-option value="UTC-5">UTC-5 (美国东部时间)</a-select-option>
                    </a-select>
                  </a-form-item>
                </div>
                <div class="form-item">
                  <a-form-item label="日期格式">
                    <a-select v-model:value="formData.basic.dateFormat" placeholder="请选择日期格式">
                      <a-select-option value="YYYY-MM-DD">YYYY-MM-DD</a-select-option>
                      <a-select-option value="MM/DD/YYYY">MM/DD/YYYY</a-select-option>
                      <a-select-option value="DD/MM/YYYY">DD/MM/YYYY</a-select-option>
                    </a-select>
                  </a-form-item>
                </div>
              </div>
            </div>

            <div class="form-section">
              <h3 class="form-group-header">显示设置</h3>
              <div class="form-row">
                <div class="form-item">
                  <a-form-item label="主题模式">
                    <a-radio-group v-model:value="formData.basic.theme">
                      <a-radio value="light">浅色模式</a-radio>
                      <a-radio value="dark">深色模式</a-radio>
                      <a-radio value="auto">跟随系统</a-radio>
                    </a-radio-group>
                  </a-form-item>
                </div>
                <div class="form-item">
                  <a-form-item label="页面布局">
                    <a-select v-model:value="formData.basic.layout" placeholder="请选择页面布局">
                      <a-select-option value="side">侧边导航</a-select-option>
                      <a-select-option value="top">顶部导航</a-select-option>
                      <a-select-option value="mix">混合导航</a-select-option>
                    </a-select>
                  </a-form-item>
                </div>
              </div>
              <div class="form-row">
                <div class="form-item form-item-full">
                  <a-form-item label="首页内容">
                    <a-textarea 
                      v-model:value="formData.basic.homeContent" 
                      placeholder="请输入首页显示内容"
                      :rows="4"
                    />
                  </a-form-item>
                </div>
              </div>
            </div>
          </a-tab-pane>

          <!-- 安全设置标签页 -->
          <a-tab-pane key="security" tab="安全设置">
            <div class="form-section">
              <h3 class="form-group-header">账户安全</h3>
              <div class="form-row">
                <div class="form-item">
                  <a-form-item label="密码强度">
                    <a-select v-model:value="formData.security.passwordStrength" placeholder="请选择密码强度要求">
                      <a-select-option value="low">低强度</a-select-option>
                      <a-select-option value="medium">中强度</a-select-option>
                      <a-select-option value="high">高强度</a-select-option>
                    </a-select>
                  </a-form-item>
                </div>
                <div class="form-item">
                  <a-form-item label="密码有效期(天)">
                    <a-input-number 
                      v-model:value="formData.security.passwordExpiry" 
                      :min="30" 
                      :max="365" 
                      style="width: 100%"
                    />
                  </a-form-item>
                </div>
              </div>
              <div class="form-row">
                <div class="form-item">
                  <a-form-item label="登录失败锁定">
                    <a-switch v-model:checked="formData.security.loginLock" />
                  </a-form-item>
                </div>
                <div class="form-item">
                  <a-form-item label="失败次数阈值">
                    <a-input-number 
                      v-model:value="formData.security.failThreshold" 
                      :min="3" 
                      :max="10" 
                      :disabled="!formData.security.loginLock"
                      style="width: 100%"
                    />
                  </a-form-item>
                </div>
              </div>
            </div>

            <div class="form-section">
              <h3 class="form-group-header">会话管理</h3>
              <div class="form-row">
                <div class="form-item">
                  <a-form-item label="会话超时(分钟)">
                    <a-input-number 
                      v-model:value="formData.security.sessionTimeout" 
                      :min="15" 
                      :max="480" 
                      style="width: 100%"
                    />
                  </a-form-item>
                </div>
                <div class="form-item">
                  <a-form-item label="同时登录限制">
                    <a-switch v-model:checked="formData.security.singleLogin" />
                  </a-form-item>
                </div>
              </div>
              <div class="form-row">
                <div class="form-item form-item-full">
                  <a-form-item label="登录IP白名单">
                    <a-textarea 
                      v-model:value="formData.security.ipWhitelist" 
                      placeholder="请输入允许登录的IP地址，多个IP用逗号分隔"
                      :rows="3"
                    />
                  </a-form-item>
                </div>
              </div>
            </div>
          </a-tab-pane>

          <!-- 通知设置标签页 -->
          <a-tab-pane key="notification" tab="通知设置">
            <div class="form-section">
              <h3 class="form-group-header">邮件通知</h3>
              <div class="form-row">
                <div class="form-item">
                  <a-form-item label="启用邮件通知">
                    <a-switch v-model:checked="formData.notification.emailEnabled" />
                  </a-form-item>
                </div>
                <div class="form-item">
                  <a-form-item label="SMTP服务器">
                    <a-input 
                      v-model:value="formData.notification.smtpServer" 
                      placeholder="smtp.example.com"
                      :disabled="!formData.notification.emailEnabled"
                    />
                  </a-form-item>
                </div>
              </div>
              <div class="form-row">
                <div class="form-item">
                  <a-form-item label="SMTP端口">
                    <a-input-number 
                      v-model:value="formData.notification.smtpPort" 
                      :min="1" 
                      :max="65535" 
                      :disabled="!formData.notification.emailEnabled"
                      style="width: 100%"
                    />
                  </a-form-item>
                </div>
                <div class="form-item">
                  <a-form-item label="SSL加密">
                    <a-switch 
                      v-model:checked="formData.notification.sslEnabled" 
                      :disabled="!formData.notification.emailEnabled"
                    />
                  </a-form-item>
                </div>
              </div>
            </div>

            <div class="form-section">
              <h3 class="form-group-header">系统通知</h3>
              <div class="form-row">
                <div class="form-item">
                  <a-form-item label="新用户注册">
                    <a-switch v-model:checked="formData.notification.newUserAlert" />
                  </a-form-item>
                </div>
                <div class="form-item">
                  <a-form-item label="系统更新">
                    <a-switch v-model:checked="formData.notification.systemUpdateAlert" />
                  </a-form-item>
                </div>
              </div>
              <div class="form-row">
                <div class="form-item">
                  <a-form-item label="错误日志">
                    <a-switch v-model:checked="formData.notification.errorLogAlert" />
                  </a-form-item>
                </div>
                <div class="form-item">
                  <a-form-item label="安全事件">
                    <a-switch v-model:checked="formData.notification.securityEventAlert" />
                  </a-form-item>
                </div>
              </div>
            </div>
          </a-tab-pane>

          <!-- 高级设置标签页 -->
          <a-tab-pane key="advanced" tab="高级设置">
            <div class="form-section">
              <h3 class="form-group-header">性能设置</h3>
              <div class="form-row">
                <div class="form-item">
                  <a-form-item label="缓存策略">
                    <a-select v-model:value="formData.advanced.cacheStrategy" placeholder="请选择缓存策略">
                      <a-select-option value="memory">内存缓存</a-select-option>
                      <a-select-option value="redis">Redis缓存</a-select-option>
                      <a-select-option value="none">不使用缓存</a-select-option>
                    </a-select>
                  </a-form-item>
                </div>
                <div class="form-item">
                  <a-form-item label="缓存过期时间(分钟)">
                    <a-input-number 
                      v-model:value="formData.advanced.cacheExpiry" 
                      :min="5" 
                      :max="1440" 
                      style="width: 100%"
                    />
                  </a-form-item>
                </div>
              </div>
              <div class="form-row">
                <div class="form-item">
                  <a-form-item label="API请求限制">
                    <a-input-number 
                      v-model:value="formData.advanced.apiRateLimit" 
                      :min="10" 
                      :max="1000" 
                      style="width: 100%"
                    />
                  </a-form-item>
                </div>
                <div class="form-item">
                  <a-form-item label="请求超时(秒)">
                    <a-input-number 
                      v-model:value="formData.advanced.requestTimeout" 
                      :min="5" 
                      :max="300" 
                      style="width: 100%"
                    />
                  </a-form-item>
                </div>
              </div>
            </div>

            <div class="form-section">
              <h3 class="form-group-header">日志设置</h3>
              <div class="form-row">
                <div class="form-item">
                  <a-form-item label="日志级别">
                    <a-select v-model:value="formData.advanced.logLevel" placeholder="请选择日志级别">
                      <a-select-option value="debug">Debug</a-select-option>
                      <a-select-option value="info">Info</a-select-option>
                      <a-select-option value="warn">Warn</a-select-option>
                      <a-select-option value="error">Error</a-select-option>
                    </a-select>
                  </a-form-item>
                </div>
                <div class="form-item">
                  <a-form-item label="日志保留天数">
                    <a-input-number 
                      v-model:value="formData.advanced.logRetentionDays" 
                      :min="7" 
                      :max="365" 
                      style="width: 100%"
                    />
                  </a-form-item>
                </div>
              </div>
              <div class="form-row">
                <div class="form-item form-item-full">
                  <a-form-item label="日志文件路径">
                    <a-input 
                      v-model:value="formData.advanced.logPath" 
                      placeholder="/var/log/myapp"
                    />
                  </a-form-item>
                </div>
              </div>
            </div>
          </a-tab-pane>
        </a-tabs>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <a-button @click="handleReset">重置</a-button>
          <a-button type="primary" @click="handleSave">保存设置</a-button>
        </div>
      </a-card>
    </div> 
</template>
<script setup>
import { createApp, ref, reactive, computed, watch } from 'vue';
import { message } from 'ant-design-vue'
    
// 默认配置数据
const defaultFormData = {
    basic: {
        systemName: '我的应用系统',
        language: 'zh-CN',
        timezone: 'UTC+8',
        dateFormat: 'YYYY-MM-DD',
        theme: 'light',
        layout: 'side',
        homeContent: '欢迎使用我们的系统，这里将展示系统的最新动态和重要通知。'
    },
    security: {
        passwordStrength: 'medium',
        passwordExpiry: 90,
        loginLock: true,
        failThreshold: 5,
        sessionTimeout: 30,
        singleLogin: false,
        ipWhitelist: ''
    },
    notification: {
        emailEnabled: true,
        smtpServer: 'smtp.example.com',
        smtpPort: 587,
        sslEnabled: true,
        newUserAlert: true,
        systemUpdateAlert: true,
        errorLogAlert: false,
        securityEventAlert: true
    },
    advanced: {
        cacheStrategy: 'memory',
        cacheExpiry: 60,
        apiRateLimit: 100,
        requestTimeout: 30,
        logLevel: 'info',
        logRetentionDays: 30,
        logPath: '/var/log/myapp'
    }
};

// 响应式数据
const activeTab = ref('basic');
const formData = reactive(JSON.parse(JSON.stringify(defaultFormData)));

// 计算属性 - 是否有未保存的更改
const hasUnsavedChanges = ref(false);

// 监听表单数据变化
watch(formData, () => {
    hasUnsavedChanges.value = true;
}, { deep: true });

// 方法
const handleSave = () => {
    // 在实际应用中，这里应该发送API请求保存配置
    message.success('配置保存成功！');
    hasUnsavedChanges.value = false;
    console.log('保存的配置数据：', JSON.stringify(formData, null, 2));
};

const handleReset = () => {
    // 重置表单数据
    Object.keys(defaultFormData).forEach(key => {
       Object.assign(formData[key], defaultFormData[key]);
    });
    message.info('配置已重置为默认值！');
    hasUnsavedChanges.value = false;
};
      
</script>
<style>
    .config-container {
      max-width: 1200px;
      margin: 20px auto;
      padding: 0 20px;
    }
    .header-section {
      margin-bottom: 24px;
      padding-bottom: 16px;
      border-bottom: 1px solid #f0f0f0;
    }
    .header-title {
      font-size: 24px;
      font-weight: 600;
      margin-bottom: 8px;
      color: #262626;
    }
    .header-description {
      color: #595959;
      font-size: 14px;
      line-height: 1.6;
    }
    .form-group-header {
      font-size: 16px;
      font-weight: 500;
      margin-bottom: 16px;
      color: #262626;
    }
    .form-section {
      margin-bottom: 24px;
    }
    .form-row {
      display: flex;
      flex-wrap: wrap;
      margin: 0 -8px;
    }
    .form-item {
      flex: 1;
      min-width: 250px;
      padding: 0 8px;
      margin-bottom: 16px;
    }
    .form-item-full {
      flex: 0 0 100%;
    }
    .action-buttons {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
      margin-top: 24px;
      padding-top: 16px;
      border-top: 1px solid #f0f0f0;
    }
    @media (max-width: 768px) {
      .form-row {
        flex-direction: column;
      }
      .form-item {
        min-width: 100%;
      }
    }
</style>