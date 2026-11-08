import { FolderAddOutlined, QuestionCircleOutlined, SaveOutlined } from '@ant-design/icons';
import {
  Button,
  Card,
  Col,
  Empty,
  Form,
  Input,
  InputNumber,
  message,
  Popconfirm,
  Row,
  Select,
  Space,
  Tag,
  Tree,
  Typography,
} from 'antd';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import NavDirFormFields from './components/NavDirFormFields';
import NavDirFormModal from './components/NavDirFormModal';
import {
  createSiteNavDir,
  deleteSiteNavDir,
  fetchSiteMenuTree,
  saveResTreeOverlay,
  updateSiteNavDir,
} from './service';

const { Text } = Typography;

const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};

function getResourceId(node) {
  return node?.resourceId ?? node?.resource_id ?? null;
}

function sameResourceId(a, b) {
  if (a == null || b == null) {
    return false;
  }
  return String(a) === String(b);
}

function isSiteNavDir(node) {
  return node?.menuNodeKind === 'nav_dir' && node?.scopeLevel === 'domain';
}

function resolveNavDirLabel(values, selectedNode) {
  return (values?.label ?? selectedNode?.overlayLabel ?? selectedNode?.resourceName ?? '').trim();
}

function buildSiteNavDirPayload(domainId, selectedNode, formValues, mode) {
  if (!domainId) {
    return { error: '缺少站点 ID' };
  }
  if (mode === 'create') {
    const label = resolveNavDirLabel(formValues, null);
    if (!label) {
      return { error: '请输入分组名称' };
    }
    return {
      payload: {
        domainId,
        label,
        overlayParentId: formValues?.overlayParentId || null,
        overlayDisplayOrder: formValues?.overlayDisplayOrder ?? null,
      },
    };
  }
  if (mode === 'update') {
    const resourceId = getResourceId(selectedNode);
    if (!resourceId) {
      return { error: '缺少资源 ID' };
    }
    const label = resolveNavDirLabel(formValues, selectedNode);
    if (!label) {
      return { error: '请输入分组名称' };
    }
    return {
      payload: {
        domainId,
        resourceId,
        label,
        overlayParentId: formValues?.overlayParentId || null,
        overlayDisplayOrder: formValues?.overlayDisplayOrder ?? null,
      },
    };
  }
  if (mode === 'delete') {
    const resourceId = getResourceId(selectedNode);
    if (!resourceId) {
      return { error: '缺少资源 ID' };
    }
    return {
      payload: {
        domainId,
        resourceId,
      },
    };
  }
  return { error: '未知操作' };
}

function flattenTree(nodes, list = []) {
  if (!nodes) return list;
  nodes.forEach((node) => {
    list.push(node);
    if (node.children?.length) {
      flattenTree(node.children, list);
    }
  });
  return list;
}

function buildTreeData(nodes, overlayDraft) {
  if (!nodes?.length) return [];
  return nodes.map((node) => {
    const resourceId = getResourceId(node);
    const draft = overlayDraft[resourceId] || {};
    const title = draft.overlayLabel ?? node.overlayLabel ?? node.resourceName;
    const globalName = node.globalResourceName ?? node.resourceName;
    const siteOwned = isSiteNavDir(node);
    const hasOverlay = !siteOwned && (
      draft.overlayParentId != null
      || draft.overlayDisplayOrder != null
      || draft.overlayLabel != null
      || node.overlayParentId != null
      || node.overlayLabel != null
    );
    return {
      key: String(resourceId),
      title: (
        <Space size={4}>
          <span>{title}</span>
          {siteOwned ? <Tag color="purple">站点分组</Tag> : null}
          {hasOverlay ? <Tag color="blue">已定制</Tag> : null}
          {!siteOwned && globalName && globalName !== title ? (
            <Text type="secondary" style={{ fontSize: 12 }}>({globalName})</Text>
          ) : null}
        </Space>
      ),
      children: node.children?.length ? buildTreeData(node.children, overlayDraft) : undefined,
    };
  });
}

const MenuLayoutPanel = ({ domainId }) => {
  const [treeRaw, setTreeRaw] = useState([]);
  const [flatNodes, setFlatNodes] = useState([]);
  const [overlayDraft, setOverlayDraft] = useState({});
  const [selectedId, setSelectedId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [createSubmitting, setCreateSubmitting] = useState(false);
  const [navDirSaving, setNavDirSaving] = useState(false);
  const [navDirDraft, setNavDirDraft] = useState(null);
  const [overlayForm] = Form.useForm();
  const [navDirForm] = Form.useForm();

  const loadTree = useCallback(async () => {
    if (!domainId) return;
    setLoading(true);
    try {
      const res = await fetchSiteMenuTree(domainId);
      const data = res?.data || [];
      setTreeRaw(data);
      setFlatNodes(flattenTree(data));
    } finally {
      setLoading(false);
    }
  }, [domainId]);

  useEffect(() => {
    loadTree();
  }, [loadTree]);

  const selectedNode = useMemo(
    () => flatNodes.find((n) => sameResourceId(getResourceId(n), selectedId)),
    [flatNodes, selectedId],
  );

  const parentOptions = useMemo(() => flatNodes.map((n) => ({
    value: getResourceId(n),
    label: n.globalResourceName ?? n.resourceName,
  })), [flatNodes]);

  const overlayParentOptions = useMemo(
    () => parentOptions.filter((opt) => !sameResourceId(opt.value, selectedId)),
    [parentOptions, selectedId],
  );

  const treeData = useMemo(() => buildTreeData(treeRaw, overlayDraft), [treeRaw, overlayDraft]);
  const hasOverlayDraft = Object.keys(overlayDraft).length > 0;

  useEffect(() => {
    setNavDirDraft(null);
  }, [selectedId]);

  useEffect(() => {
    if (!selectedNode) {
      overlayForm.resetFields();
      return;
    }
    if (isSiteNavDir(selectedNode)) {
      return;
    }
    const resourceId = getResourceId(selectedNode);
    const draft = overlayDraft[resourceId] || {};
    overlayForm.setFieldsValue({
      overlayParentId: draft.overlayParentId ?? selectedNode.overlayParentId ?? undefined,
      overlayDisplayOrder: draft.overlayDisplayOrder ?? selectedNode.overlayDisplayOrder ?? undefined,
      overlayLabel: draft.overlayLabel ?? selectedNode.overlayLabel ?? undefined,
    });
  }, [selectedNode, overlayForm, overlayDraft]);

  const openEditor = (resourceId) => {
    if (!resourceId) return;
    setSelectedId(resourceId);
  };

  const handleOverlayValuesChange = (_, allValues) => {
    if (!selectedId || !selectedNode || isSiteNavDir(selectedNode)) {
      return;
    }
    setOverlayDraft((prev) => ({
      ...prev,
      [selectedId]: {
        overlayParentId: allValues.overlayParentId || null,
        overlayDisplayOrder: allValues.overlayDisplayOrder ?? null,
        overlayLabel: allValues.overlayLabel?.trim() || null,
      },
    }));
  };

  const handleNavDirValuesChange = (_, allValues) => {
    if (!selectedNode || !isSiteNavDir(selectedNode)) {
      return;
    }
    setNavDirDraft({
      label: allValues.label?.trim() ?? '',
      overlayParentId: allValues.overlayParentId || null,
      overlayDisplayOrder: allValues.overlayDisplayOrder ?? null,
    });
  };

  const handleSaveOverlay = async () => {
    const items = Object.entries(overlayDraft).map(([resourceId, v]) => ({
      resourceId,
      overlayParentId: v.overlayParentId,
      overlayDisplayOrder: v.overlayDisplayOrder,
      overlayLabel: v.overlayLabel,
    }));
    if (!items.length) return;
    const res = await saveResTreeOverlay({ domainId, items });
    if (res?.code !== 200) return;
    message.success('菜单布局已保存');
    setOverlayDraft({});
    loadTree();
  };

  const handleCreateNavDir = async (values) => {
    const built = buildSiteNavDirPayload(domainId, null, values, 'create');
    if (built.error) {
      message.warning(built.error);
      return;
    }
    setCreateSubmitting(true);
    try {
      const res = await createSiteNavDir(built.payload);
      if (res?.code !== 200) return;
      message.success('分组已创建');
      setCreateModalVisible(false);
      const newResourceId = res?.data?.resourceId;
      if (newResourceId != null) {
        setSelectedId(newResourceId);
      }
      loadTree();
    } catch (err) {
      message.error(err?.response?.message || err?.message || '创建失败');
    } finally {
      setCreateSubmitting(false);
    }
  };

  const handleUpdateNavDir = async () => {
    if (!selectedNode || !isSiteNavDir(selectedNode)) return;
    let formValues;
    try {
      formValues = await navDirForm.validateFields();
    } catch {
      return;
    }
    const mergedValues = navDirDraft ? { ...formValues, ...navDirDraft } : formValues;
    const built = buildSiteNavDirPayload(domainId, selectedNode, mergedValues, 'update');
    if (built.error) {
      message.warning(built.error);
      return;
    }
    setNavDirSaving(true);
    try {
      const res = await updateSiteNavDir(built.payload);
      if (res?.code !== 200) return;
      message.success('分组已更新');
      setNavDirDraft(null);
      loadTree();
    } catch (err) {
      message.error(err?.response?.message || err?.message || '保存失败');
    } finally {
      setNavDirSaving(false);
    }
  };

  const handleDeleteNavDir = async () => {
    if (!selectedNode || !isSiteNavDir(selectedNode)) return;
    const built = buildSiteNavDirPayload(domainId, selectedNode, null, 'delete');
    if (built.error) {
      message.warning(built.error);
      return;
    }
    try {
      await deleteSiteNavDir(built.payload);
      message.success('分组已删除');
      setSelectedId(null);
      setNavDirDraft(null);
      navDirForm.resetFields();
      loadTree();
    } catch (err) {
      message.error(err?.response?.message || err?.message || '删除失败');
    }
  };

  const renderDetailPanel = () => {
    if (!selectedNode) {
      return <Empty description="请在左侧选择菜单节点" />;
    }
    if (isSiteNavDir(selectedNode)) {
      return (
        <Form
          {...formLayout}
          key={String(getResourceId(selectedNode))}
          form={navDirForm}
          initialValues={{
            label: selectedNode.overlayLabel ?? selectedNode.resourceName,
            overlayParentId: selectedNode.overlayParentId ?? undefined,
            overlayDisplayOrder: selectedNode.overlayDisplayOrder ?? undefined,
          }}
          onValuesChange={handleNavDirValuesChange}
        >
          <NavDirFormFields
            parentOptions={parentOptions}
            excludeResourceId={getResourceId(selectedNode)}
          />
          <Form.Item wrapperCol={{ offset: 6, span: 18 }}>
            <Space>
              <Button type="primary" loading={navDirSaving} onClick={handleUpdateNavDir}>
                保存
              </Button>
              <Popconfirm
                title="删除前请确认已在角色管理中取消该资源的授权，确定删除该分组？"
                icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                onConfirm={handleDeleteNavDir}
              >
                <Button danger>删除</Button>
              </Popconfirm>
            </Space>
          </Form.Item>
        </Form>
      );
    }
    return (
      <Form
        {...formLayout}
        form={overlayForm}
        onValuesChange={handleOverlayValuesChange}
      >
        <Form.Item name="overlayParentId" label="本站上级">
          <Select
            allowClear
            placeholder="留空=继承平台树"
            options={overlayParentOptions}
            showSearch
            optionFilterProp="label"
          />
        </Form.Item>
        <Form.Item name="overlayDisplayOrder" label="本站排序">
          <InputNumber style={{ width: '100%' }} placeholder="留空=继承平台排序" />
        </Form.Item>
        <Form.Item name="overlayLabel" label="站内展示名">
          <Input placeholder="留空=继承平台名称" maxLength={128} />
        </Form.Item>
      </Form>
    );
  };

  return (
    <>
      <Card
        size="small"
        title="菜单树"
        extra={(
          <Space>
            <Button icon={<FolderAddOutlined />} onClick={() => setCreateModalVisible(true)}>
              新建分组
            </Button>
            <Button
              type="primary"
              icon={<SaveOutlined />}
              onClick={handleSaveOverlay}
              disabled={!hasOverlayDraft}
            >
              保存布局
            </Button>
          </Space>
        )}
        loading={loading}
      >
        <Row gutter={24}>
          <Col xs={24} lg={10}>
            <Tree
              showLine
              selectedKeys={selectedId != null ? [String(selectedId)] : []}
              treeData={treeData}
              onSelect={(keys) => {
                if (keys.length) openEditor(keys[0]);
              }}
            />
          </Col>
          <Col xs={24} lg={14}>
            {renderDetailPanel()}
          </Col>
        </Row>
      </Card>
      <NavDirFormModal
        modalVisible={createModalVisible}
        parentOptions={parentOptions}
        submitting={createSubmitting}
        onCancel={() => setCreateModalVisible(false)}
        onSubmit={handleCreateNavDir}
      />
    </>
  );
};

export default MenuLayoutPanel;
