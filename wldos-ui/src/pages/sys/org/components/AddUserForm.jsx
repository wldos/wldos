import React, { useEffect, useMemo, useState } from 'react';
import { message, Select, Space } from 'antd';
import FullscreenModal from '@/components/FullscreenModal';
import UserList from "@/pages/sys/user";
import {
  filterDomainOptionsForCom,
  isValidScopeComId,
  needsTargetSiteSelection,
  resolveCreateUserDomainId,
  mergeToolbarWriteScope,
  canPickMemberScopeInModal,
} from '@/utils/orgCreateUserDomain';

const AddUserForm = (props) => {
  const {
    onCancel: handleAddUserModalVisible,
    addUserModalVisible,
    values,
    addUser,
    comList = [],
    domainOptions = [],
    allDomainRows = [],
    showDomainSelect,
    effectiveComId,
    listDomainId,
    listComId,
  } = props;

  const isTadmin = values?.orgCode === 'tadmin';
  const isDadmin = values?.orgCode === 'dadmin';
  const needsIdentityExtra = isTadmin || isDadmin;

  const needsOpsScope = useMemo(() => needsTargetSiteSelection(values, {
    showDomainSelect,
    targetDomainId: listDomainId,
    domainOptions,
  }), [values, showDomainSelect, listDomainId, domainOptions]);

  const showModalScopePickers = useMemo(() => (
    needsOpsScope
    && !needsIdentityExtra
    && (canPickMemberScopeInModal(values) || !isValidScopeComId(listComId) || !listDomainId)
  ), [needsOpsScope, needsIdentityExtra, values, listComId, listDomainId]);

  const [identityExtraId, setIdentityExtraId] = useState(undefined);
  const [scopeComId, setScopeComId] = useState(undefined);
  const [scopeDomainId, setScopeDomainId] = useState(undefined);

  useEffect(() => {
    if (!addUserModalVisible) {
      return;
    }
    setIdentityExtraId(undefined);
    setScopeComId(isValidScopeComId(listComId) ? String(listComId) : undefined);
    setScopeDomainId(listDomainId || undefined);
  }, [addUserModalVisible, listComId, listDomainId]);

  const scopeDomainOptions = useMemo(() => {
    if (allDomainRows?.length && isValidScopeComId(scopeComId)) {
      return filterDomainOptionsForCom(allDomainRows, scopeComId);
    }
    if (isValidScopeComId(scopeComId) && domainOptions?.length) {
      return domainOptions;
    }
    return domainOptions || [];
  }, [allDomainRows, scopeComId, domainOptions]);

  const wrappedAddUser = async (val = {}) => {
    if (isTadmin) {
      if (!identityExtraId) {
        message.warning('请选择受托租户（该用户将成为其租户管理员）');
        return false;
      }
      return addUser({ ...val, userComId: identityExtraId });
    }
    if (isDadmin) {
      if (!identityExtraId) {
        message.warning('请选择目标站点（该用户将成为其站点管理员）');
        return false;
      }
      return addUser({ ...val, domainId: identityExtraId });
    }
    if (needsOpsScope) {
      const scoped = mergeToolbarWriteScope(
        { comId: scopeComId, domainId: scopeDomainId },
        { effectiveComId: listComId || effectiveComId, targetDomainId: listDomainId },
      );
      const comId = scoped.comId;
      const domainId = scoped.domainId
        || resolveCreateUserDomainId(values, { targetDomainId: listDomainId, domainOptions });
      if (!isValidScopeComId(comId)) {
        message.warning('请选择目标租户');
        return false;
      }
      if (!domainId) {
        message.warning('请选择目标站点');
        return false;
      }
      return addUser({ ...val, domainId, comId });
    }
    return addUser(val);
  };

  const comSelectOptions = (comList || []).filter((item) => isValidScopeComId(item?.value));

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
      {needsIdentityExtra && (
        <div style={{ marginBottom: 16, minWidth: 360 }}>
          <Select
            style={{ width: '100%' }}
            placeholder={isTadmin ? '请选择受托租户（该用户将成为其租户管理员）' : '请选择目标站点（该用户将成为其站点管理员）'}
            options={isTadmin ? comSelectOptions : domainOptions}
            value={identityExtraId}
            onChange={setIdentityExtraId}
            showSearch
            optionFilterProp="label"
          />
        </div>
      )}
      {showModalScopePickers && (
        <Space direction="vertical" style={{ marginBottom: 16, minWidth: 360, width: '100%' }} size={12}>
          <Select
            style={{ width: '100%' }}
            placeholder="请选择目标租户"
            options={comSelectOptions}
            value={scopeComId}
            onChange={(v) => {
              setScopeComId(v || undefined);
              setScopeDomainId(undefined);
            }}
            showSearch
            optionFilterProp="label"
          />
          <Select
            style={{ width: '100%' }}
            placeholder="请选择目标站点"
            options={scopeDomainOptions}
            value={scopeDomainId}
            onChange={(v) => setScopeDomainId(v || undefined)}
            showSearch
            optionFilterProp="label"
          />
        </Space>
      )}
      <UserList
        orgId={values.id}
        archId={values.archId}
        comId={values.comId && String(values.comId) !== '0' ? values.comId : effectiveComId}
        listDomainId={scopeDomainId || listDomainId}
        listComId={isValidScopeComId(scopeComId) ? scopeComId : listComId}
        addUser={wrappedAddUser}
      />
    </FullscreenModal>
  );
};

export default AddUserForm;
