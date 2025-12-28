import React, {useEffect, useState} from "react";
import {ProFormText} from "@ant-design/pro-form";
import {SafetyCertificateOutlined} from "@ant-design/icons";
import {FormattedMessage, useIntl} from "umi";
import styles from "./index.less";
import {checkCaptcha, queryCaptcha} from "@/services/login";

const iCode = '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';

/**
 * 验证码组件。
 *
 * @author 树悉猿
 * @date 2021/9/10
 * @version 1.0
 */
const VerifyCode = (props) => {
  const {style, setVerify, setCode, refresh, ...restProps} = props;

  const [captcha, setCaptcha] = useState(iCode);
  const [uuid, setUuid] = useState('');

  const intl = useIntl();

  const updateCaptcha = async () => {
    const res = await queryCaptcha();

    const {authcode, uuid : cuid} = res?.data?? {};
    if (authcode && cuid) {
      setCaptcha(authcode);
      setUuid(cuid);
    }
  };

  useEffect(async () => {
      await updateCaptcha();
  }, [refresh]);

  const validateCode = async (rule, value) => {
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
        await updateCaptcha();
        return Promise.reject(new Error('验证码错误!'));
      }
    } catch (error) {
      setVerify('error');
      await updateCaptcha();
      return Promise.reject(new Error('验证码校验失败，请重试!'));
    }
  };

  return (
    <div className={style?? styles.captcha}>
      <div className={styles.input}>
        <ProFormText
          {...restProps}
          fieldProps={{
            size: 'large',
            prefix: <SafetyCertificateOutlined className={styles.prefixIcon}/>,
          }}
          placeholder={intl.formatMessage({
            id: 'pages.login.verifycode.placeholder',
            defaultMessage: '',
          })}
          rules={[
            {
              required: true,
              message: (
                <FormattedMessage
                  id="pages.login.verifycode.required"
                  defaultMessage="请输入验证码!"
                />
              ),
            },
            {
              validator: async (rule, value) => validateCode(rule, value)
            },
          ]}
        />
      </div>
      <div onClick={ async () => updateCaptcha() } className={styles.image}>
        <img src={`data:image/jpeg;base64,${captcha}`} alt="验证码"/>
      </div>
    </div>
  );
};

export default VerifyCode;