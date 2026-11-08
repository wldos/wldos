/**
 * 列表分页 URL：第 1 页为 *.html，第 n 页（n&gt;1）为 {paginationRoot}/page/n。
 * 与 cms-portal 及 archives-author、info-author 等 Controller 一致。
 */

export function htmlFirstListPageHref(pageOneUrl, paginationRoot, pageNum) {
  const n = Number(pageNum);
  if (!n || n <= 1) {
    return pageOneUrl;
  }
  return `${paginationRoot}/page/${n}`;
}

export function resolveHtmlFirstListFromUrl(pageOneUrl) {
  const paginationRoot = pageOneUrl.replace(/\.html$/i, '');
  return { pageOneUrl, paginationRoot };
}

export function parseHtmlFirstPageFromMatchUrl(matchUrl) {
  if (!matchUrl || !matchUrl.includes('/page/')) {
    return 1;
  }
  const m = matchUrl.match(/\/page\/(\d+)/);
  return m ? parseInt(m[1], 10) : 1;
}
