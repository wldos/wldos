/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.config;

import com.wldos.common.res.Result;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.swagger.common.SwaggerPluginSupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * WLDOS Result 包装规则：当 API 返回非 Result 类型时，Swagger 文档自动显示为 Result&lt;T&gt; 结构。
 * <p>
 * 配合 GlobalResponseHandler/EdgeHandler 的运行时自动包装，实现：
 * - 代码层：API 可直接返回 Product、PageData 等，无需显式写 Result&lt;T&gt;
 * - 文档层：Swagger 自动展示完整消息模板 { code, message, success, data }
 * <p>
 * 注意：此规则会将 Object 替换为 Result&lt;Object&gt;，与 PageData 等嵌套泛型结合时，
 * 可能触发 Springfox ModelContext.getGenericNamingStrategy 无限递归导致 StackOverflowError。
 * 若 Swagger 启动报错，可设置 wldos.framework.swagger.result-wrapper-rule.enabled=false 禁用。
 *
 * @author 元悉宇宙
 * @date 2026/2/1
 * @version 1.0
 */
@Component
@ConditionalOnProperty(name = "wldos.framework.swagger.result-wrapper-rule.enabled", havingValue = "true", matchIfMissing = false)
public class ResultWrapperAlternateTypeRule implements AlternateTypeRuleConvention {

    private final TypeResolver typeResolver = new TypeResolver();

    @Override
    public List<AlternateTypeRule> rules() {
        return Collections.singletonList(new ResultWrapperRule());
    }

    @Override
    public int getOrder() {
        return SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1000;
    }

    private class ResultWrapperRule extends AlternateTypeRule {

        public ResultWrapperRule() {
            super(typeResolver.resolve(Object.class), typeResolver.resolve(Result.class, Object.class));
        }

        @Override
        public boolean appliesTo(ResolvedType type) {
            if (type == null) {
                return false;
            }
            Class<?> erased = type.getErasedType();
            if (erased == null) {
                return false;
            }
            // 已是 Result 体系，无需包装
            if (Result.class.isAssignableFrom(erased)) {
                return false;
            }
            if (ResponseEntity.class.isAssignableFrom(erased)) {
                return false;
            }
            if (String.class.equals(erased) || Resource.class.isAssignableFrom(erased)) {
                return false;
            }
            if (Void.class.equals(erased) || void.class.equals(erased)) {
                return false;
            }
            return true;
        }

        @Override
        public ResolvedType alternateFor(ResolvedType type) {
            return typeResolver.resolve(Result.class, type);
        }
    }
}
