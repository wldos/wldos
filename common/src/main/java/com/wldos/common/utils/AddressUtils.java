/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.utils;

import com.wldos.common.utils.http.HttpUtils;
import com.wldos.common.utils.http.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;


/**
 * IP地址工具类。
 *
 * @author 树悉猿
 * @date 2021/04/05
 * @version V1.0
 */
@Slf4j
public final class AddressUtils {

	private AddressUtils() {
		throw new IllegalStateException("Utility class");
	}
	// IP地址查询
	public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";
	// 未知地址
	public static final String UNKNOWN = "XX XX";

	public static String getRealAddressByIP(String ip) {
		// 内网不查询
		if (IpUtils.internalIp(ip)) {
			return "内网IP";
		}
		try {
			String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip + "&json=true", "GBK");
			if (StringUtils.isEmpty(rspStr)) {
				log.error("获取地理位置异常 {}", ip);
				return UNKNOWN;
			}
			JSONObject obj = new JSONObject(rspStr);
			String region = obj.getString("pro");
			String city = obj.getString("city");
			return String.format("%s %s", region, city);
		}
		catch (Exception e) {
			log.error("获取地理位置异常 {}", ip);
		}
		return UNKNOWN;
	}
}