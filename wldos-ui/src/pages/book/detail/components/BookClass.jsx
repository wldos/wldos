import { Card } from 'antd';
import React, {useEffect, useState} from 'react';
import styles from '../../read/style.less';
import {queryBookList} from "@/pages/home/service";
import ElementList from "@/pages/book/read/list";

const count = 8;
const BookClass = (props) => {
  const { bookId } = props;
  const path = `/book-${bookId}.html`;

  const [cur, setCur] = useState(1);
  const [data, setData] = useState({list: [], total: 0, pageSize: count, current: 1,});
  const [loading, setLoading] = useState(false);

  useEffect(async () => {
    setLoading(true);
    const res = await queryBookList({
      pageSize: data.pageSize,
      current: 1,
      pubType: 'chapter',
      sorter: {"createTime":"ascend"},
      path
    });
    setLoading(false);
    if (res?.data) {
      const {rows = [], total = 0, current = 1, pageSize = count} = res.data;
      setData({list: rows, total, current, pageSize,});
    }
  }, []);

  const fetchMore = async (num) => {
    setLoading(true);
    const totalPageNum = Math.floor((parseInt(data.total, 10) - 1) / parseInt(data.pageSize, 10)) + 1;
    if (num > totalPageNum) {
      setLoading(false);
      return;
    }
    const res = await queryBookList({
      pageSize: data.pageSize,
      current: num,
      pubType: 'chapter',
      sorter: {"createTime": "ascend"},
      path
    });
    setLoading(false);
    if (res?.data) {
      const {rows = [], total = 0, current = 1, pageSize = count} = res.data;
      if (total === 0) {
        return;
      }

      const state = {
        ...data,
        list: data.list.concat(rows), total, current, pageSize, hasMore: true,
      };

      setData(state);
      setCur(num + 1);
    }
  };

  return (
    <Card className={styles.tabsCard} bordered={true} >
      {data.list && <ElementList fetchMore={fetchMore} list={data.list} pageSize={data.pageSize} loading={loading} total={data.total} cur={cur} />}
    </Card>
  );
};

export default BookClass;
