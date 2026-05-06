import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import {
  Modal,
  Typography,
  Empty,
  Space,
  Button,
  Tag,
  Input,
  Divider,
  Collapse,
  Avatar,
  Select,
  Tooltip,
  Form,
  Dropdown,
  Menu,
  message,
  Alert,
} from 'antd';

const { Panel } = Collapse;
import {
  PlusOutlined,
  SearchOutlined,
  UserOutlined,
  TeamOutlined,
  SettingOutlined,
  AppstoreOutlined,
  FullscreenOutlined,
  FullscreenExitOutlined,
} from '@ant-design/icons';
import {
  buildDesktopExternalChannel,
  closeDesktopExternal,
  isDesktopEmbedded,
  migrateDesktopExternalChannelAsync,
  openDesktopExternal,
  openDesktopExternalForce,
  mountDesktopExternalRect,
  unmountDesktopExternalRect,
  showDesktopExternal,
  subscribeSocialBindSessionReady,
  setDesktopExternalModalSuppressed,
} from '@/utils/desktopEmbeddedBridge';
import AccountGroupManageModal from './AccountGroupManageModal';

/** 左侧分组排序：默认分组固定置顶，其余按中文排序，不随 accounts 接口返回顺序变化 */
const GROUP_SORT_DEFAULT_FIRST = '默认分组';

const AccountManageModal = ({
  open,
  onClose,
  accounts = [],
  platforms = [],
  onAddAccount,
  onRefresh,
}) => {
  const [selectedAccountId, setSelectedAccountId] = useState(null);
  const [keyword, setKeyword] = useState('');
  const [addModalOpen, setAddModalOpen] = useState(false);
  const [selectedPlatformCode, setSelectedPlatformCode] = useState('');
  const [customGroups, setCustomGroups] = useState([]);
  const [groupAlias, setGroupAlias] = useState({});
  const [deletedGroups, setDeletedGroups] = useState([]);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [iframeReloadSeed, setIframeReloadSeed] = useState(0);
  const embeddedDesktop = isDesktopEmbedded();
  const [addForm] = Form.useForm();
  const desktopMountRef = useRef(null);
  /** destroyOnClose 重新打开后首帧挂载槽常为 null；用 callback ref 就绪计数驱动 rect 订阅，避免误调 unmount 且漏绑 ResizeObserver */
  const [desktopCefSlotGeneration, setDesktopCefSlotGeneration] = useState(0);
  const bindDesktopCefMountRef = useCallback((el) => {
    desktopMountRef.current = el;
    if (el) {
      setDesktopCefSlotGeneration((n) => n + 1);
    }
  }, []);
  /** 同步互斥：防止连点「确认」时 React state 尚未更新导致多次 POST 插入多条 cms_social_account */
  const addAccountSubmitLockRef = useRef(false);
  const submitPendingBindLockRef = useRef(false);
  const [addingAccount, setAddingAccount] = useState(false);
  const [submittingPendingBind, setSubmittingPendingBind] = useState(false);
  /** 仅选择平台/分组并去登录；真正写库延后到登录完成后的「提交账号」 */
  const [pendingRegistration, setPendingRegistration] = useState(null);
  const [pendingDisplayName, setPendingDisplayName] = useState('');
  const [groupManageOpen, setGroupManageOpen] = useState(false);
  /** 绑定成功瞬间 accounts 尚未含新 id 时，用快照撑住 selected，避免右侧 CEF 槽卸载白屏（小号等非默认组同样） */
  const [bindListSnapshot, setBindListSnapshot] = useState(null);
  /** 桌面壳绑定事件携带的昵称/头像/profileExtra，提交时合并进 extraJson（单次消费） */
  const lastShellBindDetailRef = useRef(null);
  /**
   * 绑定写库后若壳 migrate 失败：列表已是真实 id，叠层仍挂在 pending-* channel，用 hold 映射 entity。
   * migrate 成功则清空。
   */
  const desktopCefSessionHoldRef = useRef(null);
  /**
   * 壳端对已存在 channel 每次 open2 都会 loadURL；URL 上带变化的 _wldos_reload= 会触发公众号整页重载 → 已登录态变「请重新登录」。
   * 同一账号 channel 在首次 open2 之后，再次选中只 show 不 loadURL，切换账号再切回可保留 Cookie。
   */
  const desktopPrimedChannelsRef = useRef(new Set());

  const parseAvatarFromExtra = (account) => {
    if (!account?.extraJson) return null;
    try {
      const ex = JSON.parse(account.extraJson);
      const u = ex?.avatarUrl || ex?.headImg || ex?.avatar;
      return typeof u === 'string' && u.trim() ? u.trim() : null;
    } catch (e) {
      return null;
    }
  };

  const hasDesktopBindHarvestSignal = (detail) => {
    const n = (detail?.detectedAccountName || '').trim();
    const av = (detail?.avatarUrl || '').trim();
    const pe = detail?.profileExtra;
    if (n || av) return true;
    if (pe && typeof pe === 'object') {
      if (String(pe.mpUin || '').trim()) return true;
      if (String(pe.mpUserName || '').trim()) return true;
    }
    return false;
  };

  const resolveGroupName = (raw) => {
    let name = String(raw || '默认分组');
    const visited = new Set();
    while (groupAlias[name] && !visited.has(name)) {
      visited.add(name);
      name = groupAlias[name];
    }
    return name;
  };

  const groups = useMemo(() => {
    const map = new Map();
    (accounts || []).forEach((a) => {
      let g = '默认分组';
      if (a?.extraJson) {
        try {
          const extra = JSON.parse(a.extraJson);
          if (extra?.groupName) g = String(extra.groupName);
        } catch (e) {
          // ignore
        }
      }
      g = resolveGroupName(g);
      if ((deletedGroups || []).includes(g)) return;
      if (!map.has(g)) map.set(g, []);
      map.get(g).push(a);
    });
    (customGroups || []).forEach((g) => {
      const name = resolveGroupName(g);
      if ((deletedGroups || []).includes(name)) return;
      if (!map.has(name)) map.set(name, []);
    });
    const entries = Array.from(map.entries());
    entries.sort(([nameA], [nameB]) => {
      const a0 = nameA === GROUP_SORT_DEFAULT_FIRST;
      const b0 = nameB === GROUP_SORT_DEFAULT_FIRST;
      if (a0 && !b0) return -1;
      if (!a0 && b0) return 1;
      return String(nameA).localeCompare(String(nameB), 'zh-CN');
    });
    return entries;
  }, [accounts, customGroups, deletedGroups, groupAlias]);

  /** 待绑定不写库、不占左侧列表；仅用于右侧 CEF 挂载 */
  const selected = useMemo(() => {
    if (
      pendingRegistration &&
      String(selectedAccountId) === String(pendingRegistration.tempId)
    ) {
      return {
        id: pendingRegistration.tempId,
        platform: pendingRegistration.platform,
        accountName: `${pendingRegistration.platformLabel || pendingRegistration.platform}（待绑定）`,
        platformHomeUrl: pendingRegistration.platformHomeUrl || '',
        extraJson: JSON.stringify({ groupName: pendingRegistration.groupName }),
        status: 'PENDING_LOGIN',
      };
    }
    const inList = (accounts || []).find((a) => String(a.id) === String(selectedAccountId));
    if (inList) return inList;
    if (
      bindListSnapshot &&
      String(bindListSnapshot.id) === String(selectedAccountId)
    ) {
      return bindListSnapshot;
    }
    return undefined;
  }, [accounts, selectedAccountId, pendingRegistration, bindListSnapshot]);

  useEffect(() => {
    if (!bindListSnapshot?.id) return;
    if ((accounts || []).some((a) => String(a.id) === String(bindListSnapshot.id))) {
      setBindListSnapshot(null);
    }
  }, [accounts, bindListSnapshot]);

  useEffect(() => {
    if (!open) setBindListSnapshot(null);
  }, [open]);

  /** 供 CEF 矩形 effect 使用：仅随「选中哪条账号」变化，避免 accounts 刷新导致 selected 引用变而误跑 cleanup */
  const selectedRef = useRef(selected);
  selectedRef.current = selected;
  const cefRectSyncKey = useMemo(() => {
    if (!embeddedDesktop || !open) return '';
    if (
      pendingRegistration &&
      String(selectedAccountId) === String(pendingRegistration.tempId)
    ) {
      return `p:${pendingRegistration.tempId}`;
    }
    return selectedAccountId != null && String(selectedAccountId) !== ''
      ? `a:${selectedAccountId}`
      : '';
  }, [embeddedDesktop, open, selectedAccountId, pendingRegistration]);

  const platformOptions = useMemo(
    () =>
      (platforms || [])
        .filter((p) => String(p.status || '').toUpperCase() === 'ACTIVE')
        .map((p) => ({
          label: p.name || p.code,
          value: p.code,
          hint: p.hint || '',
          home: p.homeUrl || p.platformHomeUrl || '',
        })),
    [platforms],
  );

  const selectedPlatform = platformOptions.find((p) => String(p.value) === String(selectedPlatformCode));

  /**
   * @param {'login'|'home'} [opts.entry] 微信：login=扫码登录页（仅添加流程）；home=后台首页（凭 Cookie，列表切换/绑定成功后）
   */
  const resolvePlatformHomeUrl = (account, opts = {}) => {
    const direct = String(account?.platformHomeUrl || '').trim();
    const code = String(account?.platform || '').toUpperCase();
    const entryLogin = opts.entry === 'login';
    if (code === 'WECHAT_MP' || code === 'WECHAT' || code === 'WX_MP') {
      if (entryLogin) {
        if (direct && direct.includes('/cgi-bin/loginpage')) {
          return direct;
        }
        return 'https://mp.weixin.qq.com/cgi-bin/loginpage?t=wxm2-login&lang=zh_CN';
      }
      return 'https://mp.weixin.qq.com/cgi-bin/home?t=home/index&lang=zh_CN';
    }
    if (direct) return direct;
    if (code === 'XIAOHONGSHU') return 'https://creator.xiaohongshu.com';
    if (code === 'TOUTIAO') return 'https://mp.toutiao.com';
    return '';
  };

  const resolvePlatformHomeUrlWithSeed = (account, opts = {}) => {
    const base = resolvePlatformHomeUrl(account, opts);
    if (!base) return '';
    const sep = base.includes('?') ? '&' : '?';
    return `${base}${sep}_wldos_reload=${iframeReloadSeed}`;
  };

  const resolveExternalEntityIdForCef = (account) => {
    const h = desktopCefSessionHoldRef.current;
    if (
      h &&
      account &&
      String(account.id) === String(h.boundAccountId) &&
      String(account.platform || '').toUpperCase() === String(h.platform || '').toUpperCase()
    ) {
      return h.tempEntityId;
    }
    return account?.id;
  };

  const accountExternalChannel = (account) =>
    buildDesktopExternalChannel({
      biz: 'social',
      module: 'account',
      scene: String(account?.platform || 'unknown').toLowerCase(),
      entity: resolveExternalEntityIdForCef(account),
    });

  const openAccountDesktopCef = (account, { forceReload = false, entry } = {}) => {
    if (!embeddedDesktop) return false;
    const base = resolvePlatformHomeUrl(account, entry != null ? { entry } : {});
    if (!base) return false;
    const channel = accountExternalChannel(account);
    if (forceReload) {
      desktopPrimedChannelsRef.current.delete(channel);
    }
    if (!forceReload && desktopPrimedChannelsRef.current.has(channel)) {
      return showDesktopExternal(channel);
    }
    const version = forceReload ? Date.now() : iframeReloadSeed;
    const sep = base.includes('?') ? '&' : '?';
    const targetUrl = `${base}${sep}_wldos_reload=${version}`;
    const ok = forceReload
      ? openDesktopExternalForce(channel, targetUrl)
      : openDesktopExternal(channel, targetUrl);
    if (ok !== false) {
      desktopPrimedChannelsRef.current.add(channel);
    }
    return ok;
  };

  /** 仅同步矩形 + show，不 loadURL（避免微信公众号整页重载后出现「登录超时」）。 */
  const flushDesktopEmbedRectForAccount = (account) => {
    if (!embeddedDesktop || !desktopMountRef.current || !account) return;
    const r = desktopMountRef.current.getBoundingClientRect();
    if (r.width < 4 || r.height < 4) return;
    mountDesktopExternalRect({
      x: r.left,
      y: r.top,
      width: r.width,
      height: r.height,
      viewportWidth: window.innerWidth,
      viewportHeight: window.innerHeight,
    });
    showDesktopExternal(accountExternalChannel(account));
  };

  /** 壳侧叠层依赖 mount 矩形 + show；在 modal-suppress 解除或布局稳定后必须再推一次，否则 BrowserShellFrame 仍 hide 或矩形为 0（右侧空白）。 */
  const flushDesktopEmbedRect = useCallback(() => {
    const s = selectedRef.current;
    if (s) flushDesktopEmbedRectForAccount(s);
  }, [embeddedDesktop]);

  /** 绑定后 migrate 失败时：选中真实账号行仍对应 pending channel，避免 open2 整页重载冲掉已登录页。 */
  const cefSessionHoldMatchesAccount = (a) => {
    const h = desktopCefSessionHoldRef.current;
    return (
      !!h &&
      !!a &&
      String(a.id) === String(h.boundAccountId) &&
      String(a.platform || '').toUpperCase() === String(h.platform || '').toUpperCase()
    );
  };

  /** 关闭账号管理或卸载组件时收回原生叠层矩形；勿在「已打开但挂载槽尚未插入 DOM」时 unmount，否则会误清壳侧状态且无法再次 mount（再次打开右侧空白/错位）。 */
  useEffect(() => {
    if (!embeddedDesktop || open) return undefined;
    desktopCefSessionHoldRef.current = null;
    desktopPrimedChannelsRef.current.clear();
    unmountDesktopExternalRect();
    return undefined;
  }, [embeddedDesktop, open]);

  useEffect(() => {
    const sel = selectedRef.current;
    if (!embeddedDesktop || !open || !sel || addModalOpen || !desktopMountRef.current) {
      return undefined;
    }
    const syncRect = () => {
      if (!desktopMountRef.current) return;
      const r = desktopMountRef.current.getBoundingClientRect();
      mountDesktopExternalRect({
        x: r.left,
        y: r.top,
        width: r.width,
        height: r.height,
        // 必须与 getBoundingClientRect 同属 CSS 像素坐标系；勿乘 devicePixelRatio，
        // 否则 Java 端 scale 偏小，嵌入 CEF 会向左上偏移并压住侧栏。
        viewportWidth: window.innerWidth,
        viewportHeight: window.innerHeight,
      });
    };
    syncRect();
    try {
      showDesktopExternal(accountExternalChannel(sel));
    } catch (e) {
      /* ignore */
    }
    let raf0 = 0;
    let raf1 = 0;
    const deferFlush = () => {
      syncRect();
      try {
        const cur = selectedRef.current;
        if (cur) showDesktopExternal(accountExternalChannel(cur));
      } catch (e) {
        /* ignore */
      }
    };
    raf0 = requestAnimationFrame(() => {
      raf1 = requestAnimationFrame(deferFlush);
    });
    const t1 = window.setTimeout(deferFlush, 80);
    const t2 = window.setTimeout(deferFlush, 260);
    const ro = new ResizeObserver(() => syncRect());
    ro.observe(desktopMountRef.current);
    window.addEventListener('resize', syncRect);
    // Modal body 滚动时 rect 会变，ResizeObserver 不触发
    window.addEventListener('scroll', syncRect, true);
    return () => {
      cancelAnimationFrame(raf0);
      cancelAnimationFrame(raf1);
      window.clearTimeout(t1);
      window.clearTimeout(t2);
      ro.disconnect();
      window.removeEventListener('resize', syncRect);
      window.removeEventListener('scroll', syncRect, true);
      // 勿在切换账号或列表刷新时 unmount：Java 端 clearExternalMountRect 会 hide 整个叠层，
      // 与 show 交替易打断多 channel 会话，表现为其它账号「登录超时」。
    };
  }, [
    embeddedDesktop,
    open,
    cefRectSyncKey,
    isFullscreen,
    addModalOpen,
    desktopCefSlotGeneration,
  ]);

  /** 仅子 Modal 算 blocking；勿把 submittingPendingBind 算入，否则提交前后与壳 open/mount 交错易白屏。 */
  const overlayBlocking = groupManageOpen || addModalOpen;
  const prevOverlayBlockingRef = useRef(undefined);
  useEffect(() => {
    const prev = prevOverlayBlockingRef.current;
    prevOverlayBlockingRef.current = overlayBlocking;
    if (!embeddedDesktop || !open || !selectedRef.current) return undefined;
    if (overlayBlocking) return undefined;
    if (prev === undefined) return undefined;
    if (!(prev === true && overlayBlocking === false)) return undefined;
    const run = () => flushDesktopEmbedRect();
    requestAnimationFrame(() => requestAnimationFrame(run));
    const tid = window.setTimeout(run, 180);
    return () => window.clearTimeout(tid);
  }, [overlayBlocking, embeddedDesktop, open, cefRectSyncKey, flushDesktopEmbedRect]);

  // 新增账号弹窗打开时，保证有默认分组选中，避免 Select 无选项导致 validateFields 卡住/失败。
  useEffect(() => {
    if (!addModalOpen) return;
    const current = addForm.getFieldValue('groupName');
    if (!current) {
      addForm.setFieldsValue({ groupName: '默认分组' });
    }
  }, [addModalOpen, addForm]);

  const createGroup = () => {
    let newName = '';
    Modal.confirm({
      title: '创建新分组',
      content: (
        <Input
          placeholder="请输入分组名称"
          onChange={(e) => {
            newName = e.target.value;
          }}
        />
      ),
      onOk: () => {
        const name = String(newName || '').trim();
        if (!name) {
          return Promise.reject(new Error('分组名称不能为空'));
        }
        if (groups.some(([g]) => g === name)) {
          return Promise.reject(new Error('分组已存在'));
        }
        setCustomGroups((prev) => [...prev, name]);
        addForm.setFieldsValue({ groupName: name });
        return Promise.resolve();
      },
    });
  };

  const renameGroup = (oldName) => {
    let nextName = oldName;
    Modal.confirm({
      title: `重命名分组：${oldName}`,
      content: (
        <Input
          defaultValue={oldName}
          onChange={(e) => {
            nextName = e.target.value;
          }}
        />
      ),
      onOk: () => {
        const name = String(nextName || '').trim();
        if (!name) return Promise.reject(new Error('分组名称不能为空'));
        if (name === oldName) return Promise.resolve();
        if (groups.some(([g]) => g === name)) return Promise.reject(new Error('分组已存在'));
        setGroupAlias((prev) => ({ ...prev, [oldName]: name }));
        setCustomGroups((prev) => prev.map((g) => (g === oldName ? name : g)));
        return Promise.resolve();
      },
    });
  };

  const deleteGroup = (name, list) => {
    if ((list || []).length > 0) {
      message.warning('分组下仍有账号，无法删除');
      return;
    }
    setDeletedGroups((prev) => [...prev, name]);
    setCustomGroups((prev) => prev.filter((g) => g !== name));
  };

  /** 第一步：只校验平台与分组，打开登录页（不写库） */
  const handleGoToPlatformLogin = async () => {
    if (addAccountSubmitLockRef.current || addingAccount) return;
    if (!selectedPlatformCode) {
      message.error('请先选择平台');
      return;
    }
    addAccountSubmitLockRef.current = true;
    setAddingAccount(true);
    try {
      desktopCefSessionHoldRef.current = null;
      const values = await addForm.validateFields();
      const tempId = `pending-${Date.now()}-${selectedPlatformCode}`;
      const platformLabel = selectedPlatform?.label || selectedPlatformCode;
      const groupName = values.groupName || '默认分组';
      const platformHomeUrl = selectedPlatform?.home || '';
      setPendingRegistration({
        tempId,
        platform: selectedPlatformCode,
        groupName,
        platformHomeUrl,
        platformLabel,
      });
      setPendingDisplayName('');
      setSelectedAccountId(tempId);
      const virtualAccount = {
        id: tempId,
        platform: selectedPlatformCode,
        accountName: `${platformLabel}（待绑定）`,
        platformHomeUrl,
        extraJson: JSON.stringify({ groupName }),
      };
      if (embeddedDesktop) {
        openAccountDesktopCef(virtualAccount, { forceReload: true, entry: 'login' });
      }
      message.info(
        embeddedDesktop
          ? '已打开登录页，扫码进入公众号后台后将自动提交账号'
          : '请在右侧页面完成登录；登录成功后点击下方「登录完成，提交账号」写入系统',
      );
      setAddModalOpen(false);
      setSelectedPlatformCode('');
      addForm.resetFields();
    } catch (e) {
      message.error(e?.message || '请完善表单');
    } finally {
      setAddingAccount(false);
      addAccountSubmitLockRef.current = false;
    }
  };

  /** 第二步：登录完成后调用绑定 API（完整事务） */
  const submitPendingBindToServer = async () => {
    if (!pendingRegistration || submitPendingBindLockRef.current || submittingPendingBind) return;
    if (!onAddAccount) {
      message.error('缺少添加账号处理器(onAddAccount)');
      return;
    }
    submitPendingBindLockRef.current = true;
    setSubmittingPendingBind(true);
    try {
      const shellDetail = lastShellBindDetailRef.current;
      lastShellBindDetailRef.current = null;
      const displayName =
        (pendingDisplayName || '').trim() ||
        (shellDetail?.detectedAccountName || '').trim() ||
        pendingRegistration.platformLabel ||
        pendingRegistration.platform;
      const extraPayload = { groupName: pendingRegistration.groupName };
      if (shellDetail?.avatarUrl) {
        extraPayload.avatarUrl = shellDetail.avatarUrl;
      }
      if (shellDetail?.profileExtra && typeof shellDetail.profileExtra === 'object') {
        Object.assign(extraPayload, shellDetail.profileExtra);
      }
      const bindResult = await onAddAccount({
        platform: pendingRegistration.platform,
        accountName: displayName,
        groupName: pendingRegistration.groupName,
        platformHomeUrl: pendingRegistration.platformHomeUrl || '',
        extraJson: JSON.stringify(extraPayload),
      });
      const created = bindResult?.account != null ? bindResult.account : bindResult;
      const replacedPreviousAccountId = bindResult?.replacedPreviousAccountId;
      const pr = pendingRegistration;
      const surface =
        created?.id != null && pr != null
          ? {
              id: created.id,
              platform: pr.platform,
              accountName: created.accountName || displayName,
              platformHomeUrl: created.platformHomeUrl || pr.platformHomeUrl || '',
              extraJson: created.extraJson || JSON.stringify({ groupName: pr.groupName }),
              status: created.status || 'ACTIVE',
            }
          : null;
      if (surface) {
        setBindListSnapshot(surface);
      }
      /**
       * 同身份删旧建新：关掉旧 id 的 CEF（与后端删旧行一致）。
       */
      if (
        embeddedDesktop &&
        replacedPreviousAccountId != null &&
        pr?.platform != null
      ) {
        const oldCh = buildDesktopExternalChannel({
          biz: 'social',
          module: 'account',
          scene: String(pr.platform || 'unknown').toLowerCase(),
          entity: replacedPreviousAccountId,
        });
        closeDesktopExternal(oldCh);
        desktopPrimedChannelsRef.current.delete(oldCh);
      }
      /**
       * 桌面端：pending channel 上已完成扫码登录，须 migrate 到真实账号 channel，保留同一 CefRequestContext/Cookie；
       * 勿先 close 再 open 登录页，否则新 channel 无 Cookie，表现为「刚登录成功又未登录」。
       */
      if (embeddedDesktop && pr?.tempId != null && created?.id != null && surface) {
        const fromCh = buildDesktopExternalChannel({
          biz: 'social',
          module: 'account',
          scene: String(pr.platform || 'unknown').toLowerCase(),
          entity: pr.tempId,
        });
        const toCh = buildDesktopExternalChannel({
          biz: 'social',
          module: 'account',
          scene: String(pr.platform || 'unknown').toLowerCase(),
          entity: created.id,
        });
        desktopPrimedChannelsRef.current.delete(fromCh);
        desktopPrimedChannelsRef.current.delete(toCh);
        desktopCefSessionHoldRef.current = null;
        try {
          await migrateDesktopExternalChannelAsync(fromCh, toCh);
          desktopPrimedChannelsRef.current.add(toCh);
          try {
            showDesktopExternal(toCh);
          } catch (ignored) {
            /* ignore */
          }
          flushDesktopEmbedRectForAccount(surface);
          setDesktopCefSlotGeneration((n) => n + 1);
        } catch (migErr) {
          message.warning(
            (migErr && migErr.message) || '会话迁移失败，将重新打开页面，若未登录请重新扫码',
          );
          try {
            closeDesktopExternal(fromCh);
          } catch (ignored) {
            /* ignore */
          }
          window.setTimeout(() => {
            openAccountDesktopCef(surface, { forceReload: true, entry: 'home' });
            flushDesktopEmbedRectForAccount(surface);
            setDesktopCefSlotGeneration((n) => n + 1);
          }, 180);
        }
      }
      setPendingRegistration(null);
      setPendingDisplayName('');
      if (created?.id != null) {
        setSelectedAccountId(created.id);
      }
      if (typeof onRefresh === 'function') {
        try {
          await onRefresh();
        } catch (ignored) {
          /* ignore */
        }
      }
    } catch (e) {
      message.error(e?.message || '提交失败');
    } finally {
      submitPendingBindLockRef.current = false;
      setSubmittingPendingBind(false);
    }
  };

  const submitPendingBindLatestRef = useRef(submitPendingBindToServer);
  submitPendingBindLatestRef.current = submitPendingBindToServer;

  /** 桌面壳 URL 探测登录成功后派发事件 → 静默调用绑定 API（§3.3），与手动「提交账号」同一路径 */
  useEffect(() => {
    if (!embeddedDesktop || !open || !pendingRegistration) {
      return undefined;
    }
    const unsub = subscribeSocialBindSessionReady((detail) => {
      const pr = pendingRegistration;
      if (!pr) return;
      const expected = buildDesktopExternalChannel({
        biz: 'social',
        module: 'account',
        scene: String(pr.platform || 'unknown').toLowerCase(),
        entity: pr.tempId,
      });
      if (String(detail.channel || '') !== expected) return;
      if (
        String(detail.platform || '').toUpperCase() !== String(pr.platform || '').toUpperCase()
      ) {
        return;
      }
      if (submitPendingBindLockRef.current || submittingPendingBind) return;
      if (!hasDesktopBindHarvestSignal(detail)) {
        message.warning('未识别到公众号后台登录信息，请在右侧完成扫码并进入后台首页后再试');
        return;
      }
      lastShellBindDetailRef.current = detail;
      submitPendingBindLatestRef.current();
    });
    return unsub;
  }, [embeddedDesktop, open, pendingRegistration, submittingPendingBind]);

  /** 仅在有 Ant 子 Modal 时隐藏 JCEF 叠层（与 submittingPendingBind 解耦，见 overlayBlocking 注释）。 */
  useEffect(() => {
    if (!embeddedDesktop || !open) return undefined;
    const blocking = groupManageOpen || addModalOpen;
    if (blocking) {
      setDesktopExternalModalSuppressed(true);
      return () => setDesktopExternalModalSuppressed(false);
    }
    return undefined;
  }, [embeddedDesktop, open, groupManageOpen, addModalOpen]);

  useEffect(() => {
    if (!embeddedDesktop || open) return undefined;
    setDesktopExternalModalSuppressed(false);
    return undefined;
  }, [embeddedDesktop, open]);

  const cancelPendingBindManually = useCallback(() => {
    desktopCefSessionHoldRef.current = null;
    setPendingRegistration((prev) => {
      if (prev && embeddedDesktop) {
        try {
          const ch = buildDesktopExternalChannel({
            biz: 'social',
            module: 'account',
            scene: String(prev.platform || 'unknown').toLowerCase(),
            entity: prev.tempId,
          });
          desktopPrimedChannelsRef.current.delete(ch);
          closeDesktopExternal(ch);
        } catch (ignored) {
          /* ignore */
        }
      }
      return null;
    });
    setPendingDisplayName('');
    setSelectedAccountId((id) => (String(id || '').startsWith('pending-') ? null : id));
  }, [embeddedDesktop]);

  /** 关闭主面板时清理待绑定状态并关闭临时 CEF */
  useEffect(() => {
    if (open) return undefined;
    cancelPendingBindManually();
    return undefined;
  }, [open, cancelPendingBindManually]);

  const platformIcon = (code) => {
    const c = String(code || '').toUpperCase();
    if (c.includes('WECHAT')) return '微';
    if (c.includes('XIAOHONGSHU')) return '红';
    if (c.includes('TOUTIAO')) return '头';
    if (c.includes('DOUYIN')) return '抖';
    return String(c || '?').slice(0, 1);
  };

  const viewportContentHeight = isFullscreen
    ? 'calc(100vh - 220px)'
    : 'auto';
  const creatorAreaHeight = isFullscreen
    ? 'calc(100vh - 390px)'
    : 520;
  const modalWidth = isFullscreen ? '100vw' : (embeddedDesktop ? 1480 : 1120);

  return (
    <>
    <Modal
      title={(
        <div style={{ display: 'flex', justifyContent: 'flex-start', alignItems: 'center', gap: 8 }}>
          <span>账号管理</span>
          <Button
            size="small"
            type="text"
            icon={isFullscreen ? <FullscreenExitOutlined /> : <FullscreenOutlined />}
            onClick={(e) => {
              e.stopPropagation();
              setIsFullscreen((v) => !v);
            }}
          />
        </div>
      )}
      open={open}
      onCancel={onClose}
      onOk={onClose}
      width={modalWidth}
      style={isFullscreen ? { top: 0, paddingBottom: 0, maxWidth: '100vw' } : undefined}
      bodyStyle={
        isFullscreen
          ? { height: 'calc(100vh - 110px)', overflowY: 'auto' }
          : { maxHeight: 'calc(100vh - 140px)', overflowY: 'auto' }
      }
      maskClosable={false}
      okText="关闭"
      cancelButtonProps={{ style: { display: 'none' } }}
      destroyOnClose
      afterClose={onRefresh}
      afterOpenChange={(v) => {
        if (!v && isDesktopEmbedded() && selected) {
          // 勿 closeDesktopExternal：会话保留在壳内，重开 modal 后 open2 仅切前台不 loadURL，才能直接回到上次后台页。
          desktopCefSessionHoldRef.current = null;
          desktopPrimedChannelsRef.current.clear();
        } else if (v && embeddedDesktop && selected) {
          if (cefSessionHoldMatchesAccount(selected)) {
            window.setTimeout(() => flushDesktopEmbedRectForAccount(selected), 0);
          } else {
            openAccountDesktopCef(selected);
          }
        }
      }}
    >
      <>
        {pendingRegistration ? (
          embeddedDesktop ? (
            // 桌面壳内：登录中只保留「取消本次添加」入口；登录成功后 pendingRegistration 被
            // submitPendingBindToServer 清空，整块自然消失。无需再显示"登录公众平台并进入后台后..."提示，
            // 该信息由「+」按钮被 disabled、左侧出现"待绑定"虚拟行隐含传达。
            <div
              style={{
                marginBottom: 12,
                display: 'flex',
                justifyContent: 'flex-end',
                alignItems: 'center',
                flexWrap: 'wrap',
                gap: 8,
              }}
            >
              <Button
                type="link"
                size="small"
                danger
                disabled={submittingPendingBind}
                onClick={cancelPendingBindManually}
              >
                取消本次添加
              </Button>
            </div>
          ) : (
            <Alert
              type="info"
              showIcon
              style={{ marginBottom: 12 }}
              message="完成平台登录后再提交账号"
              description={(
                <Space direction="vertical" style={{ width: '100%' }} size={12}>
                  <Typography.Text type="secondary">
                    当前未写入数据库。请在右侧完成登录后，填写显示名称（可选），再点击提交。
                  </Typography.Text>
                  <Input
                    placeholder={`显示名称，默认：${pendingRegistration.platformLabel}`}
                    value={pendingDisplayName}
                    onChange={(e) => setPendingDisplayName(e.target.value)}
                    allowClear
                  />
                  <Space wrap>
                    <Button
                      type="primary"
                      loading={submittingPendingBind}
                      disabled={submittingPendingBind}
                      onClick={() => submitPendingBindToServer()}
                    >
                      登录完成，提交账号
                    </Button>
                    <Button
                      type="link"
                      danger
                      disabled={submittingPendingBind}
                      onClick={cancelPendingBindManually}
                    >
                      取消本次添加
                    </Button>
                  </Space>
                </Space>
              )}
            />
          )
        ) : null}
      <div style={{ display: 'flex', minHeight: 560, height: viewportContentHeight, border: '1px solid #f0f0f0', borderRadius: 6, overflow: 'hidden' }}>
        <div style={{ width: 44, borderRight: '1px solid #f0f0f0', padding: '8px 4px', background: '#fafafa' }}>
          <Space direction="vertical" size={10} style={{ width: '100%', alignItems: 'center' }}>
            <Tooltip title="搜索">
              <Button size="small" type="text" icon={<SearchOutlined />} />
            </Tooltip>
            <Tooltip title="账号">
              <Button size="small" type="text" icon={<UserOutlined />} />
            </Tooltip>
            <Tooltip title="分组">
              <Button size="small" type="text" icon={<TeamOutlined />} />
            </Tooltip>
            <Tooltip title="分组与账号管理">
              <Button
                size="small"
                type="text"
                icon={<SettingOutlined />}
                onClick={() => setGroupManageOpen(true)}
              />
            </Tooltip>
          </Space>
        </div>

        <div style={{ width: 320, borderRight: '1px solid #f0f0f0', padding: 10, display: 'flex', flexDirection: 'column' }}>
          <div style={{ display: 'flex', gap: 8, marginBottom: 8 }}>
            <Input
              placeholder="搜索账号/平台"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
            />
            <Tooltip title={pendingRegistration ? '请先完成待绑定流程或取消后再添加' : '添加账号'}>
              <Button
                type="primary"
                icon={<PlusOutlined />}
                disabled={!!pendingRegistration}
                onClick={() => setAddModalOpen(true)}
              />
            </Tooltip>
          </div>
          <div style={{ flex: 1, overflow: 'auto' }}>
            {groups.length ? (
              <Collapse
                bordered={false}
                defaultActiveKey={groups.map(([name]) => name)}
              >
                {groups.map(([name, list]) => (
                  <Panel
                    key={name}
                    header={(
                      <Dropdown
                        trigger={['contextMenu']}
                        overlay={(
                          <Menu
                            onClick={({ key, domEvent }) => {
                              domEvent?.stopPropagation();
                              if (key === 'add') createGroup();
                              if (key === 'rename') renameGroup(name);
                              if (key === 'delete') deleteGroup(name, list);
                            }}
                          >
                            <Menu.Item key="add">添加新分组</Menu.Item>
                            <Menu.Item key="rename">重命名分组</Menu.Item>
                            <Menu.Item key="delete">删除分组</Menu.Item>
                          </Menu>
                        )}
                      >
                        <span onClick={(e) => e.stopPropagation()}>{`${name} (${list.length})`}</span>
                      </Dropdown>
                    )}
                  >
                    {(list || [])
                      .filter((a) => {
                        const k = keyword.trim().toLowerCase();
                        if (!k) return true;
                        const n = String(a.accountName || '').toLowerCase();
                        const p = String(a.platform || '').toLowerCase();
                        return n.includes(k) || p.includes(k);
                      })
                      .map((a) => (
                        <div
                          key={a.id}
                          onClick={() => {
                            const h = desktopCefSessionHoldRef.current;
                            if (h && String(a.id) !== String(h.boundAccountId)) {
                              desktopCefSessionHoldRef.current = null;
                            }
                            setSelectedAccountId(a.id);
                            if (!embeddedDesktop) return;
                            if (cefSessionHoldMatchesAccount(a)) {
                              window.requestAnimationFrame(() =>
                                window.requestAnimationFrame(() => flushDesktopEmbedRectForAccount(a)),
                              );
                            } else {
                              openAccountDesktopCef(a);
                            }
                          }}
                          style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: 8,
                            padding: '7px 8px',
                            borderRadius: 4,
                            cursor: 'pointer',
                            marginBottom: 4,
                            background: String(selectedAccountId) === String(a.id) ? '#e6f7ff' : 'transparent',
                          }}
                        >
                          <Avatar size={24} src={parseAvatarFromExtra(a) || undefined}>
                            {String(a.accountName || a.platform || '?').slice(0, 1)}
                          </Avatar>
                          <div style={{ minWidth: 0 }}>
                            <Typography.Text ellipsis style={{ display: 'block', maxWidth: 220 }}>
                              {a.accountName || a.platform}
                            </Typography.Text>
                            <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                              {a.platform || '-'}
                            </Typography.Text>
                          </div>
                        </div>
                      ))}
                  </Panel>
                ))}
              </Collapse>
            ) : (
              <Empty description="暂无账号，请先点击右上角 + 添加" />
            )}
          </div>
        </div>

        <div style={{ flex: 1, padding: embeddedDesktop ? 0 : 16 }}>
          {selected ? (
            embeddedDesktop ? (
              <div style={{ height: creatorAreaHeight, border: '1px solid #f0f0f0', borderRadius: 4, overflow: 'hidden', padding: 0 }}>
                <div ref={bindDesktopCefMountRef} style={{ height: '100%', background: '#fff' }} />
              </div>
            ) : (
              <Space direction="vertical" style={{ width: '100%' }} size={10}>
                <Typography.Title level={5} style={{ margin: 0 }}>
                  {selected.accountName || selected.platform}
                </Typography.Title>
                <Space>
                  <Tag>{selected.platform || '-'}</Tag>
                  <Tag color={(selected.status || '').toUpperCase() === 'ACTIVE' ? 'green' : 'default'}>
                    {selected.status || 'UNKNOWN'}
                  </Tag>
                  <Button size="small" onClick={() => setIframeReloadSeed((v) => v + 1)}>刷新登录页</Button>
                </Space>
                <Typography.Text type="secondary">
                  右侧承载平台创作中心。未登录时平台会自动跳转登录页；登录完成后请关闭再打开本面板以刷新状态。
                </Typography.Text>
                <Divider style={{ margin: '12px 0' }} />
                <div style={{ height: creatorAreaHeight, border: '1px solid #f0f0f0', borderRadius: 4, overflow: 'hidden' }}>
                  {resolvePlatformHomeUrlWithSeed(selected) ? (
                    <iframe
                      key={`${selected?.id || 'selected'}-${iframeReloadSeed}`}
                      title={`creator-center-${selected.id}`}
                      src={resolvePlatformHomeUrlWithSeed(selected)}
                      style={{ border: 'none', width: '100%', height: '100%' }}
                    />
                  ) : (
                    <div style={{ padding: 24 }}>
                      <Empty description="该账号未配置创作中心地址" />
                    </div>
                  )}
                </div>
              </Space>
            )
          ) : (
            <Empty description="请在中间区域选择一个账号，右侧将打开对应平台创作中心" />
          )}
        </div>
      </div>
      </>
    </Modal>
    <Modal
      title="添加账号"
      open={addModalOpen}
      onCancel={() => {
        setAddModalOpen(false);
        setSelectedPlatformCode('');
        addForm.resetFields();
      }}
      onOk={handleGoToPlatformLogin}
      confirmLoading={addingAccount}
      okButtonProps={{ disabled: addingAccount }}
      okText="前往登录"
      cancelText="取消"
      destroyOnClose
    >
      <Space direction="vertical" style={{ width: '100%' }} size={14}>
        <div>
          <Typography.Text strong>选择平台</Typography.Text>
          <div style={{ marginTop: 8, display: 'grid', gridTemplateColumns: 'repeat(4, minmax(0, 1fr))', gap: 8 }}>
            {platformOptions.length ? platformOptions.map((p) => (
              <div
                key={p.value}
                onClick={() => setSelectedPlatformCode(p.value)}
                style={{
                  border: String(selectedPlatformCode) === String(p.value) ? '1px solid #1890ff' : '1px solid #f0f0f0',
                  borderRadius: 6,
                  padding: 8,
                  cursor: 'pointer',
                  textAlign: 'center',
                  background: String(selectedPlatformCode) === String(p.value) ? '#e6f7ff' : '#fff',
                }}
              >
                <Avatar size={26} style={{ background: '#1677ff', marginBottom: 6 }}>
                  {platformIcon(p.value)}
                </Avatar>
                <div style={{ fontSize: 12, lineHeight: 1.2 }}>{p.label}</div>
              </div>
            )) : <Empty description="暂无可用平台" />}
          </div>
        </div>
        <Form form={addForm} layout="vertical">
          <Form.Item label="归属分组" name="groupName" rules={[{ required: true, message: '请输入分组名称' }]}>
            <Select
              showSearch
              placeholder="选择或输入新分组"
              options={
                groups.length
                  ? groups.map(([name]) => ({ label: name, value: name }))
                  : [{ label: '默认分组', value: '默认分组' }]
              }
              dropdownRender={(menu) => (
                <>
                  {menu}
                  <Divider style={{ margin: '8px 0' }} />
                  <div style={{ padding: '0 8px 8px' }}>
                    <Button type="link" style={{ padding: 0 }} onClick={createGroup}>创建新的分组</Button>
                  </div>
                </>
              )}
              onSearch={(v) => addForm.setFieldsValue({ groupName: v })}
            />
          </Form.Item>
        </Form>
      </Space>
    </Modal>
    <AccountGroupManageModal
      open={groupManageOpen}
      onClose={() => setGroupManageOpen(false)}
      accounts={accounts}
      onRefresh={onRefresh}
    />
    </>
  );
};

export default AccountManageModal;
