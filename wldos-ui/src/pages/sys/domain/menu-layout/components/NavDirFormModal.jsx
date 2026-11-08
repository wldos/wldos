import React from 'react';
import { Button, Card, Form, Space, Tooltip } from 'antd';
import { FolderOutlined, InfoCircleOutlined, PlusOutlined } from '@ant-design/icons';
import FullscreenModal from '@/components/FullscreenModal';
import NavDirFormFields from './NavDirFormFields';

const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};

const NavDirFormModal = ({
  modalVisible,
  parentOptions,
  submitting,
  onCancel,
  onSubmit,
}) => {
  const [form] = Form.useForm();

  const handleCancel = () => {
    form.resetFields();
    onCancel();
  };

  const handleSubmit = async () => {
    const values = await form.validateFields();
    onSubmit(values);
  };

  const renderFooter = () => (
    <Space>
      <Button onClick={handleCancel}>取消</Button>
      <Button type="primary" loading={submitting} onClick={handleSubmit}>
        提交
      </Button>
    </Space>
  );

  return (
    <FullscreenModal
      width={800}
      bodyStyle={{ maxHeight: '70vh', overflowY: 'auto', padding: '24px' }}
      destroyOnClose
      title={(
        <Space>
          <PlusOutlined style={{ color: '#1890ff' }} />
          新建站点菜单分组
        </Space>
      )}
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={handleCancel}
    >
      <Form {...formLayout} form={form}>
        <Card
          title={(
            <Space>
              <FolderOutlined style={{ color: '#1890ff' }} />
              分组信息
            </Space>
          )}
          size="small"
          extra={(
            <Tooltip title="本站自建的侧栏分组">
              <InfoCircleOutlined />
            </Tooltip>
          )}
        >
          <NavDirFormFields parentOptions={parentOptions} />
        </Card>
      </Form>
    </FullscreenModal>
  );
};

export default NavDirFormModal;
