import {Button, Form, message, Progress} from 'antd';
import React, {useEffect, useState} from 'react';
import {FormattedMessage, history, Link, useIntl} from 'umi';
import styles from './style.less';
import {ProFormCaptcha, ProFormText} from "@ant-design/pro-form";
import {MailTwoTone} from "@ant-design/icons";
import {
  checkCaptcha,
  checkEmail,
  queryCaptchaEmail,
  resetPassword
} from "@/services/login";
import PasswdReset from "@/pages/user/forget/components/Passwd";

const FormItem = Form.Item;

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

const ResetPasswd = ({submitting, userRegister}) => {
  const [visible, setvisible] = useState(false);
  const [popover, setpopover] = useState(false);
  const intl = useIntl();
  const [verify, setVerify] = useState('');
  const [refresh, setRefresh] = useState(1);
  const [emailIsNull, setEmailStatus] = useState(true);
  const [uuid, setUuid] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [formValues, setFormValues] = useState({});
  const [reset, setReset] = useState(false);

  const confirmDirty = false;
  let interval;
  const [form] = Form.useForm();

  useEffect(() => {
    if (!reset) {
      return;
    }

    history.push({
      pathname: '/user/login',
    });
  }, [reset]);

  useEffect(
    () => () => {
      clearInterval(interval);
    }, []);

  const getPasswordStatus = () => {
    const value = form.getFieldValue('passwd');

    if (value && value.length > 9) {
      return 'ok';
    }

    if (value && value.length > 5) {
      return 'pass';
    }

    return 'poor';
  };

  const onFinish = (values) => {
    if (verify !== 'ok') {
      return;
    }

    setFormValues({...values, uuid})
   // 弹出新密码设置对话框
    setModalVisible(true);
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

    if (value.length < 6) {
      return promise.reject('');
    }

    if (value && confirmDirty) {
      form.validateFields(['confirm']);
    }

    return promise.resolve();
  };

  const validateCaptcha = async (rule, value) => {
    if (value === undefined || value.length < 4) {
      return Promise.reject();
    }
    const res = await checkCaptcha({captcha: value, uuid});
    if (res === undefined || res.data === undefined || res.data.status === undefined) {
      return Promise.reject(new Error('校验超时请重试!'));
    }
    if (res.data.status === 'error') {
      setVerify('error');
      return Promise.reject(new Error('验证码错误或过期，请重新输入或获取!'));
    }

    if (res.data.status === 'ok') {
      setVerify('ok');
    }
    return Promise.resolve();
  };

  const validateEmail = async (rule, value) => {
    if (value === undefined || value.length < 4) {
      return Promise.reject();
    }
    const res = await checkEmail({email: value});
    if (res === undefined || res.data === undefined || res.data.status === undefined) {
      return Promise.reject(new Error('邮箱校验超时请重试!'));
    }
    if (res.data.status === 'error') {
      return Promise.reject(new Error('邮箱不存在!'));
    }

    return Promise.resolve();
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

  const resetPasswd = async (values) => {

    try {
      const res = await resetPassword(values);

      if (res && res.data) {
        const {status, news} = res.data;
        if (status === 'error'){
          message.error(news);
          return false;
        }
      }
      message.info("修改成功，请重新登陆！").then(() => setReset(true));
      return true;
    } catch (error) {
      message.error('修改失败请重试！');
      return false;
    }
  };

  return (
    <div className={styles.main}>
      <h3>
        <FormattedMessage id="userForget.forget.passwd"/>
      </h3>
      <Form form={form} name="UserForget" onFinish={onFinish}>
        <>
          <ProFormText
            fieldProps={{
              size: 'large',
              prefix: <MailTwoTone className={styles.prefixIcon}/>,
            }}
            name="email"
            placeholder={intl.formatMessage({
              id: 'userForget.email.placeholder',
              defaultMessage: '邮箱',
            })}
            onChange={(e) => setEmailStatus(!e.target.value)}
            rules={[
              {
                required: true,
                message: intl.formatMessage({
                  id: 'userForget.email.required',
                }),
              },
              {
                type: 'email',
                message: intl.formatMessage({
                  id: 'userForget.email.wrong-format',
                }),
              },
              {
                validator: async (rule, value) => validateEmail(rule, value)
              },
            ]}
          />
          <ProFormCaptcha
            fieldProps={{
              size: 'large',
              prefix: <MailTwoTone className={styles.prefixIcon}/>,
            }}
            captchaProps={{
              size: 'large',
            }}
            placeholder={intl.formatMessage({
              id: 'pages.login.captcha.placeholder',
              defaultMessage: '请输入验证码',
            })}
            captchaTextRender={(timing, count) => {
              if (timing) {
                if (emailIsNull) {
                  return intl.formatMessage({
                    id: 'pages.login.phoneLogin.getVerificationCode',
                    defaultMessage: '获取验证码',
                  });
                }
                return `${count} ${intl.formatMessage({
                  id: 'pages.getCaptchaSecondText',
                  defaultMessage: '获取验证码',
                })}`;
              }

              return intl.formatMessage({
                id: 'pages.login.phoneLogin.getVerificationCode',
                defaultMessage: '获取验证码',
              });
            }}
            phoneName="email"
            name="captcha"
            rules={[
              {
                required: true,
                message: (
                  <FormattedMessage
                    id="pages.login.captcha.required"
                    defaultMessage="请输入验证码！"
                  />
                ),
              },
              {
                validator: async (rule, value) => validateCaptcha(rule, value)
              },
            ]}
            /* eslint-disable-next-line no-unused-vars */
            onGetCaptcha={async (email) => {

              if (emailIsNull) {
                setVerify('error');
                message.error('请输入邮箱！');
                return;
              }
              const res = await queryCaptchaEmail({email}); // 查询验证码，并在后台发送给手机

              const {uuid : cuid} = res?.data?? {};
              if (cuid) {
                setUuid(cuid);
              }

              message.success('获取验证码成功！');
            }}
          />
        </>
        <FormItem>
          <Button
            size="large"
            loading={submitting}
            className={styles.submit}
            type="primary"
            htmlType="submit"
          >
            <FormattedMessage id="userForget.form.submit"/>
          </Button>
          <Link className={styles.login} to="/user/login">
            <FormattedMessage id="userForget.forget.sign-in"/>
          </Link>
        </FormItem>
      </Form>
      {verify === 'ok' ? (
        <PasswdReset
          onSubmit={async (value) => {
            const success = await resetPasswd(value);

            if (success) {
              setModalVisible(false);
            }
          }}
          onCancel={() => {
            setModalVisible(false);
          }}
          modalVisible={modalVisible}
          values={formValues}
        />
      ) : null}
    </div>
  );
};

export default ResetPasswd;
