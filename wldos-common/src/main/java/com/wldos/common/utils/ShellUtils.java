/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import lombok.extern.slf4j.Slf4j;

/**
 * 执行shell脚本。
 *
 * @author 元悉宇宙
 * @date 2022/2/12
 * @version 1.0
 */
@Slf4j
public class ShellUtils {

	/**
	 * 执行shell，返回执行成功结果 同时打印命令返回结果
	 * @param command 命令行
	 * @param isConsole 是否只打印控制台输出信息
	 * @return 是否成功
	 */
	public static boolean execShell(String command, boolean isConsole) {
		Process process = null;
		BufferedReader input = null;
		boolean flag = true;
		try {
			boolean isWin = ShellUtils.isWindows();
			process = Runtime.getRuntime().exec(isWin ? new String[] { "cmd", "/c", command } : new String[] { "/bin/sh", "-c", command });
			input = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName(isWin ? "GBK" : "UTF-8")));

			String line;
			while ((line = input.readLine()) != null) {
				if (isConsole)
					log.info(line);
				else
					log.info(line);
			}
		}
		catch (Throwable e) {
			flag = false;
			log.error("执行异常", e);
		}
		finally {
			int exitValue = 1;
			try {
				if (input != null)
					input.close();
				if (process != null)
					exitValue = process.waitFor();
			}
			catch (IOException | InterruptedException e) {
				log.error("exec command failed. command: {} {}", command, e);
			}
			if (0 != exitValue) {
				flag = false;
				log.error("call shell failed. error code is :" + exitValue + " command: " + command);
			}
		}

		return flag;
	}

	/**
	 * 执行shell命令
	 * @param shellString shell命令行
	 * @return true 执行成功 false 执行失败
	 */
	public static boolean callShell(String shellString) {
		boolean flag = true;
		Process process = null;
		try {
			boolean isWin = ShellUtils.isWindows();
			process = Runtime.getRuntime().exec(isWin ? new String[] { "cmd", "/c", shellString } : new String[] { "/bin/sh", "-c", shellString });
		}
		catch (Throwable e) {
			flag = false;
			log.error("call shell failed. " + e);
		}
		finally {
			int exitValue = 1;
			try {
				assert process != null;
				exitValue = process.waitFor();
			}
			catch (InterruptedException e) {
				log.error("call shell failed.", e);
			}
			if (0 != exitValue) {
				flag = false;
				log.error("call shell failed. error code is :" + exitValue + " command: " + shellString);
			}
		}

		return flag;
	}

	/**
	 * 执行shell，返回执行成功结果 同时打印命令返回结果
	 * @param command 命令行
	 * @return 执行结果
	 */
	public static String execCommand(String command) {
		Process process = null;
		BufferedReader input = null;
		StringBuilder sd = new StringBuilder();
		try {
			try {
				boolean isWin = ShellUtils.isWindows();
				process = Runtime.getRuntime().exec(isWin ? new String[] { "cmd", "/c", command } : new String[] { "/bin/sh", "-c", command });
				input = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName(isWin ? "GBK" : "UTF-8")));
				String line;
				while ((line = input.readLine()) != null) {
					sd.append(line);
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		finally {
			int exitValue = 1;
			try {
				if (input != null)
					input.close();
				if (process != null)
					exitValue = process.waitFor();
			}
			catch (IOException | InterruptedException e) {
				log.error("exec command failed. command: {} {}", command, e);
			}
			if (0 != exitValue) {
				log.error("exec command failed. error code is: {} command: {}", exitValue, command);
			}
		}
		return sd.toString();
	}

	/**
	 * 判断当前系统是否windows系统，否则Linux或其他系统
	 *
	 * @return 是否windows
	 */
	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().startsWith("windows");
	}
}