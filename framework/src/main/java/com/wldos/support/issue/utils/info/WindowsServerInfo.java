/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue.utils.info;

import java.net.InetAddress;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author 树悉猿
 * @date 2022/1/24
 * @version 1.0
 */
public final class WindowsServerInfo extends ServerInfo {
	@Override
	public List<String> getIpAddress() throws Exception {
		List<String> result = null;

		List<InetAddress> inetAddresses = getLocalAllInetAddress();

		if (inetAddresses != null && inetAddresses.size() > 0) {
			result = inetAddresses.stream().map(InetAddress::getHostAddress).distinct().map(String::toLowerCase).collect(Collectors.toList());
		}

		return result;
	}

	@Override
	public List<String> getMacAddress() throws Exception {
		List<String> result = null;

		//1. 获取所有网络接口
		List<InetAddress> inetAddresses = getLocalAllInetAddress();

		if (inetAddresses != null && inetAddresses.size() > 0) {
			//2. 获取所有网络接口的Mac地址
			result = inetAddresses.stream().map(this::getMacByInetAddress).distinct().collect(Collectors.toList());
		}

		return result;
	}

	@Override
	public String getCPUSerial() throws Exception {

		String serialNumber = "";
		Process process = Runtime.getRuntime().exec("wmic cpu get processorid");
		process.getOutputStream().close();
		Scanner scanner = new Scanner(process.getInputStream());

		if (scanner.hasNext()) {
			scanner.next();
		}

		if (scanner.hasNext()) {
			serialNumber = scanner.next().trim();
		}

		scanner.close();
		return serialNumber;
	}

	@Override
	public String getMainBoardSerial() throws Exception {

		String serialNumber = "";
		Process process = Runtime.getRuntime().exec("wmic baseboard get serialnumber");
		process.getOutputStream().close();
		Scanner scanner = new Scanner(process.getInputStream());

		if (scanner.hasNext()) {
			scanner.next();
		}

		if (scanner.hasNext()) {
			serialNumber = scanner.next().trim();
		}

		scanner.close();
		return serialNumber;
	}
}