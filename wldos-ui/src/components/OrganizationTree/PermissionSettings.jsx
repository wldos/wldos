import React, { useState, useEffect } from 'react';
import { Form, Tree, Button, Space, message } from 'antd';

const PermissionSettings = ({ organization, onSave }) => {
  const [form] = Form.useForm();
  const [treeData, setTreeData] = useState([]);
  const [checkedKeys, setCheckedKeys] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // 模拟权限数据
    const mockPermissions = [
      {
        title: '系统管理',
        key: 'system',
        children: [
          { title: '用户管理', key: 'system:user' },
          { title: '角色管理', key: 'system:role' },
          { title: '权限管理', key: 'system:permission' }
        ]
      },
      {
        title: '内容管理',
        key: 'content',
        children: [
          { title: '文章管理', key: 'content:article' },
          { title: '分类管理', key: 'content:category' },
          { title: '标签管理', key: 'content:tag' }
        ]
      },
      {
        title: '组织管理',
        key: 'organization',
        children: [
          { title: '公司管理', key: 'organization:company' },
          { title: '体系管理', key: 'organization:system' },
          { title: '机构管理', key: 'organization:org' }
        ]
      }
    ];
    setTreeData(mockPermissions);
  }, []);

  const handleSubmit = async (values) => {
    setLoading(true);
    try {
      const permissionData = {
        organizationId: organization.id,
        permissions: checkedKeys,
        ...values
      };
      
      if (onSave) {
        await onSave(permissionData);
        message.success('权限设置保存成功');
      } else {
        message.info('权限设置功能待实现');
      }
    } catch (error) {
      message.error('权限设置保存失败');
    } finally {
      setLoading(false);
    }
  };

  const onCheck = (checkedKeysValue) => {
    setCheckedKeys(checkedKeysValue);
  };

  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={handleSubmit}
    >
      <Form.Item label="权限配置">
        <Tree
          checkable
          onCheck={onCheck}
          checkedKeys={checkedKeys}
          treeData={treeData}
          style={{ maxHeight: 400, overflow: 'auto' }}
        />
      </Form.Item>

      <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
        <Space>
          <Button onClick={() => form.resetFields()}>
            重置
          </Button>
          <Button type="primary" htmlType="submit" loading={loading}>
            保存权限
          </Button>
        </Space>
      </Form.Item>
    </Form>
  );
};

export default PermissionSettings;
