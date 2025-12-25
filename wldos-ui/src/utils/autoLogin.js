import { getAuthority } from './authority';
import { queryCurrent } from '@/services/user';
import { redirect } from './utils';

/**
 * 自动登录检测工具
 */
export class AutoLoginManager {
  static instance = null;
  
  constructor() {
    if (AutoLoginManager.instance) {
      return AutoLoginManager.instance;
    }
    AutoLoginManager.instance = this;
  }
  
  /**
   * 检查是否有有效的登录状态
   */
  async checkLoginStatus() {
    try {
      const authority = getAuthority();
      
      // 如果没有authority或为guest，说明未登录
      if (!authority || authority === 'guest') {
        return false;
      }
      
      // 尝试获取当前用户信息，验证token是否有效
      const response = await queryCurrent();
      if (response?.data?.userInfo?.id) {
        return true;
      }
      
      return false;
    } catch (error) {
      console.log('AutoLogin check failed:', error);
      return false;
    }
  }
  
  /**
   * 自动登录处理
   */
  async handleAutoLogin() {
    const isLoggedIn = await this.checkLoginStatus();
    
    if (isLoggedIn) {
      // 用户已登录，重定向到首页
      redirect();
      return true;
    }
    
    return false;
  }
  
  /**
   * 获取用户autoLogin偏好
   */
  getUserAutoLoginPreference() {
    try {
      const preference = localStorage.getItem('wldos-autoLogin-preference');
      return preference ? JSON.parse(preference) : false;
    } catch (error) {
      console.log('Failed to get autoLogin preference:', error);
      return false;
    }
  }
  
  /**
   * 设置用户autoLogin偏好
   */
  setUserAutoLoginPreference(value) {
    try {
      localStorage.setItem('wldos-autoLogin-preference', JSON.stringify(value));
    } catch (error) {
      console.log('Failed to set autoLogin preference:', error);
    }
  }
}

// 导出单例实例
export const autoLoginManager = new AutoLoginManager();
