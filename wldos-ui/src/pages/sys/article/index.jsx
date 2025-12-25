import {Button, Divider, message, Popconfirm} from 'antd';
import React, {useEffect, useRef, useState} from 'react';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import isMobile from '@/hooks/isMobile';
import {queryPage, publishEntity, offlineEntity, removeEntity, removeEntities} from './service';
import {Link} from "umi";
import {QuestionCircleOutlined} from "@ant-design/icons";
import {fetchEnumPubStatus} from "@/services/enum";
import {selectToEnum} from "@/utils/utils";

/**
 * 批量删除
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

const ArticleList = () => {
  const actionRef = useRef();
  const [selectedRowsState, setSelectedRows] = useState([]);  
  const [pubStatusEnum, setPubStatus] = useState({});
  
  // 移动端检测
  const mobile = isMobile();
  
  // 容器宽度监听
  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();
  
  // 使用桌面端粘性布局
  useDesktopSticky(actionRef);

  useEffect(async () => {
    const res = await fetchEnumPubStatus();

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
  const totalColsWidth = 1200; // 估算总宽度
  const scrollX = totalColsWidth > containerWidth ? totalColsWidth : undefined;

  const columns = [
    {
      title: '标题',
      dataIndex: 'pubTitle',
      fixed: mobile ? undefined : 'left',
      render: (dom, entity) => {
        return <Link to={`/element-${entity.id}/preview`} target="_blank">{dom}</Link>;
      },
      width: '16%'
    },
    {
      title: '作者',
      hideInSearch: true,
      render: (_, {member,  createBy}) => {
        return member && <Link to={`/archives-author/${createBy}.html`} target="_blank">{member.nickname}</Link>;
      },
    },
    {
      title: '分类',
      hideInSearch: true,
      render: (_, {terms}) => {
        return (<span>{terms && enumTerms(terms,  'category')}</span>);
      },
    },
    {
      title: '标签',
      hideInSearch: true,
      render: (_, {tags}) => {
        return (<span>{tags && enumTerms(tags,  'tag')}</span>);
      },
    },
    {
      title: '日期',
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
    },
    {
      title: '评论数',
      dataIndex: 'commentCount',
      hideInSearch: true,
      sorter: true,
    },
    {
      title: '关注数',
      dataIndex: 'starCount',
      hideInSearch: true,
      sorter: true,
    },
    {
      title: '点赞数',
      dataIndex: 'likeCount',
      hideInSearch: true,
      sorter: true,
    },
    {
      title: '浏览数',
      dataIndex: 'views',
      sorter: true,
      hideInSearch: true,
    },
    {
      title: '状态',
      dataIndex: 'pubStatus',
      hideInForm: true,
      filters: true,
      onFilter: false,
      valueEnum: pubStatusEnum,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      fixed: mobile ? undefined : 'right',
      render: (_, record) => (
        <>
          {(record.pubStatus === 'publish' || record.pubStatus === 'inherit') &&
            <Popconfirm title="您确定要下线？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
            onConfirm={async () => {
            await doOffline(record);
            actionRef.current?.reloadAndRest?.();
            }}>
            <a>下线</a>
            </Popconfirm>}
          {(record.pubStatus !== 'publish' && record.pubStatus !== 'inherit') &&
            <Popconfirm title="您确定要发布？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                        onConfirm={async () => {
                          await handlePublish(record);
                          actionRef.current?.reloadAndRest?.();
                        }}>
              <a>发布</a>
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
          headerTitle="内容列表"
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
        </FooterToolbar>
      )}
    </PageContainer>
  );
};

export default ArticleList;
