import axios from 'axios';
import { message } from 'ant-design-vue';
import router from '../router';
import { useAuthStore } from '../utils/auth';

 
const httpService = axios.create({
 
  //timeout: 5000,
});


httpService.interceptors.request.use(
  (config) => {
    // 从请求配置中获取 loading 状态控制函数（组件级）
    if (config.loading?.show) {
      config.loading.show(true); // 开启加载状态
    }
    const authStore = useAuthStore()
    const isLoggedIn = () => authStore.isAuthenticated;
    const getToken   = () => authStore.user?.data.token;
    const getRyid = () => authStore.user?.data?.ryid;
    
    if (isLoggedIn()) {
      config.headers.Authorization = `Bearer ${getToken()}`;
      config.headers.ryid = getRyid();
    }
 
    return config;
  },
  (error) => {
    message.error('请求发送失败，请检查网络！');
    return Promise.reject(error);
  }
);

httpService.interceptors.response.use(
  (response) => {
    const { config, data } = response;
    // 关闭组件级加载状态
    if (config.loading?.show) {
      config.loading.show(false); // 关闭加载状态
    }
     
    // 业务逻辑判断：假设后端返回格式为 { code: 200, data: {}, msg: '成功' }
    if (data.code === 0) {
      return data; // 只返回业务数据，简化组件内使用
    } else {
      // 业务错误（如参数错误、数据不存在等）
       message.error(data.msg || '业务请求失败！');
      return Promise.reject(new Error(data.msg || '业务请求失败'));
    }
    return response;
  },
  (error) => {
      const { config } = error;
      // 关闭组件级加载状态
      if (config?.loading?.show) {
        config.loading.show(false);
      }

      if (error.response) {
      // 服务器返回错误状态码
      switch (error.response.status) {
        case 400:
          message.error('请求错误')
          break
        case 401:
          message.error('未授权，请重新登录')
          if (!['/login', '/register'].includes(router.currentRoute.value.path)) {
 
            const authStore = useAuthStore();
            authStore.logout();
            router.push('/login');
          }
          break
        case 403:
          message.error('拒绝访问')
          break
        case 404:
          message.error('请求地址出错')
          break
        case 500:
          message.error('服务器内部错误')
          break
        default:
          message.error(`连接错误 ${error.response.status}`)
      }
    } else if (error.request) {
      // 请求发出但没有收到响应
      message.error('网络连接异常，请检查网络')
    } else {
      // 其他错误
      message.error(error.message)
    }
    return Promise.reject(error);
  }
);

export default httpService;