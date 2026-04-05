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
        name="secQuest"
        label={intl.formatMessage({ id: 'account.security.question.new' })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'userRegister.sec-quest.required',
            }),
          },
        ]}
      >
        <Input
          size="large"
          type="phone"
          placeholder={intl.formatMessage({
            id: 'userRegister.sec-quest.placeholder',
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
        name="oldBakEmail"
        label={intl.formatMessage({ id: 'account.security.question.old' })}
        rules={[
          {
            required: true,
            message: intl.formatMessage({
              id: 'userRegister.sec-quest.required',
            }),
          },
        ]}
      >
        <Input
          size="large"
          type="phone"
          placeholder={intl.formatMessage({
            id: 'userRegister.sec-quest.placeholder',
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
      title={intl.formatMessage({ id: 'account.security.question.modal' })}
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
      >
        {values.sec?.secQuest ? old() : ''}
        {renderContent()}
      </Form>
    </Modal>
  );
};

export default MobileChange;
