import React from 'react';
import {Link} from 'umi';
import moment from 'moment';
import styles from './index.less';
import {Avatar} from "antd";

const ChapterListContent = ({ data: { id, pubExcerpt, pubType, createBy, createTime, member } }) => (
  <div className={styles.listContent}>
    {/* eslint-disable-next-line no-nested-ternary */}
    <div className={styles.description} dangerouslySetInnerHTML={{__html: pubType === 'book' ?
        `${pubExcerpt} ... <a href="/product-${id}.html" target="_blank">>>详情</a>`
    : (pubType === 'info' ? `${pubExcerpt} ... <a href="/info-${id}.html" target="_blank">>>详情</a>`:
          (pubType === 'doc' ? `${pubExcerpt} ... <a href="/doc/book/${id}.html" target="_blank">>>全文</a>` :
            `${pubExcerpt} ... <a href="/archives-${id}.html" target="_blank">>>全文</a>`))}}/>
    {member && (
    <div className={styles.extra}>
      <Avatar
        src={member.avatar}
        tips={member.nickname}
      />
      <Link to={`/archives-author/${createBy}.html`} target="_blank" rel="noopener">{member.nickname.length > 2 ? `${member.nickname.substring(0, 2)}...` : `${member.nickname}`}</Link>{' 发布于'}
      <em>{moment(createTime).format('YYYY-MM-DD HH:mm')}</em>
    </div>)}
  </div>
);

export default ChapterListContent;
