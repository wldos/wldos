/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import { useEffect } from 'react';
import { readDarkMode, syncDarkTheme } from '@/components/DarkTheme/darkMode';

/** 应用根挂载时按 localStorage 同步 DarkReader，门户/后台/登录页共用 */
export default function DarkThemeBootstrap() {
  useEffect(() => {
    syncDarkTheme(readDarkMode());
  }, []);
  return null;
}
