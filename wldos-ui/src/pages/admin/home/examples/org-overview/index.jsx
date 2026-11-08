import React from 'react';
import { Alert } from 'antd';
import Monitor from '@/pages/sys/monitor';

/** 开源社区站示例管理端首页 */
const OrgAdminHome = (props) => (
  <>
    <Alert
      type="success"
      showIcon
      style={{ marginBottom: 16 }}
      message="开源社区站概览（示例）"
      description="适用于 wldos.org 等社区域；可替换为下载量、贡献者等社区向指标。"
    />
    <Monitor {...props} />
  </>
);

export default OrgAdminHome;
