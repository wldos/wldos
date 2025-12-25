import {reloadAuthorized} from './Authorized';
import wldosStorage from "@/utils/wldostorage";

export function getAuthority(str = 'guest') {
  const authorityString =
    typeof str === 'undefined' && wldosStorage ? wldosStorage.get('wldos-authority') : str;

  let authority;

  try {
    if (authorityString) {
      authority = JSON.parse(authorityString);
    } else {
      authority = ['guest'];
    }
  } catch (e) {
    authority = authorityString;
  }

  if (typeof authority === 'string') {
    return [authority];
  }

  return authority;
}

/**
 * @desc 通过response对象获取服务端返回的认证信息并缓存本地。
 * @param response 服务端响应
 */
export function setAuthority(response) {
  const token = response?.token?? {};
  const inHalfHours = token.refresh?? 30; // 过期时间，单位分钟

  if (response.currentAuthority) {
    const {currentAuthority: authority} = response;
    const proAuthority = typeof authority === 'string' ? [authority] : authority;
    wldosStorage.set('wldos-authority', JSON.stringify(proAuthority), inHalfHours);
  }
  // 新token存在，不管旧token是否存在，不同则更新(不需要刷新,后端不传递token)。实现token的自动刷新，服务器端控制刷新节奏。
  if (token.accessToken) {
    wldosStorage.set('accessToken', token.accessToken, inHalfHours);
  }

  reloadAuthorized();
}

export function clearAuthority() {
  // 先清空本地缓存，如果启用token，应该先清除token
  wldosStorage.remove('accessToken');
  wldosStorage.remove('wldos-authority');

  reloadAuthorized();
}
