/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

/**
 * TinyMCE 按需加载工具
 * 
 * 使用方法：
 * 1. 在组件中 import { loadTinyMCE } from '@/utils/loadTinyMCE';
 * 2. 在 useEffect 或 componentDidMount 中调用 await loadTinyMCE();
 * 3. 加载完成后再渲染 Editor 组件
 */

let loadingPromise = null;
let isLoaded = false;

/**
 * 动态加载 TinyMCE 脚本
 * @returns {Promise<void>}
 */
export function loadTinyMCE() {
  // 如果已经加载完成，直接返回
  if (isLoaded || window.tinymce) {
    isLoaded = true;
    return Promise.resolve();
  }

  // 如果正在加载中，返回同一个 Promise
  if (loadingPromise) {
    return loadingPromise;
  }

  // 开始加载
  loadingPromise = new Promise((resolve, reject) => {
    const script = document.createElement('script');
    script.src = '/tinymce/tinymce.min.js';
    script.async = true;
    
    script.onload = () => {
      isLoaded = true;
      loadingPromise = null;
      resolve();
    };
    
    script.onerror = (error) => {
      loadingPromise = null;
      reject(new Error('Failed to load TinyMCE: ' + error.message));
    };
    
    document.head.appendChild(script);
  });

  return loadingPromise;
}

/**
 * 检查 TinyMCE 是否已加载
 * @returns {boolean}
 */
export function isTinyMCELoaded() {
  return isLoaded || !!window.tinymce;
}

export default loadTinyMCE;
