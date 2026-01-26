/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.system.internal;

import com.wldos.framework.support.plugins.WLDOSPlugin;
import com.wldos.gateway.plugins.IPluginApiGateway;
import com.wldos.gateway.plugins.impl.DefaultPluginApiGatewayImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认插件管理器实现（开源版本）
 * 当 Agent 模块不存在时使用此默认实现
 * 
 * 开源版本支持基础插件生态：
 * - ✅ 支持：基础插件加载、插件运行、插件API路由
 * - ❌ 不支持：热插拔、插件隔离、Spring上下文隔离、Hook系统等高级功能
 *
 * 注意：要实现完整的插件生态（热插拔、隔离等），需要安装商业版的 agent 模块
 *
 * @author 元悉宇宙
 * @version 1.0
 * @date 2026-01-10
 */
@Slf4j
class DefaultPluginManager implements IPlugin {
    
    private boolean initialized = false;

    // 插件实例缓存（开源版本：不支持热插拔）
    private final Map<String, WLDOSPlugin> pluginInstances = new ConcurrentHashMap<>();

    // 插件类加载器缓存（开源版本：不支持隔离，但需要缓存以便后续使用）
    private final Map<String, URLClassLoader> pluginClassLoaders = new ConcurrentHashMap<>();

    /**
     * 设置类加载器（开源版本：基础实现）
     * 注意：开源版本不支持插件隔离，所有插件共享主应用的类加载器
     */
    @Override
    public void setClassLoader(URLClassLoader urlClassLoader, ConfigurableEnvironment env) {
        // 开源版本：基础实现
        // 不支持插件隔离，插件使用主应用的类加载器
        // 商业版 agent 模块会提供插件隔离的类加载器管理
    }

    /**
     * 注册插件管理器（开源版本：基础实现）
     * 从数据库读取已安装的插件并注册到 PluginApiGateway
     * 注意：开源版本不支持插件Spring上下文隔离，插件Bean需要手动注册到主应用上下文
     */
    @Override
    public void register(ConfigurableApplicationContext context) {
        if (initialized) {
            return;
        }

        try {
            // 尝试获取 PluginApiGateway（开源版本的默认实现）
            IPluginApiGateway pluginApiGateway = null;
            try {
                pluginApiGateway = context.getBean(IPluginApiGateway.class);
            } catch (Exception e) {
                log.debug("无法获取 IPluginApiGateway，跳过插件注册");
                return;
            }

            // 尝试从数据库读取已安装的插件列表
            // 注意：这里假设插件表存在，如果不存在会静默失败
            try {
                JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);

                // 查询已安装且启用的插件（包含主类和路径信息）
                // 注意：这里使用简化的SQL，假设表结构存在
                String sql = "SELECT plugin_code, main_class, plugin_path FROM wo_plugin_registry " +
                            "WHERE delete_flag = 'normal' AND plugin_status = 'ENABLED'";

                List<Map<String, Object>> plugins = jdbcTemplate.queryForList(sql);

                int loadedCount = 0;
                for (Map<String, Object> plugin : plugins) {
                    String pluginCode = (String) plugin.get("plugin_code");
                    String mainClass = (String) plugin.get("main_class");
                    String pluginPath = (String) plugin.get("plugin_path");

                    if (pluginCode != null && !pluginCode.isEmpty()) {
                        try {
                            // 加载并启动插件
                            WLDOSPlugin pluginInstance = loadAndStartPlugin(
                                context, pluginCode, mainClass, pluginPath);

                            if (pluginInstance != null) {
                                // 注册插件代码到 PluginApiGateway
                                if (pluginApiGateway instanceof DefaultPluginApiGatewayImpl) {
                                    ((DefaultPluginApiGatewayImpl) pluginApiGateway).registerPluginCode(pluginCode);
                                }
                                loadedCount++;
                                log.info("插件加载成功（开源版本，不支持热插拔）: pluginCode={}", pluginCode);
                            }
                        } catch (Exception e) {
                            log.warn("插件加载失败: pluginCode={}, error={}", pluginCode, e.getMessage());
                            // 继续加载其他插件，不中断流程
                        }
                    }
                }

                log.info("开源版本插件加载完成，共加载 {} 个插件（不支持热插拔）", loadedCount);

            } catch (Exception e) {
                // 数据库表不存在或查询失败，静默处理
                // 这表示可能没有安装插件，或者表结构不同
                log.debug("无法从数据库读取插件列表，可能未安装插件或表结构不同: {}", e.getMessage());
            }

            initialized = true;

        } catch (Exception e) {
            log.warn("开源版本插件注册失败: {}", e.getMessage());
            // 不抛出异常，避免影响应用启动
        }
    }

    /**
     * 启动插件管理器（开源版本：基础实现）
     * 注意：开源版本不支持插件热插拔，插件需要在应用启动时手动加载
     * 实际的插件实例加载和启动已经在 register() 阶段完成
     */
    @Override
    public void boot(ConfigurableApplicationContext context) {
        // 开源版本：基础实现
        // 插件已经在 register() 阶段加载和启动，这里可以添加启动后的额外处理逻辑
        log.debug("开源版本插件管理器启动完成（不支持热插拔）");
    }

    /**
     * 加载并启动插件（开源版本：基础实现）
     *
     * @param context Spring应用上下文
     * @param pluginCode 插件代码
     * @param mainClass 插件主类
     * @param pluginPath 插件路径
     * @return 插件实例
     */
    private WLDOSPlugin loadAndStartPlugin(ConfigurableApplicationContext context,
                                          String pluginCode, String mainClass, String pluginPath) {
        try {
            // 1. 创建插件类加载器（开源版本：不支持隔离，使用主应用类加载器作为父类加载器）
            URLClassLoader classLoader = createPluginClassLoader(pluginPath, context.getClassLoader());
            pluginClassLoaders.put(pluginCode, classLoader);

            // 2. 加载插件主类
            if (mainClass == null || mainClass.trim().isEmpty()) {
                log.warn("插件主类未设置: pluginCode={}", pluginCode);
                return null;
            }

            Class<?> pluginClass = classLoader.loadClass(mainClass);

            // 3. 实例化插件
            WLDOSPlugin pluginInstance = (WLDOSPlugin) pluginClass.getDeclaredConstructor().newInstance();

            // 4. 设置ApplicationContext（开源版本：使用主应用上下文，不支持隔离）
            pluginInstance.setApplicationContext(context);

            // 5. 初始化插件
            pluginInstance.init();

            // 6. 启动插件
            pluginInstance.start();

            // 7. 注册插件Bean到Spring上下文（开源版本：注册到主应用上下文）
            registerPluginBeans(context, pluginInstance, pluginCode);

            // 8. 缓存插件实例
            pluginInstances.put(pluginCode, pluginInstance);

            return pluginInstance;

        } catch (Exception e) {
            log.error("加载插件失败: pluginCode={}, error={}", pluginCode, e.getMessage(), e);
            // 清理已创建的类加载器
            URLClassLoader classLoader = pluginClassLoaders.remove(pluginCode);
            if (classLoader != null) {
                try {
                    classLoader.close();
                } catch (Exception ex) {
                    // 忽略关闭异常
                }
            }
            return null;
        }
    }

    /**
     * 创建插件类加载器（开源版本：基础实现）
     * 注意：开源版本不支持插件隔离，使用主应用类加载器作为父类加载器
     */
    private URLClassLoader createPluginClassLoader(String pluginPath, ClassLoader parentClassLoader) {
        try {
            List<URL> urls = new ArrayList<>();

            // 如果插件路径为空，返回主应用类加载器
            if (pluginPath == null || pluginPath.trim().isEmpty()) {
                log.warn("插件路径为空，使用主应用类加载器");
                return new URLClassLoader(new URL[0], parentClassLoader);
            }

            // 解析插件路径，查找jar文件
            File pluginDir = new File(pluginPath);
            if (!pluginDir.exists() || !pluginDir.isDirectory()) {
                log.warn("插件目录不存在: {}", pluginPath);
                return new URLClassLoader(new URL[0], parentClassLoader);
            }

            // 查找插件目录下的所有jar文件
            findJarFiles(pluginDir, urls);

            if (urls.isEmpty()) {
                log.warn("插件目录中未找到jar文件: {}", pluginPath);
                return new URLClassLoader(new URL[0], parentClassLoader);
            }

            return new URLClassLoader(urls.toArray(new URL[0]), parentClassLoader);

        } catch (Exception e) {
            log.error("创建插件类加载器失败: pluginPath={}, error={}", pluginPath, e.getMessage());
            return new URLClassLoader(new URL[0], parentClassLoader);
        }
    }

    /**
     * 递归查找jar文件
     */
    private void findJarFiles(File dir, List<URL> urls) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                findJarFiles(file, urls);
            } else if (file.getName().endsWith(".jar")) {
                try {
                    urls.add(file.toURI().toURL());
                } catch (Exception e) {
                    log.warn("无法添加jar文件到类路径: {}, error={}", file.getAbsolutePath(), e.getMessage());
                }
            }
        }
    }

    /**
     * 注册插件Bean到Spring上下文（开源版本：基础实现）
     * 注意：开源版本不支持插件Spring上下文隔离，插件Bean注册到主应用上下文
     */
    private void registerPluginBeans(ConfigurableApplicationContext context,
                                     WLDOSPlugin plugin, String pluginCode) {
        try {
            // 开源版本：简化实现，只注册插件主实例
            // 商业版 agent 模块会提供完整的Bean扫描和注册功能

            ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();

            // 注册插件实例为Bean（使用插件代码作为Bean名称）
            String beanName = "plugin_" + pluginCode;
            if (!beanFactory.containsBean(beanName)) {
                beanFactory.registerSingleton(beanName, plugin);
                log.debug("注册插件Bean: beanName={}", beanName);
            }

        } catch (Exception e) {
            log.warn("注册插件Bean失败: pluginCode={}, error={}", pluginCode, e.getMessage());
            // 不抛出异常，允许插件继续运行
        }
    }
}
