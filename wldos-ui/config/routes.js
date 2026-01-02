const routes = [
  {
    path: '/',
    component: '../layouts/BlankLayout',
    routes: [
      {
        path: '/user',
        component: '../layouts/UserLayout',
        routes: [
          {
            path: '/user/login',
            component: './user/login',
          },
          {
            path: '/user',
            redirect: '/user/login',
          },
          {
            path: '/user/auth/:authType',
            component: './user/login/authcode',
          },
          {
            path: '/user/auth/login/:authType',
            component: './user/login/auth2'
          },
          {
            path: '/user/register-result',
            component: './user/register-result',
          },
          {
            path: '/user/register',
            component: './user/register',
          },
          {
            path: '/user/active/verify=:verify',
            component: './user/active'
          },
          {
            path: '/user/forget',
            component: './user/forget',
          },
          // 动态占位路由：用于承载后端动态返回的页面（菜单融合显示，页面由 dynamicrouter 渲染）
          {
            path: '/*',
            component: './dynamicrouter',
            hideInMenu: true,
          },
          {
            component: '404',
          },
        ],
      },
      {
        path: '/space',
        component: '../layouts/SecurityLayout',
        routes: [
          {
            path: '/space',
            redirect: '/space/book'
          },
          {
            path: '/space/book',
            component: './book',
          },
          {
            path: '/space/book/:bookId',
            component: './book',
          },
          {
            path: '/space/book/:bookId/chapter/:chapterId',
            component: './book',
          },
          {
            component: '404',
          },
        ],
      },
      {
        path: '/doc',
        routes: [
          {
            path: '/doc',
            redirect: '/doc/book'
          },
          {
            path: '/doc/book',
            component: './doc',
          },
          {
            path: '/doc/book/:bookId.html',
            component: './doc',
          },
          {
            path: '/doc/book/:bookId/chapter/:chapterId.html',
            component: './doc',
          },
          {
            component: '404',
          },
        ],
      },
      {
        path: '/admin',
        component: '../layouts/AdminLayout',
        routes: [
          {
            path: '/admin',
            component: './sys/monitor',
          },
          {
            path: '/admin/sys',
            component: './sys/monitor',
          },
          {
            path: '/admin/sys',
            routes: [
              {
                path: '/admin/sys/options',
                component: './sys/config',
              },
              {
                path: '/admin/sys/reg',
                component: './sys/reg',
              },
              {
                path: '/admin/sys/oauth',
                component: './sys/oauth'
              },
            ],
          },
          {
            path: '/admin/res',
            routes: [
              {
                path: '/admin/res/app',
                component: './sys/app',
              },
              {
                path: '/admin/res/res',
                component: './sys/res',
              },
              {
                path: '/admin/res/front',
                component: './sys/res/frontmenu',
              },
            ]
          },
          {
            path: '/admin/auth',
            routes: [
              {
                path: '/admin/auth/role',
                component: './sys/role',
              },
            ]
          },
          {
            path: '/admin/organ',
            routes: [
              {
                path: '/admin/organ/architecture',
                component: './sys/organ/architecture',
              },
              {
                path: '/admin/organ/com',
                component: './sys/com',
              },
              {
                path: '/admin/organ/arch',
                component: './sys/arch',
              },
              {
                path: '/admin/organ/org',
                component: './sys/org',
              },
              {
                path: '/admin/organ/user',
                component: './sys/user',
              },
            ]
          },
          {
            path: '/admin/dom',
            routes: [
              {
                path: '/admin/dom/category',
                component: './sys/category',
              },
              {
                path: '/admin/dom/tag',
                component: './sys/tag',
              },
              {
                path: '/admin/dom/domain',
                component: './sys/domain',
              },
            ],
          },
          {
            path: '/admin/book',
            routes: [
              {
                path: '/admin/book/model',
                component: './sys/category',
              },
              {
                path: '/admin/book/lib',
                component: './sys/category',
              },
              {
                path: '/admin/book/sale',
                component: './sys/category',
              },
              {
                path: '/admin/book/audit',
                component: './sys/category',
              },
            ],
          },
          {
            path: '/admin/info',
            routes: [
              {
                path: '/admin/info/class',
                component: './sys/category',
              },
              {
                path: '/admin/info/flow',
                component: './sys/category',
              },
              {
                path: '/admin/info/order',
                component: './sys/category',
              },
              {
                path: '/admin/info/audit',
                component: './sys/category',
              },
            ],
          },
          {
            path: '/admin/cms',
            routes: [
              {
                path: '/admin/cms/pub',
                routes: [
                  {
                    path: '/admin/cms/pub/chapter',
                    component: './sys/article',
                  },
                  {
                    path: '/admin/cms/pub/book',
                    component: './sys/book',
                  },
                  {
                    path: '/admin/cms/pub/info',
                    component: './sys/info',
                  },
                ],
              },
              {
                path: '/admin/cms/comment',
                component: './sys/comment',
              },
              {
                path: '/admin/cms/media',
                component: './sys/category',
              },
              {
                path: '/admin/cms/page',
                component: './sys/category',
              },
              {
                path: '/admin/cms/talk',
                component: './sys/category',
              },
              {
                path: '/admin/cms/appear',
                component: './sys/category',
              },
              {
                path: '/admin/cms/plugin',
                component: './sys/category',
              },
              {
                path: '/admin/cms/set',
                component: './sys/category',
              },
            ],
          },
          {
            path: '/admin/collector',
            routes: [
              {
                path: '/admin/collector/list',
                component: './sys/collector',
              },
              {
                path: '/admin/collector/rule',
                component: './sys/collector/rule',
              },
            ],
          },
          // 管理端动态占位路由：用于承载后端动态返回的页面（菜单融合显示，页面由 AdminDynamicRouter 渲染）
          {
            path: '/*',
            component: './admindynamicrouter',
            hideInMenu: true,
          },
          {
            component: '404',
          },
        ],
      },
      {
        path: '/',
        component: '../layouts/BasicLayout',
        routes: [
          {
            path: '/index.html',
            redirect: '/',
          },
          {
            path: '/index.htm',
            redirect: '/',
          },
          {
            path: '/',
            component: './home',
          },
          {
            path: '/page/:page',
            component: './home',
          },
          {
            path: '/category',
            component: './home/category',
          },
          {
            path: '/product',
            component: './home/productCategory',
          },
          { // 因umi框架前后两端的路由支持不够理想，采用小类的模式确定分类路由，slug为小类，小类之上的所有父级类别在url中不体现，仅体现在面包屑中
            path: '/product/category/:slugCategory',
            component: './home/productCategory',
          },
          {
            path: '/product/tag/:slugTag',
            component: './home/productTag',
          },
          {
            path: '/archives',
            component: './home/archivesCategory',
          },
          {
            path: '/archives/page/:page',
            component: './home/archivesCategory',
          },
          {
            path: '/archives/category/:slugCategory',
            component: './home/archivesCategory',
          },
          {
            path: '/archives/category/:slugCategory/page/:page',
            component: './home/archivesCategory',
          },
          {
            path: '/archives/tag/:slugTag',
            component: './home/archivesTag',
          },
          {
            path: '/archives/tag/:slugTag/page/:page',
            component: './home/archivesTag',
          },
          {
            path: '/archives-author/:userId.html',
            component: './account/center/components/Articles'
          },
          {
            path: '/info',
            component: './home/infoCategory',
          },
          {
            path: '/info/page/:page',
            component: './home/infoCategory',
          },
          {
            path: '/info/category/:slugCategory',
            component: './home/infoCategory',
          },
          {
            path: '/info/category/:slugCategory/page/:page',
            component: './home/infoCategory',
          },
          {
            path: '/info/tag/:slugTag',
            component: './home/infoTag',
          },
          {
            path: '/info/tag/:slugTag/page/:page',
            component: './home/infoTag',
          },
          {
            path: '/info-author/:userId.html',
            component: './account/center/components/InfoAuthor'
          },
          {
            path: '/search',
            component: './search',
          },
          {
            path: '/info/pub/create',
            component: './book/create',
          },
          {
            path: '/xiupu',
            component: './book/create/pub',
          },
          {
            path: '/product-:bookId.html',
            component: './book/detail',
          },
          {
            path: '/book-:bookId.html',
            component: './book/read',
          },
          {
            path: '/element-:id.html',
            component: './book/read/element',
          },
          {
            path: '/info-:infoId.html',
            component: './book/detail/info',
          },
          {
            path: '/archives-:id.html',
            component: './book/article',
          },
          {
            path: '/product-:bookId/:preview',
            component: './book/detail',
          },
          {
            path: '/info-:infoId/:preview',
            component: './book/detail/info',
          },
          {
            path: '/archives-:id/:preview',
            component: './book/article',
          },
          {
            path: '/element-:id/:preview',
            component: './book/read/element',
          },
          {
            path: '/account',
            routes: [
              {
                path: '/',
                redirect: '/account/center',
              },
              {
                path: '/account/center',
                component: './account/center',
              },
              {
                path: '/account/settings',
                component: './account/settings',
              },
            ],
          },
          {
            path: '/:slug',
            component: './book/page',
          },
          // 动态占位路由：用于承载后端动态返回的页面（菜单融合显示，页面由 dynamicrouter 渲染）
          {
            path: '/*',
            component: './dynamicrouter',
            hideInMenu: true,
          },
          {
            component: '404',
          },
        ],
      },
    ],
  },
];

export default routes;
