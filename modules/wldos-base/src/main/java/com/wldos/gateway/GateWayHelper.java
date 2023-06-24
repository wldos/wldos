/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.gateway;

import java.util.List;
import java.util.Objects;

import com.wldos.common.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 网关操作。
 *
 * @author 树悉猿
 * @date 2023/4/8
 * @version 1.0
 */
@Slf4j
public final class GateWayHelper {
	public GateWayHelper() {throw new IllegalStateException("Utility class");}

	public static boolean isMatchUri(String reqUri, List<String> target) {
		for (String s : target) {
			if (s.equals("/")) {
				if (Objects.equals(reqUri, s))
					return true;
				continue;
			}

			if (reqUri.startsWith(s))
				return true;
		}
		return false;
	}

	/**
	 * 应用编码最长5位，作为应用在平台上的请求路径前缀。
	 *
	 * @param reqUri 请求资源URI
	 * @return 应用编码
	 */
	public static String appCode(String reqUri) {
		try {
			String root = "/";
			if ("".equals(reqUri) || root.equals(ObjectUtils.string(reqUri).trim()))
				return root;

			String[] temp = reqUri.split(root);

			return ObjectUtils.isBlank(temp[1]) ? null : temp[1].toLowerCase();
		}
		catch (Exception e) {
			log.error("请求截取应用编码异常");
		}
		return null;
	}
}
