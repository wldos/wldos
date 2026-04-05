import React, { useState } from 'react';
import { Form, InputNumber, Button, Input } from 'antd';
import { useIntl } from 'umi';
import { SearchOutlined } from '@ant-design/icons';
import PubSelectorModal from './PubSelectorModal';

/** 表单项内的输入+按钮（接收 Form.Item 注入的 value） */
const DocSelectInput = ({ value, onOpenSelector, placeholder, buttonText }) => (
  <Input.Group compact style={{ display: 'flex' }}>
    <InputNumber
      value={value}
      min={1}
      placeholder={placeholder}
      readOnly
      style={{ flex: 1, minWidth: 0 }}
      onClick={onOpenSelector}
      controls={false}
    />
    <Button type="default" icon={<SearchOutlined />} onClick={onOpenSelector}>
      {buttonText}
    </Button>
  </Input.Group>
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
 */
const DocSelectField = ({
  name = 'pubId',
  label,
  rules,
  extra,
  placeholder,
  buttonText,
  modalTitle,
}) => {
  const intl = useIntl();
  const form = Form.useFormInstance();
  const [visible, setVisible] = useState(false);

  const handleSelect = (pubId) => {
    if (form) form.setFieldsValue({ [name]: pubId ?? undefined });
    setVisible(false);
  };

  return (
    <>
      <Form.Item
        name={name}
        label={label || intl.formatMessage({ id: 'component.docSelect.label' })}
        rules={rules}
        extra={extra}
      >
        <DocSelectInput
          placeholder={placeholder || intl.formatMessage({ id: 'component.docSelect.placeholder' })}
          buttonText={buttonText || intl.formatMessage({ id: 'component.docSelect.button' })}
          onOpenSelector={() => setVisible(true)}
        />
      </Form.Item>
      <PubSelectorModal
        visible={visible}
        title={modalTitle || intl.formatMessage({ id: 'component.docSelect.modal.title' })}
        onSelect={handleSelect}
        onCancel={() => setVisible(false)}
      />
    </>
  );
};

export default DocSelectField;
export { PubSelectorModal };
