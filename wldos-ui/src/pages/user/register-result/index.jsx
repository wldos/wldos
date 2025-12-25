import {Button, Result} from 'antd';
import {FormattedMessage, Link, useIntl} from 'umi';
import React from 'react';
import styles from './style.less';
import {mailist} from "@/pages/user/register-result/maillist";

const actions = (location) => {
    const account = location.state?.account?? '306991142@qq.com';
    const mail = account.indexOf('@') > -1 ? account.substring(account.indexOf('@') + 1) : '88.com';
    const target = mailist[mail] || 'http://www.wldos.com';
    return (
        <div className={styles.actions}>
            <a href={target} target="_blank" rel="noreferrer">
                <Button size="large" type="primary">
                    <FormattedMessage
                        id="userRegister-result.register-result.view-mailbox"/>
                </Button>
            </a>
            <Link to="/">
                <Button size="large">
                    <FormattedMessage
                        id="userRegister-result.register-result.back-home"/>
                </Button>
            </Link>
        </div>
    );
};

const RegisterResult = ({location}) => {
    const intl = useIntl();
    return (
        <Result
            className={styles.registerResult}
            status="success"
            title={
                <div className={styles.title}>
                    <FormattedMessage
                        id="userRegister-result.register-result.msg"
                        values={{
                            email: location.state?.account?? 'support@xiupu.net',
                        }}
                    />
                </div>
            }
            subTitle={intl.formatMessage({
                id: 'userRegister-result.register-result.activation-email',
            })}
            extra={actions(location)}
        />
    );
};

export default RegisterResult;
