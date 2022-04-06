/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.utils.domain;

import javax.servlet.http.HttpServletRequest;

import com.wldos.common.utils.ObjectUtils;

/**
 * 域相关工具类。
 *
 * @author 树悉猿
 * @date 2021/9/6
 * @version 1.0
 */
public class DomainUtils {
	/**
	 * 获取用户当前访问的顶级域名
	 *
	 * @param request 请求
	 * @param domainHeader 域参数在request中保存的header
	 * @return 所用域名的顶级域名，类似： xxx.com...
	 */
	public static String getDomain(HttpServletRequest request, String domainHeader) {
		// 约定所有终端使用请求头设置的域名为请求域名，作为白名单验证的依据
		String domain = ObjectUtils.string(request.getHeader(domainHeader));
		if (domain.equals("")) { // 未设置header, 取真实域名
			return request.getServerName().toLowerCase();
		}
		String[] dStr = domain.split("\\.");
		if (dStr.length > 2) { // 只考虑顶级域名，暂不允许设置二级域名做多站点
			domain = dStr[1] + "." + dStr[2];
		}
		return domain;
	}

	private DomainUtils() {
		throw new IllegalStateException("Utility class");
	}
}
