import React, { useRef } from 'react';
import { Space, Button } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import FullscreenModal from '@/components/FullscreenModal';

const CreateForm = (props) => {
  const { modalVisible, onCancel } = props;
  const formRef = useRef();
  
  const handleSubmit = () => {
    // 通过 ref 获取表单实例，调用表单的 submit 方法
    // 表单的 submit 方法会触发 Form 的 onFinish 事件
    if (formRef.current) {
      formRef.current.submit();
    }
  };
  
  const renderFooter = () => (
    <Space>
      <Button onClick={() => onCancel()}>取消</Button>
      <Button type="primary" icon={<PlusOutlined />} onClick={handleSubmit}>
        创建应用
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
          新建应用
        </Space>
      }
      visible={modalVisible}
      onCancel={() => onCancel()}
      footer={renderFooter()}
    >
      {React.cloneElement(props.children, { ref: formRef })}
    </FullscreenModal>
  );
};

export default CreateForm;
