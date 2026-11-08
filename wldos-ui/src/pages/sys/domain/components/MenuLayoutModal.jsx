import React from 'react';
import FullscreenModal from '@/components/FullscreenModal';
import MenuLayoutPanel from '@/pages/sys/domain/menu-layout/MenuLayoutPanel';

const MenuLayoutModal = (props) => {
  const {
    onCancel,
    modalVisible,
    values,
  } = props;

  return (
    <FullscreenModal
      width="fit-content"
      bodyStyle={{ padding: '24px' }}
      destroyOnClose
      visible={modalVisible}
      title={values?.siteName ? `菜单布局 - ${values.siteName}` : '菜单布局'}
      footer={null}
      onCancel={onCancel}
    >
      {values?.id ? <MenuLayoutPanel domainId={values.id} /> : null}
    </FullscreenModal>
  );
};

export default MenuLayoutModal;
