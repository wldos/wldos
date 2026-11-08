/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.gateway.edge;

/**
 * IP 规则匹配结果，供 {@link com.wldos.gateway.EdgeGateWayFilter} 与限流器消费。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public final class EdgeIpPolicyMatch {

	private final EdgeIpPolicyAction action;
	private final Integer ratePerSecond;
	private final Integer ratePerMinute;
	private final Long ruleId;

	public EdgeIpPolicyMatch(EdgeIpPolicyAction action, Integer ratePerSecond, Integer ratePerMinute, Long ruleId) {
		this.action = action;
		this.ratePerSecond = ratePerSecond;
		this.ratePerMinute = ratePerMinute;
		this.ruleId = ruleId;
	}

	public EdgeIpPolicyAction getAction() {
		return action;
	}

	public Integer getRatePerSecond() {
		return ratePerSecond;
	}

	public Integer getRatePerMinute() {
		return ratePerMinute;
	}

	public Long getRuleId() {
		return ruleId;
	}
}
