import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { Pagination } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { connect, history, Link } from 'umi';
import { queryBookList } from '@/pages/home/service';
import { genTdkCrumbs } from '@/utils/utils';
import Chapters from '@/components/ArchiveList/Chapters';
import { fetchSeoCrumbs } from '@/pages/home/service';
import { starObject } from '@/components/ArchiveList/Chapters/service';
import {
  htmlFirstListPageHref,
  parseHtmlFirstPageFromMatchUrl,
  resolveHtmlFirstListFromUrl,
} from '@/utils/authorListPagination';

/**
 * 存档作品列表（作者公开页、个人中心收藏/点赞等）
 */
const Articles = (props) => {
  const {
    dispatch,
    count = 16,
    url,
    slugTerm,
    id,
    authorUserId,
    page,
    pageUrl,
    isBook,
    match,
  } = props;

  const htmlFirst = useMemo(() => {
    if (authorUserId) {
      return resolveHtmlFirstListFromUrl(`/archives-author/${authorUserId}.html`);
    }
    const routeUrl = pageUrl || match?.url || '';
    const likeStar = routeUrl.match(/^\/archives-(like|star)\//);
    if (likeStar && match?.params?.userId) {
      return resolveHtmlFirstListFromUrl(`/archives-${likeStar[1]}/${match.params.userId}.html`);
    }
    if (url && /\.html(\?|$)/i.test(url.split('?')[0])) {
      const one = url.indexOf('/page/') > -1
        ? `${url.substring(0, url.indexOf('/page/'))}.html`
        : url.split('?')[0];
      return resolveHtmlFirstListFromUrl(one);
    }
    return null;
  }, [authorUserId, url, pageUrl, match]);

  const pageSize = htmlFirst ? 8 : count;
  const path = useMemo(() => {
    if (authorUserId) {
      return `/archives-author/${authorUserId}.html`;
    }
    if (id) {
      return `/archives-author/${id}.html`;
    }
    if (htmlFirst) {
      return htmlFirst.pageOneUrl;
    }
    return url ?? match?.url ?? '';
  }, [authorUserId, id, htmlFirst, url, match]);

  const routePage = page ? parseInt(page, 10) : parseHtmlFirstPageFromMatchUrl(pageUrl || match?.url);
  const [cur, setCur] = useState(routePage || 1);
  const [data, setData] = useState({ list: [], total: 0, pageSize, current: 1 });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setCur(routePage || 1);
  }, [routePage]);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      setLoading(true);
      const res = await queryBookList({
        pageSize,
        current: cur,
        sorter: { createTime: 'descend' },
        path,
      });
      if (cancelled) return;
      if (res && res.data) {
        const { rows = [], total = 0, current = 1, pageSize: ps = pageSize } = res.data;
        setData({ list: rows, total, current, pageSize: ps });
      }
      setLoading(false);
    })();
    return () => {
      cancelled = true;
    };
  }, [path, cur, pageSize]);

  useEffect(async () => {
    const res = await fetchSeoCrumbs({ slugTerm, tempType: 'archives' });
    if (res && res.data) {
      await genTdkCrumbs(
        { seoCrumbs: res.data },
        dispatch,
        authorUserId || id ? undefined : '存档列表',
        '作者存档 - ',
      );
    }
  }, [url, isBook, authorUserId, id, slugTerm, dispatch]);

  const onPageChange = useCallback(
    (pNum) => {
      setCur(pNum);
      if (htmlFirst) {
        history.replace(htmlFirstListPageHref(htmlFirst.pageOneUrl, htmlFirst.paginationRoot, pNum));
        return;
      }
      const base = url || match?.url || '/';
      if (pNum === 1) {
        history.replace(base);
      } else {
        history.replace(
          base === '/'
            ? `/page/${pNum}`
            : `${base.endsWith('/') ? base : `${base}/`}page/${pNum}`,
        );
      }
    },
    [htmlFirst, url, match],
  );

  const itemRender = useCallback(
    (current, type, originalElement) => {
      if (htmlFirst) {
        const to = htmlFirstListPageHref(htmlFirst.pageOneUrl, htmlFirst.paginationRoot, current);
        if (type === 'page') return <Link to={to}>{current}</Link>;
        if (type === 'prev' || type === 'next') return <Link to={to}>{type === 'prev' ? '<' : '>'}</Link>;
        return originalElement;
      }
      const target = url === '/' || (url && url.endsWith('/')) ? url : `${url || ''}/`;
      if (type === 'page') return <Link to={`${target}page/${current}`}>{current}</Link>;
      if (type === 'prev') return <Link to={`${target}page/${current}`}>&lt;</Link>;
      if (type === 'next') return <Link to={`${target}page/${current}`}>&gt;</Link>;
      return originalElement;
    },
    [htmlFirst, url],
  );

  const star = async (p) => {
    if (!p) return;
    const res = await starObject(p);
    if (res && res.data) {
      const listData = [];
      const { list } = data;
      for (let i = 0, len = list.length; i < len; i += 1) {
        let pub = list[i];
        if (pub.id === p.id) {
          if (p.path === '/cms/star') {
            const c = pub.starCount + res.data;
            pub = { ...pub, starCount: c + 1 === 0 ? 0 : c };
          } else {
            const c = pub.likeCount + res.data;
            pub = { ...pub, likeCount: c + 1 === 0 ? 0 : c };
          }
        }
        listData.push(pub);
      }
      setData({ ...data, list: listData });
    }
  };

  const body = (
    <>
      <Chapters
        list={data.list}
        pageSize={data.pageSize}
        loading={loading}
        total={data.total}
        cur={cur}
        star={star}
        isBook={isBook}
      />
      {data.total > data.pageSize ? (
        <Pagination
          current={cur}
          onChange={onPageChange}
          itemRender={itemRender}
          total={data.total}
          pageSize={data.pageSize}
          showQuickJumper
          showSizeChanger={false}
          style={{ marginTop: 16, textAlign: 'center' }}
        />
      ) : null}
    </>
  );

  return match?.url ? (
    <PageContainer title={false} breadcrumb={false}>
      {body}
    </PageContainer>
  ) : (
    body
  );
};

export default connect(({ user }) => ({
  tdk: user.tdk,
}))(Articles);
