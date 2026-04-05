/**
 * 门户静态首页 extraProps：按区块用多行文本编辑原始 HTML；提交时序列化为 portalStatic JSON（不用富文本组件，避免 HTML 被二次转义）。
 */
import React, { useCallback, useEffect, useState } from 'react';
import { Alert, Collapse, Input, Tabs, Typography } from 'antd';
import {
  PORTAL_STATIC_SECTION_KEYS,
  buildPortalStaticExtraProps,
  isPortalStaticExtraProps,
  parsePortalStaticToSectionMap,
} from '@/pages/home/static/portalStatic';

const { Panel } = Collapse;
const { TextArea } = Input;

const SECTION_LABELS = {
  hero: '首屏 Hero',
  coreCapabilities: '能做什么（三栏）',
  coreValue: '为什么选择',
  useCases: '应用场景',
  architecture: '架构优势',
  capabilities: '主要能力',
  techStack: '技术栈',
  techPhilosophy: '技术理念',
  cloudIot: '云物互联',
};

const SECTION_PLACEHOLDER = `直接粘贴或编写 HTML 片段（不含外层 <section>，与门户 StaticHome 内层 DOM/class 一致）。
示例：div.heroContent > div.heroBadge、h1、p.heroSubtitle、div.heroActions 等。
模板见 wldos-ui/docs/portal-static-home-template/sections/*.html`;

function emptySectionMap() {
  const o = {};
  PORTAL_STATIC_SECTION_KEYS.forEach((k) => {
    o[k] = '';
  });
  return o;
}

function sectionMapsEqual(a, b) {
  return PORTAL_STATIC_SECTION_KEYS.every((k) => (a[k] || '').trim() === (b[k] || '').trim());
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
      rows={16}
      placeholder={SECTION_PLACEHOLDER}
      style={{ fontFamily: 'Consolas, Monaco, "Courier New", monospace', fontSize: 12 }}
      spellCheck={false}
    />
  );
}

/**
 * 受控组件：value / onChange 为 extraProps 整串 JSON 字符串
 */
const PortalStaticExtraPropsEditor = ({ value, onChange }) => {
  const [sections, setSections] = useState(emptySectionMap);
  const [activeKey, setActiveKey] = useState(PORTAL_STATIC_SECTION_KEYS[0]);

  useEffect(() => {
    const parsed = parsePortalStaticToSectionMap(value);
    setSections((prev) => (sectionMapsEqual(prev, parsed) ? prev : parsed));
  }, [value]);

  const handleHtmlChange = useCallback(
    (key, html) => {
      setSections((prev) => {
        const next = { ...prev, [key]: html };
        onChange(buildPortalStaticExtraProps(next));
        return next;
      });
    },
    [onChange],
  );

  const showInvalidAlert = value && String(value).trim() && !isPortalStaticExtraProps(value);

  return (
    <div>
      {showInvalidAlert ? (
        <Alert
          type="warning"
          showIcon
          style={{ marginBottom: 12 }}
          message="当前扩展配置不是 portalStatic 合法 JSON，下方将以空内容开始；请从「高级：原始 JSON」备份原文后重新编辑或手工修正。"
        />
      ) : null}
      <Typography.Text type="secondary" style={{ display: 'block', marginBottom: 8 }}>
        按区块编辑<strong>原始 HTML</strong>，保存后写入扩展配置（与门户 home/static 的 dangerouslySetInnerHTML 一致）。留空的区块使用站点默认版块。
      </Typography.Text>
      <Tabs
        activeKey={activeKey}
        onChange={setActiveKey}
        destroyInactiveTabPane={false}
        type="card"
        size="small"
        style={{ marginBottom: 12 }}
      >
        {PORTAL_STATIC_SECTION_KEYS.map((k) => (
          <Tabs.TabPane tab={SECTION_LABELS[k] || k} key={k}>
            <SectionTextArea
              sectionKey={k}
              html={sections[k] || ''}
              onHtmlChange={handleHtmlChange}
            />
          </Tabs.TabPane>
        ))}
      </Tabs>
      <Collapse ghost>
        <Panel header="高级：原始 JSON（只读，便于排查）" key="raw">
          <TextArea
            readOnly
            rows={6}
            value={value || ''}
            placeholder="（空）"
            style={{ fontFamily: 'Consolas, Monaco, "Courier New", monospace', fontSize: 12 }}
          />
        </Panel>
      </Collapse>
    </div>
  );
};

export default PortalStaticExtraPropsEditor;
