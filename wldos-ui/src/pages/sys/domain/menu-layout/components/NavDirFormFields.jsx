import React from 'react';
import { Form, Input, InputNumber, Select, Space, Tooltip } from 'antd';
import { InfoCircleOutlined } from '@ant-design/icons';

const FormItem = Form.Item;

const NavDirFormFields = ({ parentOptions = [], excludeResourceId }) => {
  const options = excludeResourceId
    ? parentOptions.filter((opt) => opt.value !== excludeResourceId)
    : parentOptions;

  return (
    <>
      <FormItem
        name="label"
        label={(
          <Space size={4}>
            分组名称
            <Tooltip title="侧栏展示的分组名">
              <InfoCircleOutlined style={{ color: '#999' }} />
            </Tooltip>
          </Space>
        )}
        rules={[
          { required: true, message: '请输入分组名称' },
          { max: 128, message: '最多 128 字' },
        ]}
      >
        <Input placeholder="分组名称" maxLength={128} />
      </FormItem>
      <FormItem
        name="overlayParentId"
        label={(
          <Space size={4}>
            本站上级
            <Tooltip title="留空表示挂到根节点">
              <InfoCircleOutlined style={{ color: '#999' }} />
            </Tooltip>
          </Space>
        )}
      >
        <Select
          allowClear
          placeholder="留空=挂根"
          options={options}
          showSearch
          optionFilterProp="label"
        />
      </FormItem>
      <FormItem name="overlayDisplayOrder" label="本站排序">
        <InputNumber style={{ width: '100%' }} placeholder="留空=默认 0" />
      </FormItem>
    </>
  );
};

export default NavDirFormFields;
