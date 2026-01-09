/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, {useEffect, useRef, useState} from 'react';
import {Tag, Radio, Select, Space, Divider} from 'antd';
import ProList from '@ant-design/pro-list';
import {Link} from 'umi';
import styles from './index.less';
import {
  queryCategoryFromPid,
  queryTopCategory
} from "@/pages/sys/category/service";
import {queryBookList} from "@/pages/home/service";
import {queryProvince, queryCity} from "@/pages/account/settings/service";
import moment from "moment";
import {preUpdate} from "@/pages/book/create/service";
import EditBookForm from "@/pages/book/components/EditBookForm";
import {updateBook} from "@/pages/book/components/booklist";

const {Group, Button} = Radio;
const {Option} = Select;
const Chapters = (props) => {
  const {title,  path, pubType, dispatch, categoryList, tagData} = props;
  const [cat, setCat] = useState([]);
  const [categories, setCategories] = useState([]);
  const [prov, setProv] = useState([]);
  const [city, setCity] = useState([]);
  // eslint-disable-next-line no-unused-vars
  const [price, setPrice] = useState([{label: '不限', value: '0,0'},{label: '100元以下', value: '0,100'},{label: '100-200元', value: '100,200'},{label: '200-500元', value: '200,500'},
    {label: '500-1000元', value: '500,1000'},{label: '1000-2000元', value: '1000,2000'},{label: '2000-3500元', value: '2000,3500'},{label: '3500元以上', value: '3500,0'},]);
  const actionRef = useRef();
  const [param, setParam] = useState({});
  const [updateVisible, handleUpdateVisible] = useState(false);
  const [bookInfo, setBookInfo] = useState({});

  useEffect(async () => {
    const res = await queryTopCategory();
    if (res?.data) {
      setCat(res.data);
    }
  }, [path]);

  useEffect(async () => {
    const res = await queryProvince();
    if (res?.data) {
      setProv(res.data);
    }
  }, []);

  useEffect(() => {
    setParam({...param, pubType});
  }, []);

  const enumTags = (tags = []) => {
    // eslint-disable-next-line no-nested-ternary
    const prefix = pubType === 'info' ? 'info' : (pubType === 'book' ? 'product' : 'archives');
    const temp = [];
    for (let i = 0, len = tags.length; i < len; i += 1) {
      const tag = tags[i];
      if (tag)
        temp.push((<Tag key={`${tag.id}-${i}`}><Link to={`/${prefix}/tag/${tag.slug}`} target="_blank" rel="noopener">{tag.name}</Link></Tag>));
    }
    return temp;
  };

  const search = (v) => {
    setParam({...param, ...v});
  };
  const rendCat = (c) => c?.length && (
    c.map(t => {
      return <Option key={t.value} value={t.value}>{t.label}</Option>;
    })
  );
  const rendItem = (term, tId) => term?.length && (
      term.map((t, i) => {
        const checked = {color: 'blue', border: 0};
        return <Button key={t.key} value={t.key} style={tId === t.key ? checked : {border: 0}}>{ i === 0 ? '不限' : t.title}</Button>;
      })
  );
  const rendProv = (p) => p?.length && (
    p.map(t => {
      return <Option key={t.id} value={t.id}>{t.name}</Option>;
    })
  );
  const rendCity = (c, cId) => c?.length && cId && (
    c.map(t => {
      const checked = {color: 'blue', border: 0};
      return <Button key={t.id} value={t.id} style={cId === t.id ? checked : {border: 0}}>{t.name}</Button>;
    })
  );
  const rendPrice = (c, cId) => c?.length && (
    c.map(t => {
      const checked = {color: 'blue', border: 0};
      return <Button key={t.value} value={t.value} style={cId === t.value ? checked : {border: 0}}>{t.label}</Button>;
    })
  );
  const formLayout = {
    span: 24,
  };
  return (
    <>
      <ProList
        headerTitle={title}
        actionRef={actionRef}
        rowKey="id"
        className={styles.articleList}
        form={formLayout}
        itemLayout="vertical"
        search={{
          optionRender: false,
          collapsed: false
        }}
        params={param}
        request={async (params, sorter, filter) => {
          const res = await queryBookList({
            path,
            ...params,
            filter,
            sorter: {...sorter, "createTime":"descend"},
          });
          return Promise.resolve({
            total: res?.data?.total || 0,
            data: res?.data?.rows || [],
            success: true,
          });
        }}
        pagination={{pageSize: 20,}}
        metas={{
          title: {
            dataIndex: 'pubTitle',
            title: '标题',
            search: false,
            render: (dom, entity) => {
              // eslint-disable-next-line no-nested-ternary
              return <Link to={entity.pubType === 'info' ? `/info-${entity.id}.html` : entity.pubType === 'book' ? `/product-${entity.id}.html` : `/archives-${entity.id}.html`} target="_blank" rel="noopener">{dom}</Link>;
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
              pubType === 'info' ? <a onClick={async () => {
                const res = await preUpdate({id: item.id});
                if (res?.data) {
                  const {pubTypeExt, ...otherValues} = res.data;
                  const realValues = {...otherValues, ...pubTypeExt};
                  setBookInfo(realValues);
                }
                handleUpdateVisible(true);
              }} >编辑</a> :
                <Link to={`/space/book/${item.id}`} rel="noopener noreferrer" target="_blank">编辑</Link>
            ),
          },
          content: {
            search: false,
            render: (dom, item) => {
              return <>
                <div>
                  <Space split={<Divider type="vertical" />}>
                    <span>{item.city?? '火星'}</span>
                    <span>{moment(item.createTime).fromNow()}</span>
                    <span style={{color: 'red'}}>{item.ornPrice ? `￥${item.ornPrice}元` : '面议'}</span>
                  </Space>
                </div>
                {/* eslint-disable-next-line no-nested-ternary */}
              <div dangerouslySetInnerHTML={{__html: item.pubType === 'info' ? `${item.pubExcerpt} ... <a href="/info-${item.id}.html" target="_blank">>>详情</a>` :
                  (item.pubType === 'book' ? `${item.pubExcerpt} ... <a href="/product-${item.id}.html" target="_blank">>>详情</a>` :
                    `${item.pubExcerpt} ... <a href="/archives-${item.id}.html" target="_blank">>>详情</a>`)}} />
              </>;
            },
          },
          termTypeId: { // 用list平铺展示当前分类下的所有子分类
            title: '分类',
            dataIndex: 'termTypeId',
            renderFormItem: (_, fieldConfig) => {
              if (fieldConfig.type === 'form') {
                return null;
              }
              return <>
                <Select showSearch style={{width: '120px'}} defaultValue=""
                  onChange={
                    async (v) => {
                      if (v === "") {
                        search({termTypeId: null});
                        setCategories([]);
                        return;
                      }
                      const res = await queryCategoryFromPid({termTypeId: v});
                      if (res?.data) {
                        setCategories(res.data);
                      }
                      search({termTypeId: v});
                    }
                  }>
                  <Option key="" value="">-选择分类-</Option>
                  {rendCat(cat)}
                </Select>
                <Group
                  className={styles.radioButton}
                  onChange={(v) => search({termTypeId: v.target.value})}
                >
                  {rendItem(categories, param.termTypeId)}
                </Group>
              </>;
            }
          },
          city: {
            title: '区域',
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
                        setCity([{id: '-1', name: '全省', parentId: v, provName: ''}, ...res.data]);
                      }
                      search({province: v, city: "-1"});
                    }
                  }>
                  <Option key="" value="">-选择省分-</Option>
                  {rendProv(prov)}</Select>
                <Group
                  className={styles.radioButton}
                  onChange={(v) => search({city: v.target.value})}
                >
                  {rendCity(city, param.city)}
                </Group></>;
            }
          },
          price: {
            title: '价位',
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
      {bookInfo && Object.keys(bookInfo).length ? (
        <EditBookForm
          onSubmit={async (value) => {
            const success = await updateBook(value);

            if (success) {
              dispatch({ // 转发可以跳过effects，直接请求reducer
                type: 'bookSpace/updateCurBook',
                payload: {data: value},
              });
              handleUpdateVisible(false);
              setBookInfo({});
            }
          }}
          onCancel={() => {
            handleUpdateVisible(false);
            setBookInfo({});
          }}
          updateModalVisible={updateVisible}
          values={bookInfo}
          categories={categoryList}
          tagData={tagData}
        />
      ) : null}
    </>
  );
};

export default Chapters;
