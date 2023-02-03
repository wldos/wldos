/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.controller;

import com.wldos.base.controller.NoRepoController;
import com.wldos.common.enums.FileAccessPolicyEnum;
import com.wldos.support.storage.IStore;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 常量controller。
 *
 * @author 树悉猿
 * @date 2022/3/7
 * @version 1.0
 */
@RequestMapping("constant")
@RestController
public class ConstantController extends NoRepoController {
	/**
	 * 文件服务前缀
	 *
	 * @return 文件服务前缀
	 */
	@GetMapping("ossUrl")
	public String fetchOssUrl() {
		// 设置ossUrl，用于前端拼装文件类完整url
		return this.resJson.ok(IStore.KEY_OSS_URL, this.store.genOssUrl(FileAccessPolicyEnum.PUBLIC));
	}
}
