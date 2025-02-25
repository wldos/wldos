/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.hook;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Component;

/**
 * @author 元悉宇宙
 * @version 1.0
 * @date 2025/2/25
 */
@ConditionalOnMissingClass("com.wldos.support.hook.WSHookImpl")
@Component
public class DefaultWSHookImpl implements WSHook{
}
