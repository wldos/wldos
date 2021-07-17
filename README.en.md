# wldos

#### Description
WLDOS[worldAS]是一款互联网运营平台，本仓库是wldos后端工程。
WLDOS开发平台，基于springboot实现轻量级快速开发框架，SaaS应用架构。默认支持多租户运行模式，同时支持关闭以单租户模式运行。默认单实例运行，在分布式部署方面支持融入serviceMesh架构或者传统中心化分布式架构。

#### Software Architecture
Software architecture description

union form response json format as below：

```js
// user authority template：
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
// front menu template：
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

#### Installation

1.  xxxx
2.  xxxx
3.  xxxx

#### Instructions

1.  xxxx
2.  xxxx
3.  xxxx

#### Contribution

1.  Fork the repository
2.  Create Feat_xxx branch
3.  Commit your code
4.  Create Pull Request


#### Gitee Feature

1.  You can use Readme\_XXX.md to support different languages, such as Readme\_en.md, Readme\_zh.md
2.  Gitee blog [blog.gitee.com](https://blog.gitee.com)
3.  Explore open source project [https://gitee.com/explore](https://gitee.com/explore)
4.  The most valuable open source project [GVP](https://gitee.com/gvp)
5.  The manual of Gitee [https://gitee.com/help](https://gitee.com/help)
6.  The most popular members  [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
