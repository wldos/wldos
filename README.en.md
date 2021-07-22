# wldos

#### Description
Wldos ([wel'dɑ:s] [wɛl'dɑ:s]) is an Internet operation platform. This warehouse is a back-end project of wldos. As it is a new project, it is very suitable for novices to follow and learn.
Wldos development platform, based on springboot to achieve lightweight rapid development framework, SaaS application architecture. By default, it supports multi tenant operation mode and single tenant operation mode. By default, it runs in a single instance. In terms of distributed deployment, it supports integrating servicemesh architecture or traditional centralized distributed architecture.

#### Software Architecture
Framework technology: spring boot 2.4.6, spring data JDBC, custom encapsulation.

Application Architecture: front end separation, front end reactjs, back end spring MVC, JWT authentication, stateless single instance SaaS architecture, compatible with spring cloud, supporting integration into servicemesh.

The unified response JSON format of software architecture description is as follows:

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
            {
                name: 'table-list',
                icon: 'smile',
                path: '/list/table-list',
            },
        ],
    },]
```

#### Installation

Deploy the back end first:

Download the back-end project locally and open the project with idea.

Install MySQL database script to generate database.

The project updates the Maven library.

Execute MVN spring boot: run on the idea console to run the project and then deploy the front end:

After downloading the local database, open the front-end project and execute NPM install to install the dependency library.

Execute NPM start to start the front-end project.

#### Instructions

Browser access localhost: 8000, user name, password are admin, pay attention to the browser if there is Google browser.

Click the management menu on the left to use the system management function.

Login using JWT authentication.

Participation and contribution

#### Effect display

See: 《wldos, the front end and back end development platform of SaaS developed by java language》 https://www.zhiletu.com/archives-10982.html


