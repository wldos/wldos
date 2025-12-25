import {Button, Divider, message, Popconfirm} from 'antd';
import React, {useRef, useState, useEffect} from 'react';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import isMobile from '@/hooks/isMobile';
import {offlineEntity, publishEntity, queryPage, removeEntity, removeEntities, spamComment, trashComment} from './service';
import {Link} from "umi";
import {QuestionCircleOutlined} from "@ant-design/icons";

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
 *  审批通过
 */
const doAudit = async (fields) => {
  const hide = message.loading('正在审批');
  if (!fields) return true;

  try {
    await publishEntity({
      id: fields.id,
    });
    hide();
    message.success('审批成功，即将刷新');
    return true;
  } catch (error) {
    hide();
    message.error('审批失败，请重试');
    return false;
  }
};
/**
 *  驳回
 */
const doReject = async (fields) => {
  const hide = message.loading('正在驳回');
  if (!fields) return true;

  try {
    await offlineEntity({
      id: fields.id,
    });
    hide();
    message.success('驳回成功，即将刷新');
    return true;
  } catch (error) {
    hide();
    message.error('驳回失败，请重试');
    return false;
  }
};
/**
 *  垃圾评论
 */
const doSpam = async (fields) => {
  const hide = message.loading('正在处理');
  if (!fields) return true;

  try {
    await spamComment({
      id: fields.id,
    });
    hide();
    message.success('处理成功，即将刷新');
    return true;
  } catch (error) {
    hide();
    message.error('处理失败，请重试');
    return false;
  }
};
/**
 *  回收站
 */
const doTrash = async (fields) => {
  const hide = message.loading('正在处理');
  if (!fields) return true;

  try {
    await trashComment({
      id: fields.id,
    });
    hide();
    message.success('回收站成功，即将刷新');
    return true;
  } catch (error) {
    hide();
    message.error('回收站失败，请重试');
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

const CommentList = () => {
  const actionRef = useRef();
  const [selectedRowsState, setSelectedRows] = useState([]);
  
  // 移动端检测
  const mobile = isMobile();
  
  // 容器宽度监听
  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();
  
  // 使用桌面端粘性布局
  useDesktopSticky(actionRef);

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

  // 计算列总宽度
  const totalColsWidth = 1000; // 估算总宽度
  const scrollX = totalColsWidth > containerWidth ? totalColsWidth : undefined;

  const columns = [
    {
      title: '作者',
      dataIndex: 'author',
      fixed: mobile ? undefined : 'left',
      render: (dom, entity) => {
        return <Link to={`/info-author/${entity.createBy}.html`} target="_blank">{dom}</Link>;
      },
      width: '16%'
    },
    {
      title: '评论内容',
      dataIndex: 'content',
    },
    {
      title: '评论主题',
      dataIndex: 'pubTitle',
      hideInForm: true,
    },
    {
      title: '评论时间',
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      fixed: mobile ? undefined : 'right',
      render: (_, record) => (
        <>
          {record.approved === '1' &&
            <Popconfirm title="您确定要驳回？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
            onConfirm={async () => {
            await doReject(record);
            actionRef.current?.reloadAndRest?.();
            }}>
            <a>驳回</a>
            </Popconfirm>}
          {record.approved !== '1' &&
            <Popconfirm title="您确定要审批通过？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                        onConfirm={async () => {
                          await doAudit(record);
                          actionRef.current?.reloadAndRest?.();
                        }}>
              <a>通过</a>
            </Popconfirm>}
          <Divider type="vertical"/>
          <Popconfirm title="您确定标记为垃圾？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await doSpam(record);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <a>标记垃圾</a>
          </Popconfirm>
          <Divider type="vertical"/>
          <Popconfirm title="您确定要回收站？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await doTrash(record);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <a>回收站</a>
          </Popconfirm>
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
          headerTitle="评论列表"
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
          <Popconfirm title="您确定要删除这些评论？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await handleRemove(selectedRowsState);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <Button>删除</Button>
          </Popconfirm>
          <Button type="primary">导出</Button>
        </FooterToolbar>
      )}
    </PageContainer>
  );
};

export default CommentList;
