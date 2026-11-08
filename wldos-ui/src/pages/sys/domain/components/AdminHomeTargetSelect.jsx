import React, { useEffect, useMemo, useState } from 'react';
import { Form, Input, Select, Space, Tooltip } from 'antd';
import { InfoCircleOutlined } from '@ant-design/icons';
import { useIntl } from 'umi';
import { queryDomainResList } from '../service';

const ADMIN_MENU_TYPES = new Set(['admin_menu', 'admin_plugin_menu']);

const flattenResources = (nodes, out = []) => {
  if (!nodes?.length) {
    return out;
  }
  nodes.forEach((node) => {
    out.push(node);
    if (node.children?.length) {
      flattenResources(node.children, out);
    }
  });
  return out;
};

export default function AdminHomeTargetSelect({ domainId, form }) {
  const intl = useIntl();
  const preset = Form.useWatch('adminHomePreset', form);
  const [menuOptions, setMenuOptions] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!domainId) {
      setMenuOptions([]);
      return undefined;
    }
    let cancelled = false;
    setLoading(true);
    queryDomainResList({ domainId, current: 1, pageSize: 5000 })
      .then((res) => {
        if (cancelled) {
          return;
        }
        const rows = res?.data?.rows ?? res?.data ?? [];
        const flat = flattenResources(Array.isArray(rows) ? rows : []);
        const options = flat
          .filter((r) => r?.resourcePath && ADMIN_MENU_TYPES.has(r.resourceType))
          .map((r) => ({
            label: `${r.resourceName || r.resourcePath} (${r.resourcePath})`,
            value: r.resourcePath,
            title: r.resourceName || '',
          }));
        setMenuOptions(options);
      })
      .finally(() => {
        if (!cancelled) {
          setLoading(false);
        }
      });
    return () => {
      cancelled = true;
    };
  }, [domainId]);

  const presetOptions = useMemo(
    () => [
      {
        value: 'inherit',
        label: intl.formatMessage({
          id: 'sys.domain.update.adminHomePreset.inherit',
          defaultMessage: '继承根域 / 内置仪表盘',
        }),
      },
      {
        value: 'menu',
        label: intl.formatMessage({
          id: 'sys.domain.update.adminHomePreset.menu',
          defaultMessage: '管理端菜单',
        }),
      },
      {
        value: 'example',
        label: intl.formatMessage({
          id: 'sys.domain.update.adminHomePreset.example',
          defaultMessage: '示例页（component）',
        }),
      },
      {
        value: 'advanced',
        label: intl.formatMessage({
          id: 'sys.domain.update.adminHomePreset.advanced',
          defaultMessage: '高级自定义',
        }),
      },
    ],
    [intl],
  );

  const onMenuChange = (path) => {
    const hit = menuOptions.find((o) => o.value === path);
    form.setFieldsValue({
      adminHomeMenuPath: path,
      adminHomeUrl: path,
      adminHomeModule: 'redirect',
      adminHomeTitle: hit?.title || '',
    });
  };

  return (
    <>
      <Form.Item
        name="adminHomePreset"
        label={
          <Space>
            {intl.formatMessage({ id: 'sys.domain.update.field.adminHomePreset', defaultMessage: '首页目标' })}
            <Tooltip
              title={intl.formatMessage({
                id: 'sys.domain.update.field.adminHomePreset.tip',
                defaultMessage: '留空配置表示继承根域主站；本域写入 JSON 后独享',
              })}
            >
              <InfoCircleOutlined style={{ color: '#999' }} />
            </Tooltip>
          </Space>
        }
      >
        <Select options={presetOptions} />
      </Form.Item>

      {preset === 'menu' ? (
        <Form.Item
          name="adminHomeMenuPath"
          label={intl.formatMessage({ id: 'sys.domain.update.field.adminHomeMenu', defaultMessage: '选择菜单' })}
          rules={[
            {
              required: true,
              message: intl.formatMessage({
                id: 'sys.domain.update.rule.adminHomeMenu',
                defaultMessage: '请选择管理端菜单',
              }),
            },
          ]}
        >
          <Select
            showSearch
            loading={loading}
            optionFilterProp="label"
            placeholder={intl.formatMessage({
              id: 'sys.domain.update.ph.adminHomeMenu',
              defaultMessage: '搜索本域 admin 菜单',
            })}
            options={menuOptions}
            onChange={onMenuChange}
          />
        </Form.Item>
      ) : null}

      {preset === 'example' || preset === 'menu' ? (
        <Form.Item
          name="adminHomeTitle"
          label={intl.formatMessage({ id: 'sys.domain.update.field.adminHomeTitle', defaultMessage: '首页标题' })}
        >
          <Input />
        </Form.Item>
      ) : null}

      {preset === 'advanced' ? (
        <>
          <Form.Item
            name="adminHomeModule"
            label={intl.formatMessage({ id: 'sys.domain.update.field.adminHomeModule', defaultMessage: '首页类型' })}
          >
            <Select>
              <Select.Option value="component">
                {intl.formatMessage({
                  id: 'sys.domain.update.adminHomeModule.component',
                  defaultMessage: '自定义组件',
                })}
              </Select.Option>
              <Select.Option value="redirect">
                {intl.formatMessage({
                  id: 'sys.domain.update.adminHomeModule.redirect',
                  defaultMessage: '跳转',
                })}
              </Select.Option>
            </Select>
          </Form.Item>
          <Form.Item
            name="adminHomeUrl"
            label={intl.formatMessage({ id: 'sys.domain.update.field.adminHomeUrl', defaultMessage: '组件或跳转路径' })}
            labelCol={{ span: 3 }}
            wrapperCol={{ span: 21 }}
          >
            <Input
              placeholder={intl.formatMessage({
                id: 'sys.domain.update.ph.adminHomeUrl',
                defaultMessage: '如 admin/home/examples/commercial-overview',
              })}
            />
          </Form.Item>
          <Form.Item
            name="adminHomeTitle"
            label={intl.formatMessage({ id: 'sys.domain.update.field.adminHomeTitle', defaultMessage: '首页标题' })}
          >
            <Input />
          </Form.Item>
        </>
      ) : null}

      {preset !== 'inherit' ? (
        <Form.Item
          name="adminHomeExtraProps"
          label={intl.formatMessage({ id: 'sys.domain.update.field.adminHomeExtraProps', defaultMessage: '扩展参数' })}
          labelCol={{ span: 3 }}
          wrapperCol={{ span: 21 }}
        >
          <Input.TextArea
            rows={3}
            placeholder={intl.formatMessage({ id: 'sys.domain.update.ph.adminHomeExtraProps', defaultMessage: '{}' })}
          />
        </Form.Item>
      ) : null}
    </>
  );
}
