import {connect, FormattedMessage} from 'umi';
import React, { Component } from 'react';
import {List, message} from 'antd';
import Passwd from "@/pages/account/settings/components/Passwd";
import {encryptByAES} from "@/utils/utils";
import {
  updateBakEmail,
  updateMobile,
  updatePasswd, updateSecQuest
} from "@/pages/account/settings/service";
import Mobile from "@/pages/account/settings/components/Mobile";
import Email from "@/pages/account/settings/components/Email";
import SecQuest from "@/pages/account/settings/components/SecQuest";
import {fetchEncryptKey} from "@/services/login";

const passwordStrength = {
  strong: (
    <span className="strong">
      <FormattedMessage id="account.security.strong" defaultMessage="Strong" />
    </span>
  ),
  medium: (
    <span className="medium">
      <FormattedMessage id="account.security.medium" defaultMessage="Medium" />
    </span>
  ),
  weak: (
    <span className="weak">
      <FormattedMessage id="account.security.weak" defaultMessage="Weak" />
    </span>
  ),
};

const changePasswd = async (values) => {
  const {username, oldPasswd, id, password, confirm, encryptKey} = values;
  const aesPassword = encryptByAES(oldPasswd, encryptKey);

  try {
    const res = await updatePasswd({
      oldPasswd: aesPassword, id, password, confirm
    });

    if (res && res.data) {
      const {status, news} = res.data;
      if (status === 'error'){
        message.error(news);
        return false;
      }
    }
    message.info("修改成功，请重新登陆！");
    return true;
  } catch (error) {
    message.error('修改失败请重试！');
    return false;
  }
};

const changeMobile = async (values) => {
  const {oldMobile, id, mobile} = values;

  try {
    const res = await updateMobile({
      oldMobile, id, mobile
    });

    if (res && res.data) {
      const {status, news} = res.data;
      if (status === 'error'){
        message.error(news);
        return false;
      }
    }
    message.info("修改成功！");
    return true;
  } catch (error) {
    message.error('修改失败请重试！');
    return false;
  }
};

const changeSecQuest = async (values) => {
  const {oldSecQuest, id, secQuest} = values;

  try {
    const res = await updateSecQuest({
      oldSecQuest, id, secQuest
    });

    if (res && res.data) {
      const {status, news} = res.data;
      if (status === 'error'){
        message.error(news);
        return false;
      }
    }
    message.info("修改成功！");
    return true;
  } catch (error) {
    message.error('修改失败请重试！');
    return false;
  }
};

const changeEmail = async (values) => {
  const {oldBakEmail, id, bakEmail} = values;

  try {
    const res = await updateBakEmail({
      oldBakEmail, id, bakEmail
    });

    if (res && res.data) {
      const {status, news} = res.data;
      if (status === 'error'){
        message.error(news);
        return false;
      }
    }
    message.info("修改成功！");
    return true;
  } catch (error) {
    message.error('修改失败请重试！');
    return false;
  }
};

class SecurityView extends Component {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      formValues: {},
      mobileVisible: false,
      questVisible: false,
      emailVisible: false
    };
  }

  updateCurrent = () => {
    const {dispatch} = this.props;
    dispatch({
      type: 'account/fetchCurrent',
    });
  };

  setVisible = (visible, u) => {
    this.setState({
      ...visible,
      formValues: u,
    });
  };

  getData = (u) => [
    {
      title: <FormattedMessage id="account.security.password" />,
      description: (
        <>
          <FormattedMessage id="account.security.password-description" />
          {passwordStrength[u.sec?.passStatus?? 'strong']}
        </>
      ),
      actions: [
        <a key="Modify" onClick={() => this.setVisible({modalVisible: true}, u)} >
          <FormattedMessage id="account.security.modify" defaultMessage="Modify" />
        </a>,
      ],
    },
    {
      title: <FormattedMessage id="account.security.phone" />,
      description: <><FormattedMessage id="account.security.phone-description" />{u.sec?.mobile?? '未设置'}</>,
      actions: [
        <a key="Modify" onClick={() => this.setVisible({mobileVisible: true}, u)}>
          <FormattedMessage id="account.security.modify" defaultMessage="Modify" />
        </a>,
      ],
    },
    {
      title: <FormattedMessage id="account.security.question" />,
      description: <><FormattedMessage id="account.security.question-description" />{u.sec?.secQuest?? '未设置'}</>,
      actions: [
        <a key="Set" onClick={() => this.setVisible({questVisible: true}, u)}>
          <FormattedMessage id="account.security.set" defaultMessage="Set" />
        </a>,
      ],
    },
    {
      title: <FormattedMessage id="account.security.email" />,
      description: <><FormattedMessage id="account.security.email-description" />{u.sec?.bakEmail?? '未设置'}</>,
      actions: [
        <a key="Modify" onClick={() => this.setVisible({emailVisible: true}, u)}>
          <FormattedMessage id="account.security.modify" defaultMessage="Modify" />
        </a>,
      ],
    },
    {
      title:  <FormattedMessage id="account.security.mfa" />,
      description: <><FormattedMessage id="account.security.mfa-description" />{u.sec?.mfa?? '未设置'}</>,
      actions: [
        <a key="bind" onClick={() => alert('暂未实现')}>
          <FormattedMessage id="account.security.bind" defaultMessage="Bind" />
        </a>,
      ],
    },
  ];

  render() {
    const {currentUser} = this.props;
    if (!currentUser?.id) {
      return '';
    }

    const data = this.getData(currentUser);

    const {modalVisible, formValues, mobileVisible, questVisible, emailVisible} = this.state;

    return (
      <>
        <List
          itemLayout="horizontal"
          dataSource={data}
          renderItem={(item) => (
            <List.Item actions={item.actions}>
              <List.Item.Meta title={item.title} description={item.description} />
            </List.Item>
          )}
        />
        {formValues && Object.keys(formValues).length ? (
          <Passwd
            onSubmit={async (value) => {
              const success = await changePasswd(value);

              if (success) {
                this.setVisible({modalVisible: false}, {});

                this.updateCurrent();
              }
            }}
            onCancel={() => {
              this.setVisible({modalVisible: false}, {});
            }}
            modalVisible={modalVisible}
            values={formValues}
          />
        ) : null}
        {formValues && Object.keys(formValues).length ? (
          <Mobile
            onSubmit={async (value) => {
              const success = await changeMobile(value);

              if (success) {
                this.setVisible({mobileVisible: true}, {});

                this.updateCurrent();
              }
            }}
            onCancel={() => {
              this.setVisible({mobileVisible: false}, {});
            }}
            modalVisible={mobileVisible}
            values={formValues}
          />
        ) : null}
        {formValues && Object.keys(formValues).length ? (
          <SecQuest
            onSubmit={async (value) => {
              const success = await changeSecQuest(value);

              if (success) {
                this.setVisible({questVisible: false}, {});

                this.updateCurrent();
              }
            }}
            onCancel={() => {
              this.setVisible({questVisible: false}, {});
            }}
            modalVisible={questVisible}
            values={formValues}
          />
        ) : null}
        {formValues && Object.keys(formValues).length ? (
          <Email
            onSubmit={async (value) => {
              const success = await changeEmail(value);

              if (success) {
                this.setVisible({emailVisible: false}, {});

                this.updateCurrent();
              }
            }}
            onCancel={() => {
              this.setVisible({emailVisible: false}, {});
            }}
            modalVisible={emailVisible}
            values={formValues}
          />
        ) : null}
      </>
    );
  }
}

export default connect(({account}) => ({
  currentUser: account.currentUser,
}))(SecurityView);
