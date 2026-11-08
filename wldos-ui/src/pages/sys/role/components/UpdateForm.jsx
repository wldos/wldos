import React, { useCallback, useEffect, useState } from 'react';
import {Button, Form, Input, Select, Card, Space, Tooltip, message} from 'antd';
import {useIntl} from 'umi';
import { DISPLAY_ORDER_PATTERN_STRING } from '@/utils/displayOrder';
import FullscreenModal from '@/components/FullscreenModal';
import { DATA_POLICY_TIP_ID, DATA_POLICY_TIP_PROPS } from '../_shared/dataPolicyTip';
import ScopeDimensionField, { filterPersistableScopes } from './ScopeDimensionField';
import {
  fetchScopeDimensions,
  fetchPickableDictTypes,
  fetchDirectAdoptedDimensions,
  fetchRoleScopes,
  copyRoleScopes,
} from '../service';
import {
  EditOutlined,
  UserOutlined,
  SettingOutlined,
  InfoCircleOutlined,
  KeyOutlined,
  FileTextOutlined,
  NumberOutlined,
  ApartmentOutlined,
} from '@ant-design/icons';

const FormItem = Form.Item;
const {TextArea} = Input;
const {Option} = Select;

const formLayout = {
  labelCol: {
    span: 7,
  },
  wrapperCol: {
    span: 13,
  },
};

const UpdateForm = (props) => {
  const intl = useIntl();
  const [form] = Form.useForm();
  const {
    onSubmit: handleUpdate,
    onCancel: handleUpdateModalVisible,
    updateModalVisible,
    values,
    roles,
    comList = [],
    assignable = true,
    managed = true,
    isPlatformAdmin = false,
  } = props;

  const assignOnly = !managed && assignable;

  const [globalDimensions, setGlobalDimensions] = useState([]);
  const [customDimensions, setCustomDimensions] = useState([]);
  const [pickableDictTypes, setPickableDictTypes] = useState([]);
  const [directAdoptedDictCodes, setDirectAdoptedDictCodes] = useState([]);
  const [adoptedDictCodes, setAdoptedDictCodes] = useState([]);
  const [scopes, setScopes] = useState([]);
  const [copySourceRoleId, setCopySourceRoleId] = useState(undefined);

  const reloadScopeData = useCallback(() => {
    if (!values?.id) {
      return;
    }
    const roleId = values.id;
    fetchScopeDimensions({ roleId }).then((res) => {
      if (res?.code === 200) {
        const data = res.data || {};
        setGlobalDimensions(data.globalDimensions || []);
        setCustomDimensions(data.customDimensions || []);
      }
    });
    fetchPickableDictTypes({ roleId }).then((res) => {
      if (res?.code === 200) {
        setPickableDictTypes(res.data || []);
      }
    });
    fetchDirectAdoptedDimensions({ roleId }).then((res) => {
      if (res?.code === 200) {
        const codes = res.data || [];
        setDirectAdoptedDictCodes(codes);
        setAdoptedDictCodes(codes);
      }
    });
    fetchRoleScopes({ roleId }).then((res) => {
      if (res?.code === 200) {
        setScopes(res.data || []);
      }
    });
  }, [values?.id]);

  useEffect(() => {
    if (!updateModalVisible || !values?.id || !assignable) {
      return;
    }
    reloadScopeData();
  }, [updateModalVisible, values?.id, assignable, reloadScopeData]);

  const handleCopyScopes = async () => {
    if (!copySourceRoleId || !values?.id) {
      return;
    }
    const res = await copyRoleScopes({
      sourceRoleId: copySourceRoleId,
      targetRoleId: values.id,
    });
    if (res?.code === 200) {
      message.success('已复制定制过滤');
      reloadScopeData();
      setCopySourceRoleId(undefined);
    }
  };

  const scopeFieldProps = {
    globalDimensions,
    customDimensions,
    pickableDictTypes,
    directAdoptedDictCodes,
    adoptedDictCodes,
    onAdoptedChange: setAdoptedDictCodes,
    value: scopes,
    onChange: setScopes,
  };

  const persistScopes = () => {
    const persistable = filterPersistableScopes(scopes);
    const dimCodesWithValues = new Set(persistable.map((s) => s.dimensionCode).filter(Boolean));
    const resolveDictCode = (dimensionCode) => {
      const ext = (customDimensions || []).find((item) => item.dimensionCode === dimensionCode);
      if (ext?.dictCode) {
        return ext.dictCode;
      }
      const pick = (pickableDictTypes || []).find(
        (item) => item.registeredDimensionCode === dimensionCode || item.dictCode === dimensionCode,
      );
      if (pick?.dictCode) {
        return pick.dictCode;
      }
      if ((adoptedDictCodes || []).includes(dimensionCode)) {
        return dimensionCode;
      }
      return null;
    };
    const dictCodesWithValues = new Set(
      [...dimCodesWithValues].map(resolveDictCode).filter(Boolean),
    );
    return {
      scopes: persistable,
      adoptedDictCodes: (adoptedDictCodes || []).filter((c) => dictCodesWithValues.has(c)),
    };
  };

  const renderScopeCopyBar = () => (
    <Card size="small" style={{ marginBottom: 16 }}>
      <Space wrap>
        <Select
          allowClear
          placeholder="复制定制过滤自…"
          style={{ minWidth: 220 }}
          value={copySourceRoleId}
          onChange={setCopySourceRoleId}
          options={(roles || []).filter((r) => String(r.value) !== String(values?.id))}
        />
        <Button disabled={!copySourceRoleId} onClick={handleCopyScopes}>
          复制
        </Button>
      </Space>
    </Card>
  );

  const handleNext = async () => {
    if (assignOnly) {
      const fieldsValue = await form.validateFields(['dataPolicy']);
      handleUpdate({ ...values, dataPolicy: fieldsValue.dataPolicy }, persistScopes());
      return;
    }
    const fieldsValue = await form.validateFields();
    const value = { ...values, ...fieldsValue };
    handleUpdate(value, assignable ? persistScopes() : undefined);
  };

  const renderContent = () => (
      <div>
        {assignOnly ? (
          <>
            <Card
              title={
                <Space>
                  <SettingOutlined style={{ color: '#52c41a' }} />
                  数据权限与范围
                </Space>
              }
              size="small"
              style={{ marginBottom: '16px' }}
            >
              <FormItem
                name="dataPolicy"
                label={
                  <Space>
                    {intl.formatMessage({ id: 'sys.role.update.field.dataPolicy', defaultMessage: '数据策略' })}
                    <Tooltip title={intl.formatMessage({ id: DATA_POLICY_TIP_ID })} {...DATA_POLICY_TIP_PROPS}>
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
              >
                <Select
                  allowClear
                  placeholder={intl.formatMessage({ id: 'sys.role.update.ph.dataPolicy', defaultMessage: '默认按身份推断' })}
                  style={{ width: '100%' }}
                >
                  <Option value="SELF">{intl.formatMessage({ id: 'sys.role.dataPolicy.SELF', defaultMessage: '仅本人' })}</Option>
                  <Option value="ORG">{intl.formatMessage({ id: 'sys.role.dataPolicy.ORG', defaultMessage: '本组织' })}</Option>
                  <Option value="ORG_SUBTREE">{intl.formatMessage({ id: 'sys.role.dataPolicy.ORG_SUBTREE', defaultMessage: '组织及下级' })}</Option>
                  <Option value="TENANT">{intl.formatMessage({ id: 'sys.role.dataPolicy.TENANT', defaultMessage: '本租户' })}</Option>
                  <Option value="TENANT_DOMAIN">{intl.formatMessage({ id: 'sys.role.dataPolicy.TENANT_DOMAIN', defaultMessage: '本租户本站点' })}</Option>
                  <Option value="DOMAIN">{intl.formatMessage({ id: 'sys.role.dataPolicy.DOMAIN', defaultMessage: '本站点' })}</Option>
                  <Option value="ALL">{intl.formatMessage({ id: 'sys.role.dataPolicy.ALL', defaultMessage: '全部' })}</Option>
                </Select>
              </FormItem>
            </Card>
            {values?.id ? (
              <>
                {renderScopeCopyBar()}
                <ScopeDimensionField {...scopeFieldProps} />
              </>
            ) : null}
          </>
        ) : (
          <>
        <Card
          title={
            <Space>
              <UserOutlined style={{ color: '#1890ff' }} />
              {intl.formatMessage({ id: 'sys.role.update.card.basic', defaultMessage: '基础信息' })}
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title={intl.formatMessage({ id: 'sys.role.update.card.basic.tip', defaultMessage: '角色的基本信息' })}>
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="roleName"
            label={
              <Space>
                {intl.formatMessage({ id: 'sys.role.update.field.roleName', defaultMessage: '角色名称' })}
                <Tooltip title={intl.formatMessage({ id: 'sys.role.update.field.roleName.tip', defaultMessage: '角色的显示名称，用于界面展示' })}>
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: intl.formatMessage({ id: 'sys.role.update.rule.roleName', defaultMessage: '请输入名称，不能为空，最多25个字！' }),
                max: 25,
              },
            ]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder={intl.formatMessage({ id: 'sys.role.update.ph.roleName', defaultMessage: '请输入角色名称，最多25个字' })}
            />
          </FormItem>

          <FormItem
            name="roleCode"
            label={
              <Space>
                {intl.formatMessage({ id: 'sys.role.update.field.roleCode', defaultMessage: '角色编码' })}
                <Tooltip title={intl.formatMessage({ id: 'sys.role.update.field.roleCode.tip', defaultMessage: '角色的唯一标识，用于系统识别' })}>
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: intl.formatMessage({ id: 'sys.role.update.rule.roleCode', defaultMessage: '请输入编码，不能为空，最多32位！' }),
                max: 32,
              },
            ]}
          >
            <Input
              prefix={<KeyOutlined />}
              placeholder={intl.formatMessage({ id: 'sys.role.update.ph.roleCode', defaultMessage: '请输入角色编码，最多32位' })}
            />
          </FormItem>

          <FormItem
            name="roleDesc"
            label={
              <Space>
                {intl.formatMessage({ id: 'sys.role.update.field.roleDesc', defaultMessage: '角色描述' })}
                <Tooltip title={intl.formatMessage({ id: 'sys.role.update.field.roleDesc.tip', defaultMessage: '角色的详细说明，帮助理解角色用途' })}>
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: false,
                message: intl.formatMessage({ id: 'sys.role.update.rule.roleDescMax', defaultMessage: '最多150个字！' }),
                max: 150,
              },
            ]}
          >
            <TextArea
              rows={3}
              placeholder={intl.formatMessage({ id: 'sys.role.update.ph.roleDesc', defaultMessage: '请输入角色描述，最多150个字' })}
              style={{ resize: 'vertical' }}
            />
          </FormItem>
        </Card>

        <Card
          title={
            <Space>
              <SettingOutlined style={{ color: '#52c41a' }} />
              {intl.formatMessage({ id: 'sys.role.update.card.role', defaultMessage: '角色配置' })}
            </Space>
          }
          size="small"
          style={{ marginBottom: '16px' }}
          extra={
            <Tooltip title={intl.formatMessage({ id: 'sys.role.update.card.role.tip', defaultMessage: '角色的类型和层级配置' })}>
              <InfoCircleOutlined />
            </Tooltip>
          }
        >
          <FormItem
            name="roleType"
            label={
              <Space>
                {intl.formatMessage({ id: 'sys.role.update.field.roleType', defaultMessage: '角色类型' })}
                <Tooltip title={intl.formatMessage({ id: 'sys.role.update.field.roleType.tip', defaultMessage: '角色的分类，影响权限范围' })}>
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Select
              placeholder={intl.formatMessage({ id: 'sys.role.update.ph.roleType', defaultMessage: '请选择角色类型' })}
              style={{ width: '100%' }}
            >
              {isPlatformAdmin ? (
                <Option value="sys_role">{intl.formatMessage({ id: 'sys.role.roleType.sys_role', defaultMessage: '系统角色' })}</Option>
              ) : null}
              <Option value="subject">{intl.formatMessage({ id: 'sys.role.roleType.subject', defaultMessage: '社会主体' })}</Option>
              <Option value="tal_role">{intl.formatMessage({ id: 'sys.role.roleType.tal_role', defaultMessage: '租户角色' })}</Option>
            </Select>
          </FormItem>

          <FormItem
            name="dataPolicy"
            label={
              <Space>
                {intl.formatMessage({ id: 'sys.role.update.field.dataPolicy', defaultMessage: '数据策略' })}
                <Tooltip
                  title={intl.formatMessage({ id: DATA_POLICY_TIP_ID })}
                  {...DATA_POLICY_TIP_PROPS}
                >
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Select
              allowClear
              placeholder={intl.formatMessage({ id: 'sys.role.update.ph.dataPolicy', defaultMessage: '默认按身份推断' })}
              style={{ width: '100%' }}
            >
              <Option value="SELF">{intl.formatMessage({ id: 'sys.role.dataPolicy.SELF', defaultMessage: '仅本人' })}</Option>
              <Option value="ORG">{intl.formatMessage({ id: 'sys.role.dataPolicy.ORG', defaultMessage: '本组织' })}</Option>
              <Option value="ORG_SUBTREE">{intl.formatMessage({ id: 'sys.role.dataPolicy.ORG_SUBTREE', defaultMessage: '组织及下级' })}</Option>
              <Option value="TENANT">{intl.formatMessage({ id: 'sys.role.dataPolicy.TENANT', defaultMessage: '本租户' })}</Option>
              <Option value="TENANT_DOMAIN">{intl.formatMessage({ id: 'sys.role.dataPolicy.TENANT_DOMAIN', defaultMessage: '本租户本站点' })}</Option>
              <Option value="DOMAIN">{intl.formatMessage({ id: 'sys.role.dataPolicy.DOMAIN', defaultMessage: '本站点' })}</Option>
              <Option value="ALL">{intl.formatMessage({ id: 'sys.role.dataPolicy.ALL', defaultMessage: '全部' })}</Option>
            </Select>
          </FormItem>

          <FormItem
            name="comId"
            label={
              <Space>
                {intl.formatMessage({ id: 'sys.role.update.field.comId', defaultMessage: '归属公司' })}
                <Tooltip title={intl.formatMessage({ id: 'sys.role.update.field.comId.tip', defaultMessage: '角色所属的公司；不选择时超管创建落平台，租户管理员创建按自身所属租户' })}>
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Select
              placeholder={intl.formatMessage({ id: 'sys.role.update.ph.comId', defaultMessage: '请选择归属公司（不选默认平台/自身租户）' })}
              style={{ width: '100%' }}
              allowClear
              filterOption={(input, option) =>
                option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
              }
              options={comList}
            />
          </FormItem>

          <FormItem
            name="parentId"
            label={
              <Space>
                {intl.formatMessage({ id: 'sys.role.update.field.parentId', defaultMessage: '父角色' })}
                <Tooltip title={intl.formatMessage({ id: 'sys.role.update.field.parentId.tip', defaultMessage: '角色的上级角色，用于角色层级管理' })}>
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Select
              placeholder={intl.formatMessage({ id: 'sys.role.update.ph.parentId', defaultMessage: '请选择父角色' })}
              style={{ width: '100%' }}
              showSearch
              optionFilterProp="label"
              options={roles}
            />
          </FormItem>

          <FormItem
            name="displayOrder"
            label={
              <Space>
                {intl.formatMessage({ id: 'sys.role.update.field.displayOrder', defaultMessage: '展示顺序' })}
                <Tooltip title={intl.formatMessage({ id: 'sys.role.update.field.displayOrder.tip', defaultMessage: '角色在列表中的显示顺序，1-9999' })}>
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            rules={[
              {
                required: true,
                message: intl.formatMessage({ id: 'sys.role.update.rule.displayOrder', defaultMessage: '展示顺序1~9999！' }),
                pattern: DISPLAY_ORDER_PATTERN_STRING,
                max: 3,
              },
            ]}
          >
            <Input
              prefix={<NumberOutlined />}
              placeholder={intl.formatMessage({ id: 'sys.role.update.ph.displayOrder', defaultMessage: '请输入数字，1-9999' })}
            />
          </FormItem>

          <FormItem
            name="isValid"
            label={
              <Space>
                {intl.formatMessage({ id: 'sys.role.update.field.isValid', defaultMessage: '状态' })}
                <Tooltip title={intl.formatMessage({ id: 'sys.role.update.field.isValid.tip', defaultMessage: '角色的启用状态' })}>
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Select
              placeholder={intl.formatMessage({ id: 'sys.role.update.ph.isValid', defaultMessage: '请选择状态' })}
              style={{ width: '100%' }}
            >
              <Option value="1">{intl.formatMessage({ id: 'sys.role.status.valid', defaultMessage: '有效' })}</Option>
              <Option value="0">{intl.formatMessage({ id: 'sys.role.status.invalid', defaultMessage: '无效' })}</Option>
            </Select>
          </FormItem>
        </Card>

        {assignable && values?.id ? (
          <>
            {renderScopeCopyBar()}
            <ScopeDimensionField {...scopeFieldProps} />
          </>
        ) : null}
          </>
        )}
      </div>
    );

  const renderFooter = () =>
    (
      <>
        <Button onClick={() => handleUpdateModalVisible(false, values)}>{intl.formatMessage({ id: 'sys.role.update.cancel', defaultMessage: '取消' })}</Button>
        <Button type="primary" icon={<EditOutlined />} onClick={() => handleNext()}>{intl.formatMessage({ id: 'sys.role.update.save', defaultMessage: '保存配置' })}</Button>
      </>
    );

  return (
    <FullscreenModal
      title={
        <Space>
          <EditOutlined style={{ color: '#1890ff' }} />
          {assignOnly ? '数据权限与范围' : intl.formatMessage({ id: 'sys.role.update.title', defaultMessage: '角色配置' })}
        </Space>
      }
      width={800}
      bodyStyle={{
        maxHeight: '70vh',
        overflowY: 'auto',
        padding: '24px'
      }}
      destroyOnClose
      visible={updateModalVisible}
      footer={renderFooter()}
      onCancel={() => handleUpdateModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={{
          roleName: props.values.roleName,
          roleCode: props.values.roleCode,
          roleType: props.values.roleType,
          dataPolicy: props.values.dataPolicy,
          parentId: props.values.parentId,
          roleDesc: props.values.roleDesc,
          isValid: props.values.isValid,
          displayOrder: props.values.displayOrder,
          comId: props.values.comId,
        }}
      >
        {renderContent()}
      </Form>
    </FullscreenModal>
  );
};

export default UpdateForm;
