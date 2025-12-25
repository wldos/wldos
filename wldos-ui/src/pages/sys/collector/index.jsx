import {Button, Divider, message, Popconfirm} from 'antd';
import React, {useEffect, useRef, useState} from 'react';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import isMobile from '@/hooks/isMobile';
import {fetchSrcCache, queryPage} from './service';
import {Link} from "umi";
import {publishEntity, offlineEntity, removeEntity, removeEntities} from "@/pages/sys/article/service";
import {QuestionCircleOutlined} from "@ant-design/icons";
import {fetchEnumIsCollect} from "@/services/enum";
import {selectToEnum} from "@/utils/utils";
import CacheModal from "@/pages/sys/collector/components/CacheModal";
import PreCacheModal from "@/pages/sys/collector/components/PreCacheModal";

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

const handleCache = async (fields) => {
  return true;
};

const CollectorList = () => {
  const actionRef = useRef();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const [isCollectedEnum, setIsCollect] = useState({});
  const [modalVisible, handleModalVisible] = useState(false);
  const [formValues, setFormValues] = useState({});
  const [preModalVisible, handlePreModalVisible] = useState(false);
  const [preFormValues, setPreFormValues] = useState({});
  
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
      setIsCollect(selectToEnum(res.data));
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

  // 计算列总宽度
  const totalColsWidth = 1000; // 估算总宽度
  const scrollX = totalColsWidth > containerWidth ? totalColsWidth : undefined;

  const columns = [
    {
      title: '网址',
      dataIndex: 'siteUrl',
      fixed: mobile ? undefined : 'left',
      render: (dom, entity) => {
        return <a href={dom} target="_blank">{dom}</a>;
      },
      width: '16%'
    },
    {
      title: '是否采集',
      dataIndex: 'isCollect',
      hideInForm: true,
      filters: true,
      onFilter: false,
      valueEnum: isCollectedEnum,
    },
    {
      title: '规则ID',
      dataIndex: 'ruleId',
      hideInSearch: true,
    },
    {
      title: '采集时间',
      dataIndex: 'dateTime',
      sorter: true,
      valueType: 'dateTime',
    },
    {
      title: '热词',
      hideInSearch: true,
      dataIndex: 'hotWord',
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      fixed: mobile ? undefined : 'right',
      render: (_, record) => (
        <>
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
          <Divider type="vertical"/>
          <a
            onClick={async () => {
              handleModalVisible(true);
              const src = await fetchSrcCache(record?.id);
              setFormValues(src?.data?? {});
            }}
          >
            源内容
          </a>
          <Divider type="vertical"/>
          <a
            onClick={async () => {
              handlePreModalVisible(true);
              const src = await fetchSrcCache(record?.id);
              setPreFormValues(src?.data?? {});
            }}
          >
            预处理
          </a>
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
      {formValues && Object.keys(formValues).length ? (
        <CacheModal
          onSubmit={async (value) => {
            const success = await handleCache(value);

            if (success) {
              handleModalVisible(false);
              setFormValues({});

              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          onCancel={() => {
            handleModalVisible(false);
            setFormValues({});
          }}
          modalVisible={modalVisible}
          values={formValues}
        />
      ) : null}
      {preFormValues && Object.keys(preFormValues).length ? (
        <PreCacheModal
          onSubmit={async (value) => {
            const success = await handleCache(value);

            if (success) {
              handlePreModalVisible(false);
              setPreFormValues({});

              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          onCancel={() => {
            handlePreModalVisible(false);
            setPreFormValues({});
          }}
          modalVisible={preModalVisible}
          values={preFormValues}
        />
      ) : null}
    </PageContainer>
  );
};

export default CollectorList;
