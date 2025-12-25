# WLDOSHook 注解校验使用说明

## 概述

WLDOSHook 提供了运行时注解校验功能，确保 `@WLDOSHook` 注解的正确性。这个校验在Hook注册时自动执行，确保Hook机制的正常运行。

## 校验功能

### 1. 类级别注解校验
当在类上使用 `@WLDOSHook` 注解时：
- **HANDLER类型**：类必须实现 `Handler` 接口
- **INVOKE类型**：类必须实现 `Invoker` 接口

### 2. 方法级别注解校验
当在方法上使用 `@WLDOSHook` 注解时：

#### 参数校验
- 方法必须接受 `Object... args` 可变参数
- 不支持其他参数类型

#### 返回类型校验
- **HANDLER类型**：方法必须返回 `Object` 或兼容类型（不能返回void）
- **INVOKE类型**：方法必须返回 `void`

## 使用示例

### 正确用法

#### 方法级别Handler类型
```java
@WLDOSHook(extName = "user.login", type = HookType.HANDLER, priority = 10, numArgs = 1)
public Object handleLogin(Object... args) {
    // 处理逻辑
    return "success";
}
```

#### 方法级别Invoker类型
```java
@WLDOSHook(extName = "user.logout", type = HookType.INVOKE, priority = 10, numArgs = 1)
public void handleLogout(Object... args) {
    // 处理逻辑
}
```

#### 类级别Handler类型
```java
@WLDOSHook(extName = "class.level.handler", type = HookType.HANDLER, priority = 5, numArgs = 1)
public class MyPlugin extends AbstractPlugin implements Handler {
    @Override
    public Object applyHandler(Object... args) {
        // 处理逻辑
        return "class level result";
    }
}
```

#### 类级别Invoker类型
```java
@WLDOSHook(extName = "class.level.invoker", type = HookType.INVOKE, priority = 5, numArgs = 1)
public class MyPlugin extends AbstractPlugin implements Invoker {
    @Override
    public void invoke(Object... args) {
        // 处理逻辑
    }
}
```

### 错误用法示例

#### 错误1：Handler类型返回void
```java
@WLDOSHook(extName = "test.error", type = HookType.HANDLER, priority = 10, numArgs = 1)
public void errorHandler(Object... args) {
    // ❌ 编译通过，但运行时会抛出异常
}
```

#### 错误2：Invoker类型返回Object
```java
@WLDOSHook(extName = "test.error", type = HookType.INVOKE, priority = 10, numArgs = 1)
public Object errorInvoker(Object... args) {
    // ❌ 编译通过，但运行时会抛出异常
    return args[0];
}
```

#### 错误3：参数不是可变参数
```java
@WLDOSHook(extName = "test.error", type = HookType.HANDLER, priority = 10, numArgs = 1)
public Object errorHandler(Object args) {
    // ❌ 编译通过，但运行时会抛出异常
    return args;
}
```

## 错误信息示例

运行时校验失败时会抛出 `IllegalArgumentException`，包含详细的错误信息：

```
Method com.example.Plugin.errorHandler must return Object or compatible type when using @WLDOSHook with type=HANDLER

Method com.example.Plugin.errorInvoker must return void when using @WLDOSHook with type=INVOKE

Method com.example.Plugin.errorHandler must accept Object... args parameters when using @WLDOSHook

Class com.example.Plugin must implement Handler interface when using @WLDOSHook with type=HANDLER
```

## 校验时机

- **注册时校验**：在 `HookRegistrationUtils.registerHooks()` 方法中执行
- **自动执行**：插件启用时自动进行校验
- **立即反馈**：校验失败立即抛出异常，不会继续注册

## 优势

1. **运行时安全**：确保Hook机制的正确性
2. **即时反馈**：错误立即被发现
3. **详细错误信息**：提供具体的错误原因和位置
4. **自动执行**：无需手动调用，集成在注册流程中

## 注意事项

- 校验在运行时执行，不是编译期检查
- 建议在开发环境中及时修复校验错误
- 校验失败会导致插件注册失败
- 可以通过IDE插件实现更严格的编译期检查
