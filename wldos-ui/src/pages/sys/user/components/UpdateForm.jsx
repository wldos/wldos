import React, { useEffect, useState } from 'react';
import {Button, Form, Input, Select, Card, Space, Tooltip} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import { fetchEnumUserStatus } from '@/services/enum';
import {
  UserOutlined,
  KeyOutlined,
  MailOutlined,
  PhoneOutlined,
  FileTextOutlined,
  SettingOutlined,
  InfoCircleOutlined,
  CheckCircleOutlined,
  EditOutlined,
  LockOutlined,
  ApartmentOutlined
} from '@ant-design/icons';
import OrgTreeSelect from '@/components/TreeSelects/OrgTreeSelect';

const FormItem = Form.Item;
const {TextArea} = Input;

const formLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 16,
  },
};

const UpdateForm = (props) => {
  const [form] = Form.useForm();
  const {
    onSubmit: handleUpdate,
    onCancel: handleUpdateModalVisible,
    updateModalVisible,
    values,
    comList = [],
  } = props;

  const [statusOptions, setStatusOptions] = useState([]);
  const comId = Form.useWatch('comId', form);

  useEffect(() => {
    fetchEnumUserStatus().then((res) => {
      if (res?.data) {
        setStatusOptions(res.data);
      }
    });
  }, []);

  useEffect(() => {
    if (!updateModalVisible) {
      return;
    }
    form.setFieldsValue({
      nickname: values?.nickname,
      remark: values?.remark,
      loginName: values?.loginName || values?.login_name || values?.account,
      email: values?.email,
      mobile: values?.mobile,
      status: values?.status,
      orgId: values?.orgId,
      comId: values?.comId,
    });
  }, [updateModalVisible, values, form]);

  const handleNext = async () => {
    try {
      const fieldsValue = await form.validateFields();
      const value = {...values, ...fieldsValue};
      await handleUpdate(value);
    } catch (error) {
      console.log('表单验证失败:', error);
    }
  };

  const renderContent = () => {
    return (
      <div>
        {/* 基础信息 */}
        <Card
          title={
            <Space>
              <UserOutlined style={{ color: '#1890ff' }} />
              基础信息
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title="用户的基本信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="nickname"
            label={
              <Space>
                昵称
                <Tooltip title="用户的显示名称，用于界面展示">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入昵称，不能为空，最多50个字！',
                max: 50,
              },
            ]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="请输入昵称，最多50个字"
            />
          </FormItem>

          <FormItem
            name="loginName"
            label={
              <Space>
                登录账号
                <Tooltip title="用户的登录用户名">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '请输入登录账号，不能为空，最多30个字符！',
                max: 30,
              },
            ]}
          >
            <Input
              prefix={<KeyOutlined />}
              placeholder="请输入登录账号，最多30个字符"
            />
          </FormItem>

          <FormItem
            name="email"
            label={
              <Space>
                邮箱地址
                <Tooltip title="用户的邮箱地址，用于接收通知">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: '邮箱不能为空',
              },
              {
                type: 'email',
                message: '邮箱格式不正确',
              },
            ]}
          >
            <Input
              prefix={<MailOutlined />}
              placeholder="请输入邮箱地址"
            />
          </FormItem>

          <FormItem
            name="mobile"
            label={
              <Space>
                手机号码
                <Tooltip title="用户的手机号码，用于接收短信通知（选填，须 11 位）">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                validator(_, value) {
                  const v = value == null ? '' : String(value).trim();
                  if (!v) return Promise.resolve();
                  if (/^1[3-9]\d{9}$/.test(v)) return Promise.resolve();
                  return Promise.reject(new Error('手机号必须是11位数字'));
                },
              },
            ]}
          >
            <Input
              prefix={<PhoneOutlined />}
              placeholder="请输入手机号码"
            />
          </FormItem>
        </Card>

        {/* 用户配置 */}
        <Card
          title={
            <Space>
              <SettingOutlined style={{ color: '#52c41a' }} />
              用户配置
            </Space>
          }
          size="small"
          extra={
            <Tooltip title="用户的状态和备注信息">
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="remark"
            label={
              <Space>
                备注信息
                <Tooltip title="用户的备注信息，用于管理员记录">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                max: 200,
                message: '备注信息最多200个字符！',
              },
            ]}
          >
            <TextArea
              rows={3}
              placeholder="请输入备注信息，最多200个字符"
              style={{ resize: 'vertical' }}
            />
          </FormItem>

          <FormItem
            name="status"
            label={
              <Space>
                用户状态
                <Tooltip title="用户的启用状态">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Select
              placeholder="请选择用户状态"
              style={{ width: '100%' }}
              options={statusOptions}
            />
          </FormItem>
        </Card>

        {/* 组织归属，仅用户管理全局列表可见（组织成员子列表配置不涉及归属调整） */}
        {values.orgId !== undefined && (
          <Card
            title={
              <Space>
                <ApartmentOutlined style={{ color: '#722ed1' }} />
                组织归属
              </Space>
            }
            size="small"
            style={{ marginTop: '16px' }}
            extra={
              <Tooltip title="用户所属的公司与组织">
                <InfoCircleOutlined />
              </Tooltip>
            }
          >
            <FormItem
              name="comId"
              label={
                <Space>
                  公司
                  <Tooltip title="用户的主企业归属，调整后只迁移该用户主企业这一条关联记录，其它兼职企业不受影响">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
            >
              <Select
                showSearch
                optionFilterProp="label"
                placeholder="请选择公司"
                options={comList}
                onChange={() => {
                  form.setFieldsValue({ orgId: undefined });
                  form.validateFields(['orgId']);
                }}
              />
            </FormItem>

            <FormItem
              name="orgId"
              label={
                <Space>
                  组织
                  <Tooltip title="随公司级联，只能选择该公司下的组织；调整后只迁移该用户主组织记录，其它兼职组织不受影响">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              dependencies={['comId']}
              rules={[
                {
                  validator(_, value) {
                    if (!comId || value !== undefined) {
                      return Promise.resolve();
                    }
                    return Promise.reject(new Error('公司不为空时，组织不允许为空'));
                  },
                },
              ]}
            >
              <OrgTreeSelect
                comId={comId}
                domainId={values.domainId}
                seedOption={
                  values.orgId != null && values.orgName
                    ? { value: values.orgId, label: values.orgName }
                    : undefined
                }
                placeholder={comId ? '请选择组织' : '请先选择公司'}
              />
            </FormItem>
          </Card>
        )}
      </div>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleUpdateModalVisible(false, values)}>取消</Button>
        <Button type="primary" icon={<EditOutlined />} onClick={handleNext}>保存配置</Button>
      </>
    );
  };

  return (
    <FullscreenModal
      width={800}
      bodyStyle={{
        padding: '24px'
      }}
      destroyOnClose
      title={
        <Space>
          <EditOutlined style={{ color: '#1890ff' }} />
          用户配置
        </Space>
      }
      visible={updateModalVisible}
      footer={renderFooter()}
      onCancel={() => handleUpdateModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={{
          nickname: values?.nickname,
          remark: values?.remark,
          loginName: values?.loginName || values?.login_name || values?.account,
          email: values?.email,
          mobile: values?.mobile,
          status: values?.status,
          orgId: values?.orgId,
          comId: values?.comId,
        }}
      >
        {renderContent()}
      </Form>
    </FullscreenModal>
  );
};

export default UpdateForm;
