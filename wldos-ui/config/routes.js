/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

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
            path: '/doc/',
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
                path: '/admin/sys/calendar',
                component: './sys/calendar',
                name: '节假日日历',
              },
              {
                path: '/admin/sys/reg',
                component: './sys/reg',
              },
              {
                path: '/admin/sys/license-apply',
                component: './sys/license-apply',
                name: '许可证申请',
              },
              {
                path: '/admin/sys/oauth',
                component: './sys/oauth'
              },
              {
                path: '/*',
                component: './admindynamicrouter',
                hideInMenu: true,
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
              { path: '/*', component: './admindynamicrouter', hideInMenu: true },
            ]
          },
          {
            path: '/admin/auth',
            routes: [
              {
                path: '/admin/auth/role',
                component: './sys/role',
              },
              { path: '/*', component: './admindynamicrouter', hideInMenu: true },
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
              { path: '/*', component: './admindynamicrouter', hideInMenu: true },
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
              { path: '/*', component: './admindynamicrouter', hideInMenu: true },
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
              { path: '/*', component: './admindynamicrouter', hideInMenu: true },
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
              { path: '/*', component: './admindynamicrouter', hideInMenu: true },
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
                  { path: '/*', component: './admindynamicrouter', hideInMenu: true },
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
              { path: '/*', component: './admindynamicrouter', hideInMenu: true },
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
              { path: '/*', component: './admindynamicrouter', hideInMenu: true },
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
            path: '/content',
            component: './home/productCategory',
          },
          { // 因umi框架前后两端的路由支持不够理想，采用小类的模式确定分类路由，slug为小类；content 表示内容付费（原 product 合集）
            path: '/content/category/:slugCategory',
            component: './home/productCategory',
          },
          {
            path: '/content/tag/:slugTag',
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
          // 商业模块（社区版注释掉本段代码，不分发 commercial 目录）
          {
            path: '/product',
            component: './commercial/products', name: '产品中心',
          },
          {
            path: '/product-:id.html',
            component: './commercial/products/detail',
          },
          {
            path: '/product/trial',
            component: './commercial/trial',
            name: '试用申请',
          },
          {
            path: '/agreement',
            component: './commercial/agreement', name: '服务协议',
          },
          {
            path: '/checkout',
            component: './commercial/checkout', name: '结算页',
          },
          {
            path: '/order/list',
            component: './commercial/order', name: '我的订单',
          },
          {
            path: '/order/:orderNo',
            component: './commercial/order/detail'
          },
          {
            path: '/license/list',
            component: './commercial/license', name: '我的 License'
          },
          {
            path: '/license/:id',
            component: './commercial/license/detail'
          },
          {
            path: '/ticket/list',
            component: './commercial/ticket', name: '我的工单'
          },
          {
            path: '/ticket/create',
            component: './commercial/ticket/create'
          },
          {
            path: '/ticket/:id',
            component: './commercial/ticket/detail'
          },
          {
            path: '/social-publish',
            component: './commercial/social-publish',
            name: '内容发布',
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
            path: '/content-:bookId.html',
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
            path: '/content-:bookId/:preview',
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
              {
                path: '/account/referral',
                component: './commercial/referral',
                name: '个人推荐中心',
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

/** 递归收集所有带 name 的路由，得到 path -> title，供布局等通用使用（如未配置在菜单时的 tab 标题） */
export function getRouteTitleByPath(routeList) {
  const out = {};
  function walk(list) {
    if (!list || !Array.isArray(list)) return;
    list.forEach((r) => {
      if (r.path && r.name) out[r.path] = r.name;
      if (r.routes) walk(r.routes);
    });
  }
  walk(routeList || []);
  return out;
}

export const routeTitleByPath = getRouteTitleByPath(routes);
export default routes;
