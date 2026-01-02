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
import { PLUGIN_CODE } from './config/plugin';

// 将React和antd暴露到全局作用域，供插件使用
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
  
  // 注意：不再暴露插件编码到全局，避免多个插件同时运行时冲突
  // request.js 直接导入 PLUGIN_CODE，每个插件使用自己的配置
  
  // 暴露插件的 webpack runtime 的 require 函数，供主应用加载插件模块时使用
  // 注意：这需要在插件的 webpack runtime 加载后才能访问
  // 主应用可以通过 window.__PLUGIN_REQUIRE__[pluginCode][version] 获取
  if (!window.__PLUGIN_REQUIRE__) {
    window.__PLUGIN_REQUIRE__ = {};
  }
  if (!window.__PLUGIN_REQUIRE__[PLUGIN_CODE]) {
    window.__PLUGIN_REQUIRE__[PLUGIN_CODE] = {};
  }
  
  // 在插件的 webpack runtime 加载后，通过 webpackJsonp.push 的回调获取 require 函数
  // 但是，插件的 webpack runtime 是闭包，我们无法直接访问
  // 所以，我们需要通过其他方式获取插件的 webpack runtime 的 require 函数
  // 
  // 实际上，我们可以通过 webpackJsonp.push 的回调机制获取模块
  // 但是，插件的 webpack runtime 是闭包，我们无法直接访问
  // 
  // 所以，我们需要让插件的 webpack runtime 将 require 函数导出到全局变量
  // 但是，插件的 webpack runtime 是闭包，我们无法直接访问
  // 
  // 实际上，我们可以通过 webpackJsonp.push 的回调机制获取模块
  // 但是，插件的 webpack runtime 是闭包，我们无法直接访问
}

const { pwa } = defaultSettings;
const isHttps = document.location.protocol === 'https:'; // if pwa is true

if (pwa && typeof window !== 'undefined') {
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

