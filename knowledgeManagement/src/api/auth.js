import httpService from '../utils/request';
import { useAuthStore } from '../utils/auth';

/**
 * @description 用户登录
 * @param {Object} credentials - 登录凭据
 * @param {string} credentials.username - 用户名
 * @param {string} credentials.password - 密码
 * @returns {Promise<Object>} - 登录结果
 */
export const login = async (credentials) => {
  try {
    const resdata = await httpService.post('/api/auth/login', credentials);
    
    if (  resdata.code == 0 ){
        const authStore = useAuthStore();
        let data = resdata.data;
        console.log(data);

        // 更新认证状态
        authStore.setUser({
          data
        });
    }   
    return resdata.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || '登录失败');
  }
};

/**
 * @description 用户登出
 * @returns {Promise<void>}
 */
export const logout = async () => {
  const authStore = useAuthStore();
  authStore.logout();
};