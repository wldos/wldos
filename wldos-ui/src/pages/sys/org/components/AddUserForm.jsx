import React from 'react';
import FullscreenModal from '@/components/FullscreenModal';
import UserList from "@/pages/sys/user";

const AddUserForm = (props) => {
  const {
    onCancel: handleAddUserModalVisible,
    addUserModalVisible,
    values,
    addUser
  } = props;


  return (
    <FullscreenModal
      width={"fit-content"}
      bodyStyle={{
        padding: '24px',
      }}
      destroyOnClose
      visible={addUserModalVisible}
      title={values.orgName}
      footer={null}
      onCancel={() => handleAddUserModalVisible()}
    >
      <UserList  orgId={values.id} archId={values.archId} comId={values.comId} addUser={addUser}/>
    </FullscreenModal>
  );
};

export default AddUserForm;
