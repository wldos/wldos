import ProLayout from '@ant-design/pro-layout';
import React, {useEffect, useRef, useState} from 'react';
import {connect, history, Link, useIntl} from 'umi';
import {BackTop, Spin} from 'antd';
import {FolderOutlined, FileTextOutlined} from '@ant-design/icons';
import RightContent from '@/components/GlobalHeader/RightContent';
import styles from '@/wldos.less';
import { getHome, wldosHeader} from "@/utils/utils";
import { setPortalMenuTree } from '@/utils/portalMenuVisit';
import Document from "@/pages/doc/components/Document";

const isDocEntryPath = (pathname) =>
  pathname === '/doc' || pathname === '/doc/' || pathname === '/doc/book';

/** 仅书本页、尚无 chapter 段（需跳到首章） */
const isBookOnlyPathname = (pathname) =>
  /\/doc\/book\/\d+\.html$/.test(pathname) && !/\/chapter\//.test(pathname);

const chapterPath = (bookId, chapterId) =>
  `/doc/book/${bookId}/chapter/${chapterId}.html`;

const bookPath = (bookId) => `/doc/book/${bookId}.html`;

const parseBookIdFromPathname = (pathname) => {
  const m = pathname.match(/\/doc\/book\/(\d+)(?:\.html|\/)/);
  return m ? m[1] : null;
};

const needsRouteResolve = (pathname) =>
  isDocEntryPath(pathname) || isBookOnlyPathname(pathname);

const dispatchAction = (dispatch, type, payload) =>
  new Promise((resolve) => {
    dispatch({ type, payload, callback: resolve });
  });

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
  const [redirecting, setRedirecting] = useState(() =>
    needsRouteResolve(typeof window !== 'undefined' ? window.location.pathname : '/doc'),
  );

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
        sorter: { displayOrder: 'ascend', id: 'ascend' },
      },
      callback: async (res) => {
        // 使用 success 判断 API 是否成功
        if (res?.success && res?.data?.rows?.length > 0) {
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

  /** 一次 replace 落到首章，避免 /doc → /doc/book → book.html → chapter 多次抖动 */
  const navigateToFirstChapter = async (targetBookId, { showLoading = true } = {}) => {
    if (!dispatch) {
      setRedirecting(false);
      return;
    }
    if (showLoading) {
      setRedirecting(true);
    }
    let bookId = targetBookId;
    if (!bookId) {
      const listRes = await dispatchAction(dispatch, 'doc/fetchDoc', {
        current: 1,
        pageSize: 50,
        sorter: { displayOrder: 'ascend', id: 'ascend' },
      });
      const rows = listRes?.data?.rows ?? [];
      if (!rows.length) {
        setRedirecting(false);
        return;
      }
      bookId = rows[0].id;
      setDataLoaded(true);
    } else if (menuData.length === 0) {
      await dispatchAction(dispatch, 'doc/fetchDoc', {
        current: 1,
        pageSize: 50,
        sorter: { displayOrder: 'ascend', id: 'ascend' },
      });
      setDataLoaded(true);
    }

    const bookRes = await dispatchAction(dispatch, 'doc/fetchCurrentDoc', { bookId });
    if (!bookRes?.success) {
      setRedirecting(false);
      return;
    }
    const chapters = bookRes?.data?.chapter ?? [];
    if (chapters.length > 0) {
      const chapterId = chapters[0].id;
      const target = chapterPath(bookId, chapterId);
      if (location.pathname !== target) {
        history.replace(target);
      }
      await dispatchAction(dispatch, 'doc/fetchCurrentChapter', { bookId, chapterId });
    } else {
      const target = bookPath(bookId);
      if (location.pathname !== target) {
        history.replace(target);
      }
      queryCurrentChapter(dispatch, { bookId, chapterId: '' });
    }
    setRedirecting(false);
  };

  const loadChapterRoute = (params) => {
    if (menuData.length === 0) {
      queryDoc(dispatch, null);
    }
    const bookId = params?.bookId;
    if (bookId && (!currentDoc.id || String(currentDoc.id) !== String(bookId))) {
      queryCurrentDoc(dispatch, params, null);
    }
    queryCurrentChapter(dispatch, params);
    setRedirecting(false);
  };

  const query = (dispatch, params, path) => {
    if (path === '/doc/book/:bookId/chapter/:chapterId.html') {
      loadChapterRoute(params);
    }
  };

  const docClick = (key, props) => {
    const { dispatch: d, curId, curLen } = props;
    if (curId !== key || curLen <= 1) {
      navigateToFirstChapter(String(key), { showLoading: false });
    }
  };

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
    const {
      match: { path, params, url },
    } = props;
    const pathname = location.pathname;

    if (!dispatch) {
      return;
    }

    if (needsRouteResolve(pathname)) {
      const bookId = parseBookIdFromPathname(pathname);
      navigateToFirstChapter(bookId || undefined);
      localStorage.setItem('docUrl', url);
      return;
    }

    if (url !== localStorage?.getItem('docUrl') || !currentDoc.id) {
      query(dispatch, params, path);
      localStorage.setItem('docUrl', url);
    } else {
      setRedirecting(false);
    }
  }, [location.pathname]);

  // 移除菜单数据监听，使用非受控方式

  useEffect(() => {
    if (!site) return;
    const {siteName, siteTitle, siteKeyword, siteDescription, siteLogo, favicon} = site;
    const pageDesc = siteTitle ? ` - ${siteName}` : '';
    const homeFlag = location.pathname === getHome();
    setSeo({
      // eslint-disable-next-line no-nested-ternary
      title: homeFlag ? (siteTitle?? '') : (tdk?.title ? `${tdk.title}${pageDesc}` : ''),
      keywords: homeFlag ? (siteKeyword ?? '') : (tdk?.keywords ?? siteKeyword ?? ''),
      description: homeFlag ? (siteDescription ?? '') : (tdk?.description ?? siteDescription ?? ''),
      logo: siteLogo,
      favicon,
      crumbs: tdk?.crumbs || [],
    });
    document?.getElementById('root').scrollIntoView(true);
  }, [tdk, site, location.pathname]);

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

  if (redirecting) {
    return (
      <>
        {wldosHeader(seo)}
        <div className={styles.docRouteLoading}>
          <Spin size="large" />
        </div>
      </>
    );
  }

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
        postMenuData={(menu) => {
          const m = menu || [];
          setPortalMenuTree(m);
          return m;
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
