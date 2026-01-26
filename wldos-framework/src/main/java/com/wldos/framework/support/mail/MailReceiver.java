/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.mail;

import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.springframework.mail.MailException;

/**
 * 邮件接收接口。
 * 支持从指定邮箱账户通过 IMAP/POP3 协议接收邮件。
 *
 * @author 元悉宇宙
 * @date 2026/01/22
 * @version 1.0
 */
public interface MailReceiver {

	/**
	 * 获取指定邮箱账户的未读邮件列表。
	 * @param accountConfig 邮箱账户配置（包含协议、主机、端口、用户名、密码等）
	 * @return 未读邮件列表
	 * @throws MailException 邮件操作异常
	 */
	List<Message> fetchUnreadMessages(MailAccountConfig accountConfig) throws MailException;

	/**
	 * 获取指定邮箱账户的所有邮件（或指定文件夹的邮件）。
	 * @param accountConfig 邮箱账户配置
	 * @param folderName 文件夹名称（如 "INBOX"），为空则使用默认收件箱
	 * @return 邮件列表
	 * @throws MailException 邮件操作异常
	 */
	List<Message> fetchMessages(MailAccountConfig accountConfig, String folderName) throws MailException;

	/**
	 * 获取指定时间范围内的邮件。
	 * @param accountConfig 邮箱账户配置
	 * @param folderName 文件夹名称
	 * @param sinceDays 从多少天前开始（例如：7 表示最近7天）
	 * @return 邮件列表
	 * @throws MailException 邮件操作异常
	 */
	List<Message> fetchMessagesSince(MailAccountConfig accountConfig, String folderName, int sinceDays) throws MailException;

	/**
	 * 将邮件标记为已读。
	 * @param accountConfig 邮箱账户配置
	 * @param message 要标记的邮件
	 * @throws MailException 邮件操作异常
	 */
	void markAsRead(MailAccountConfig accountConfig, Message message) throws MailException;

	/**
	 * 删除邮件。
	 * @param accountConfig 邮箱账户配置
	 * @param message 要删除的邮件
	 * @throws MailException 邮件操作异常
	 */
	void deleteMessage(MailAccountConfig accountConfig, Message message) throws MailException;

	/**
	 * 邮箱账户配置类。
	 */
	class MailAccountConfig {
		private String protocol; // "imap" 或 "pop3"
		private String host; // 如 "imap.gmail.com"
		private int port; // 如 993 (IMAPS) 或 995 (POP3S)
		private String username; // 邮箱地址
		private String password; // 密码或应用专用密码
		private boolean useSSL = true; // 是否使用 SSL/TLS
		private String folder = "INBOX"; // 默认文件夹

		public MailAccountConfig() {
		}

		public MailAccountConfig(String protocol, String host, int port, String username, String password) {
			this.protocol = protocol;
			this.host = host;
			this.port = port;
			this.username = username;
			this.password = password;
		}

		public String getProtocol() {
			return protocol;
		}

		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public boolean isUseSSL() {
			return useSSL;
		}

		public void setUseSSL(boolean useSSL) {
			this.useSSL = useSSL;
		}

		public String getFolder() {
			return folder;
		}

		public void setFolder(String folder) {
			this.folder = folder;
		}
	}
}
