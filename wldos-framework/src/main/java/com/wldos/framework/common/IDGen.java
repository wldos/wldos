/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.common;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.security.SecureRandom.getInstance;
import static java.security.SecureRandom.getInstanceStrong;

/**
 * 主键生成器。需要根据实际需要规划，前端js最大支持16位正整数：9007199254740991，普通业务系统不宜超出。
 *
 * @author 元悉宇宙
 * @date 2021年3月11日
 * @version V1.0
 */
@Component
public final class IDGen {
	/** 开始时间戳：2021-3-17 18:37:50 */
	private static final long EPOCH_STAMP = 1615977470000L;

	private static final long SEQUENCE_BIT = 12L; // 每毫秒2的8次方个随机序列，最大15位,0 ~ 2^8 - 1,9位以内生成的ID在16位以内，可以被js正确解析

	private static final long MACHINE_BIT = 5L; // 0 ~ 2^5-1

	private static final long DATA_CENTER_BIT = 5L;

	private static final long MAX_SEQUENCE_NUM = ~(-1L << SEQUENCE_BIT);

	private static final long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);

	private static final long MAX_DATA_CENTER_NUM = ~(-1L << DATA_CENTER_BIT);

	private static final long MACHINE_LEFT = SEQUENCE_BIT;

	private static final long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;

	private static final long TIMESTAMP_LEFT = SEQUENCE_BIT + MACHINE_BIT + DATA_CENTER_BIT;

	@Value("${wldos.system.machineId:1}")
	private long machineId;

	@Value("${wldos.system.dataCenterId:1}")
	private long dataCenterId;

	private long sequence = 0L;

	private long lastTimestamp = -1L;

	private final SecureRandom secureRandom;

	public IDGen() {
		if (this.machineId > MAX_MACHINE_NUM || this.machineId < 0) {
			throw new IllegalArgumentException(String.format("machine id can't be greater than %d or less than 0", MAX_MACHINE_NUM));
		}
		if (this.dataCenterId > MAX_DATA_CENTER_NUM || this.dataCenterId < 0) {
			throw new IllegalArgumentException(String.format("data center id can't be greater than %d or less than 0", MAX_DATA_CENTER_NUM));
		}

		String os = System.getProperty("os.name");
		try {
			if (os.toLowerCase().startsWith("win")) {
				// for windows
				this.secureRandom = getInstanceStrong();

			}
			else {
				// for linux
				this.secureRandom = getInstance("NativePRNGNonBlocking");
			}
		}
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 生成新主键
	 *
	 * @return 主键
	 */
	public synchronized static long nextId() {
		return IdWorker.getId(); // 统一改为mybatis plus 分布式唯一id
		/* long currentTimeMillis = this.currentTimeMillis();
		if (currentTimeMillis < this.lastTimestamp) {
			throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
					(this.lastTimestamp - currentTimeMillis)));
		}

		if (this.lastTimestamp == currentTimeMillis) {
			this.sequence = (this.sequence + 1) & MAX_SEQUENCE_NUM;
			if (this.sequence == 0) {
				this.sequence = secureRandom.nextInt(Long.valueOf(SEQUENCE_BIT).intValue());
				currentTimeMillis = this.tilNextMillis(this.lastTimestamp);
			}
		}
		else {
			this.sequence = secureRandom.nextInt(Long.valueOf(SEQUENCE_BIT).intValue());
		}
		this.lastTimestamp = currentTimeMillis;

		// 64 Bit ID (42(Millis)+5(Data Center ID)+5(Machine ID)+12(Repeat Sequence Summation))
		return ((currentTimeMillis - EPOCH_STAMP) << TIMESTAMP_LEFT)
				| (this.dataCenterId << DATA_CENTER_LEFT)
				| (this.machineId << MACHINE_LEFT)
				| this.sequence;*/
	}

	private long tilNextMillis(long lastTimestamp) {
		long currentTimeMillis = this.currentTimeMillis();
		while (currentTimeMillis <= lastTimestamp) {
			currentTimeMillis = this.currentTimeMillis();
		}
		return currentTimeMillis;
	}

	private long currentTimeMillis() {
		return System.currentTimeMillis();
	}
}