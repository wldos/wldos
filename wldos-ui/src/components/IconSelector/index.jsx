import React, { useMemo, useState } from 'react';
import { Input, Row, Col, Tooltip, Tabs, Badge } from 'antd';
import { iconMap, getAvailableIcons } from '@/utils/iconLibrary';

const IconSelector = ({ value, onChange, placeholder = "选择图标" }) => {
  // 处理初始值，兼容字符串格式
  const getInitialValue = () => {
    if (!value) return null;
    if (typeof value === 'string') {
      return { type: 'antd', name: value, value: value };
    }
    return value;
  };

  const initialValue = getInitialValue();
  const [iconType, setIconType] = useState(initialValue?.type || 'antd');
  const [customIcon, setCustomIcon] = useState(initialValue?.type === 'custom' ? initialValue?.value : '');
  const [customUrl, setCustomUrl] = useState(initialValue?.type === 'url' ? initialValue?.value : '');
  const [activeTab, setActiveTab] = useState('all');

  // 图标分类定义
  const iconCategories = {
    'all': { name: '全部图标', color: '#1890ff' },
    'basic': { name: '基础图标', color: '#52c41a' },
    'navigation': { name: '导航图标', color: '#fa8c16' },
    'media': { name: '媒体图标', color: '#eb2f96' },
    'system': { name: '系统图标', color: '#722ed1' },
    'business': { name: '业务图标', color: '#13c2c2' },
    'status': { name: '状态图标', color: '#f5222d' }
  };

  // 图标分类映射
  const getIconCategory = (iconName) => {
    const categoryMap = {
      // 基础图标
      'home': 'basic',
      'user': 'basic',
      'team': 'basic',
      'setting': 'basic',
      'search': 'basic',
      'plus': 'basic',
      'edit': 'basic',
      'delete': 'basic',
      'close': 'basic',
      'check': 'basic',
      'info': 'basic',
      'warning': 'basic',
      'question': 'basic',
      'exclamation': 'basic',
      'eye': 'basic',
      'tool': 'basic',
      
      // 导航图标
      'menu': 'navigation',
      'appstore': 'navigation',
      'global': 'navigation',
      'reload': 'navigation',
      'download': 'navigation',
      'upload': 'navigation',
      'dashboard': 'navigation',
      'desktop': 'navigation',
      
      // 媒体图标
      'picture': 'media',
      'file': 'media',
      'book': 'media',
      'bookmark': 'media',
      'message': 'media',
      'heart': 'media',
      'star': 'media',
      'like': 'media',
      'dislike': 'media',
      'comment': 'media',
      'file-text': 'media',
      'file-pdf': 'media',
      'file-word': 'media',
      'file-excel': 'media',
      'file-ppt': 'media',
      'file-image': 'media',
      'file-zip': 'media',
      'folder': 'media',
      'folder-open': 'media',
      
      // 系统图标
      'api': 'system',
      'bug': 'system',
      'code': 'system',
      'function': 'system',
      'node-index': 'system',
      'experiment': 'system',
      'database': 'system',
      'cloud': 'system',
      'cloud-upload': 'system',
      'cloud-download': 'system',
      'cloud-server': 'system',
      'wifi': 'system',
      'lock': 'system',
      'unlock': 'system',
      'key': 'system',
      'safety': 'system',
      'security-scan': 'system',
      
      // 业务图标
      'shopping-cart': 'business',
      'dollar': 'business',
      'bank': 'business',
      'credit-card': 'business',
      'wallet': 'business',
      'shop': 'business',
      'car': 'business',
      'gift': 'business',
      'trophy': 'business',
      'crown': 'business',
      
      // 状态图标
      'check-circle': 'status',
      'close-circle': 'status',
      'exclamation-circle': 'status',
      'stop': 'status',
      'play-circle': 'status',
      'pause-circle': 'status',
      'clock': 'status',
      'calendar': 'status',
      'schedule': 'status',
      'history': 'status',
      'thunderbolt': 'status',
      'fire': 'status'
    };
    
    return categoryMap[iconName] || 'basic';
  };

  // 从统一图标库获取图标列表
  const getIconList = (category = 'all') => {
    const availableIcons = getAvailableIcons();
    
    // 图标中文标签映射
    const iconLabels = {
      'home': '首页',
      'smile': '笑脸',
      'form': '表单',
      'list': '列表',
      'table': '表格',
      'setting': '设置',
      'user': '用户',
      'team': '团队',
      'database': '数据库',
      'file-text': '文档',
      'book': '图书',
      'comment': '评论',
      'picture': '图片',
      'global': '全球',
      'tool': '工具',
      'search': '搜索',
      'plus': '添加',
      'edit': '编辑',
      'delete': '删除',
      'eye': '查看',
      'download': '下载',
      'upload': '上传',
      'reload': '刷新',
      'save': '保存',
      'close': '关闭',
      'check': '确认',
      'info': '信息',
      'warning': '警告',
      'question': '问题',
      'exclamation': '感叹',
      'heart': '心形',
      'star': '星形',
      'like': '点赞',
      'dislike': '踩',
      'thunderbolt': '闪电',
      'fire': '火焰',
      'crown': '皇冠',
      'trophy': '奖杯',
      'gift': '礼物',
      'shopping-cart': '购物车',
      'dollar': '美元',
      'bank': '银行',
      'credit-card': '信用卡',
      'wallet': '钱包',
      'shop': '商店',
      'car': '汽车',
      'appstore': '应用商店',
      'dashboard': '仪表盘',
      'desktop': '桌面',
      'api': 'API',
      'bug': '错误',
      'code': '代码',
      'function': '函数',
      'node-index': '节点索引',
      'experiment': '实验',
      'file': '文件',
      'folder': '文件夹',
      'folder-open': '打开文件夹',
      'file-pdf': 'PDF文件',
      'file-word': 'Word文件',
      'file-excel': 'Excel文件',
      'file-ppt': 'PPT文件',
      'file-image': '图片文件',
      'file-zip': '压缩文件',
      'wifi': 'WiFi',
      'cloud': '云',
      'cloud-upload': '云上传',
      'cloud-download': '云下载',
      'cloud-server': '云服务器',
      'clock': '时钟',
      'calendar': '日历',
      'schedule': '计划',
      'history': '历史',
      'safety': '安全',
      'security-scan': '安全扫描',
      'lock': '锁定',
      'unlock': '解锁',
      'key': '钥匙',
      'check-circle': '确认圆圈',
      'close-circle': '关闭圆圈',
      'exclamation-circle': '感叹圆圈',
      'stop': '停止',
      'play-circle': '播放圆圈',
      'pause-circle': '暂停圆圈',
      'menu': '菜单',
      'bookmark': '书签',
      'message': '消息'
    };

    let iconsToProcess = availableIcons;
    
    // 如果指定了分类，只处理该分类的图标
    if (category !== 'all') {
      iconsToProcess = availableIcons.filter(iconName => 
        getIconCategory(iconName) === category
      );
    }

    return iconsToProcess.map(iconName => ({
      name: iconName,
      icon: iconMap[iconName],
      label: iconLabels[iconName] || iconName,
      category: getIconCategory(iconName)
    }));
  };

  const allIcons = getIconList(activeTab);
  const [keyword, setKeyword] = useState('');
  
  const filtered = useMemo(() => {
    if (!keyword) return allIcons;
    const lower = keyword.toLowerCase();
    return allIcons.filter(i => 
      i.name.toLowerCase().includes(lower) || 
      (i.label || '').toLowerCase().includes(lower)
    );
  }, [allIcons, keyword]);

  const handleIconSelect = (iconName, iconType) => {
    const iconData = {
      type: iconType,
      name: iconName,
      value: iconName
    };
    onChange?.(iconData);
  };

  const handleCustomIconChange = (e) => {
    const customValue = e.target.value;
    setCustomIcon(customValue);
    if (customValue) {
      const iconData = {
        type: 'custom',
        name: customValue,
        value: customValue
      };
      onChange?.(iconData);
    }
  };

  const handleCustomUrlChange = (e) => {
    const urlValue = e.target.value;
    setCustomUrl(urlValue);
    if (urlValue) {
      const iconData = {
        type: 'url',
        name: urlValue,
        value: urlValue
      };
      onChange?.(iconData);
    }
  };

  // 渲染图标网格
  const renderIconGrid = (icons) => (
    <Row gutter={[8, 8]} style={{ marginTop: 16 }}>
      {icons.map((item) => (
        <Col span={3} key={item.name}>
          <Tooltip title={`${item.label} (${item.name})`}>
            <div
              style={{
                width: '40px',
                height: '40px',
                border: initialValue?.name === item.name ? '2px solid #1890ff' : '1px solid #d9d9d9',
                borderRadius: '6px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                cursor: 'pointer',
                backgroundColor: initialValue?.name === item.name ? '#e6f7ff' : '#fff',
                transition: 'all 0.2s',
                margin: '0 auto',
                position: 'relative'
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.backgroundColor = initialValue?.name === item.name ? '#e6f7ff' : '#f5f5f5';
                e.currentTarget.style.borderColor = '#1890ff';
                e.currentTarget.style.transform = 'scale(1.05)';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.backgroundColor = initialValue?.name === item.name ? '#e6f7ff' : '#fff';
                e.currentTarget.style.borderColor = initialValue?.name === item.name ? '#1890ff' : '#d9d9d9';
                e.currentTarget.style.transform = 'scale(1)';
              }}
              onClick={() => handleIconSelect(item.name, 'antd')}
            >
              <div style={{ 
                fontSize: '18px', 
                color: initialValue?.name === item.name ? '#1890ff' : '#666' 
              }}>
                {item.icon}
              </div>
              {/* 分类标识 */}
              <div style={{
                position: 'absolute',
                top: '-2px',
                right: '-2px',
                width: '8px',
                height: '8px',
                borderRadius: '50%',
                backgroundColor: iconCategories[item.category]?.color || '#1890ff',
                border: '1px solid #fff'
              }} />
            </div>
          </Tooltip>
        </Col>
      ))}
    </Row>
  );

  // 渲染分类标签页
  const renderCategoryTabs = () => {
    const tabItems = Object.keys(iconCategories).map(categoryKey => {
      const category = iconCategories[categoryKey];
      const categoryIcons = getIconList(categoryKey);
      const filteredCategoryIcons = keyword ? 
        categoryIcons.filter(i => 
          i.name.toLowerCase().includes(keyword.toLowerCase()) || 
          (i.label || '').toLowerCase().includes(keyword.toLowerCase())
        ) : categoryIcons;

      return {
        key: categoryKey,
        label: (
          <Badge 
            count={filteredCategoryIcons.length} 
            size="small" 
            style={{ backgroundColor: category.color }}
          >
            <span style={{ color: category.color, fontWeight: 500 }}>
              {category.name}
            </span>
          </Badge>
        ),
        children: renderIconGrid(filteredCategoryIcons)
      };
    });

    return (
      <Tabs
        activeKey={activeTab}
        onChange={setActiveTab}
        items={tabItems}
        size="small"
        style={{ marginTop: 16 }}
      />
    );
  };

  return (
    <div style={{ width: '100%' }}>
      <div style={{ marginBottom: 16, color: '#666', fontSize: '14px' }}>
        选择图标
      </div>

      <Input.Search
        placeholder="搜索图标名称"
        allowClear
        value={keyword}
        onChange={(e) => setKeyword(e.target.value)}
        style={{ marginBottom: 12 }}
      />

      {keyword ? (
        <div>
          <h4 style={{ margin: '0 0 12px 0', fontSize: '14px', color: '#666' }}>
            搜索结果 ({filtered.length} 个图标)
          </h4>
          {renderIconGrid(filtered)}
        </div>
      ) : (
        renderCategoryTabs()
      )}

      {/* 自定义图标输入 */}
      <div style={{ marginTop: 24, paddingTop: 16, borderTop: '1px solid #f0f0f0' }}>
        <h4 style={{ margin: '0 0 12px 0', fontSize: '14px', color: '#666' }}>
          自定义图标
        </h4>
        <Input
          placeholder="输入自定义图标名称"
          value={customIcon}
          onChange={handleCustomIconChange}
          style={{ marginBottom: 8 }}
        />
        <Input
          placeholder="输入图标URL"
          value={customUrl}
          onChange={handleCustomUrlChange}
        />
      </div>
    </div>
  );
};

export default IconSelector;