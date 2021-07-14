/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 授权的资源信息。
 *
 * @Title AuthInfo
 * @Package com.wldos.system.vo
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/26
 * @Version 1.0
 */
@Data
public class AuthInfo implements Serializable {
	public String resourceCode;

	public String resourceName;

	public String resourcePath;

	public String resourceType;

	public String requestMethod;
}
