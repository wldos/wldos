import ProLayout from '@ant-design/pro-layout';
import React, {useEffect, useRef, useState} from 'react';
import {connect, history, Link} from 'umi';
import {BackTop} from 'antd';
import RightContent from '@/components/GlobalHeader/RightContent';
import WldosFooterDom from '@/layouts/foot';
import styles from '@/wldos.less';
import { renderIcon } from '@/utils/iconLibrary';
import {getHome, wldosHeader} from "@/utils/utils";


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
  const closeBread = true;
  /**
   * 菜单和路由的区别在于：路由表示请求资源的路径，菜单表示功能的路径，路由包括前端路由(组件)和后端路由(api)，先有路由后有菜单，控制路由权限包括前后端两个方面，所有路由权限和菜单权限都统一在后端定制
   * 因为权限已经在后端过滤，返回的都是有权限菜单，不必检查权限。（要求：除动态路由外，静态路由应该和菜单url相同，操作权限在API侧判断，主要包括数据权限和角色权限，数据权限发生在域、业务类型、租户、组织的边界）
   */

  const menuHandle = (menus) =>
    menus.map((item) => ({
      ...item,
      // 保持 name 为纯文本，避免悬浮提示问题
      name: item.name,
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

  useEffect(() => { // 获取域信息
    const homeFlag = location.pathname === getHome();
    if (site) {
      const {siteName, siteTitle, siteKeyword, siteDescription, siteLogo, favicon} = site;
      const pageDesc = siteTitle ? ` - ${siteName}` : '';
      // 定义seo标签，首页取默认配置，内容页需要传递
      setSeo({
        // eslint-disable-next-line no-nested-ternary
        title: homeFlag ? (siteTitle?? '') : (tdk.title ? `${tdk.title}${pageDesc}` : ''),
        keywords: homeFlag ? siteKeyword : tdk.keywords || siteKeyword,
        description: homeFlag ? siteDescription : tdk.description || siteDescription,
        logo: siteLogo,
        favicon,
        crumbs: tdk?.crumbs || [],
      });
      setHome(homeFlag); // 首页，根据相关参数执行重定向操作
    }
    // 自动回到顶部
    document?.getElementById('root').scrollIntoView(true);
  }, [tdk]);

  /**
   * init variables
   */
  const handleMenuCollapse = (payload) => {
    setCollapsed(payload);
  };

  return (
    <>
      {wldosHeader(seo)}
      <ProLayout
        className={`${styles.topNavWldos} ${closeBread ? styles.topNavContentNoBread : styles.topNavContent}`}
        logo={seo.logo}
        // formatMessage={formatMessage} // 关掉国际化输出防止未作国际化处理时报错，不从menu改是因为top布局时menu设置menu={{loading, locale: false}}会导致刷新时多个齿轮效果
        menuDataRender={(md) => menuHandle(md && md.length>0 ? menuData.pushAll(md) : menuData)}
        {...settings}
        menu={loading}
        collapsed={collapsed}
        onCollapse={handleMenuCollapse}
        onMenuHeaderClick={() => history.push('/')}
        title={seo.title}
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
          if (!seo.logo) { return ''; }
          return lgo;
        }}
        // pageTitleRender={(siteSettings) => siteSettings.site}
        footerRender={() => (isHome || tdk?.pubType === 'page') && <WldosFooterDom/>} // 如果组件内使用了connect，这里需要返回react组件(首字母大写)，否则直接返回组件变量
        rightContentRender={() => <RightContent />}
        postMenuData={(menu) => { // 在显示前对菜单数据进行查看，修改不会触发重新渲染
          menuDataRef.current = menu || [];
          return menu || [];
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

