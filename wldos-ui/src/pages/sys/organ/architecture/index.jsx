import React, { useState, useEffect, useRef, useMemo, useCallback } from 'react';
import { Row, Col, Card, Button, Space, message, AutoComplete } from 'antd';
import { ApartmentOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons';
import { connect, useIntl, useLocation, history } from 'umi';
import { buildAuthorityHelper } from '@/hooks/useAuthority';
import OrganizationTree from '@/components/OrganizationTree/OrganizationTree';
import OrganizationDetail from '@/components/OrganizationTree/OrganizationDetail';
import ComCreateForm from '@/pages/sys/com/components/CreateForm';
import ComCreateFormContent from '@/pages/sys/com/components/CreateFormContent';
import { getComSelectOption } from '@/pages/sys/com/service';
import { getArchList } from '@/pages/sys/arch/service';
import { getOrgList, fetchOrgType } from '@/pages/sys/org/service';
import { queryPage as queryDomainPage } from '@/pages/sys/domain/service';
import {
  updateCompany,
  addCompany,
  addSystem,
  deleteCompany,
  updateSystem,
  addOrganization,
  deleteSystem,
  updateOrganization,
  deleteOrganization,
  getCompanyById,
} from '@/services/organization';
import { canProvisionCompany, filterDomainOptionsForCom } from '@/utils/orgCreateUserDomain';

const SEARCH_RESULT_LIMIT = 20;

const PLATFORM_COM_ID = '0';
const PLATFORM_COM_NAME = '平台';

function buildOrganizationSearchIndex(comOptions, archMetaList, orgMetaList, treeData, typeLabels) {
  const comNameById = new Map();
  comNameById.set(PLATFORM_COM_ID, PLATFORM_COM_NAME);

  (treeData || []).forEach((node) => {
    if (node?.type === 'company' && node.id != null) {
      comNameById.set(String(node.id), node.comName || node.name || comNameById.get(String(node.id)) || '');
    }
  });

  (comOptions || []).forEach((item) => {
    if (item?.value != null) {
      comNameById.set(String(item.value), item.label || comNameById.get(String(item.value)) || '');
    }
  });

  (archMetaList || []).forEach((arch) => {
    if (arch?.comId != null && !comNameById.has(String(arch.comId))) {
      comNameById.set(String(arch.comId), String(arch.comId) === PLATFORM_COM_ID ? PLATFORM_COM_NAME : '');
    }
  });

  const archNameById = new Map();
  (archMetaList || []).forEach((arch) => {
    if (arch?.id != null) {
      archNameById.set(String(arch.id), arch.archName || '');
    }
  });

  const items = [];

  const companyIds = new Set();
  const addCompanyItem = (id, label) => {
    if (id == null || companyIds.has(String(id))) {
      return;
    }
    companyIds.add(String(id));
    const name = label || comNameById.get(String(id)) || '';
    items.push({
      key: `company-${id}`,
      type: 'company',
      id,
      label: name,
      path: name,
      typeLabel: typeLabels.company,
    });
  };

  (treeData || []).forEach((node) => {
    if (node?.type === 'company') {
      addCompanyItem(node.id, node.comName || node.name);
    }
  });

  (comOptions || []).forEach((com) => {
    addCompanyItem(com.value, com.label);
  });

  (archMetaList || []).forEach((arch) => {
    if (arch?.id == null || arch.comId == null) {
      return;
    }
    const comName = comNameById.get(String(arch.comId)) || '';
    const label = arch.archName || '';
    const path = comName ? `${comName} / ${label}` : label;
    items.push({
      key: `system-${arch.id}`,
      type: 'system',
      id: arch.id,
      comId: arch.comId,
      label,
      path,
      typeLabel: typeLabels.system,
    });
  });

  (orgMetaList || []).forEach((org) => {
    if (org?.id == null || org.comId == null || org.archId == null) {
      return;
    }
    const comName = comNameById.get(String(org.comId)) || '';
    const archName = archNameById.get(String(org.archId)) || '';
    const label = org.orgName || '';
    const path = [comName, archName, label].filter(Boolean).join(' / ');
    items.push({
      key: `organization-${org.id}`,
      type: 'organization',
      id: org.id,
      comId: org.comId,
      archId: org.archId,
      label,
      path,
      typeLabel: typeLabels.organization,
    });
  });

  return items;
}

function filterSearchIndex(index, keyword) {
  const q = (keyword || '').trim().toLowerCase();
  if (!q) {
    return [];
  }
  return index
    .filter((item) => `${item.typeLabel} ${item.label} ${item.path}`.toLowerCase().includes(q))
    .slice(0, SEARCH_RESULT_LIMIT);
}

function findTreeNode(nodes, type, id) {
  if (!Array.isArray(nodes) || !nodes.length) {
    return null;
  }
  for (const node of nodes) {
    if (node.type === type && String(node.id) === String(id)) {
      return node;
    }
    const found = findTreeNode(node.children, type, id);
    if (found) {
      return found;
    }
  }
  return null;
}

const OrganizationArchitecture = ({ hasAuthority, isAdmin, isTenantAdmin }) => {
  const canManageCompanyStructure = canProvisionCompany(isAdmin, isTenantAdmin);
  const intl = useIntl();
  const location = useLocation();
  const pendingFocusRef = useRef(null);
  const [selectedNode, setSelectedNode] = useState(null);
  const [selectedKey, setSelectedKey] = useState(null);
  const [showUsers, setShowUsers] = useState(false);
  const [treeData, setTreeData] = useState([]);
  const [createComVisible, setCreateComVisible] = useState(false);
  const [reloadToken, setReloadToken] = useState(0);
  const triggerReload = () => setReloadToken((t) => t + 1);

  const [comOptions, setComOptions] = useState([]);
  const [orgTypeOptions, setOrgTypeOptions] = useState([]);
  const [archOptionsForOrg, setArchOptionsForOrg] = useState([]);
  const [orgOptionsForParent, setOrgOptionsForParent] = useState([]);
  const [orgMetaList, setOrgMetaList] = useState([]);
  const [archMetaList, setArchMetaList] = useState([]);
  const [focusTarget, setFocusTarget] = useState(null);
  const [allDomainRows, setAllDomainRows] = useState([]);
  const [archTargetDomainId, setArchTargetDomainId] = useState(undefined);
  const [archDomainOptions, setArchDomainOptions] = useState([]);
  const [archShowDomainSelect, setArchShowDomainSelect] = useState(false);
  const [searchKeyword, setSearchKeyword] = useState('');

  const typeLabels = useMemo(() => ({
    company: intl.formatMessage({ id: 'component.organizationTree.nodeType.company' }),
    system: intl.formatMessage({ id: 'component.organizationTree.nodeType.system' }),
    organization: intl.formatMessage({ id: 'component.organizationTree.nodeType.organization' }),
  }), [intl]);

  const searchIndex = useMemo(
    () => buildOrganizationSearchIndex(comOptions, archMetaList, orgMetaList, treeData, typeLabels),
    [comOptions, archMetaList, orgMetaList, treeData, typeLabels],
  );

  const searchOptions = useMemo(
    () => filterSearchIndex(searchIndex, searchKeyword).map((item) => ({
      value: item.key,
      label: (
        <div>
          <span style={{ color: '#888', marginRight: 6 }}>{item.typeLabel}</span>
          {item.label}
          {item.path !== item.label ? (
            <div style={{ fontSize: 12, color: '#aaa' }}>{item.path}</div>
          ) : null}
        </div>
      ),
    })),
    [searchIndex, searchKeyword],
  );

  const handleSearchSelect = useCallback((key) => {
    const item = searchIndex.find((entry) => entry.key === key);
    if (!item) {
      return;
    }
    if (item.type === 'company') {
      setFocusTarget({ type: 'company', id: item.id });
    } else if (item.type === 'system') {
      setFocusTarget({ type: 'system', id: item.id, comId: item.comId });
    } else if (item.type === 'organization') {
      setFocusTarget({
        type: 'organization',
        id: item.id,
        comId: item.comId,
        archId: item.archId,
      });
    }
    setSearchKeyword('');
  }, [searchIndex]);

  const refreshOrgParentOptions = async () => {
    const orgRes = await getOrgList();
    const orgArr = orgRes?.data || [];
    setOrgMetaList(orgArr);
    setOrgOptionsForParent(orgArr.map((item) => ({
      label: item.orgName,
      value: item.id,
      archId: item.archId,
      comId: item.comId,
    })));
  };

  const resolvePendingFocus = () => {
    const pending = pendingFocusRef.current;
    if (!pending) {
      return;
    }
    if (pending.focusType === 'organization') {
      const org = orgMetaList.find((item) => String(item.id) === String(pending.focusId));
      if (org?.comId != null && org?.archId != null) {
        setFocusTarget({
          type: 'organization',
          id: org.id,
          comId: org.comId,
          archId: org.archId,
        });
        pendingFocusRef.current = null;
      }
      return;
    }
    if (pending.focusType === 'company') {
      setFocusTarget({ type: 'company', id: pending.focusId });
      pendingFocusRef.current = null;
      return;
    }
    if (pending.focusType === 'system') {
      const arch = archMetaList.find((item) => String(item.id) === String(pending.focusId));
      if (arch?.comId != null) {
        setFocusTarget({
          type: 'system',
          id: arch.id,
          comId: arch.comId,
        });
        pendingFocusRef.current = null;
      }
    }
  };

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const focusType = params.get('focusType');
    const focusId = params.get('focusId');
    if (focusType && focusId) {
      pendingFocusRef.current = { focusType, focusId };
      history.replace(location.pathname);
      resolvePendingFocus();
    }
  }, [location.pathname, location.search]);

  useEffect(() => {
    (async () => {
      const comRes = await getComSelectOption();
      setComOptions(comRes?.data || []);

      const orgTypeRes = await fetchOrgType();
      setOrgTypeOptions(orgTypeRes?.data || []);

      const archRes = await getArchList();
      const archArr = archRes?.data || [];
      setArchMetaList(archArr);
      setArchOptionsForOrg(archArr.map((item) => ({ label: item.archName, value: item.id })));

      await refreshOrgParentOptions();

      const domainRes = await queryDomainPage({ current: 1, pageSize: 500, sorter: { displayOrder: 'ascend' } });
      setAllDomainRows(domainRes?.data?.rows ?? []);
    })();
  }, []);

  useEffect(() => {
    if (!selectedNode || selectedNode.type !== 'organization') {
      setArchDomainOptions([]);
      setArchShowDomainSelect(false);
      setArchTargetDomainId(undefined);
      return;
    }
    const orgDomain = selectedNode.domainId;
    if (orgDomain != null && orgDomain !== '' && Number(orgDomain) > 0) {
      setArchDomainOptions([]);
      setArchShowDomainSelect(false);
      setArchTargetDomainId(String(orgDomain));
      return;
    }
    const options = filterDomainOptionsForCom(allDomainRows, selectedNode.comId);
    setArchDomainOptions(options);
    setArchShowDomainSelect(options.length > 1);
    if (options.length === 1) {
      setArchTargetDomainId(options[0].value);
    } else {
      setArchTargetDomainId(undefined);
    }
  }, [selectedNode, allDomainRows]);

  useEffect(() => {
    resolvePendingFocus();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [orgMetaList, archMetaList]);

  const isOk = (resp) => !!(resp && (
    resp.success || resp.code === 200
  ));

  const handleSelect = (keys, { node }) => {
    setSelectedKey(keys[0]);
    setSelectedNode(node.node);
  };

  const handleRefresh = () => {
    setSelectedNode(null);
    setSelectedKey(null);
    triggerReload();
  };

  const handleTreeDataChange = (data) => {
    setTreeData(data);
    const pending = pendingFocusRef.current;
    if (!pending) {
      return;
    }
    const node = findTreeNode(data, pending.focusType, pending.focusId);
    if (node) {
      pendingFocusRef.current = null;
      setSelectedKey(`${node.type}-${node.id}`);
      setSelectedNode(node);
    }
  };

  const handleCompanyUpdate = async (payload) => {
    try {
      const response = await updateCompany(payload);
      if (!isOk(response)) {
        message.error((response && (response.message || response.msg)) || intl.formatMessage({ id: 'component.organizationTree.company.msg.updateFail' }));
        return;
      }

      const latest = await getCompanyById(payload.id);
      if (latest) {
        setSelectedNode((prev) => {
          if (!prev) return prev;
          if (prev.id !== payload.id && prev.comId !== payload.id) return prev;
          return { ...prev, ...latest, comName: latest.comName || latest.name };
        });
      }

      triggerReload();
      message.success(intl.formatMessage({ id: 'component.organizationTree.company.msg.updateOk' }));
    } catch (error) {
      console.error('更新公司失败:', error);
      message.error(intl.formatMessage({ id: 'component.organizationTree.company.msg.updateFail' }));
    }
  };

  const handleSystemAdd = async (companyId, values) => {
    try {
      const response = await addSystem({
        comId: companyId,
        ...values,
      });
      if (isOk(response)) {
        message.success(intl.formatMessage({ id: 'component.organizationTree.system.msg.addOk' }));
        triggerReload();
      } else {
        message.error((response && (response.message || response.msg)) || intl.formatMessage({ id: 'component.organizationTree.system.msg.addFail' }));
      }
    } catch (error) {
      console.error('添加体系失败:', error);
      message.error(intl.formatMessage({ id: 'component.organizationTree.system.msg.addFail' }));
    }
  };

  const handleCompanyDelete = async (companyId) => {
    try {
      const response = await deleteCompany({ id: companyId });
      if (isOk(response)) {
        message.success(intl.formatMessage({ id: 'component.organizationTree.company.msg.deleteOk' }));
        setSelectedNode(null);
        setSelectedKey(null);
        triggerReload();
      } else {
        message.error((response && (response.message || response.msg)) || intl.formatMessage({ id: 'component.organizationTree.company.msg.deleteFail' }));
      }
    } catch (error) {
      console.error('删除公司失败:', error);
      message.error(intl.formatMessage({ id: 'component.organizationTree.company.msg.deleteFail' }));
    }
  };

  const handleSystemUpdate = async (systemId, values) => {
    try {
      const response = await updateSystem({
        id: systemId,
        ...values,
      });
      if (isOk(response)) {
        setSelectedNode((prev) => {
          if (!prev || prev.id !== systemId) return prev;
          return { ...prev, ...values };
        });
        triggerReload();
        message.success(intl.formatMessage({ id: 'component.organizationTree.system.msg.updateOk' }));
      } else {
        message.error((response && (response.message || response.msg)) || intl.formatMessage({ id: 'component.organizationTree.system.msg.updateFail' }));
      }
    } catch (error) {
      console.error('更新体系失败:', error);
      message.error(intl.formatMessage({ id: 'component.organizationTree.system.msg.updateFail' }));
    }
  };

  const handleOrganizationAdd = async (systemId, values) => {
    try {
      const response = await addOrganization({
        archId: systemId,
        ...values,
      });
      if (isOk(response)) {
        message.success(intl.formatMessage({ id: 'component.organizationTree.org.msg.addOk' }));
        await refreshOrgParentOptions();
        triggerReload();
      } else {
        message.error((response && (response.message || response.msg)) || intl.formatMessage({ id: 'component.organizationTree.org.msg.addFail' }));
      }
    } catch (error) {
      console.error('添加机构失败:', error);
      message.error(intl.formatMessage({ id: 'component.organizationTree.org.msg.addFail' }));
    }
  };

  const handleSystemDelete = async (systemId) => {
    try {
      const response = await deleteSystem({ id: systemId });
      if (isOk(response)) {
        message.success(intl.formatMessage({ id: 'component.organizationTree.system.msg.deleteOk' }));
        setSelectedNode(null);
        setSelectedKey(null);
        triggerReload();
      } else {
        message.error((response && (response.message || response.msg)) || intl.formatMessage({ id: 'component.organizationTree.system.msg.deleteFail' }));
      }
    } catch (error) {
      console.error('删除体系失败:', error);
      message.error(intl.formatMessage({ id: 'component.organizationTree.system.msg.deleteFail' }));
    }
  };

  const handleOrganizationUpdate = async (orgId, values) => {
    try {
      const response = await updateOrganization({
        id: orgId,
        ...values,
      });
      if (isOk(response)) {
        message.success(intl.formatMessage({ id: 'component.organizationTree.org.msg.updateOk' }));
        await refreshOrgParentOptions();
        triggerReload();
      } else {
        message.error((response && (response.message || response.msg)) || intl.formatMessage({ id: 'component.organizationTree.org.msg.updateFail' }));
      }
    } catch (error) {
      console.error('更新机构失败:', error);
      message.error(intl.formatMessage({ id: 'component.organizationTree.org.msg.updateFail' }));
    }
  };

  const handleMembersChanged = () => {
    setShowUsers(false);
    setShowUsers(true);
  };

  const handleCompanyAdd = async (values) => {
    try {
      const response = await addCompany(values);
      if (isOk(response)) {
        message.success(intl.formatMessage({ id: 'component.organizationTree.company.msg.addOk' }));
        triggerReload();
        return true;
      }
      message.error((response && (response.message || response.msg)) || intl.formatMessage({ id: 'component.organizationTree.company.msg.addFail' }));
      return false;
    } catch (error) {
      console.error('添加公司失败:', error);
      message.error(intl.formatMessage({ id: 'component.organizationTree.company.msg.addFail' }));
      return false;
    }
  };

  const handleOrganizationDelete = async (orgId) => {
    try {
      const response = await deleteOrganization({ id: orgId });
      if (isOk(response)) {
        message.success(intl.formatMessage({ id: 'component.organizationTree.org.msg.deleteOk' }));
        setSelectedNode(null);
        setSelectedKey(null);
        await refreshOrgParentOptions();
        triggerReload();
      } else {
        message.error((response && (response.message || response.msg)) || intl.formatMessage({ id: 'component.organizationTree.org.msg.deleteFail' }));
      }
    } catch (error) {
      console.error('删除机构失败:', error);
      message.error(intl.formatMessage({ id: 'component.organizationTree.org.msg.deleteFail' }));
    }
  };

  return (
    <div style={{ height: '100vh', padding: '16px' }}>
      <Card
        title={
          <Space>
            <ApartmentOutlined />
            {intl.formatMessage({ id: 'component.organizationTree.page.title' })}
          </Space>
        }
        extra={
          <Space>
            {canManageCompanyStructure ? (
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={() => setCreateComVisible(true)}
              >
                {intl.formatMessage({ id: 'component.organizationTree.page.addCompany' })}
              </Button>
            ) : null}
            <Button
              icon={<ReloadOutlined />}
              onClick={handleRefresh}
            >
              {intl.formatMessage({ id: 'component.organizationTree.page.refresh' })}
            </Button>
          </Space>
        }
        style={{ height: '100%' }}
        bodyStyle={{ height: 'calc(100% - 57px)', padding: 0 }}
      >
        <Row gutter={16} style={{ height: '100%' }}>
          <Col span={8} style={{ height: '100%' }}>
            <Card
              title={intl.formatMessage({ id: 'component.organizationTree.page.treeTitle' })}
              size="small"
              style={{ height: '100%' }}
              bodyStyle={{ height: 'calc(100% - 40px)', overflow: 'auto', padding: '8px 12px' }}
            >
              <AutoComplete
                style={{ width: '100%', marginBottom: 8 }}
                value={searchKeyword}
                options={searchOptions}
                onSearch={setSearchKeyword}
                onSelect={handleSearchSelect}
                placeholder={intl.formatMessage({ id: 'component.organizationTree.search.placeholder' })}
                notFoundContent={
                  searchKeyword.trim()
                    ? intl.formatMessage({ id: 'component.organizationTree.search.empty' })
                    : null
                }
                allowClear
              />
              <OrganizationTree
                onSelect={handleSelect}
                selectedKey={selectedKey}
                showUsers={showUsers}
                onTreeDataChange={handleTreeDataChange}
                reloadToken={reloadToken}
                focusTarget={focusTarget}
                onFocusApplied={() => setFocusTarget(null)}
              />
            </Card>
          </Col>
          <Col span={16} style={{ height: '100%' }}>
            <Card
              title={intl.formatMessage({ id: 'component.organizationTree.detail.manage' })}
              size="small"
              style={{ height: '100%' }}
              bodyStyle={{ height: 'calc(100% - 40px)', overflow: 'auto', padding: 16 }}
            >
              <OrganizationDetail
                selectedNode={selectedNode}
                treeData={treeData}
                onCompanyUpdate={handleCompanyUpdate}
                onCompanyAdd={handleCompanyAdd}
                onSystemAdd={handleSystemAdd}
                onCompanyDelete={handleCompanyDelete}
                onSystemUpdate={handleSystemUpdate}
                onOrganizationAdd={handleOrganizationAdd}
                onSystemDelete={handleSystemDelete}
                onOrganizationUpdate={handleOrganizationUpdate}
                onMembersChanged={handleMembersChanged}
                onOrganizationDelete={handleOrganizationDelete}
                comOptions={comOptions}
                orgTypeOptions={orgTypeOptions}
                archOptionsForOrg={archOptionsForOrg}
                orgOptionsForParent={orgOptionsForParent}
                hasAuthority={hasAuthority}
                targetDomainId={archTargetDomainId}
                domainOptions={archDomainOptions}
                showDomainSelect={archShowDomainSelect}
                onTargetDomainChange={setArchTargetDomainId}
              />
            </Card>
          </Col>
        </Row>
      </Card>

      <ComCreateForm
        modalVisible={createComVisible}
        onCancel={() => setCreateComVisible(false)}
      >
        <ComCreateFormContent
          onSubmit={async (values) => {
            const ok = await handleCompanyAdd(values);
            if (ok) setCreateComVisible(false);
          }}
          onCancel={() => setCreateComVisible(false)}
          roles={comOptions}
        />
      </ComCreateForm>
    </div>
  );
};

export default connect(({ user }) => buildAuthorityHelper(user, '/admin'))(OrganizationArchitecture);
