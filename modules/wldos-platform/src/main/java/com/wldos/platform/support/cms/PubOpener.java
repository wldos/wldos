/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.support.cms;

import java.util.List;

import com.wldos.platform.support.cms.dto.ContModelDto;

/**
 * 发布操作开瓶器。
 *
 * @author 元悉宇宙
 * @date 2023/3/25
 * @version 1.0
 */
public interface PubOpener {
	/**
	 * 根据发布内容富文本html内容和长度生成发布内容摘要
	 *
	 * @param html 发布内容html内容
	 * @param length 长度，seo描述一般140
	 * @return 摘要信息
	 */
	String genPubExcerpt(String html, int length);

	/**
	 * 查询内容附件(文件、图片等附属子实体)
	 *
	 * @param pid 发布内容id
	 * @return 内容列表
	 */
	List<ContModelDto> queryContAttach(Long pid);
}
