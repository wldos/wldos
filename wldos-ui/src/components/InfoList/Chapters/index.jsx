/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, {useEffect, useRef, useState, useCallback} from 'react';
import {Tag, Radio, Select, Space, Divider} from 'antd';
import ProList from '@ant-design/pro-list';
import {history, Link, useIntl} from 'umi';
import styles from './index.less';
import {queryCategoryFromPlug} from "@/pages/sys/category/service";
import {queryBookList} from "@/pages/home/service";
import {queryProvince, queryCity} from "@/pages/account/settings/service";
import moment from "moment";
import {createImageErrorHandler} from "@/utils/imageUtils";

const {Group, Button} = Radio;
const {Option} = Select;
const Chapters = (props) => {
  const {title, slugTerm,  path, pubType='info', url, count, page} = props;
  const intl = useIntl();
  const [categories, setCategories] = useState([]);
  const [prov, setProv] = useState([]);
  const [city, setCity] = useState([]);
  const [loading, setLoading] = useState(false);
  // eslint-disable-next-line no-unused-vars
  const [price, setPrice] = useState([
    {label: intl.formatMessage({ id: 'component.infoList.chapters.price.unlimited' }), value: '0,0'},
    {label: intl.formatMessage({ id: 'component.infoList.chapters.price.lt100' }), value: '0,100'},
    {label: intl.formatMessage({ id: 'component.infoList.chapters.price.100to200' }), value: '100,200'},
    {label: intl.formatMessage({ id: 'component.infoList.chapters.price.200to500' }), value: '200,500'},
    {label: intl.formatMessage({ id: 'component.infoList.chapters.price.500to1000' }), value: '500,1000'},
    {label: intl.formatMessage({ id: 'component.infoList.chapters.price.1000to2000' }), value: '1000,2000'},
    {label: intl.formatMessage({ id: 'component.infoList.chapters.price.2000to3500' }), value: '2000,3500'},
    {label: intl.formatMessage({ id: 'component.infoList.chapters.price.gt3500' }), value: '3500,0'},
  ]);
  const actionRef = useRef();
  const [param, setParam] = useState({});

  const [cur, setCur] = useState(page ? parseInt(page, 10) : 1);
  const [data, setData] = useState({total: 0, pageSize: count, });

  const onPageChange = useCallback((pNum) => {
    setCur(pNum);
    if (pNum === 1)
      history.replace(url);
    else
      history.replace(url === '/' ? `/page/${pNum}` : `${url.endsWith('/') ? url : `${url}/`}page/${pNum}`);
  }, [url]);

  const itemRender = (current, type, originalElement) => {
    const target = url === '/' || url.endsWith('/') ? url : `${url  }/`;
    if (type === 'page')
      return <Link to={`${target}page/${current}`}>{current}</Link>
    if (type === 'prev')
      return <Link to={`${target}page/${current}`}>&lt;</Link>
    if (type === 'next')
      return <Link to={`${target}page/${current}`}>&gt;</Link>
    return originalElement;
  };

  useEffect(async () => {
    const res = await queryCategoryFromPlug({slugTerm});
    if (res?.data) {
      setCategories(res.data);
    }
    if (actionRef.current) {
      actionRef.current.reload();
    }
  }, [path]);

  useEffect(async () => {
    const res = await queryProvince();
    if (res?.data) {
      setProv(res.data);
    }
  }, []);

  const enumTags = (tags = []) => {
    const prefix = pubType === 'info' ? 'info' : 'product';
    const temp = [];
    for (let i = 0, len = tags.length; i < len; i += 1) {
      const tag = tags[i];
      if (tag)
        temp.push((<Tag key={`${tag.id}-${i}`}><Link to={`/${prefix}/tag/${tag.slug}`} target="_blank" rel="noopener">{tag.name}</Link></Tag>));
    }
    return temp;
  };

  const search = useCallback((v) => {
    setParam({...param, ...v});
  }, [param]);

  const checked = {color: 'blue', border: 0};

  const rendItem = useCallback(
    (term, tId) =>
      term?.length &&
      term.map((t, i) => (
        <Button
          key={t.key}
          value={t.key}
          style={tId === t.key ? checked : {border: 0}}
        >
          {i === 0
            ? intl.formatMessage({ id: 'component.infoList.chapters.price.unlimited' })
            : t.title}
        </Button>
      )),
    [checked, intl],
  );

  const rendProv = useCallback(
    (p) =>
      p?.length &&
      p.map((t) => (
        <Option key={t.id} value={t.id}>
          {t.name}
        </Option>
      )),
    [],
  );

  const rendCity = useCallback(
    (c, cId) =>
      c?.length &&
      cId &&
      c.map((t) => (
        <Button
          key={t.id}
          value={t.id}
          style={cId === t.id ? checked : {border: 0}}
        >
          {t.name}
        </Button>
      )),
    [checked],
  );

  const rendPrice = useCallback(
    (c, cId) =>
      c?.length &&
      c.map((t) => (
        <Button
          key={t.value}
          value={t.value}
          style={cId === t.value ? checked : {border: 0}}
        >
          {t.label}
        </Button>
      )),
    [checked],
  );
  const formLayout = {
    span: 24,
  };

  // 已移除骨架屏

  return (
    <div className={styles.listContainer}>
      <ProList
        headerTitle={title}
        actionRef={actionRef}
        rowKey="id"
        className={styles.articleList}
        form={formLayout}
        itemLayout="vertical"
        search={{
          optionRender: false,
          collapsed: false,
          labelWidth: 'auto',
          span: 24,
        }}
        params={param}
        request={async (params, sorter, filter) => {
          setLoading(true);
          try {
            const res = await queryBookList({
              pubType,
              path,
              ...params,
              pageSize: data.pageSize,
              current: cur,
              filter,
              sorter: {...sorter, "createTime":"descend"},
            });

            if (res?.data) {
              const state = {
                ...data,
                total: res.data.total, pageSize: res.data.pageSize,
              };
              setData(state);
            }

            return Promise.resolve({
              total: res?.data?.total || 0,
              data: res?.data?.rows || [],
              success: true,
            });
          } finally {
            setLoading(false);
          }
        }}
        pagination={{
          current: cur,
          onChange: onPageChange,
          itemRender,
          total: data.total,
          pageSize: data.pageSize,
          showQuickJumper: true,
          showSizeChanger: false,
          showTotal: (total, range) =>
            intl.formatMessage(
              { id: 'component.infoList.chapters.pagination.total' },
              { start: range[0], end: range[1], total },
            ),
          className: styles.pagination
        }}
        metas={{
          title: {
            dataIndex: 'pubTitle',
            title: intl.formatMessage({ id: 'component.infoList.chapters.col.title' }),
            search: false,
            render: (dom, entity) => {
              return <Link to={pubType === 'info' ? `/info-${entity.id}.html` : `/content-${entity.id}.html`} target="_blank" rel="noopener">{dom}</Link>;
            }
          },
          description: {
            search: false,
            render: (dom, item) => (
              <>
                {item.tags && enumTags(item.tags)}
              </>
            ),
          },
          extra: {
            search: false,
            render: (dom, item) => (
              <Link to={`/info-${item.id}.html`} rel="nofollow " target="_blank">
                <img width={160} height={120} alt="cover" src={item.cover} loading="lazy" onError={createImageErrorHandler({ width: '160', height: '120', fontSize: '14' })} style={{objectFit:'cover', background:'#f5f5f5'}}/>
              </Link>
            ),
          },
          content: {
            search: false,
            render: (dom, item) => {
              return <>
                <div>
                  <Space split={<Divider type="vertical" />}>
                    <span>{item.city ?? intl.formatMessage({ id: 'component.infoList.chapters.city.fallback' })}</span>
                    <span>{moment(item.createTime).fromNow()}</span>
                    <span style={{color: 'red'}}>
                      {intl.formatMessage(
                        { id: 'component.infoList.chapters.price.display' },
                        {
                          price:
                            item.ornPrice ??
                            intl.formatMessage({ id: 'component.infoList.chapters.price.negotiable' }),
                        },
                      )}
                    </span>
                  </Space>
                </div>
              <div
                dangerouslySetInnerHTML={{
                  __html:
                    pubType === 'info'
                      ? `${item.pubExcerpt} ... <a href="/info-${item.id}.html" target="_blank">>>${intl.formatMessage({
                          id: 'component.infoList.chapters.more',
                        })}</a>`
                      : `${item.pubExcerpt} ... <a href="/content-${item.id}.html" target="_blank">>>${intl.formatMessage({
                          id: 'component.infoList.chapters.more',
                        })}</a>`,
                }}
              />
              </>;
            },
          },
          termTypeId: { // 用list平铺展示当前分类下的所有子分类
            title: intl.formatMessage({ id: 'component.infoList.chapters.col.category' }),
            dataIndex: 'termTypeId',
            renderFormItem: (_, fieldConfig) => {
              if (fieldConfig.type === 'form') {
                return null;
              }
              return <Group
                className={styles.radioButton}
                onChange={(v) => search({termTypeId: v.target.value})}
              >
                {rendItem(categories, param.termTypeId)}
              </Group>;
            }
          },
          city: {
            title: intl.formatMessage({ id: 'component.infoList.chapters.col.region' }),
            renderFormItem: () => {
              return <>
                <Select showSearch style={{width: '120px'}} defaultValue=""
                        onChange={async (v) => {
                            if (v === "") {
                              search({province: null, city: null});
                              setCity([]);
                              return;
                            }
                            const res = await queryCity(v);
                            if (res?.data) {
                              setCity([{id: '-1', name: intl.formatMessage({ id: 'component.infoList.chapters.region.allProvince' }), parentId: v, provName: ''}, ...res.data]);
                            }
                            search({province: v, city: "-1"});
                          }
                        }>
                  <Option key="" value="">
                    {intl.formatMessage({ id: 'component.infoList.chapters.region.selectProvince' })}
                  </Option>
                  {rendProv(prov)}</Select>
                <Group
                  className={styles.radioButton}
                  onChange={(v) => search({city: v.target.value})}
                >
                  {rendCity(city, param.city)}
                </Group></>;
            },
          },
          price: {
            title: intl.formatMessage({ id: 'component.infoList.chapters.col.price' }),
            renderFormItem: () => {
              return <>
                <Group
                  className={styles.radioButton}
                  onChange={(v) => search({price: v.target.value})}
                >
                  {rendPrice(price, param.price)}
                </Group>
              </>;
            }
          },
        }}
      />
      {/* 已去除骨架屏 */}
    </div>
  );
};

export default Chapters;
