/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 *
 */

package com.wldos.support.mail;

import java.io.InputStream;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 * 邮件发送。
 *
 * @author 元悉宇宙
 * @date 2023/3/29
 * @version 1.0
 */
public interface MailSender {
	/**
	 * Send the given simple mail message.
	 * @param simpleMessage the message to send
	 * @throws MailParseException in case of failure when parsing the message
	 * @throws MailAuthenticationException in case of authentication failure
	 * @throws MailSendException in case of failure when sending the message
	 */
	void send(SimpleMailMessage simpleMessage) throws MailException;

	/**
	 * Send the given array of simple mail messages in batch.
	 * @param simpleMessages the messages to send
	 * @throws MailParseException in case of failure when parsing a message
	 * @throws MailAuthenticationException in case of authentication failure
	 * @throws MailSendException in case of failure when sending a message
	 */
	void send(SimpleMailMessage... simpleMessages) throws MailException;

	/**
	 * Create a new JavaMail MimeMessage for the underlying JavaMail Session
	 * of this sender. Needs to be called to create MimeMessage instances
	 * that can be prepared by the client and passed to send(MimeMessage).
	 * @return the new MimeMessage instance
	 * @see #send(MimeMessage)
	 * @see #send(MimeMessage[])
	 */
	MimeMessage createMimeMessage();

	/**
	 * Create a new JavaMail MimeMessage for the underlying JavaMail Session
	 * of this sender, using the given input stream as the message source.
	 * @param contentStream the raw MIME input stream for the message
	 * @return the new MimeMessage instance
	 * @throws org.springframework.mail.MailParseException
	 * in case of message creation failure
	 */
	MimeMessage createMimeMessage(InputStream contentStream) throws MailException;

	/**
	 * Send the given JavaMail MIME message.
	 * The message needs to have been created with {@link #createMimeMessage()}.
	 * @param mimeMessage message to send
	 * @throws org.springframework.mail.MailAuthenticationException
	 * in case of authentication failure
	 * @throws org.springframework.mail.MailSendException
	 * in case of failure when sending the message
	 * @see #createMimeMessage
	 */
	void send(MimeMessage mimeMessage) throws MailException;

	/**
	 * Send the given array of JavaMail MIME messages in batch.
	 * The messages need to have been created with {@link #createMimeMessage()}.
	 * @param mimeMessages messages to send
	 * @throws org.springframework.mail.MailAuthenticationException
	 * in case of authentication failure
	 * @throws org.springframework.mail.MailSendException
	 * in case of failure when sending a message
	 * @see #createMimeMessage
	 */
	void send(MimeMessage... mimeMessages) throws MailException;

	/**
	 * Send the JavaMail MIME message prepared by the given MimeMessagePreparator.
	 * <p>Alternative way to prepare MimeMessage instances, instead of
	 * {@link #createMimeMessage()} and {@link #send(MimeMessage)} calls.
	 * Takes care of proper exception conversion.
	 * @param mimeMessagePreparator the preparator to use
	 * @throws org.springframework.mail.MailPreparationException
	 * in case of failure when preparing the message
	 * @throws org.springframework.mail.MailParseException
	 * in case of failure when parsing the message
	 * @throws org.springframework.mail.MailAuthenticationException
	 * in case of authentication failure
	 * @throws org.springframework.mail.MailSendException
	 * in case of failure when sending the message
	 */
	void send(MimeMessagePreparator mimeMessagePreparator) throws MailException;

	/**
	 * Send the JavaMail MIME messages prepared by the given MimeMessagePreparators.
	 * <p>Alternative way to prepare MimeMessage instances, instead of
	 * {@link #createMimeMessage()} and {@link #send(MimeMessage[])} calls.
	 * Takes care of proper exception conversion.
	 * @param mimeMessagePreparators the preparator to use
	 * @throws org.springframework.mail.MailPreparationException
	 * in case of failure when preparing a message
	 * @throws org.springframework.mail.MailParseException
	 * in case of failure when parsing a message
	 * @throws org.springframework.mail.MailAuthenticationException
	 * in case of authentication failure
	 * @throws org.springframework.mail.MailSendException
	 * in case of failure when sending a message
	 */
	void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException;
}
