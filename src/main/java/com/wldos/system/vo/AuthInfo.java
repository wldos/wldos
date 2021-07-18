/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 授权的资源信息。
 *
 * @author 树悉猿
 * @date 2021/4/26
 * @version 1.0
 */
@Data
public class AuthInfo implements Serializable {
	private String resourceCode;

	private String resourceName;

	private String resourcePath;

	private String resourceType;

	private String requestMethod;
}
