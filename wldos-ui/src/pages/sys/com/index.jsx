import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Divider, Drawer, message, Popconfirm, Tooltip} from 'antd';
import React, {useEffect, useRef, useState} from 'react';
import {Link, connect} from 'umi';
import { buildAuthorityHelper } from '@/hooks/useAuthority';
import useRouteAccessible, { ORGAN_ARCHITECTURE_ROUTE } from '@/hooks/useRouteAccessible';
import { canProvisionCompany } from '@/utils/orgCreateUserDomain';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import { adminListPageContainerProps } from '@/pages/commercial/_shared/pageContainerStyle';
import ProTableX from '@/components/ProTableX';
import ProDescriptions from '@ant-design/pro-descriptions';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import isMobile from '@/hooks/isMobile';
import CreateForm from './components/CreateForm';
import CreateFormContent from './components/CreateFormContent';
import UpdateForm from './components/UpdateForm';
import {
  addComAdmin,
  addEntity,
  getComSelectOption,
  queryPage,
  removeEntity,
  removeEntitys,
  updateEntity
} from './service';
import AddAdminForm from "@/pages/sys/com/components/AddAdminForm";
import {selectToEnum} from "@/utils/utils";
import { canDeleteCompanyRecord, companyDeleteBlockReason } from './companyDeleteGuard';

/**
 * 添加节点
 * @param fields
 */
const handleAdd = async (fields) => {
  const hide = message.loading('正在添加');

  try {
    await addEntity({...fields});
    hide();
    message.success('添加成功');
    return true;
  } catch (error) {
    hide();
    message.error('添加失败请重试！');
    return false;
  }
};

/**
 * 更新节点
 * @param fields
 */
const handleUpdate = async (fields) => {
  const hide = message.loading('正在配置');

  try {
    await updateEntity({
      comName: fields.comName,
      comCode: fields.comCode,
      parentId: fields.parentId,
      comDesc: fields.comDesc,
      isValid: fields.isValid,
      displayOrder: fields.displayOrder,
      id: fields.id,
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
 *  批量删除
 * @param selectedRows
 */
const handleRemove = async (selectedRows) => {
  const hide = message.loading('正在删除');
  if (!selectedRows) return true;
  try {
    await removeEntitys({
      ids: selectedRows.map((row) => row.id),
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
/**
 *  删除节点
 * @param selectedRows
 */
  const handleRemoveOne = async (fields) => {
  if (!fields) return true;

  if (!canDeleteCompanyRecord(fields)) {
    message.info(companyDeleteBlockReason(fields) || '当前公司不可删除');
    return false;
  }
  const hide = message.loading('正在删除');
  try {
    const res = await removeEntity({
      id: fields.id,
    });
    hide();
    if (res?.code !== 200) {
      return false;
    }
    message.success('删除成功，即将刷新');
    return true;
  } catch (error) {
    hide();
    return false;
  }
};

const addTAdmin = async (value={ids: [], userComId: ''}) => {
  if (!!value && value.ids?.length === 0) {
    message.info('请选择要设置为管理员的人员！');
    return false;
  }

  const hide = message.loading('正在添加');

  try {
    const res = await addComAdmin({
      userIds: value.ids,
      userComId: value.userComId
    });
    hide();
    if (res?.data !== '')
      message.warn(res.data);
    else
      message.success('添加成功');
    return true;
  } catch (error) {
    hide();
    message.error('添加失败请重试！');
    return false;
  }

};

const ComList = ({ isAdmin, isTenantAdmin }) => {
  const canManageCompanyStructure = canProvisionCompany(isAdmin, isTenantAdmin);
  const [createModalVisible, handleModalVisible] = useState(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState(false);
  const [stepFormValues, setStepFormValues] = useState({});
  const actionRef = useRef();
  const [row, setRow] = useState();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const [parentId, setParentId] = useState('0');
  const [roleList, setRoleList] = useState({});
  const [roles, setRoles] = useState([]);
  const [addAdminModalVisible, handleAddAdminModalVisible] = useState(false);
  const [addAdminValues, setAddAdminValues] = useState({});

  // 移动端检测
  const mobile = isMobile();
  const canOpenArchitecture = useRouteAccessible(ORGAN_ARCHITECTURE_ROUTE);

  // 容器宽度监听
  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();

  // 使用桌面端粘性布局 - 使用原版
  useDesktopSticky(actionRef);

  useEffect(async () => {

    const comData = await getComSelectOption();

    const arr = comData?.data?? [];
    setRoleList(selectToEnum(arr));
    setRoles(arr);
  }, []);

  // 监听容器宽度变化
  useEffect(() => {
    if (!containerRef.current) return;

    const updateWidth = () => {
      if (containerRef.current) {
        setContainerWidth(containerRef.current.offsetWidth);
      }
    };

    updateWidth();
    const resizeObserver = new ResizeObserver(updateWidth);
    resizeObserver.observe(containerRef.current);

    return () => resizeObserver.disconnect();
  }, []);

  const columns = [
    {
      title: '公司名称',
      dataIndex: 'comName',
      tip: '公司是平台上独立运营的扁平化租户，上下级仅是一种机构关系体现不参与权限管理',
      fixed: mobile ? undefined : 'left',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '公司名称为必填项',
          },
          {
            max: 50,
            type: 'string',
            message: '最多50个字',
          },
        ],
      },
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '公司编码',
      dataIndex: 'comCode',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '公司编码为必填项，请填统一社会信用代码',
          },
          {
            max: 50,
            type: 'string',
            message: '最多50位',
          },
        ],
      },
    },
    {
      title: '描述',
      dataIndex: 'comDesc',
      valueType: 'textarea',
      width: '20%'
    },
    {
      title: '父公司',
      dataIndex: 'parentId',
      hideInTable: true,
      hideInForm: true,
      valueEnum: roleList,
    },
    {
      title: '展示顺序',
      dataIndex: 'displayOrder',
      hideInSearch: true,
      sorter: true,
    },
    {
      title: '状态',
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
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      fixed: mobile ? undefined : 'right',
      render: (_, record) => (
        <>
          {canManageCompanyStructure ? (
            <>
              <a
                onClick={() => {
                  handleModalVisible(true);
                  setParentId(record.id);
                }}
              >
                子级
              </a>
              <Divider type="vertical"/>
              <a onClick={() => {
                handleModalVisible(true);
                setParentId(record.parentId);
              }}>同级</a>
              <Divider type="vertical"/>
            </>
          ) : null}
          <a
            onClick={() => {
              handleUpdateModalVisible(true);
              setStepFormValues(record);
            }}
          >
            配置
          </a>
          {canOpenArchitecture ? (
            <>
              <Divider type="vertical"/>
              <Link to={`${ORGAN_ARCHITECTURE_ROUTE}?focusType=company&focusId=${record.id}`}>
                在架构中打开
              </Link>
            </>
          ) : null}
          <Divider type="vertical"/>
          <a
            onClick={() => {
              handleAddAdminModalVisible(true);
              setAddAdminValues(record);
            }}
          >
            设置管理员
          </a>
          {canManageCompanyStructure ? (
            <>
              <Divider type="vertical"/>
              {canDeleteCompanyRecord(record) ? (
                <Popconfirm title="您确定要删除？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                            onConfirm={async () => {
                              const ok = await handleRemoveOne(record);
                              if (ok) {
                                actionRef.current?.reloadAndRest?.();
                              }
                            }}>
                  <a>删除</a>
                </Popconfirm>
              ) : (
                <Tooltip title={companyDeleteBlockReason(record)}>
                  <span style={{ color: 'rgba(0,0,0,0.25)', cursor: 'not-allowed' }}>删除</span>
                </Tooltip>
              )}
            </>
          ) : null}
        </>
      ),
    },
  ];

  // 计算列总宽度 - 动态计算
  const totalColsWidth = columns.reduce((total, col) => total + (typeof col.width === 'number' ? col.width : 120), 0);
  const scrollX = mobile ? undefined : (totalColsWidth > (containerWidth || 0) ? totalColsWidth : undefined);

  return (
    <PageContainer {...adminListPageContainerProps}>
      <div ref={containerRef}>
        <ProTableX
          headerTitle="公司清单"
          actionRef={actionRef}
          rowKey="id"
          search={{
            labelWidth: 120,
          }}
          toolBarRender={() => [
            canManageCompanyStructure ? (
              <Button key={0} type="primary" onClick={() => handleModalVisible(true)}>
                <PlusOutlined/> 新建
              </Button>
            ) : null,
          ]}
          request={async (params, sorter, filter) => {
            const res = await queryPage({
              ...params,
              sorter: {...sorter, 'displayOrder': 'ascend',},
              filter
            });
            const rows = res?.data?.rows || [];

            return Promise.resolve({
              total: res?.data?.total || 0,
              data: rows,
              success: true,
            });
          }
          }
          columns={columns}
          rowSelection={canManageCompanyStructure ? {
            onChange: (_, selectedRows) => setSelectedRows(selectedRows),
          } : false}
          pagination={{
            defaultPageSize: 15,
            pageSizeOptions: ['10', '15', '20', '30', '50'],
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条/总共 ${total} 条`,
          }}
          tableLayout={mobile ? undefined : 'fixed'}
          scroll={mobile ? undefined : { x: scrollX }}
        />
      </div>
      {canManageCompanyStructure && selectedRowsState?.length > 0 && (
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
              项&nbsp;&nbsp;
            </div>
          }
        >
          <Popconfirm title="您确定要删除？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await handleRemove(selectedRowsState);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <Button>批量删除</Button>
          </Popconfirm>
          <Button type="primary">批量导出</Button>
        </FooterToolbar>
      )}
      <CreateForm onCancel={() => handleModalVisible(false)}
                  modalVisible={createModalVisible}>
        <CreateFormContent
          onSubmit={async (value) => {
            const success = await handleAdd({...value, parentId});

            if (success) {
              handleModalVisible(false);
              setParentId('0');

              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          onCancel={() => {
            handleModalVisible(false);
            setParentId('0');
          }}
          roles={roles}
        />
      </CreateForm>
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
          roles={roles}
        />
      ) : null}
      {addAdminValues && Object.keys(addAdminValues).length ?
        <AddAdminForm
          onCancel={() => {
            handleAddAdminModalVisible(false);
            setAddAdminValues({});
          }}
          addAdminModalVisible={addAdminModalVisible}
          values={addAdminValues}
          addTAdmin={addTAdmin}
        /> : null}

      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.comName && (
          <ProDescriptions
            column={2}
            title={row?.comName}
            request={async () => ({
              data: row || {},
            })}
            params={{
              id: row?.comName,
            }}
            columns={columns}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default connect(({ user }) => buildAuthorityHelper(user, '/admin'))(ComList);
