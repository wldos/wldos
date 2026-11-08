import React, { useEffect, useState } from 'react';
import { Form, Button, Input, Space } from 'antd';
import { useIntl } from 'umi';
import { EyeOutlined, SearchOutlined } from '@ant-design/icons';
import PubSelectorModal from './PubSelectorModal';
import PubPickerDetailDrawer from './PubPickerDetailDrawer';
import { getAdminPub } from './cmsPickerService';

/** 表单项内的展示与操作（接收 Form.Item 注入的 value） */
const DocSelectInput = ({
  value,
  displayText,
  onOpenSelector,
  onPreview,
  placeholder,
  buttonText,
  previewText,
  previewDisabled,
  onChange: _formOnChange,
}) => (
  <Space.Compact style={{ display: 'flex', width: '100%' }}>
    <Input
      value={displayText || (value != null && value !== '' ? String(value) : '')}
      placeholder={placeholder}
      readOnly
      style={{ flex: 1, minWidth: 0 }}
      onClick={onOpenSelector}
    />
    <Button type="default" icon={<SearchOutlined />} onClick={onOpenSelector}>
      {buttonText}
    </Button>
    <Button
      type="default"
      icon={<EyeOutlined />}
      disabled={previewDisabled}
      onClick={onPreview}
    >
      {previewText}
    </Button>
  </Space.Compact>
);

/**
 * 文档选择表单项（存档 /archives 文档，供协议、产品管理等复用）
 * 须在 Form 内使用，内部通过 Form.useFormInstance() 写回选中项。
 *
 * @param {string} name - 表单字段名，如 'pubId'
 * @param {string} [label] - 表单项标签
 * @param {object[]} [rules] - 校验规则
 * @param {string} [extra] - 额外说明
 * @param {string} [placeholder] - 输入框占位
 * @param {string} [buttonText] - 按钮文案
 * @param {string} [modalTitle] - 弹窗标题
 * @param {boolean} [includeInternal] - true 时走管理端列表，可选 INTERNAL_ONLY（不含 page 类型）
 */
const DocSelectField = ({
  name = 'pubId',
  label,
  rules,
  extra,
  placeholder,
  buttonText,
  modalTitle,
  includeInternal = false,
}) => {
  const intl = useIntl();
  const form = Form.useFormInstance();
  const pubId = Form.useWatch(name, form);
  const [visible, setVisible] = useState(false);
  const [previewOpen, setPreviewOpen] = useState(false);
  const [selectedMeta, setSelectedMeta] = useState(null);

  useEffect(() => {
    if (!pubId) {
      setSelectedMeta(null);
      return;
    }
    if (selectedMeta?.id === pubId) {
      return;
    }
    let cancelled = false;
    getAdminPub(pubId).then((res) => {
      if (cancelled || res?.code !== 200) {
        return;
      }
      const data = res?.data;
      if (data) {
        setSelectedMeta({
          id: data.id,
          pubTitle: data.pubTitle,
          pubType: data.pubType,
          visibilityScope: data.visibilityScope,
        });
      }
    });
    return () => {
      cancelled = true;
    };
  }, [pubId, selectedMeta?.id]);

  const handleSelect = (id, title, record) => {
    if (form) {
      form.setFieldsValue({ [name]: id ?? undefined });
    }
    setSelectedMeta({
      id,
      pubTitle: title,
      pubType: record?.pubType,
      visibilityScope: record?.visibilityScope,
    });
    setVisible(false);
  };

  const displayText =
    pubId && selectedMeta?.pubTitle
      ? `${selectedMeta.pubTitle} · ID ${pubId}`
      : pubId
        ? `ID ${pubId}`
        : '';

  return (
    <>
      <Form.Item
        name={name}
        label={label || intl.formatMessage({ id: 'component.docSelect.label' })}
        rules={rules}
        extra={extra}
      >
        <DocSelectInput
          displayText={displayText}
          placeholder={placeholder || intl.formatMessage({ id: 'component.docSelect.placeholder' })}
          buttonText={buttonText || intl.formatMessage({ id: 'component.docSelect.button' })}
          previewText={intl.formatMessage({ id: 'component.docSelect.button.preview' })}
          previewDisabled={!pubId}
          onOpenSelector={() => setVisible(true)}
          onPreview={() => setPreviewOpen(true)}
        />
      </Form.Item>
      <PubSelectorModal
        visible={visible}
        title={modalTitle || intl.formatMessage({ id: 'component.docSelect.modal.title' })}
        pickerSource={includeInternal ? 'admin' : 'archives'}
        onSelect={handleSelect}
        onCancel={() => setVisible(false)}
      />
      <PubPickerDetailDrawer
        open={previewOpen}
        pubId={pubId}
        mode="preview"
        onClose={() => setPreviewOpen(false)}
        onMetaChange={(patch) => {
          if (patch?.id === pubId) {
            setSelectedMeta((prev) => (prev ? { ...prev, ...patch } : prev));
          }
        }}
      />
    </>
  );
};

export default DocSelectField;
export { PubSelectorModal };
