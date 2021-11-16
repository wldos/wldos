/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support.cache;

import java.util.concurrent.TimeUnit;

/**
 * 定制缓存接口。
 *
 * @author 树悉猿
 * @date 2021/5/7
 * @version 1.0
 */
public interface ICache {

	void set(String key, Object value, long sourceDuration, TimeUnit sourceUnit);

	Object get(String key);

	void remove(String key);

	void removeByPrefix(String keyPrefix);

	long size();
	@SuppressWarnings("unused")
	void clear();
}
