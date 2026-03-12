<div align="center">
  <img alt="WLDOS" src="http://gitee.com/wldos/wldos/raw/master/modules/wldos-platform/src/main/resources/resources/store/logo.png" width="100" height="100">
  <h1>WLDOS 云应用支撑平台</h1>
  <p><strong>5分钟构建企业级多租户 SaaS 应用</strong></p>
  <p>
    <a href="http://www.wldos.com">🎬 在线演示</a> •
    <a href="#-快速开始">🚀 快速开始</a> •
    <a href="#-技术交流">💬 加入社区</a> •
    <a href="README.EN.md">English</a>
  </p>
  <p>
    <img src="https://img.shields.io/badge/WLDOS-V2.3.8.2-deepskyblue.svg" alt="version">
    <img src="https://img.shields.io/badge/SpringBoot-2.7-green.svg" alt="springboot">
    <img src="https://img.shields.io/badge/React-17-blue.svg" alt="react">
    <a href="https://gitee.com/wldos/wldos/stargazers"><img src="https://gitee.com/wldos/wldos/badge/star.svg?theme=dark" alt="star"></a>
  </p>
</div>

---

## ✨ 核心能力

| 能力 | 说明 |
|------|------|
| 🏢 **多租户** | 一套代码服务多个客户，数据完全隔离 |
| 🌐 **多域名** | 不同域名展示不同内容，独立品牌运营 |
| 🔌 **插件热插拔** | 业务模块即插即用，无需重启 |
| 🔐 **完整权限** | RBAC + 数据权限 + 多级组织 |
| 📱 **前后端融合** | Spring Boot + React，动态单体架构 |
| 💰 **内容付费** | 内置付费系统，开箱即用 |

## 🚀 快速开始

### 方式一：Maven 启动（推荐）

```bash
# 1. 克隆项目
git clone https://gitee.com/wldos/wldos.git
cd wldos

# 2. 初始化数据库（MySQL 5.7+）
# 执行 wldos-web/db/init.sql

# 3. 修改数据库配置
# 编辑 wldos-web/src/main/resources/application-dev.properties

# 4. 启动后端
mvn clean install -pl com.wldos:wldos-web -am -DskipTests "-Dspring.profiles.active=dev"
mvn -pl com.wldos:wldos-web spring-boot:run "-Dspring.profiles.active=dev"

# 5. 启动前端
cd wldos-ui
npm install && npm start

# 6. 访问 http://localhost:8000  账号：admin / admin
```

### 方式二：桌面版安装（Windows）

下载桌面安装包，双击运行，开箱即用。[下载地址](http://www.wldos.com)

## 📸 功能截图

<details>
<summary>点击展开截图</summary>

![系统管理](https://gitee.com/wldos/wldos/raw/master/zone/dev/wldos.jpeg)
![内容付费](https://gitee.com/wldos/wldos/raw/master/zone/dev/KPayCMS.jpeg)
![动态单体架构](https://gitee.com/wldos/wldos/raw/master/zone/dev/wldos2.0.jpeg)

</details>

## 💬 技术交流

| 渠道 | 信息 |
|------|------|
| QQ群 | 830355512（群一已满） |
| 公众号 | 元悉世界（id: yuanxiyuzhou） |
| 演示站 | [www.wldos.com](http://www.wldos.com) |

![元悉世界公众号](zone/dev/元悉世界公众号.jpg)

---

## 📖 详细文档

<details>
<summary>功能说明</summary>

### 功能说明

WLDOS是个软件家族，目前由开发框架、支撑平台和内容付费三大板块构成，其中框架和支撑平台是通用支撑，内容付费是基于通用支撑展开的最佳实践。<br/>  
输出两个项目：WLDOS云应用支撑平台（管理端）和WLDOS内容付费系统（业务端），2.0版本推出动态单体架构融合了前后端分离架构和微服务架构。应用功能结构如下：
#### 1.0前后端分离架构
![WLDOS支撑平台](https://gitee.com/wldos/wldos/raw/master/zone/dev/wldos.jpeg)
![WLDOS内容付费](https://gitee.com/wldos/wldos/raw/master/zone/dev/KPayCMS.jpeg)
#### 2.0动态单体架构
![WLDOS2.0动态单体](https://gitee.com/wldos/wldos/raw/master/zone/dev/wldos2.0.jpeg)

### 技术说明
**语言：** Java8、ReactJs17。  
**框架：** springboot2.7.18（支持Java17，后期版本支持Java25）。  
**ORM：** spring-data-jdbc2.4.17，spring-data是介于spring-data-jpa与jdbc之间的一个友好框架，兼有二者优点，连接池采用boot自带hikari。  
**前端：** ReactJs17，AntD ProV4.5。（后期版本支持React18，antd pro v5）  
**中间件：** tomcat9（支持换成其他），apache2或nginx。  
**辅助：** 自带cache、自带JWT、自带文件服务。  
**兼容性：** 后端jdk1.8，前端IE11+、Google Chrome、Edge等。

**应用架构：** 前后端分离，前端ReactJs，后端springMVC(2.0推出webflux架构版)，JWT认证，无状态，原生兼容springCloud，支持融入serviceMesh。

**1.0核心功能：** 系统管理（应用、资源、权限、用户、组织、租户、多域、分类），内容管理，信息发布，内容创作、内容付费（在线付费、在线交付）。

**2.0规划功能：** 服务集成与开放能力、插件扩展管理能力、类serviceMesh Ad-hoc架构支撑能力。

**3.0规划功能：** 软件工厂、云物互联支撑能力、智能建模与机器人系统。

### 模块介绍

wldos-sdk-api：sdk契约模块  
wldos-framework：wldos开发框架，已经连同common包发布到maven中央仓库，建议在项目中直接依赖，防止与官方版本产生分歧 
````
<dependencies>
   <dependency>
      <groupId>com.wldos</groupId>
      <artifactId>wldos-framework</artifactId> <!-- framework默认依赖了common包 无需单独引入common包的依赖 -->
      <version>${project.parent.version}</version>
      <scope>compile</scope>
   </dependency>
</dependencies>
````
wldos-platform: wldos支撑平台，整个系统管理功能全在这一模块实现。       
wldos-cms: 为支撑平台基础上开发的内容付费管理系统，如果只需要支撑平台，可以仅运行wldos-platform模块，启动后在系统管理后台-资源管理清除cms的菜单；    
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
   mvn clean install -pl com.wldos:wldos-web -am -DskipTests "-Dspring.profiles.active=dev" <br/><br/>
   启动项目：    
   mvn -pl com.wldos:wldos-web spring-boot:run "-Dspring.profiles.active=dev"。<br/><br/>
6. 项目打包：<br/>
   mvn clean package -pl com.wldos:wldos-web -am -DskipTests "-Dspring.profiles.active=prod"

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
    6.后续推出本地安装版，支持Windows系统本地安装部署。

### 开源协议与商用许可

本项目采用**双许可证模式**：

#### 开源部分（Apache 2.0）

- ✅ 源代码已开放，可商用（无需魔改）
- ✅ 与商业版代码一致
- ✅ 功能不受限

**许可证**：Apache License 2.0

#### 非开源部分（商业许可证）

- ⚠️ wldos-platform 模块中的非开源部分
- ⚠️ 其他商业组件

**许可证**：WLDOS® 商业授权协议

**说明**：不论主体，可以免费用，分发需授权。

#### 版本说明

**社区开源版**：
- ✅ 功能完整（目前功能不受限，和商业版一样）
- ✅ 包含非纯开源模块
- ⚠️ 缺乏商业支持（仅提供社区支持）

**商业版**：
- ✅ 功能完整（与社区开源版相同）
- ✅ 包含完整的非纯开源模块
- ✅ 商业支持（提供商业技术支持、定制开发）

#### 如何选择许可证

**使用开源许可证（Apache 2.0）**：
- ✅ 个人学习和研究
- ✅ 开源项目使用
- ✅ 使用社区开源版

**使用商业许可证**：
- ✅ 商业项目使用
- ✅ 需要商业支持

#### 联系方式

- 商业授权：306991142@qq.com
- 开源社区：GitHub/Gitee
- 官网：http://gitee.com/wldos or 306991142@qq.com

详细条款请参见 [LICENSE](LICENSE) 和 [term.md](term.md)

*附：*
#### 项目后端结构(以实际为准)

````
wldos根目录
├─wldos-sdk-api----------------------------------------wldos契约SDK-API模块（依赖maven中央仓库）
|——wldos-framework----------------------------------wldos framework框架
├─modules---------------------------------------功能模块目录
│  ├─wldos-cms------------------------------------wldos内容付费管理系统
│  └─wldos-platform-----------------------------------wldos云应用支撑平台
├─wldos-web-------------------------------------项目web入口模块
````

#### 项目前端结构(以实际为准)

````
wldos-ui根目录
├─config--------------------------------------------配置目录
├─src
│  ├─assets-----------------------------------------静态资源
│  ├─components-------------------------------------全局组件
│  ├─layouts----------------------------------------布局组件
│  ├─locales----------------------------------------国际化资源
│  ├─models-----------------------------------------models组件
│  ├─pages------------------------------------------页面组件
│  │  ├─account-------------------------------------用户中心
│  │  ├─book----------------------------------------内容付费
│  │  ├─doc-----------------------------------------文档中心
│  │  ├─exception-----------------------------------异常处理
│  │  ├─home----------------------------------------门户组件
│  │  ├─search--------------------------------------全文检索
│  │  ├─sys-----------------------------------------系统管理
│  │  └─user----------------------------------------用户登录
│  ├─services---------------------------------------全局API
│  └─utils------------------------------------------工具类
└─zone----------------------------------------------README图片

````
#### 2.0动态单体项目结构
````
wldos根目录
├── modules---------------------------------多模块子项目
│   ├── wldos-cms-----------------------内容付费CMS系统
│   │   ├── pom.xml
│   │   └── src
│   ├── wldos-oauth2------------------------社会化登录（未开放）
│   │   ├── pom.xml
│   │   └── src
│   ├── wldos-platform----------------------WLDOS支撑平台
│   │   ├── pom.xml
│   │   └── src
│   └── wldos-store-------------------------应用商店（未开放）
│       ├── pom.xml
│       └── src
├── wldos-framework-------------------------WLDOS开发框架
│   ├── pom.xml
│   └── src
├── wldos-installer-------------------------桌面版（未开放）
│   ├── pom.xml
│   ├── src
├── wldos-integration-demo------------------框架集成demo
│   ├── frontend-demo-----------------------前端集成demo
│   ├── pom.xml
│   ├── src
├── wldos-plugins---------------------------插件模块
│   ├── plugin-config-template.yml----------插件配置模板
│   ├── plugin-demo-------------------------全栈插件demo
│   │   ├── CHANGELOG.md
│   │   ├── README.md
│   │   ├── build-ui.bat
│   │   ├── build-ui.sh
│   │   ├── plugin.yml-----------------------插件配置
│   │   ├── pom.xml
│   │   └── src------------------------------插件源码（后端+前端）
│   ├── pom.xml
│   └── wldos-plugin-demo-------------------纯后端插件demo
│       ├── plugin.properties
│       ├── plugin.yml
│       ├── pom.xml
│       └── src
├── wldos-ui--------------------------------前端（动态单体）
│   ├── config
│   ├── node--------------------------------内置node（开发时依赖）
│   ├── package.json
│   ├── src
├── wldos-web-------------------------------项目web入口模块
│   ├── plugins-----------------------------内置插件目录
│   │   ├── plugin-demo---------------------已安装插件目录（全栈插件demo）
│   │   ├── plugin-demo.zip-----------------全栈插件demo安装包
│   │   ├── wldos-plugin-demo---------------后端插件demo安装目录
│   │   └── wldos-plugin-demo.zip-----------后端插件demo安装包
│   ├── pom.xml
│   ├── src---------------------------------源码目录
│   └── store-------------------------------附件目录（本地存储服务）
│       ├── 2025----------------------------随机附件文件目录
│       └── plugins-------------------------内置插件目录（插件市场）
└── 开发日志.txt

````

### 版本协议说明
- wldos 2.3.8.2+ 版本：已彻底移除所有GPL/AGPL协议依赖，主协议为 Apache License 2.0，可自由分发、闭源商用（无需开源）；
- wldos 2.3.7及以下版本：包含GPL协议依赖，使用/分发该版本需遵守GPL条款（建议升级至2.3.8.2+）。

</details>

---

**如果觉得有帮助，请给个 ⭐ Star 支持！**