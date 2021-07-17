# wldos

#### 介绍
WLDOS是一款互联网运营平台，本仓库是wldos后端工程。  
WLDOS开发平台，基于springboot实现轻量级快速开发框架，SaaS应用架构。默认支持多租户运行模式，同时支持关闭以单租户模式运行。默认单实例运行，在分布式部署方面支持融入serviceMesh架构或者传统中心化分布式架构。
####关于发音  
同WorldOS。  
#### 软件架构
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
// 前端菜单模板：
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
            {
                name: 'step-form',
                icon: 'smile',
                path: '/form/step-form',
            },
            {
                name: 'advanced-form',
                icon: 'smile',
                path: '/form/advanced-form',
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
                    {
                        name: 'projects',
                        icon: 'smile',
                        path: '/list/search/projects',
                    },
                    {
                        name: 'applications',
                        icon: 'smile',
                        path: '/list/search/applications',
                    },
                ],
            },
            {
                name: 'table-list',
                icon: 'smile',
                path: '/list/table-list',
            },
            {
                name: 'basic-list',
                icon: 'smile',
                path: '/list/basic-list',
            },
            {
                name: 'card-list',
                icon: 'smile',
                path: '/list/card-list',
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

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)

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