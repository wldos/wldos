import React from 'react';
import FullscreenModal from '@/components/FullscreenModal';
import AdminList from "@/pages/sys/com/components/AdminList";

const AddAdminForm = (props) => {
  const {
    onCancel: handleAddAdminModalVisible,
    addAdminModalVisible,
    values,
    addTAdmin
  } = props;


  return (
    <FullscreenModal
      width={"fit-content"}
      bodyStyle={{
        padding: '24px'
      }}
      destroyOnClose
      visible={addAdminModalVisible}
      title={values.comName}
      footer={null}
      onCancel={() => handleAddAdminModalVisible()}
    >
      <AdminList userComId={values.id} addTAdmin={addTAdmin}/>
    </FullscreenModal>
  );
};

export default AddAdminForm;
