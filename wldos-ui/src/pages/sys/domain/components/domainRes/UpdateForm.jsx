import React, {useEffect, useState} from 'react';
import {Button, Form, Input, Select, TreeSelect} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';

const FormItem = Form.Item;

const formLayout = {
  labelCol: {
    span: 7,
  },
  wrapperCol: {
    span: 13,
  },
};

const UpdateForm = (props) => {
  const [form] = Form.useForm();
  const [urlVisible, setUrlVisible] = useState(false);
  const {
    onSubmit: handleUpdate,
    onCancel: handleUpdateModalVisible,
    updateModalVisible,
    values,
    templates,
    categoryList
  } = props;

  useEffect(() => {
    setUrlVisible(values?.url);
  }, [values?.url])

  const handleNext = async () => {
    const fieldsValue = await form.validateFields();
    const value = {...values, ...fieldsValue};
    handleUpdate(value);
  };

  const handleChange = (v) => {
    setUrlVisible(v === 'url' || v === 'component');
  };

  const renderContent = () => {
    return (
      <>
        <FormItem name="moduleName" label="动态模板">
          <Select
            style={{
              width: '100%',
            }}
            filterOption={(input, option) =>
              option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
            }
            onChange={handleChange}
            options={templates}
          >
          </Select>
        </FormItem>
        <FormItem name="termTypeId" label="关联分类">
          <TreeSelect
            showSearch
            treeData={categoryList}
            treeDefaultExpandAll
            allowClear
            dropdownStyle={{ maxHeight: 400, overflow: 'auto'}}
            placeholder="请选择"
            treeNodeFilterProp="title"
          />
        </FormItem>
        {urlVisible && (<FormItem name="url" label="指定url" rules={[
          {
            required: false,
            message: '最长256个字符！',
            max: 256,
          },
        ]}>
          <Input placeholder="完整url: http://www.wldos.com" />
        </FormItem>)}
      </>
    );
  };

  const renderFooter = () =>
    (
      <>
        <Button onClick={() => handleUpdateModalVisible(false, values)}>取消</Button>
        <Button type="primary" onClick={() => handleNext()}>提交</Button>
      </>
    );

  return (
    <FullscreenModal
      width={640}
      bodyStyle={{
        padding: '32px 40px 48px'
      }}
      destroyOnClose
      title={`域资源配置-${props.values.resourceName}`}
      visible={updateModalVisible}
      footer={renderFooter()}
      onCancel={() => handleUpdateModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={{
          moduleName: props.values.moduleName,
          termTypeId: props.values.termTypeId,
          url: props.values.url,
        }}
      >
        {renderContent()}
      </Form>
    </FullscreenModal>
  );
};

export default UpdateForm;
