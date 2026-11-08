import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { Button, Checkbox, Form, Input, Modal, Select, Space, Tag, Tooltip, AutoComplete, message } from 'antd';
import { InfoCircleOutlined } from '@ant-design/icons';
import { fetchProvisionScenarios, fetchProvisionScenarioCodeDict, queryPage, saveProvisionScenarios } from '../service';

const SCENARIO_CODE_PATTERN = /^[a-z][a-z0-9_-]{0,47}$/;

const defaultCloneOptions = () => ({
  copyMenu: true,
  copyDomainResource: true,
  copyPages: false,
  copyDocs: false,
  copyTermBindings: false,
  copySiteConfig: false,
  copyDomainOptions: false,
});

const CloneOptionFields = ({ namePath, disabled }) => (
  <Space wrap size={[8, 4]}>
    <Form.Item name={[...namePath, 'cloneOptions', 'copyMenu']} valuePropName="checked" noStyle>
      <Checkbox disabled={disabled}>菜单</Checkbox>
    </Form.Item>
    <Form.Item name={[...namePath, 'cloneOptions', 'copyDomainResource']} valuePropName="checked" noStyle>
      <Checkbox disabled={disabled}>域资源</Checkbox>
    </Form.Item>
    <Form.Item name={[...namePath, 'cloneOptions', 'copyPages']} valuePropName="checked" noStyle>
      <Checkbox disabled={disabled}>页面</Checkbox>
    </Form.Item>
    <Form.Item name={[...namePath, 'cloneOptions', 'copyDocs']} valuePropName="checked" noStyle>
      <Checkbox disabled={disabled}>文档</Checkbox>
    </Form.Item>
    <Form.Item name={[...namePath, 'cloneOptions', 'copyTermBindings']} valuePropName="checked" noStyle>
      <Checkbox disabled={disabled}>分类标签</Checkbox>
    </Form.Item>
    <Form.Item name={[...namePath, 'cloneOptions', 'copySiteConfig']} valuePropName="checked" noStyle>
      <Checkbox disabled={disabled}>站点配置</Checkbox>
    </Form.Item>
    <Form.Item name={[...namePath, 'cloneOptions', 'copyDomainOptions']} valuePropName="checked" noStyle>
      <Checkbox disabled={disabled}>域参数</Checkbox>
    </Form.Item>
  </Space>
);

export default function ProvisionScenarioModal({ visible, onCancel, onSuccess }) {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [domainOptions, setDomainOptions] = useState([]);
  const [codeDict, setCodeDict] = useState([]);

  const loadDomains = useCallback(async () => {
    const res = await queryPage({ current: 1, pageSize: 500, comId: '0' });
    const rows = res?.data?.rows || [];
    setDomainOptions(
      rows.map((d) => ({
        value: String(d.id),
        label: `${d.siteName || d.id} (${d.siteDomain || '-'})`,
      })),
    );
  }, []);

  const loadScenarios = useCallback(async () => {
    setLoading(true);
    try {
      const res = await fetchProvisionScenarios();
      const list = res?.data || [];
      form.setFieldsValue({
        scenarios: list.length
          ? list.map((item) => ({
              ...item,
              templateDomainId: item.templateDomainId != null ? String(item.templateDomainId) : undefined,
              cloneOptions: { ...defaultCloneOptions(), ...(item.cloneOptions || {}) },
            }))
          : [],
      });
    } finally {
      setLoading(false);
    }
  }, [form]);

  const loadCodeDict = useCallback(async () => {
    const res = await fetchProvisionScenarioCodeDict();
    if (res?.data) {
      setCodeDict(res.data);
    }
  }, []);

  const codeAutoOptions = useMemo(
    () =>
      (codeDict || []).map((item) => ({
        value: item.value,
        label: `${item.label} (${item.value})`,
      })),
    [codeDict],
  );

  const handleCodeSelect = (fieldIndex, value) => {
    const hit = codeDict.find((o) => o.value === value);
    if (!hit) {
      return;
    }
    const scenarios = form.getFieldValue('scenarios') || [];
    const row = scenarios[fieldIndex];
    if (row && !row.label) {
      const next = [...scenarios];
      next[fieldIndex] = { ...row, label: hit.label };
      form.setFieldsValue({ scenarios: next });
    }
  };

  useEffect(() => {
    if (!visible) {
      return;
    }
    loadDomains();
    loadScenarios();
    loadCodeDict();
  }, [visible, loadDomains, loadScenarios, loadCodeDict]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setSubmitting(true);
      const scenarios = (values.scenarios || []).map((item) => ({
        code: item.code?.trim(),
        label: item.label?.trim(),
        templateDomainId: item.templateDomainId ? String(item.templateDomainId) : null,
        handoffLandingPath: item.handoffLandingPath?.trim() || undefined,
        defaultScenario: !!item.defaultScenario,
        enabled: item.enabled !== false,
        cloneOptions: item.cloneOptions || defaultCloneOptions(),
      }));
      const res = await saveProvisionScenarios({ scenarios });
      if (res?.code !== 200) {
        return;
      }
      message.success('入驻场景已保存');
      onSuccess?.();
    } catch (error) {
      if (error?.errorFields) {
        return;
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Modal
      title="入驻场景"
      open={visible}
      onCancel={onCancel}
      onOk={handleSubmit}
      confirmLoading={submitting}
      width={920}
      destroyOnClose
    >
      <Form form={form} layout="vertical" disabled={loading}>
        <Form.List name="scenarios">
          {(fields, { add, remove }) => (
            <>
              {fields.map((field, index) => (
                <div
                  key={field.key}
                  style={{ border: '1px solid #f0f0f0', borderRadius: 8, padding: 16, marginBottom: 12 }}
                >
                  <Space align="baseline" style={{ marginBottom: 12, width: '100%' }} wrap>
                    <Tag color="blue">场景 {index + 1}</Tag>
                    <Form.Item
                      {...field}
                      name={[field.name, 'code']}
                      rules={[
                        { required: true, message: '编码必填' },
                        {
                          validator: (_, v) => {
                            const s = v != null ? String(v).trim() : '';
                            if (!s) {
                              return Promise.reject(new Error('编码必填'));
                            }
                            if (!SCENARIO_CODE_PATTERN.test(s)) {
                              return Promise.reject(
                                new Error('须小写字母开头，仅含小写字母、数字、_、-'),
                              );
                            }
                            return Promise.resolve();
                          },
                        },
                      ]}
                      style={{ marginBottom: 0, minWidth: 200 }}
                    >
                      <AutoComplete
                        options={codeAutoOptions}
                        placeholder="选择或输入编码"
                        filterOption={(input, option) => {
                          const q = (input || '').toLowerCase();
                          const val = String(option?.value || '').toLowerCase();
                          const lab = String(option?.label || '').toLowerCase();
                          return val.includes(q) || lab.includes(q);
                        }}
                        onSelect={(value) => handleCodeSelect(field.name, value)}
                      />
                    </Form.Item>
                    <Tooltip title="预设来自类型管理 provision_scenario；新增编码在类型管理维护后即可选用，无需发版">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                    <Form.Item
                      {...field}
                      name={[field.name, 'label']}
                      rules={[{ required: true, message: '名称必填' }]}
                      style={{ marginBottom: 0, minWidth: 160 }}
                    >
                      <Input placeholder="展示名称" />
                    </Form.Item>
                    <Form.Item
                      {...field}
                      name={[field.name, 'templateDomainId']}
                      rules={[{ required: true, message: '模板站必选' }]}
                      style={{ marginBottom: 0, minWidth: 260 }}
                    >
                      <Select placeholder="模板站（com_id=0）" options={domainOptions} showSearch optionFilterProp="label" />
                    </Form.Item>
                    <Form.Item {...field} name={[field.name, 'defaultScenario']} valuePropName="checked" noStyle>
                      <Checkbox>默认</Checkbox>
                    </Form.Item>
                    <Form.Item {...field} name={[field.name, 'enabled']} valuePropName="checked" noStyle>
                      <Checkbox defaultChecked>启用</Checkbox>
                    </Form.Item>
                    <Button type="link" danger onClick={() => remove(field.name)}>
                      删除
                    </Button>
                  </Space>
                  <CloneOptionFields namePath={[field.name]} />
                </div>
              ))}
              <Button
                type="dashed"
                block
                onClick={() =>
                  add({
                    enabled: true,
                    defaultScenario: fields.length === 0,
                    cloneOptions: defaultCloneOptions(),
                  })
                }
              >
                添加场景
              </Button>
            </>
          )}
        </Form.List>
      </Form>
    </Modal>
  );
}
