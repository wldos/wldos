/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.utils.http;

import javax.servlet.http.HttpServletRequest;

public class IpUtils {
	private static final String UNKNOWN = "unknown";

	private IpUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static boolean internalIp(String ip) {
		byte[] addr = textToNumFormatV4(ip);
		return internalIp(addr) || "127.0.0.1".equals(ip);
	}

	/**
	 * 将IPv4地址转换成字节
	 *
	 * @param text IPv4地址
	 * @return byte 字节
	 */
	public static byte[] textToNumFormatV4(String text) {
		if (text.length() == 0) {
			return new byte[0];
		}

		byte[] bytes = new byte[4];
		String[] elements = text.split("\\.", -1);
		try {
			switch (elements.length) {
				case 1:
					return ipRead(Long.parseLong(elements[0]));
				case 2:
					return ipRead(Integer.parseInt(elements[0]), Integer.parseInt(elements[1]));
				case 3:
					return ipRead(elements);
				case 4:
					for (int i = 0; i < 4; ++i) {
						long l = Integer.parseInt(elements[i]);
						if ((l < 0L) || (l > 255L)) {
							return new byte[0];
						}
						bytes[i] = (byte) (int) (l & 0xFF);
					}
					break;
				default:
					return new byte[0];
			}
		}
		catch (NumberFormatException e) {
			return new byte[0];
		}
		return bytes;
	}

	private static byte[] ipRead(long l) {
		byte[] bytes = new byte[0];
		if ((l < 0L) || (l > 4294967295L)) {
			return bytes;
		}

		bytes = new byte[4];
		bytes[0] = (byte) (int) (l >> 24 & 0xFF);
		bytes[1] = (byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF);
		bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
		bytes[3] = (byte) (int) (l & 0xFF);

		return bytes;
	}

	private static byte[] ipRead(long ele1, long ele2) {
		byte[] bytes = new byte[0];

		if ((ele1 < 0L) || (ele1 > 255L)) {
			return bytes;
		}

		if ((ele2 < 0L) || (ele2 > 16777215L)) {
			return bytes;
		}

		bytes = new byte[4];

		bytes[0] = (byte) (int) (ele1 & 0xFF);
		bytes[1] = (byte) (int) (ele2 >> 16 & 0xFF);
		bytes[2] = (byte) (int) ((ele2 & 0xFFFF) >> 8 & 0xFF);
		bytes[3] = (byte) (int) (ele2 & 0xFF);

		return bytes;
	}

	private static byte[] ipRead(String[] elements) {
		byte[] bytes = new byte[4];

		long l = Integer.parseInt(elements[2]);
		if ((l < 0L) || (l > 65535L)) {
			return new byte[0];
		}

		for (int i = 0; i < elements.length - 1 ; ++i) {
			l = Integer.parseInt(elements[i]);
			if ((l < 0L) || (l > 255L)) {
				return new byte[0];
			}
			bytes[i] = (byte) (int) (l & 0xFF);
		}

		bytes[2] = (byte) (int) (l >> 8 & 0xFF);
		bytes[3] = (byte) (int) (l & 0xFF);

		return bytes;
	}

	private static boolean internalIp(byte[] addr) {
		if (addr == null || addr.length < 2) {
			return true;
		}
		final byte b0 = addr[0];
		final byte b1 = addr[1];
		// 10.x.x.x/8
		final byte SECTION_1 = 0x0A;
		// 172.16.x.x/12
		final byte SECTION_2 = (byte) 0xAC;
		final byte SECTION_3 = (byte) 0x10;
		final byte SECTION_4 = (byte) 0x1F;
		// 192.168.x.x/16
		final byte SECTION_5 = (byte) 0xC0;
		final byte SECTION_6 = (byte) 0xA8;
		switch (b0) {
			case SECTION_1:
				return true;
			case SECTION_2:
				return (b1 >= SECTION_3 && b1 <= SECTION_4);
			case SECTION_5:
				return b1 == SECTION_6;
			default:
				return false;
		}
	}

}
