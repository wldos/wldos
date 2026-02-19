/*
 * 服务协议预览页（公开）
 */
import React, { useEffect, useState } from 'react';
import { Card, Spin, Tabs } from 'antd';
import { getAgreementListByType, getAgreementDetail } from '@/pages/user/login/agreement';

const AgreementView = () => {
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeId, setActiveId] = useState(null);
  const [content, setContent] = useState('');
  const [contentLoading, setContentLoading] = useState(false);

  useEffect(() => {
    getAgreementListByType('ORDER')
      .then((rows) => {
        setList(Array.isArray(rows) ? rows : []);
        if (rows?.[0]?.id) {
          setActiveId(rows[0].id);
        }
      })
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    if (!activeId) {
      setContent('');
      return;
    }
    setContentLoading(true);
    getAgreementDetail(activeId)
      .then((res) => {
        const data = res?.data?.data ?? res?.data;
        const html = data?.contentHtml ?? data?.content ?? '';
        setContent(html || '<p>暂无内容</p>');
      })
      .catch(() => setContent('<p>加载失败</p>'))
      .finally(() => setContentLoading(false));
  }, [activeId]);

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: 80 }}>
        <Spin size="large" />
      </div>
    );
  }

  if (list.length === 0) {
    return (
      <Card title="服务协议">
        <p style={{ color: 'rgba(0,0,0,0.45)' }}>暂无生效的服务协议</p>
      </Card>
    );
  }

  const items = list.map((a) => ({
    key: String(a.id),
    label: a.title || a.agreementCode || '协议',
    children: contentLoading ? (
      <div style={{ textAlign: 'center', padding: 40 }}><Spin /></div>
    ) : (
      <div className="agreement-content" style={{ lineHeight: 1.7 }} dangerouslySetInnerHTML={{ __html: content }} />
    ),
  }));

  return (
    <Card title="服务协议">
      <Tabs
        activeKey={activeId ? String(activeId) : undefined}
        onChange={(k) => setActiveId(k ? Number(k) : null)}
        items={items}
      />
    </Card>
  );
};

export default AgreementView;
