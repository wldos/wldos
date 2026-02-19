import React, { useRef, useState, useEffect, useMemo } from 'react';
import { Button, Divider, message, Popconfirm, Select, TreeSelect } from 'antd';
import { PlusOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import {
  queryStoreProductAdminList,
  deleteStoreProduct,
  addStoreProduct,
  updateStoreProduct,
  getStoreProductCategories,
  getStoreProductAdminDetail,
} from './service';
import CreateForm from './components/CreateForm';
import UpdateForm from './components/UpdateForm';

/** 分类树：value 用 categoryCode，title 用 categoryName；long 在 JSON 里已是字符串，直接用 */
function toTreeData(items) {
  if (!Array.isArray(items)) return [];
  return items.filter(Boolean).map((item) => {
    const code = item.categoryCode;
    const val = code != null && code !== '' ? code : undefined;
    const title = item.categoryName || item.name || item.title || val || '';
    const isRoot =
      (code != null && String(code) === '0') ||
      (item.categoryName === '根分类' && (item.parentId === null || item.parentId === undefined));
    const children = item.children?.length ? toTreeData(item.children) : undefined;
    if (val == null) return null;
    return {
      title,
      value: val,
      key: val,
      disabled: isRoot,
      selectable: !isRoot,
      children: children?.length ? children : undefined,
    };
  }).filter(Boolean);
}

/** 扁平化 toTreeData 得到的树为 valueEnum 与 filters（排除 disabled 根节点），用于列筛选与表头显示 */
function flattenCategoriesForFilter(treeData) {
  const valueEnum = {};
  const filters = [];
  if (!Array.isArray(treeData)) return { valueEnum, filters };
  const walk = (list) => {
    (list || []).forEach((item) => {
      const code = item.value;
      const name = item.title || code || '';
      if (code != null && code !== '' && !item.disabled) {
        valueEnum[code] = { text: name };
        filters.push({ text: name, value: code });
      }
      if (item.children?.length) walk(item.children);
    });
  };
  walk(treeData);
  return { valueEnum, filters };
}

const handleAdd = async (fields) => {
  const hide = message.loading('正在添加');
  try {
    const res = await addStoreProduct({ ...fields, productType: 'PLUGIN' });
    hide();
    if (res?.code === 200) {
      message.success('添加成功');
      return true;
    }
    message.error(res?.message || '添加失败');
    return false;
  } catch (error) {
    hide();
    message.error('添加失败请重试！');
    return false;
  }
};

const handleUpdate = async (fields) => {
  const hide = message.loading('正在更新');
  try {
    await updateStoreProduct(fields.id, fields);
    hide();
    message.success('更新成功');
    return true;
  } catch (error) {
    hide();
    message.error('更新失败请重试！');
    return false;
  }
};

const handleRemove = async (record) => {
  const hide = message.loading('正在删除');
  try {
    await deleteStoreProduct(record.id);
    hide();
    message.success('删除成功');
    return true;
  } catch (error) {
    hide();
    message.error('删除失败请重试！');
    return false;
  }
};

const commercialTypeValueEnum = {
  OPEN_SOURCE: { text: '开源' },
  FREE_COMMERCIAL: { text: '免费商业' },
  PAID_COMMERCIAL: { text: '付费' },
};

const productTypeValueEnum = {
  PLUGIN: { text: '插件' },
  SCENARIO: { text: '场景' },
};

const StoreProductsAdmin = () => {
  const actionRef = useRef();
  const [createVisible, setCreateVisible] = useState(false);
  const [updateVisible, setUpdateVisible] = useState(false);
  const [editValues, setEditValues] = useState({});
  const [categoryTreeData, setCategoryTreeData] = useState([]);
  useDesktopSticky(actionRef);

  useEffect(() => {
    getStoreProductCategories().then((list) => setCategoryTreeData(toTreeData(list)));
  }, []);

  const { valueEnum: categoryValueEnum, filters: categoryFilters } = useMemo(
    () => flattenCategoriesForFilter(categoryTreeData),
    [categoryTreeData]
  );
  const categoryNameByCode = useMemo(() => {
    const m = {};
    const walk = (list) => {
      (list || []).forEach((item) => {
        const code = item.value;
        const name = item.title;
        if (code != null) m[code] = name;
        if (item.children?.length) walk(item.children);
      });
    };
    walk(categoryTreeData);
    return m;
  }, [categoryTreeData]);

  const columns = [
    { title: 'ID', dataIndex: 'id', width: 80, hideInSearch: true },
    {
      title: '关键词',
      dataIndex: 'keyword',
      hideInTable: true,
      ellipsis: true,
    },
    { title: '展示标题', dataIndex: 'displayTitle', ellipsis: true, hideInSearch: true },
    { title: '插件ID', dataIndex: 'pluginId', width: 120, hideInSearch: true },
    {
      title: '分类',
      dataIndex: 'categoryCode',
      width: 120,
      filters: categoryFilters.length ? categoryFilters : undefined,
      onFilter: false,
      valueEnum: Object.keys(categoryValueEnum).length ? categoryValueEnum : undefined,
      render: (_, record) => {
        const code = record.categoryTermTypeId ?? record.categoryCode;
        return code != null ? (categoryNameByCode[String(code)] ?? '-') : '-';
      },
      renderFormItem: () => (
        <TreeSelect
          placeholder="请选择分类（可多选）"
          allowClear
          treeCheckable
          treeData={categoryTreeData}
          treeDefaultExpandAll
          style={{ width: '100%' }}
        />
      ),
    },
    {
      title: '价格',
      dataIndex: 'price',
      width: 80,
      hideInSearch: true,
      render: (v) => (v > 0 ? `¥${v}` : '免费'),
    },
    {
      title: '商业类型',
      dataIndex: 'commercialType',
      width: 100,
      filters: true,
      onFilter: false,
      valueEnum: commercialTypeValueEnum,
      renderFormItem: () => (
        <Select placeholder="请选择（可多选）" mode="multiple" allowClear style={{ width: '100%' }}>
          {Object.entries(commercialTypeValueEnum).map(([k, v]) => (
            <Select.Option key={k} value={k}>{v.text}</Select.Option>
          ))}
        </Select>
      ),
    },
    {
      title: '产品类型',
      dataIndex: 'productType',
      width: 90,
      filters: true,
      onFilter: false,
      valueEnum: productTypeValueEnum,
      renderFormItem: () => (
        <Select placeholder="请选择（可多选）" mode="multiple" allowClear style={{ width: '100%' }}>
          {Object.entries(productTypeValueEnum).map(([k, v]) => (
            <Select.Option key={k} value={k}>{v.text}</Select.Option>
          ))}
        </Select>
      ),
    },
    {
      title: '精选',
      dataIndex: 'isFeatured',
      width: 80,
      hideInSearch: true,
      render: (v) => (v === '1' ? '是' : '否'),
    },
    {
      title: '排序',
      dataIndex: 'displayOrder',
      width: 80,
      hideInSearch: true,
      sorter: true,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      width: 150,
      hideInSearch: true,
      render: (_, record) => (
        <>
          <a onClick={() => window.open(`/product-${record.id}.html`, '_blank')}>预览</a>
          <Divider type="vertical" />
          <a
            onClick={async () => {
              try {
                const res = await getStoreProductAdminDetail(record.id);
                const data = res?.data?.data ?? res?.data ?? res;
                setEditValues(data || record);
              } catch (e) {
                // 详情加载失败时退回列表行数据（无分类/标签明细）
                setEditValues(record);
              }
              setUpdateVisible(true);
            }}
          >
            编辑
          </a>
          <Divider type="vertical" />
          <Popconfirm
            title="确定删除？"
            icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
            onConfirm={async () => {
              const ok = await handleRemove(record);
              if (ok) actionRef.current?.reload?.();
            }}
          >
            <a style={{ color: '#ff4d4f' }}>删除</a>
          </Popconfirm>
        </>
      ),
    },
  ];

  return (
    <PageContainer
      title="产品展示配置"
      style={{ padding: '0', margin: '0' }}
      bodyStyle={{ padding: '24px', margin: '0' }}
    >
      <ProTableX
        headerTitle="展示配置"
        actionRef={actionRef}
        rowKey="id"
        search={{ labelWidth: 100 }}
        toolBarRender={() => [
          <Button key="add" type="primary" onClick={() => setCreateVisible(true)}>
            <PlusOutlined /> 新建
          </Button>,
        ]}
        request={async (params, sorter, filter) => {
          const res = await queryStoreProductAdminList({
            current: params.current,
            pageSize: params.pageSize,
            sorter: sorter || params.sorter,
            filter: filter || params.filter,
            condition: {
              keyword: params.keyword,
              categoryCode: params.categoryCode,
              commercialType: params.commercialType,
              productType: params.productType,
            },
          });
          const data = res?.data?.data || res?.data || {};
          return {
            data: data.rows || [],
            total: data.total || 0,
            success: res?.code === 200,
          };
        }}
        columns={columns}
      />
      <CreateForm
        modalVisible={createVisible}
        onSubmit={async (values) => {
          const ok = await handleAdd(values);
          if (ok) {
            setCreateVisible(false);
            actionRef.current?.reload?.();
          }
        }}
        onCancel={() => setCreateVisible(false)}
      />
      <UpdateForm
        modalVisible={updateVisible}
        values={editValues}
        onSubmit={async (values) => {
          const ok = await handleUpdate(values);
          if (ok) {
            setUpdateVisible(false);
            setEditValues({});
            actionRef.current?.reload?.();
          }
        }}
        onCancel={() => { setUpdateVisible(false); setEditValues({}); }}
      />
    </PageContainer>
  );
};

export default StoreProductsAdmin;
