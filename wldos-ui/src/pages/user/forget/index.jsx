import {Button, Form, Input, message, Progress} from 'antd';
import React, {useEffect, useState} from 'react';
import {FormattedMessage, history, Link, useIntl} from 'umi';
import styles from './style.less';
import {ProFormText} from "@ant-design/pro-form";
import {MailTwoTone, UserOutlined} from "@ant-design/icons";
import PasswdReset from "@/pages/user/forget/components/Passwd";
import ClickCaptchaModal from '@/pages/user/forget/components/ClickCaptchaModal';
import { parseLoginActionResult } from '@/utils/loginResult';
import {
  checkCaptchaForForget,
  fetchForgetResetProfile,
  forgetEmailAdmissionForForget,
  queryCaptchaEmailForForget,
  resetPasswordForForget,
} from '@/pages/user/forget/service';

const FORGET_EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

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
  const [forgetProof, setForgetProof] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [formValues, setFormValues] = useState({});
  const [reset, setReset] = useState(false);
  const [accountMatchesEmail, setAccountMatchesEmail] = useState(false);
  const [clickCaptchaOpen, setClickCaptchaOpen] = useState(false);
  const [pendingEmailForCode, setPendingEmailForCode] = useState('');
  const [emailAdmitted, setEmailAdmitted] = useState(false);
  const [admitToken, setAdmitToken] = useState('');
  const [topError, setTopError] = useState('');
  const [secondaryVerify, setSecondaryVerify] = useState(false);
  const [emailCodeCountdown, setEmailCodeCountdown] = useState(0);
  const [emailCodeSending, setEmailCodeSending] = useState(false);

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

  useEffect(() => {
    fetchForgetResetProfile().then((res) => {
      if (res?.code === 200 && res?.data) {
        const enabled = res.data.secondaryVerify === true
          || res.data.secondaryVerify === 'true';
        setSecondaryVerify(enabled);
      }
    });
  }, []);

  useEffect(
    () => () => {
      clearInterval(interval);
    }, []);

  useEffect(() => {
    if (emailCodeCountdown <= 0) {
      return undefined;
    }
    const timer = window.setTimeout(() => {
      setEmailCodeCountdown((prev) => prev - 1);
    }, 1000);
    return () => window.clearTimeout(timer);
  }, [emailCodeCountdown]);

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

  const onFinish = async (values) => {
    if (!uuid || !forgetProof) {
      message.error('请先获取邮箱验证码');
      return;
    }
    try {
      const res = await checkCaptchaForForget({
        captcha: values.captcha,
        uuid,
        forgetProof,
      });
      if (!(res?.code === 200 && res?.data?.status === 'ok')) {
        message.error('验证码错误或已过期，请重新获取');
        setVerify('error');
        return;
      }
      setVerify('ok');
      const email = (values.email || '').trim();
      setFormValues({
        ...values,
        email,
        loginName: accountMatchesEmail ? email : (values.loginName || '').trim(),
        uuid,
        forgetProof,
        accountMatchesEmail,
      });
      setModalVisible(true);
    } catch (e) {
      message.error('验证码校验失败，请重试');
      setVerify('error');
    }
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
    try {
      if (!forgetProof) {
        return Promise.reject(new Error('请先获取邮箱验证码'));
      }
      const res = await checkCaptchaForForget({ captcha: value, uuid, forgetProof });
      if (res?.code === 200 && res?.data?.status === 'ok') {
        setVerify('ok');
        return Promise.resolve();
      }
      setVerify('error');
      return Promise.reject(new Error(res?.message || '验证码错误或过期，请重新输入或获取!'));
    } catch (error) {
      setVerify('error');
      return Promise.reject(new Error('验证码校验失败，请重试!'));
    }
  };

  const validateEmailFormat = async (_, value) => {
    const trimmed = (value || '').trim();
    if (!trimmed) {
      return Promise.reject();
    }
    if (!FORGET_EMAIL_PATTERN.test(trimmed)) {
      return Promise.reject(new Error(
        intl.formatMessage({ id: 'userForget.email.wrong-format' }),
      ));
    }
    return Promise.resolve();
  };

  const applyAccountMatchesFromCheck = (data) => {
    const matches = data?.accountMatchesEmail === true
      || data?.accountMatchesEmail === 'true';
    setAccountMatchesEmail(matches);
    return matches;
  };

  const abortGetCaptcha = (msg) => {
    message.error(msg);
    throw new Error(msg);
  };

  const sendEmailCaptchaAfterAdmit = async (addr, token) => {
    if (!token) {
      message.error(intl.formatMessage({ id: 'userForget.admission.required' }));
      return false;
    }
    setEmailCodeSending(true);
    setUuid('');
    setForgetProof('');
    try {
      const params = { email: addr, admitToken: token };
      if (!accountMatchesEmail) {
        params.loginName = (form.getFieldValue('loginName') || '').trim();
      }
      const res = await queryCaptchaEmailForForget(params);
      if (res?.code !== 200) {
        message.error(res?.message || intl.formatMessage({ id: 'userForget.email-code.send-failed' }));
        return false;
      }
      const payload = res?.data || {};
      const cuid = payload.uuid;
      if (!cuid || payload.sent === 'false') {
        message.error(payload.hint || intl.formatMessage({ id: 'userForget.email-code.send-failed' }));
        return false;
      }
      applyAccountMatchesFromCheck(payload);
      setUuid(cuid);
      setForgetProof(payload.forgetProof || '');
      setVerify('');
      setEmailCodeCountdown(60);
      message.success(intl.formatMessage({ id: 'userForget.email-code.sent' }));
      return true;
    } finally {
      setEmailCodeSending(false);
    }
  };

  const handleGetEmailCode = async () => {
    if (emailCodeCountdown > 0 || emailCodeSending) {
      return;
    }
    if (!accountMatchesEmail) {
      try {
        await form.validateFields(['loginName']);
      } catch (e) {
        return;
      }
    }
    const addr = (form.getFieldValue('email') || '').trim();
    await sendEmailCaptchaAfterAdmit(addr, admitToken);
  };

  const rejectAdmission = (text) => {
    setEmailAdmitted(false);
    setAdmitToken('');
    setAccountMatchesEmail(false);
    setTopError(text);
  };

  const completeEmailAdmission = async (addr, graphPass) => {
    const payload = { email: addr };
    if (graphPass) {
      payload.graphPass = graphPass;
    }
    setTopError('');
    let admitRes;
    try {
      admitRes = await forgetEmailAdmissionForForget(payload);
    } catch (e) {
      rejectAdmission(intl.formatMessage({ id: 'userForget.admission.failed' }));
      return false;
    }
    const data = admitRes?.data;
    if (!(admitRes?.code === 200 && data?.status === 'ok' && data?.admitToken)) {
      const hint = data?.hint;
      const notExists = !hint || hint === '邮箱不存在';
      rejectAdmission(notExists ? intl.formatMessage({ id: 'userForget.user.not-exists' }) : hint);
      return false;
    }
    applyAccountMatchesFromCheck(data);
    setAdmitToken(data.admitToken);
    setEmailAdmitted(true);
    setEmailCodeCountdown(0);
    return true;
  };

  const startForgetFlow = async () => {
    const addr = (form.getFieldValue('email') || '').trim();
    if (!addr) {
      message.error('请输入邮箱');
      return;
    }
    if (!FORGET_EMAIL_PATTERN.test(addr)) {
      message.error(intl.formatMessage({ id: 'userForget.email.wrong-format' }));
      return;
    }
    if (secondaryVerify) {
      setPendingEmailForCode(addr);
      setClickCaptchaOpen(true);
      return;
    }
    await completeEmailAdmission(addr, null);
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
      const res = await resetPasswordForForget(values);
      const outcome = parseLoginActionResult(res);
      if (!outcome.ok) {
        if (outcome.news) {
          message.error(outcome.news);
        }
        return false;
      }
      message.info('修改成功，请重新登录！').then(() => setReset(true));
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
        {topError ? (
          <div className={styles.error} style={{ marginBottom: 16 }}>{topError}</div>
        ) : null}
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
            disabled={emailAdmitted}
            onChange={(e) => {
              setEmailStatus(!e.target.value);
              setAccountMatchesEmail(false);
              setUuid('');
              setForgetProof('');
              setEmailAdmitted(false);
              setAdmitToken('');
              setTopError('');
              form.setFields([{ name: 'email', errors: [] }]);
            }}
            rules={[
              {
                required: true,
                message: intl.formatMessage({
                  id: 'userForget.email.required',
                }),
              },
              {
                validator: validateEmailFormat,
              },
            ]}
          />
          {!emailAdmitted ? (
            <FormItem>
              <Button
                size="large"
                className={styles.submit}
                type="primary"
                onClick={startForgetFlow}
              >
                <FormattedMessage id="userForget.reset-password.btn" defaultMessage="重置密码" />
              </Button>
            </FormItem>
          ) : null}
          {emailAdmitted && admitToken && !accountMatchesEmail ? (
            <FormItem
              name="loginName"
              rules={[{ required: true, whitespace: true, message: intl.formatMessage({ id: 'userForget.loginName.required' }) }]}
            >
              <Input
                size="large"
                prefix={<UserOutlined className={styles.prefixIcon}/>}
                placeholder={intl.formatMessage({ id: 'userForget.loginName.placeholder' })}
              />
            </FormItem>
          ) : null}
          {emailAdmitted && admitToken ? (
            <FormItem
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
                  validator: async (rule, value) => validateCaptcha(rule, value),
                },
              ]}
            >
              <Input
                size="large"
                prefix={<MailTwoTone className={styles.prefixIcon}/>}
                placeholder={intl.formatMessage({
                  id: 'pages.login.captcha.placeholder',
                  defaultMessage: '请输入验证码',
                })}
                addonAfter={(
                  <Button
                    type="link"
                    size="small"
                    disabled={emailCodeCountdown > 0 || emailCodeSending}
                    loading={emailCodeSending}
                    onClick={handleGetEmailCode}
                  >
                    {emailCodeCountdown > 0
                      ? intl.formatMessage(
                        { id: 'userForget.email-code.countdown' },
                        { count: emailCodeCountdown },
                      )
                      : intl.formatMessage({ id: 'userForget.form.get-captcha' })}
                  </Button>
                )}
              />
            </FormItem>
          ) : null}
        </>
        {emailAdmitted && admitToken && (
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
          </FormItem>
        )}
        <FormItem>
          <Link className={styles.login} to="/user/login">
            <FormattedMessage id="userForget.forget.sign-in"/>
          </Link>
        </FormItem>
      </Form>
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
      {secondaryVerify && (
      <ClickCaptchaModal
        open={clickCaptchaOpen}
        onCancel={() => {
          setClickCaptchaOpen(false);
          setPendingEmailForCode('');
        }}
        onSuccess={async (graphPass) => {
          const addr = pendingEmailForCode;
          setPendingEmailForCode('');
          setClickCaptchaOpen(false);
          if (!addr) {
            return;
          }
          await completeEmailAdmission(addr, graphPass);
        }}
      />
      )}
    </div>
  );
};

export default ResetPasswd;
