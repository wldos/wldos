import {PlusOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import {Button, Divider, Drawer, Form, Input, message, Popconfirm, Select, Space, Tag, Tooltip, Typography} from 'antd';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';
import {useIntl, connect, Link} from 'umi';
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
  addEntity,
  queryPage,
  removeEntity,
  removeEntities,
  updateEntity,
  addDomainApp,
  getPlatDomain,   addDomainRes,
  bindDomainAdmins,
  fetchProvisionScenarios,
} from './service';
import AddAppForm from "@/pages/sys/domain/components/AddAppForm";
import AddResForm from "@/pages/sys/domain/components/AddResForm";
import MenuLayoutModal from "@/pages/sys/domain/components/MenuLayoutModal";
import DomainAdminForm from "@/pages/sys/domain/components/DomainAdminForm";
import CloneSiteForm from './components/CloneSiteForm';
import ProvisionScenarioModal from './components/ProvisionScenarioModal';
import {getComSelectOption} from "@/pages/sys/com/service";
import {fetchOssUrl} from "@/services/constant";
import {UploadView, upParams} from "@/components/FileUpload";
import {selectToEnum} from "@/utils/utils";
import { fetchPlatformOperatorContext } from '@/pages/sys/platform-operator/service';
import { canDeleteDomainRecord, domainDeleteBlockReason } from './domainDeleteGuard';
import { adminComToolbarOptions } from '@/utils/orgCreateUserDomain';

const DomainList = ({ isAdmin }) => {
  const intl = useIntl();

  const handleAdd = useCallback(async (fields) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.domain.msg.loading.add', defaultMessage: '正在添加' }));

    try {
      const res = await addEntity({...fields});
      hide();
      // 域名/个性域名重名等业务失败走 BaseException，HTTP 200 + 非 200 业务码，全局 notification 已弹出具体原因，
      // 这里必须判断 code 才能决定是否弹"添加成功"，否则会出现先弹成功再弹错误通知的矛盾提示。
      if (res?.code !== 200) {
        return false;
      }
      message.success(intl.formatMessage({ id: 'sys.domain.msg.addSuccess', defaultMessage: '添加成功' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.domain.msg.addFail', defaultMessage: '添加失败请重试！' }));
      return false;
    }
  }, [intl]);

  const handleUpdate = useCallback(async (fields) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.domain.msg.loading.config', defaultMessage: '正在配置' }));

    try {
      const res = await updateEntity({
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
        portalQrUrl: fields.portalQrUrl,
        portalFooterI18n: fields.portalFooterI18n,
        adminHomeConfig: fields.adminHomeConfig || '',
        id: fields.id,
      });
      hide();
      if (res?.code !== 200) {
        return false;
      }
      message.success(intl.formatMessage({ id: 'sys.domain.msg.configSuccess', defaultMessage: '配置成功' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.domain.msg.configFail', defaultMessage: '配置失败请重试！' }));
      return false;
    }
  }, [intl]);

  const addApp = useCallback(async (value={ids: [], domainId: '', comId: ''}) => {
    if (!!value && value.ids?.length === 0) {
      message.info(intl.formatMessage({ id: 'sys.domain.msg.selectApps', defaultMessage: '请选择要添加的应用！' }));
      return false;
    }

    const hide = message.loading(intl.formatMessage({ id: 'sys.domain.msg.loading.add', defaultMessage: '正在添加' }));

    try {
      const res = await addDomainApp(value);
      hide();
      if (res?.data !== '')
        message.warn(res.data);
      else
        message.success(intl.formatMessage({ id: 'sys.domain.msg.addSuccess', defaultMessage: '添加成功' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.domain.msg.addFail', defaultMessage: '添加失败请重试！' }));
      return false;
    }
  }, [intl]);

  const addRes = useCallback(async (value={ids: [], domainId: ''}) => {
    if (!!value && value.ids?.length === 0) {
      message.info(intl.formatMessage({ id: 'sys.domain.msg.selectRes', defaultMessage: '请选择要添加的资源！' }));
      return false;
    }

    const hide = message.loading(intl.formatMessage({ id: 'sys.domain.msg.loading.add', defaultMessage: '正在添加' }));

    try {
      const res = await addDomainRes(value);
      hide();
      if (res?.data !== '')
        message.warn(res.data);
      else
        message.success(intl.formatMessage({ id: 'sys.domain.msg.addSuccess', defaultMessage: '添加成功' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.domain.msg.addFail', defaultMessage: '添加失败请重试！' }));
      return false;
    }
  }, [intl]);

  const bindAdmins = useCallback(async (value={userIds: [], domainId: ''}) => {
    if (!!value && value.userIds?.length === 0) {
      message.info(intl.formatMessage({ id: 'sys.domain.msg.selectAdmins', defaultMessage: '请选择要设置的用户！' }));
      return false;
    }

    const hide = message.loading(intl.formatMessage({ id: 'sys.domain.msg.loading.add', defaultMessage: '正在添加' }));

    try {
      const res = await bindDomainAdmins(value);
      hide();
      if (res?.data !== '')
        message.warn(res.data);
      else
        message.success(intl.formatMessage({ id: 'sys.domain.msg.addSuccess', defaultMessage: '添加成功' }));
      return true;
    } catch (error) {
      hide();
      message.error(intl.formatMessage({ id: 'sys.domain.msg.addFail', defaultMessage: '添加失败请重试！' }));
      return false;
    }
  }, [intl]);

  const [createModalVisible, handleModalVisible] = useState(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState(false);
  const [stepFormValues, setStepFormValues] = useState({});
  const actionRef = useRef();
  const [row, setRow] = useState();
  const [selectedRowsState, setSelectedRows] = useState([]);
  const [comList, setComList] = useState({});
  const [comListArray, setComListArray] = useState([]);
  const [targetComId, setTargetComId] = useState(undefined);
  const [listScopeReady, setListScopeReady] = useState(!isAdmin);
  const [addAppModalVisible, handleAddAppModalVisible] = useState(false);
  const [addAppValues, setAddAppValues] = useState({});
  const [addResModalVisible, handleAddResModalVisible] = useState(false);
  const [addResValues, setAddResValues] = useState({});
  const [menuLayoutModalVisible, setMenuLayoutModalVisible] = useState(false);
  const [menuLayoutValues, setMenuLayoutValues] = useState({});
  const [addAdminModalVisible, handleAddAdminModalVisible] = useState(false);
  const [addAdminValues, setAddAdminValues] = useState({});
  const [cloneModalVisible, setCloneModalVisible] = useState(false);
  const [cloneSourceRecord, setCloneSourceRecord] = useState(null);
  const [scenarioModalVisible, setScenarioModalVisible] = useState(false);
  const [provisionScenarios, setProvisionScenarios] = useState([]);
  const [operatorPrimaryDomainId, setOperatorPrimaryDomainId] = useState(undefined);
  const [platDomain, setPlatDomain] = useState('');
  const [ossUrl, setOssUrl] = useState('');
  const [logoUrl, setLogoUrl] = useState('');
  const [logoPath, setPath] = useState('');

  const mobile = isMobile();

  const [containerWidth, setContainerWidth] = useState(0);
  const containerRef = useRef();

  useDesktopSticky(actionRef);

  const reloadProvisionScenarios = useCallback(async () => {
    if (!isAdmin) {
      return;
    }
    try {
      const res = await fetchProvisionScenarios();
      if (res?.code === 200) {
        setProvisionScenarios(res?.data || []);
      }
    } catch (e) {
      // ignore
    }
  }, [isAdmin]);

  const templateScenarioMap = useMemo(() => {
    const map = new Map();
    (provisionScenarios || []).forEach((scenario) => {
      const domainId = scenario?.templateDomainId != null ? String(scenario.templateDomainId) : '';
      if (!domainId) {
        return;
      }
      const prev = map.get(domainId) || [];
      prev.push(scenario);
      map.set(domainId, prev);
    });
    return map;
  }, [provisionScenarios]);

  const domainDeleteContext = useMemo(() => ({
    primaryDomainId: operatorPrimaryDomainId,
    templateScenarioMap,
  }), [operatorPrimaryDomainId, templateScenarioMap]);

  const handleRemove = useCallback(async (selectedRows) => {
    const hide = message.loading(intl.formatMessage({ id: 'sys.domain.msg.loading.delete', defaultMessage: '正在删除' }));
    if (!selectedRows) return true;
    const blocked = selectedRows.find((row) => !canDeleteDomainRecord(row, domainDeleteContext));
    if (blocked) {
      hide();
      message.info(domainDeleteBlockReason(blocked, domainDeleteContext) || '所选站点含不可删除项');
      return false;
    }
    try {
      const res = await removeEntities({
        ids: selectedRows.map((row) => row.id),
      });
      hide();
      if (res?.code !== 200) {
        return false;
      }
      message.success(intl.formatMessage({ id: 'sys.domain.msg.deleteSuccess', defaultMessage: '删除成功，即将刷新' }));
      return true;
    } catch (error) {
      hide();
      return false;
    }
  }, [intl, domainDeleteContext]);

  const handleRemoveOne = useCallback(async (fields) => {
    if (!fields) return true;

    if (fields.children) {
      message.info(intl.formatMessage({ id: 'sys.domain.msg.hasChildren', defaultMessage: '存在子节点，请先删除子节点' }));
      return false;
    }
    if (!canDeleteDomainRecord(fields, domainDeleteContext)) {
      message.info(domainDeleteBlockReason(fields, domainDeleteContext) || '当前站点不可删除');
      return false;
    }
    const hide = message.loading(intl.formatMessage({ id: 'sys.domain.msg.loading.delete', defaultMessage: '正在删除' }));
    try {
      const res = await removeEntity({
        id: fields.id,
      });
      hide();
      if (res?.code !== 200) {
        return false;
      }
      message.success(intl.formatMessage({ id: 'sys.domain.msg.deleteSuccess', defaultMessage: '删除成功，即将刷新' }));
      return true;
    } catch (error) {
      hide();
      return false;
    }
  }, [intl, domainDeleteContext]);

  const beforeUp = useCallback((file) => {
    const isGt50K = file.size / 1024 > 100;
    if (isGt50K) {
      return message.error(intl.formatMessage({ id: 'sys.domain.msg.logoMax', defaultMessage: 'logo大小不能超过100k' })).then(() => false);
    }

    return true;
  }, [intl]);

  const handleChange = useCallback((info) => {
    const {file: {status, response}} = info;

    if (status === 'done') {
      message.success(intl.formatMessage({ id: 'sys.domain.msg.uploadSuccess', defaultMessage: '上传成功！' }), 1).then(() => {
        const {data: {url, path}} = response;
        setLogoUrl(url ?? undefined);
        if (path)
          setPath(path);
      });
    } else if (status === 'error') {
      message.error(intl.formatMessage({ id: 'sys.domain.msg.uploadFail', defaultMessage: '上传失败！' }), 2).then(()=>{});
    }
  }, [intl]);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      if (isAdmin) {
        setListScopeReady(false);
      }
      const comData = await getComSelectOption();
      if (cancelled) {
        return;
      }
      const arr = comData?.data ?? [];
      const toolbarComOptions = isAdmin
        ? adminComToolbarOptions(arr, intl.formatMessage, { allowPlatformScope: true })
        : arr;
      setComList(selectToEnum(toolbarComOptions));
      setComListArray(toolbarComOptions);

      const tasks = [getPlatDomain(), fetchOssUrl()];
      if (isAdmin) {
        tasks.push(fetchPlatformOperatorContext().catch(() => null));
      }
      const results = await Promise.all(tasks);
      const domain = results[0];
      const oss = results[1];
      const operatorCtx = isAdmin ? results[2] : null;
      if (cancelled) {
        return;
      }
      if (domain?.data) {
        setPlatDomain(domain.data.platDomain);
      }
      if (oss?.data) {
        setOssUrl(oss.data.ossUrl);
      }
      if (operatorCtx?.code === 200 && operatorCtx?.data?.primaryDomainId != null) {
        setOperatorPrimaryDomainId(String(operatorCtx.data.primaryDomainId));
      }
      setListScopeReady(true);
      if (isAdmin) {
        reloadProvisionScenarios();
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [isAdmin, reloadProvisionScenarios]);

  useEffect(() => {
    if (!listScopeReady) {
      return;
    }
    actionRef.current?.reload?.();
  }, [listScopeReady, targetComId]);

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

  const totalColsWidth = 2180;
  const scrollX = totalColsWidth > containerWidth ? totalColsWidth : undefined;

  const columns = useMemo(() => [
    {
      title: intl.formatMessage({ id: 'sys.domain.col.index', defaultMessage: '序号' }),
      dataIndex: 'displayOrder',
      hideInSearch: true,
      sorter: true,
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.id', defaultMessage: '站点 ID' }),
      dataIndex: 'id',
      hideInSearch: true,
      hideInForm: true,
      width: 200,
      ellipsis: true,
      render: (_, record) => {
        const id = record?.id;
        if (id == null || id === '') {
          return '-';
        }
        return <Typography.Text copyable>{String(id)}</Typography.Text>;
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.siteName', defaultMessage: '名称' }),
      dataIndex: 'siteName',
      tip: intl.formatMessage({ id: 'sys.domain.col.siteName.tip', defaultMessage: '在平台基础上配置的独立域' }),
      fixed: mobile ? undefined : 'left',
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.domain.rule.siteNameRequired', defaultMessage: '网站名称为必填项' }),
          },
          {
            max: 50,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.domain.rule.max50chars', defaultMessage: '最多50个字' }),
          },
        ],
      },
      render: (dom, entity) => {
        const linked = templateScenarioMap.get(String(entity.id)) || [];
        return (
          <Space size={4} wrap>
            <a onClick={() => setRow(entity)}>{dom}</a>
            {linked.map((scenario) => (
              <Tag key={scenario.code} color={scenario.ready ? 'blue' : 'warning'}>
                入驻模板·{scenario.label}
              </Tag>
            ))}
          </Space>
        );
      },
    },
    {
      title: '挂载',
      dataIndex: 'mountSummary',
      hideInSearch: true,
      width: 120,
      render: (_, entity) => {
        const linked = templateScenarioMap.get(String(entity.id)) || [];
        if (!linked.length) {
          return '-';
        }
        const primary = linked[0];
        return `资源 ${primary.resourceCount ?? 0} / 应用 ${primary.appCount ?? 0}`;
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.siteDomain', defaultMessage: '域名' }),
      dataIndex: 'siteDomain',
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.domain.rule.siteDomainRequired', defaultMessage: '域名为必填项' }),
          },
          {
            max: 50,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.domain.rule.max50', defaultMessage: '最多50位' }),
          },
        ],
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.siteLogo', defaultMessage: 'logo' }),
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
            <UploadView key={entity.id} buttonTitle={intl.formatMessage({ id: 'sys.domain.upload.click', defaultMessage: '点此上传' })} src={logoUrl} params={{...upParams(), accept: '.jpg,.png,.gif,.jpeg,.bmp,.svg,.svg+xml'}}
                        beforeUp={(file) => beforeUp(file)}
                        onChange={(info) => handleChange(info)} />
            </span>
        </Input.Group>
      )
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.favicon', defaultMessage: 'favicon' }),
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
            <UploadView key={entity.id} buttonTitle={intl.formatMessage({ id: 'sys.domain.upload.click', defaultMessage: '点此上传' })} src={logoUrl} params={{...upParams(), accept: '.jpg,.png,.gif,.jpeg,.bmp,.svg,.x-icon,.ico,.svg+xml'}}
                        beforeUp={(file) => beforeUp(file)}
                        onChange={(info) => handleChange(info)} />
            </span>
        </Input.Group>
      )
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.secondDomain', defaultMessage: '个性域名' }),
      dataIndex: 'secondDomain',
      tip: intl.formatMessage({ id: 'sys.domain.col.secondDomain.tip', defaultMessage: '平台子域快捷入口，完整地址为 前缀.{platDomain}，与主站域名独立' }, { platDomain }),
      // 新建/编辑表单各自在 CreateFormContent.jsx / UpdateForm.jsx 内独立声明了必填校验；
      // 此处 columns 同时驱动 ProTable 查询表单与详情 ProDescriptions，查询条件不应必填，故不放校验规则。
      render: (_, record) => (<span>{record.secondDomain}</span>),
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.siteUrl', defaultMessage: '网址' }),
      dataIndex: 'siteUrl',
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.domain.rule.siteUrlRequired', defaultMessage: '主页地址为必填项' }),
          },
          {
            max: 200,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.domain.rule.max200', defaultMessage: '最多200位' }),
          },
        ],
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.siteTitle', defaultMessage: '网站标题' }),
      dataIndex: 'siteTitle',
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.domain.rule.siteTitleRequired', defaultMessage: '网站标题为必填项' }),
          },
          {
            max: 50,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.domain.rule.max50chars', defaultMessage: '最多50个字' }),
          },
        ],
      },
      width: '10%'
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.siteKeyword', defaultMessage: '关键词' }),
      dataIndex: 'siteKeyword',
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.domain.rule.keywordRequired', defaultMessage: '关键词为必填项' }),
          },
          {
            max: 125,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.domain.rule.max125', defaultMessage: '最多125个字' }),
          },
        ],
      },
      width: '10%'
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.siteDescription', defaultMessage: '描述' }),
      dataIndex: 'siteDescription',
      formItemProps: {
        rules: [
          {
            required: true,
            message: intl.formatMessage({ id: 'sys.domain.rule.descRequired', defaultMessage: '描述为必填项' }),
          },
          {
            max: 125,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.domain.rule.max125', defaultMessage: '最多125个字' }),
          },
        ],
      },
      width: '12%'
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.slogan', defaultMessage: '网站口号' }),
      dataIndex: 'slogan',
      hideInSearch: true,
      formItemProps: {
        rules: [
          {
            max: 25,
            type: 'string',
            message: intl.formatMessage({ id: 'sys.domain.rule.max25', defaultMessage: '最多25个字' }),
          },
        ],
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.comId', defaultMessage: '公司' }),
      dataIndex: 'comId',
      filters: true,
      onFilter: false,
      hideInForm: true,
      valueEnum: comList,
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.status', defaultMessage: '状态' }),
      dataIndex: 'isValid',
      hideInForm: true,
      filters: true,
      onFilter: false,
      valueEnum: {
        '0': {
          text: intl.formatMessage({ id: 'sys.domain.status.invalid', defaultMessage: '无效' }),
          status: 'invalid',
        },
        '1': {
          text: intl.formatMessage({ id: 'sys.domain.status.valid', defaultMessage: '有效' }),
          status: 'valid',
        },
      },
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.cnameDomain', defaultMessage: '别名' }),
      dataIndex: 'cnameDomain',
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.foot', defaultMessage: '底部栏目' }),
      dataIndex: 'foot',
      hideInSearch: true,
      hideInTable: true,
      tooltip: foot,
      renderFormItem: () => (<Input.TextArea
        rows={4}
      />)
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.flink', defaultMessage: '友情链接' }),
      dataIndex: 'flink',
      hideInSearch: true,
      hideInTable: true,
      tooltip: `${flink}`,
      renderFormItem: () => (<Input.TextArea
        rows={4}
      />)
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.copy', defaultMessage: '版权信息' }),
      dataIndex: 'copy',
      hideInSearch: true,
      hideInTable: true,
      tooltip: `${copy}`,
      renderFormItem: () => (<Input.TextArea
        rows={4}
      />)
    },
    {
      title: intl.formatMessage({ id: 'sys.domain.col.operation', defaultMessage: '操作' }),
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
            {intl.formatMessage({ id: 'sys.domain.action.config', defaultMessage: '配置' })}
          </a>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              setAddAppValues(record);
              handleAddAppModalVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'sys.domain.action.app', defaultMessage: '应用' })}
          </a>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              setAddResValues(record);
              handleAddResModalVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'sys.domain.action.res', defaultMessage: '资源' })}
          </a>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              setMenuLayoutValues(record);
              setMenuLayoutModalVisible(true);
            }}
          >
            菜单布局
          </a>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              setAddAdminValues(record);
              handleAddAdminModalVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'sys.domain.action.admin', defaultMessage: '设置管理员' })}
          </a>
          <Divider type="vertical"/>
          <a
            onClick={() => {
              setCloneSourceRecord(record);
              setCloneModalVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'sys.domain.action.clone', defaultMessage: '复制' })}
          </a>
          <Divider type="vertical"/>
          {canDeleteDomainRecord(record, domainDeleteContext) ? (
            <Popconfirm title={intl.formatMessage({ id: 'sys.domain.popconfirm.delete', defaultMessage: '您确定要删除？' })} icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                        onConfirm={async () => {
                          const ok = await handleRemoveOne(record);
                          if (ok) {
                            actionRef.current?.reloadAndRest?.();
                          }
                        }}
            >
              <a>{intl.formatMessage({ id: 'sys.domain.action.delete', defaultMessage: '删除' })}</a>
            </Popconfirm>
          ) : (
            <Tooltip title={domainDeleteBlockReason(record, domainDeleteContext)}>
              <span style={{ color: 'rgba(0,0,0,0.25)', cursor: 'not-allowed' }}>
                {intl.formatMessage({ id: 'sys.domain.action.delete', defaultMessage: '删除' })}
              </span>
            </Tooltip>
          )}
        </>
      ),
      width: 300
    },
  ], [intl, mobile, comList, platDomain, handleRemoveOne, logoUrl, beforeUp, handleChange, templateScenarioMap, domainDeleteContext]);

  return (
    <PageContainer {...adminListPageContainerProps}>
      <div ref={containerRef}>
        <ProTableX
          headerTitle={intl.formatMessage({ id: 'sys.domain.headerTitle', defaultMessage: '域名清单' })}
          actionRef={actionRef}
          manualRequest={!listScopeReady}
          rowKey="id"
          size={"small"}
          search={{
            labelWidth: 120,
          }}
          toolBarRender={() => [
            isAdmin && comListArray.length > 0 ? (
              <Space key="com-select" size={8}>
                <span>目标租户</span>
                <Select
                  style={{ minWidth: 180 }}
                  allowClear
                  placeholder="选择租户"
                  options={comListArray}
                  value={targetComId}
                  onChange={(value) => setTargetComId(value || undefined)}
                  showSearch
                  optionFilterProp="label"
                />
              </Space>
            ) : null,
            isAdmin && String(targetComId) === '0' ? (
              <Button key="scenarios" onClick={() => setScenarioModalVisible(true)}>
                入驻场景
              </Button>
            ) : null,
            <Button
              key={0}
              type="primary"
              onClick={() => {
                if (isAdmin && (targetComId == null || targetComId === '')) {
                  message.warning('请先在工具栏选择目标租户；体验模板站须选「平台」');
                  return;
                }
                handleModalVisible(true);
              }}
            >
              <PlusOutlined/> {intl.formatMessage({ id: 'sys.domain.toolbar.new', defaultMessage: '新建' })}
            </Button>,
          ]}
          request={async (params, sorter, filter) => {
            const query = {
              ...params,
              sorter: {...sorter, 'displayOrder': 'ascend',},
              filter
            };
            if (isAdmin && targetComId) {
              query.comId = targetComId;
            }
            const res = await queryPage(query);

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
            showTotal: (total, range) =>
              intl.formatMessage(
                { id: 'sys.domain.pagination.range', defaultMessage: '第 {start}-{end} 条/总共 {total} 条' },
                { start: range[0], end: range[1], total },
              ),
          }}
          tableLayout={mobile ? undefined : 'fixed'}
          scroll={mobile ? undefined : { x: scrollX }}
        />
      </div>
      {selectedRowsState?.length > 0 && (
        <FooterToolbar
          extra={
            <div>
              {intl.formatMessage({ id: 'sys.domain.footer.selected', defaultMessage: '已选择' })}{' '}
              <a
                style={{
                  fontWeight: 600,
                }}
              >
                {selectedRowsState.length}
              </a>{' '}
              {intl.formatMessage({ id: 'sys.domain.footer.items', defaultMessage: '项' })}&nbsp;&nbsp;
            </div>
          }
        >
          <Popconfirm title={intl.formatMessage({ id: 'sys.domain.popconfirm.delete', defaultMessage: '您确定要删除？' })} icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
                      onConfirm={async () => {
                        await handleRemove(selectedRowsState);
                        setSelectedRows([]);
                        actionRef.current?.reloadAndRest?.();
                      }}>
            <Button>
              {intl.formatMessage({ id: 'sys.domain.footer.batchDelete', defaultMessage: '批量删除' })}
            </Button>
          </Popconfirm>
          <Button type="primary">{intl.formatMessage({ id: 'sys.domain.footer.batchExport', defaultMessage: '批量导出' })}</Button>
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
            const payload = { ...value, siteLogo: logoPath };
            if (isAdmin && targetComId != null && targetComId !== '') {
              payload.comId = targetComId;
            }
            const success = await handleAdd(payload);

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
          onMenuLayout={(record) => {
            setMenuLayoutValues(record);
            setMenuLayoutModalVisible(true);
          }}
        /> : null}
      {menuLayoutValues && Object.keys(menuLayoutValues).length ?
        <MenuLayoutModal
          onCancel={() => {
            setMenuLayoutModalVisible(false);
            setMenuLayoutValues({});
          }}
          modalVisible={menuLayoutModalVisible}
          values={menuLayoutValues}
        /> : null}
      {addAdminValues && Object.keys(addAdminValues).length ?
        <DomainAdminForm
          onCancel={() => {
            handleAddAdminModalVisible(false);
            setAddAdminValues({});
          }}
          adminModalVisible={addAdminModalVisible}
          values={addAdminValues}
          bindAdmins={bindAdmins}
        /> : null}
      <CloneSiteForm
        visible={cloneModalVisible}
        sourceRecord={cloneSourceRecord}
        onCancel={() => {
          setCloneModalVisible(false);
          setCloneSourceRecord(null);
        }}
        onSuccess={() => {
          setCloneModalVisible(false);
          setCloneSourceRecord(null);
          actionRef.current?.reload?.();
        }}
      />
      <ProvisionScenarioModal
        visible={scenarioModalVisible}
        onCancel={() => setScenarioModalVisible(false)}
        onSuccess={() => {
          setScenarioModalVisible(false);
          reloadProvisionScenarios();
        }}
      />

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

export default connect(({ user }) => ({
  isAdmin: !!user.sessionFlags?.isAdmin,
}))(DomainList);
