/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import React, { useState } from 'react';
import { Button, Space } from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import IconSelector from '@/components/IconSelector';

const IconPickerModal = ({ visible, onCancel, onOk, value }) => {
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
        取消
      </Button>
      <Button type="primary" onClick={handleOk}>
        确定
      </Button>
    </Space>
  );

  return (
    <FullscreenModal
      title="选择图标"
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
