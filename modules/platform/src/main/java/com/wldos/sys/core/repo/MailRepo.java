/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.repo;

import com.wldos.sys.core.entity.WoMail;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 邮件仓库操作类。
 *
 * @author 树悉猿
 * @date 2022/8/23
 * @version 1.0
 */
public interface MailRepo extends PagingAndSortingRepository<WoMail, Long> {
}
