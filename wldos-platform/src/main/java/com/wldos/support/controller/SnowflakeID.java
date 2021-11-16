/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support.controller;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.security.SecureRandom.getInstance;
import static java.security.SecureRandom.getInstanceStrong;

/**
 * 主键生成器。
 *
 * @author 树悉猿
 * @date 2021年3月11日
 * @version V1.0
 */
@Component
public final class SnowflakeID {
	/** 开始时间戳：2021-3-17 18:37:50 */
	private static final long EPOCH_STAMP = 1615977470000L;

	private static final long SEQUENCE_BIT = 12L;

	private static final long MACHINE_BIT = 5L;

	private static final long DATA_CENTER_BIT = 5L;

	private static final long MAX_SEQUENCE_NUM = ~(-1L << SEQUENCE_BIT);

	private static final long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);

	private static final long MAX_DATA_CENTER_NUM = ~(-1L << DATA_CENTER_BIT);

	private static final long MACHINE_LEFT = SEQUENCE_BIT;

	private static final long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;

	private static final long TIMESTAMP_LEFT = SEQUENCE_BIT + MACHINE_BIT + DATA_CENTER_BIT;

	@Value("${wldos.system.machineId}")
	private long machineId;

	@Value("${wldos.system.dataCenterId}")
	private long dataCenterId;

	private long sequence = 0L;

	private long lastTimestamp = -1L;

	private final SecureRandom secureRandom;

	public SnowflakeID() {
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

	public synchronized long nextId() {
		long currentTimeMillis = this.currentTimeMillis();
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
				| this.sequence;
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

	public static void main(String[] args) {
		SnowflakeID sf = new SnowflakeID();
		for (int x = 0; x < 3; x++) {
			System.out.println(sf.nextId());
		}
	}
}
