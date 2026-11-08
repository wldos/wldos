import React, { useEffect, useMemo, useState } from 'react';
import { Checkbox, Form, Modal, Select, message } from 'antd';
import { cloneSite, queryPage } from '../service';

export default function CloneSiteForm({ visible, sourceRecord, onCancel, onSuccess }) {
  const [form] = Form.useForm();
  const [submitting, setSubmitting] = useState(false);
  const [domainOptions, setDomainOptions] = useState([]);

  const sourceDomainId = sourceRecord?.id != null ? String(sourceRecord.id) : undefined;

  const selectableTargets = useMemo(
    () => domainOptions.filter((o) => o.value !== sourceDomainId),
    [domainOptions, sourceDomainId],
  );

  useEffect(() => {
    if (!visible) {
      return;
    }
    queryPage({ current: 1, pageSize: 500 }).then((res) => {
      const rows = res?.data?.rows ?? [];
      setDomainOptions(
        rows.map((d) => ({
          label: `${d.siteName || d.id} (${d.siteDomain || '-'})`,
          value: d.id != null ? String(d.id) : '',
        })),
      );
    });
    form.setFieldsValue({
      sourceDomainId,
      targetDomainId: undefined,
        copyMenu: true,
        copyDomainResource: true,
        copyPages: true,
        copyDocs: true,
        copyTermBindings: true,
        copySiteConfig: false,
        copyDomainOptions: false,
      });
  }, [visible, sourceDomainId, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setSubmitting(true);
      const res = await cloneSite({
        sourceDomainId: values.sourceDomainId,
        targetDomainId: values.targetDomainId,
        options: {
          copyMenu: values.copyMenu,
          copyDomainResource: values.copyDomainResource,
          copyPages: values.copyPages,
          copyDocs: values.copyDocs,
          copyTermBindings: values.copyTermBindings,
          copySiteConfig: values.copySiteConfig,
          copyDomainOptions: values.copyDomainOptions,
        },
      });
      const data = res?.data ?? res;
      message.success(
        `复制完成：菜单关联 ${data?.menuResourceCount ?? 0}、域资源关联 ${data?.domainResourceCount ?? 0}、` +
          `页面 ${data?.pageCount ?? 0}、文档 ${data?.docCount ?? 0}（章节 ${data?.docChapterCount ?? 0}）、` +
          `分类/标签关联 ${data?.termBindingCount ?? 0}、域级参数 ${data?.domainOptionCount ?? 0}`,
      );
      onSuccess?.();
    } catch (e) {
      if (e?.errorFields) {
        return;
      }
      message.error(e?.data?.message || e?.message || '复制失败');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Modal
      title="复制子站"
      open={visible}
      onCancel={onCancel}
      onOk={handleSubmit}
      confirmLoading={submitting}
      destroyOnClose
      width={520}
    >
      <p style={{ color: '#666', marginBottom: 16 }}>
        从源域复制<strong>域资源关联</strong>（共享平台 wo_resource，不克隆资源本体）、已发布 CMS page 与文档库到目标域，用于英文子站初始化。
      </p>
      <Form form={form} layout="vertical">
        <Form.Item name="sourceDomainId" label="源域" rules={[{ required: true, message: '请选择源域' }]}>
          <Select disabled options={domainOptions} />
        </Form.Item>
        <Form.Item name="targetDomainId" label="目标域" rules={[{ required: true, message: '请选择目标域' }]}>
          <Select
            showSearch
            optionFilterProp="label"
            placeholder="选择目标域"
            options={selectableTargets}
          />
        </Form.Item>
        <Form.Item name="copyMenu" valuePropName="checked">
          <Checkbox>复制门户菜单域资源关联（menu / plugin_menu）</Checkbox>
        </Form.Item>
        <Form.Item name="copyDomainResource" valuePropName="checked">
          <Checkbox>复制全部域资源关联（含首页 static-org、路由 url 等）</Checkbox>
        </Form.Item>
        <Form.Item name="copyPages" valuePropName="checked">
          <Checkbox>复制已发布 page 内容</Checkbox>
        </Form.Item>
        <Form.Item name="copyDocs" valuePropName="checked">
          <Checkbox>复制文档库（doc 及章节）</Checkbox>
        </Form.Item>
        <Form.Item name="copyTermBindings" valuePropName="checked">
          <Checkbox>复制分类与标签关联</Checkbox>
        </Form.Item>
        <Form.Item name="copySiteConfig" valuePropName="checked">
          <Checkbox>复制站点配置（页脚/QR/多语言/管理端首页，写入 wo_domain）</Checkbox>
        </Form.Item>
        <Form.Item name="copyDomainOptions" valuePropName="checked">
          <Checkbox>复制域级系统参数（不含已迁入 wo_domain 的站点项；默认不勾选）</Checkbox>
        </Form.Item>
      </Form>
    </Modal>
  );
}
