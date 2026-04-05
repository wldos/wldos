import React, {useEffect, useState} from 'react';
import {Button, Form, Input, Modal} from 'antd';
import styles from "@/pages/user/register/style.less";
import { useIntl } from 'umi';
import {fetchEncryptKey} from "@/services/login";

const FormItem = Form.Item;

const formLayout = {
  labelCol: {
    span: 7,
  },
  wrapperCol: {
    span: 13,
  },
};

const PasswdChange = (props) => {
  const [form] = Form.useForm();
  const intl = useIntl();
  const [visible, setVisible] = useState(false);
  const [popover, setPopover] = useState(false);
  const [encryptKey, setEncryptKey] = useState('');

  const confirmDirty = false;
  const {
    onSubmit: handleUpdate,
    onCancel: handleModalVisible,
    modalVisible,
    values,
    hideOld,
  } = props;

  useEffect(async () => {
    const res = await fetchEncryptKey();
    if (res?.data) {
      setEncryptKey(res.data);
    }
  }, []);

  const handleNext = async () => {
    const fieldsValue = await form.validateFields();
    const value = {...values, ...fieldsValue};
    handleUpdate(value);
  };

  const checkConfirm = (_, value) => {
    const promise = Promise;

    if (value && value !== form.getFieldValue('password')) {
      return promise.reject(
        intl.formatMessage({
          id: 'userRegister.password.twice',
        }),
      );
    }

    return promise.resolve();
  };

  const checkPassword = (_, value) => {
    const promise = Promise; // 没有值的情况

    if (!value) {
      setVisible(!!value);
      return promise.reject(
        intl.formatMessage({
          id: 'userRegister.password.required',
        }),
      );
    }

    if (!visible) {
      setVisible(!!value);
    }

    setPopover(!popover);

    if (value && confirmDirty) {
      form.validateFields(['confirm']);
    }

    return promise.resolve();
  };

  const renderContent = () => {
    return (
      <>
        <FormItem
          name="oldPasswd"
          label={intl.formatMessage({ id: 'account.security.password.old' })}
          hidden={hideOld}
          className={
            form.getFieldValue('oldPasswd') &&
            form.getFieldValue('oldPasswd').length > 0 &&
            styles.password
          }
          rules={[
            {
              required: !hideOld,
              message: intl.formatMessage({
                id: 'userRegister.password.required',
              }),
            },
          ]}
        >
          <Input
            size="large"
            type="password"
            placeholder={intl.formatMessage({
              id: 'userRegister.password.placeholder',
            })}
          />
        </FormItem>
        <FormItem
          name="password"
          label={intl.formatMessage({ id: 'account.security.password.new' })}
          className={
            form.getFieldValue('password') &&
            form.getFieldValue('password').length > 0 &&
            styles.password
          }
          rules={[
            {
              validator: checkPassword,
            },
          ]}
        >
          <Input
            size="large"
            type="password"
            placeholder={intl.formatMessage({
              id: 'userRegister.password.placeholder',
            })}
          />
        </FormItem>
        <FormItem
          name="confirm"
          label={intl.formatMessage({ id: 'account.security.password.confirm' })}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'userRegister.confirm-password.required',
              }),
            },
            {
              validator: checkConfirm,
            },
          ]}
        >
          <Input
            size="large"
            type="password"
            placeholder={intl.formatMessage({
              id: 'userRegister.confirm-password.placeholder',
            })}
          />
        </FormItem>
      </>
    );
  };

  const renderFooter = (encryptKey) => {

    return (
      <>
        <Button onClick={() => handleModalVisible(false, { ...values, encryptKey })}>
          {intl.formatMessage({ id: 'account.common.cancel' })}
        </Button>
        <Button type="primary" onClick={() => handleNext()}>
          {intl.formatMessage({ id: 'account.common.submit' })}
        </Button>
      </>
    );
  };

  return (
    <Modal
      width={640}
      bodyStyle={{
        padding: '32px 40px 48px',
      }}
      destroyOnClose
      title={intl.formatMessage({ id: 'account.security.password.modal' })}
      visible={modalVisible}
      footer={renderFooter(encryptKey)}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
      >
        {renderContent()}
      </Form>
    </Modal>
  );
};

export default PasswdChange;
