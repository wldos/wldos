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
import CompanyEditForm from './CompanyEditForm';
import SystemCreateForm from './SystemCreateForm';

const CompanyDetail = ({ company, onCompanyUpdate, onSystemAdd, onCompanyDelete }) => {
  const intl = useIntl();
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [systemModalVisible, setSystemModalVisible] = useState(false);
  const [editLoading, setEditLoading] = useState(false);
  const [systemLoading, setSystemLoading] = useState(false);

  const handleEditCompany = () => {
    setEditModalVisible(true);
  };

  const handleAddSystem = () => {
    setSystemModalVisible(true);
  };

  const handleDeleteCompany = async () => {
    try {
      if (onCompanyDelete) {
        await onCompanyDelete(company.id);
        message.success(intl.formatMessage({ id: 'component.organizationTree.company.msg.deleteOk' }));
      } else {
        message.info(intl.formatMessage({ id: 'component.organizationTree.company.msg.deleteTodo' }));
      }
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.company.msg.deleteFail' }));
    }
  };

  const handleEditSubmit = async (values) => {
    setEditLoading(true);
    try {
      if (onCompanyUpdate) {
        await onCompanyUpdate({ id: company.id, ...values });
        setEditModalVisible(false);
      } else {
        setEditModalVisible(false);
      }
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.company.msg.updateFail' }));
    } finally {
      setEditLoading(false);
    }
  };

  const handleSystemSubmit = async (values) => {
    setSystemLoading(true);
    try {
      if (onSystemAdd) {
        await onSystemAdd(company.id, values);
        // 不在这里显示成功消息，由父组件统一处理
        setSystemModalVisible(false);
      } else {
        message.info(intl.formatMessage({ id: 'component.organizationTree.system.msg.addTodo' }));
        setSystemModalVisible(false);
      }
    } catch (error) {
      message.error(intl.formatMessage({ id: 'component.organizationTree.system.msg.addFail' }));
    } finally {
      setSystemLoading(false);
    }
  };

  return (
    <div>
      <Descriptions title={intl.formatMessage({ id: 'component.organizationTree.company.detail.title' })} bordered column={2}>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.company.detail.name' })}>
          {company.comName || company.name || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.company.detail.code' })}>
          {company.comCode || company.code || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.company.detail.desc' })} span={2}>
          {company.comDesc || company.description || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.company.detail.displayOrder' })}>
          {company.displayOrder || '-'}
        </Descriptions.Item>
        <Descriptions.Item label={intl.formatMessage({ id: 'component.organizationTree.company.detail.status' })}>
          <Tag color={company.isValid === '1' ? 'green' : 'red'}>
            {company.isValid === '1'
              ? intl.formatMessage({ id: 'component.organizationTree.common.status.valid' })
              : intl.formatMessage({ id: 'component.organizationTree.common.status.invalid' })}
          </Tag>
        </Descriptions.Item>
      </Descriptions>

      <div style={{ marginTop: 16 }}>
        <Space>
          <Button type="primary" icon={<EditOutlined />} onClick={handleEditCompany}>
            {intl.formatMessage({ id: 'component.organizationTree.company.action.edit' })}
          </Button>
          <Button icon={<PlusOutlined />} onClick={handleAddSystem}>
            {intl.formatMessage({ id: 'component.organizationTree.system.action.add' })}
          </Button>
          <Popconfirm
            title={intl.formatMessage({ id: 'component.organizationTree.company.confirm.deleteTitle' })}
            description={intl.formatMessage({ id: 'component.organizationTree.common.confirm.deleteDesc' })}
            onConfirm={handleDeleteCompany}
          >
            <Button danger icon={<DeleteOutlined />}>
              {intl.formatMessage({ id: 'component.organizationTree.company.action.delete' })}
            </Button>
          </Popconfirm>
        </Space>
      </div>

      {/* 编辑公司模态框 */}
      <Modal
        title={intl.formatMessage({ id: 'component.organizationTree.company.modal.editTitle' })}
        open={editModalVisible}
        onCancel={() => setEditModalVisible(false)}
        footer={null}
        width={600}
      >
        <CompanyEditForm
          company={company}
          onSubmit={handleEditSubmit}
          loading={editLoading}
        />
      </Modal>

      {/* 添加体系模态框 */}
      <Modal
        title={intl.formatMessage({ id: 'component.organizationTree.system.modal.addTitle' })}
        open={systemModalVisible}
        onCancel={() => setSystemModalVisible(false)}
        footer={null}
        width={600}
      >
        <SystemCreateForm
          companyId={company.id}
          onSubmit={handleSystemSubmit}
          loading={systemLoading}
        />
      </Modal>
    </div>
  );
};

export default CompanyDetail;
