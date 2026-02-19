/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.notice;

/**
 * 通知项 Map 的键常量，与前端消息气泡约定一致。
 *
 * @author 元悉宇宙
 */
public final class NoticeItemKeys {

	public static final String ID = "id";
	public static final String TYPE = "type";
	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
	public static final String DATETIME = "datetime";
	public static final String READ = "read";
	public static final String LINK = "link";
	public static final String EXTRA = "extra";
	public static final String AVATAR = "avatar";

	/** type 取值：通知（如工单提醒、邮件） */
	public static final String TYPE_NOTIFICATION = "notification";
	/** type 取值：消息（如站内信） */
	public static final String TYPE_MESSAGE = "message";
	/** type 取值：待办 */
	public static final String TYPE_EVENT = "event";

	private NoticeItemKeys() {
	}
}
