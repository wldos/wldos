import React from 'react';
import {Button, Form, Input} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';

const FormItem = Form.Item;
const {TextArea} = Input;

const formLayout = {
  labelCol: {
    span: 0,
  },
  wrapperCol: {
    span: 24,
  },
};

const CacheModal = (props) => {
  const [form] = Form.useForm();
  const {
    onSubmit: handleCache,
    onCancel: handleModalVisible,
    modalVisible,
    values,
  } = props;

  const handleNext = async () => {
    const fieldsValue = await form.validateFields();
    const value = {...values, ...fieldsValue};
    handleCache(value);
  };

  const renderContent = () => {
    return (
      <>
        <FormItem
          name="srcCache"
        >
          <TextArea rows={24} readOnly/>
        </FormItem>
      </>
    );
  };

  const renderFooter = () => {

    return (
      <>
        <Button onClick={() => handleModalVisible(false, values)}>关闭</Button>
        {/*<Button type="primary" onClick={() => handleNext()}>提交</Button>*/}
      </>
    );
  };

  return (
    <FullscreenModal
      width={1024}
      bodyStyle={{
        padding: '12px'
      }}
      destroyOnClose
      title="源快照"
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={{
          srcCache: props.values.srcCache,
        }}
      >
        {renderContent()}
      </Form>
    </FullscreenModal>
  );
};

export default CacheModal;
