<p align="center">
 <img alt="WLDOS" src="http://www.wldos.com/store/wldos.svg" width="120" height="120" style="margin-bottom: 10px; background: deepskyblue; border-radius: 60px; ">
</p>
<h3 align="center" style="margin:30px 0 30px;font-weight:bold;font-size:30px;">云应用支撑平台 - 云物互联驱动</h3>
<h5 align="center" style="margin:30px 0 30px;font-size:20px;">基于 WLDOS 开发 云物互联应用 聚焦、开放、管控你的生态</h5>
<p align="center">
 <a href="https://wldos.com/" target="__blank"><img alt="WLDOS-V2.0" src="https://img.shields.io/badge/WLDOS-V2.0-deepskyblue.svg"></a>
 <a href="https://spring.io/projects/spring-boot#learn" target="__blank"><img alt="SpringBoot-2.7" src="https://img.shields.io/badge/SpringBoot-2.7-bluegreen.svg"></a>
 <a href="https://gitee.com/wldos/wldos/stargazers" target="__blank"><img alt="star" src="https://gitee.com/wldos/wldos/badge/star.svg?theme=dark"></a>
 <a href="https://gitee.com/wldos/wldos/members" target="__blank"><img alt="fork" src="https://gitee.com/wldos/wldos/badge/fork.svg?theme=dark"></a>
</p>    

### 项目介绍

WLDOS（音：汉['wou'da'si]，World Operating System），Java开发，致力于为云物互联场景下的云、管、端应用提供基础支撑平台，适用于搭建各类网站、开发SaaS服务、研发业务中台等。<br/>

解决痛点：想利用线上技术拓展业务，对如何实现、落地有困惑，需要一个可落地的支撑底座搭建可持续扩展的平台，同时不需要昂贵的基础设施。

**适合个人建站、企业建站、业务中台、研发平台和综合支撑系统，适合技术在起步阶段或想在云物互联领域开发项目的团队。**

如果您觉得有价值，请<a href="https://gitee.com/wldos/wldos" style="color: red">star支持</a>我们！

### 项目特点

1.深入浅出：  
低成本，低门槛，仅需要传统基础设施，一线顶级架构师数十年磨一剑，封装陡峭的技术库以传统的技术风格面向开发人员，无需太多学习成本，开箱即用。

<br/>框架底层仅引用了springboot为主的开源库，形成了可持续优化的框架wldos-framework，基础上实现了wldos支撑平台，按需自研、从零开始有利于积累和成长。

2.灵活：   
不迷信业内各种高大上的轮子，崇尚自主研发，屏蔽深奥实现细节降低技术门槛，以大家熟悉的技术范式实现高级的功能扩展能力，深度定制的接口支持灵活扩展。<br/>  
基于springboot搭建，你可以随时定制自己的技术栈，比如引入mybatis作为orm框架 或者引入flowable作为你的业务流程引擎。<br/>  
3.务实：  
自主研发、自主运营，理论与实践相结合、研发与运营相结合，问题导向，以务实拥抱未来。

更多特点，请阅读代码。

#### 技术交流

QQ群：群一：792559964(已满) 群二：830355512   
作者公众号：元悉世界 （id:yuanxiyuzhou）

![作者公众号：元悉世界](元悉世界公众号.jpg)

演示地址：<a href="http://www.wldos.com/user/login?redirect=http%3A%2F%2Fwldos.com%2Fadmin%2Fres%2Fapp">点击前往</a>（官网即演示环境）

### 功能说明

WLDOS是个软件家族，目前由开发框架、支撑平台和内容付费三大板块构成，其中框架和支撑平台是通用支撑，内容付费是基于通用支撑展开的最佳实践。<br/>  
输出两个项目：WLDOS云应用支撑平台（管理端）和WLDOS内容付费系统（业务端）。应用功能结构如下：
![WLDOS支撑平台](https://gitee.com/wldos/wldos/raw/master/zone/dev/wldos.jpeg)
![WLDOS内容付费](https://gitee.com/wldos/wldos/raw/master/zone/dev/KPayCMS.jpeg)

### 技术说明
**语言：** Java8、ReactJs17。  
**框架：** springboot2.7.8（理论支持Java17）。  
**ORM：** spring-data-jdbc2.4.7，spring-data是介于spring-data-jpa与jdbc之间的一个友好框架，兼有二者优点，连接池采用boot自带hikari。  
**前端：** ReactJs17，AntD ProV4.5。  
**中间件：** tomcat9（支持换成其他），apache2或nginx。  
**辅助：** 自带cache、自带JWT、自带文件服务。  
**兼容性：** 后端jdk1.8，前端IE11+、Google Chrome、Edge等。

**应用架构：** 前后端分离，前端ReactJs，后端springMVC(2.0推出webflux架构版)，JWT认证，无状态，原生兼容springCloud，支持融入serviceMesh。

**1.0核心功能：** 系统管理（应用、资源、权限、用户、组织、租户、多域、分类），内容管理，信息发布，内容创作、内容付费（在线付费、在线交付）。

**2.0规划功能：** 服务集成与开放能力、插件扩展管理能力、类serviceMesh Ad-hoc架构支撑能力。

**3.0规划功能：** 软件工厂、云物互联支撑能力、智能建模与机器人系统。

### 模块介绍

wldos-common：通用模块  
wldos-framework：wldos开发框架
wldos-platform: wldos支撑平台    
wldos-oauth2: wldos社会化登录模块    
wldos-kpaycms: 为支撑平台基础上开发的内容付费管理系统，如果只需要支撑平台，可以仅运行wldos-platform模块，启动后在系统管理后台-资源管理清除cms的菜单；    
wldos-web：项目入口模块，资源配置，打war包或可执行jar从这里开始。

### 安装教程

#### 启动后端：
1. 后端工程下载到本地，用idea打开项目。<br/>
2. 安装mysql数据库脚本，生成数据库。 <br/>
   数据库脚本在wldos-web/db下，mysql5.7，数据库用户名、密码见wldos-web/resources/application-dev.properties。 <br/><br/>

3. 设置文件存储位置。  <br/>
   默认:${project-root}\\wldos-web\\Temp，如要修改，在wldos-platform下找properties中相应选项配置  <br/><br/>
4. 项目更新maven库。服务器端口号默认8080。<br/><br/>
5. 安装项目；  <br/>
   mvn clean install -pl com.wldos:wldos-web -am -DskipTests  <br/><br/>
   启动项目：    
   mvn -pl com.wldos:wldos-web spring-boot:run。<br/><br/>

#### 启动前端： <br/>
1.下载前端项目到本地后，打开项目，执行tyarn安装依赖js库。<br/>
2.执行npm start启动前端项目。前端访问路径：http://localhost:8000  <br/>
3.登录。超级管理员admin，密码同名称。

#### 部署到服务器: <br/>
    1.在服务器上安装好mysql5.7、tomcat9、apache2.4，数据库配置好DBA权限的用户，更新配置到platform/application*.properties中；  
    2.把上面第5步安装生成的war包部署到tomcat，可以解压后配置虚拟主机，也可以直接配置war包到虚拟主机；
    3.启动tomcat，系统自动完成数据库的创建和初始化；
    4.部署前端dist到服务器，配置好静态资源服务器apache或nginx的虚拟主机和对tomcat的反向代理；
    5.浏览器访问服务器解析的域名打开首页表示安装成功。

### 开源协议与商用许可

WLDOS平台已申请软件著作权证书，开放出的源代码以及软件可以商用(无需魔改^0^)，与商业版代码完全一致。   
商业合作qq：306991142。

官网：http://www.wldos.com or 306991142@qq.com

*附：*
#### 项目结构(以实际为准)

````
wldos根目录
├─wldos-common----------------------------------------wldos-common通用模块
│  ├─src----------------------------------------通用模块源码包
│  │  ├─main
│  │  │  ├─java
│  │  │  │  └─com
│  │  │  │      └─wldos
│  │  │  │          └─common
│  │  │  │              ├─dto-------------------通用数据传输类
│  │  │  │              ├─enums-----------------通用枚举类
│  │  │  │              ├─exception-------------全局异常基础包
│  │  │  │              ├─res-------------------通用响应模板
│  │  │  │              ├─utils-----------------通用工具类
│  │  │  │              │  ├─captcha------------验证码相关
│  │  │  │              │  ├─domain-------------域操作相关
│  │  │  │              │  ├─encrypt------------加密解密类
│  │  │  │              │  ├─http---------------http相关
│  │  │  │              │  └─img----------------图片处理类
│  │  │  │              └─vo--------------------通用视图类
│  │  │  └─resources----------------------------静态资源包
│  │  └─test------------------------------------单元测试包
│  └─target-------------------------------------编译打包输出目录
├─lib-------------------------------------------本地依赖jar
├─modules---------------------------------------功能模块目录
│  ├─wldos-kpaycms------------------------------------wldos内容付费管理系统
│  │  ├─src-------------------------------------内容付费系统源码包
│  │  │  ├─main
│  │  │  │  ├─java
│  │  │  │  │  └─com
│  │  │  │  │      └─wldos
│  │  │  │  │          ├─book-------------------内容付费实现包(coding)
│  │  │  │  │          │  ├─controller
│  │  │  │  │          │  ├─entity
│  │  │  │  │          │  ├─repo
│  │  │  │  │          │  ├─service
│  │  │  │  │          │  └─vo
│  │  │  │  │          └─cms--------------------内容管理基础包
│  │  │  │  │              ├─controller
│  │  │  │  │              ├─dto
│  │  │  │  │              ├─entity
│  │  │  │  │              ├─enums
│  │  │  │  │              ├─model
│  │  │  │  │              ├─repo
│  │  │  │  │              ├─service
│  │  │  │  │              └─vo
│  │  │  │  └─resources-------------------------静态资源包
│  │  │  └─test---------------------------------平台单元测试包
│  │  └─target----------------------------------编译打包输出目录
│  └─wldos-platform-----------------------------------wldos云应用支撑平台
│      ├─src------------------------------------平台源码包
│      │  ├─main
│      │  │  ├─java
│      │  │  │  └─com
│      │  │  │      └─wldos
│      │  │  │          ├─auth------------------登录鉴权授权包
│      │  │  │          │  ├─controller---------登录鉴权授权包
│      │  │  │          │  ├─dto----------------登录相关数据传输类
│      │  │  │          │  ├─enums--------------登录相关枚举值
│      │  │  │          │  ├─model--------------登录相关模型
│      │  │  │          │  ├─service------------登录相关业务层
│      │  │  │          │  └─vo-----------------登录相关实体类
│      │  │  │          ├─conf------------------平台参数配置包
│      │  │  │          ├─handler---------------全局处理器
│      │  │  │          └─sys-------------------平台系统包目录
│      │  │  │              └─core--------------平台核心实现驱动包
│      │  │  │                  ├─controller----平台核心控制层
│      │  │  │                  ├─entity--------平台核心模型实体类
│      │  │  │                  ├─repo----------平台核心数据仓库层
│      │  │  │                  ├─service-------平台核心业务层
│      │  │  │                  └─vo------------平台核心模型视图类
│      │  │  └─resources------------------------静态资源包
│      │  └─test--------------------------------平台单元测试包
│      └─target---------------------------------编译打包输出目录
├─wldos-web-------------------------------------项目web入口模块
│  ├─db-----------------------------------------平台数据库脚本
│  ├─src
│  │  ├─main
│  │  │  ├─java
│  │  │  │  └─com
│  │  │  │      └─wldos-------------------------web入口包
│  │  │  └─resources----------------------------最终资源包
│  │  │      └─resources------------------------静态资源包
│  │  │          └─store------------------------自带资源包
│  │  └─test------------------------------------单元测试包
│  └─target-------------------------------------编译打包输出目录
└─zone------------------------------------------测试静态资源包
````