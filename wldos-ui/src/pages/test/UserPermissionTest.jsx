import React, { useState } from 'react';
import { Button, Card, Space } from 'antd';
import { EyeOutlined } from '@ant-design/icons';
import UserPermissionView from '@/components/UserPermissionView';

const UserPermissionTest = () => {
  const [modalVisible, setModalVisible] = useState(false);

  const testUser = {
    id: '1', // 实际用户ID
    nickname: '龙神',
    account: 'admin'
  };

  return (
    <div style={{ padding: 24 }}>
      <Card title="用户权限查看功能测试">
        <Space>
          <Button 
            type="primary" 
            icon={<EyeOutlined />}
            onClick={() => setModalVisible(true)}
          >
            测试权限查看
          </Button>
        </Space>
        
        <div style={{ marginTop: 16 }}>
          <h3>测试说明：</h3>
          <ul>
            <li>点击"测试权限查看"按钮打开权限查看弹窗</li>
            <li>权限查看组件会调用 /user/currentUser 和 /admin/sys/user/adminMenu 接口</li>
            <li>展示用户端权限和管理端权限</li>
            <li>支持树形菜单权限展示</li>
          </ul>
        </div>
      </Card>

      <UserPermissionView
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        userId={testUser.id}
        userName={testUser.nickname}
      />
    </div>
  );
};

export default UserPermissionTest;
