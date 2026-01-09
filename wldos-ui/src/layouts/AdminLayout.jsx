/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import ProLayout from '@ant-design/pro-layout';
import React, {useCallback, useEffect, useRef, useState} from 'react';
import {connect, history, Link, useIntl} from 'umi';
import {BackTop, Tabs, Dropdown} from 'antd';
import RightContent from '@/components/GlobalHeader/RightContentAdmin';
import wldosFooterDom from '@/layouts/footAdmin';
import styles from '@/wldos.less';
import { renderIcon } from '@/utils/iconLibrary';
import {querySiteSeo} from "@/services/user";
import {wldosHeader} from "@/utils/utils";

const AdminLayout = (props) => {
  const {
    dispatch,
    children,
    location = {
      pathname: '/',
    },
    menuData,
    settings,
    loading,
  } = props;

  const [site, setSite] = useState({title: '', keywords: '', description: '', logo: '', favicon: '', version: ''});
  const [collapsed, setCollapsed] = useState(false);
  const adminHomePath = '/admin';
  const [tabs, setTabs] = useState([
    { key: adminHomePath, path: adminHomePath, title: '首页', closable: false }
  ]); // 存储标签信息，首页不可关闭
  const [activeKey, setActiveKey] = useState(location?.pathname || adminHomePath);
  // 记录折叠状态以便全屏退出后还原
  const prevCollapsedRef = useRef(false);

  /**
   * 因为权限已经在后端过滤，返回的都是有权限菜单，不必检查权限。
   */

  // 为侧边菜单渲染图标，同时保留原始图标值到 tabIcon，供标签栏使用
  const menuHandle = (menus) => {
    // 空值检查：如果 menus 为空或不是数组，返回空数组
    if (!menus || !Array.isArray(menus)) {
      return [];
    }
    return menus.map((item) => ({
        ...item,
        tabIcon: item.icon, // 保留原始图标值
        icon: renderIcon(item.icon),
        children: item.children && menuHandle(item.children),
    }));
  };


  const menuDataRef = useRef([]);
  // 用户刚打开网站，应该是游客，触发请求，后台判断header中的token
  useEffect(() => {
    if (dispatch) {
      dispatch({
        type: 'user/fetchCurrent',
      });
      dispatch({
        type: 'user/fetchAdminMenu',
      });
    }
  }, []);

  useEffect(async () => { // 获取域信息
    const res =  await querySiteSeo();

    if (res && res.data) {
      const {siteTitle, siteDescription, siteLogo, favicon, version} = res.data;

      setSite({title: `${siteTitle}_${siteDescription}`, logo: siteLogo, favicon, version});
    }
  }, []);

  const handleMenuCollapse = (payload) => {
    setCollapsed(payload);
  };

  const {formatMessage} = useIntl();

  // 根据菜单数据构建 path->menuItem 的索引，便于查找标题与图标
  const pathToMenu = useRef({});
  const [menuReady, setMenuReady] = useState(false);
  // 从 props.menuData 递归查找标题，优先使用 name 作为标签名
  const findTitleFromMenu = useCallback((list, targetPath) => {
    const stack = Array.isArray(list) ? [...list] : [];
    while (stack.length) {
      const item = stack.shift();
      if (!item) continue;
      if (item.path === targetPath) {
        return item.name || item.title || item.menuName || item.label || item.text || item.zhName || item.cnName || targetPath;
      }
      if (item.children && item.children.length) {
        stack.unshift(...item.children);
      }
    }
    return '';
  }, [menuData]);
  // 标题提取工具：兼容多种字段
  const extractTitle = (menuItem, fallbackPath) => {
    if (!menuItem) return fallbackPath;
    const {
      name,
      title,
      menuName,
      label,
      text,
      zhName,
      cnName,
    } = menuItem;
    return name || title || menuName || label || text || zhName || cnName || fallbackPath;
  };
  // 初始化一次菜单映射
  useEffect(() => {
    const flat = {};
    const walk = (list) => {
      // 空值检查：确保 list 是数组
      if (!list || !Array.isArray(list)) {
        return;
      }
      list.forEach((it) => {
        if (it && it.path) flat[it.path] = it;
        if (it && it.children) walk(it.children);
      });
    };
    walk(menuDataRef.current);
    pathToMenu.current = flat;
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // 监听路由变化：新增或激活标签
  useEffect(() => {
    const path = location.pathname || adminHomePath;
    setActiveKey(path);
    setTabs((prev) => {
      // 如果标签已存在，更新其标题（防止刷新后标题丢失）
      if (prev.some((t) => t.key === path)) {
        return prev.map(t => {
          if (t.key === path) {
            // 直接从最新 menuData 里获取；若未就绪则保持为空，避免显示路径
            const title = findTitleFromMenu(menuData, t.path || path) || t.title || '';
            return { ...t, title };
          }
          return t;
        });
      }
      // 新增标签
      const title = findTitleFromMenu(menuData, path) || '';
      const newTab = {
        key: path,
        path,
        title,
        closable: path !== adminHomePath
      };
      return [...prev, newTab];
    });
  }, [location.pathname, menuData, findTitleFromMenu]);

  // 进入/退出全屏时自动折叠或还原侧边菜单
  useEffect(() => {
    const handleFsChange = () => {
      const isFs = !!document.fullscreenElement;
      if (isFs) {
        prevCollapsedRef.current = collapsed;
        setCollapsed(true);
      } else {
        setCollapsed(prevCollapsedRef.current);
      }
    };
    document.addEventListener('fullscreenchange', handleFsChange);
    document.addEventListener('webkitfullscreenchange', handleFsChange);
    document.addEventListener('mozfullscreenchange', handleFsChange);
    document.addEventListener('MSFullscreenChange', handleFsChange);
    return () => {
      document.removeEventListener('fullscreenchange', handleFsChange);
      document.removeEventListener('webkitfullscreenchange', handleFsChange);
      document.removeEventListener('mozfullscreenchange', handleFsChange);
      document.removeEventListener('MSFullscreenChange', handleFsChange);
    };
  }, [collapsed]);

  // 菜单数据就绪后，强制更新所有标签标题
  useEffect(() => {
    if (!menuReady) return;

    setTabs((prev) => prev.map((t) => {
      const fixedTitle = findTitleFromMenu(menuData, t.path || t.key) || t.title || '';

      return { ...t, title: fixedTitle };
    }));
  }, [menuReady, menuData, findTitleFromMenu]);

  // 当路由变化且菜单已就绪时，二次校正当前和已打开标签标题，避免刷新后显示为路径
  useEffect(() => {
    if (!menuReady) return;
    setTabs((prev) => prev.map((t) => {
      const fixedTitle = findTitleFromMenu(menuData, t.path || t.key) || t.title || '';
      return { ...t, title: fixedTitle };
    }));
  }, [location.pathname, menuReady, menuData, findTitleFromMenu]);

  // 监听后端菜单数据变化，构建索引并标记就绪
  useEffect(() => {
    const list = (menuDataRef.current && menuDataRef.current.length > 0) ? menuDataRef.current : (menuData || []);
    const flat = {};
    const walk = (arr) => {
      // 空值检查：确保 arr 是数组
      if (!arr || !Array.isArray(arr)) {
        return;
      }
      arr.forEach((it) => {
        if (it && it.path) flat[it.path] = it;
        if (it && it.children) walk(it.children);
      });
    };
    walk(list);
    pathToMenu.current = flat;
    setMenuReady(true);
  }, [menuData]);

  // 关闭标签
  const onEditTab = (targetKey, action) => {
    if (action !== 'remove') return;
    if (targetKey === adminHomePath) return; // 首页不可关闭
    setTabs((prev) => {
      const idx = prev.findIndex((t) => t.key === targetKey);
      if (idx === -1) return prev;
      const next = prev.filter((t) => t.key !== targetKey);
      // 如果关闭的是当前激活标签，切换到上一个或首页
      if (activeKey === targetKey) {
        const fallback = next[idx - 1]?.key || next[0]?.key || adminHomePath;
        setActiveKey(fallback);
        if (fallback !== location.pathname) history.push(fallback);
      }
      return next;
    });
  };

  // 切换标签
  const onChangeTab = (key) => {
    setActiveKey(key);
    if (key !== location.pathname) history.push(key);
  };

  // 处理首页标签点击
  const handleHomeClick = () => {
    setActiveKey(adminHomePath);
    if (adminHomePath !== location.pathname) {
      history.push(adminHomePath);
    }
    // 强制触发其他标签的重新渲染，确保焦点状态正确更新
    setTabs(prev => [...prev]);
  };

  // Tab 右键菜单动作
  const refreshTab = (key) => {
    const url = new URL(window.location.href);
    if (key !== location.pathname) {
      history.push(key);
      return;
    }
    const ts = Date.now().toString();
    const search = url.search ? `${url.search}&_t=${ts}` : `?_t=${ts}`;
    history.replace(`${url.pathname}${search}${url.hash || ''}`);
  };

  const closeCurrentTab = (key) => {
    if (key === adminHomePath) return;
    onEditTab(key, 'remove');
  };

  const closeOtherTabs = (key) => {
    setTabs((prev) => {
      const keep = prev.filter(t => t.key === adminHomePath || t.key === key);
      if (!keep.find(t => t.key === key)) return prev;
      setActiveKey(key);
      if (location.pathname !== key) history.push(key);
      return keep;
    });
  };

  const closeRightTabs = (key) => {
    setTabs((prev) => {
      const idx = prev.findIndex(t => t.key === key);
      if (idx === -1) return prev;
      const next = prev.filter((t, i) => i <= idx || t.key === adminHomePath);
      // 若当前激活被关闭，切到目标key
      if (!next.find(t => t.key === activeKey)) {
        setActiveKey(key);
        if (location.pathname !== key) history.push(key);
      }
      return next;
    });
  };

  const buildTabMenu = (key) => ({
    items: [
      { key: 'refresh', label: '刷新' },
      { type: 'divider' },
      { key: 'close', label: '关闭当前', disabled: key === adminHomePath },
      { key: 'closeRight', label: '关闭右侧' },
      { key: 'closeOthers', label: '关闭其他' },
    ],
    onClick: ({ key: action }) => {
      if (action === 'refresh') return refreshTab(key);
      if (action === 'close') return closeCurrentTab(key);
      if (action === 'closeOthers') return closeOtherTabs(key);
      if (action === 'closeRight') return closeRightTabs(key);
    },
  });

  // 暂时取消 ErrorBoundary，避免潜在的重复挂载导致的请求循环

  // 激活标签滚动到可见区域
  useEffect(() => {
    // 容器：Tabs 外层的滚动区域，就是我们渲染 items 的父 div
    const container = document.querySelector('.ant-tabs-nav-list');
    if (!container) return;
    const el = container.querySelector(`[data-tab-key="${activeKey}"]`);
    if (!el) return;
    const parent = container.parentElement; // 可滚动容器
    if (!parent) return;
    const elRect = el.getBoundingClientRect();
    const pRect = parent.getBoundingClientRect();
    if (elRect.left < pRect.left || elRect.right > pRect.right) {
      // 平滑滚动到中间位置
      const offset = el.offsetLeft - (parent.clientWidth - el.clientWidth) / 2;
      parent.scrollTo({ left: offset, behavior: 'smooth' });
    }
  }, [activeKey, tabs.length]);


  return (
    <>
      {wldosHeader(site)}
      <ProLayout
        className={styles.slideNavWldos}
        logo={site.logo}
        title={site.title || ''}
        formatMessage={formatMessage}
        contentStyle={{ background: '#f5f7fa' }}
        menuDataRender={(md) => {
          // 确保 menuData 是数组，如果为空则使用空数组
          const baseMenu = Array.isArray(menuData) ? menuData : [];
          // 如果 md 存在且是数组且有数据，则合并；否则使用 baseMenu
          const finalMenu = (md && Array.isArray(md) && md.length > 0)
            ? [...baseMenu, ...md]
            : baseMenu;
          return menuHandle(finalMenu);
        }}
        {...settings}
        openKeys={false}
        menu={{loading, locale: false, ignoreFlatMenu: true}}
        collapsed={collapsed}
        onCollapse={handleMenuCollapse}
        onMenuHeaderClick={() => history.push(adminHomePath)}
        menuItemRender={(menuItemProps, defaultDom) => {
          if ( // 自定义菜单项的 渲染 方法
            menuItemProps.isUrl ||
            !menuItemProps.path
          ) {
            return defaultDom;
          }

          // 判断是否是父路径（当前路径是菜单路径的子路径）
          const isParentPath = location.pathname.startsWith(menuItemProps.path + '/');
          // 只有完全匹配的路径才应该显示选中状态
          const isExactMatch = location.pathname === menuItemProps.path;

          return (
            <Link
              to={menuItemProps.path}
              data-is-parent-path={isParentPath ? 'true' : 'false'}
              data-is-exact-match={isExactMatch ? 'true' : 'false'}
            >
              {defaultDom}
            </Link>
          );
        }}
        // 多标签工作台中，行业通行做法：面包屑可省略，避免与标签重复
        breadcrumbRender={() => []}
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
          if (!site.logo) { return ''; }
          // 根据菜单栏状态显示不同图标
          return (
            <div style={{ display: 'flex', alignItems: 'center', height: '100%' }}>
              {collapsed ? (
                // 菜单栏回收时显示favicon小图标
                <img
                  src={site.favicon || site.logo}
                  alt="favicon"
                  style={{
                    height: '24px',
                    width: '24px',
                    objectFit: 'contain',
                    display: 'block'
                  }}
                  onError={(e) => {
                    e.target.style.display = 'none';
                  }}
                />
              ) : (
                // 菜单栏展开时显示完整logo
                <img
                  src={site.logo}
                  alt="logo"
                  style={{
                    height: '32px',
                    width: 'auto',
                    objectFit: 'contain',
                    display: 'block'
                  }}
                  onError={(e) => {
                    e.target.style.display = 'none';
                  }}
                />
              )}
            </div>
          );
        }}
        // pageTitleRender={(props) => { return 'wldos'; }}
        footerRender={() => wldosFooterDom(site.version)}
        rightContentRender={() => <RightContent/>}
        postMenuData={(menu) => { // 在显示前对菜单数据进行查看
          // 确保 menu 是数组
          const list = Array.isArray(menu) ? menu : [];
          menuDataRef.current = list;
          // 同步构建 path->menu 索引，供标签标题/图标渲染
          const flat = {};
          const walk = (arr) => {
            // 空值检查：确保 arr 是数组
            if (!arr || !Array.isArray(arr)) {
              return;
            }
            arr.forEach((it) => {
              if (it && it.path) flat[it.path] = it;
              if (it && it.children) walk(it.children);
            });
          };
          walk(list);
          pathToMenu.current = flat;
          return list;
        }}
      >
        {/* 多标签页区域 - 首页固定 + 其他可滚动 */}
        <div style={{
          position: 'sticky',
          top: 48, // 固定在顶栏下方（与 headerHeight 保持一致）
          zIndex: 100,
          background: '#fff',
          borderBottom: '1px solid #e8e8e8',
          boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
          // 通栏：完全铺满，抵消所有内边距
          margin: '0 -24px',
          padding: '0 24px',
          // 强制消除顶部间隙
          marginTop: '-24px',
          // 覆盖可能的antd全局样式
          borderTop: 'none',
          borderLeft: 'none',
          borderRight: 'none',
          // 强制重置所有可能的间距
          paddingTop: '0 !important',
          marginBottom: '0 !important',
          // 覆盖全局样式
          lineHeight: '1',
          fontSize: '14px'
        }}>
          <div style={{ display: 'flex', alignItems: 'center' }}>
            {/* 固定首页标签 - 使用与其他标签一致的样式 */}
            {(() => {
              const mHome = pathToMenu.current[adminHomePath] || {};
              // 强制设置首页标签名称为"首页"
              const homeTitle = '首页';
              const isActive = activeKey === adminHomePath;
              return (
                <div
                  onClick={handleHomeClick}
                  style={{
                    display: 'inline-flex',
                    alignItems: 'center',
                    padding: '6px 12px',
                    marginRight: 8,
                    cursor: 'pointer',
                    userSelect: 'none',
                    flexShrink: 0, // 防止收缩
                    borderBottom: isActive ? '2px solid #1890ff' : '2px solid transparent',
                    color: isActive ? '#1890ff' : 'rgba(0,0,0,0.85)',
                    fontSize: '13px',
                    fontWeight: isActive ? 500 : 400,
                    transition: 'all 0.3s ease'
                  }}
                >
                  <span title={homeTitle}>{homeTitle}</span>
                </div>
              );
            })()}

            {/* 可滚动的其他标签 */}
            <div style={{ flex: 1, minWidth: 0, overflow: 'hidden' }}>
              <Tabs
                type="editable-card"
                hideAdd
                size="small"
                activeKey={activeKey}
                onChange={onChangeTab}
                onEdit={onEditTab}
                onMouseDown={(e) => {
                  if (e.button !== 1) return; // 只处理中键
                  // 找到最近的可点击标签元素，读取其 data-key
                  const target = e.target.closest('[data-tab-key]');
                  const key = target && target.getAttribute('data-tab-key');
                  if (key) {
                    e.preventDefault();
                    closeCurrentTab(key);
                  }
                }}
                // 强制重新渲染，确保焦点状态正确
                key={`tabs-${activeKey}`}
                items={tabs.filter(t => t.key !== adminHomePath).map(t => {
                  const m = pathToMenu.current[t.path] || pathToMenu.current[t.key] || {};
                  const title = t.title || m.name || m.title || '';
                  // 调试：渲染标签日志（开发时可开启）
                  return ({
                    key: t.key,
                    label: (
                    <Dropdown trigger={['contextMenu']} menu={buildTabMenu(t.key)}>
                      <span data-tab-key={t.key} style={{
                        display: 'inline-flex',
                        alignItems: 'center',
                        gap: 6,
                        fontSize: '13px',
                        maxWidth: 180,
                        whiteSpace: 'nowrap',
                        overflow: 'hidden',
                        textOverflow: 'ellipsis'
                      }}>
                        <span title={typeof title === 'string' ? title : ''}>{title || '\u00A0'}</span>
                      </span>
                    </Dropdown>
                    ),
                    closable: true,
                  });
                })}
                tabBarStyle={{
                  background: '#fff',
                  padding: 0,
                  margin: 0,
                  borderBottom: 'none'
                }}
                style={{
                  background: '#fff',
                  borderRadius: '0',
                  boxShadow: 'none'
                }}
              />
            </div>
          </div>
        </div>
        {/* 内容容器：全宽布局，消除所有间隙 */}
        <div style={{
          padding: '0',
          background: '#f5f7fa',
          minHeight: 'calc(100vh - 200px)'
        }}>
          {/* 去除错误边界，保留骨架屏 */}
          <React.Suspense fallback={
              <div style={{ padding: 24 }}>
                <div style={{ background: '#fff', borderRadius: 0, padding: 24 }}>
                  <div style={{ height: 16, width: 200, background: '#f2f3f5', marginBottom: 16 }} />
                  <div style={{ height: 12, width: '60%', background: '#f2f3f5', marginBottom: 12 }} />
                  <div style={{ height: 12, width: '70%', background: '#f2f3f5', marginBottom: 12 }} />
                  <div style={{ height: 12, width: '40%', background: '#f2f3f5', marginBottom: 12 }} />
                  <div style={{ height: 200, width: '100%', background: '#f2f3f5' }} />
                </div>
              </div>
            }>
              <div style={{
                background: '#fff',
                borderRadius: 0,
                minHeight: 'calc(100vh - 200px)',
                width: '100%'
              }}>
        {children}
              </div>
          </React.Suspense>
        </div>
      </ProLayout>
      <BackTop/>
    </>
  );
};

export default connect(({settings, user, loading}) => ({
  menuData: user.adminMenu,
  loading: loading.models.user,
  settings: {
    ...settings,
    layout: "side",
    contentWidth: "Fluid",
    headerHeight: 48,
  }
}))(AdminLayout);
