import { createI18n } from 'vue-i18n'
import zhCN from './lang/zh_CN'
import enUS from './lang/en_US'
 

// 导入antdv的语言包
import antZhCN from 'ant-design-vue/es/locale/zh_CN'
import antEnUS from 'ant-design-vue/es/locale/en_US'

// 合并自定义语言和antdv语言包
const messages = {
  'zh-CN': {
    ...zhCN,
    ...antZhCN
  },
  'en-US': {
    ...enUS,
    ...antEnUS
  }
}

const i18n = createI18n({
  legacy: false, // 使用Composition API时需要设置为false
  globalInjection: true, // 全局注入$t函数
  locale: localStorage.getItem('language') || 'zh-CN', // 默认语言
  messages
})

 

export default i18n
