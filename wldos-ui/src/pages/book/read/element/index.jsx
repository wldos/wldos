import {Typography, Button, BackTop, Affix, Space, Card} from 'antd';
import React, {useEffect, useState, useCallback, useMemo} from 'react';
import {GridContent, PageContainer} from '@ant-design/pro-layout';
import {Link, connect} from 'umi';
import {MenuOutlined} from '@ant-design/icons';
import styles from './style.less';
import {genTdkCrumbs} from "@/utils/utils";
import Title from "antd/lib/typography/Title";
import {previewElement, queryElement} from "@/pages/book/read/element/service";
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import rehypeRaw from 'rehype-raw';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { vscDarkPlus, vs } from 'react-syntax-highlighter/dist/esm/styles/prism';
import '@/styles/markdown.css';
import MarkdownImage from '@/components/MarkdownImage';

const {Text} = Typography;

const Element = (props) => {
  const {dispatch, match, } = props;
  const [article, setArticle] = useState(undefined);
  const [toc, setToc] = useState([]);
  const [activeId, setActiveId] = useState('');
  const [tocCollapsed, setTocCollapsed] = useState(false);
  const [showTocPreview, setShowTocPreview] = useState(false);
  const [mobileTocOpen, setMobileTocOpen] = useState(false);
  const [tocHovered, setTocHovered] = useState(false);
  const [tocPinned, setTocPinned] = useState(false);
  const [hoverTimeout, setHoverTimeout] = useState(null);
  
  // 移动端目录状态
  const [mobileTocVisible, setMobileTocVisible] = useState(false);
  
  // 水印相关状态
  const [watermarkText, setWatermarkText] = useState('WLDOS');
  const [watermarkEnabled, setWatermarkEnabled] = useState(true);
  
  // 暗黑模式状态
  const [isDarkMode, setIsDarkMode] = useState(false);
  
  // 监听暗黑模式变化
  useEffect(() => {
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    const handleChange = () => setIsDarkMode(mediaQuery.matches);
    handleChange(); // 设置初始主题
    mediaQuery.addEventListener('change', handleChange);
    return () => mediaQuery.removeEventListener('change', handleChange);
  }, []);

  // 生成水印SVG URL
  const generateWatermarkSVG = useCallback((text) => {
    try {
      // 验证输入文本
      const safeText = (text || 'WLDOS').replace(/[<>]/g, ''); // 防止XSS
      
      // 检测是否为暗黑模式
      const isDarkMode = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
      
      // 根据模式选择颜色
      const color = isDarkMode ? '#2a2a2a' : '#d0d0d0'; // 暗黑模式用极淡的深色，亮色模式用浅色
      
      const svgContent = `<svg xmlns='http://www.w3.org/2000/svg' width='200' height='200' viewBox='0 0 200 200'><text x='50%' y='50%' font-family='Arial, sans-serif' font-size='16' fill='${color}' text-anchor='middle' dy='.3em' transform='rotate(-45 100 100)'>${safeText}</text></svg>`;
      return `url("data:image/svg+xml,${encodeURIComponent(svgContent)}")`;
    } catch (error) {
      console.warn('生成水印SVG失败:', error);
      // 返回默认水印
      return `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='200' height='200' viewBox='0 0 200 200'%3E%3Ctext x='50%25' y='50%25' font-family='Arial, sans-serif' font-size='16' fill='%23d0d0d0' text-anchor='middle' dy='.3em' transform='rotate(-45 100 100)'%3EWLDOS%3C/text%3E%3C/svg%3E")`;
    }
  }, []);

  // 生成目录
  const generateToc = useCallback((content) => {
    if (!content) return [];
    
    // 先移除代码块（三个反引号包裹的内容）和行内代码，避免将代码中的注释识别为标题
    let cleanedContent = content;
    
    // 移除代码块（```...```格式）
    cleanedContent = cleanedContent.replace(/```[\s\S]*?```/g, '');
    
    // 移除行内代码（`...`格式）
    cleanedContent = cleanedContent.replace(/`[^`\n]+`/g, '');
    
    const lines = cleanedContent.split('\n');
    const toc = [];
    
    lines.forEach((line, index) => {
      // 匹配行首的标题（# 开头，后面跟空格，然后是标题文本）
      const match = line.match(/^(#{1,6})\s+(.+)$/);
      if (match) {
        const level = match[1].length;
        const text = match[2].trim();
        const id = `heading-${toc.length}`;
        toc.push({ level, text, id, line: index });
      }
    });
    
    return toc;
  }, []);

  // 从HTML内容生成目录
  const generateTocFromHtml = useCallback((htmlContent) => {
    if (!htmlContent) return [];
    
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = htmlContent;
    
    const headings = tempDiv.querySelectorAll('h1, h2, h3, h4, h5, h6');
    const toc = [];
    
    headings.forEach((heading, index) => {
      const level = parseInt(heading.tagName.charAt(1));
      const text = heading.textContent.trim();
      const id = `heading-${index}`;
      toc.push({ level, text, id });
    });
    
    return toc;
  }, []);

  // 处理滚动事件
  const handleScroll = useCallback(() => {
    if (!toc.length) return;
    
    const headerHeight = window.innerWidth <= 1024 ? 60 : 80;
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
    
    let activeId = '';
    for (let i = toc.length - 1; i >= 0; i--) {
      const element = document.getElementById(toc[i].id);
      if (element) {
        const elementTop = element.offsetTop - headerHeight;
        if (scrollTop >= elementTop) {
          activeId = toc[i].id;
          break;
        }
      }
    }
    
    setActiveId(activeId);
  }, [toc]);

  useEffect(async () => {
    const {params} = match;
    const res = !params.preview ? await queryElement({id: params.id}) : await previewElement({id: params.id, preview: params.preview});

    if (res?.data) {
      await genTdkCrumbs(res.data, dispatch, '正文', '');
      setArticle(res.data);
      
      // 设置水印
      if (res.data.watermarkText || res.data.watermarkEnabled !== undefined) {
        setWatermarkText(res.data.watermarkText || 'WLDOS');
        setWatermarkEnabled(res.data.watermarkEnabled === 'true' || res.data.watermarkEnabled === true);
      }
      
      // 生成目录
      if (res.data.pubMimeType === 'text/markdown') {
        const generatedToc = generateToc(res.data.pubContent);
        setToc(generatedToc);
      } else {
        // 富文本内容也生成目录
        const generatedToc = generateTocFromHtml(res.data.pubContent);
        setToc(generatedToc);
      }
    }
  }, [match.url, generateToc, generateTocFromHtml]);

  // 监听滚动事件
  useEffect(() => {
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, [handleScroll]);


  // 图片懒加载处理
  const handleImageLoad = useCallback((e) => {
    const img = e.target;
    img.style.opacity = '1';
  }, []);

  const handleImageError = useCallback((e) => {
    const img = e.target;
    const svgPlaceholder = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0nNDAwJyBoZWlnaHQ9JzMwMCcgeG1sbnM9J2h0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnJz48cmVjdCB3aWR0aD0nMTAwJScgaGVpZ2h0PScxMDAlJyBmaWxsPScjZjVmNWY1Jy8+PHRleHQgeD0nNTAlIHk9JzUwJScgZm9udC1zaXplPScxNScgZmlsbD0nIzk5OTk5OScgdGV4dC1hbmNob3I9J21pZGRsZSc+5Y+v5Y+w5a2X5om+5a+55aSx5Y+35L+hPC90ZXh0Pjwvc3ZnPg==';
    img.src = svgPlaceholder;
  }, []);

  // 处理内容，添加图片懒加载
  const processedContent = useMemo(() => {
    if (!article?.pubContent) return '';
    
    // 如果是markdown格式，直接返回原始内容
    if (article.pubMimeType === 'text/markdown') {
      return article.pubContent;
    }
    
    // 对于富文本内容，为标题添加ID
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = article.pubContent;
    
    // 为标题添加ID
    const headings = tempDiv.querySelectorAll('h1, h2, h3, h4, h5, h6');
    headings.forEach((heading, index) => {
      heading.id = `heading-${index}`;
    });
    
    return tempDiv.innerHTML;
  }, [article?.pubContent, article?.pubMimeType]);

  const titleStyle = {
    marginBottom: '8px',
    paddingBottom: '8px',
    lineHeight: '40px',
    fontSize: '28px',
    fontWeight: '500',
    borderBottom: '2px solid #f4f4f4'
  };


  // 智能鼠标事件处理
  const handleMouseEnter = () => {
    if (hoverTimeout) {
      clearTimeout(hoverTimeout);
      setHoverTimeout(null);
    }
    if (!showTocPreview) {
      setShowTocPreview(true);
    }
  };

  const handleMouseLeave = () => {
    const timeout = setTimeout(() => {
      setShowTocPreview(false);
    }, 150);
    setHoverTimeout(timeout);
  };

  // 键盘快捷键支持
  useEffect(() => {
    const handleKeyPress = (e) => {
      // 按 T 键切换目录显示/隐藏
      if (e.key.toLowerCase() === 't' && !e.ctrlKey && !e.altKey && !e.metaKey) {
        // 检查是否在输入框中
        const activeElement = document.activeElement;
        const isInputFocused = activeElement && (
          activeElement.tagName === 'INPUT' ||
          activeElement.tagName === 'TEXTAREA' ||
          activeElement.contentEditable === 'true'
        );
        
        if (!isInputFocused) {
          e.preventDefault();
          setTocCollapsed(!tocCollapsed);
        }
      }
    };

    document.addEventListener('keydown', handleKeyPress);
    return () => {
      document.removeEventListener('keydown', handleKeyPress);
    };
  }, [tocCollapsed]);

  // 清理定时器
  useEffect(() => {
    return () => {
      if (hoverTimeout) {
        clearTimeout(hoverTimeout);
      }
    };
  }, [hoverTimeout]);

  // 目录组件
  const tocComponent = toc.length > 0 && (
    <>
      {/* 正常目录 */}
      {!tocCollapsed && (
        <Affix offsetTop={80}>
          <Card 
            title="目录" 
            size="small" 
            className={styles.tocCard}
            extra={
              <Button 
                type="text" 
                size="small" 
                icon={<MenuOutlined />}
                onClick={() => setTocCollapsed(true)}
              />
            }
          >
            <div className={styles.tocList}>
              {toc.map((item, index) => (
                <div
                  key={index}
                  className={`${styles.tocItem} ${activeId === item.id ? styles.active : ''}`}
                  style={{ paddingLeft: `${(item.level - 1) * 16}px` }}
                  onClick={() => {
                    const element = document.getElementById(item.id);
                    if (element) {
                      const headerHeight = window.innerWidth <= 1024 ? 60 : 80;
                      window.scrollTo({
                        top: element.offsetTop - headerHeight,
                        behavior: 'smooth'
                      });
                    }
                  }}
                >
                  {item.text}
                </div>
              ))}
            </div>
          </Card>
        </Affix>
      )}
      
      {/* 浮动目录按钮 */}
      {tocCollapsed && (
        <div 
          className={styles.floatingTocButton}
          onMouseEnter={handleMouseEnter}
          onMouseLeave={handleMouseLeave}
        >
          <Button
            type="primary"
            shape="circle"
            icon={<MenuOutlined />}
            size="large"
            onClick={() => setTocCollapsed(false)}
            className={styles.floatingTocBtn}
            title="展开目录 (快捷键: T)"
          />
          
          {/* 桌面端悬停预览 */}
          {showTocPreview && (
            <div 
              className={styles.tocPreview}
              onMouseEnter={handleMouseEnter}
              onMouseLeave={handleMouseLeave}
            >
              <div className={styles.tocPreviewHeader}>
                <span>目录预览</span>
                <span className={styles.tocPreviewHint}>滚动查看全部</span>
              </div>
              <div className={styles.tocPreviewList}>
                {toc.map((item) => (
                  <div
                    key={item.id}
                    className={`${styles.tocPreviewItem} ${activeId === item.id ? styles.active : ''}`}
                    style={{ paddingLeft: `${(item.level - 1) * 12}px` }}
                    onClick={() => {
                      const element = document.getElementById(item.id);
                      if (element) {
                        const headerHeight = window.innerWidth <= 1024 ? 60 : 80;
                        const elementPosition = element.offsetTop - headerHeight;
                        window.scrollTo({
                          top: elementPosition,
                          behavior: 'smooth'
                        });
                      }
                    }}
                  >
                    <span
                      style={{
                        fontSize: item.level === 1 ? '13px' : '12px',
                        fontWeight: item.level <= 2 ? '500' : '400'
                      }}
                    >
                      {item.text}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </>
  );

  const articleBody = article && (
    <div className={styles.articleContainer}>
      <div className={styles.articleContent}>
        <Typography>
          <Title level={1} style={titleStyle}>{article.pubTitle}</Title>
          <div 
            className={styles.entry}
            style={{
              '--watermark-bg': watermarkEnabled ? generateWatermarkSVG(watermarkText) : 'none'
            }}
          >
            {article.pubMimeType === 'text/markdown' ? (
              <ReactMarkdown 
                remarkPlugins={[remarkGfm]} 
                rehypePlugins={[rehypeRaw]} 
                className="markdown-body"
                components={{
                  h1: ({ children, ...props }) => {
                    const text = children.toString();
                    const id = `heading-${toc.findIndex(item => item.text === text)}`;
                    return <h1 id={id} {...props}>{children}</h1>;
                  },
                  h2: ({ children, ...props }) => {
                    const text = children.toString();
                    const id = `heading-${toc.findIndex(item => item.text === text)}`;
                    return <h2 id={id} {...props}>{children}</h2>;
                  },
                  h3: ({ children, ...props }) => {
                    const text = children.toString();
                    const id = `heading-${toc.findIndex(item => item.text === text)}`;
                    return <h3 id={id} {...props}>{children}</h3>;
                  },
                  h4: ({ children, ...props }) => {
                    const text = children.toString();
                    const id = `heading-${toc.findIndex(item => item.text === text)}`;
                    return <h4 id={id} {...props}>{children}</h4>;
                  },
                  h5: ({ children, ...props }) => {
                    const text = children.toString();
                    const id = `heading-${toc.findIndex(item => item.text === text)}`;
                    return <h5 id={id} {...props}>{children}</h5>;
                  },
                  h6: ({ children, ...props }) => {
                    const text = children.toString();
                    const id = `heading-${toc.findIndex(item => item.text === text)}`;
                    return <h6 id={id} {...props}>{children}</h6>;
                  },
                  img({src, alt, title, ...props}) {
                    return (
                      <MarkdownImage 
                        src={src} 
                        alt={alt} 
                        title={title} 
                        content={article.pubContent}
                        {...props}
                      />
                    );
                  },
                  code({node, inline, className, children, ...props}) {
                    const match = /language-(\w+)/.exec(className || '');
                    
                    return !inline && match ? (
                      <div style={{ 
                        backgroundColor: isDarkMode ? '#0d1117' : '#f6f8fa',
                        borderRadius: '6px',
                        padding: '16px',
                        margin: '16px 0',
                        overflow: 'auto'
                      }}>
                        <SyntaxHighlighter
                          style={isDarkMode ? vscDarkPlus : vs}
                          language={match[1]}
                          PreTag="div"
                          showLineNumbers={false}
                          wrapLines={true}
                          wrapLongLines={true}
                          customStyle={{
                            background: 'transparent',
                            margin: 0,
                            padding: 0,
                            border: 'none',
                            borderRadius: 0
                          }}
                          {...props}
                        >
                          {String(children).replace(/\n$/, '')}
                        </SyntaxHighlighter>
                      </div>
                    ) : (
                      <code className={className} {...props}>
                        {children}
                      </code>
                    );
                  }
                }}
              >
                {article.pubContent}
              </ReactMarkdown>
            ) : (
              <div 
                className="rich-text-content"
                dangerouslySetInnerHTML={{ __html: processedContent }} 
              />
            )}
          </div>
          <div className={styles.pubPrevNext}>
            {article.prev && (<><strong>上一篇</strong>：<Link to={`/element-${article.prev.id}.html`} rel="prev">{article.prev.pubTitle}</Link><br/></>)}
            {article.next && (<><strong>下一篇</strong>：<Link to={`/element-${article.next.id}.html`} rel="next">{article.next.pubTitle}</Link></>)}
          </div>
          <div className={styles.clearfix}/>
        </Typography>
      </div>
      {tocComponent}

      {/* 移动端目录按钮 */}
      <div className={styles.mobileToolbar}>
        <div
          className={styles.mobileToolbarButton}
          onClick={() => setMobileTocVisible(!mobileTocVisible)}
        >
          <MenuOutlined />
        </div>
      </div>

      {/* 移动端目录遮罩 */}
      {mobileTocVisible && (
        <div
          className={styles.mobileTocOverlay}
          onClick={() => setMobileTocVisible(false)}
        >
          <div
            className={styles.mobileTocContent}
            onClick={(e) => e.stopPropagation()}
          >
            <div className={styles.mobileTocHeader}>
              <span>目录</span>
              <span
                className={styles.mobileTocClose}
                onClick={() => setMobileTocVisible(false)}
              >
                ×
              </span>
            </div>
            <div className={styles.mobileTocList}>
              {toc.map((item) => (
                <div
                  key={item.id}
                  className={`${styles.mobileTocItem} ${activeId === item.id ? styles.active : ''}`}
                  style={{ paddingLeft: `${(item.level - 1) * 16}px` }}
                  onClick={() => {
                    const element = document.getElementById(item.id);
                    if (element) {
                      const headerHeight = window.innerWidth <= 1024 ? 60 : 80;
                      const elementPosition = element.offsetTop - headerHeight;
                      window.scrollTo({
                        top: elementPosition,
                        behavior: 'smooth'
                      });
                      setMobileTocVisible(false);
                    }
                  }}
                >
                  <span
                    style={{
                      fontSize: item.level === 1 ? '14px' : '13px',
                      fontWeight: item.level <= 2 ? '500' : '400'
                    }}
                  >
                    {item.text}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );

return (
  <PageContainer title={false} breadcrumb={false}>
    <GridContent>
      {article === "" ? <p>请求的内容不存在了……</p> : articleBody}
    </GridContent>
  </PageContainer>
);
};

export default connect(({ user } ) => (
  {
    tdk: user.tdk,
  }
))(Element);