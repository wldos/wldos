# wldos
#### 引子
元宇宙的概念炒得火热，现在发现其中的很多理念与当初的创想不谋而合，身为码农的人们应该做点什么，后生万学，奋起直追，犹未为晚。那我们就从力所能及的地方开始吧，也许我们并不内行，但我们对未来有自己的憧憬和定义，身为一只码农所能做的，最好是从代码开始输出自己的想法，在系统的研发设计中，一步步探求我们最引以为傲的“乌托邦”，然后实现它，驱动它，直到创造美好的世界！而WLDOS（World Operating System）似乎就是为了这样的目开始的涂鸦，这是一个拙劣而平凡的“小板凳”，我们正在不断打磨它、升级它。

#### 特点  
仅底层引用了springboot等基础开源包，上层是在自研封装开发框架的基础上开发的支撑平台，优点是依赖少，避免了引入spring全家桶所造成的噪声，按需自研、从零开始有利于开发团队、项目的积累和成长。

#### 介绍
WLDOS云服务应用支撑平台，简称WLDOS云应用支撑平台或WLDOS平台，本仓库是wldos平台后端工程。基础型新项目，适合新手跟随学习。  
WLDOS是一款面向互联网的开放运营软件支撑平台，基于多域架构，支持多租、多应用的SaaS系统软件，wldos.com基于WLDOS搭建，新功能持续开放中。(备案期暂不能访问)

WLDOS云应用支撑平台，基于springboot二次封装的轻量级快速开发框架，SaaS应用架构，后期支持脱离springboot独立运行。默认支持多域(站)系统，也可以单站模式运行，默认支持多租户运行模式，同时支持单租户模式运行。默认单实例运行，在分布式部署方面支持融入serviceMesh架构或者传统中心化分布式架构，具体部署方式不作约束，自行规划。

**适合个人建站、企业建站、搭建业务中台的基础、研发平台，适合技术在起步阶段或想在互联网领域开发项目的团队。**

关于发音  
为统一发音，采用汉语拼音：['wo'dou'si] 。

#### 软件架构
框架技术：springboot2.4.6（支持升级到最新版），spring-data-jdbc，定制封装的框架级原生jdbcAPI让开发者自由发挥。

应用架构：前后端分离，前端ReactJs，后端springMVC，JWT认证，无状态，原生兼容springCloud，支持融入serviceMesh。

2.0开始推出webflux架构版。

**1.0核心功能：系统管理（应用、资源、权限、用户、组织、租户、多域、分类），内容管理，信息发布，内容创作、内容付费（在线付费、在线交付）。**

软件架构说明
####  项目结构
````
wldos
├─wldos-kpaycms──────────────────────内容管理
├─wldos-platform─────────────────────支撑平台
│  ├─src
│  │  ├─main
│  │  │  ├─java
│  │  │  │  └─com
│  │  │  │      └─wldos
│  │  │  │          ├─cms───────────类型管理
│  │  │  │          ├─support───────开发框架
│  │  │  │          ├─system────────系统管理
│  │  │  └─resources────────────────平台配置
````
#### 功能架构  
WLDOS是个软件家族，目前由开发框架、支撑平台和内容管理三大版块构成，组成两个项目模块：WLDOS云应用支撑平台和WLDOS内容管理系统，如下：　　
![WLDOS支撑平台](https://images.gitee.com/uploads/images/2021/1116/113101_bbed1cc4_7754170.jpeg "wldos-platform.jpeg")
![WLDOS内容管理](https://images.gitee.com/uploads/images/2021/1116/113134_443ea303_7754170.jpeg "在这里输入图片标题")

统一响应json格式如下：

```js
// 用户权限模板：
{
    "data":{
        "userInfo":{
            "id":"1502803624724185094",
                "name":"nihao",
                "avatar":"http://192.168.1.23:8080/store/202108/04110119v2Y66WF9.jpg"
        },
        "menu":[
            {
                "path":"/",
                "icon":"home",
                "name":"首页",
                "id":100,
                "parentId":0,
                "isLeaf":true,
                "childCount":0,
                "index":0
            }
        ],
        "currentAuthority":[
            "user"
        ],
        "isManageSide":0
    },
    "status":200,
    "message":"ok"
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
4.  在idea控制台执行mvn clean install -Dmaven.test.skip=true安装项目；  
    目前分2个模块：wldos-platform为wldos支撑平台，wldos-kpaycms为支撑平台基础上  
    开发的内容付费管理系统，如果只需要支撑平台，可以仅运行wldos-platform模块，启动  
    后在系统管理后台-资源管理清除cms的菜单；  
    部署前端：
1.  下载本地后，打开前端项目，执行tyarn安装依赖js库。
2.  执行npm start启动前端项目，npm build执行打包编译。前端访问路径：http://localhost:8000
3.  超级管理员admin，密码同名称。

#### 使用说明

1.  浏览器访问localhost:8000,用户名、密码都是admin,注意浏览器要使用谷歌浏览器、Edge，IE11有卡顿。
2.  点击左侧管理菜单，使用系统管理功能。
3.  登陆使用JWT认证。

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


#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request

#### 交流群

qq群。
![WLDOS交流qq群](https://images.gitee.com/uploads/images/2021/0723/112715_53a377a2_7754170.png "wldos微信")
![WLDOS粉丝抖音](https://images.gitee.com/uploads/images/2021/1114/164709_688ab276_7754170.png "wldos代码创客.png")
![微信公众号](https://images.gitee.com/uploads/images/2021/1118/085852_e889e669_7754170.jpeg "wldos订阅号.jpg")