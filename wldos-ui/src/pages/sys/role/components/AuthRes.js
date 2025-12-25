import React, {useState} from 'react'
import {Button, Tree} from 'antd';
import FullscreenModal from '@/components/FullscreenModal';

const AuthRes = (props) => {
  const {
    onSubmit: handleAuth,
    onCancel: handleAuthModalVisible,
    authModalVisible,
    existRes,
    res,
    values,
  } = props;

  const [expandedKeys, setExpandedKeys] = useState(['0']);
  const [checkedKeys, setCheckedKeys] = useState(existRes);
  // const [selectedKeys, setSelectedKeys] = useState([]);
  const [autoExpandParent, setAutoExpandParent] = useState(true);

  const onExpand = (expandedKeysValue) => {

    setExpandedKeys(expandedKeysValue);
    setAutoExpandParent(false);
  };

  const onCheck = (checkedKeysValue) => {
    setCheckedKeys(checkedKeysValue.checked);
  };
  /*
  const onSelect = (selectedKeysValue, info) => {
    console.log('onselect=', selectedKeysValue, selectedKeys);
    setSelectedKeys(selectedKeysValue);
  }; */

  const handleAddRes = () => {
    handleAuth({resIds: checkedKeys, roleId: values.id});
  }

  const renderFooter = () =>
    (
      <>
        <Button onClick={() => handleAuthModalVisible(false, values)}>取消</Button>
        <Button type="primary" onClick={() => handleAddRes()}>授权</Button>
      </>
    );

  return (
    <FullscreenModal
      width={800}
      bodyStyle={{
        padding: '24px'
      }}
      destroyOnClose
      title="角色授权"
      visible={authModalVisible}
      footer={renderFooter()}
      onCancel={() => handleAuthModalVisible()}
    >
      <Tree
        checkable
        showLine
        checkStrictly
        onExpand={onExpand}
        expandedKeys={expandedKeys}
        autoExpandParent={autoExpandParent}
        onCheck={onCheck}
        checkedKeys={checkedKeys}
        // onSelect={onSelect}
        // selectedKeys={selectedKeys}
        treeData={res}
      />
    </FullscreenModal>
  );
}

export default AuthRes;
