import React from 'react';
import { Alert } from 'antd';
import Monitor from '@/pages/sys/monitor';

/** 商业站示例管理端首页：可替换为订单/License 等真实统计组件 */
const CommercialAdminHome = (props) => (
  <>
    <Alert
      type="info"
      showIcon
      style={{ marginBottom: 16 }}
      message="商业站运营概览（示例）"
      description="示例管理端首页组件；在管理端「域级系统参数」为本站配置 admin_home_config，将 url 指向本组件路径。可在此基础上接入订单、License 等接口。"
    />
    <Monitor {...props} />
  </>
);

export default CommercialAdminHome;
