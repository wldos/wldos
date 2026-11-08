/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import updateDarkTheme from '@/components/DarkTheme/UpdateTheme';

export const DARK_MODE_STORAGE_KEY = 'darkMode';

export const DARK_MODE_CHANGE_EVENT = 'wldos:dark-mode-change';

export function readDarkMode() {
  if (typeof window === 'undefined') {
    return false;
  }
  return localStorage.getItem(DARK_MODE_STORAGE_KEY) === '1';
}

export function syncDarkTheme(enabled) {
  return updateDarkTheme(!!enabled);
}

export function setDarkModeEnabled(enabled) {
  if (typeof window === 'undefined') {
    return;
  }
  localStorage.setItem(DARK_MODE_STORAGE_KEY, enabled ? '1' : '0');
  syncDarkTheme(enabled);
  window.dispatchEvent(
    new CustomEvent(DARK_MODE_CHANGE_EVENT, { detail: { enabled: !!enabled } }),
  );
}

/** @returns {boolean} 切换后的暗黑状态 */
export function toggleDarkMode() {
  setDarkModeEnabled(!readDarkMode());
  return readDarkMode();
}
