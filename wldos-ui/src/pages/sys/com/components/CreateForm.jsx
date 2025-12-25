import React from 'react';
import { Space } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import FullscreenModal from '@/components/FullscreenModal';

const CreateForm = (props) => {
  const { modalVisible, onCancel } = props;
  return (
    <FullscreenModal
      width={800}
      bodyStyle={{
        padding: '24px',
        maxHeight: '80vh',
        overflowY: 'auto',
      }}
      destroyOnClose
      title={
        <Space>
          <PlusOutlined style={{ color: '#52c41a' }} />
          新建公司
        </Space>
      }
      visible={modalVisible}
      onCancel={() => onCancel()}
      footer={null}
    >
      {props.children}
    </FullscreenModal>
  );
};

export default CreateForm;
