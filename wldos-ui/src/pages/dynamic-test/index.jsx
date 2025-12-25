import React from 'react';
import { Card, Typography, Space, Tag } from 'antd';
import { connect } from 'umi';

const { Title, Paragraph } = Typography;

const DynamicTestPage = () => {
  return (
    <Card>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <div>
          <Title level={2}>动态路由测试页面</Title>
          <Paragraph>
            这是一个通过后端菜单配置动态加载的页面组件。
          </Paragraph>
        </div>

        <div>
          <Title level={4}>当前路径信息</Title>
          <Paragraph>
            <Tag color="blue">路径: {window.location.pathname}</Tag>
          </Paragraph>
        </div>

        <div>
          <Title level={4}>功能说明</Title>
          <Paragraph>
            <ul>
              <li>此页面通过后端菜单配置动态加载</li>
              <li>支持组件、内链、外链等多种打开方式</li>
              <li>与静态路由无缝融合，注意组件必须有首页index.jsx</li>
              <li>支持权限控制和菜单层级</li>
            </ul>
          </Paragraph>
        </div>

        <div>
          <Title level={4}>后端菜单配置示例</Title>
          <Paragraph>
            <pre style={{ background: '#f5f5f5', padding: '16px', borderRadius: '4px' }}>
{`{
  "path": "/dynamic-test",
  "name": "动态测试页面",
  "component": "./dynamic-test",
  "openMethod": "component",
  "menuLevel": "menu",
  "icon": "dashboard",
  "sort": 1,
  "visible": true
}`}
            </pre>
          </Paragraph>
        </div>
      </Space>
    </Card>
  );
};

export default connect(({ routing }) => ({
  location: routing?.location
}))(DynamicTestPage);
