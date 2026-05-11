/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useState } from 'react';
import { Descriptions, Tag, Button, Space, Modal, message, Popconfirm } from 'antd';
import { useIntl } from 'umi';
import { EditOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import SystemEditForm from './SystemEditForm';
import OrganizationCreateForm from './OrganizationCreateForm';

const SystemDetail = ({ system, companyName, onSystemUpdate, onOrganizationAdd, onSystemDelete }) => {
  const intl = useIntl();
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
        message.success(intl.formatMessage({ id: 'component.organizationTree.system.msg.deleteOk' }));
      } else {
        message.info(intl.formatMessage({ id: 'component.organizationTree.system.msg.deleteTodo' }));
      }
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.system.msg.deleteFail' }));
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
        message.info(intl.formatMessage({ id: 'component.organizationTree.system.msg.updateTodo' }));
        setEditModalVisible(false);
      }
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.system.msg.updateFail' }));
    } finally {
      setEditLoading(false);
    }
  };

  const handleOrgSubmit = async (values) => {
    setOrgLoading(true);
    try {
      if (onOrganizationAdd) {
        await onOrganizationAdd(system.id, values);
        message.success(intl.formatMessage({ id: 'component.organizationTree.org.msg.addOk' }));
        setOrgModalVisible(false);
      } else {
        message.info(intl.formatMessage({ id: 'component.organizationTree.org.msg.addTodo' }));
        setOrgModalVisible(false);
      }
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.org.msg.addFail' }));
    } finally {
      setOrgLoading(false);
    }
  };

  return (
    <div>
      <Descriptions title={intl.formatMessage({ id: 'component.organizationTree.system.detail.title' })} bordered column={2}>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.system.detail.name' })}>
          {system.archName || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.system.detail.code' })}>
          {system.archCode || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.system.detail.company' })}>
          {companyName || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.system.detail.displayOrder' })}>
          {system.displayOrder || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.system.detail.desc' })} span={2}>
          {system.archDesc || system.description || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.system.detail.status' })}>
          <Tag color={system.isValid === '1' ? 'green' : 'red'}>
            {system.isValid === '1'
              ? intl.formatMessage({ id: 'component.organizationTree.common.status.valid' })
              : intl.formatMessage({ id: 'component.organizationTree.common.status.invalid' })}
          </Tag>
        </Descriptions.Item>
      </Descriptions>

      <div style={{ marginTop: 16 }}>
        <Space>
          <Button type="primary" icon={<EditOutlined />} onClick={handleEditSystem}>
            {intl.formatMessage({ id: 'component.organizationTree.system.action.edit' })}
          </Button>
          <Button icon={<PlusOutlined />} onClick={handleAddOrganization}>
            {intl.formatMessage({ id: 'component.organizationTree.org.action.add' })}
          </Button>
          <Popconfirm
            title={intl.formatMessage({ id: 'component.organizationTree.system.confirm.deleteTitle' })}
            description={intl.formatMessage({ id: 'component.organizationTree.common.confirm.deleteDesc' })}
            onConfirm={handleDeleteSystem}
          >
            <Button danger icon={<DeleteOutlined />}>
              {intl.formatMessage({ id: 'component.organizationTree.system.action.delete' })}
            </Button>
          </Popconfirm>
        </Space>
      </div>

      {/* 编辑体系模态框 */}
      <Modal
        title={intl.formatMessage({ id: 'component.organizationTree.system.modal.editTitle' })}
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
        title={intl.formatMessage({ id: 'component.organizationTree.org.modal.addTitle' })}
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
