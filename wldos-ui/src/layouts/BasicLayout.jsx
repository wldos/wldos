/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import ProLayout from '@ant-design/pro-layout';
import React, {useEffect, useRef, useState} from 'react';
import {connect, history, Link, useIntl} from 'umi';
import {BackTop} from 'antd';
import RightContent from '@/components/GlobalHeader/RightContent';
import WldosFooterDom from '@/layouts/foot';
import styles from '@/wldos.less';
import { renderIcon } from '@/utils/iconLibrary';
import { setPortalMenuTree } from '@/utils/portalMenuVisit';
import {getHome, wldosHeader} from "@/utils/utils";
import { isDesktopEmbedded, mergeDesktopShellSite, resolveDesktopLayoutTitle, resolveDesktopProLayoutBrand } from '@/utils/desktopEmbeddedBridge';
import { stripHiddenNavMenus } from '@/pages/sys/res/menuFormRules';


const BasicLayout = (props) => {
  const {
    dispatch,
    children,
    location = {
      pathname: '/',
    },
    menuData,
    tdk,
    site,
    loading,
    settings,
  } = props;

  const [seo, setSeo] = useState({title: '', keywords: '', description: '', logo: '', favicon: '', crumbs: []});
  const [collapsed, setCollapsed] = useState(false);
  const [isHome, setHome] = useState(false);
  const [brandingRev, setBrandingRev] = useState(0);
  const closeBread = true;
  /**
   * 菜单和路由的区别在于：路由表示请求资源的路径，菜单表示功能的路径，路由包括前端路由(组件)和后端路由(api)，先有路由后有菜单，控制路由权限包括前后端两个方面，所有路由权限和菜单权限都统一在后端定制
   * 因为权限已经在后端过滤，返回的都是有权限菜单，不必检查权限。（要求：除动态路由外，静态路由应该和菜单url相同，操作权限在API侧判断，主要包括数据权限和角色权限，数据权限发生在域、业务类型、租户、组织的边界）
   */

  /** 过滤头像/桌面槽位与「页面菜单」(route) — 后者仅路由与页内链，不进顶栏 */
  const filterMainNavMenu = (menus) => {
    if (!menus || !Array.isArray(menus)) return [];
    const regionFiltered = menus
      .filter((item) => item.menuRegion !== 'nav_avatar' && item.menuRegion !== 'desktop_home')
      .map((item) => ({
        ...item,
        children: item.children && item.children.length ? filterMainNavMenu(item.children) : item.children,
      }));
    return stripHiddenNavMenus(regionFiltered);
  };

  // 菜单 name 由后台 wo_resource.resource_name 直出（已是站点语言文案，如中文/英文），
  // 不再走 ProLayout 的 i18n 二次解析。每项必须显式 locale: false，
  // 否则 ProLayout 会拼出 `menu.${name}` 调 formatMessage 找翻译，
  // 控制台刷出 `[React Intl] Missing message: "menu.创作"` 这类无意义 warn。
  // 全局 menu={{ locale: false }} 也能压住，但顶布局会引发"刷新时多个齿轮效果"，
  // 所以只在每个 item 上关，不全局关。
  const menuHandle = (menus) =>
    menus.map((item) => ({
      ...item,
      name: item.name,
      locale: false,
      icon: renderIcon(item.icon),
      children: item.children && menuHandle(item.children),
    }));

  const menuDataRef = useRef([]);
  // 用户刚打开网站，应该是游客，触发请求，后台判断header中的token,此处进行权限校验，通过才渲染，@todo 权限校验提升到app.js/render方法，在render获取当前用户的权限数据
  // 根据location 和 menuData确定是否有菜单权限，无权限返回不渲染，根据按钮权限确定页面上某个按钮是否有操作权限，根据按钮权限简化为不可见即不可操作
  useEffect(() => {
    if (dispatch) {
      dispatch({
        type: 'user/fetchCurrent',
      });
      dispatch({
        type: 'user/fetchSite',
      });
    }
  }, []);

  useEffect(() => {
    const onBranding = () => setBrandingRev((v) => v + 1);
    window.addEventListener('wldos:desktop-branding-ready', onBranding);
    return () => window.removeEventListener('wldos:desktop-branding-ready', onBranding);
  }, []);

  useEffect(() => { // 获取域信息，依赖 site/tdk/location 以便 site 异步加载后更新 keywords 等
    const homeFlag = location.pathname === getHome();
    const sourceSite = isDesktopEmbedded() ? mergeDesktopShellSite(site) : site;
    if (sourceSite) {
      const {siteName, siteTitle, siteKeyword, siteDescription, siteLogo, favicon} = sourceSite;
      const pageDesc = siteTitle ? ` - ${siteName}` : '';
      const pageTitle = homeFlag ? (siteTitle ?? '') : (tdk.title ? `${tdk.title}${pageDesc}` : '');
      // 定义seo标签，首页取默认配置，内容页需要传递
      setSeo({
        // eslint-disable-next-line no-nested-ternary
        title: isDesktopEmbedded()
          ? resolveDesktopLayoutTitle(sourceSite, pageTitle)
          : pageTitle,
        keywords: homeFlag ? (siteKeyword ?? '') : (tdk?.keywords ?? siteKeyword ?? ''),
        description: homeFlag ? (siteDescription ?? '') : (tdk?.description ?? siteDescription ?? ''),
        logo: siteLogo,
        favicon,
        crumbs: tdk?.crumbs || [],
      });
      setHome(homeFlag); // 首页，根据相关参数执行重定向操作
    }
    // 自动回到顶部
    document?.getElementById('root').scrollIntoView(true);
  }, [tdk, site, location.pathname, brandingRev]);

  /**
   * init variables
   */
  const handleMenuCollapse = (payload) => {
    setCollapsed(payload);
  };

  const {formatMessage} = useIntl();

  const isHomePage = location.pathname === getHome();
  const desktopEmbedded = isDesktopEmbedded();
  const displaySite = desktopEmbedded ? mergeDesktopShellSite(site) : site;
  const proBrand = resolveDesktopProLayoutBrand(site);
  const layoutLogo = desktopEmbedded ? (proBrand.logo || false) : seo.logo;
  const layoutTitle = desktopEmbedded ? proBrand.title : seo.title;

  // B2「数字管家桌面壳 headless 路由」：内容叠层/侧滑面板以 `?shellChrome=none` 挂载 wldos-ui 页面时，
  // 页面本身不需要门户的顶部导航/页脚——那一整条 chrome（汉堡菜单、搜索、通知、头像下拉、分享、
  // 主题切换）是给独立浏览门户用的，桌面壳自己已经有原生顶栏承担这些职责，两套 chrome 叠在一起
  // 就是账号管理侧滑面板里出现的那条多余导航条。只跳过 ProLayout 外壳，children（业务页面本身）
  // 照常渲染。
  const isHeadless = new URLSearchParams(location.search || '').get('shellChrome') === 'none';
  if (isHeadless) {
    return (
      <>
        {wldosHeader(seo)}
        {children}
      </>
    );
  }

  return (
    <>
      {wldosHeader(seo)}
      <ProLayout
        className={`${styles.topNavWldos} ${closeBread ? styles.topNavContentNoBread : styles.topNavContent} ${isHomePage ? styles.homeLayout : ''}`}
        logo={layoutLogo || false}
        formatMessage={formatMessage} // 关掉国际化输出防止未作国际化处理时报错，不从menu改是因为top布局时menu设置menu={{loading, locale: false}}会导致刷新时多个齿轮效果
        menuDataRender={(md) => menuHandle(filterMainNavMenu(md && md.length > 0 ? menuData.pushAll(md) : menuData))}
        {...settings}
        menu={loading}
        collapsed={collapsed}
        onCollapse={handleMenuCollapse}
        onMenuHeaderClick={() => history.push('/')}
        title={layoutTitle}
        pageTitleRender = {(titleProps) => { // 完全自定义标题,冗余设置title属性是为了压制antd的默认标题
          return titleProps.title;
        }}
        menuItemRender={(menuItemProps, defaultDom) => {
          // 自定义菜单项的渲染方法
          if (menuItemProps.isUrl) {
            return defaultDom;
          }

          // 如果有路径且不是当前页面，添加链接
          if (menuItemProps.path && location && location.pathname !== menuItemProps.path) {
            return <Link to={menuItemProps.path} target={menuItemProps.target || '_self'}>{defaultDom}</Link>;
          }

          return defaultDom;
        }}
        subMenuItemRender={(menuItemProps, defaultDom) => {
          // 父级菜单标题渲染：保持 name 为纯文本用于提示，但标题可点击
          if (menuItemProps.path) {
            return <Link to={menuItemProps.path} target={menuItemProps.target || '_self'}>{defaultDom}</Link>;
          }
          return defaultDom;
        }}
        menuHeaderRender={(lgo/* , title */) => {
          if (layoutLogo) {
            return lgo;
          }
          if (desktopEmbedded && layoutTitle) {
            return (
              <span style={{ fontSize: 18, fontWeight: 600 }}>
                {String(layoutTitle).split(' - ')[0]}
              </span>
            );
          }
          return '';
        }}
        // pageTitleRender={(siteSettings) => siteSettings.site}
        footerRender={() => (isHome || tdk?.pubType === 'page') && <WldosFooterDom/>} // 如果组件内使用了connect，这里需要返回react组件(首字母大写)，否则直接返回组件变量
        rightContentRender={() => <RightContent />}
        postMenuData={(menu) => { // 在显示前对菜单数据进行查看，修改不会触发重新渲染
          const m = menu || [];
          menuDataRef.current = m;
          setPortalMenuTree(m);
          return m;
        }}
      >
        {children}
      </ProLayout>
      <BackTop/>
    </>
  );
};

export default connect(({settings, user, loading}) => ({
  tdk: user.tdk,
  site: user.site,
  menuData: user.menuData,
  loading: loading.models.user,
  settings: {
    ...settings,
  }
}))(BasicLayout);

