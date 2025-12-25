import {
  Button,
  message,
  notification,
  Card,
  Table,
  Space,
  Tag,
  Modal,
  Form,
  Input,
  Select,
  Typography,
  Popconfirm
} from 'antd';
import React from 'react';
import ReactDOM from 'react-dom';
import { useIntl } from 'umi';
import defaultSettings from '../config/defaultSettings';
import request from '@/utils/request';

// 将React、antd和request暴露到全局作用域，供插件使用
if (typeof window !== 'undefined') {
  window.React = React;
  window.ReactDOM = ReactDOM;
  window.antd = {
    Button,
    message,
    notification,
    Card,
    Table,
    Space,
    Tag,
    Modal,
    Form,
    Input,
    Select,
    Typography,
    Popconfirm
  };
  // 暴露主应用的 request 工具，确保插件可以使用带 token 的请求
  // 插件的 request.js 会优先使用 window.request（如果存在）
  window.request = request;
}
const { pwa } = defaultSettings;
const isHttps = document.location.protocol === 'https:'; // if pwa is true

if (pwa && typeof window !== 'undefined') {
  const { message, Button, notification } = window.antd || antd;

  // Notify user if offline now
  window.addEventListener('sw.offline', () => {
    message.warning(
      useIntl().formatMessage({
        id: 'app.pwa.offline',
      }),
    );
  }); // Pop up a prompt on the page asking the user if they want to use the latest version

  window.addEventListener('sw.updated', (event) => {
    const e = event;

    const reloadSW = async () => {
      // Check if there is sw whose state is waiting in ServiceWorkerRegistration
      // https://developer.mozilla.org/en-US/docs/Web/API/ServiceWorkerRegistration
      const worker = e.detail && e.detail.waiting;

      if (!worker) {
        return true;
      } // Send skip-waiting event to waiting SW with MessageChannel

      await new Promise((resolve, reject) => {
        const channel = new MessageChannel();

        channel.port1.onmessage = (msgEvent) => {
          if (msgEvent.data.error) {
            reject(msgEvent.data.error);
          } else {
            resolve(msgEvent.data);
          }
        };

        worker.postMessage(
          {
            type: 'skip-waiting',
          },
          [channel.port2],
        );
      }); // Refresh current page to use the updated HTML and other assets after SW has skiped waiting

      window.location.reload(true);
      return true;
    };

    const key = `open${Date.now()}`;
    const btn = (
      <Button
        type="primary"
        onClick={() => {
          notification.close(key);
          reloadSW();
        }}
      >
        {useIntl().formatMessage({
          id: 'app.pwa.serviceworker.updated.ok',
        })}
      </Button>
    );
    notification.open({
      message: useIntl().formatMessage({
        id: 'app.pwa.serviceworker.updated',
      }),
      description: useIntl().formatMessage({
        id: 'app.pwa.serviceworker.updated.hint',
      }),
      btn,
      key,
      onClose: async () => null,
    });
  });
} else if ('serviceWorker' in navigator && isHttps) {
  // unregister service worker
  const { serviceWorker } = navigator;

  if (serviceWorker.getRegistrations) {
    serviceWorker.getRegistrations().then((sws) => {
      sws.forEach((sw) => {
        sw.unregister();
      });
    });
  }

  serviceWorker.getRegistration().then((sw) => {
    if (sw) sw.unregister();
  }); // remove all caches

  if (typeof window !== 'undefined' && window.caches && window.caches.keys) {
    caches.keys().then((keys) => {
      keys.forEach((key) => {
        caches.delete(key);
      });
    });
  }
}
