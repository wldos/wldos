/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue.utils.info;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.wldos.support.issue.verify.VerifyEnv;
import com.wldos.support.issue.vo.VerifyInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 树悉猿
 * @date 2022/1/24
 * @version 1.0
 */
@Slf4j
public abstract class ServerInfo {

	public VerifyInfo getServerInfos(VerifyEnv verifyEnv){

		try {
			return VerifyInfo.of(this.getIpAddress(), this.getMacAddress(), this.getCPUSerial(), this.getMainBoardSerial(), verifyEnv);
		}catch (Exception e){
			log.error("获取服务器硬件信息失败，请确保存在完整的授权信息配置。");
		}

		return null;
	}

	protected abstract List<String> getIpAddress() throws Exception;

	protected abstract List<String> getMacAddress() throws Exception;

	protected abstract String getCPUSerial() throws Exception;

	protected abstract String getMainBoardSerial() throws Exception;

	@SuppressWarnings("rawtypes")
	protected List<InetAddress> getLocalAllInetAddress() throws Exception {
		List<InetAddress> result = new ArrayList<>(4);

		for (Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces(); networkInterfaces.hasMoreElements(); ) {
			NetworkInterface iface = (NetworkInterface) networkInterfaces.nextElement();

			for (Enumeration inetAddresses = iface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
				InetAddress inetAddr = (InetAddress) inetAddresses.nextElement();

				if(!inetAddr.isLoopbackAddress()
						&& !inetAddr.isLinkLocalAddress() && !inetAddr.isMulticastAddress()){
					result.add(inetAddr);
				}
			}
		}

		return result;
	}

	protected String getMacByInetAddress(InetAddress inetAddr){
		try {
			byte[] mac = NetworkInterface.getByInetAddress(inetAddr).getHardwareAddress();
			StringBuilder stringBuffer = new StringBuilder();

			for(int i=0;i<mac.length;i++){
				if(i != 0) {
					stringBuffer.append("-");
				}

				String temp = Integer.toHexString(mac[i] & 0xff);
				if(temp.length() == 1){
					stringBuffer.append("0").append(temp);
				}else{
					stringBuffer.append(temp);
				}
			}

			return stringBuffer.toString().toUpperCase();
		} catch (SocketException e) {
			log.error(e.getMessage());
		}
		return null;
	}
}