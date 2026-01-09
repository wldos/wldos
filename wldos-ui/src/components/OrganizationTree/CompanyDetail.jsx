/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useState } from 'react';
import { Descriptions, Tag, Button, Space, Modal, message, Popconfirm } from 'antd';
import { EditOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import CompanyEditForm from './CompanyEditForm';
import SystemCreateForm from './SystemCreateForm';

const CompanyDetail = ({ company, onCompanyUpdate, onSystemAdd, onCompanyDelete }) => {
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
        message.success('公司删除成功');
      } else {
        message.info('删除公司功能待实现');
      }
    } catch (error) {
      message.error('删除公司失败');
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
      message.error('更新公司信息失败');
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
        message.info('添加体系功能待实现');
        setSystemModalVisible(false);
      }
    } catch (error) {
      message.error('添加体系失败');
    } finally {
      setSystemLoading(false);
    }
  };

  return (
    <div>
      <Descriptions title="公司信息" bordered column={2}>
        <Descriptions.Item label="公司名称">{company.comName || company.name || '-'}</Descriptions.Item>
        <Descriptions.Item label="公司编码">{company.comCode || company.code || '-'}</Descriptions.Item>
        <Descriptions.Item label="描述" span={2}>
          {company.comDesc || company.description || '-'}
        </Descriptions.Item>
        <Descriptions.Item label="展示顺序">{company.displayOrder || '-'}</Descriptions.Item>
        <Descriptions.Item label="状态">
          <Tag color={company.isValid === '1' ? 'green' : 'red'}>
            {company.isValid === '1' ? '有效' : '无效'}
          </Tag>
        </Descriptions.Item>
      </Descriptions>

      <div style={{ marginTop: 16 }}>
        <Space>
          <Button type="primary" icon={<EditOutlined />} onClick={handleEditCompany}>
            编辑公司
          </Button>
          <Button icon={<PlusOutlined />} onClick={handleAddSystem}>
            添加体系
          </Button>
          <Popconfirm
            title="确定要删除这个公司吗？"
            description="删除后将无法恢复，请谨慎操作。"
            onConfirm={handleDeleteCompany}
            okText="确定"
            cancelText="取消"
          >
            <Button danger icon={<DeleteOutlined />}>
              删除公司
            </Button>
          </Popconfirm>
        </Space>
      </div>

      {/* 编辑公司模态框 */}
      <Modal
        title="编辑公司"
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
        title="添加体系"
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
