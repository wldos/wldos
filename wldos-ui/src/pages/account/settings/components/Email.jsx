import React from 'react';
import {Button, Form, Input, Modal} from 'antd';
import {useIntl} from "umi";

const FormItem = Form.Item;

const formLayout = {
  labelCol: {
    span: 7,
  },
  wrapperCol: {
    span: 13,
  },
};

const BakEmailChange = (props) => {
  const [form] = Form.useForm();
  const intl = useIntl();
  const {
    onSubmit: handleUpdate,
    onCancel: handleModalVisible,
    modalVisible,
    values,
  } = props;

  const handleNext = async () => {
    const fieldsValue = await form.validateFields();
    const value = {...values, ...fieldsValue};
    handleUpdate(value);
  };

  const renderContent = () => {
    return (
      <FormItem
        name="bakEmail"
        label="新邮箱"
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'userRegister.bak-email.required',
            }),
          },
        ]}
      >
        <Input
          size="large"
          type="phone"
          placeholder={intl.formatMessage({
            id: 'userRegister.bak-email.placeholder',
          })}
        />
      </FormItem>
    );
  };

  const renderFooter = () => {

    return (
      <>
        <Button onClick={() => handleModalVisible(false, values)}>取消</Button>
        <Button type="primary" onClick={() => handleNext()}>提交</Button>
      </>
    );
  };

  const old = () => {
    return (
      <FormItem
        name="oldBakEmail"
        label="原备用邮箱"
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'userRegister.bak-email.required',
            }),
          },
        ]}
      >
        <Input
          size="large"
          type="phone"
          placeholder={intl.formatMessage({
            id: 'userRegister.bak-email.placeholder',
          })}
        />
      </FormItem>
    );
  };

  return (
    <Modal
      width={640}
      bodyStyle={{
        padding: '32px 40px 48px',
      }}
      destroyOnClose
      title="备用邮箱"
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
      >
        {values.sec?.bakEmail ? old() : ''}
        {renderContent()}
      </Form>
    </Modal>
  );
};

export default BakEmailChange;
