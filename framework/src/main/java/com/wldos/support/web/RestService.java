/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;

/**
 * 解析请求并转发。
 *
 * @author 树悉猿
 * @date 2021-04-01
 * @version V1.0
 */
@SuppressWarnings({ "unused" })
public interface RestService {

	ResponseEntity<String> redirect(HttpServletRequest request, HttpServletResponse response, String routeUrl, String prefix);
}