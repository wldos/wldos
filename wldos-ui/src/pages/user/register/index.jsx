import {Button, Form, Input, message, Popover, Progress, Select} from 'antd';
import React, {useEffect, useState} from 'react';
import {connect, FormattedMessage, history, Link, useIntl} from 'umi';
import styles from './style.less';
import VerifyCode from "@/pages/user/components/VerifyCode";
import {checkPasswdStatus} from "@/pages/user/register/service";

const FormItem = Form.Item;
const {Option} = Select;
const InputGroup = Input.Group;

const passwordStatusMap = {
  ok: (
    <div className={styles.success}>
      <FormattedMessage id="userRegister.strength.strong"/>
    </div>
  ),
  pass: (
    <div className={styles.warning}>
      <FormattedMessage id="userRegister.strength.medium"/>
    </div>
  ),
  poor: (
    <div className={styles.error}>
      <FormattedMessage id="userRegister.strength.short"/>
    </div>
  ),
};
const passwordProgressMap = {
  ok: 'success',
  pass: 'normal',
  poor: 'exception',
};

const Register = ({submitting, dispatch, userRegister}) => {
  const [count, setcount] = useState(0);
  const [visible, setvisible] = useState(false);
  const [prefix, setprefix] = useState('86');
  const [popover, setpopover] = useState(false);
  const intl = useIntl();
  const [verify, setVerify] = useState('');
  const [refresh, setRefresh] = useState(1);
  const [code, setCode] = useState(undefined);

  const confirmDirty = false;
  let interval;
  const [form] = Form.useForm();

  useEffect(() => {
    if (!userRegister) {
      return;
    }

    const account = form.getFieldValue('email');

    if (userRegister.status === 'ok') {
      message.success(userRegister.news).then(history.push({
        pathname: '/user/register-result',
        state: {
          account,
        },
      }));
    } else if (userRegister.status === 'error')
      message.error(userRegister.news).then(() => setRefresh(refresh+1));
  }, [userRegister]);
  useEffect(
    () => () => {
      clearInterval(interval);
    },
    [],
  );

  const onGetCaptcha = () => {
    let counts = 59;
    setcount(counts);
    interval = window.setInterval(() => {
      counts -= 1;
      setcount(counts);

      if (counts === 0) {
        clearInterval(interval);
      }
    }, 1000);
  };

  const getPasswordStatus = async () => {
    const passwd = form.getFieldValue('passwd');
    const res = await checkPasswdStatus({passwd});
    const { status } = res?.data?? {};
    if (!status) {
      return 'poor';
    }

    return status;
  };

  const onFinish = (values) => {
    if (verify !== 'ok') {
      return;
    }

    const {verifyCode, ...rest} = values;
    dispatch({
      type: 'userRegister/submit',
      payload: {verifyCode: `${verifyCode}${code}`, ...rest, prefix},
    });
  };

  const checkConfirm = (_, value) => {
    const promise = Promise;

    if (value && value !== form.getFieldValue('passwd')) {
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
      setvisible(!!value);
      return promise.reject(
        intl.formatMessage({
          id: 'userRegister.password.required',
        }),
      );
    }

    if (!visible) {
      setvisible(!!value);
    }

    setpopover(!popover);

    if (value.length < 8) {
      return promise.reject('请设置8位以上密码');
    }

    if (value && confirmDirty) {
      form.validateFields(['confirm']);
    }

    return promise.resolve();
  };

  const changePrefix = (value) => {
    setprefix(value);
  };

  const renderPasswordProgress = () => {
    const value = form.getFieldValue('passwd');
    const passwordStatus = getPasswordStatus();
    return value && value.length ? (
      <div className={styles[`progress-${passwordStatus}`]}>
        <Progress
          status={passwordProgressMap[passwordStatus]}
          className={styles.progress}
          strokeWidth={6}
          percent={value.length * 10 > 100 ? 100 : value.length * 10}
          showInfo={false}
        />
      </div>
    ) : null;
  };

  return (
    <div className={styles.main}>
      <h3>
        <FormattedMessage id="userRegister.register.register"/>
      </h3>
      <Form form={form} name="UserRegister" onFinish={onFinish}>
        <FormItem
          name="email"
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'userRegister.email.required',
              }),
            },
            {
              type: 'email',
              message: intl.formatMessage({
                id: 'userRegister.email.wrong-format',
              }),
            },
          ]}
        >
          <Input
            size="large"
            placeholder={intl.formatMessage({
              id: 'userRegister.email.placeholder',
            })}
          />
        </FormItem>
        <FormItem
          name="nickname"
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'userRegister.accountName.required',
              }),
            },
          ]}
        >
          <Input
            size="large"
            placeholder={intl.formatMessage({
              id: 'userRegister.accountName.placeholder',
            })}
          />
        </FormItem>
        <Popover
          getPopupContainer={(node) => {
            if (node && node.parentNode) {
              return node.parentNode;
            }

            return node;
          }}
          content={
            visible && (
              <div
                style={{
                  padding: '4px 0',
                }}
              >
                {passwordStatusMap[getPasswordStatus()]}
                {renderPasswordProgress()}
                <div
                  style={{
                    marginTop: 10,
                  }}
                >
                  <FormattedMessage id="userRegister.strength.msg"/>
                </div>
              </div>
            )
          }
          overlayStyle={{
            width: 240,
          }}
          placement="right"
          visible={visible}
        >
          <FormItem
            name="passwd"
            className={
              form.getFieldValue('passwd') &&
              form.getFieldValue('passwd').length > 0 &&
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
        </Popover>
        <FormItem
          name="confirm"
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
        <VerifyCode name="verifyCode" setVerify={setVerify} refresh={refresh} setCode={setCode}/>
        <FormItem>
          <Button
            size="large"
            loading={submitting}
            className={styles.submit}
            type="primary"
            htmlType="submit"
          >
            <FormattedMessage id="userRegister.register.register"/>
          </Button>
          <Link className={styles.login} to="/user/login">
            <FormattedMessage id="userRegister.register.sign-in"/>
          </Link>
        </FormItem>
      </Form>
    </div>
  );
};

export default connect(({userRegister, loading}) => ({
  userRegister,
  submitting: loading.effects['userRegister/submit'],
}))(Register);
