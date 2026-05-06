/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

/**
 * book-integration facade：把 BookView 需要的 social-publish service 接口收口在本目录，
 * 与 stub（pages/book/_ext/publish-stub/service.js）保持同名 export 镜像。
 *
 * <p>BookView 通过 webpack alias `@book-publish-ext/service` 在 commercial / community 两端切换：
 *   <ul>
 *     <li>commercial → 本文件 → 上层真实 service.js</li>
 *     <li>community  → pages/book/_ext/publish-stub/service.js（noop）</li>
 *   </ul>
 *
 * <p>BookView 直接引根 service.js 会让"开源目录直引 commercial 路径"的边界破裂；
 * 通过这层 facade，BookView 的 import 路径全部统一到 `@book-publish-ext/...`。
 */
export {
  bindMyPublishAccount,
  queryMyPublishAccounts,
  querySupportedPlatforms,
} from '@/pages/commercial/social-publish/service';
