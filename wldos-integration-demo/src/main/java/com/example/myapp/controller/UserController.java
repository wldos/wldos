/*
 * WLDOS 第三方集成示例项目
 * 
 * @author 元悉宇宙
 * @date 2025-12-28
 */
package com.example.myapp.controller;

import com.wldos.common.exception.BaseException;
import com.wldos.common.res.Result;
import com.wldos.common.res.ResultCode;
import com.example.myapp.dto.UserCreateDTO;
import com.example.myapp.service.UserService;
import com.example.myapp.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户管理 Controller
 * 
 * 演示普通 Controller 的使用方式（不继承框架基类）
 * 
 * 演示 WLDOS 框架的以下功能：
 * 1. 统一响应格式（自动包装为 Result）
 * 2. 异常处理（自动捕获并返回统一格式）
 * 3. 参数校验（Bean Validation）
 * 4. Swagger API 文档
 * 
 * 注意：普通 Controller 不继承框架基类，适用于简单的业务场景
 * 如果需要使用框架提供的工具方法（如 getUserId, getTenantId 等），
 * 建议继承 NonEntityController 或 EntityController
 */
@Api(tags = "用户管理（普通Controller示例）")
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取用户列表
     * 返回值可以是任意类型，框架会自动包装为 Result
     * 
     * 访问：GET http://localhost:8080/api/users/list
     */
    @ApiOperation(value = "获取用户列表", notes = "返回所有用户列表，框架会自动包装为 Result 格式")
    @GetMapping("/list")
    public List<UserVO> getUserList() {
        // 直接返回业务对象，框架会自动包装为 Result.ok(data)
        return userService.getUserList();
    }
    
    /**
     * 获取用户详情
     * 也可以直接返回 Result 类型
     * 
     * 访问：GET http://localhost:8080/api/users/{id}
     */
    @ApiOperation(value = "获取用户详情", notes = "根据用户ID获取用户详情")
    @GetMapping("/{id}")
    public Result getUserById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error(ResultCode.BAD_REQUEST, "用户ID无效");
        }
        
        UserVO user = userService.getUserById(id);
        if (user == null) {
            return Result.error(ResultCode.NOT_FOUND, "用户不存在");
        }
        
        return Result.ok(user);
    }
    
    /**
     * 创建用户
     * 使用 @Valid 进行参数校验
     * 
     * 访问：POST http://localhost:8080/api/users
     * Body: {"username": "test", "email": "test@example.com"}
     */
    @ApiOperation(value = "创建用户", notes = "创建新用户，参数校验失败会自动返回 Result.error(422, errors)")
    @PostMapping
    public Result createUser(@RequestBody @Valid UserCreateDTO dto) {
        // 参数校验会自动处理，校验失败会返回 Result.error(422, errors)
        UserVO user = userService.createUser(dto);
        return Result.ok(user);
    }
    
    /**
     * 更新用户
     * 
     * 访问：PUT http://localhost:8080/api/users/{id}
     */
    @ApiOperation(value = "更新用户", notes = "更新用户信息")
    @PutMapping("/{id}")
    public Result updateUser(@PathVariable Long id, @RequestBody @Valid UserCreateDTO dto) {
        UserVO user = userService.updateUser(id, dto);
        if (user == null) {
            return Result.error(ResultCode.NOT_FOUND, "用户不存在");
        }
        return Result.ok(user);
    }
    
    /**
     * 删除用户
     * 
     * 访问：DELETE http://localhost:8080/api/users/{id}
     */
    @ApiOperation(value = "删除用户", notes = "根据用户ID删除用户")
    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable Long id) {
        boolean success = userService.deleteUser(id);
        if (!success) {
            return Result.error(ResultCode.NOT_FOUND, "用户不存在");
        }
        return Result.ok("删除成功");
    }
    
    /**
     * 异常处理示例
     * 演示框架自动捕获异常并返回统一格式
     * 
     * 访问：GET http://localhost:8080/api/users/exception
     */
    @ApiOperation(value = "异常处理示例", notes = "演示框架自动捕获异常并返回统一格式")
    @GetMapping("/exception")
    public UserVO exceptionExample() {
        // 如果抛出异常，框架会自动捕获并返回 Result.error(...)
        throw new BaseException("这是一个业务异常示例", 400);
    }
    
    /**
     * 错误响应示例
     * 演示使用 ResultCode 枚举返回错误
     * 
     * 访问：GET http://localhost:8080/api/users/error
     */
    @ApiOperation(value = "错误响应示例", notes = "演示使用 ResultCode 枚举返回错误")
    @GetMapping("/error")
    public Result errorExample() {
        // 使用 ResultCode 枚举
        return Result.error(ResultCode.NOT_FOUND, "用户不存在");
    }
}

