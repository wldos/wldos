/*
 * SEO 模块（商业）：robots.txt 等。内容存于前端静态 public/robots.txt，后端保存时直接写入该目录。
 */
import React, { useEffect, useState } from 'react';
import { Card, Button, Input, message } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { getRobotsTxt, saveRobotsTxt } from './service';

const { TextArea } = Input;

export default function SeoPage() {
  const [content, setContent] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    getRobotsTxt()
      .then((res) => {
        const text = res?.data?.content ?? res?.content ?? (typeof res === 'string' ? res : '') ?? '';
        setContent(text);
      })
      .catch(() => message.error('加载失败'))
      .finally(() => setLoading(false));
  }, []);

  const handleSave = async () => {
    setSaving(true);
    try {
      await saveRobotsTxt(content);
      message.success('保存成功');
    } catch (e) {
      message.error(e?.data?.message || '保存失败');
    } finally {
      setSaving(false);
    }
  };

  return (
    <PageContainer title="robots.txt" content="查看、编辑站点根路径的 robots.txt，保存后直接写入前端 public 目录。">
      <Card loading={loading}>
        <TextArea
          rows={18}
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="User-agent: *&#10;Disallow: /admin&#10;..."
          style={{ fontFamily: 'monospace', fontSize: 13 }}
        />
        <div style={{ marginTop: 16 }}>
          <Button type="primary" loading={saving} onClick={handleSave}>
            保存
          </Button>
        </div>
      </Card>
    </PageContainer>
  );
}
