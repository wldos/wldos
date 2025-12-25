import ProLayout from '@ant-design/pro-layout';
import React, {useEffect, useRef, useState} from 'react';
import {connect, history, Link, useIntl} from 'umi';
import {BackTop, message} from 'antd';
import {FolderOutlined, FileTextOutlined} from '@ant-design/icons';
import RightContent from '@/components/GlobalHeader/RightContent';
import styles from '@/wldos.less';
import { getHome, wldosHeader} from "@/utils/utils";
import Document from "@/pages/doc/components/Document";

const Doc = (props) => {
  const {
    dispatch,
    location = {
      pathname: '/',
    },
    menuData,
    currentDoc,
    currentChapter,
    settings,
    loading,
    site,
    tdk,
  } = props;
  const [seo, setSeo] = useState({title: '', keywords: '', description: '', logo: '', favicon: '', crumbs: []});
  const [collapsed, setCollapsed] = useState(false);
  const [isMobile, setIsMobile] = useState(false);
  const [dataLoaded, setDataLoaded] = useState(false);

  const queryDoc = (dispatch, callback) => {
    if (dataLoaded) {
      // 如果已经加载过数据，直接执行回调
      if (callback) {
        callback({message: 'ok', data: {rows: menuData}});
      }
      return;
    }

    dispatch({
      type: 'doc/fetchDoc',
      payload: {
        current: 1,
        pageSize: 50, // 默认50个文档
        sorter: {"createTime":"ascend"}
      },
      callback: async (res) => {
        if (res?.message === 'ok' && res?.data?.rows?.length > 0) {
          // 为每个文档加载章节信息，确保菜单能正确显示子级
          const docPromises = res.data.rows.map(doc => 
            dispatch({
              type: 'doc/fetchCurrentDoc',
              payload: { bookId: doc.id },
              callback: null
            })
          );
          
          // 等待所有文档的章节信息加载完成
          await Promise.all(docPromises);
          setDataLoaded(true);
          
          // 数据加载完成，不需要手动设置展开状态
        }
        
        if (callback) {
          callback(res);
        }
      },
    });
  };

  const queryCurrentDoc = (dispatch, params, callback) => {
    dispatch({
      type: 'doc/fetchCurrentDoc',
      payload: {
        bookId: params.bookId,
      },
      callback,
    });
  };

  const queryCurrentChapter = (dispatch, params) => {
    dispatch({
      type: 'doc/fetchCurrentChapter',
      payload: {
        bookId: params.bookId,
        chapterId: params.chapterId,
      },
    });
  };

  const callback4Chapter = (dispatch, resp) => {
    if (resp?.message === 'ok') {
      const {id: bookId, chapter} = resp.data;
      if (chapter?.length > 0) {
        const {id: chapterId} = chapter[0];
        history.push({
          pathname: `/doc/book/${bookId}/chapter/${chapterId}.html`,
        });
      } else {
        queryCurrentChapter(dispatch, {bookId, chapterId: ''});
      }
    }
  };

  const query = (dispatch, params, path) => {
    if (path === '/doc/book') {
      // 只有在没有菜单数据时才查询
      if (menuData.length === 0) {
        queryDoc(dispatch, (res) => {
          const {data: {rows = [],},} = res;
          if (rows?.length > 0) {
            history.push({
              pathname: `/doc/book/${rows[0].id}.html`,
            });
          }
        });
      } else {
        // 如果已有菜单数据，直接跳转到第一个文档
        const firstDoc = menuData[0];
        if (firstDoc) {
          history.push({
            pathname: `/doc/book/${firstDoc.id}.html`,
          });
        }
      }
    } else if (path === '/doc/book/:bookId/chapter/:chapterId.html') {
      if (menuData.length === 0) {
        queryDoc(dispatch, null);
      }
      if (!currentDoc.id || currentDoc.chapter.length <= 1) {
        queryCurrentDoc(dispatch, params, null);
      }
      queryCurrentChapter(dispatch, params);
    } else { // match '/doc/book/:bookId'
      if (menuData.length === 0) {
        queryDoc(dispatch, null);
      }
      queryCurrentDoc(dispatch, params, (res) => callback4Chapter(dispatch, res), );
    }
  }

  const docClick = (key, props) => {
    const {dispatch, curId, curLen} = props;
    if (curId !== key || curLen <= 1) {
      queryCurrentDoc(dispatch, {bookId: key}, (res) => callback4Chapter(dispatch, res), );
    }
  }

  const menuHandle = (menus, parentId, isChapter, props) =>
    menus.map((item) => {
      const hasChildren = item.chapter && item.chapter.length > 0;
      return {
        ...item,
        name: item.pubTitle ? (isChapter ? item.pubTitle : <Link onClick={() => docClick(item.id, props)}>{item.pubTitle}</Link>) : '无标题',
        path: isChapter ? `/doc/book/${parentId}/chapter/${item.id}.html` : `/doc/book/${item.id}.html`,
        children: hasChildren ? menuHandle(item.chapter, item.id, true) : undefined,
        // 为有子级的文集添加折叠图标
        icon: hasChildren ? <FolderOutlined /> : <FileTextOutlined />,
      };
    });

  const menuDataRef = useRef([]);

  useEffect(() => {
    if (dispatch) {
      dispatch({
        type: 'user/fetchCurrent',
      });
      dispatch({
        type: 'user/fetchSite',
      });
    }

    // 首次访问，清除docUrl，防止历史数据干扰查询
    localStorage.removeItem('docUrl');
  }, []);

  useEffect(() => {
    const { match: {path, params, url} } = props;
    if (url !== localStorage?.getItem('docUrl') || !currentDoc.id) {
      if (dispatch) {

        query(dispatch, params, path);
        localStorage.setItem('docUrl', url);
      }
    }
  }, [location.pathname]);

  // 移除菜单数据监听，使用非受控方式

  useEffect(async () => {

    const {siteName, siteTitle, siteKeyword, siteDescription, siteLogo, favicon} = site;
    const pageDesc = siteTitle ? ` - ${siteName}` : '';

    // 定义seo标签，首页取默认配置，内容页需要传递
    const homeFlag = location.pathname === getHome();
    setSeo({
      // eslint-disable-next-line no-nested-ternary
      title: homeFlag ? (siteTitle?? '') : (tdk.title ? `${tdk.title}${pageDesc}` : ''),
      keywords: homeFlag ? siteKeyword : tdk.keywords || siteKeyword,
      description: homeFlag ? siteDescription : tdk.description || siteDescription,
      logo: siteLogo,
      favicon,
      crumbs: tdk.crumbs || [],
    });
    // 自动回到顶部
    document?.getElementById('root').scrollIntoView(true);
  }, [tdk]);

  // 监听窗口大小变化，判断移动端
  useEffect(() => {
    const handleResize = () => {
      setIsMobile(window.innerWidth < 768);
    };

    // 初始化
    handleResize();

    // 监听窗口大小变化
    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, []);

  const handleMenuCollapse = (payload) => {
    setCollapsed(payload);
  };

  const {formatMessage} = useIntl();
  return (
    <>
      {wldosHeader(seo)}
      <ProLayout
        className={`${styles.slideNavWldos} ${styles.docLayout}`}
        logo={seo.logo}
        title={seo.title}
        // formatMessage={formatMessage}
        pageTitleRender = {(titleProps) => {
          return titleProps.title;
        }}
        openKeys={false}
        menuDataRender={(md) => {
          // 避免重复渲染，只在数据变化时重新渲染
          const currentMenuData = md?.length > 0 ? menuData.pushAll(md) : menuData;
          return menuHandle(currentMenuData, 0, false, {dispatch, curId: currentDoc.id, curLen: currentDoc?.chapter?.length ?? 0});
        }}
        {...settings}
        contentStyle={{
          background: '#fff',
          minHeight: '100vh',
        }}
        menu={{
          loading,
          // 支持层级结构
          ignoreFlatMenu: true,
        }}
        collapsed={collapsed}
        onCollapse={handleMenuCollapse}
        breakpoint="md"
        collapsedButtonRender={isMobile ? undefined : false}
        onMenuHeaderClick={() => history.push('/')}
        menuItemRender={(menuItemProps, defaultDom) => {
          if (
            menuItemProps.isUrl ||
            !menuItemProps.path ||
            (location && location.pathname === menuItemProps.path)
          ) {
            return defaultDom;
          }
          return <Link to={menuItemProps.path}>{defaultDom}</Link>;
        }}
        breadcrumbRender={(routers = []) => [
          { // 自定义面包屑的数据
            path: '/',
            breadcrumbName: formatMessage({
              id: 'menu.home',
            }),
          },
          ...routers,
        ]}
        /* eslint-disable-next-line no-unused-vars */
        itemRender={(route, params, routes, paths) => {
          const last = routes.indexOf(route) === routes.length - 1;
          return last ? (
            <span>{route.breadcrumbName}</span>
          ) : (
            <Link to={route.path}>{route.breadcrumbName}</Link>
          );
        }}
        menuHeaderRender={(lgo/* , title */) => {
          if (!seo.logo) { return ''; }
          return lgo;
        }}
        rightContentRender={() => <RightContent />}
        postMenuData={(menu) => {
          menuDataRef.current = menu || [];
          return menu || [];
        }}
        siderWidth={300}
      >
        <Document key={currentChapter.id} {...{currentChapter, dispatch}}/>
      </ProLayout>
      <BackTop/>
    </>
  );
};

export default connect(({settings, doc, user, loading}) => ({
  tdk: user.tdk,
  site: user.site,
  menuData: doc.doc,
  currentDoc: doc.currentDoc,
  currentChapter: doc.currentChapter,
  loading: loading.models.user,
  settings: {
    ...settings,
    layout: "side",
    contentWidth: "Fluid",
    headerHeight: 48,
  }
}))(Doc);
