import React from 'react';
import { useIntl } from 'umi';
import { Button, Form, Select, Space, TreeSelect } from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import { SettingOutlined } from '@ant-design/icons';

const UNCHANGED = '__unchanged__';

const BatchSettingModal = ({
  visible,
  onCancel,
  onSubmit,
  menuRegionOptions,
  terminalOptions,
  scopeLevelOptions,
  apps,
  menus,
  selectedCount,
}) => {
  const intl = useIntl();
  const [form] = Form.useForm();

  const withUnchanged = (options, labelId, defaultLabel) => [
    {
      label: intl.formatMessage({ id: labelId, defaultMessage: defaultLabel }),
      value: UNCHANGED,
    },
    ...(options || []).filter((item) => item.value !== ''),
  ];

  const handleOk = async () => {
    const values = await form.validateFields();
    const patch = {};
    if (values.menuRegion !== UNCHANGED) {
      patch.menuRegion = values.menuRegion ?? '';
    }
    if (values.terminalsMode !== UNCHANGED) {
      patch.terminals = values.terminalsMode === 'clear'
        ? ''
        : (Array.isArray(values.terminals) ? values.terminals.join(',') : '');
    }
    if (values.parentIdMode !== UNCHANGED) {
      patch.parentId = values.parentId;
    }
    if (values.scopeLevel !== UNCHANGED) {
      patch.scopeLevel = values.scopeLevel;
    }
    if (values.isValid !== UNCHANGED) {
      patch.isValid = values.isValid;
    }
    if (values.appId !== UNCHANGED) {
      patch.appId = values.appId;
    }
    if (Object.keys(patch).length === 0) {
      return;
    }
    await onSubmit(patch);
    form.resetFields();
  };

  return (
    <FullscreenModal
      width={640}
      bodyStyle={{ padding: '24px' }}
      visible={visible}
      title={
        <Space>
          <SettingOutlined style={{ color: '#1890ff' }} />
          {intl.formatMessage({ id: 'sys.res.batch.setting.title', defaultMessage: '批量设置' })}
          <span style={{ fontWeight: 400, fontSize: 14, color: '#666' }}>
            ({selectedCount})
          </span>
        </Space>
      }
      onCancel={() => {
        form.resetFields();
        onCancel();
      }}
      footer={
        <Space>
          <Button onClick={onCancel}>
            {intl.formatMessage({ id: 'sys.res.common.cancel', defaultMessage: '取消' })}
          </Button>
          <Button type="primary" onClick={handleOk}>
            {intl.formatMessage({ id: 'sys.res.batch.setting.apply', defaultMessage: '应用到选中项' })}
          </Button>
        </Space>
      }
    >
      <Form
        form={form}
        layout="vertical"
        initialValues={{
          menuRegion: UNCHANGED,
          terminalsMode: UNCHANGED,
          parentIdMode: UNCHANGED,
          scopeLevel: UNCHANGED,
          isValid: UNCHANGED,
          appId: UNCHANGED,
        }}
      >
        <Form.Item
          name="parentIdMode"
          label={intl.formatMessage({ id: 'sys.res.field.parentMenu', defaultMessage: '上级菜单' })}
        >
          <Select
            options={[
              {
                label: intl.formatMessage({ id: 'sys.res.batch.setting.unchanged', defaultMessage: '不修改' }),
                value: UNCHANGED,
              },
              {
                label: intl.formatMessage({ id: 'sys.res.batch.setting.parent.custom', defaultMessage: '指定上级（下方选择）' }),
                value: 'custom',
              },
            ]}
          />
        </Form.Item>
        <Form.Item noStyle shouldUpdate={(prev, curr) => prev.parentIdMode !== curr.parentIdMode}>
          {({ getFieldValue }) =>
            getFieldValue('parentIdMode') === 'custom' ? (
              <Form.Item
                name="parentId"
                label=" "
                rules={[
                  {
                    required: true,
                    message: intl.formatMessage({ id: 'sys.res.rule.selectParentMenu', defaultMessage: '请选择上级菜单' }),
                  },
                ]}
              >
                <TreeSelect
                  showSearch
                  treeData={menus || []}
                  treeDefaultExpandAll
                  treeLine
                  placeholder={intl.formatMessage({ id: 'sys.res.ph.treeSelect', defaultMessage: '请选择' })}
                  treeNodeFilterProp="title"
                  dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                />
              </Form.Item>
            ) : null
          }
        </Form.Item>
        <Form.Item
          name="menuRegion"
          label={intl.formatMessage({ id: 'sys.res.field.menuRegion', defaultMessage: '展示区域' })}
        >
          <Select
            options={[
              {
                label: intl.formatMessage({ id: 'sys.res.batch.setting.unchanged', defaultMessage: '不修改' }),
                value: UNCHANGED,
              },
              {
                label: intl.formatMessage({ id: 'sys.res.menuRegion.default', defaultMessage: '默认主导航' }),
                value: '',
              },
              ...(menuRegionOptions || []).filter((item) => item.value !== ''),
            ]}
          />
        </Form.Item>
        <Form.Item
          name="terminalsMode"
          label={intl.formatMessage({ id: 'sys.res.field.terminals', defaultMessage: '可见终端' })}
        >
          <Select
            options={[
              {
                label: intl.formatMessage({ id: 'sys.res.batch.setting.unchanged', defaultMessage: '不修改' }),
                value: UNCHANGED,
              },
              {
                label: intl.formatMessage({ id: 'sys.res.terminals.all', defaultMessage: '全部终端' }),
                value: 'clear',
              },
              {
                label: intl.formatMessage({ id: 'sys.res.batch.setting.terminals.custom', defaultMessage: '指定终端（下方多选）' }),
                value: 'custom',
              },
            ]}
          />
        </Form.Item>
        <Form.Item noStyle shouldUpdate={(prev, curr) => prev.terminalsMode !== curr.terminalsMode}>
          {({ getFieldValue }) =>
            getFieldValue('terminalsMode') === 'custom' ? (
              <Form.Item name="terminals" label=" ">
                <Select
                  mode="multiple"
                  allowClear
                  placeholder={intl.formatMessage({
                    id: 'sys.res.field.terminals.placeholder',
                    defaultMessage: '默认全部终端可见',
                  })}
                  options={(terminalOptions || []).filter((item) => item.value !== '')}
                />
              </Form.Item>
            ) : null
          }
        </Form.Item>
        <Form.Item
          name="scopeLevel"
          label={intl.formatMessage({ id: 'sys.res.field.scopeLevel', defaultMessage: '授权层级' })}
        >
          <Select
            options={withUnchanged(scopeLevelOptions, 'sys.res.batch.setting.unchanged', '不修改')}
          />
        </Form.Item>
        <Form.Item
          name="isValid"
          label={intl.formatMessage({ id: 'sys.res.field.isValid', defaultMessage: '应用状态' })}
        >
          <Select
            options={[
              {
                label: intl.formatMessage({ id: 'sys.res.batch.setting.unchanged', defaultMessage: '不修改' }),
                value: UNCHANGED,
              },
              { label: intl.formatMessage({ id: 'sys.res.valid.yes', defaultMessage: '有效' }), value: '1' },
              { label: intl.formatMessage({ id: 'sys.res.valid.no', defaultMessage: '无效' }), value: '0' },
            ]}
          />
        </Form.Item>
        <Form.Item
          name="appId"
          label={intl.formatMessage({ id: 'sys.res.field.appId', defaultMessage: '归属应用' })}
        >
          <Select
            options={[
              {
                label: intl.formatMessage({ id: 'sys.res.batch.setting.unchanged', defaultMessage: '不修改' }),
                value: UNCHANGED,
              },
              ...(apps || []).map((app) => ({
                label: app.label || app.appName,
                value: app.value ?? app.id,
              })),
            ]}
          />
        </Form.Item>
      </Form>
    </FullscreenModal>
  );
};

export default BatchSettingModal;
