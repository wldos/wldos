import {Button, Divider, Drawer, message, Popconfirm} from 'antd';
import React, {useEffect, useRef, useState} from 'react';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import isMobile from '@/hooks/isMobile';
import {queryPage} from './service';
import {Link} from "umi";
import {publishEntity, offlineEntity, removeEntity, removeEntities} from "@/pages/sys/article/service";
import {QuestionCircleOutlined} from "@ant-design/icons";
import {fetchEnumIsCollect} from "@/services/enum";
import {selectToEnum} from "@/utils/utils";
import ProDescriptions from "@ant-design/pro-descriptions";
import UpdateForm from "@/pages/sys/app/components/UpdateForm";
import {updateEntity} from "@/pages/sys/app/service";

/**
 *  批量删除
 * @param selectedRows
 */
const handleRemove = async (selectedRows) => {
  const hide = message.loading('正在删除');
  if (!selectedRows) return true;
  try {
    await removeEntities({
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
 *  发布
 */
const handlePublish = async (fields) => {
  const hide = message.loading('正在发布');
  if (!fields) return true;

  try {
    await publishEntity({
      id: fields.id,
      pubType: fields.pubType,
    });
    hide();
    message.success('发布成功，即将刷新');
    return true;
  } catch (error) {
    hide();
    message.error('发布失败，请重试');
    return false;
  }
};
/**
 *  下线
 */
const doOffline = async (fields) => {
  const hide = message.loading('正在下线');
  if (!fields) return true;

  try {
    await offlineEntity({
      id: fields.id,
    });
    hide();
    message.success('下线成功，即将刷新');
    return true;
  } catch (error) {
    hide();
    message.error('下线失败，请重试');
    return false;
  }
};

const handleUpdate = async (fields) => {
  const hide = message.loading('正在配置');

  try {
    await updateEntity({
      appName: fields.appName,
      appDesc: fields.appDesc,
      appType: fields.appType,
      appCode: fields.appCode,
      appSecret: fields.appSecret,
      isValid: fields.isValid,
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
 *  删除节点
 */
const handleDelete = async (fields) => {
  if (!fields) return true;
  const hide = message.loading('正在删除');
  try {
    await removeEntity({
      id: fields.id,
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

const CollectorRuleList = () => {
  const actionRef = useRef();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const [updateModalVisible, handleUpdateModalVisible] = useState(false);
  const [updateFormValues, setUpdateFormValues] = useState({});
  const [row, setRow] = useState();
  const [pubStatusEnum, setPubStatus] = useState({});
  
  // 移动端检测
  const mobile = isMobile();
  
  // 容器宽度监听
  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();
  
  // 使用桌面端粘性布局
  useDesktopSticky(actionRef);

  useEffect(async () => {
    const res = await fetchEnumIsCollect();

    if (res?.data)
      setPubStatus(selectToEnum(res.data));
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

  const enumTerms = (tags = [], classType = 'tag') => {
    const temp = [];
    for (let i = 0, len = tags.length; i < len; i += 1) {
      const tag = tags[i];
      if (tag)
        temp.push((<Link key={`${tag.id}-art-tag-${i}`} to={`/archives/${classType}/${tag.slug}`} target="_blank">{`${tag.name} `}</Link>));
    }
    return temp;
  };

  // 计算列总宽度
  const totalColsWidth = 1000; // 估算总宽度
  const scrollX = totalColsWidth > containerWidth ? totalColsWidth : undefined;

  const columns = [
    {
      title: '规则名称',
      dataIndex: 'ruleName',
      fixed: mobile ? undefined : 'left',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '体系名称为必填项',
          },
          {
            max: 12,
            type: 'string',
            message: '最多12个字',
          },
        ],
      },
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
      width: '16%'
    },
    {
      title: '采集索引页',
      hideInSearch: true,
      dataIndex: 'urlIndex',
      render: (dom, entity) => {
        return <a href={dom} target="_blank">{dom}</a>;
      },
    },
    {
      title: '编码',
      hideInSearch: true,
      hideInTable: true,
      dataIndex: 'charset',
    },
    {
      title: 'url前缀',
      hideInSearch: true,
      hideInTable: true,
      dataIndex: 'preFix',
    },
    {
      title: 'Url匹配正则',
      dataIndex: 'urlRegex',
      hideInTable: true,
      hideInSearch: true,
    },
    {
      title: '大小写敏感',
      dataIndex: 'insensitive',
      hideInSearch: true,
      sorter: true,
    },
    {
      title: '标题匹配正则',
      dataIndex: 'titleRegex',
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: '内容匹配正则',
      dataIndex: 'contentRegex',
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: '需要再处理',
      dataIndex: 'needHandle',
      sorter: true,
    },
    {
      title: '图片前缀替换',
      dataIndex: 'picUrlReplace',
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      fixed: mobile ? undefined : 'right',
      render: (_, record) => (
        <>
          <a
            onClick={() => {
              handleUpdateModalVisible(true);
              setUpdateFormValues(record);
            }}
          >
            配置
          </a>
          <Divider type="vertical"/>
          {record.pubStatus === 'publish' &&
          <Popconfirm title="您确定要下线？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await doOffline(record);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <a>下线</a>
          </Popconfirm>}
          {record.pubStatus !== 'publish' &&
          <Popconfirm title="您确定要发布？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await handlePublish(record);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <a>执行</a>
          </Popconfirm>}
          <Divider type="vertical"/>
          <Popconfirm title="您确定要删除？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await handleDelete(record);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <a>删除</a>
          </Popconfirm>
        </>
      ),
    },
  ];
  return (
    <PageContainer
      style={{
        padding: '0',
        margin: '0'
      }}
      bodyStyle={{
        padding: '24px',
        margin: '0'
      }}
    >
      <div ref={containerRef}>
        <ProTableX
          headerTitle="合集列表"
          actionRef={actionRef}
          rowKey="id"
          search={{
            labelWidth: 120,
          }}
          request={async (params, sorter, filter) => {
            const res = await queryPage({...params,
              sorter,
              filter});
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
          <Popconfirm title="您确定要删除这些内容？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await handleRemove(selectedRowsState);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <Button>删除</Button>
          </Popconfirm>
          <Button type="primary">导出</Button>
          <Button type="primary">导出</Button>
        </FooterToolbar>
      )}

      {updateModalVisible && Object.keys(updateModalVisible).length ? (
        <UpdateForm
          onSubmit={async (value) => {
            const success = await handleUpdate(value);

            if (success) {
              handleUpdateModalVisible(false);
              setUpdateFormValues({});

              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          onCancel={() => {
            handleUpdateModalVisible(false);
            setUpdateFormValues({});
          }}
          updateModalVisible={updateModalVisible}
          values={updateFormValues}
        />
      ) : null}

      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.ruleName && (
          <ProDescriptions
            column={2}
            title={row?.ruleName}
            request={async () => ({
              data: row || {},
            })}
            params={{
              id: row?.id,
            }}
            columns={columns}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default CollectorRuleList;
