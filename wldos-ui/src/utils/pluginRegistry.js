/**
 * WLDOS 插件注册表
 * 提供统一的插件注册、查询、卸载能力，兼容旧 window[code] 写法
 */

// 初始化全局注册表
if (!window.WLDOSPlugins) {
  window.WLDOSPlugins = {
    _m: new Map(), // 内部存储 Map<code, module>
    
    /**
     * 注册插件模块
     * @param {string} code - 插件编码
     * @param {object} module - 插件模块对象
     */
    register(code, module) {
      if (!code || !module) {
        console.warn('[WLDOSPlugins] register: code or module is required');
        return;
      }
      this._m.set(code, module);
      if (process.env.NODE_ENV === 'development') {
        console.log(`[WLDOSPlugins] 插件 ${code} 已注册到注册表`);
      }
    },
    
    /**
     * 获取插件模块
     * @param {string} code - 插件编码
     * @returns {object|null} 插件模块对象，不存在返回 null
     */
    get(code) {
      if (!code) {
        return null;
      }
      return this._m.get(code) || null;
    },
    
    /**
     * 卸载插件模块
     * @param {string} code - 插件编码
     * @returns {boolean} 是否成功卸载
     */
    unload(code) {
      if (!code) {
        return false;
      }
      const deleted = this._m.delete(code);
      if (deleted && process.env.NODE_ENV === 'development') {
        console.log(`[WLDOSPlugins] 插件 ${code} 已从注册表卸载`);
      }
      return deleted;
    },
    
    /**
     * 检查插件是否已注册
     * @param {string} code - 插件编码
     * @returns {boolean}
     */
    has(code) {
      return this._m.has(code);
    },
    
    /**
     * 获取所有已注册的插件编码列表
     * @returns {string[]}
     */
    list() {
      return Array.from(this._m.keys());
    },
    
    /**
     * 清空所有注册的插件（谨慎使用）
     */
    clear() {
      this._m.clear();
      if (process.env.NODE_ENV === 'development') {
        console.log('[WLDOSPlugins] 注册表已清空');
      }
    }
  };
}

export default window.WLDOSPlugins;

