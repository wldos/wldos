/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue.verify;

import java.io.File;

import de.schlichtherle.license.LicenseContent;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * license验证器。
 *
 * @author 树悉猿
 * @date 2022/1/24
 * @version 1.0
 */
public interface Verifier {

	LicenseContent install(VerifyEnv verifyEnv, ConfigurableListableBeanFactory beanFactory);

	/**
	 * 校验License证书
	 *
	 * @return boolean
	 */
	boolean check();

	/**
	 * 预安装license，返回空表示安装失败
	 *
	 * @param verifyEnv 校验环境
	 * @param file license文件
	 * @return 安装结果
	 */
	LicenseContent preInstall(VerifyEnv verifyEnv, File file);

	/**
	 * 运行时更新license
	 *
	 * @param verifyEnv license环境变量
	 * @return license信息
	 */
	LicenseContent reInstall(VerifyEnv verifyEnv);
}