import {parse, stringify} from 'querystring';
import {matchRoutes} from "react-router-config";
import {history} from "umi";
import AvatarList from "@/components/AvatarList";
import React from "react";
import {Typography} from "antd";
import styles from "@/pages/book/detail/style.less";
import {Helmet} from "react-helmet";
import {JSEncrypt} from "jsencrypt";
import wldosStorage from "@/utils/wldostorage";
import BookClass from "@/pages/book/detail/components/BookClass";
export const guest = "guest";

const reg = /(((^https?:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+(?::\d+)?|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)$/;
export const isUrl = (path) => reg.test(path);

/* 判断请求者是否网络爬虫 */
export const isBot = () =>
  (window.navigator.userAgent.match(/(googlebot|bingbot|yandex|baiduspider|sogou.*|soso.*|360.*|youdaobot|msnbot|yahoo.*|twitterbot|facebookexternalhit|rogerbot|linkedinbot|embedly|quora|showyoubot|outbrain|pinterest|slackbot|vkShare|W3C_Validator)/i));


export const getPageQuery = () => parse(window.location.href.split('?')[1]);
export const getHome = () => { // 退出返回首页
    return '/';
};

/**
 * 判断是否首页
 *
 * @param pathname 当前url路径
 * @returns {boolean} 是否首页
 */
export const isHome = (pathname) => {
  return pathname === getHome();
};

/**
 * 判断是否管理端
 *
 * @param pathname 当前url路径
 * @returns {boolean} 是否首页
 */
export const isBackGround = (pathname) => {
  // 约定只有管理端路由包含admin
  return pathname && pathname.indexOf('/admin/') > -1;
};

export function getRouteAuthority(path, routerData) {
    const routes = matchRoutes(routerData, path);
    let authorities = [];
    routes.forEach(item => {
        if (Array.isArray(item.route.authority)) {
            authorities = authorities.concat(item.route.authority);
        } else if (typeof item.route.authority === 'string') {
            authorities.push(item.route.authority);
        }
    });

    return authorities;
}

export function isServer() {
  return typeof window === 'undefined';
}

export const redirectReq = (path) => { // 请求后重定向回来
    const {redirect} = getPageQuery();

    if (!isServer() && window.location.pathname !== path && !redirect) {
        history.replace({
            pathname: path,
            search: stringify({
                redirect: window.location.href,
            }),
        });
    }
};

export function getDomain() {
  if (!isServer()) {
    return window.location.hostname;
  }
  return ''; // 仅对于web端，对于app请用app_key实现
}

/**
 * 字符串截取 包含对中文处理,str需截取字符串,start开始截取位置,n截取长度
 *
 * @param str
 * @param start
 * @param n
 * @returns {string|*}
 * @constructor
 */
export function subStr(str, start, n) {
  if (!str) {
    return '';
  }
  if (str.replace(/[\u4e00-\u9fa5]/g, '**').length <= n) {
    return str;
  }
  let len = 0;
  let tmpStr = '';
  for (let i = start; i < str.length; i += 1) { // 遍历字符串
    if(/[\u4e00-\u9fa5]/.test(str[i])) { // 中文 长度为两字节
      len += 2;
    } else {
      len += 1;
    }
    if (len > n) {
      break;
    } else {
      tmpStr += str[i];
    }
  }
  return tmpStr;
}

/**
 * 生成自定义面包屑
 *
 * @param crumbs 面包屑数据，数据格式必须为: [{path: '', breadcrumbName: ''},]
 * @param tailName 正文面包屑名称（面包屑结尾pattern）
 * @returns {[]} 面包屑
 */
export function genCrumbs(crumbs = [], tailName='') {
  const bcs = [];
  const page = tailName && {path: '#', breadcrumbName: tailName,};
  bcs.push(...crumbs);
  if (page)
    bcs.push(page);

  return bcs;
}

/**
 * 创建tdk和面包屑数据
 *
 * @param data 数据
 * @param tailName 末尾面包屑名称
 * @param titlePrefix 标题前缀
 * @param dispatch 转发
 */
export function genTdkCrumbs(data = {seoCrumbs: {title: '', keywords: '', description: '', pubType: '', crumbs: []}}, dispatch, tailName = '', titlePrefix = '') {
  const { seoCrumbs: {title = '没有内容', keywords = '', description = '', pubType = '', crumbs = []} } = data;
  const bcs = genCrumbs(crumbs, tailName);
  if (dispatch) {
    dispatch({
      type: 'user/saveTdk',
      payload: {title: `${titlePrefix}${title}`, keywords, description, pubType, crumbs: bcs}
    });
  }
}
export function genTdkCrumbsFor404(dispatch) {
  if (dispatch) {
    dispatch({
      type: 'user/saveTdk',
      payload: {title: '404 not found', keywords: '没有内容', description: '找不到内容', crumbs: [{path: '#', breadcrumbName: '404 not found',}]}
    });
  }
}

/**
 * 生成头像列表
 *
 * @param id 记录id
 * @param members 成员数组
 * @returns {[]} 头像列表组件
 */
export const genAvatars = (id = '', members = []) => {
  const temp = [];
  for (let i = 0, len = members.length; i < len; i += 1) {
    const member = members[i];
    const authorUrl = member?.id ? `/archives-author/${member.id}.html` : null;
    temp.push((<AvatarList.Item
      key={`${id}-${i}`}
      src={member.avatar}
      tips={member.name}
      onClick={() => { if (authorUrl) window.open(authorUrl, '_blank'); }}
    />));
  }
  return temp;
};
// AES加密用户密码(其实是RSA)
export const encryptByAES = (ps, encryptKey) => {
  const enc = new JSEncrypt();
  enc.setPublicKey(encryptKey);
  const encrypted = enc.encrypt(ps);
  return encrypted.toString();
};
// 下拉列表转枚举对象
export const selectToEnum = (selList = [{label:"", value:""},]) => {
  const data = {};
  for (let i = 0, len = selList.length; i < len; i += 1) { // 经测试优化for比forEach快10倍左右
    const item = selList[i];
    data[item.value] = {
      text: item.label,
    }
  }
  return data;
}
// 详情页内容区
export const bodyContent = (content) => (
  <Typography>
    <div className={styles.entry} dangerouslySetInnerHTML={{ __html: content}} />
  </Typography>);

// 文集详情页内容区
export const bodyContentBook = (content, pId) => (<>
  {bodyContent(content)}
  <BookClass bookId={pId} />
</>);

export const wldosHeader = (seo) => {
  document?.documentElement.style.setProperty('--mobile-logo', `url(${seo?.favicon ? seo.favicon : './assets/logo-m.png'})`);
  return (
  /* 可自定义需不需要编码 */
  <Helmet encodeSpecialCharacters={false}>
    <html lang="zh_CN"/>
    <meta name="keywords" content={seo.keywords}/>
    <meta name="description" content={seo.description}/>
    <link rel="icon" href={seo.favicon} type="image/x-icon"/>
  </Helmet>
)};

export const redirect = () => {
  const urlParams = new URL(window.location.href);
  const query = getPageQuery();

  let {redirect: _redirect} = query;

  if (_redirect) {
    const redirectUrlParams = new URL(_redirect);

    if (redirectUrlParams.origin === urlParams.origin) {
      _redirect = _redirect.substring(urlParams.origin.length);

      if (_redirect.match(/^\/.*#/)) {
        _redirect = _redirect.substring(_redirect.indexOf('#') + 1);
      }
    }
  }
  window.location.href = _redirect || '/';
};
export const headerFix = {
  'X-CU-AccessToken-WLDOS': wldosStorage.get('accessToken') || guest,
  'domain-wldos': getDomain(),
};

export const typeEnum = {'doc': '文档', 'info': '信息', 'book': '合集', 'post': '文章'};
export const typeUrl = (item) => item.pubType === 'info' ? `/info-${item.id}.html` :
    (item.pubType === 'book' ? `/product-${item.id}.html`: (
      item.pubType === 'doc' ? `/doc/book/${item.id}.html` :
      (item.pubType === 'page' ? `/${item.pubName}` : `/archives-${item.id}.html`)));
export function isPc () {
  const _$=["Win", "Mac", "X11"];
  const is_win=navigator["platform"]["indexOf"](_$[0])===0;
  const is_mac=navigator["platform"]["indexOf"](_$[1])===0;
  const is_x11=(navigator["platform"]===_$[2]);
  return is_win || is_mac || is_x11;
}
