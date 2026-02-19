/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.notice;

import java.util.List;
import java.util.Map;

/**
 * 用户通知/消息提供者：各业务模块实现此接口，由平台统一聚合后返回给前端消息气泡。
 * <p>
 * 单条结构约定（Map 键）：id、type（notification/message/event）、title、description、
 * datetime（ISO 或时间戳）、read、link（可选）、extra（可选）、avatar（可选）。
 * 详见《消息气泡-工单站内信邮件集成方案》。
 * </p>
 *
 * @author 元悉宇宙
 * @see NoticeItemKeys
 */
public interface UserNoticesProvider {

	/**
	 * 获取当前用户在本模块下的通知列表。
	 *
	 * @param userId  当前用户 ID，可为 null（未登录时返回空列表）
	 * @param maxCount 建议单模块最多返回条数，实现方可忽略但建议遵守以控制总量
	 * @return 符合约定结构的通知项列表，不可为 null，可为空列表
	 */
	List<Map<String, Object>> getNotices(Long userId, int maxCount);
}
