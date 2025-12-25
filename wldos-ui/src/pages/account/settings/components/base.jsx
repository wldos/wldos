import {UploadOutlined} from '@ant-design/icons';
import {Button, Col, Form, Input, message, Modal, Radio, Row, Select, Upload} from 'antd';
import {connect, FormattedMessage} from 'umi';
import React, {Component} from 'react';
import GeographicView from './GeographicView';
import PhoneView from './PhoneView';
import styles from './BaseView.less';
import config from "@/utils/config";
import Cropper from "react-cropper";
import 'cropperjs/dist/cropper.css';
import {headerFix} from "@/utils/utils";

const {Option} = Select;

const {prefix} = config;

// 头像组件 方便以后独立，增加裁剪之类的功能
const AvatarView = ({avatar, params = {}, beforeUp, onChange}) => (
  <>
    <div className={styles.avatar_title}>
      <FormattedMessage id="account.basic.avatar" defaultMessage="Avatar"/>
    </div>
    <div className={styles.avatar}>
      <img src={avatar} alt="avatar"/>
    </div>
    <Upload name="avatar"
            showUploadList={false}
            beforeUpload={(file) => { // @todo 其他全局前置约束，由后台同一配置驱动，前台获取结果
              if (params.accept.indexOf(`.${file.type.substring(file.type.indexOf('/')+1, file.type.length)}`) === -1)
                return message.error('不允许的文件类型').then(() => false)
              return beforeUp(file);
            }}
            onChange={onChange}
            action={`${prefix}/user/uploadAvatar`}
            {...params}
    >
      <div className={styles.button_view}>
        <Button>
          <UploadOutlined/>
          <FormattedMessage
            id="account.basic.change-avatar"
            defaultMessage="Change avatar"
          />
        </Button>
      </div>
    </Upload>
  </>
);

const validatorGeographic = (_, value) => {
  const {province={}, city={}} = value?? {};

  if (!province.key)
    return Promise.resolve(); // 用Promise代替callback

  if (!city.key) {
    return Promise.reject(new Error('请输入所在城市!'));
  }

  return Promise.resolve();
};

const validatorPhone = (rule, value) => {
  if (!value)
    return Promise.resolve();
  const values = value.split('-');

  if (!values[0]) {
    return Promise.reject(new Error('请输入区号!'));
  }

  if (!values[1]) {
    return Promise.reject(new Error('请输入电话号码!'));
  }

  return Promise.resolve();
};

let blobFile = null;

class BaseView extends Component {
  view = undefined;

  cropRef = null;

  constructor(props) {
    super(props);
    this.state = {
      cropVisible: false,
      cropSrc: '',
      type: undefined,
    };
  }

  componentDidMount() {
    const {currentUser, dispatch} = this.props;
    if (!currentUser)
      dispatch({
        type: 'account/fetchCurrent',
      });
  }

  getAvatarURL() {
    const {currentUser} = this.props;

    if (currentUser) {
      if (currentUser.avatar) {
        return currentUser.avatar;
      }
    }

    return '';
  }

  upParams = () => {
    return {
      // multiple: true,
      accept: '.jpg,.png,.gif,.jpeg,.bmp',
      headers: headerFix,
    };
  };

  updateCurrent = () => {
    const {dispatch} = this.props;
    dispatch({
      type: 'account/fetchCurrent',
    });
  };

  // 设置裁切比例
  setAspectRatio = (e) => {
    if (!this.cropRef)
      return;
    this.cropRef.setAspectRatio(e.target.value);
  };

  // 设置旋转角度
  setRotateTo = (value) => {
    if (!this.cropRef)
      return;
    this.cropRef.rotate(value);
  };

  // 确认裁切
  saveCropper = (ftype) => {
    if (!this.cropRef)
      return;
    this.cropRef.getCroppedCanvas().toBlob((blob) => {
      blobFile = blob;
    }, ftype || 'image/png');
  };

  beforeUp = (file) => {
    const isGt50K = file.size / 1024 / 1024 > 5;
    if (isGt50K) {
      return message.error('图片大小不能超过5M').then(() => false);
    }

    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = (e) => {
      this.setState({
        cropSrc: e.target.result,
        cropVisible: true,
        type: file.type,
      })
    };

    // eslint-disable-next-line no-unused-vars
    return new Promise((resolve, reject) => {
      const itv = setInterval(() => {
        if (blobFile) {// 异步监听
          blobFile.uid = file.uid;
          blobFile.name = file.name;
          // 持续上传
          resolve(blobFile);
          this.setState({cropVisible: false});

          blobFile = null;

          window.clearInterval(itv);
        }
      }, 100);
    });
  };

  handleChange = (info) => {
    const {file: {status}} = info;

    if (status === 'done') {
      message.success(`${info.file.name} 文件上传成功！`, 1).then(() => this.updateCurrent());
    } else if (status === 'error') {
      message.error(`${info.file.name} 文件上传失败！`, 2).then();
    }
  };

  getViewDom = (ref) => {
    this.view = ref;
  };

  handleFinish = (values) => {
    const {geographic, ...otherValues} = values;
    const base = {
      province: geographic.province.key,
      city: geographic.city.key,
      ...otherValues
    }
    const {dispatch} = this.props;
    dispatch({
      type: 'account/saveBase',
      payload: base,
      callback: (res) => {
        if (res && res.data) {
          if (res.data === 'ok')
            this.updateCurrent();
        }
      }
    });
  };

  render() {
    const {currentUser} = this.props;
    const { cropVisible, cropSrc, type,} = this.state;
    return (
      <div className={styles.baseView} ref={this.getViewDom}>
        <div className={styles.left}>
          <Form
            layout="vertical"
            onFinish={this.handleFinish}
            initialValues={{...currentUser, country: 'China'}}
            hideRequiredMark
          >
            <Form.Item
              name="email"
              label={
                <FormattedMessage
                  id="account.basic.email" defaultMessage="Email"
                />
              }
              rules={[
                {
                  required: false,
                  message: <FormattedMessage id="account.basic.email-message"
                                             defaultMessage="Please input your email!"/>
                },
              ]}
            >
              <Input/>
            </Form.Item>
            <Form.Item
              name="nickname"
              label={
                <FormattedMessage id="account.basic.nickname"
                                  defaultMessage="Nickname"/>
              }
              rules={[
                {
                  required: false,
                  message: <FormattedMessage
                    id="account.basic.nickname-message"
                    defaultMessage="Please input your Nickname!"/>
                },
              ]}
            >
              <Input/>
            </Form.Item>
            <Form.Item
              name="remark"
              label={
                <FormattedMessage id="account.basic.profile"
                                  defaultMessage="Personal profile"/>
              }
              rules={[
                {
                  required: false,
                  message: <FormattedMessage
                    id="account.basic.profile-message"
                    defaultMessage="Please input your personal profile!"/>
                },
              ]}
            >
              <Input.TextArea
                placeholder={
                  <FormattedMessage id="account.basic.profile-placeholder"
                                    defaultMessage="Brief introduction to yourself"/>
                }
                rows={4}
              />
            </Form.Item>
            <Form.Item
              name="title"
              label={
                <FormattedMessage id="account.basic.title"
                                  defaultMessage="Title"/>
              }
              rules={[
                {
                  required: false,
                  message: <FormattedMessage
                    id="account.basic.title-message"
                    defaultMessage="Please input your Title!"/>
                },
              ]}
            >
              <Input/>
            </Form.Item>
            <Form.Item
              name="company"
              label={
                <FormattedMessage id="account.basic.group"
                                  defaultMessage="Group"/>
              }
              rules={[
                {
                  required: false,
                  message: <FormattedMessage
                    id="account.basic.group-message"
                    defaultMessage="Please input your Group!"/>
                },
              ]}
            >
              <Input/>
            </Form.Item>
            <Form.Item
              name="country"
              label={
                <FormattedMessage id="account.basic.country"
                                  defaultMessage="Country/Region"/>
              }
              rules={[
                {
                  required: false,
                  message: <FormattedMessage
                    id="account.basic.country-message"
                    defaultMessage="Please input your country!"/>
                },
              ]}
            >
              <Select
                style={{
                  maxWidth: 220,
                }}
                label="国家"
              >
                <Option value="China">中国</Option>
              </Select>
            </Form.Item>
            <Form.Item
              name="geographic"
              label={
                <FormattedMessage id="account.basic.geographic"
                                  defaultMessage="Province or city"/>
              }
              rules={[
                {
                  required: false,
                  message: <FormattedMessage
                    id="account.basic.geographic-message"
                    defaultMessage="Please input your geographic info!"/>
                },
                {
                  validator: validatorGeographic,
                },
              ]}
            >
              <GeographicView/>
            </Form.Item>
            <Form.Item
              name="address"
              label={
                <FormattedMessage id="account.basic.address"
                                  defaultMessage="District Street Address"/>
              }
              rules={[
                {
                  required: false,
                  message: <FormattedMessage
                    id="account.basic.address-message"
                    defaultMessage="Please input your address!"/>
                },
              ]}
            >
              <Input/>
            </Form.Item>
            <Form.Item
              name="telephone"
              label={
                <FormattedMessage id="account.basic.phone"
                                  defaultMessage="Phone Number"/>
              }
              rules={[
                {
                  required: false,
                  message: <FormattedMessage id="account.basic.phone-message"
                                             defaultMessage="Please input your phone!"/>
                },
                {
                  validator: validatorPhone,
                },
              ]}
            >
              <PhoneView/>
            </Form.Item>
            <Form.Item
              name="mobile"
              label={
                <FormattedMessage id="account.basic.mobile"
                                  defaultMessage="Cell-Phone Number"/>
              }
              rules={[
                {
                  required: false,
                  message: <FormattedMessage id="account.basic.mobile-message"
                                             defaultMessage="Please input your cell-phone number!"/>
                },
                {
                  pattern: /^1\d{10}$/,
                  message: <FormattedMessage id="account.basic.mobile-format"
                                             defaultMessage="Please input your cell-phone number!"/>
                },
              ]}
            >
              <Input />
            </Form.Item>
            <Form.Item>
              <Button htmlType="submit" type="primary">
                <FormattedMessage
                  id="account.basic.update"
                  defaultMessage="Update Information"/>
              </Button>
            </Form.Item>
          </Form>
        </div>
        <div className={styles.right}>
          <AvatarView avatar={this.getAvatarURL()} params={this.upParams()}
                      beforeUp={this.beforeUp}
                      onChange={this.handleChange}/>
        </div>
        <Modal
          title="裁切"
          visible={cropVisible}
          footer={null}
          width={748}
          destroyOnClose
          onCancel={() => { this.setState({cropVisible: false,}); }}
        >
          <div className={styles.cropperModal}>
            <Row gutter={[16, 16]}>
              <Col span={18}>
                <Cropper // 函数组件请使用useRef ref，类组件使用onInitialized
                  onInitialized={(ref) => { if (ref) { this.cropRef = ref; } }}
                  src={cropSrc}
                  style={{ height: 320, width: '100%' }}
                  preview=".preview"
                  viewMode={0}
                  zoomable={true}
                  guides={true}
                  background={true}
                  rotatable={true}
                  aspectRatio={1 / 1}
                />
              </Col>
              <Col span={6} className={styles.right_content}>
                <div
                  className="preview"
                  style={{
                    height: 200,
                    width: '100%',
                    float: 'left',
                    flex: 1,
                    overflow: 'hidden',
                    border: '1px solid #e8e8e8',
                  }}
                ></div>

                <Radio.Group
                  onChange={this.setAspectRatio}
                  buttonStyle="solid"
                  defaultValue={1 / 1}
                  className={styles.button_group}
                >
                  <Radio.Button value={1 / 1}>1:1</Radio.Button>
                  <Radio.Button value={9 / 16}>9:16</Radio.Button>
                  <Radio.Button value={4 / 3}>4:3</Radio.Button>
                </Radio.Group>
              </Col>
            </Row>

            <Row gutter={[16, 16]}>
              <Col span={9}>
                <Input.Group>
                  <Button onClick={() => this.setRotateTo(-90)}>左旋转</Button>
                  <Button onClick={() => this.setRotateTo(-15)}>-15°</Button>
                  <Button onClick={() => this.setRotateTo(-30)}>-30°</Button>
                  <Button onClick={() => this.setRotateTo(-45)}>-45°</Button>
                </Input.Group>
              </Col>
              <Col span={9}>
                <Input.Group>
                  <Button onClick={() => this.setRotateTo(90)}>右旋转</Button>
                  <Button onClick={() => this.setRotateTo(15)}>15°</Button>
                  <Button onClick={() => this.setRotateTo(30)}>30°</Button>
                  <Button onClick={() => this.setRotateTo(45)}>45°</Button>
                </Input.Group>
              </Col>
              <Col span={6}>
                <Button type="primary" block onClick={() => this.saveCropper(type)}>
                  确定
                </Button>
              </Col>
            </Row>
          </div>
        </Modal>
      </div>
    );
  }
}

export default connect(({account}) => ({
  currentUser: account.currentUser,
}))(BaseView);
