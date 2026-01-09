/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

/**
 * 插件演示首页组件
 */
import React from 'react';
import { Card, Typography, Space, Tag } from 'antd';
import { AppstoreOutlined } from '@ant-design/icons';

const Home = () => {
  // 在组件内部解构，确保 Typography 已经从 window.antd 获取
  const { Title, Paragraph } = Typography;
  
  return (
    <div style={{ padding: 24 }}>
      <Card>
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          <div>
            <Title level={2}>
              <AppstoreOutlined style={{ marginRight: 8 }} />
              插件开发演示
            </Title>
            <Tag color="blue">v1.0.0</Tag>
          </div>
          
          <Paragraph>
            这是一个 WLDOS 插件开发演示项目，作为插件开发者的基础框架和参考示例。
          </Paragraph>
          
          <Card title="功能特性" size="small">
            <ul>
              <li>完整的插件结构示例</li>
              <li>后端 Controller、Service、Entity 示例</li>
              <li>前端 React 组件示例</li>
              <li>菜单和路由配置示例</li>
              <li>Hook 机制使用示例</li>
              <li>API 调用示例</li>
            </ul>
          </Card>
          
          <Card title="快速开始" size="small">
            <ol>
              <li>复制此插件作为基础框架</li>
              <li>修改 plugin.yml 中的插件信息</li>
              <li>根据业务需求修改 Java 代码</li>
              <li>根据业务需求修改前端组件</li>
              <li>运行 <code>mvn clean package</code> 构建插件</li>
              <li>将生成的 ZIP 文件上传到系统安装</li>
            </ol>
          </Card>
        </Space>
      </Card>
    </div>
  );
};

export default Home;

