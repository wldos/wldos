/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.sys.core.service;

import java.io.File;

import javax.mail.internet.MimeMessage;

import com.wldos.framework.service.RepoService;
import com.wldos.support.mail.MailSender;
import com.wldos.sys.core.entity.WoMail;
import com.wldos.sys.core.enums.MailEnum;
import com.wldos.sys.core.repo.MailRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 邮件service。
 *
 * @author 树悉猿
 * @date 2022/8/23
 * @version 1.0
 */
@RefreshScope
@Service
@Transactional(rollbackFor = Exception.class)
public class MailService extends RepoService<MailRepo, WoMail, Long> {

	/** 发件人邮箱地址 */
	@Value("${wldos_mail_fromMail_addr:}")
	private String from;

	@Autowired
	@Lazy
	@Qualifier("mailSender")
	private MailSender mailSender;

	/**
	 * 发送文本邮件
	 *
	 * @param to 收件人邮箱
	 * @param subject 邮件主题
	 * @param content 邮件内容
	 */
	public void sendEmailText(String to, String subject, String content, Long cUid, String cUip) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(content);

		this.sendMail(to, content, message, cUid, cUip);
	}

	/**
	 * 发送多媒体邮件
	 *
	 * @param to 收件人邮箱
	 * @param subject 邮件主题
	 * @param content 邮件内容
	 */
	public void sendMailHtml(String to, String subject, String content, Long cUid, String cUip) {
		MimeMessage message = mailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content, true);

			this.sendMail(to, content, message, cUid, cUip);
		}
		catch (Exception ignored) {
			this.getLog().warn("发送html邮件时异常！请检查邮箱配置");
			// throw new RuntimeException("发送html邮件时异常！", e);
		}
	}

	/**
	 * 发送带附件的邮件
	 *
	 * @param to 收件人邮箱
	 * @param subject 邮件主题
	 * @param content 邮件内容
	 * @param filePath 附件路径
	 * @param cUid 当前用户id
	 * @param cUip 当前用户ip
	 */
	public void sendMailAttach(String to, String subject, String content, String filePath, Long cUid, String cUip) {
		MimeMessage message = this.mailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content, true);

			FileSystemResource fsr = new FileSystemResource(new File(filePath));
			String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
			helper.addAttachment(fileName, fsr);

			this.sendMail(to, content, message, cUid, cUip);
		}
		catch (Exception e) {
			throw new RuntimeException("发送带附件的邮件时异常！", e);
		}
	}

	/**
	 * 发送嵌入静态资源的邮件
	 *
	 * @param to 收件人邮箱
	 * @param subject 邮件主题
	 * @param content 邮件内容
	 * @param rscPath 资源路径
	 * @param cUid 当前用户id
	 * @param cUip 当前用户ip
	 */
	public void sendMailStaticRes(String to, String subject, String content, String rscPath, Long cUid, String cUip) {
		MimeMessage message = this.mailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content, true);

			FileSystemResource fsr = new FileSystemResource(new File(rscPath));
			helper.addInline(Long.toString(this.nextId()), fsr);

			this.sendMail(to, content, message, cUid, cUip);
		}
		catch (Exception e) {
			throw new RuntimeException("发送嵌入静态资源邮件时异常！", e);
		}
	}

	private void sendMail(String to, String content, MimeMessage message, Long cUid, String cUip) {
		WoMail mail = null;

		try {
			mail = this.createEmail(to, content, cUid, cUip);
			this.mailSender.send(message);
			this.updateEmail(mail, MailEnum.SUCCESS.getValue());
			getLog().info("发送邮件成功：{}", to);
		}
		catch (Exception e) {
			assert mail != null;
			this.updateEmail(mail, MailEnum.SUCCESS.getValue());
			throw new RuntimeException("发送邮件异常！", e);
		}
	}

	private void sendMail(String to, String content, SimpleMailMessage message, Long cUid, String cUip) {
		WoMail mail = null;

		try {
			mail = this.createEmail(to, content, cUid, cUip);
			this.mailSender.send(message);
			this.updateEmail(mail, MailEnum.SUCCESS.getValue());
			getLog().info("发送邮件成功：{}", to);
		}
		catch (Exception e) {
			assert mail != null;
			this.updateEmail(mail, MailEnum.SUCCESS.getValue());
			throw new RuntimeException("发送邮件异常！", e);
		}
	}

	private WoMail createEmail(String to, String content, Long cUid, String cUip) {
		WoMail mail = new WoMail();
		mail.setStatus(MailEnum.SENDING.getValue());
		mail.setContent(content);
		mail.setFromAddr(from);
		mail.setToAddr(to);
		this.save(mail, true);
		return mail;
	}

	private void updateEmail(WoMail mail, String status) {
		mail.setStatus(status);
		this.update(mail, true);
	}
}