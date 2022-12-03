/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue.utils.info;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * 获取Linux系统下服务器硬件信息。
 *
 * @author 树悉猿
 * @date 2022/1/24
 * @version 1.0
 */
public final class LinuxServerInfo extends ServerInfo {
	@Override
	public List<String> getIpAddress() throws Exception {
		List<String> result = null;
		//获取所有网络接口
		List<InetAddress> inetAddresses = getLocalAllInetAddress();

		if (inetAddresses != null && inetAddresses.size() > 0) {
			result = inetAddresses.stream().map(InetAddress::getHostAddress).distinct().map(String::toLowerCase).collect(Collectors.toList());
		}

		return result;
	}

	@Override
	public List<String> getMacAddress() throws Exception {
		List<String> result = null;

		// 获取所有网络接口
		List<InetAddress> inetAddresses = getLocalAllInetAddress();

		if (inetAddresses != null && inetAddresses.size() > 0) {
			// 获取所有网络接口的Mac地址
			result = inetAddresses.stream().map(this::getMacByInetAddress).distinct().collect(Collectors.toList());
		}

		return result;
	}

	@Override
	public String getCPUSerial() throws Exception {
		//序列号
		String serialNumber = "";

		//使用dmidecode命令获取CPU序列号
		String[] shell = { "/bin/bash", "-c", "dmidecode -t processor | grep 'ID' | awk -F ':' '{print $2}' | head -n 1" };
		Process process = Runtime.getRuntime().exec(shell);
		process.getOutputStream().close();

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String line = reader.readLine().trim();
		if (StringUtils.isNotBlank(line)) {
			serialNumber = line;
		}

		reader.close();
		return serialNumber;
	}

	@Override
	public String getMainBoardSerial() throws Exception {
		//序列号
		String serialNumber = "";

		//使用dmidecode命令获取主板序列号
		String[] shell = { "/bin/bash", "-c", "dmidecode | grep 'Serial Number' | awk -F ':' '{print $2}' | head -n 1" };
		Process process = Runtime.getRuntime().exec(shell);
		process.getOutputStream().close();

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String line = reader.readLine().trim();
		if (StringUtils.isNotBlank(line)) {
			serialNumber = line;
		}

		reader.close();
		return serialNumber;
	}
}