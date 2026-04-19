<template>
  <a-config-provider :locale="locale" :theme="theme">
    <router-view />
  </a-config-provider>
</template>
<script>
import { defineComponent ,computed} from 'vue'
import { theme } from 'ant-design-vue'
 
import { useI18n } from 'vue-i18n'
import zhCN from 'ant-design-vue/es/locale/zh_CN'
import enUS from 'ant-design-vue/es/locale/en_US'

export default defineComponent({
  name: 'App',
  setup() {
    const { locale } = useI18n();
    console.log(locale.value  )
    // 党建主题 - 红色主题
    const themeConfig = computed(() => ({
      algorithm: theme.defaultAlgorithm,
      token: {
        colorPrimary: '#c62f2f', // 主色调 - 红色
        colorSuccess: '#52c41a',
        colorWarning: '#faad14',
        colorError: '#f5222d',
        colorInfo: '#1890ff',
        colorBgBase: '#fff',
        colorTextBase: '#000',
        borderRadius: 6,
        fontSize: 14,
        sizeStep: 4,
        sizeUnit: 4
      }
    }))
    return {
      locale:  locale === 'zh-CN' ? zhCN : enUS,
      theme:themeConfig
    };
  },
})
</script>
<style >

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans', sans-serif;
  background-color: #f5f5f5;
}

.party-bg {
  background: linear-gradient(135deg, #c62f2f 0%, #8b1a1a 100%);
}

.party-card {
  box-shadow: 0 4px 12px rgba(198, 47, 47, 0.15);
  border: 1px solid #f0f0f0;
}

.party-header {
  background: linear-gradient(135deg, #c62f2f 0%, #8b1a1a 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.party-footer {
  background-color: #2a1215;
  color: rgba(255, 255, 255, 0.85);
}
</style>
 
