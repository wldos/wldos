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
        label="新密保问题"
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
        <Button onClick={() => handleModalVisible(false, values)}>取消</Button>
        <Button type="primary" onClick={() => handleNext()}>提交</Button>
      </>
    );
  };

  const old = () => {
    return (
      <FormItem
        name="oldBakEmail"
        label="原密保问题"
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
      title="密保问题"
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
