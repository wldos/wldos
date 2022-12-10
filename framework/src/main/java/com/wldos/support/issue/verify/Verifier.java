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
 * @author 树悉猿
 * @date 2022/1/24
 * @version 1.0
 */
public interface Verifier {

	LicenseContent install(VerifyEnv verifyEnv, ConfigurableListableBeanFactory beanFactory);

	boolean check();

	LicenseContent preInstall(VerifyEnv verifyEnv, File file);

	LicenseContent reInstall(VerifyEnv verifyEnv);
}