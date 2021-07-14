/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.util;

import com.wldos.support.util.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;


/**
 * @Title: AddressUtils
 * @Package
 * @Description: IP地址工具类。
 * @author 树悉猿
 * @date
 * @version V1.0
 */
@Slf4j
public class AddressUtils {

	// IP地址查询
	public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";

	// 未知地址
	public static final String UNKNOWN = "XX XX";

	public static String getRealAddressByIP(String ip) {
		String address = UNKNOWN;
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
		return address;
	}
}
