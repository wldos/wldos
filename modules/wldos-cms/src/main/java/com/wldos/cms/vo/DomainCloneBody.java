/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 子站复制请求体。
 */
@Getter
@Setter
public class DomainCloneBody {

	private Long sourceDomainId;
	private Long targetDomainId;
	private DomainCloneOptions options;
}
