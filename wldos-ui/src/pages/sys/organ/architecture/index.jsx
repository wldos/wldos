import React, { useState } from 'react';
import { Row, Col, Card, Button, Space, message } from 'antd';
import { ApartmentOutlined, ReloadOutlined } from '@ant-design/icons';
import OrganizationTree from '@/components/OrganizationTree/OrganizationTree';
import OrganizationDetail from '@/components/OrganizationTree/OrganizationDetail';
import {
  updateCompany,
  addSystem,
  deleteCompany,
  updateSystem,
  addOrganization,
  deleteSystem,
  updateOrganization,
  addUserToOrganization,
  updateOrganizationPermissions,
  deleteOrganization,
  getOrganizationArchitecture,
  getCompanyById,
} from '@/services/organization';

const OrganizationArchitecture = () => {
  const [selectedNode, setSelectedNode] = useState(null);
  const [selectedKey, setSelectedKey] = useState(null);
  const [showUsers, setShowUsers] = useState(false);
  const [treeData, setTreeData] = useState([]);

  // 统一判断本系统 API 成功规则（兼容历史字段）
  const isOk = (resp) => !!(resp && (
    resp.success || resp.code === 200
  ));

  const handleSelect = (keys, { node }) => {
    setSelectedKey(keys[0]);
    setSelectedNode(node.node);
  };

  const handleRefresh = () => {
    setSelectedNode(null);
    setSelectedKey(null);
    // 触发树形组件重新加载数据，避免全页面刷新
    setTreeData([]); // 清空数据触发重新加载
  };

  const handleTreeDataChange = (data) => {
    setTreeData(data);
  };

  // 公司管理回调函数
  // 参照“首页为容器、子页面处理交互”的约定：父级负责数据更新，交互消息与异常在子组件内处理
  const handleCompanyUpdate = async (payload) => {
    try {
      const response = await updateCompany(payload);
      if (!isOk(response)) {
        message.error((response && (response.message || response.msg)) || '公司信息更新失败');
        return;
      }

      // 1) 回查公司详情，刷新右侧详情
      const latest = await getCompanyById(payload.id);
      if (latest) {
        setSelectedNode((prev) => {
          if (!prev) return prev;
          if (prev.id !== payload.id && prev.comId !== payload.id) return prev;
          return { ...prev, ...latest, comName: latest.comName || latest.name };
        });
      }

      // 2) 回查组织架构数据，刷新左侧树（保持一致性）
      const arch = await getOrganizationArchitecture();
      if (arch && arch.data) {
        setTreeData(arch.data);
      }

      // 统一在此处提示一次（事务闭环执行完毕后提示）
      message.success('公司信息更新成功');
    } catch (error) {
      console.error('更新公司失败:', error);
      message.error('公司信息更新失败');
    }
  };

  const handleSystemAdd = async (companyId, values) => {
    try {
      const response = await addSystem({
        comId: companyId,
        ...values
      });
      if (isOk(response)) {
        message.success('体系添加成功');

        // 清空数据触发树形组件重新加载
        setTreeData([]);
      } else {
        message.error((response && (response.message || response.msg)) || '体系添加失败');
      }
    } catch (error) {
      console.error('添加体系失败:', error);
      message.error('体系添加失败');
    }
  };

  const handleCompanyDelete = async (companyId) => {
    try {
      const response = await deleteCompany({ id: companyId });
      if (isOk(response)) {
        message.success('公司删除成功');
        // 刷新树形数据
        setTreeData([]); // 清空数据触发重新加载
      } else {
        message.error((response && (response.message || response.msg)) || '公司删除失败');
      }
    } catch (error) {
      console.error('删除公司失败:', error);
      message.error('公司删除失败');
    }
  };

  // 体系管理回调函数
  const handleSystemUpdate = async (systemId, values) => {
    try {
      const response = await updateSystem({
        id: systemId,
        ...values
      });
      if (isOk(response)) {
        // 1) 更新当前选中的节点详情
        setSelectedNode((prev) => {
          if (!prev || prev.id !== systemId) return prev;
          return { ...prev, ...values };
        });

        // 2) 刷新树形数据
        setTreeData([]); // 清空数据触发重新加载

        // 统一在此处提示一次
        message.success('体系信息更新成功');
      } else {
        message.error((response && (response.message || response.msg)) || '体系信息更新失败');
      }
    } catch (error) {
      console.error('更新体系失败:', error);
      message.error('体系信息更新失败');
    }
  };

  const handleOrganizationAdd = async (systemId, values) => {
    try {
      const response = await addOrganization({
        archId: systemId,
        ...values
      });
      if (isOk(response)) {
        message.success('机构添加成功');
        // 刷新树形数据
        setTreeData([]); // 清空数据触发重新加载
      } else {
        message.error((response && (response.message || response.msg)) || '机构添加失败');
      }
    } catch (error) {
      console.error('添加机构失败:', error);
      message.error('机构添加失败');
    }
  };

  const handleSystemDelete = async (systemId) => {
    try {
      const response = await deleteSystem({ id: systemId });
      if (isOk(response)) {
        message.success('体系删除成功');
        // 刷新树形数据
        setTreeData([]); // 清空数据触发重新加载
      } else {
        message.error((response && (response.message || response.msg)) || '体系删除失败');
      }
    } catch (error) {
      console.error('删除体系失败:', error);
      message.error('体系删除失败');
    }
  };

  // 机构管理回调函数
  const handleOrganizationUpdate = async (orgId, values) => {
    try {
      const response = await updateOrganization({
        id: orgId,
        ...values
      });
      if (isOk(response)) {
        message.success('机构信息更新成功');
        // 刷新树形数据
        setTreeData([]); // 清空数据触发重新加载
      } else {
        message.error((response && (response.message || response.msg)) || '机构信息更新失败');
      }
    } catch (error) {
      console.error('更新机构失败:', error);
      message.error('机构信息更新失败');
    }
  };

  const handleUserAdd = async (orgId, values) => {
    try {
      const response = await addUserToOrganization({
        organizationId: orgId,
        ...values
      });
      if (isOk(response)) {
        message.success('用户添加成功');
        // 刷新用户列表
        setShowUsers(false);
        setShowUsers(true);
      } else {
        message.error((response && (response.message || response.msg)) || '用户添加失败');
      }
    } catch (error) {
      console.error('添加用户失败:', error);
      message.error('用户添加失败');
    }
  };

  const handlePermissionSettings = async (permissionData) => {
    try {
      const response = await updateOrganizationPermissions(permissionData);
      if (isOk(response)) {
        message.success('权限设置保存成功');
      } else {
        message.error((response && (response.message || response.msg)) || '权限设置保存失败');
      }
    } catch (error) {
      console.error('权限设置失败:', error);
      message.error('权限设置保存失败');
    }
  };

  const handleOrganizationDelete = async (orgId) => {
    try {
      const response = await deleteOrganization({ id: orgId });
      if (isOk(response)) {
        message.success('机构删除成功');
        // 刷新树形数据
        setTreeData([]); // 清空数据触发重新加载
      } else {
        message.error((response && (response.message || response.msg)) || '机构删除失败');
      }
    } catch (error) {
      console.error('删除机构失败:', error);
      message.error('机构删除失败');
    }
  };

  return (
    <div style={{ height: '100vh', padding: '16px' }}>
      <Card
        title={
          <Space>
            <ApartmentOutlined />
            组织架构管理
          </Space>
        }
        extra={
          <Space>
            <Button
              icon={<ReloadOutlined />}
              onClick={handleRefresh}
            >
              刷新
            </Button>
          </Space>
        }
        style={{ height: '100%' }}
        bodyStyle={{ height: 'calc(100% - 57px)', padding: 0 }}
      >
        <Row gutter={16} style={{ height: '100%' }}>
          <Col span={8} style={{ height: '100%' }}>
            <Card
              title="组织架构"
              size="small"
              style={{ height: '100%' }}
              bodyStyle={{ height: 'calc(100% - 40px)', overflow: 'auto' }}
            >
              <OrganizationTree
                onSelect={handleSelect}
                selectedKey={selectedKey}
                showUsers={showUsers}
                onTreeDataChange={handleTreeDataChange}
                externalTreeData={treeData}
              />
            </Card>
          </Col>
          <Col span={16} style={{ height: '100%' }}>
            <Card
              title="详情管理"
              size="small"
              style={{ height: '100%' }}
              bodyStyle={{ height: 'calc(100% - 40px)', overflow: 'auto' }}
            >
              <OrganizationDetail
                selectedNode={selectedNode}
                treeData={treeData}
                onCompanyUpdate={handleCompanyUpdate}
                onSystemAdd={handleSystemAdd}
                onCompanyDelete={handleCompanyDelete}
                onSystemUpdate={handleSystemUpdate}
                onOrganizationAdd={handleOrganizationAdd}
                onSystemDelete={handleSystemDelete}
                onOrganizationUpdate={handleOrganizationUpdate}
                onUserAdd={handleUserAdd}
                onPermissionSettings={handlePermissionSettings}
                onOrganizationDelete={handleOrganizationDelete}
              />
            </Card>
          </Col>
        </Row>
      </Card>
    </div>
  );
};

export default OrganizationArchitecture;
