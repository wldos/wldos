<p align="center">
 <img alt="WLDOS" src="https://www.wldos.com/store/wldos.svg" width="120" height="120" style="margin-bottom: 10px; background: deepskyblue; border-radius: 60px; ">
</p>
<h3 align="center" style="margin:30px 0 30px;font-weight:bold;font-size:30px;">WLDOS云物互联驱动 - 内容付费系统</h3>
<p align="center">
 <a href="https://wldos.com/" target="__blank"><img alt="WLDOS-V1.5" src="https://img.shields.io/badge/WLDOS-V1.5-deepskyblue.svg"></a>
 <a href="https://spring.io/projects/spring-boot#learn" target="__blank"><img alt="SpringBoot-2.5" src="https://img.shields.io/badge/SpringBoot-2.5-bluegreen.svg"></a>
 <a href="https://gitee.com/wldos/wldos/stargazers" target="__blank"><img alt="star" src="https://gitee.com/wldos/wldos/badge/star.svg?theme=dark"></a>
 <a href="https://gitee.com/wldos/wldos/members" target="__blank"><img alt="fork" src="https://gitee.com/wldos/wldos/badge/fork.svg?theme=dark"></a>
</p>    

### WLDOS愿景

元宇宙融合虚拟与现实的理念与作者所想不谋而合，WLDOS（World Operating System）为了这种系统目标而孵化，是可以平台化和边缘化的云物互联支撑系统，面向社区开源、线上服务和业态孵化而生。

解决痛点：想利用网络化拓展业务，对如何拓展业务有困惑，需要一个可落地的软件搭建平台，同时不需要昂贵的容器化基础设施。

两条腿走路：WLDOS云应用支撑平台 + 内容付费业务场景实现

如果您觉得有前景，请<a href="https://gitee.com/wldos/wldos" style="color: red">star</a>我们!

### 项目特点

去容器化实现，不需要高昂的硬件基础设施，封装了陡峭的技术库以最普通的技术面向开发人员，无需太多学习成本，适合中小企业使用。

1.干净：框架底层仅引用了springboot为主的开源包，形成了可持续优化的开发框架wldos-framework，在此基础上实现了wldos支撑平台，没有引入spring全家桶所造成的噪声，按需自研、从零开始有利于开发团队、项目的积累和成长。

2.完整：包含底层到业务应用的完整产品线，是云物互联场景下的最佳实践，不迷信业内各种高大上的轮子，崇尚自主研发，屏蔽深奥实现细节降低技术门槛，以大家熟悉的技术范式实现高级的功能特性，"土"而自由。

3.真实：自主研发、自主运营，研发与运营同时进行的模式更务实，不做KPI，现实应用驱动，用什么，做什么。

#### 技术交流

QQ 群：830355512   
公众号：wldos

![公众号：wldos](https://gitee.com/wldos/wldos/raw/master/wldos.jpg)

### 项目介绍

WLDOS云应用支撑平台，简称WLDOS平台。本仓库是wldos平台后端工程。基础型新项目适合新手学习。  
WLDOS是world operate system的缩写，表示驱动世界的操作系统，不拘泥于某个业务领域，立足于对云端、管端和终端的融合支撑，理念是简化世界复杂度，用一套落地的系统方法可持续地支撑尽可能多的业务。  
WLDOS是类SaaS的云、管、端支撑平台，与SaaS的区别在于可云、可端，基于token认证的前后端分离应用架构，以多租户、多站点、多应用等实现虚拟域，是云、管、端的融合实现。

**适合个人建站、企业建站、中小企业搭建业务中台的基础研发平台，适合技术在起步阶段或想在云物互联领域开发项目的团队。**

关于发音  
WLDOS采用汉语拼音：[wou da si]。

演示地址：<a href="http://www.wldos.com/user/login?redirect=http%3A%2F%2Fwldos.com%2Fadmin%2Fres%2Fapp">点击前往</a>（演示环境版本低于社区版）

###技术说明
语言：Java8、ReactJs17。
框架：springboot2.4.6（支持升级到最新）。  
ORM：spring-data-jdbc2.1.9，连接池采用boot自带hikari。  
前端：ReactJs17，AntD ProV4.5。  
中间件：tomcat9（支持换成其他）。  
辅助：自带cache、自带JWT、自带文件服务。
兼容性：后端jdk1.8，前端IE11+、Google Chrome、Edge等。

应用架构：前后端分离，前端ReactJs，后端springMVC(2.0计划推出webflux架构版)，JWT认证，无状态，原生兼容springCloud，支持融入serviceMesh。

**1.0核心功能：系统管理（应用、资源、权限、用户、组织、租户、多域、分类），内容管理，信息发布，内容创作、内容付费（在线付费、在线交付）。**

**2.0规划功能：服务集成与开放能力、插件扩展管理能力、类serviceMesh Ad-hoc架构支撑能力。**

**3.0规划功能：软件工厂、云物互联支撑能力、智能建模与机器人系统。**

### 功能模块

WLDOS是个软件家族，目前由开发框架、支撑平台和内容管理三大版块构成，组成两个项目模块：WLDOS云应用支撑平台和WLDOS内容管理系统，如下：
![WLDOS支撑平台](https://gitee.com/wldos/wldos/raw/master/zone/dev/wldos.jpeg)
![WLDOS内容付费](https://gitee.com/wldos/wldos/raw/master/zone/dev/KPayCMS.jpeg)

### 安装教程

先部署后端：
1.  后端工程下载到本地，用idea打开项目。
2.  安装mysql数据库脚本，生成数据库。数据库脚本请进下方的qq群自行下载。   
    设置文件存储位置，默认E:\\Temp，如要修改，在wldos-platform下找properties中相应选项配置  
    把zone目录下的store.rar(图片压缩包较大，去qq群830355512文件里下载)解压到设置的文件存储位置，store为指定存储位置下的一级目录，里面有  
    演示数据对应的图片，如果不设置相关图片显示404。
3.  项目更新maven库。服务器端口号默认8080。
4.  安装项目；  
    目前开放7个模块：  
    wldos-common：通用模块  
    wldos-framework：wldos开发框架 
    wldos-platform-base: wldos支撑平台基础
    wldos-platform: wldos支撑平台    
    wldos-oauth2: wldos社会化登录模块    
    wldos-kpaycms: 为支撑平台基础上开发的内容付费管理系统，如果只需要支撑平台，可以仅运行wldos-platform模块，启动后在系统管理后台-资源管理清除cms的菜单；    
    wldos-web：项目入口模块，资源配置，打war包或可执行jar从这里开始。

    安装数据库，数据库脚本在wldos-web/db下，默认mysql5.7，数据库用户名、密码见wldos-web/resources/application-dev.properties。
    数据库正常运行后，以下命令启动项目：  
    打包安装web模块：  
    mvn clean install -pl com.wldos:wldos-web -am -DskipTests  
    启动项目：    
    mvn -pl com.wldos:wldos-web spring-boot:run 启动cms和支撑平台。

    部署前端：
1.  下载本地后，打开前端项目，执行tyarn安装依赖js库。
2.  执行npm start启动前端项目，npm build执行打包编译。前端访问路径：http://localhost:8000
3.  超级管理员admin，密码同名称。

### 使用说明

1. 浏览器访问localhost:8000,用户名、密码都是admin,注意浏览器要使用谷歌浏览器、IE11可能有卡顿。
2. 点击左侧管理菜单，使用系统管理功能。
3. 登陆使用JWT认证。

### 开源协议与商用许可

WLDOS平台已申请软件著作权证书，开放出的源代码以及软件可以商用(无需魔改^0^)，与商业版代码完全一致。   
商业合作qq：306991142。

官网：https://www.wldos.com

*附：*
#### 项目结构

````
wldos根目录
├─common----------------------------------------wldos-common通用模块
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
│  ├─kpaycms------------------------------------wldos内容付费管理系统
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
│  └─platform-----------------------------------wldos云应用支撑平台
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