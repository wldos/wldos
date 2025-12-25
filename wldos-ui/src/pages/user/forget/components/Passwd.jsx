import React, {useState} from 'react';
import {Button, Form, Input, Modal} from 'antd';
import styles from "@/pages/user/register/style.less";
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

const PasswdReset = (props) => {
  const [form] = Form.useForm();
  const intl = useIntl();
  const [visible, setVisible] = useState(false);
  const [popover, setPopover] = useState(false);

  const confirmDirty = false;
  const {
    onSubmit: handleUpdate,
    onCancel: handleModalVisible,
    modalVisible,
    values,
    hideOld,
  } = props;

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
          name="loginName"
          label="用户名"
          hidden={hideOld}
          className={
            form.getFieldValue('loginName') &&
            form.getFieldValue('loginName').length > 0 &&
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
            placeholder={intl.formatMessage({
              id: 'userRegister.password.placeholder',
            })}
          />
        </FormItem>
        <FormItem
          name="password"
          label="新密码"
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
          label="确 认"
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

  const renderFooter = () => {

    return (
      <>
        <Button onClick={() => handleModalVisible(false, values)}>取消</Button>
        <Button type="primary" onClick={() => handleNext()}>提交</Button>
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
      title="重置密码"
      visible={modalVisible}
      footer={renderFooter()}
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

export default PasswdReset;
