# wldos

#### 介绍
WLDOS是一款互联网运营平台，本仓库是wldos后端工程。由于是新项目，很适合新手跟随学习。  
WLDOS开发平台，基于springboot实现轻量级快速开发框架，SaaS应用架构。默认支持多租户运行模式，同时支持关闭以单租户模式运行。默认单实例运行，在分布式部署方面支持融入serviceMesh架构或者传统中心化分布式架构。  

#### 关于发音  
英：[wel'dɑ:s] 美：[wɛl'dɑ:s]。  

#### 软件架构  
框架技术：springboot2.4.6，spring-data-jdbc，定制封装。  

应用架构：前后端分离，前端ReactJs，后端springMVC，JWT认证，无状态单实例SaaS架构，兼容springCloud，支持融入serviceMesh。  

软件架构说明
统一响应json格式如下：

```js
// 用户权限模板：
{
    "data":{
        "userInfo":{
            "id":"1502803624724185094",
                "name":"nihao",
                "avatar":"https://pic.zhiletu.com/2021/04/zhiletudouyin-e1618196547818-150x150.png"
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
    },
    {
        path: '/list',
        icon: 'table',
        name: 'list',
        children: [
            {
                path: '/list/search',
                name: 'search-list',
                children: [
                    {
                        name: 'articles',
                        icon: 'smile',
                        path: '/list/search/articles',
                    },
                ],
            },
        ],
    },]
```  

#### 安装教程
先部署后端：  
1.  后端工程下载到本地，用idea打开项目。  
2.  安装mysql数据库脚本，生成数据库。  
3.  项目更新maven库。  
4.  在idea控制台执行mvn spring-boot:run运行项目
再部署前端：  
1.  下载本地后，打开前端项目，执行npm install安装依赖库。
2.  执行npm start启动前端项目。    

#### 使用说明

1.  浏览器访问localhost:8000,用户名、密码都是admin,注意浏览器要是有谷歌浏览器。
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

限于篇幅过长，详情关注：[Java语言开发的SaaS版前后端分离开发平台WLDOS](https://www.zhiletu.com/archives-10982.html)  

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.4.3/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.4.3/maven-plugin/reference/html/#build-image)
* [Spring Security](https://docs.spring.io/spring-boot/docs/2.4.3/reference/htmlsingle/#boot-features-security)
* [Java Mail Sender](https://docs.spring.io/spring-boot/docs/2.4.3/reference/htmlsingle/#boot-features-email)
* [JDBC API](https://docs.spring.io/spring-boot/docs/2.4.3/reference/htmlsingle/#boot-features-sql)

### Guides
The following guides illustrate how to use some features concretely:

* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [Accessing Relational Data using JDBC with Spring](https://spring.io/guides/gs/relational-data-access/)
* [Managing Transactions](https://spring.io/guides/gs/managing-transactions/)
* [Using Spring Cloud Gateway](https://github.com/spring-cloud-samples/spring-cloud-gateway-sample)