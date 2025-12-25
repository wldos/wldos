import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Drawer, message, Popconfirm, TreeSelect} from 'antd';
import React, {useEffect, useRef, useState} from 'react';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import ProTable from '@ant-design/pro-table';
import ProDescriptions from '@ant-design/pro-descriptions';
import {
  getAppList,
  getResList,
  queryPage, removeDomainRes,
  updateEntity
} from './service';
import UpdateForm from './UpdateForm';
import AddResList from "@/pages/sys/domain/components/domainRes/AddResList";
import {queryLayerCategory} from "@/pages/sys/category/service";
import {queryEnumTemplate,queryEnumResource} from "@/services/enum";
import {selectToEnum} from "@/utils/utils";

/**
 * 更新节点
 * @param fields
 */
const handleUpdate = async (fields) => {
  const hide = message.loading('正在配置');

  try {
    await updateEntity({
      id: fields.id,
      domainId: fields.domainId,
      moduleName: fields.moduleName,
      url: fields.url,
      termTypeId: fields.termTypeId,
    });
    hide();
    message.success('配置成功');
    return true;
  } catch (error) {
    hide();
    message.error('配置失败请重试！');
    return false;
  }
};

/**
 *  取消域资源关联
 * @param selectedRows 选中的资源
 * @param domainId 域id
 */
const cancelDomainRes = async (selectedRows, domainId) => {
  const hide = message.loading('正在删除');
  if (!selectedRows) return true;
  try {
    await removeDomainRes({
      ids: selectedRows.map((row) => row.resourceId),
      domainId,
    });
    hide();
    message.success('删除成功，即将刷新');
    return true;
  } catch (error) {
    hide();
    message.error('删除失败，请重试');
    return false;
  }
};

const DomResList = (props) => {
  const {domainId = '', addRes} = props;
  const [updateModalVisible, handleUpdateModalVisible] = useState(false);
  const [stepFormValues, setStepFormValues] = useState({});
  const actionRef = useRef();
  const [row, setRow] = useState();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const [resList, setResList] = useState({});
  const [reses, setReses] = useState([]);
  const [appList, setAppList] = useState({});
  const [apps, setApps] = useState([]);
  const [addModalVisible, handleAddModalVisible] = useState(false);
  const [categoryList, setCategoryList] = useState([]);
  const [templates, setTemplates] = useState([]);
  const [tempType, setTempType] = useState({});
  const [resType, setResType] = useState({});

  useEffect(async () => {
    const res = await getAppList();

    let data = {};
    let temp = [];
    let arr = res?.data?? [];
    for (let i = 0, len = arr.length; i < len; i += 1) {
      const item = arr[i];
      data[item.id] = {
        text: item.appName,
        value: item.id,
      }
      temp.push({label: item.appName, value: item.id});
    }
    setAppList(data);
    setApps(temp);

    const resData = await getResList();

    data = {};
    temp = [];
    arr = resData?.data?? [];
    for (let i = 0, len = arr.length; i < len; i += 1) {
      const item = arr[i];
      data[item.id] = {
        text: item.resourceName,
        value: item.id,
      }
      temp.push({label: item.resourceName, value: item.id});
    }
    setResList(data);
    setReses(temp);
  }, []);

  useEffect(async () => {
    const resData = await queryLayerCategory();
    if (resData?.data)
      setCategoryList(resData.data);
  }, []);

  useEffect(async () => {
    const resData = await queryEnumTemplate() || [];
    if (resData.data)
      setTemplates(resData.data);

    setTempType(selectToEnum(resData));

    const resTypes = await queryEnumResource() || [];
    setResType(selectToEnum(resTypes))
  }, []);

  const columns = [
    {
      title: '资源名称',
      dataIndex: 'resourceName',
      tip: '资源包括菜单、组件、api或静态资源，特点是需要用确定的方式请求(URI+HTTP METHOD)',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '资源名称为必填项',
          },
          {
            max: 25,
            type: 'string',
            message: '最多25个字',
          },
        ],
      },
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '资源编码',
      dataIndex: 'resourceCode',
      tip: '菜单和路由保持一致：资源编码在同一应用下唯一，资源编码原则上取urlPattern的叶子节点：/appCode/resName，子资源名称在父资源名称之后。',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '应用编码为必填项',
          },
          {
            max: 50,
            type: 'string',
            message: '最大50个字符',
          },
        ],
      },
    },
    {
      title: '资源路径',
      dataIndex: 'resourcePath',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '资源路径为必填项',
          },
          {
            max: 250,
            type: 'string',
            message: '最大250个字符',
          },
        ],
      },
    },
    {
      title: 'icon名称',
      dataIndex: 'icon',
    },
    {
      title: '资源类型',
      dataIndex: 'resourceType',
      filters: true,
      onFilter: false,
      valueEnum: resType,
    },
    {
      title: '请求方法',
      dataIndex: 'requestMethod',
      filters: true,
      onFilter: false,
      valueEnum: {
        GET: {
          text: 'GET',
        },
        POST: {
          text: 'POST',
        },
        PUT: {
          text: 'PUT',
        },
        DELETE: {
          text: 'DELETE',
        },
      },
    },
    {
      title: '打开方式',
      dataIndex: 'target',
      valueEnum: {
        '_self': {
          text: '_self',
        },
        '_blank': {
          text: '_blank',
        },
        '_parent': {
          text: '_parent',
        },
        '_top': {
          text: '_top',
        },
      },
    },
    {
      title: '上级资源',
      dataIndex: 'parentId',
      hideInTable: true,
      hideInForm: true,
      valueEnum: resList,
    },
    {
      title: '展示顺序',
      dataIndex: 'displayOrder',
      hideInTable: true,
      hideInSearch: true,
      sorter: true,
    },
    {
      title: '归属应用',
      dataIndex: 'appId',
      filters: true,
      onFilter: false,
      valueEnum: appList,
    },
    {
      title: '资源描述',
      dataIndex: 'remark',
      valueType: 'textarea',
    },
    {
      title: '资源状态',
      dataIndex: 'isValid',
      hideInForm: true,
      filters: true,
      onFilter: false,
      valueEnum: {
        '0': {
          text: '无效',
          status: 'invalid',
        },
        '1': {
          text: '有效',
          status: 'valid',
        },
      },
    },
    {
      title: '动态模板',
      dataIndex: 'moduleName',
      hideInForm: true,
      filters: true,
      onFilter: false,
      valueEnum: tempType,
    },
    {
      title: '关联分类',
      dataIndex: 'termName',
      hideInForm: true,
      hideInSearch: true,
      width: '16%'
    },
    {
      title: '指定url',
      dataIndex: 'url',
      width: '16%'
    },
    {
      title: '分类目录',
      dataIndex: 'termTypeId',
      hideInForm: true,
      hideInTable: true,
      renderFormItem: (_, fieldConfig) => {
        if (fieldConfig.type === 'form') {
          return null;
        }
        return (
          <TreeSelect
            showSearch
            treeData={categoryList}
            treeDefaultExpandAll
            allowClear
            dropdownStyle={{ maxHeight: 400, overflow: 'auto'}}
            placeholder="请选择"
            treeNodeFilterProp="title"
          />);
      }
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => (
        <a
          onClick={() => {
            handleUpdateModalVisible(true);
            setStepFormValues(record);
          }}
        >
          配置
        </a>
      ),
    },
  ];

  return (
    <PageContainer>
      <ProTable
        headerTitle="资源清单"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button key={1} type="primary" onClick={() => handleAddModalVisible(true)}>
            <PlusOutlined/> 添加
          </Button>,
        ]}
        request={async (params, sorter, filter) => {
          const res = await queryPage({
            ...params,
            domainId,
            sorter,
            filter
          });
          return Promise.resolve({
            total: res?.data?.total || 0,
            data: res?.data?.rows || [],
            success: true,
          });
        }
        }
        columns={columns}
        rowSelection={{
          onChange: (_, selectedRows) => setSelectedRows(selectedRows),
        }}
      />
      {selectedRowsState?.length > 0 && (
        <FooterToolbar
          extra={
            <div>
              已选择{' '}
              <a
                style={{
                  fontWeight: 600,
                }}
              >
                {selectedRowsState.length}
              </a>{' '}
              项
            </div>
          }
        >
          <Popconfirm title="您确定要删除？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await cancelDomainRes(selectedRowsState, domainId);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <Button>批量删除</Button>
          </Popconfirm>
          <Button type="primary">批量导出</Button>
        </FooterToolbar>
      )}
      {stepFormValues && Object.keys(stepFormValues).length ? (
        <UpdateForm
          onSubmit={async (value) => {
            const success = await handleUpdate(value);

            if (success) {
              handleUpdateModalVisible(false);
              setStepFormValues({});

              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          onCancel={() => {
            handleUpdateModalVisible(false);
            setStepFormValues({});
          }}
          updateModalVisible={updateModalVisible}
          values={stepFormValues}
          apps={apps}
          reses={reses}
          templates={templates}
          categoryList={categoryList}
        />
      ) : null}
      <AddResList
        onCancel={() => handleAddModalVisible(false)}
        modalVisible={addModalVisible}
        onSubmit={async (value) => {
          const success = await addRes(value);

          if (success) {
            handleAddModalVisible(false);

            if (actionRef.current) {
              actionRef.current.reload();
            }
          }
        }}
        addModalVisible={addModalVisible}
        values={{domainId}}
      />

      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.resourceName && (
          <ProDescriptions
            column={2}
            title={row?.resourceName}
            request={async () => ({
              data: row || {},
            })}
            params={{
              id: row?.resourceName,
            }}
            columns={columns}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default DomResList;
