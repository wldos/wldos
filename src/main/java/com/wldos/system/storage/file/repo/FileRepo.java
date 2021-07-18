/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.storage.file.repo;

import com.wldos.system.storage.file.entity.WoFile;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 文件仓库操作类。
 *
 * @author 树悉猿
 * @date 2021/4/27
 * @version 1.0
 */
public interface FileRepo extends PagingAndSortingRepository<WoFile, Long> {
}
