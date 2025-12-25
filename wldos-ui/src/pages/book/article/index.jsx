import {Avatar, Card, Col, List, Row, Typography} from 'antd';
import React, {useEffect, useState, useCallback} from 'react';
import {GridContent, PageContainer} from '@ant-design/pro-layout';
import {Link, connect} from 'umi';
import styles from './style.less';
import moment from "moment";
import {genAvatars, genTdkCrumbs, genTdkCrumbsFor404, typeUrl} from "@/utils/utils";
import {queryListSideCar} from "@/pages/home/service";
import AvatarList from "@/components/AvatarList";
import Title from "antd/lib/typography/Title";
import {previewArticle, queryArticle} from "@/pages/book/article/service";
import Comments from "@/pages/book/detail/components/Comments";
import Share from "@/components/share";
import remarkGfm from "remark-gfm";
import rehypeRaw from "rehype-raw";
import ReactMarkdown from "react-markdown";
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { vscDarkPlus, vs } from 'react-syntax-highlighter/dist/esm/styles/prism';
import '@/styles/markdown.css';
import MarkdownImage from '@/components/MarkdownImage';

const Article = (props) => {
  const {dispatch, match, } = props;

  const [article, setArticle] = useState(undefined);
  const [listSide, setList] = useState(undefined);
  
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

  useEffect(async () => {
    const {params} = match;
    let res = !params.preview ? await queryArticle({id: params.id}) : await previewArticle({id: params.id, preview: params.preview});
    
    if (res?.data) {
      await genTdkCrumbs(res.data, dispatch, '正文', '');
      setArticle(res.data);
      
      // 设置水印
      if (res.data.watermarkText || res.data.watermarkEnabled !== undefined) {
        setWatermarkText(res.data.watermarkText || 'WLDOS');
        setWatermarkEnabled(res.data.watermarkEnabled === 'true' || res.data.watermarkEnabled === true);
      }
    }else{
      await genTdkCrumbsFor404(dispatch);
      setArticle("");
    }

    res = await queryListSideCar({
      pageName: 'all',
    });

    if (res?.data) {
      setList(res.data.rows);
    }
  }, [match.url]);

  const cardList = listSide && (
    <List
      grid={{
        gutter: 24, xs: 1, sm: 1, md: 1, lg: 1, xl: 1, xxl: 1,
      }}
      dataSource={listSide}
      renderItem={({cover, id, members, pubTitle, pubType, subTitle, createTime}, index) => (
        <List.Item key={`${id}-prod-${index}`}>
            <Card className={styles.card} hoverable onClick={() => window.open(typeUrl({pubType, id}), '_blank')}
                  cover={<img alt={pubTitle} src={cover}/>}>
              <Card.Meta
                title={<Link to={typeUrl({pubType, id})} target="_blank" rel="noopener">{pubTitle}</Link>}
              />
              <div className={styles.cardItemContent}>
                <span>{moment(createTime).fromNow()}</span>
                <div className={styles.avatarList}>
                  <AvatarList size="small">
                    {
                      genAvatars(`${id}-article-`, members)
                    }
                  </AvatarList>
                </div>
              </div>
            </Card>
        </List.Item>
      )}
    />
  );

  const titleStyle = {
    marginBottom: '8px',
    paddingBottom: '8px',
    lineHeight: '40px',
    fontSize: '28px',
    fontWeight: '500',
    borderBottom: '2px solid #f4f4f4'
  };

  const enumTags = (tags = [], ) => {
    const temp = [];
    for (let i = 0, len = tags.length; i < len; i += 1) {
      const tag = tags[i];
      if (tag)
        temp.push((<Link key={`${tag.id}-art-tag-${i}`} to={`/archives/tag/${tag.slug}`} target="_blank" rel="noopener">{i<=len-2 ? `${tag.name}, ` : `${tag.name}`}</Link>));
    }
    return temp;
  };

  const articleBody = article && (
    <Typography>
      <Title level={1} style={titleStyle}>{article.pubTitle}</Title>
      <div className={styles.pubMeta}>
        <Avatar
          key={1}
          src={article.member.avatar}
          tips={article.member.nickname}
        />{' '}
        <Link to={`/archives-author/${article.member.id}.html`} target="_blank" rel="noopener">{article.member.nickname}</Link>
        <em>{moment(article.createTime).format('YYYY-MM-DD HH:mm')}</em>
        {' '}阅读 {article.views}
      </div>
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
            className={"markdown-body"}
            components={{
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
          <div dangerouslySetInnerHTML={{ __html: article.pubContent}} />
        )}
      </div>
      <Share style={styles.share} title={article.pubTitle || '无标题'} src={'@/assets/log.svg'}/>
      <div className={styles.pubMetaBottom}>
        <div className={styles.tags}>
        <span>
          tags: {article.tags && enumTags(article.tags)}
        </span>
        </div>
      </div>
      <div className={styles.pubPrevNext}>
        {article.prev && (<><strong>上一篇</strong>：<Link to={`/archives-${article.prev.id}.html`} rel="prev">{article.prev.pubTitle}</Link><br/></>)}
        {article.next && (<><strong>下一篇</strong>：<Link to={`/archives-${article.next.id}.html`} rel="next">{article.next.pubTitle}</Link></>)}
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
                    <Link to={`/archives-${item.id}.html`} target="_blank" rel="noopener">
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
);

return (
  <PageContainer title={false} breadcrumb={false}>
    <GridContent>
      <Row gutter={24}>
        <Col lg={6} md={24}>
          <Card title="热门排行推荐" className={styles.hotRec} bordered={false}
                style={{marginBottom: 24,}}>
            {cardList}
          </Card>
        </Col>
        <Col lg={18} md={24}>
          <Card className={styles.tabsCard} bordered={true}>
            {article === "" ? <p>请求的内容不存在了……</p> : articleBody}
          </Card>
        </Col>
      </Row>
    </GridContent>
  </PageContainer>
);
};

export default connect(({ user } ) => (
  {
    tdk: user.tdk,
  }
))(Article);