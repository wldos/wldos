/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth.model;

/**
 * 账户消息设置。
 *
 * @author 树悉猿
 * @date 2021/9/22
 * @version 1.0
 */
public class AccMes {
	private String userMesStatus;

	private String sysMesStatus;

	private String todoMesStatus;

	public String getUserMesStatus() {
		return userMesStatus;
	}

	public void setUserMesStatus(String userMesStatus) {
		this.userMesStatus = userMesStatus;
	}

	public String getSysMesStatus() {
		return sysMesStatus;
	}

	public void setSysMesStatus(String sysMesStatus) {
		this.sysMesStatus = sysMesStatus;
	}

	public String getTodoMesStatus() {
		return todoMesStatus;
	}

	public void setTodoMesStatus(String todoMesStatus) {
		this.todoMesStatus = todoMesStatus;
	}
}
