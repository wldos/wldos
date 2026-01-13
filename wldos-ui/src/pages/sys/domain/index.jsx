import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Divider, Drawer, Form, Input, message, Popconfirm, Space} from 'antd';
import React, {useEffect, useRef, useState} from 'react';
import {FooterToolbar, PageContainer} from '@ant-design/pro-layout';
import ProTableX from '@/components/ProTableX';
import ProDescriptions from '@ant-design/pro-descriptions';
import useDesktopSticky from '@/components/ProTableX/useDesktopSticky';
import isMobile from '@/hooks/isMobile';
import CreateForm from './components/CreateForm';
import CreateFormContent from './components/CreateFormContent';
import UpdateForm from './components/UpdateForm';
import {
  addEntity,
  queryPage,
  removeEntity,
  removeEntities,
  updateEntity,
  addDomainApp,
  getPlatDomain, addDomainRes,
} from './service';
import AddAppForm from "@/pages/sys/domain/components/AddAppForm";
import AddResForm from "@/pages/sys/domain/components/AddResForm";
import {getComSelectOption} from "@/pages/sys/com/service";
import {fetchOssUrl} from "@/services/constant";
import {UploadView, upParams} from "@/components/FileUpload";
import {selectToEnum} from "@/utils/utils";

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
      siteName: fields.siteName,
      siteDomain: fields.siteDomain,
      siteLogo: fields.siteLogo,
      favicon: fields.favicon,
      secondDomain: fields.secondDomain,
      siteUrl:fields.siteUrl,
      siteTitle: fields.siteTitle,
      siteKeyword: fields.siteKeyword,
      siteDescription: fields.siteDescription,
      slogan: fields.slogan,
      comId: fields.comId,
      isValid: fields.isValid,
      displayOrder: fields.displayOrder,
      cnameDomain: fields.cnameDomain,
      foot: fields.foot,
      flink: fields.flink,
      copy: fields.copy,
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
 * 删除节点
 *
 * @param fields
 * @returns {Promise<boolean>}
 */
const handleRemoveOne = async (fields) => {
  if (!fields) return true;

  if (fields.children) {
    message.info("存在子节点，请先删除子节点");
    return true;
  }
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

/**
 * 添加应用
 * @param value
 * @returns {Promise<boolean>}
 */
const addApp = async (value={ids: [], domainId: '', comId: ''}) => {
  if (!!value && value.ids?.length === 0) {
    message.info('请选择要添加的应用！');
    return false;
  }

  const hide = message.loading('正在添加');

  try {
    const res = await addDomainApp(value);
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

/**
 * 添加资源
 * @param value
 * @returns {Promise<boolean>}
 */
const addRes = async (value={ids: [], domainId: ''}) => {
  if (!!value && value.ids?.length === 0) {
    message.info('请选择要添加的资源！');
    return false;
  }

  const hide = message.loading('正在添加');

  try {
    const res = await addDomainRes(value);
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

const DomainList = () => {
  const [createModalVisible, handleModalVisible] = useState(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState(false);
  const [stepFormValues, setStepFormValues] = useState({});
  const actionRef = useRef();
  const [row, setRow] = useState();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const [comList, setComList] = useState({});
  const [addAppModalVisible, handleAddAppModalVisible] = useState(false);
  const [addAppValues, setAddAppValues] = useState({});
  const [addResModalVisible, handleAddResModalVisible] = useState(false);
  const [addResValues, setAddResValues] = useState({});
  const [platDomain, setPlatDomain] = useState('');
  const [ossUrl, setOssUrl] = useState('');
  const [logoUrl, setLogoUrl] = useState('');
  const [logoPath, setPath] = useState('');

  // 移动端检测
  const mobile = isMobile();

  // 容器宽度监听
  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();

  // 使用桌面端粘性布局
  useDesktopSticky(actionRef);

  const beforeUp = (file) => {
    const isGt50K = file.size / 1024 > 100;
    if (isGt50K) {
      return message.error('logo大小不能超过100k').then(() => false);
    }

    return true;
  };

  const handleChange = (info) => {
    const {file: {status, response}} = info;

    if (status === 'done') {
      message.success(`上传成功！`, 1).then(() => {
        const {data: {url, path}} = response;
        setLogoUrl(url ?? undefined);
        if (path)
          setPath(path);
      });
    } else if (status === 'error') {
      message.error(`上传失败！`, 2).then(()=>{});
    }
  };

  useEffect(async () => {
    const comData = await getComSelectOption();

    const arr = comData?.data?? [];
    setComList(selectToEnum(arr));

    const domain = await getPlatDomain();
    if (domain && domain.data) {
      setPlatDomain(domain.data.platDomain);
    }

    const oss = await fetchOssUrl();
    if (oss?.data)
      setOssUrl(oss.data.ossUrl);
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

  const foot = "<div>\n" +
    "<h3>关于本站</h3>\n" +
    "<div>\n" +
    "<p><a href=\"http://www.gitee.com/wldos/wldos/#\" target=\"_blank\" rel=\"noopener noreferrer\">关于我们</a></p>\n" +
    "<p><a href=\"http://www.gitee.com/wldos/wldos/#\" target=\"_blank\" rel=\"noopener noreferrer\">联系我们</a>\n" +
    "</p>\n" +
    "<p><a href=\"http://www.gitee.com/wldos/wldos/#\" target=\"_blank\" rel=\"noopener noreferrer\">加入我们</a>\n" +
    "</p>\n" +
    "<p><a href=\"http://www.gitee.com/wldos/wldos/#\" target=\"_blank\" rel=\"noopener noreferrer\">隐私协议</a>\n" +
    "</p>\n" +
    "<p><a href=\"http://www.gitee.com/wldos/wldos/#\" target=\"_blank\" rel=\"noopener noreferrer\">售后服务</a>\n" +
    "</p>\n" +
    "</div>\n" +
    "</div>\n" +
    "<div>\n" +
    "<h3>会员通道</h3>\n" +
    "<div>\n" +
    "<p>\n" +
    "<a href=\"http://www.gitee.com/wldos/wldos/user/login\" rel=\"nofollow\">登录</a>/<a href=\"https://www.gitee.com/wldos/wldos/register-2\" rel=\"nofollow\">注册</a>\n" +
    "</p>\n" +
    "<p><a href=\"http://www.gitee.com/wldos/wldos/account\" rel=\"nofollow\">个人中心</a></p>\n" +
    "<p><a href=\"http://www.gitee.com/wldos/wldos/ucenter?pd=ref\" rel=\"nofollow\">代理推广</a>\n" +
    "</p>\n" +
    "<p><a href=\"http://www.gitee.com/wldos/wldos/ucenter?pd=money\" rel=\"nofollow\">在线充值</a>\n" +
    "</p>\n" +
    "<p><a href=\"http://www.gitee.com/wldos/wldos/archives-category/blog\">技术博客</a></p>\n" +
    "<p><a href=\"http://www.gitee.com/wldos/wldos/help\">会员帮助</a></p>\n" +
    "</div>\n" +
    "</div>\n" +
    "<div><h3>服务领域</h3>\n" +
    "<div>\n" +
    "<p>\n" +
    "  <a href=\"http://www.gitee.com/wldos/wldos/archives-category/shopproduct/prosite\">网站建设</a>\n" +
    "</p>\n" +
    "<p>\n" +
    "  <a href=\"http://www.gitee.com/wldos/wldos/archives-category/shopproduct/protools\">软件工具</a>\n" +
    "</p>\n" +
    "<p>\n" +
    "  <a href=\"http://www.gitee.com/wldos/wldos/archives-category/shopproduct/prodev\">开发框架</a>\n" +
    "</p>\n" +
    "<p><a\n" +
    "  href=\"http://www.gitee.com/wldos/wldos/archives-category/shopproduct/proengine\">应用引擎</a>\n" +
    "</p>\n" +
    "<p><a\n" +
    "  href=\"http://www.gitee.com/wldos/wldos/archives-category/shopproduct/resolution\">解决方案</a>\n" +
    "</p>\n" +
    "</div>\n" +
    "</div>\n" +
    "<div>\n" +
    "<h3>官方微信</h3>\n" +
    "<div>\n" +
    "<p>\n" +
    "  <img loading=\"lazy\" style=\"float: none; margin-left: auto;margin-right: auto; clear: both; border: 0;  vertical-align: middle;  max-width: 100%;  height: auto;\"\n" +
    "       src=\"http://localhost:8088/store/zltcode.jpg\" alt=\"wx\" width=\"150\" height=\"165\"/>\n" +
    "</p>\n" +
    "</div>\n" +
    "</div>\n" +
    "<div style=\"padding:0; width:28%;\">\n" +
    "<h3>联系方式</h3>\n" +
    "<div>\n" +
    "<p>\n" +
    "  <span><strong>1566-ABCD-EFG</strong></span>\n" +
    "</p>\n" +
    "<p>Q Q： 583ABC365 30DEFQ142</p>\n" +
    "<p>邮箱： support#abcdefg.com</p>\n" +
    "<p>服务： 周一至周六 9:00~17:30</p>\n" +
    "<p>&nbsp;</p>\n" +
    "<p>\n" +
    "  <a href=\"https://weibo.com/u/5810954456?is_all=1\" target=\"_blank\" rel=\"noopener nofollow noreferrer\">\n" +
    "    <img src=\"http://localhost:8088/store/weibo.svg\" style=\"margin-right: 4px\" alt=\"官方微博\"/>\n" +
    "  </a>\n" +
    "  <a href=\"http://localhost:8088/store/zltcode.jpg\" target=\"_blank\" rel=\"noopener noreferrer\">\n" +
    "    <img src=\"http://localhost:8088/store/weixin.svg\" style=\"margin-right: 4px\" alt=\"官方微信\"/>\n" +
    "  </a>\n" +
    "  <a href=\"https://user.qzone.qq.com/583716365\" target=\"_blank\" rel=\"noopener nofollow noreferrer\">\n" +
    "    <img src=\"http://localhost:8088/store/qqzone.svg\" style=\"margin-right: 4px\" alt=\"QQ空间\"/>\n" +
    "  </a>\n" +
    "  <a href=\"https://wpa.qq.com/msgrd?v=3&amp;uin=583716365&amp;site=xiupu.net&amp;menu=yes\" rel=\"noopener nofollow noreferrer\">\n" +
    "    <img src=\"http://localhost:8088/store/qq.svg\" style=\"margin-right: 4px\" alt=\"联系QQ\"/>\n" +
    "  </a>\n" +
    "  <a href=\"mailto:support@xiupu.net\" rel=\"noopener nofollow noreferrer\">\n" +
    "    <img src=\"http://localhost:8088/store/mail.svg\" style=\"margin-right: 4px\" alt=\"电子邮箱\"/>\n" +
    "  </a>\n" +
    "</p>\n" +
    "</div>\n" +
    "</div>";

  const flink = "<strong>友情链接：</strong>\n" +
    "<a href=\"https://www.github.com\" target=\"_blank\" rel=\"noopener noreferrer\">github</a>\n" +
    "<a href=\"http://www.gitee.com/wldos/wldos\" target=\"_blank\" rel=\"noopener noreferrer\">WLDOS</a>";

  const copy = "<p>\n" +
    "<a href=\"http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=37xxxx20xxxxx\" target=\"_blank\" rel=\"nofollow noopener noreferrer\">\n" +
    "<img src=\"http://www.gitee.com/wldos/wldos/store/ba.png\" alt=\"beiAn\" width=\"18\" height=\"18\"/> X公网安备 3701xxx20xxxxx号</a>&nbsp;\n" +
    "<a href=\"https://beian.miit.gov.cn/\" target=\"_blank\" rel=\"nofollow noopener noreferrer\">XICP备2xx1xxxx号</a>\n" +
    "<a href=\"http://www.gitee.com/wldos/wldos/privacy\">法律声明</a> | <a href=\"https://www.gitee.com/wldos/wldos/privacy\">隐私协议</a> | Copyright © 2022\n" +
    "<a href=\"http://www.gitee.com/wldos/wldos/\" rel=\"nofollow\">WLDOS</a> 版权所有\n" +
    "</p>";

  // 计算列总宽度
  const totalColsWidth = 2000; // 估算总宽度
  const scrollX = totalColsWidth > containerWidth ? totalColsWidth : undefined;

  const columns = [
    {
      title: '序号',
      dataIndex: 'displayOrder',
      hideInSearch: true,
      sorter: true,
    },
    {
      title: '名称',
      dataIndex: 'siteName',
      tip: '在平台基础上配置的独立域',
      fixed: mobile ? undefined : 'left',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '网站名称为必填项',
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
      title: '域名',
      dataIndex: 'siteDomain',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '域名为必填项',
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
      title: 'logo',
      dataIndex: 'siteLogo',
      valueType: 'image',
      width: 80,
      hideInSearch: true,
      render: (dom,) => (
        <Space>
          <span>{dom}</span>
        </Space>
      ),
      renderFormItem: (entity) => (
        <Input.Group compact>
            <span>
            <UploadView key={entity.id} buttonTitle="点此上传" src={logoUrl} params={{...upParams(), accept: '.jpg,.png,.gif,.jpeg,.bmp,.svg,.svg+xml'}}
                        beforeUp={(file) => beforeUp(file)}
                        onChange={(info) => handleChange(info)} />
            </span>
        </Input.Group>
      )
    },
    {
      title: 'favicon',
      dataIndex: 'favicon',
      valueType: 'image',
      width: 50,
      hideInSearch: true,
      render: (dom,) => (
        <Space>
          <span>{dom}</span>
        </Space>
      ),
      renderFormItem: (entity) => (
        <Input.Group compact>
            <span>
            <UploadView key={entity.id} buttonTitle="点此上传" src={logoUrl} params={{...upParams(), accept: '.jpg,.png,.gif,.jpeg,.bmp,.svg,.x-icon,.ico,.svg+xml'}}
                        beforeUp={(file) => beforeUp(file)}
                        onChange={(info) => handleChange(info)} />
            </span>
        </Input.Group>
      )
    },
    {
      title: '个性域名',
      dataIndex: 'secondDomain',
      tip: `个性域名.${platDomain}`,
      renderFormItem: () => (
        <Input.Group compact>
            <span>
            <Form.Item
              noStyle
              name="secondDomain"
              rules={[
                {
                  required: true,
                  message: '个性域名为必填项',
                },
                {
                  max: 10,
                  type: 'string',
                  message: '最多10位',
                },
                {
                  type: 'string',
                  pattern: '^[a-z]+$',
                  message: '只能是小写字母'
                }
              ]}
            >
              <Input
                style={{
                  width: 'calc(100% - 100px)',
                }}
                placeholder="请输入个性域名，不能为空！"
              />
            </Form.Item>
            .{platDomain}</span>
        </Input.Group>
      ),
      formItemProps: {
        rules: [
          {
            required: true,
            message: '个性域名为必填项',
          },
          {
            max: 10,
            type: 'string',
            message: '最多10位',
          },
          {
            type: 'string',
            pattern: '^[a-z]+$',
            message: '只能是小写字母'
          }
        ],
      },
      render: (_, record) => (<span>{record.secondDomain}</span>),
    },
    {
      title: '网址',
      dataIndex: 'siteUrl',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '主页地址为必填项',
          },
          {
            max: 200,
            type: 'string',
            message: '最多200位',
          },
        ],
      },
    },
    {
      title: '网站标题',
      dataIndex: 'siteTitle',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '网站标题为必填项',
          },
          {
            max: 50,
            type: 'string',
            message: '最多50个字',
          },
        ],
      },
      width: '10%'
    },
    {
      title: '关键词',
      dataIndex: 'siteKeyword',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '关键词为必填项',
          },
          {
            max: 125,
            type: 'string',
            message: '最多125个字',
          },
        ],
      },
      width: '10%'
    },
    {
      title: '描述',
      dataIndex: 'siteDescription',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '描述为必填项',
          },
          {
            max: 125,
            type: 'string',
            message: '最多125个字',
          },
        ],
      },
      width: '12%'
    },
    {
      title: '网站口号',
      dataIndex: 'slogan',
      hideInSearch: true,
      formItemProps: {
        rules: [
          {
            max: 25,
            type: 'string',
            message: '最多25个字',
          },
        ],
      },
    },
    {
      title: '公司',
      dataIndex: 'comId',
      filters: true,
      onFilter: false,
      hideInForm: true,
      valueEnum: comList,
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
      title: '别名',
      dataIndex: 'cnameDomain',
    },
    {
      title: '底部栏目',
      dataIndex: 'foot',
      hideInSearch: true,
      hideInTable: true,
      tooltip: foot,
      renderFormItem: () => (<Input.TextArea
        rows={4}
      />)
    },
    {
      title: '友情链接',
      dataIndex: 'flink',
      hideInSearch: true,
      hideInTable: true,
      tooltip: `${flink}`,
      renderFormItem: () => (<Input.TextArea
        rows={4}
      />)
    },
    {
      title: '版权信息',
      dataIndex: 'copy',
      hideInSearch: true,
      hideInTable: true,
      tooltip: `${copy}`,
      renderFormItem: () => (<Input.TextArea
        rows={4}
      />)
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
              setStepFormValues(record);
            }}
          >
            配置
          </a>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              setAddAppValues(record);
              handleAddAppModalVisible(true);
            }}
          >
            应用
          </a>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              setAddResValues(record);
              handleAddResModalVisible(true);
            }}
          >
            资源
          </a>
          <Divider type="vertical"/>
          <Popconfirm title="您确定要删除？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await handleRemoveOne(record);
                        actionRef.current?.reloadAndRest?.();
                      }}
          >
            <a>删除</a>
          </Popconfirm>
        </>
      ),
      width: 250
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
          headerTitle="域名清单"
          actionRef={actionRef}
          rowKey="id"
          size={"small"}
          search={{
            labelWidth: 120,
          }}
          toolBarRender={() => [
            <Button key={0} type="primary" onClick={() => handleModalVisible(true)}>
              <PlusOutlined/> 新建
            </Button>,
          ]}
          request={async (params, sorter, filter) => {
            const res = await queryPage({
              ...params,
              sorter: {...sorter, 'displayOrder': 'ascend',},
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
              项&nbsp;&nbsp;
            </div>
          }
        >
          <Popconfirm title="您确定要删除？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await handleRemove(selectedRowsState);
                        setSelectedRows([]);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <Button>
              批量删除
            </Button>
          </Popconfirm>
          <Button type="primary">批量导出</Button>
        </FooterToolbar>
      )}
      <CreateForm
        onCancel={() => {
          handleModalVisible(false);
          setPath('');
          setLogoUrl('');
        }}
        modalVisible={createModalVisible}
      >
        <CreateFormContent
          onSubmit={async (value) => {
            const success = await handleAdd({...value, siteLogo: logoPath});

            if (success) {
              handleModalVisible(false);
              setPath('');
              setLogoUrl('');
              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
          onCancel={() => {
            handleModalVisible(false);
            setPath('');
            setLogoUrl('');
          }}
          platDomain={platDomain}
          ossUrl={ossUrl}
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
          platDomain={platDomain}
          ossUrl={ossUrl}
        />
      ) : null}
      {addAppValues && Object.keys(addAppValues).length ?
        <AddAppForm
          onCancel={() => {
            handleAddAppModalVisible(false);
            setAddAppValues({});
          }}
          addAppModalVisible={addAppModalVisible}
          values={addAppValues}
          addApp={addApp}
        /> : null}
      {addResValues && Object.keys(addResValues).length ?
        <AddResForm
          onCancel={() => {
            handleAddResModalVisible(false);
            setAddResValues({});
          }}
          addResModalVisible={addResModalVisible}
          values={addResValues}
          addRes={addRes}
        /> : null}

      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.siteName && (
          <ProDescriptions
            column={2}
            title={row?.siteName}
            request={async () => ({
              data: row || {},
            })}
            params={{
              id: row?.siteName,
            }}
            columns={columns}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default DomainList;
