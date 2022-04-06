/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */
-- 仅对v1.0.1优化有效，用后失效。2022-02-26
ALTER TABLE `k_posts`
    ADD COLUMN `views`  int(10) UNSIGNED NULL COMMENT '查看数：每满一定次数更新一次，并非实时更新，实时记录见postmeta' AFTER `like_count`;
