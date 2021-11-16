# wldos
#### 引子
现在元宇宙的概念炒得热火，现在发现其中的很多理念与当初的创想不谋而合，身为码农的人们应该做点什么，后生万学，奋起直追，犹未为晚。那我们就从力所能及的地方开始吧，也许我们并不内行，但我们对未来有自己的憧憬和定义，身为一只码农所能做的，最好是从代码开始输出自己的想法，在系统的研发设计中，一步步探求我们最引以为傲的“乌托邦”，然后实现它，驱动它，直到创造美好的世界！而WLDOS（World Operating System）似乎就是为了这样的目开始的涂鸦，这是一个拙劣而平凡的“小板凳”，我们正在不断打磨它、升级它。

#### 介绍
WLDOS是云服务应用支撑平台，简称WLDOS云应用支撑平台或WLDOS平台，本仓库是wldos平台后端工程。由于是新项目，很适合新手跟随学习。  
WLDOS是一款面向互联网的开放运营软件支撑平台，基于多域架构，支持多租、多应用的SaaS系统软件，本站基于WLDOS搭建，新功能持续开放中。

WLDOS云应用支撑平台，基于springboot二次封装的轻量级快速开发框架，SaaS应用架构，后期支持脱离springboot独立运行。默认支持多域(站)系统，也可以单站模式运行，默认支持多租户运行模式，同时支持单租户模式运行。默认单实例运行，在分布式部署方面支持融入serviceMesh架构或者传统中心化分布式架构，具体部署方式不作强定义，自行规划。

**适合个人建站、企业建站、搭建业务中台的基础、研发平台，适合技术在起步阶段或想在互联网领域开发项目的团队。**

关于发音  
为统一发音，采用汉语拼音：['wo'dou'si] 。

#### 软件架构
框架技术：springboot2.4.6（支持升级到最新版），spring-data-jdbc，定制封装的框架级原生jdbcAPI让开发者自由发挥。

应用架构：前后端分离，前端ReactJs，后端springMVC，JWT认证，无状态，原生兼容springCloud，支持融入serviceMesh。

2.0开始推出webflux架构版。

**1.0核心功能：系统管理（应用、资源、权限、用户、组织、租户、多域、分类），内容管理，信息发布，内容创作、内容付费（在线付费、在线交付）。**

软件架构说明
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
说明：开源版和图示logo不同，开源版默认不含内容管理模块，图示系统是在WLDOS框架基础上开发的内容付费平台。

![自带首页展示图表模板](https://images.gitee.com/uploads/images/2021/0722/221040_3d1c748e_7754170.png "屏幕截图.png")

![登陆页](https://images.gitee.com/uploads/images/2021/0722/220955_aacddd4c_7754170.png "登陆页")

![系统管理dashboard](https://images.gitee.com/uploads/images/2021/0722/221124_05ccdd30_7754170.png "dashboard")

![应用管理，微服务池](https://images.gitee.com/uploads/images/2021/0722/221208_e237514c_7754170.png "应用管理")

![资源管理，支持嵌套菜单、API、静态资源等](https://images.gitee.com/uploads/images/2021/0722/221253_3b3bb6be_7754170.png "资源管理")

![角色管理支持继承授权](https://images.gitee.com/uploads/images/2021/0722/221354_162593c1_7754170.png "角色管理")

![继承父级角色的权限不可编辑](https://images.gitee.com/uploads/images/2021/0722/221443_f811451b_7754170.png "角色管理")

![租户管理，支持无限级子公司维护](https://images.gitee.com/uploads/images/2021/0722/221528_61653ebc_7754170.png "租户管理")

![业务体系管理，支持企业创建不同业务方面的组织体系](https://images.gitee.com/uploads/images/2021/0722/221621_3673d17f_7754170.png "体系管理")

![组织管理，支持组织机构维护、组织赋权、组织人员维护](https://images.gitee.com/uploads/images/2021/0722/221717_b077ef6a_7754170.png "组织管理")

![组织赋权](https://images.gitee.com/uploads/images/2021/0722/221823_33311de9_7754170.png "组织赋权")

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request

#### 交流群

qq群。
![WLDOS交流qq群](https://images.gitee.com/uploads/images/2021/0723/112715_53a377a2_7754170.png "wldos微信")
![WLDOS粉丝抖音](https://images.gitee.com/uploads/images/2021/1114/164709_688ab276_7754170.png "wldos代码创客.png")