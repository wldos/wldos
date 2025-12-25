import React from 'react';
import {Button, Form, Input, Select} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';

const FormItem = Form.Item;
const {TextArea} = Input;
const {Option} = Select;

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
  const {
    onSubmit: handleUpdate,
    onCancel: handleUpdateModalVisible,
    updateModalVisible,
    values,
    appTypeList
  } = props;

  const handleNext = async () => {
    const fieldsValue = await form.validateFields();
    const value = {...values, ...fieldsValue};
    handleUpdate(value);
  };

  const renderContent = () => {
    return (
      <>
        <FormItem
          name="appName"
          label="应用名称"
          rules={[
            {
              required: true,
              message: '请输入应用名称，不能为空，最多12个字！',
              min: 1,
              max: 12,
            },
          ]}
        >
          <Input placeholder="请输入，不能为空，最多12个字！"/>
        </FormItem>
        <FormItem
          name="appDesc"
          label="应用描述"
          rules={[
            {
              required: false,
              message: '最多50个字！',
              max: 50,
            },
          ]}
        >
          <TextArea rows={4} placeholder="请输入描述，最多50个字"/>
        </FormItem>
        <FormItem name="appType" label="应用类型">
          <Select
            style={{
              width: '100%',
            }}
            filterOption={(input, option) =>
              option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
            }

            options={appTypeList}
          >
          </Select>
        </FormItem>
        <FormItem
          name="appCode"
          label="应用编码"
          rules={[
            {
              required: true,
              message: '最多五个字母！',
              max: 5,
            },
          ]}
        >
          <Input placeholder="请输入五位以内英文编码"/>
        </FormItem>
        <FormItem
          name="appSecret"
          label="应用密钥"
          rules={[
            {
              required: true,
              message: '最多50个半角字符！',
              max: 50,
            },
          ]}
        >
          <Input placeholder="请输入密钥"/>
        </FormItem>
        <FormItem name="isValid" label="应用状态">
          <Select
            style={{
              width: '100%',
            }}
          >
            <Option value="1">有效</Option>
            <Option value="0">无效</Option>
          </Select>
        </FormItem>
      </>
    );
  };

  const renderFooter = () => {

    return (
      <>
        <Button onClick={() => handleUpdateModalVisible(false, values)}>取消</Button>
        <Button type="primary" onClick={() => handleNext()}>提交</Button>
      </>
    );
  };

  return (
    <FullscreenModal
      width={640}
      bodyStyle={{
        padding: '32px 40px 48px'
      }}
      destroyOnClose
      title="应用配置"
      visible={updateModalVisible}
      footer={renderFooter()}
      onCancel={() => handleUpdateModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={{
          appName: props.values.appName,
          appDesc: props.values.appDesc,
          appType: props.values.appType,
          appCode: props.values.appCode,
          appSecret: props.values.appSecret,
          isValid: props.values.isValid,
        }}
      >
        {renderContent()}
      </Form>
    </FullscreenModal>
  );
};

export default UpdateForm;
