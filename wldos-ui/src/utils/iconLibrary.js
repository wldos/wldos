// 统一图标库管理
import {
  // 基础图标
  HomeOutlined,
  SmileOutlined,
  FormOutlined,
  UnorderedListOutlined,
  TableOutlined,
  SettingOutlined,
  UserOutlined,
  TeamOutlined,
  DatabaseOutlined,
  FileTextOutlined,
  BookOutlined,
  CommentOutlined,
  PictureOutlined,
  GlobalOutlined,
  ToolOutlined,
  SearchOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  EyeOutlined,
  DownloadOutlined,
  UploadOutlined,
  ReloadOutlined,
  SaveOutlined,
  CloseOutlined,
  CheckOutlined,
  InfoCircleOutlined,
  WarningOutlined,
  QuestionCircleOutlined,
  ExclamationCircleOutlined,
  HeartOutlined,
  StarOutlined,
  LikeOutlined,
  DislikeOutlined,
  ThunderboltOutlined,
  FireOutlined,
  CrownOutlined,
  TrophyOutlined,
  GiftOutlined,
  ShoppingCartOutlined,
  DollarOutlined,
  BankOutlined,
  CreditCardOutlined,
  WalletOutlined,
  ShopOutlined,
  CarOutlined,
  // 高优先级图标 - 系统相关
  AppstoreOutlined,
  DashboardOutlined,
  DesktopOutlined,
  ApiOutlined,
  BugOutlined,
  CodeOutlined,
  ConsoleSqlOutlined,
  FunctionOutlined,
  NodeIndexOutlined,
  ExperimentOutlined,
  // 高优先级图标 - 文件相关
  FileOutlined,
  FolderOutlined,
  FolderOpenOutlined,
  FilePdfOutlined,
  FileWordOutlined,
  FileExcelOutlined,
  FilePptOutlined,
  FileImageOutlined,
  FileZipOutlined,
  // 高优先级图标 - 网络相关
  WifiOutlined,
  CloudOutlined,
  CloudUploadOutlined,
  CloudDownloadOutlined,
  CloudServerOutlined,
  // 高优先级图标 - 时间相关
  ClockCircleOutlined,
  CalendarOutlined,
  ScheduleOutlined,
  HistoryOutlined,
  FieldTimeOutlined,
  // 高优先级图标 - 安全相关
  SafetyOutlined,
  SecurityScanOutlined,
  LockOutlined,
  UnlockOutlined,
  KeyOutlined,
  // 高优先级图标 - 状态相关
  CheckCircleOutlined,
  CloseCircleOutlined,
  StopOutlined,
  PlayCircleOutlined,
  PauseCircleOutlined,
  // 高优先级图标 - 方向相关
  UpOutlined,
  DownOutlined,
  LeftOutlined,
  RightOutlined,
  UpCircleOutlined,
  DownCircleOutlined,
  LeftCircleOutlined,
  RightCircleOutlined,
  // 高优先级图标 - 媒体相关
  VideoCameraOutlined,
  CameraOutlined,
  SoundOutlined,
  ForwardOutlined,
  BackwardOutlined,
  // 实心图标
  HomeFilled,
  SmileFilled,
  HeartFilled,
  StarFilled,
  CrownFilled,
  TrophyFilled,
  ThunderboltFilled,
  FireFilled,
  LikeFilled,
  DislikeFilled
} from '@ant-design/icons';

// 图标映射表
export const iconMap = {
  // 基础图标
  home: <HomeOutlined />,
  smile: <SmileOutlined />,
  form: <FormOutlined />,
  list: <UnorderedListOutlined />,
  table: <TableOutlined />,
  setting: <SettingOutlined />,
  user: <UserOutlined />,
  team: <TeamOutlined />,
  database: <DatabaseOutlined />,
  'file-text': <FileTextOutlined />,
  book: <BookOutlined />,
  comment: <CommentOutlined />,
  picture: <PictureOutlined />,
  global: <GlobalOutlined />,
  tool: <ToolOutlined />,
  search: <SearchOutlined />,
  plus: <PlusOutlined />,
  edit: <EditOutlined />,
  delete: <DeleteOutlined />,
  eye: <EyeOutlined />,
  download: <DownloadOutlined />,
  upload: <UploadOutlined />,
  reload: <ReloadOutlined />,
  save: <SaveOutlined />,
  close: <CloseOutlined />,
  check: <CheckOutlined />,
  info: <InfoCircleOutlined />,
  warning: <WarningOutlined />,
  question: <QuestionCircleOutlined />,
  exclamation: <ExclamationCircleOutlined />,
  heart: <HeartOutlined />,
  star: <StarOutlined />,
  like: <LikeOutlined />,
  dislike: <DislikeOutlined />,
  thunderbolt: <ThunderboltOutlined />,
  fire: <FireOutlined />,
  crown: <CrownOutlined />,
  trophy: <TrophyOutlined />,
  gift: <GiftOutlined />,
  'shopping-cart': <ShoppingCartOutlined />,
  dollar: <DollarOutlined />,
  bank: <BankOutlined />,
  'credit-card': <CreditCardOutlined />,
  wallet: <WalletOutlined />,
  shop: <ShopOutlined />,
  car: <CarOutlined />,

  // 高优先级图标 - 系统相关
  appstore: <AppstoreOutlined />,
  dashboard: <DashboardOutlined />,
  desktop: <DesktopOutlined />,
  api: <ApiOutlined />,
  bug: <BugOutlined />,
  code: <CodeOutlined />,
  'console-sql': <ConsoleSqlOutlined />,
  function: <FunctionOutlined />,
  'node-index': <NodeIndexOutlined />,
  experiment: <ExperimentOutlined />,

  // 高优先级图标 - 文件相关
  file: <FileOutlined />,
  folder: <FolderOutlined />,
  'folder-open': <FolderOpenOutlined />,
  'file-pdf': <FilePdfOutlined />,
  'file-word': <FileWordOutlined />,
  'file-excel': <FileExcelOutlined />,
  'file-ppt': <FilePptOutlined />,
  'file-image': <FileImageOutlined />,
  'file-zip': <FileZipOutlined />,

  // 高优先级图标 - 网络相关
  wifi: <WifiOutlined />,
  cloud: <CloudOutlined />,
  'cloud-upload': <CloudUploadOutlined />,
  'cloud-download': <CloudDownloadOutlined />,
  'cloud-server': <CloudServerOutlined />,

  // 高优先级图标 - 时间相关
  clock: <ClockCircleOutlined />,
  calendar: <CalendarOutlined />,
  schedule: <ScheduleOutlined />,
  history: <HistoryOutlined />,
  'field-time': <FieldTimeOutlined />,

  // 高优先级图标 - 安全相关
  safety: <SafetyOutlined />,
  'security-scan': <SecurityScanOutlined />,
  lock: <LockOutlined />,
  unlock: <UnlockOutlined />,
  key: <KeyOutlined />,

  // 高优先级图标 - 状态相关
  'check-circle': <CheckCircleOutlined />,
  'close-circle': <CloseCircleOutlined />,
  'exclamation-circle': <ExclamationCircleOutlined />,
  stop: <StopOutlined />,
  'play-circle': <PlayCircleOutlined />,
  'pause-circle': <PauseCircleOutlined />,

  // 高优先级图标 - 方向相关
  up: <UpOutlined />,
  down: <DownOutlined />,
  left: <LeftOutlined />,
  right: <RightOutlined />,
  'up-circle': <UpCircleOutlined />,
  'down-circle': <DownCircleOutlined />,
  'left-circle': <LeftCircleOutlined />,
  'right-circle': <RightCircleOutlined />,

  // 高优先级图标 - 媒体相关
  'video-camera': <VideoCameraOutlined />,
  camera: <CameraOutlined />,
  sound: <SoundOutlined />,
  forward: <ForwardOutlined />,
  backward: <BackwardOutlined />,

  // 实心图标
  'home-filled': <HomeFilled />,
  'smile-filled': <SmileFilled />,
  'heart-filled': <HeartFilled />,
  'star-filled': <StarFilled />,
  'crown-filled': <CrownFilled />,
  'trophy-filled': <TrophyFilled />,
  'thunderbolt-filled': <ThunderboltFilled />,
  'fire-filled': <FireFilled />,
  'like-filled': <LikeFilled />,
  'dislike-filled': <DislikeFilled />
};

/**
 * 渲染图标
 * @param {string|object} iconData - 图标数据，可以是字符串或对象
 * @returns {boolean} 渲染的图标组件
 */
const isUrlLike = (s) => typeof s === 'string' && /^(https?:|\/|data:)/i.test(s);
const isImageExt = (s) => typeof s === 'string' && /\.(svg|png|jpg|jpeg|gif|webp)$/i.test(s);

export const renderIcon = (iconData) => {
  if (!iconData) return null;

  // 如果 iconData 是字符串（兼容旧格式）
  if (typeof iconData === 'string') {
    // URL 或图片扩展名，按图片渲染
    if (isUrlLike(iconData) || isImageExt(iconData)) {
      return <img src={iconData} alt="icon" style={{ width: 16, height: 16, objectFit: 'contain' }} />;
    }
    // 命中内置映射
    if (iconMap[iconData]) {
      return iconMap[iconData];
    }
    // 其他按自定义文本渲染
    return <span style={{
      display: 'inline-flex',
      alignItems: 'center',
      justifyContent: 'center',
      width: 16,
      height: 16,
      fontSize: 12,
      borderRadius: 3,
      background: '#f5f5f5',
      color: '#595959',
      lineHeight: '16px'
    }}>{iconData.length > 2 ? iconData[0] : iconData}</span>;
  }

  // 如果 iconData 是对象（新格式）
  if (typeof iconData === 'object') {
    const { type, name, value } = iconData;

    switch (type) {
      case 'antd':
        return iconMap[name] || iconMap[value] || <SmileOutlined />;
      case 'custom':
        // 自定义图标，这里可以扩展支持更多自定义图标库
        return <span style={{ fontSize: '16px' }}>{name || value}</span>;
      case 'url':
        // URL 图标
        return <img src={value} alt="icon" style={{ width: 16, height: 16, objectFit: 'contain' }} />;
      default:
        return <SmileOutlined />;
    }
  }

  return <SmileOutlined />;
};

// 获取所有可用图标名称
export const getAvailableIcons = () => {
  return Object.keys(iconMap);
};

// 检查图标是否存在
export const hasIcon = (iconName) => {
  return iconName in iconMap;
};
