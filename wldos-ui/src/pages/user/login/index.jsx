import {
  LockTwoTone,
  MailTwoTone,
  MobileTwoTone,
  UserOutlined,
} from '@ant-design/icons';
import { Alert, message, Modal, Space, Spin, Tabs, Tooltip } from 'antd';
import React, {useEffect, useRef, useState} from 'react';
import ProForm, {
  ProFormCaptcha,
  ProFormCheckbox,
  ProFormText
} from '@ant-design/pro-form';
import { connect, FormattedMessage, useIntl, Link, history, useLocation } from 'umi';
import { InfoCircleOutlined } from '@ant-design/icons';
import styles from './index.less';
import {encryptByAES, redirect, resolveLoginEncryptPublicKey} from "@/utils/utils";
import VerifyCode from "@/pages/user/components/VerifyCode";
import {queryCurrent} from "@/services/user";
import {checkCaptcha, fetchEncryptKey, queryCaptchaMobile} from "@/services/login";
import { getAgreementListByType, getAgreementDetail } from './agreement';
import {autoLoginManager} from "@/utils/autoLogin";
import qq from "@/assets/qq-icon.svg";
import wb from "@/assets/weibo-icon.svg";
import wx from "@/assets/weixin-icon.svg";

const LoginMessage = ({content}) => {
  return (<Alert
    style={{
      marginBottom: 24,
    }}
    message={content}
    type="error"
    showIcon
  />
)};

const Login = (props) => {
  const {userLogin = {}, submitting} = props;
  const {status, type: loginType} = userLogin;
  const [type, setType] = useState('account');
  const [verify, setVerify] = useState('');
  const [refresh, setRefresh] = useState(1);
  const [code, setCode] = useState(undefined);
  const [mobileIsNull, setMobileStatus] = useState(true);
  const [uuid, setUuid] = useState('');
  const [encryptKey, setEncryptKey] = useState('');
  /** pending：尚未拉取；ready：已有公钥；unavailable：本次拉取失败（后台未就绪等） */
  const [encryptKeyStatus, setEncryptKeyStatus] = useState('pending');
  const encryptKeyFetchRef = useRef(null);
  const encryptKeyRef = useRef('');
  const [autoLoginPreference, setAutoLoginPreference] = useState(false);
  const [loginAgreements, setLoginAgreements] = useState([]);
  const [agreementModal, setAgreementModal] = useState({ visible: false, title: '', content: '', loading: false });
  const formRef = useRef();
  const intl = useIntl();
  const location = useLocation();
  const trialSandboxId = React.useMemo(() => {
    const raw = location?.query?.trialSandboxId;
    if (raw == null || raw === '') {
      return undefined;
    }
    const text = String(raw).trim();
    return text || undefined;
  }, [location?.query?.trialSandboxId]);

  const openAgreementModal = (e, agreement) => {
    if (e) e.preventDefault();
    if (!agreement?.id) return;
    setAgreementModal({ visible: true, title: agreement.title || '服务协议', content: '', loading: true });
    getAgreementDetail(agreement.id)
      .then((res) => {
        const vo = res?.data?.data ?? res?.data;
        const html = vo?.contentHtml || vo?.content || '';
        setAgreementModal((prev) => ({ ...prev, content: html, loading: false }));
      })
      .catch(() => {
        setAgreementModal((prev) => ({ ...prev, content: '<p>加载失败，请稍后重试。</p>', loading: false }));
      });
  };

  // 加载用户autoLogin偏好
  useEffect(() => {
    const savedPreference = autoLoginManager.getUserAutoLoginPreference();
    setAutoLoginPreference(savedPreference);
  }, []);

  useEffect(async () => { // 用于登录后再次访问登录页
    const query = await queryCurrent();
    const { userInfo } = query?.data?? {};
    if (userInfo?.id) {

      if (status === 'notActive') {
        message.warn('待激活用户，请先到注册邮箱激活邮件完成激活');
      }

      message.success('当前用户已登录！').then(() => redirect());
    }
  }, []);

  const loadEncryptKey = async (force = false) => {
    if (!force && encryptKeyRef.current) {
      return encryptKeyRef.current;
    }
    if (encryptKeyFetchRef.current) {
      return encryptKeyFetchRef.current;
    }
    setEncryptKeyStatus('pending');
    const task = (async () => {
      try {
        const res = await fetchEncryptKey();
        const key = resolveLoginEncryptPublicKey(res);
        if (key) {
          encryptKeyRef.current = key;
          setEncryptKey(key);
          setEncryptKeyStatus('ready');
          return key;
        }
      } catch (e) {
        // skipErrorHandler：后台未启动时为网络错误，不重复刷页
      }
      setEncryptKeyStatus('unavailable');
      return '';
    })();
    encryptKeyFetchRef.current = task;
    try {
      return await task;
    } finally {
      encryptKeyFetchRef.current = null;
    }
  };

  useEffect(() => {
    loadEncryptKey();
  }, []);

  useEffect(() => { // 登录失败时
    if (status === 'error' && userLogin.news) {
      message.error(userLogin.news).then(() => {
        setRefresh(refresh + 1);
        loadEncryptKey(true);
      });
    }
  }, [submitting]);

  useEffect(() => {
    getAgreementListByType('LOGIN').then((list) => {
      setLoginAgreements(Array.isArray(list) ? list : []);
    });
  }, []);

  const handleSubmit = async (values, currentEncryptKey) => {
    if (verify !== 'ok') {
      return;
    }
    let keyMaterial = currentEncryptKey || encryptKey;
    if (!keyMaterial) {
      keyMaterial = await loadEncryptKey(true);
    }
    if (!keyMaterial) {
      return;
    }
    if (loginAgreements.length > 0 && !values.agreementConsent) {
      message.error('请先确认已阅读服务协议');
      return;
    }

    // 保存用户autoLogin偏好
    if (values.autoLogin !== undefined) {
      autoLoginManager.setUserAutoLoginPreference(values.autoLogin);
      setAutoLoginPreference(values.autoLogin);
    }

    const {dispatch} = props;
    const {password, verifyCode, mobile, captcha, agreementConsent: _omit, ...rest} = values;
    const agreementIds = loginAgreements.map((a) => a.id);
    if (loginType === 'mobile') {
      const aesPassword = encryptByAES(captcha, keyMaterial);
      if (!aesPassword) {
        const retryKey = await loadEncryptKey(true);
        if (!retryKey) {
          return;
        }
        const retryCipher = encryptByAES(captcha, retryKey);
        if (!retryCipher) {
          return;
        }
        dispatch({
          type: 'login/mobile',
          payload: {...rest, password: retryCipher, type, mobile, captcha, agreementIds},
        });
        return;
      }
      dispatch({
        type: 'login/mobile',
        payload: {...rest, password: aesPassword, type, mobile, captcha, agreementIds},
      });
    } else {
      const aesPassword = encryptByAES(password, keyMaterial);
      if (!aesPassword) {
        const retryKey = await loadEncryptKey(true);
        if (!retryKey) {
          return;
        }
        const retryCipher = encryptByAES(password, retryKey);
        if (!retryCipher) {
          return;
        }
        dispatch({
          type: 'login/login',
          payload: {
            ...rest,
            password: retryCipher,
            type,
            verifyCode: `${verifyCode}${code}`,
            agreementIds,
            ...(trialSandboxId ? { trialSandboxId } : {}),
          },
        });
        return;
      }
      dispatch({
        type: 'login/login',
        payload: {
          ...rest,
          password: aesPassword,
          type,
          verifyCode: `${verifyCode}${code}`,
          agreementIds,
          ...(trialSandboxId ? { trialSandboxId } : {}),
        },
      });
    }
  };

  const validateCaptcha = async (rule, value) => {
    if (value === undefined || value.length < 4) {
      return Promise.reject();
    }
    try {
      const res = await checkCaptcha({captcha: value, uuid});
      // 新的响应格式：response.data已经是业务数据（Map对象，包含status字段）
      if (res && res.data && res.data.status === 'ok') {
        setCode(uuid);
        setVerify('ok');
        return Promise.resolve();
      } else {
        setVerify('error');
        return Promise.reject(new Error('验证码错误或过期，请重新输入或获取!'));
      }
    } catch (error) {
      setVerify('error');
      return Promise.reject(new Error('验证码校验失败，请重试!'));
    }
  };

  return (
    <div className={styles.main}>
        <ProForm
        formRef={formRef}
        initialValues={{
          autoLogin: autoLoginPreference,
        }}
        submitter={{
          render: (_, dom) => dom.pop(),
          submitButtonProps: {
            loading: submitting || status === 'ok',
            disabled: status === 'ok',
            size: 'large',
            style: {
              width: '100%',
            },
          },
        }}
        onFinish={async (values) => {
          await handleSubmit(values, encryptKey);
          return Promise.resolve();
        }}
        isKeyPressSubmit={true}
      >
        <Tabs activeKey={type} onChange={setType}>
          <Tabs.TabPane
            key="account"
            tab={intl.formatMessage({
              id: 'pages.login.accountLogin.tab',
              defaultMessage: '账户密码登录',
            })}
          />
        </Tabs>

        {status === 'error' && loginType === 'account' && !submitting && (
          <LoginMessage
            content={intl.formatMessage({
              id: 'pages.login.accountLogin.errorMessage',
              defaultMessage: '账户或密码错误,请确定账号存在',
            })}
          />
        )}
        {type === 'account' && (
          <>
            <ProFormText
              name="username"
              fieldProps={{
                size: 'large',
                prefix: <UserOutlined className={styles.prefixIcon}/>,
              }}
              placeholder={intl.formatMessage({
                id: 'pages.login.username.placeholder',
                defaultMessage: '用户名',
              })}
              rules={[
                {
                  required: true,
                  message: (
                    <FormattedMessage
                      id="pages.login.username.required"
                      defaultMessage="请输入用户名!"
                    />
                  ),
                },
              ]}
            />
            <ProFormText.Password
              name="password"
              fieldProps={{
                size: 'large',
                prefix: <LockTwoTone className={styles.prefixIcon}/>,
              }}
              placeholder={intl.formatMessage({
                id: 'pages.login.password.placeholder',
                defaultMessage: '',
              })}
              rules={[
                {
                  required: true,
                  message: (
                    <FormattedMessage
                      id="pages.login.password.required"
                      defaultMessage="请输入密码！"
                    />
                  ),
                },
              ]}
            />
            <VerifyCode name="verifyCode" setVerify={setVerify} refresh={refresh} setCode={setCode}/>
          </>
        )}

        {status === 'error' && loginType === 'mobile' && !submitting && (
          <LoginMessage content="验证码错误"/>
        )}
        {type === 'mobile' && (
          <>
            <ProFormText
              fieldProps={{
                size: 'large',
                prefix: <MobileTwoTone className={styles.prefixIcon}/>,
              }}
              name="mobile"
              placeholder={intl.formatMessage({
                id: 'pages.login.phoneNumber.placeholder',
                defaultMessage: '手机号',
              })}
              onChange={(e) => setMobileStatus(!e.target.value)}
              rules={[
                {
                  required: true,
                  message: (
                    <FormattedMessage
                      id="pages.login.phoneNumber.required"
                      defaultMessage="请输入手机号！"
                    />
                  ),
                },
                {
                  pattern: /^1\d{10}$/,
                  message: (
                    <FormattedMessage
                      id="pages.login.phoneNumber.invalid"
                      defaultMessage="手机号格式错误！"
                    />
                  ),
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
                  if (mobileIsNull) {
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
              phoneName="mobile"
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
              onGetCaptcha={async (mobile) => {
                if (mobileIsNull) {
                  setVerify('error');
                  message.error('请输入手机号！');
                  return;
                }

                const res = await queryCaptchaMobile({mobile}); // 查询验证码，并在后台发送给手机

                const {uuid : cuid} = res?.data?? {};
                if (cuid) {
                  setUuid(cuid);
                }

                message.success('获取验证码成功！');
              }}
            />
          </>
        )}
        {loginAgreements.length > 0 && (
          <ProFormCheckbox
            name="agreementConsent"
            rules={[{ required: true, message: '请先确认已阅读服务协议' }]}
          >
            <span>
              我已阅读并同意
              {loginAgreements.map((a) => (
                <a key={a.id} href="#" onClick={(e) => openAgreementModal(e, a)} style={{ margin: '0 2px' }}>
                  《{a.title}》
                </a>
              ))}
            </span>
          </ProFormCheckbox>
        )}
        <div
          style={{
            marginBottom: 24,
          }}
        >
          <span style={{ display: 'inline-flex', alignItems: 'center' }}>
            <ProFormCheckbox noStyle name="autoLogin">
              <span>自动登录</span>
            </ProFormCheckbox>
            <Tooltip
              placement="top"
              title="仅在本机保存登录状态，最多15天有效期；公共设备请勿勾选。"
            >
              <InfoCircleOutlined style={{ color: 'rgba(0,0,0,0.45)', marginLeft: -4, verticalAlign: 'middle' }} />
            </Tooltip>
          </span>
          <a href={"/user/forget"}
            style={{
              float: 'right',
            }}
          >
            <FormattedMessage id="pages.login.forgotPassword" defaultMessage="忘记密码"/>
          </a>
          <a href={"/user/register"}
             style={{
               float: 'right',
               marginRight: 5,
             }}
          >
            <FormattedMessage id="pages.login.registerAccount" defaultMessage="注册账号"/>
          </a>
        </div>
      </ProForm>
      <Space className={styles.other} direction="vertical" style={{ width: '100%' }}>
        <span>
          <FormattedMessage id="pages.login.loginWith" defaultMessage="快捷登录"/>
          <a
            onClick={() => {
              if (loginAgreements.length > 0 && !formRef.current?.getFieldValue('agreementConsent')) {
                message.error('请先勾选并同意服务协议');
                return;
              }
              history.push('/user/auth/qq');
            }}
            style={{ cursor: 'pointer' }}
          >
            <img src={qq} className={styles.icon} title={'qq互联直接登录'} alt={'qq'}/>
          </a>
          <a
            onClick={() => {
              if (loginAgreements.length > 0 && !formRef.current?.getFieldValue('agreementConsent')) {
                message.error('请先勾选并同意服务协议');
                return;
              }
              history.push('/user/auth/weibo');
            }}
            style={{ cursor: 'pointer' }}
          >
            <img src={wb} className={styles.icon} title={'微博互联直接登录'} alt={'weibo'}/>
          </a>
          <a
            onClick={() => {
              if (loginAgreements.length > 0 && !formRef.current?.getFieldValue('agreementConsent')) {
                message.error('请先勾选并同意服务协议');
                return;
              }
              history.push('/user/auth/wechat');
            }}
            style={{ cursor: 'pointer' }}
          >
            <img src={wx} className={styles.icon} title={'微信扫码登录'} alt={'wechat'}/>
          </a>
        </span>
      </Space>

      <Modal
        title={agreementModal.title}
        open={agreementModal.visible}
        onCancel={() => setAgreementModal((prev) => ({ ...prev, visible: false }))}
        footer={[
          <button
            key="close"
            type="button"
            className="ant-btn ant-btn-primary"
            onClick={() => setAgreementModal((prev) => ({ ...prev, visible: false }))}
          >
            关闭
          </button>,
        ]}
        width={640}
        destroyOnClose
      >
        {agreementModal.loading ? (
          <div style={{ textAlign: 'center', padding: 40 }}>
            <Spin />
          </div>
        ) : (
          <div
            style={{ maxHeight: '70vh', overflow: 'auto', lineHeight: 1.6 }}
            dangerouslySetInnerHTML={{ __html: agreementModal.content || '<p>暂无内容</p>' }}
          />
        )}
      </Modal>
    </div>
  );
};

export default connect(({login, loading}) => ({
  userLogin: login,
  submitting: loading.effects['login/login'],
}))(Login);
