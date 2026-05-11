/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

/**
 * 社区版 stub：发布助手 service。仅供 BookView 的 3 个 import 使用：
 *   bindMyPublishAccount / queryMyPublishAccounts / querySupportedPlatforms
 *
 * <p>对应 commercial 实现路径：pages/commercial/social-publish/service.js（由 book-integration/service.js facade re-export）。
 * 返回 { data: ... } 的 shape 与 commercial 一致，BookView 解构 `res?.data` 不会 crash。
 */

export function bindMyPublishAccount() {
  return Promise.reject(new Error('publish_assistant_unavailable'));
}

export function queryMyPublishAccounts() {
  return Promise.resolve({ data: [] });
}

export function querySupportedPlatforms() {
  return Promise.resolve({ data: [] });
}
