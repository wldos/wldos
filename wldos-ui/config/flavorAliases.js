/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

/**
 * 开源 / 商业 flavor 扩展点 webpack alias 配置（单一信息源）。
 *
 * <p>本模块抽离自 wldos-ui/config/config.js，聚焦"哪个 alias 在 commercial / community 两端怎么切"，
 * 与 umi 主配置解耦：config.js 在 chainWebpack 内只调用一次 {@link applyFlavorExtensionAliases}。
 *
 * <h3>新增扩展点工作流</h3>
 * <ol>
 *   <li>在 {@link FLAVOR_EXTENSION_POINTS} 末尾追加 {alias, commercial, community} 一行</li>
 *   <li>jsconfig.json 的 paths 同步登记同名 alias（仅 IDE 类型解析用）</li>
 *   <li>scripts/verify-flavor-alignment.js 同步加扫描规则（防开源代码再写直引）</li>
 *   <li>编写 stub 实现，与 commercial 端同名 export 镜像，shape 必须一致</li>
 * </ol>
 *
 * <h3>为何选择"清单驱动"而非"自动扫描约定目录"</h3>
 * <p>显式清单的"单一信息源"价值大于自动发现：
 * <ul>
 *   <li>{@code grep alias-name} 能立刻定位扩展点定义；自动方案得 grep 多个文件</li>
 *   <li>新人翻一眼即知工程里有哪些可选扩展点；自动方案需要先理解扫描规则</li>
 *   <li>自动方案仍需 stub 自带元数据声明对应的 commercial 路径，登记成本只是换了位置</li>
 * </ul>
 * <p>未来扩展点数量预期在 5–10 个量级，清单驱动维护成本极低，是工程上更优的折中。
 */

import path from 'path';
import fs from 'fs';

/**
 * Flavor 扩展点登记表。每条记录定义一个 alias 在两套实现间的切换：
 * <ul>
 *   <li>{@code commercial}：商业分支真实实现路径（相对 wldos-ui 根目录）</li>
 *   <li>{@code community}：社区分支兜底路径（一般是 stub 目录或 community 自带实现）</li>
 * </ul>
 */
const FLAVOR_EXTENSION_POINTS = [
  {
    alias: '@flavor-locales',
    commercial: 'src/locales/flavor/commercial',
    community: 'src/locales/flavor/community',
  },
  {
    // BookView 自媒体发布扩展点（详见 src/pages/book/_ext/publish-stub/README.md）
    alias: '@book-publish-ext',
    commercial: 'src/pages/commercial/social-publish/book-integration',
    community: 'src/pages/book/_ext/publish-stub',
  },
  // 未来新增 flavor 扩展点：在此追加 { alias, commercial, community } 一行即可
];

/**
 * 解析单个 flavor 扩展点的实际目标目录。
 *
 * <ul>
 *   <li>显式 {@code APP_FLAVOR=community} → 走 community/stub 路径（不检测 commercial 是否存在）</li>
 *   <li>显式 {@code APP_FLAVOR=commercial} → 必须能找到 commercial 路径，否则 fail fast。
 *       CI 流水线打商业版时绝不能静默 fallback 到 stub，避免生产生成无功能的"商业版"</li>
 *   <li>未显式（默认 commercial）→ 优先 commercial；不存在则自动 fallback 到 community/stub 并打 warn</li>
 * </ul>
 *
 * <p>这层 fallback 让"社区源码 + 默认 {@code npm start} / {@code npm run dev}"也能直接跑：
 * 社区分支已经删了 commercial 相关目录，默认 {@code APP_FLAVOR=commercial} 会让 webpack resolve
 * 解析到不存在的路径而崩。商业分支因为 commercial 目录都在，命中第一分支，行为完全不变。
 *
 * <p>{@code fs.existsSync} 在 webpack chain 阶段单次执行，无热重载副作用。
 */
function resolveFlavorAliasTarget({ flavor, aliasName, commercialPath, communityPath }) {
  if (flavor === 'community') {
    return communityPath;
  }
  if (flavor === 'commercial') {
    if (!fs.existsSync(commercialPath)) {
      throw new Error(
        `[wldos][config] APP_FLAVOR=commercial 但 ${aliasName} 主路径不存在: ${commercialPath}\n`
        + '  社区分支请改用 APP_FLAVOR=community（npm run start:community:dev / build:community），'
        + '或者把 APP_FLAVOR 留空让自动 fallback 生效。',
      );
    }
    return commercialPath;
  }
  // 未显式：优先 commercial，缺失自动 fallback 到 community（社区源码下兜底）
  if (fs.existsSync(commercialPath)) {
    return commercialPath;
  }
  console.warn(
    `[wldos][config] 未显式设置 APP_FLAVOR 且 ${aliasName} 主路径不存在`
    + `\n  expected: ${commercialPath}`
    + `\n  fallback: ${communityPath}`
    + '\n  建议社区分支显式使用 APP_FLAVOR=community（npm run start:community:dev / build:community）。',
  );
  return communityPath;
}

/**
 * 把 {@link FLAVOR_EXTENSION_POINTS} 中所有扩展点一次性挂到 webpack alias 上。
 * 所有"开源 / 商业切换"统一收口在此，config.js 主体不再关心切换细节。
 *
 * @param config umi {@code chainWebpack(config)} 注入的 webpack-chain config 对象
 * @param options.uiRoot wldos-ui 根目录绝对路径（一般传 {@code path.resolve(__dirname, '..')}）
 * @param options.flavor 当前 APP_FLAVOR；省略则从 {@code process.env.APP_FLAVOR} 读取
 */
export function applyFlavorExtensionAliases(config, { uiRoot, flavor } = {}) {
  const effectiveFlavor = flavor != null ? flavor : process.env.APP_FLAVOR;
  FLAVOR_EXTENSION_POINTS.forEach((ep) => {
    config.resolve.alias.set(
      ep.alias,
      resolveFlavorAliasTarget({
        flavor: effectiveFlavor,
        aliasName: ep.alias,
        commercialPath: path.resolve(uiRoot, ep.commercial),
        communityPath: path.resolve(uiRoot, ep.community),
      }),
    );
  });
}
