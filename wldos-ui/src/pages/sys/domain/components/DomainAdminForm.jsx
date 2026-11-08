import React from 'react';
import FullscreenModal from '@/components/FullscreenModal';
import DomainAdminList from "./DomainAdminList";

const DomainAdminForm = (props) => {
  const {
    onCancel: handleAdminModalVisible,
    adminModalVisible,
    values,
    bindAdmins,
  } = props;

  return (
    <FullscreenModal
      width={"fit-content"}
      bodyStyle={{
        padding: '24px'
      }}
      destroyOnClose
      visible={adminModalVisible}
      title={values.siteName}
      footer={null}
      onCancel={() => handleAdminModalVisible()}
    >
      <DomainAdminList domainId={values.id} comId={values.comId} bindAdmins={bindAdmins}/>
    </FullscreenModal>
  );
};

export default DomainAdminForm;
