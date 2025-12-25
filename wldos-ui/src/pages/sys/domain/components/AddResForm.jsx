import React from 'react';
import FullscreenModal from '@/components/FullscreenModal';
import DomResList from "@/pages/sys/domain/components/domainRes/DomainRes";

const AddResForm = (props) => {
  const {
    onCancel: handleAddResModalVisible,
    addResModalVisible,
    values,
    addRes
  } = props;

  return (
    <FullscreenModal
      width={"fit-content"}
      bodyStyle={{
        padding: '24px'
      }}
      destroyOnClose
      visible={addResModalVisible}
      title={values.siteName}
      footer={null}
      onCancel={() => handleAddResModalVisible()}
    >
      <DomResList  domainId={values.id} addRes={addRes}/>
    </FullscreenModal>
  );
};

export default AddResForm;
