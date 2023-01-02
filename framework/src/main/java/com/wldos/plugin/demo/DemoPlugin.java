/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.plugin.demo;

import com.wldos.plugin.PluginBootStrap;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * 示例插件。
 * 本插件模块是系统启动时自动加载到spring容器的框架扩展，与应用管理中的插件不同，后者是运行时进行的扩展可以卸载，前者不能卸载属于框架的扩展范畴，后者属于业务的扩展范畴
 * 业务端应用管理对插件的支撑在其他模块另行实现，可能继承此模块，也可能独立实现。
 * 约定插件存放路径：resources/include/pluginName.jar (业务端插件路径：resources/include/pluginName/*(pluginName.jar+其他文件))
 * 约定插件主类: pluginName.class
 * 约定插件配置：plugin.properties
 * 约定插件名称：pluginName
 *
 * @author 树悉猿
 * @date 2022/12/13
 * @version 1.0
 */
public class DemoPlugin implements PluginBootStrap  {

	@Override
	public void boot(ConfigurableApplicationContext context) {
		System.out.println("wldos");
	}
}