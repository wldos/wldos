import React from 'react';
import FullscreenModal from '@/components/FullscreenModal';
import AppList from "@/pages/sys/app";

const AddAppForm = (props) => {
  const {
    onCancel: handleAddAppModalVisible,
    addAppModalVisible,
    values,
    addApp
  } = props;

  return (
    <FullscreenModal
      width={"fit-content"}
      bodyStyle={{
        padding: '24px'
      }}
      destroyOnClose
      visible={addAppModalVisible}
      title={values.siteName}
      footer={null}
      onCancel={() => handleAddAppModalVisible()}
    >
      <AppList  domainId={values.id} comId={values.comId} addApp={addApp}/>
    </FullscreenModal>
  );
};

export default AddAppForm;
