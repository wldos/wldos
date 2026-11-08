/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import { useCallback, useEffect, useState } from 'react';
import {
  DARK_MODE_CHANGE_EVENT,
  readDarkMode,
  toggleDarkMode,
} from '@/components/DarkTheme/darkMode';

export default function useDarkMode() {
  const [darkMode, setDarkMode] = useState(readDarkMode);

  useEffect(() => {
    const onChange = (event) => {
      setDarkMode(!!event.detail?.enabled);
    };
    window.addEventListener(DARK_MODE_CHANGE_EVENT, onChange);
    return () => window.removeEventListener(DARK_MODE_CHANGE_EVENT, onChange);
  }, []);

  const switchDarkMode = useCallback(() => {
    setDarkMode(toggleDarkMode());
  }, []);

  return [darkMode, switchDarkMode];
}
