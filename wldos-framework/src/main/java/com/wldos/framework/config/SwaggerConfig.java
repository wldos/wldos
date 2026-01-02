package com.wldos.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.RequestHandler;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import com.wldos.common.Constants;
import com.wldos.framework.autoconfigure.WldosFrameworkProperties;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Swagger 2.x 配置（支持可配置的包路径）
 * 使用 Knife4j 增强 Swagger UI，提供更美观的界面和更强大的功能
 * Knife4j 4.x 版本已自动配置，无需显式启用
 * 
 * @author 元悉宇宙
 * @date 2025-12-26
 * @version 2.0
 */
@Configuration
@EnableSwagger2WebMvc
@EnableConfigurationProperties(WldosFrameworkProperties.class)
@ConditionalOnProperty(name = "wldos.framework.swagger.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerConfig implements WebMvcConfigurer {
    
    @Autowired
    private WldosFrameworkProperties properties;

    @Value("${server.port:8080}")
    private String port;

    @Value("${server.address:localhost}")
    private String address;

    @Value("${server.domain:localhost}")
    private String domain;
    
    @Value("${gateway.proxy.prefix:/wldos}")
    private String proxyPrefix;
    
    /** 产品版本号，从配置变量读取（PropertiesDynImpl 设置），如果无法读取则使用常量默认值 */
    @Value("${wldos.version:" + Constants.WLDOS_VERSION + "}")
    private String wldosVersion;
    
    @Bean
    public Docket wldosApi() {
        String basePackage = properties.getBasePackage();
        
        // 如果配置了第三方包路径，同时扫描 com.wldos 和第三方包路径
        // SpringFox 的 RequestHandlerSelectors.basePackage 只支持单个包，需要使用 Predicate 组合
        String frameworkPackage = "com.wldos"; // 框架包路径不要删除，否则无法扫描到框架的API
        
        // 构建包路径过滤条件
        Predicate<RequestHandler> packagePredicate;
        if (basePackage != null && !basePackage.equals(frameworkPackage)) {
            packagePredicate = RequestHandlerSelectors.basePackage(frameworkPackage)
                .or(RequestHandlerSelectors.basePackage(basePackage));
        } else {
            packagePredicate = RequestHandlerSelectors.basePackage(frameworkPackage);
        }
        
        // 只显示带有 Swagger 注解的 API：
        // 1. 类上有 @Api 注解，或
        // 2. 方法上有 @ApiOperation 注解
        Predicate<RequestHandler> annotationPredicate = RequestHandlerSelectors.withClassAnnotation(Api.class)
            .or(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class));
        
        // 组合条件：包路径 + Swagger 注解
        Predicate<RequestHandler> apiPredicate = packagePredicate.and(annotationPredicate);
        
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("wldos-api") // 设置分组名称
            .apiInfo(apiInfo())
            .select()
            .apis(apiPredicate)
            .paths(PathSelectors.any())
            .build()
            .pathMapping(proxyPrefix) // 添加路径前缀，所有 API 路径自动加上 /wldos 前缀
            .securitySchemes(securitySchemes()); // 添加安全方案（Token 认证）
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("WLDOS API文档")
            .description("WLDOS平台API接口文档")
            .version(wldosVersion)
            .contact(new Contact("WLDOS团队", "", "306991142@qq.com"))
            .build();
    }
    
    /**
     * 配置安全方案（支持 Token 认证）
     * 使用 ApiKey 类型，支持自定义请求头
     */
    private List<SecurityScheme> securitySchemes() {
        return Collections.singletonList(
            new ApiKey(
                "Token", // 安全方案的名称，会在 Swagger UI 中显示
                Constants.TOKEN_ACCESS_HEADER, // 请求头名称：X-CU-AccessToken-WLDOS
                "header" // 参数位置：header
            )
        );
    }
    
    /**
     * 配置 Swagger UI 资源映射
     * 添加 URL 参数以启用左侧列表 + 右侧详情的布局
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Swagger UI 的资源已经由 SpringFox 自动处理
        // 这里可以添加自定义配置
    }
}

