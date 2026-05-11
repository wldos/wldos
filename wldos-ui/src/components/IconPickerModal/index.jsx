/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useState } from 'react';
import { Button, Space } from 'antd';
import { useIntl } from 'umi';
import FullscreenModal from '@/components/FullscreenModal';
import IconSelector from '@/components/IconSelector';

const IconPickerModal = ({ visible, onCancel, onOk, value }) => {
  const intl = useIntl();
  const [selectedIcon, setSelectedIcon] = useState(value || {});

  // 当 visible 变化时，更新选中状态
  React.useEffect(() => {
    if (visible) {
      setSelectedIcon(value || {});
    }
  }, [visible, value]);

  const handleOk = () => {
    onOk(selectedIcon);
    onCancel();
  };

  const handleCancel = () => {
    setSelectedIcon(value || {}); // 重置为原值
    onCancel();
  };

  const renderFooter = () => (
    <Space>
      <Button onClick={handleCancel}>
        {intl.formatMessage({ id: 'component.iconPicker.cancel' })}
      </Button>
      <Button type="primary" onClick={handleOk}>
        {intl.formatMessage({ id: 'component.iconPicker.ok' })}
      </Button>
    </Space>
  );

  return (
    <FullscreenModal
      title={intl.formatMessage({ id: 'component.iconPicker.title' })}
      visible={visible}
      onCancel={handleCancel}
      footer={renderFooter()}
      width={600}
      bodyStyle={{
        padding: '24px',
      }}
    >
      <IconSelector
        value={selectedIcon}
        onChange={setSelectedIcon}
      />
    </FullscreenModal>
  );
};

export default IconPickerModal;
