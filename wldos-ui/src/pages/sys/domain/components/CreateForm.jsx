import React, { useRef } from 'react';
import { Space, Button } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import FullscreenModal from '@/components/FullscreenModal';

const CreateForm = (props) => {
  const { modalVisible, onCancel, children } = props;
  const formRef = useRef();
  
  const handleSubmit = () => {
    formRef.current?.submit?.();
  };
  
  const renderFooter = () => (
    <Space>
      <Button onClick={() => onCancel()}>取消</Button>
      <Button type="primary" icon={<PlusOutlined />} onClick={handleSubmit}>
        创建站点
      </Button>
    </Space>
  );

  return (
    <FullscreenModal
      width={800}
      bodyStyle={{
        padding: '24px',
      }}
      destroyOnClose
      title={
        <Space>
          <PlusOutlined style={{ color: '#52c41a' }} />
          新增域名
        </Space>
      }
      visible={modalVisible}
      onCancel={() => onCancel()}
      footer={renderFooter()}
    >
      {React.cloneElement(children, { ref: formRef })}
    </FullscreenModal>
  );
};

export default CreateForm;
