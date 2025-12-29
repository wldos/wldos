/*
 * WLDOS 第三方集成示例项目
 * 
 * @author 元悉宇宙
 * @date 2025-12-28
 */
package com.example.myapp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户创建 DTO
 * 
 * 演示 Bean Validation 参数校验
 */
@ApiModel(description = "用户创建请求参数")
public class UserCreateDTO {
    
    @ApiModelProperty(value = "用户名", required = true, example = "zhangsan")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20之间")
    private String username;
    
    @ApiModelProperty(value = "邮箱", required = true, example = "zhangsan@example.com")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @ApiModelProperty(value = "昵称", example = "张三")
    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}

