import React, { useState } from 'react';
import { Descriptions, Tag, Button, Space, Modal, message, Popconfirm } from 'antd';
import { EditOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import SystemEditForm from './SystemEditForm';
import OrganizationCreateForm from './OrganizationCreateForm';

const SystemDetail = ({ system, companyName, onSystemUpdate, onOrganizationAdd, onSystemDelete }) => {
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [orgModalVisible, setOrgModalVisible] = useState(false);
  const [editLoading, setEditLoading] = useState(false);
  const [orgLoading, setOrgLoading] = useState(false);

  const handleEditSystem = () => {
    setEditModalVisible(true);
  };

  const handleAddOrganization = () => {
    setOrgModalVisible(true);
  };

  const handleDeleteSystem = async () => {
    try {
      if (onSystemDelete) {
        await onSystemDelete(system.id);
        message.success('体系删除成功');
      } else {
        message.info('删除体系功能待实现');
      }
    } catch (error) {
      message.error('删除体系失败');
    }
  };

  const handleEditSubmit = async (values) => {
    setEditLoading(true);
    try {
      if (onSystemUpdate) {
        await onSystemUpdate(system.id, values);
        // 不在这里显示成功消息，由父组件统一处理
        setEditModalVisible(false);
      } else {
        message.info('更新体系信息功能待实现');
        setEditModalVisible(false);
      }
    } catch (error) {
      message.error('更新体系信息失败');
    } finally {
      setEditLoading(false);
    }
  };

  const handleOrgSubmit = async (values) => {
    setOrgLoading(true);
    try {
      if (onOrganizationAdd) {
        await onOrganizationAdd(system.id, values);
        message.success('机构添加成功');
        setOrgModalVisible(false);
      } else {
        message.info('添加机构功能待实现');
        setOrgModalVisible(false);
      }
    } catch (error) {
      message.error('添加机构失败');
    } finally {
      setOrgLoading(false);
    }
  };

  return (
    <div>
      <Descriptions title="体系信息" bordered column={2}>
        <Descriptions.Item label="体系名称">{system.archName || '-'}</Descriptions.Item>
        <Descriptions.Item label="体系编码">{system.archCode || '-'}</Descriptions.Item>
        <Descriptions.Item label="归属公司">{companyName || '-'}</Descriptions.Item>
        <Descriptions.Item label="展示顺序">{system.displayOrder || '-'}</Descriptions.Item>
        <Descriptions.Item label="描述" span={2}>
          {system.archDesc || system.description || '-'}
        </Descriptions.Item>
        <Descriptions.Item label="状态">
          <Tag color={system.isValid === '1' ? 'green' : 'red'}>
            {system.isValid === '1' ? '有效' : '无效'}
          </Tag>
        </Descriptions.Item>
      </Descriptions>
      
      <div style={{ marginTop: 16 }}>
        <Space>
          <Button type="primary" icon={<EditOutlined />} onClick={handleEditSystem}>
            编辑体系
          </Button>
          <Button icon={<PlusOutlined />} onClick={handleAddOrganization}>
            添加机构
          </Button>
          <Popconfirm
            title="确定要删除这个体系吗？"
            description="删除后将无法恢复，请谨慎操作。"
            onConfirm={handleDeleteSystem}
            okText="确定"
            cancelText="取消"
          >
            <Button danger icon={<DeleteOutlined />}>
              删除体系
            </Button>
          </Popconfirm>
        </Space>
      </div>

      {/* 编辑体系模态框 */}
      <Modal
        title="编辑体系"
        open={editModalVisible}
        onCancel={() => setEditModalVisible(false)}
        footer={null}
        width={600}
      >
        <SystemEditForm
          system={system}
          onSubmit={handleEditSubmit}
          loading={editLoading}
        />
      </Modal>

      {/* 添加机构模态框 */}
      <Modal
        title="添加机构"
        open={orgModalVisible}
        onCancel={() => setOrgModalVisible(false)}
        footer={null}
        width={600}
      >
        <OrganizationCreateForm
          systemId={system.id}
          companyId={system.comId}
          onSubmit={handleOrgSubmit}
          loading={orgLoading}
        />
      </Modal>
    </div>
  );
};

export default SystemDetail;
