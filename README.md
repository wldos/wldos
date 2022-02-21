# WLDOS云应用支撑平台

#### 引子

元宇宙之概念炒得火热，现在发现其中很多理念与当初的创想不谋而合，身为码农的人们应做点什么——后生万学，奋起直追，犹未为晚。那我们就从力所能及的地方开始，并不内行的我们本着对当下以及未来的憧憬和预定，竭老猿之所能事，从代码开始输出。在系统的研发过程中，一步步探求我们最引以为傲的“乌托邦”，然后实现它、驱动它，直到托起更美好的世界！WLDOS（World
Operating System）算是为了这样的目标所作的涂鸦，这样一个拙劣而平凡的“小板凳”，需要在实际应用中积累和锤炼，这与某些业务型项目不同。

#### 特点

1.干净：仅底层引用了springboot等基础开源包，开发框架持续优化，在此基础上实现支撑平台，避免了引入spring全家桶所造成的噪声，按需自研、从零开始有利于开发团队、项目的积累和成长。

2.完整：包含底层到业务应用的完整产品线，是云物互联背景下的小有规模的最佳实践，不迷信业内各种高大上的轮子，崇尚自主研发，为解决实际问题而自研有利于自然成长，虽"土"，但够用。  

3.真实：自主研发、自主运营，研发与运营同时进行的模式更务实，不做KPI，现实应用驱动，用什么，做什么。
  
项目初起，较为浅陋。假以时日，WLDOS框架以及WLDOS平台家族会逐渐成熟起来，You can believe！

#### 介绍

WLDOS云应用支撑平台，简称WLDOS平台，本仓库是wldos平台后端工程。基础型新项目，适合新手跟随学习。  
WLDOS是面向云物互联的支撑平台，基于多域架构、支持多租、多应用的SaaS，wldos.com基于WLDOS搭建，新功能持续开放中。

WLDOS云应用支撑平台，基于springboot二次封装的轻量级开发框架，SaaS应用架构。默认支持多域(站)
系统，也可以单站模式运行，默认支持多租户运行模式，同时支持单租户运行。默认单实例运行，在分布式部署方面支持融入serviceMesh架构或者传统中心化分布式架构，具体部署方式不作约束，自行规划。

**适合个人建站、企业建站、搭建业务中台的基础、研发平台，适合技术在起步阶段或想在云物互联领域开发项目的团队。**

关于发音  
为统一发音，采用汉语拼音：[wo'dao'si] 。

#### 交流群

QQ 群：830355512   
公众号：wldos 

#### 软件架构

框架技术：springboot2.4.6（支持升级到最新版），spring-data-jdbc，定制封装的框架级原生jdbcAPI让开发者自由发挥。

应用架构：前后端分离，前端ReactJs，后端springMVC，JWT认证，无状态，原生兼容springCloud，支持融入serviceMesh。

2.0开始推出webflux架构版。

**1.0核心功能：系统管理（应用、资源、权限、用户、组织、租户、多域、分类），内容管理，信息发布，内容创作、内容付费（在线付费、在线交付）。**

软件架构说明

#### 项目结构

````
wldos根目录
├─wldos-framework----------------------------------wldos开发框架
│  ├─src---------------------------------------开发框架源码包
│  │  ├─main
│  │  │  ├─java
│  │  │  │  └─com
│  │  │  │      └─wldos
│  │  │  │          ├─base---------------------框架封装基础包
│  │  │  │          │  ├─controller------------全局控制层基础包
│  │  │  │          │  │  └─handler------------控制层统一处理包
│  │  │  │          │  ├─entity----------------全局实体基础辅助
│  │  │  │          │  ├─exception-------------全局异常基础包
│  │  │  │          │  ├─repo------------------全局仓库基础包
│  │  │  │          │  └─service---------------全局业务层基础包
│  │  │  │          ├─common-------------------框架通用工具包（现在是wldos-common模块）
│  │  │  │          │  ├─dto-------------------通用数据传输类
│  │  │  │          │  ├─enums-----------------通用枚举类
│  │  │  │          │  ├─res-------------------通用响应模板
│  │  │  │          │  ├─util------------------通用工具类
│  │  │  │          │  │  ├─captcha------------验证码相关
│  │  │  │          │  │  ├─domain-------------域操作相关
│  │  │  │          │  │  ├─encrypt------------加密解密类
│  │  │  │          │  │  ├─http---------------http相关
│  │  │  │          │  │  ├─img----------------图片处理类
│  │  │  │          │  │  └─jwt----------------jwt工具类
│  │  │  │          │  └─vo--------------------通用视图类
│  │  │  │          ├─support------------------框架基础能力支撑包
│  │  │  │          │  ├─auth------------------身份认证能力支撑包
│  │  │  │          │  ├─cache-----------------缓存能力支撑包
│  │  │  │          │  ├─storage---------------文件存储能力支撑包
│  │  │  │          │  └─web-------------------远程通信能力支撑包
│  │  │  │          └─example------------------框架开发样例包
│  │  │  └─resources---------------------------框架静态资源包
│  │  └─test-----------------------------------框架单元测试包
│  └─target------------------------------------框架编译打包输出目录
└─wldos-platform----------------------------------wldos云应用支撑平台
    ├─db---------------------------------------全平台数据库脚本（现在调整到wldos-web下）
    ├─src--------------------------------------平台源码包
    │  ├─main
    │  │  ├─java
    │  │  │  └─com
    │  │  │      └─wldos
    │  │  │          └─system------------------平台包根目录
    │  │  │              ├─auth----------------登录鉴权授权包
    │  │  │              │  ├─controller-------登录相关控制层
    │  │  │              │  ├─dto--------------登录相关数据传输类
    │  │  │              │  ├─enums------------登录相关枚举值
    │  │  │              │  ├─model------------登录相关模型
    │  │  │              │  ├─service----------登录相关业务层
    │  │  │              │  └─vo---------------登录相关实体类
    │  │  │              ├─conf----------------平台参数配置包
    │  │  │              ├─core----------------平台核心实现驱动包
    │  │  │              │  ├─controller-------平台核心控制层
    │  │  │              │  ├─dto--------------平台核心数据传输类
    │  │  │              │  ├─entity-----------平台核心模型实体类
    │  │  │              │  ├─enums------------平台核心模型枚举值
    │  │  │              │  ├─repo-------------平台核心数据仓库层
    │  │  │              │  ├─service----------平台核心业务层
    │  │  │              │  └─vo---------------平台核心模型视图类
    │  │  │              ├─gateway-------------平台网关鉴权包
    │  │  │              └─plugin--------------平台插件管理包
    │  │  └─resources--------------------------平台资源包
    │  │      ├─META-INF-----------------------平台元信息声明
    │  │      ├─resources----------------------平台静态资源包
    │  │      │  └─store-----------------------平台自带资源包
    │  │      ├─static-------------------------平台静态资源目录
    │  │      └─templates----------------------平台模板资源包
    │  └─test----------------------------------平台单元测试包
    │  └─target--------------------------------平台编译打包输出目录
    ├─wldos-kpaycms-------------------------------wldos内容付费管理系统
    │  ├─src-----------------------------------内容付费系统源码包
    │  │  ├─main
    │  │  │  ├─java
    │  │  │  │  └─com
    │  │  │  │      └─wldos
    │  │  │  │          ├─book-----------------内容付费实现包
    │  │  │  │          │  ├─controller
    │  │  │  │          │  ├─entity
    │  │  │  │          │  ├─repo
    │  │  │  │          │  ├─service
    │  │  │  │          │  └─vo
    │  │  │  │          └─cms------------------内容管理基础包
    │  │  │  │              ├─controller-------内容管理控制层
    │  │  │  │              ├─dto--------------内容管理数据传输类
    │  │  │  │              ├─entity-----------内容管理实体类
    │  │  │  │              ├─enums------------内容管理枚举值
    │  │  │  │              ├─model------------内容管理模型层
    │  │  │  │              ├─repo-------------内容管理数据仓库层
    │  │  │  │              ├─service----------内容管理业务层
    │  │  │  │              └─vo---------------内容管理视图类
    │  │  │  └─resources-----------------------内容管理资源包
    │  │  └─test-------------------------------内容管理单元测试包
    │  └─target--------------------------------内容管理编译打包输出目录
    现在新增wldos-web模块作为入口模块！
````

#### 功能架构

WLDOS是个软件家族，目前由开发框架、支撑平台和内容管理三大版块构成，组成两个项目模块：WLDOS云应用支撑平台和WLDOS内容管理系统，如下：
![WLDOS支撑平台](https://images.gitee.com/uploads/images/2021/1116/113101_bbed1cc4_7754170.jpeg "wldos-platform.jpeg")
![WLDOS内容管理](https://images.gitee.com/uploads/images/2021/1116/113134_443ea303_7754170.jpeg "在这里输入图片标题")

统一响应json格式如下：

```js
// 用户权限模板：
{
  "data"
:
  {
    "userInfo"
  :
    {
      "id"
    :
      "1502803624724185094",
        "name"
    :
      "nihao",
        "avatar"
    :
      "http://192.168.1.23:8080/store/202108/04110119v2Y66WF9.jpg"
    }
  ,
    "menu"
  :
    [
      {
        "path": "/",
        "icon": "home",
        "name": "首页",
        "id": 100,
        "parentId": 0,
        "isLeaf": true,
        "childCount": 0,
        "index": 0
      }
    ],
      "currentAuthority"
  :
    [
      "user"
    ],
      "isManageSide"
  :
    0
  }
,
  "status"
:
  200,
    "message"
:
  "ok"
}
// 前端路由模板：
menu: [
  {
    path: '/',
    name: 'home',
    icon: 'home',
  },
  {
    path: '/form',
    icon: 'form',
    name: 'form',
    children: [
      {
        name: 'basic-form',
        icon: 'smile',
        path: '/form/basic-form',
      },
    ],
  },]
```  

#### 安装教程

先部署后端：
1.  后端工程下载到本地，用idea打开项目。
2.  安装mysql数据库脚本，生成数据库。数据库脚本请进下方的qq群自行下载。   
    设置文件存储位置，默认E:\\Temp，如要修改，在wldos-platform下找properties中相应选项配置  
    把zone目录下的store.rar解压到设置的文件存储位置，store为指定存储位置下的一级目录，里面有  
    演示数据对应的图片，如果不设置相关图片显示404。
3.  项目更新maven库。服务器端口号默认8080。
4.  在ide控制台执行mvn clean install -pl com.wldos:wldos-web -am -DskipTests
    安装项目；  
    目前开放6个模块：  
    wldos-common：通用模块  
    wldos-framework：wldos开发框架（为了wldos的稳定和安全，采用本地lib/jar分发，请使用安装本地jar的方法安装到maven本地仓库，jar坐标见内部pom文件）  
    wldos-platform-base：wldos支撑平台基础（为了wldos的稳定和安全，采用本地lib/jar分发，请使用安装本地jar的方法安装到maven本地仓库，jar坐标见内部pom文件）  
    wldos-platform: wldos支撑平台    
    wldos-kpaycms: 为支撑平台基础上开发的内容付费管理系统，如果只需要支撑平台，可以仅运行wldos-platform模块，启动后在系统管理后台-资源管理清除cms的菜单；    
    wldos-web：项目入口模块，资源配置，打war包或可执行jar从这里开始。  
    
    安装数据库，数据库脚本在wldos-web/db下，默认mysql5.7，数据库用户名、密码见wldos-web/resources/application-dev.properties。
    数据库正常运行后，以下命令启动项目：  
    安装依赖jar到本地仓库：  
    mvn install:install-file -Dfile=./lib/wldos-framework-1.0-release.jar -DgroupId=com.wldos -DartifactId=wldos-framework -Dversion=1.0 -Dpackaging=jar -DpomFile=./lib/maven/wldos-framework/pom.xml  
    mvn install:install-file -Dfile=./lib/wldos-platform-base-1.0-release.jar -DgroupId=com.wldos -DartifactId=wldos-platform-base -Dversion=1.0 -Dpackaging=jar -DpomFile=./lib/maven/wldos-platform-base/pom.xml  
      
    启动项目：  
    mvn -pl com.wldos:wldos-web spring-boot:run 启动cms和支撑平台。  
    部署前端：
1.  下载本地后，打开前端项目，执行tyarn安装依赖js库。
2.  执行npm start启动前端项目，npm build执行打包编译。前端访问路径：http://localhost:8000
3.  超级管理员admin，密码同名称。

#### 使用说明

1. 浏览器访问localhost:8000,用户名、密码都是admin,注意浏览器要使用谷歌浏览器、Edge，IE11有卡顿。
2. 点击左侧管理菜单，使用系统管理功能。
3. 登陆使用JWT认证。

#### 效果预览

说明：开源版默认包含支撑平台 和 内容管理系统，请根据需要取舍调整。

首页门户:  
![首页门户](https://images.gitee.com/uploads/images/2021/1116/122936_d48d9dbd_7754170.png "home.png")
![网站底部](https://images.gitee.com/uploads/images/2021/1116/124342_beead136_7754170.png "foot.png")
产品详情:  
![无主图](https://images.gitee.com/uploads/images/2021/1116/125137_91191281_7754170.png "prod.png")
![产品详情](https://images.gitee.com/uploads/images/2021/1116/125202_127c5d9f_7754170.png "prod1.png")
登录页:  
![登录页](https://images.gitee.com/uploads/images/2021/1116/123044_19090038_7754170.png "login.png")

应用管理:  
![应用管理](https://images.gitee.com/uploads/images/2021/1116/123128_61bfaf3d_7754170.png "app.png")

资源管理:  
![输入图片说明](https://images.gitee.com/uploads/images/2021/1116/123215_4e193a66_7754170.png "res.png")

角色管理支持继承授权:  
![角色管理](https://images.gitee.com/uploads/images/2021/1116/123914_d5eeb5df_7754170.png "role.png")

继承父级角色的权限不可编辑:  
![角色授权](https://images.gitee.com/uploads/images/2021/1116/123934_8db22348_7754170.png "rolea.png")

租户管理，支持无限级子公司维护:  
![租户管理](https://images.gitee.com/uploads/images/2021/1116/124000_59ef8465_7754170.png "com.png")

业务体系管理，支持企业创建不同业务方面的组织体系:  
![业务体系](https://images.gitee.com/uploads/images/2021/1116/124023_089a4c7d_7754170.png "arch.png")

组织管理，支持组织机构维护、组织赋权、组织人员维护:  
![组织管理](https://images.gitee.com/uploads/images/2021/1116/124049_5159acac_7754170.png "org.png")

组织赋权:  
![组织赋权](https://images.gitee.com/uploads/images/2021/1116/124123_a7da8ac1_7754170.png "orga.png")

用户管理:  
![输入图片说明](https://images.gitee.com/uploads/images/2021/1116/124201_0e8076af_7754170.png "user.png")

多域管理:  
![输入图片说明](https://images.gitee.com/uploads/images/2021/1116/124223_6666477e_7754170.png "domain.png")

内容管理功能截图:  
内容类型管理:  
![类型管理](https://images.gitee.com/uploads/images/2021/1116/124835_4bf503ba_7754170.png "term.png")
内容管理:  
![内容管理](https://images.gitee.com/uploads/images/2021/1116/124948_1a5a1064_7754170.png "chapter.png")
信息发布:  
![信息发布](https://images.gitee.com/uploads/images/2021/1116/124404_e4f3d49e_7754170.png "info.png")
![信息编辑](https://images.gitee.com/uploads/images/2021/1116/124421_5ddd25fa_7754170.png "info1.png")
富文本编辑:  
![内容创作](https://images.gitee.com/uploads/images/2021/1116/124502_6d827797_7754170.png "space.png")
博客系统:  
![博客](https://images.gitee.com/uploads/images/2021/1116/124758_038e3010_7754170.png "blog.png")
![博客文章](https://images.gitee.com/uploads/images/2021/1116/124547_4f0944cf_7754170.png "post.png")
![博客文章底部](https://images.gitee.com/uploads/images/2021/1116/124654_4ec37756_7754170.png "post-foot.png")


#### 开源协议与商用许可  

为生存以及WLDOS平台的独特性可持续，已申请软件著作权证书，采用AGPL协议，企业商用或再分发需另购商业授权。
为更好地推动平台发展，对企业自用提供优惠的商用授权（有需要就说）。

商用或合作请联系业务QQ：306991142 
