import React from 'react';
import {Button, Form, Input, Modal} from 'antd';
import { useIntl } from 'umi';

const FormItem = Form.Item;

const formLayout = {
  labelCol: {
    span: 7,
  },
  wrapperCol: {
    span: 13,
  },
};

const MobileChange = (props) => {
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
        name="mobile"
        label={intl.formatMessage({ id: 'account.security.phone.new' })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'userRegister.phone-number.required',
            }),
          },
        ]}
      >
        <Input
          size="large"
          type="phone"
          placeholder={intl.formatMessage({
            id: 'userRegister.phone-number.placeholder',
          })}
        />
      </FormItem>
    );
  };

  const renderFooter = () => {

    return (
      <>
        <Button onClick={() => handleModalVisible(false, values)}>
          {intl.formatMessage({ id: 'account.common.cancel' })}
        </Button>
        <Button type="primary" onClick={() => handleNext()}>
          {intl.formatMessage({ id: 'account.common.submit' })}
        </Button>
      </>
    );
  };

  const old = () => {
    return (
      <FormItem
        name="oldMobile"
        label={intl.formatMessage({ id: 'account.security.phone.old' })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'userRegister.phone-number.required',
            }),
          },
        ]}
      >
        <Input
          size="large"
          type="phone"
          placeholder={intl.formatMessage({
            id: 'userRegister.phone-number.placeholder',
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
      title={intl.formatMessage({ id: 'account.security.phone.modal' })}
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
      >
        {values.sec?.mobile ? old() : ''}
        {renderContent()}
      </Form>
    </Modal>
  );
};

export default MobileChange;
