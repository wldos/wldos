/**
 * wldos.org 门户静态首页 extraProps 分区块 HTML 编辑
 */
import React, { useCallback, useEffect, useState } from 'react';
import { Alert, Collapse, Input, Tabs, Typography } from 'antd';
import {
  PORTAL_STATIC_ORG_SECTION_KEYS,
  buildPortalStaticOrgExtraProps,
  isPortalStaticOrgExtraProps,
  parsePortalStaticOrgToSectionMap,
} from '@/pages/home/static-org/portalStaticOrg';

const { Panel } = Collapse;
const { TextArea } = Input;

const SECTION_LABELS = {
  hero: '首屏 Hero',
  pillars: '三列能力',
  getStarted: '获取软件',
  contribute: '参与贡献',
  techStack: '技术栈',
};

const SECTION_PLACEHOLDER = `开源门户 HTML 片段（class 建议 portalStaticOrg_{区块key}_*）。
留空则使用 static-org 默认 JSX。org 站独立通路，勿在 extraProps 中引导 wldos.com。`;

function emptySectionMap() {
  const o = {};
  PORTAL_STATIC_ORG_SECTION_KEYS.forEach((k) => {
    o[k] = '';
  });
  return o;
}

function sectionMapsEqual(a, b) {
  return PORTAL_STATIC_ORG_SECTION_KEYS.every((k) => (a[k] || '').trim() === (b[k] || '').trim());
}

function SectionTextArea({ sectionKey, html, onHtmlChange }) {
  const handleChange = useCallback(
    (e) => {
      onHtmlChange(sectionKey, e.target.value);
    },
    [sectionKey, onHtmlChange],
  );

  return (
    <TextArea
      value={html}
      onChange={handleChange}
      rows={14}
      placeholder={SECTION_PLACEHOLDER}
      style={{ fontFamily: 'Consolas, Monaco, "Courier New", monospace', fontSize: 12 }}
      spellCheck={false}
    />
  );
}

const PortalStaticOrgExtraPropsEditor = ({ value, onChange }) => {
  const [sections, setSections] = useState(emptySectionMap);
  const [activeKey, setActiveKey] = useState(PORTAL_STATIC_ORG_SECTION_KEYS[0]);

  useEffect(() => {
    const parsed = parsePortalStaticOrgToSectionMap(value);
    setSections((prev) => (sectionMapsEqual(prev, parsed) ? prev : parsed));
  }, [value]);

  const handleHtmlChange = useCallback(
    (key, html) => {
      setSections((prev) => {
        const next = { ...prev, [key]: html };
        onChange(buildPortalStaticOrgExtraProps(next));
        return next;
      });
    },
    [onChange],
  );

  const showInvalidAlert = value && String(value).trim() && !isPortalStaticOrgExtraProps(value);

  return (
    <div>
      {showInvalidAlert ? (
        <Alert
          type="warning"
          showIcon
          style={{ marginBottom: 12 }}
          message="当前扩展配置不是 portalStaticOrg 合法 JSON，下方将以空内容开始。"
        />
      ) : null}
      <Typography.Text type="secondary" style={{ display: 'block', marginBottom: 8 }}>
        开源门户 <strong>home/static-org</strong>：按区块编辑 HTML，schemaType=portalStaticOrg。
      </Typography.Text>
      <Tabs
        activeKey={activeKey}
        onChange={setActiveKey}
        destroyInactiveTabPane={false}
        type="card"
        size="small"
        style={{ marginBottom: 12 }}
      >
        {PORTAL_STATIC_ORG_SECTION_KEYS.map((k) => (
          <Tabs.TabPane tab={SECTION_LABELS[k] || k} key={k}>
            <SectionTextArea sectionKey={k} html={sections[k] || ''} onHtmlChange={handleHtmlChange} />
          </Tabs.TabPane>
        ))}
      </Tabs>
      <Collapse ghost>
        <Panel header="高级：原始 JSON（portalStaticOrg）" key="raw">
          <TextArea value={value} rows={8} readOnly style={{ fontSize: 12 }} />
        </Panel>
      </Collapse>
    </div>
  );
};

export default PortalStaticOrgExtraPropsEditor;
