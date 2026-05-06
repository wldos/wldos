/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

/**
 * 社区版 stub：自媒体发布助手核心。仅在 webpack alias `@book-publish-ext` 切到本目录时被加载（APP_FLAVOR=community）。
 *
 * <p>BookView 的 11 个商业引用通过 alias 在两套实现之间切换：
 *   <ul>
 *     <li>commercial → pages/commercial/social-publish/book-integration/*</li>
 *     <li>community  → pages/book/_ext/publish-stub/* （本目录）</li>
 *   </ul>
 *
 * <p>本文件保持与 commercial 同名 export 的<b>形参 / 返回值形状</b>一致，BookView 的所有解构、属性访问、Promise 链
 * 都不会因 community 版而 crash。具体的"功能不可用"由 BookView 内 `assistantDesktopCommercial` 双因子守护
 * （APP_FLAVOR + isDesktopEmbedded）阻止入口出现，stub 仅作为「打不进去也得能编译过」的兜底契约。
 */

// 与 commercial 同值，保证 BookView 第 ~320 行 state 初始化的 minLeadMinutes/maxLeadMinutes 取到合理默认。
export const DEFAULT_SCHEDULE_POLICY = { minLeadMinutes: 60, maxLeadMinutes: 14 * 24 * 60 };

export const PLATFORM_SCHEDULE_POLICY = {};

export function buildSchedulePolicyMap() {
  return {};
}

/**
 * 形状镜像 commercial loadDefaultPublishContext 的返回 contract：
 * { includeWldosSelf, rawIds, rawGroupIds, accountIds, selectedAccounts, schedulePolicyMap }
 * BookView 里通过 `context.selectedAccounts` / `context.schedulePolicyMap` 等字段消费，必须给齐空集合。
 */
export function loadDefaultPublishContext() {
  return Promise.resolve({
    includeWldosSelf: true,
    rawIds: [],
    rawGroupIds: [],
    accountIds: [],
    selectedAccounts: [],
    schedulePolicyMap: {},
  });
}

export function calcScheduleRule() {
  return {
    minLeadMinutes: DEFAULT_SCHEDULE_POLICY.minLeadMinutes,
    maxLeadMinutes: DEFAULT_SCHEDULE_POLICY.maxLeadMinutes,
    platformCodes: [],
  };
}

/**
 * 社区版理论上不会触达此函数（BookView 的 assistantDesktopCommercial 双因子在入口阻挡）。
 * 万一被绕开调用，给出明确语义的 reject，便于上层 message.error 提示，而非静默 success。
 */
export function createAssistantPublishJobs() {
  return Promise.reject(new Error('publish_assistant_unavailable'));
}

export function fetchAssistantJobsForSource() {
  return Promise.resolve([]);
}
