import {Avatar, Card, Col, List, Row, Typography, Affix, Space, Button} from 'antd';
import React, { useState, useEffect, useCallback, useMemo } from 'react';
import {PageContainer} from '@ant-design/pro-layout';
import {Link, connect} from 'umi';
import styles from './style.less';
import moment from "moment";
import Title from "antd/lib/typography/Title";
import Comments from "@/pages/book/detail/components/Comments";
import Share from "@/components/share";
import remarkGfm from "remark-gfm";
import rehypeRaw from "rehype-raw";
import ReactMarkdown from "react-markdown";
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { vscDarkPlus, vs } from 'react-syntax-highlighter/dist/esm/styles/prism';
import '@/styles/markdown.css';
import {genTdkCrumbs, genTdkCrumbsFor404} from "@/utils/utils";
import {MenuOutlined} from '@ant-design/icons';
import MarkdownImage from '@/components/MarkdownImage';

const Document = ({currentChapter = {id: '', parentId: '', pubTitle: '', pubContent: ''}, dispatch }) => {
  const [article, setArticle] = useState(currentChapter?? {});
  const [toc, setToc] = useState([]);
  const [activeId, setActiveId] = useState('');
  const [tocCollapsed, setTocCollapsed] = useState(false);
  const [showTocPreview, setShowTocPreview] = useState(false);
  const [mobileTocOpen, setMobileTocOpen] = useState(false);
  const [tocHovered, setTocHovered] = useState(false);
  const [tocPinned, setTocPinned] = useState(false);
  const [hoverTimeout, setHoverTimeout] = useState(null);
  
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

  // 内容类型检测函数
  const detectContentType = useCallback((content) => {
    if (!content) return 'text/html';

    // 检测markdown特征
    const markdownPatterns = [
      /^#{1,6}\s+.+$/m,  // 标题
      /^\*\*.*\*\*$/m,    // 粗体
      /^\*.*\*$/m,        // 斜体
      /^-\s+.+$/m,        // 列表
      /^\d+\.\s+.+$/m,    // 有序列表
      /```[\s\S]*```/m,   // 代码块
      /`.*`/m,            // 行内代码
    ];

    const markdownScore = markdownPatterns.reduce((score, pattern) => {
      return score + (pattern.test(content) ? 1 : 0);
    }, 0);

    // 如果找到3个或以上markdown特征，认为是markdown
    if (markdownScore >= 3) {
      return 'text/markdown';
    }

    // 检测HTML特征
    const htmlPattern = /<[^>]+>/;
    if (htmlPattern.test(content)) {
      return 'text/html';
    }

    // 默认返回markdown（因为内容看起来像markdown）
    return 'text/markdown';
  }, []);

  // 调试信息
  useEffect(() => {
  }, [currentChapter, article, detectContentType]);

  useEffect(async () => {
    if (article?.id) {
      await genTdkCrumbs(currentChapter, dispatch, '正文', '');
    }
  }, [article?.id, currentChapter]);

  
  // 从文章数据中获取水印配置
  useEffect(() => {
    if (article && (article.watermarkText || article.watermarkEnabled !== undefined)) {
      setWatermarkText(article.watermarkText || 'WLDOS');
      setWatermarkEnabled(article.watermarkEnabled === 'true' || article.watermarkEnabled === true);
    }
  }, [article]);

  // 监听暗黑模式变化，重新生成水印
  useEffect(() => {
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    const handleChange = () => {
      // 强制重新渲染以更新水印颜色
      setWatermarkText(prev => prev + ' '); // 触发重新生成
      setTimeout(() => setWatermarkText(prev => prev.trim()), 0);
    };
    
    mediaQuery.addEventListener('change', handleChange);
    return () => mediaQuery.removeEventListener('change', handleChange);
  }, []);

  // 为markdown内容生成目录
  useEffect(() => {
    if (article.pubContent) {
      const detectedMimeType = detectContentType(article.pubContent);
      const mimeType = article.pubMimeType || detectedMimeType;
      generateToc(article.pubContent, mimeType);
    }
  }, [article.pubContent, article.pubMimeType, generateToc, detectContentType]);

  // 生成目录
  const generateToc = useCallback((content, mimeType) => {
    if (!content) return;

    if (mimeType === 'text/markdown') {
      // 对于markdown内容，从markdown文本中提取标题
      // 先移除代码块（三个反引号包裹的内容）和行内代码（单个反引号包裹的内容），避免将代码中的注释识别为标题
      let cleanedContent = content;
      
      // 移除代码块（```...```格式）
      cleanedContent = cleanedContent.replace(/```[\s\S]*?```/g, '');
      
      // 移除行内代码（`...`格式，但要避免误删真正的标题）
      // 只移除那些不在行首的单个反引号包裹的内容
      cleanedContent = cleanedContent.replace(/`[^`\n]+`/g, '');
      
      // 提取标题（只匹配不在代码块中的标题）
      const lines = cleanedContent.split('\n');
      const tocItems = [];
      
      lines.forEach((line, index) => {
        // 匹配行首的标题（# 开头，后面跟空格，然后是标题文本）
        const headingMatch = line.match(/^(#{1,6})\s+(.+)$/);
        if (headingMatch) {
          const level = headingMatch[1].length;
          const text = headingMatch[2].trim();
          const id = `heading-${tocItems.length}`;
          tocItems.push({
            id,
            level,
            text,
            element: null
          });
        }
      });
      
      if (tocItems.length > 0) {
        setToc(tocItems);
        return;
      }
    }

    // 对于HTML内容，使用原有逻辑
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = content;
    const headings = tempDiv.querySelectorAll('h1, h2, h3, h4, h5, h6');

    const tocItems = Array.from(headings).map((heading, index) => {
      // 优先使用已有的ID，如果没有则生成新的ID
      const id = heading.id || `heading-${index}`;
      if (!heading.id) {
        heading.id = id;
      }
      return {
        id,
        level: parseInt(heading.tagName.charAt(1)),
        text: heading.textContent,
        element: heading
      };
    });

    setToc(tocItems);
  }, []);

  // 监听滚动，更新活跃目录项
  useEffect(() => {
    const handleScroll = () => {
      const scrollTop = window.pageYOffset;
      const windowHeight = window.innerHeight;

      for (let i = toc.length - 1; i >= 0; i--) {
        const element = document.getElementById(toc[i].id);
        if (element && element.offsetTop <= scrollTop + 100) {
          setActiveId(toc[i].id);
          break;
        }
      }
    };

    if (toc.length > 0) {
      window.addEventListener('scroll', handleScroll);
      return () => window.removeEventListener('scroll', handleScroll);
    }
  }, [toc]);

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
    }, 150); // 150ms延迟，避免快速移动时的闪烁
    setHoverTimeout(timeout);
  };

  // 处理富文本内容的标题ID
  useEffect(() => {
    const detectedMimeType = detectContentType(article.pubContent);
    const mimeType = article.pubMimeType || detectedMimeType;
    
    if (mimeType !== 'text/markdown' && article.pubContent) {
      // 等待DOM渲染完成后，确保富文本内容的标题ID与目录一致
      const timer = setTimeout(() => {
        const headings = document.querySelectorAll('.pub-content h1, .pub-content h2, .pub-content h3, .pub-content h4, .pub-content h5, .pub-content h6');
        headings.forEach((heading, index) => {
          // 如果标题没有ID，使用目录中对应的ID
          if (!heading.id && toc[index]) {
            heading.id = toc[index].id;
          }
        });
      }, 100);
      
      return () => clearTimeout(timer);
    }
  }, [article.pubContent, article.pubMimeType, toc]);

  // 清理定时器
  useEffect(() => {
    return () => {
      if (hoverTimeout) {
        clearTimeout(hoverTimeout);
      }
    };
  }, [hoverTimeout]);

  // 处理内容，添加标题ID
  const processedContent = useMemo(() => {
    if (!article.pubContent) return '';

    const detectedMimeType = detectContentType(article.pubContent);
    const mimeType = article.pubMimeType || detectedMimeType;

    // 如果是markdown格式，直接返回原始内容，不处理HTML
    if (mimeType === 'text/markdown') {
      return article.pubContent;
    }

    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = article.pubContent;
    const headings = tempDiv.querySelectorAll('h1, h2, h3, h4, h5, h6');

    headings.forEach((heading, index) => {
      if (!heading.id) {
        heading.id = `heading-${index}`;
      }
    });

    // 生成目录
    generateToc(tempDiv.innerHTML, mimeType);

    return tempDiv.innerHTML;
  }, [article.pubContent, article.pubMimeType, generateToc, detectContentType]);


  const enumTags = (tags = [], ) => {
    const temp = [];
    for (let i = 0, len = tags.length; i < len; i += 1) {
      const tag = tags[i];
      if (tag)
        temp.push((<Link key={`${tag.id}-art-tag-${i}`} to={`/archives/tag/${tag.slug}`} target="_blank" rel="noopener">{i<=len-2 ? `${tag.name}, ` : `${tag.name}`}</Link>));
    }
    return temp;
  };

  // 目录组件
  const tocComponent = toc.length > 0 && (
    <>
      {/* 正常目录 */}
      {!tocCollapsed && (
        <Affix offsetTop={80}>
          <Card
            title={
              <Space>
                <MenuOutlined />
                <span>目录</span>
                <span
                  className={styles.tocToggle}
                  onClick={(e) => {
                    e.stopPropagation();
                    setTocCollapsed(true);
                  }}
                >
                  收起
                </span>
              </Space>
            }
            size="small"
            className={styles.tocCard}
          >
            <div className={styles.tocList}>
              {toc.map((item) => (
                <div
                  key={item.id}
                  className={`${styles.tocItem} ${activeId === item.id ? styles.active : ''}`}
                  style={{ paddingLeft: `${(item.level - 1) * 16}px` }}
                  onClick={() => {
                    const element = document.getElementById(item.id);
                    if (element) {
                      // 考虑固定头部高度
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
                      fontSize: item.level === 1 ? '14px' : '13px',
                      fontWeight: item.level <= 2 ? '500' : '400'
                    }}
                  >
                    {item.text}
                  </span>
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
          
          {/* 悬停预览 */}
          {showTocPreview && (
            <div className={styles.tocPreview}>
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
                        // 考虑固定头部高度
                        const headerHeight = window.innerWidth <= 1024 ? 60 : 80;
                        const elementPosition = element.offsetTop - headerHeight;
                        window.scrollTo({
                          top: elementPosition,
                          behavior: 'smooth'
                        });
                      }
                      // 点击预览项后保持预览状态，不隐藏
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

  const articleBody = article.id && (
    <div className={styles.articleContainer}>
      <div className={styles.articleContent}>
        <Typography>
          <Title level={1} className={styles.documentTitle}>{article.pubTitle}</Title>
          {/* 移除原来的作者信息，将在底部显示 */}
          <div 
            className={styles.entry}
            style={{
              '--watermark-bg': watermarkEnabled ? generateWatermarkSVG(watermarkText) : 'none'
            }}
          >
            {(() => {
              const detectedMimeType = detectContentType(article.pubContent);
              const mimeType = article.pubMimeType || detectedMimeType;

              return mimeType === 'text/markdown' ? (
                <ReactMarkdown
                  remarkPlugins={[remarkGfm]}
                  rehypePlugins={[rehypeRaw]}
                  className={"markdown-body"}
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
                <div dangerouslySetInnerHTML={{ __html: processedContent }} />
              );
            })()}
          </div>
          {/* 底部信息布局 - 语雀风格 */}
          <div className={styles.articleFooter}>
            <div className={styles.articleMeta}>
              <Avatar
                key={1}
                src={article.member.avatar}
                tips={article.member.nickname}
                size="small"
              />
              <span className={styles.authorName}>
                <Link to={`/archives-author/${article.member.id}.html`} target="_blank" rel="noopener">{article.member.nickname}</Link>
              </span>
              <span className={styles.publishTime}>{moment(article.createTime).format('YYYY-MM-DD HH:mm')}</span>
              <span className={styles.readCount}>阅读 {article.views}</span>
            </div>
            <div className={styles.articleActions}>
              <span className={styles.shareLabel}>分享到：</span>
              <Share style={styles.share} title={article.pubTitle || '无标题'} src={'@/assets/log.svg'}/>
            </div>
          </div>

          <div className={styles.pubMetaBottom}>
            <div className={styles.tags}>
            <span>
              tags: {article.tags && enumTags(article.tags)}
            </span>
            </div>
          </div>
          <div className={styles.pubPrevNext}>
            {article.prev && (<><strong>上一篇</strong>：<Link to={`/doc/book/${article.parentId}/chapter/${article.prev.id}.html`} rel="prev">{article.prev.pubTitle}</Link><br/></>)}
            {article.next && (<><strong>下一篇</strong>：<Link to={`/doc/book/${article.parentId}/chapter/${article.next.id}.html`} rel="next">{article.next.pubTitle}</Link></>)}
          </div>
          <div className={styles.relatedPost}>
            <h2>相关内容</h2>
            {
              article.relPubs?.length > 0 && (
                <List
                  grid={{
                    gutter: 24, xs: 1, sm: 1, md: 2, lg: 2, xl: 2, xxl: 2,
                  }}
                  dataSource={article.relPubs}
                  renderItem={(item, index) => (
                    <List.Item key={`${item.id}-rel-${index}`}>
                      <Link to={`/doc/book/${article.parentId}/chapter/${item.id}.html`} rel="noopener">
                        {item.pubTitle}
                      </Link>
                    </List.Item>
                  )}
                />
              )
            }
          </div>
          <div className={styles.clearfix}/>
          <div className={styles.comment}>
            <div className={styles.clearfix}/>
            <h3>在线评论</h3>
            <Comments id={article.id}/>
          </div>
        </Typography>
      </div>
      {tocComponent}

      {/* 移动端目录按钮 */}
      <div className={styles.mobileToolbar}>
        <div
          className={styles.mobileToolbarButton}
          onClick={() => setMobileTocOpen(!mobileTocOpen)}
        >
          <MenuOutlined />
        </div>
      </div>

      {/* 移动端目录遮罩 */}
      {mobileTocOpen && (
        <div
          className={styles.mobileTocOverlay}
          onClick={() => setMobileTocOpen(false)}
        >
          <div
            className={styles.mobileTocContent}
            onClick={(e) => e.stopPropagation()}
          >
            <div className={styles.mobileTocHeader}>
              <span>目录</span>
              <span
                className={styles.mobileTocClose}
                onClick={() => setMobileTocOpen(false)}
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
                    document.getElementById(item.id)?.scrollIntoView({ behavior: 'smooth' });
                    setMobileTocOpen(false);
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
    <PageContainer
      title={false}
      breadcrumb={false}
      style={{
        background: '#fff',
        minHeight: 'calc(100vh - 48px)', // 减去header高度
        position: 'relative',
      }}
    >
       <Card
         className={styles.tabsCard}
         bordered={false}
         style={{
           position: 'relative',
           background: '#fff',
         }}
       >
         <div style={{ position: 'relative', zIndex: 2 }}>
           {article === "" ? <p>请求的内容不存在了……</p> : articleBody}
         </div>
      </Card>
    </PageContainer>
  );
};

export default connect(({ user }) => ({
  tdk: user.tdk
}))(Document);