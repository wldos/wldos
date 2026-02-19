/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { Component } from 'react';
import { connect } from 'umi';
import { Tag, message } from 'antd';
import groupBy from 'lodash/groupBy';
import moment from 'moment';
import NoticeIcon from '../NoticeIcon';
import { markTicketRead, markAdminTicketRead } from '@/services/user';
import styles from './index.less';
import { history } from 'umi';

class GlobalHeaderRight extends Component {
  pollTimer = null;

  componentDidMount() {
    const { dispatch } = this.props;

    if (typeof Notification !== 'undefined' && Notification.permission === 'default') {
      Notification.requestPermission().catch(() => {});
    }

    if (dispatch) {
      // 首屏拉一次角标（用户侧不轮询，管理端轮询）
      dispatch({ type: 'global/fetchNoticesCount' });
      if (!this.props.disablePolling) {
        this.pollTimer = setInterval(() => {
          if (this.props.dispatch) {
            this.props.dispatch({ type: 'global/fetchNoticesCount' });
          }
        }, 60000);
      }
    }
  }

  componentWillUnmount() {
    if (this.pollTimer) {
      clearInterval(this.pollTimer);
      this.pollTimer = null;
    }
  }

  changeReadState = (clickedItem) => {
    const { id, link } = clickedItem;
    const { dispatch, noticeMode } = this.props;

    if (id && id.startsWith('ticket-')) {
      const ticketId = id.replace(/^ticket-/, '');
      if (ticketId) {
        if (noticeMode === 'adminTicket') {
          markAdminTicketRead(ticketId).then(() => {
            if (dispatch) {
              dispatch({ type: 'global/fetchNotices' });
              dispatch({ type: 'global/fetchNoticesCount' });
            }
          });
        } else {
          markTicketRead(ticketId);
        }
      }
    }
    if (dispatch) {
      dispatch({
        type: 'global/changeNoticeReadState',
        payload: id,
      });
    }
    if (link) {
      if (link.startsWith('http')) {
        window.open(link, '_blank');
      } else {
        history.push(link);
      }
    }
  };

  handleNoticeClear = (title, key) => {
    const { dispatch } = this.props;
    message.success(`${'清空了'} ${title}`);

    if (dispatch) {
      dispatch({
        type: 'global/clearNotices',
        payload: key,
      });
    }
  };

  getNoticeData = () => {
    const { notices = [] } = this.props;

    if (!notices || notices.length === 0 || !Array.isArray(notices)) {
      return {};
    }

    const newNotices = [];

    for (let i = 0, len = notices.length; i < len ; i += 1) {
      const notice = notices[i];

      const newNotice = { ...notice };

      if (newNotice.datetime) {
        newNotice.datetime = moment(notice.datetime).fromNow();
      }

      if (newNotice.id) {
        newNotice.key = newNotice.id;
      }

      if (newNotice.extra && newNotice.status) {
        const color = {
          todo: '',
          processing: 'blue',
          urgent: 'red',
          doing: 'gold',
        }[newNotice.status];
        newNotice.extra = (
          <Tag
            color={color}
            style={{
              marginRight: 0,
            }}
          >
            {newNotice.extra}
          </Tag>
        );
      }

      newNotices.push(newNotice);
    }

    return groupBy(newNotices, 'type');
  };

  getUnreadData = (noticeData) => {
    const unreadMsg = {};
    const keys = Object.keys(noticeData);
    for (let i = 0, len = keys.length; i < len; i += 1) {
      const key = keys[i];
      const value = noticeData[key];

      if (!unreadMsg[key]) {
        unreadMsg[key] = 0;
      }

      if (Array.isArray(value)) {
        unreadMsg[key] = value.filter((item) => !item.read).length;
      }
    }
    return unreadMsg;
  };

  render() {
    const { currentUser, fetchingNotices, onNoticeVisibleChange } = this.props;
    const noticeData = this.getNoticeData();
    const unreadMsg = this.getUnreadData(noticeData);
    return (
      <NoticeIcon
        className={styles.action}
        count={currentUser && currentUser.unreadCount}
        onItemClick={(item) => {
          this.changeReadState(item);
        }}
        loading={fetchingNotices}
        clearText="清空"
        viewMoreText="查看更多"
        onClear={this.handleNoticeClear}
        onPopupVisibleChange={(visible) => {
          if (visible && this.props.dispatch) {
            this.props.dispatch({ type: 'global/fetchNotices' });
          }
          onNoticeVisibleChange && onNoticeVisibleChange(visible);
        }}
        onViewMore={({ tabKey }) => {
          if (tabKey === 'notification') {
            history.push(this.props.noticeMode === 'adminTicket' ? '/admin/sys/ticket' : '/ticket/list');
          } else if (tabKey === 'message') history.push('/account/settings?tab=notification');
          else message.info('查看更多');
        }}
        clearClose
      >
        <NoticeIcon.Tab
          tabKey="notification"
          count={unreadMsg.notification}
          list={noticeData.notification}
          title="通知"
          emptyText="你已查看所有通知"
          showViewMore
        />
        <NoticeIcon.Tab
          tabKey="message"
          count={unreadMsg.message}
          list={noticeData.message}
          title="消息"
          emptyText="您已读完所有消息"
          showViewMore
        />
        <NoticeIcon.Tab
          tabKey="event"
          title="待办"
          emptyText="你已完成所有待办"
          count={unreadMsg.event}
          list={noticeData.event}
          showViewMore
        />
      </NoticeIcon>
    );
  }
}

export default connect(({ user, global, loading }) => ({
  currentUser: user.currentUser,
  collapsed: global.collapsed,
  noticeMode: global.noticeMode,
  fetchingMoreNotices: loading.effects['global/fetchMoreNotices'],
  fetchingNotices: loading.effects['global/fetchNotices'],
  notices: global.notices,
}))(GlobalHeaderRight);
