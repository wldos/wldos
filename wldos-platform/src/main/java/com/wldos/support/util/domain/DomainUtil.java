/*
 * Copyright (c) 2020 - 2021. Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see https://www.wldos.com/
 *
 */

package com.wldos.support.util.domain;

import javax.servlet.http.HttpServletRequest;

import com.wldos.support.util.ObjectUtil;

/**
 * 域相关工具类。
 *
 * @author 树悉猿
 * @date 2021/9/6
 * @version 1.0
 */
public class DomainUtil {
	public static String getDomain(HttpServletRequest request, String domainHeader) {
		String domain = request.getHeader(domainHeader);
		String[] dStr = ObjectUtil.string(domain).split("\\.");
		if (dStr.length > 2) {
			domain = dStr[1] + "." + dStr[2];
		}
		return domain;
	}

	private DomainUtil() {
		throw new IllegalStateException("Utility class");
	}
}
