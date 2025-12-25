import React from 'react';
import { Space } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import FullscreenModal from '@/components/FullscreenModal';

const CreateForm = (props) => {
  const { modalVisible, onCancel } = props;
  return (
    <FullscreenModal
      destroyOnClose
      title={
        <Space>
          <PlusOutlined style={{ color: '#1890ff' }} />
          新建角色
        </Space>
      }
      visible={modalVisible}
      onCancel={() => onCancel()}
      footer={null}
      width={800}
      bodyStyle={{
        maxHeight: '70vh',
        overflowY: 'auto',
        padding: '24px'
      }}
    >
      {props.children}
    </FullscreenModal>
  );
};

export default CreateForm;
