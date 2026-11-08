import React, { useEffect, useState } from 'react';
import { Card, Col, Row, Tag, Typography } from 'antd';
import { Link } from 'umi';
import { PageContainer } from '@ant-design/pro-layout';
import {
  TeamOutlined,
  UserOutlined,
  SafetyCertificateOutlined,
  PartitionOutlined,
  GlobalOutlined,
} from '@ant-design/icons';
import { adminListPageContainerProps } from '@/pages/commercial/_shared/pageContainerStyle';
import { fetchPlatformOperatorContext } from './service';

const { Text } = Typography;

const OPS_LINKS = [
  { path: '/admin/organ/user?platformOps=1', title: '用户管理', icon: <UserOutlined /> },
  { path: '/admin/organ/segment?platformOps=1', title: '会员分群', icon: <TeamOutlined /> },
  { path: '/admin/auth/role?platformOps=1', title: '角色管理', icon: <SafetyCertificateOutlined /> },
  { path: '/admin/organ/org?platformOps=1', title: '组织管理', icon: <PartitionOutlined /> },
  { path: '/admin/dom/domain?platformOps=1', title: '站点管理', icon: <GlobalOutlined /> },
];

const PlatformOperatorHome = () => {
  const [ctx, setCtx] = useState(null);

  useEffect(() => {
    fetchPlatformOperatorContext().then((res) => {
      if (res?.code === 200) {
        setCtx(res.data);
      }
    });
  }, []);

  return (
    <PageContainer {...adminListPageContainerProps} title="平台运营">
      <Row gutter={[16, 16]}>
        <Col span={24}>
          {ctx?.ready ? (
            <Tag color="blue">
              {ctx.comName} · {ctx.siteName || ctx.siteDomain}
            </Tag>
          ) : (
            <Text type="secondary">运营主体尚未就绪，请重启服务以完成 bootstrap 或检查 wo_options。</Text>
          )}
        </Col>
        {OPS_LINKS.map((item) => (
          <Col xs={24} sm={12} md={8} key={item.path}>
            <Link to={item.path}>
              <Card hoverable size="small">
                <Card.Meta avatar={item.icon} title={item.title} />
              </Card>
            </Link>
          </Col>
        ))}
      </Row>
    </PageContainer>
  );
};

export default PlatformOperatorHome;
