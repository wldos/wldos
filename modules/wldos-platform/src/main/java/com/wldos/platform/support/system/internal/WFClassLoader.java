/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.system.internal;

import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

/**
 * 框架内部classloader（勿动！！！）
 *
 * @author 元悉宇宙
 * @version 1.0
 * @date 2025/2/22
 */
final class WFClassLoader extends ClassLoader {
        private static final ClassLoader parent = Thread.currentThread().getContextClassLoader();
        private static WFClassLoader wldosUtilities;
        public static ClassLoader getInstance() {
            if (wldosUtilities == null)
                wldosUtilities = new WFClassLoader(parent);
            return wldosUtilities;
        }

        private WFClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Class<?> findClass(String name) {
            return load(name);
        }

        private Class<?> load(String className) {
            Class<?> newClass = null;
            InputStream byteCodeIn = null;
            InputStream loaderIn = null;
            ByteArrayOutputStream out = null;
            try {
                newClass = findLoadedClass(className);
                if (newClass != null)
                    return newClass;
                ClassPathResource loaderRes = new ClassPathResource(File.separator + "include" + File.separator + "loader.ser");
                byteCodeIn = loaderRes.getInputStream();
                out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = byteCodeIn.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
                byte[] pluginManagerByte = out.toByteArray();
                out.close();
                byteCodeIn.close();
                newClass = defineClass(className, pluginManagerByte, 0, pluginManagerByte.length);
                if (newClass == null)
                    newClass = findSystemClass(className);
                if (newClass != null)
                    resolveClass(newClass);
            }
            catch (Exception e) {
                System.out.println("加载框架失败，请不要更改框架内部文件");
            }
            return newClass;
        }

        private WFClassLoader() {}
    }