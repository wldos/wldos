import React, { useCallback, useEffect, useState } from 'react';
import {
  Button,
  Descriptions,
  Drawer,
  Input,
  message,
  Select,
  Space,
  Spin,
  Table,
  Tag,
  Typography,
} from 'antd';
import { Link, useIntl } from 'umi';
import { ReloadOutlined } from '@ant-design/icons';
import {
  getAdminPub,
  getSpaceBook,
  getSpaceBookChapter,
  updatePubVisibilityScope,
} from './cmsPickerService';
import { usePubVisibilityScopeEnum } from '@/hooks/usePubVisibilityScopeEnum';
import { usePubTypeEnum } from '@/hooks/usePubTypeEnum';
import { isChapterContainerPubType } from './cmsPickerUtils';
import CmsPreviewBody from './CmsPreviewBody';

const { Text } = Typography;
const DEFAULT_CHAPTER_PAGE_SIZE = 5;

function unwrapData(res) {
  if (res?.code !== 200) {
    return null;
  }
  return res?.data != null ? res.data : res;
}

/**
 * CMS 文档详情：主作品信息；复合类型分页展示章节并预览；有章节时在章节行选用。
 */
const PubPickerDetailDrawer = ({
  open,
  pubId,
  onClose,
  onPick,
  mode = 'preview',
  onMetaChange,
}) => {
  const intl = useIntl();
  const { options: visibilityScopeOptions } = usePubVisibilityScopeEnum();
  const { labelOf: pubTypeLabelOf } = usePubTypeEnum();
  const [loading, setLoading] = useState(false);
  const [chapterListLoading, setChapterListLoading] = useState(false);
  const [previewLoading, setPreviewLoading] = useState(false);
  const [savingScope, setSavingScope] = useState(false);
  const [meta, setMeta] = useState(null);
  const [chapters, setChapters] = useState([]);
  const [chapterPage, setChapterPage] = useState(1);
  const [chapterPageSize, setChapterPageSize] = useState(DEFAULT_CHAPTER_PAGE_SIZE);
  const [chapterTotal, setChapterTotal] = useState(0);
  const [chapterKeyword, setChapterKeyword] = useState('');
  const [selectedChapterId, setSelectedChapterId] = useState(null);
  const [selectedChapterTitle, setSelectedChapterTitle] = useState('');
  const [htmlContent, setHtmlContent] = useState('');
  const [previewMimeType, setPreviewMimeType] = useState('');
  const [visibilityScope, setVisibilityScope] = useState('PUBLIC_LISTED');

  const loadChapterPreview = useCallback(
    async (chapterId, chapterTitle) => {
      if (!pubId || chapterId == null) {
        return;
      }
      setSelectedChapterId(chapterId);
      setSelectedChapterTitle(chapterTitle || '');
      setPreviewLoading(true);
      try {
        const chRes = await getSpaceBookChapter(pubId, chapterId);
        const chapter = unwrapData(chRes);
        setHtmlContent(chapter?.pubContent || '');
        setPreviewMimeType(chapter?.pubMimeType || '');
      } finally {
        setPreviewLoading(false);
      }
    },
    [pubId],
  );

  const loadChapterPage = useCallback(
    async (page, pageSize, keyword) => {
      if (!pubId) {
        return;
      }
      setChapterListLoading(true);
      try {
        const trimmed = (keyword ?? '').trim();
        const bookRes = await getSpaceBook(pubId, {
          chapterCurrent: page,
          chapterPageSize: pageSize,
          ...(trimmed ? { chapterKeyword: trimmed } : {}),
        });
        const book = unwrapData(bookRes);
        const rows = Array.isArray(book?.chapter) ? book.chapter : [];
        setChapters(rows);
        setChapterTotal(Number(book?.chapterTotal) || 0);
        setChapterPage(page);
        setChapterPageSize(pageSize);
        if (rows.length > 0) {
          await loadChapterPreview(rows[0].id, rows[0].pubTitle);
        } else {
          setSelectedChapterId(null);
          setSelectedChapterTitle('');
          setHtmlContent('');
          setPreviewMimeType('');
        }
      } finally {
        setChapterListLoading(false);
      }
    },
    [pubId, loadChapterPreview],
  );

  const loadPreview = useCallback(async () => {
    if (!pubId) {
      return;
    }
    setLoading(true);
    try {
      const pubRes = await getAdminPub(pubId);
      const pub = unwrapData(pubRes);
      if (!pub) {
        setMeta(null);
        setChapters([]);
        setChapterTotal(0);
        setSelectedChapterId(null);
        setHtmlContent('');
        return;
      }
      setMeta({
        id: pub.id,
        pubTitle: pub.pubTitle,
        pubType: pub.pubType,
        visibilityScope: pub.visibilityScope || 'PUBLIC_LISTED',
      });
      setVisibilityScope(pub.visibilityScope || 'PUBLIC_LISTED');

      if (isChapterContainerPubType(pub.pubType)) {
        setChapterPage(1);
        setChapterKeyword('');
        await loadChapterPage(1, chapterPageSize, '');
      } else {
        setChapters([]);
        setChapterTotal(0);
        setSelectedChapterId(null);
        setSelectedChapterTitle('');
        setHtmlContent(pub.pubContent || '');
        setPreviewMimeType(pub.pubMimeType || '');
      }
    } finally {
      setLoading(false);
    }
  }, [pubId, loadChapterPage, chapterPageSize]);

  useEffect(() => {
    if (open && pubId) {
      loadPreview();
    }
    if (!open) {
      setMeta(null);
      setChapters([]);
      setSelectedChapterId(null);
      setSelectedChapterTitle('');
      setHtmlContent('');
      setPreviewMimeType('');
      setChapterPage(1);
      setChapterTotal(0);
      setChapterKeyword('');
    }
    // 仅随 open / pubId 拉主作品，避免章节关键词变更触发整页 loadPreview
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, pubId]);

  const handleChapterSearch = (value) => {
    const next = value ?? '';
    setChapterKeyword(next);
    setChapterPage(1);
    loadChapterPage(1, chapterPageSize, next);
  };

  const handleChapterKeywordChange = (e) => {
    const next = e.target.value;
    setChapterKeyword(next);
    if (next === '') {
      setChapterPage(1);
      loadChapterPage(1, chapterPageSize, '');
    }
  };

  const handleSaveScope = async () => {
    if (!pubId) {
      return;
    }
    setSavingScope(true);
    try {
      const res = await updatePubVisibilityScope(pubId, visibilityScope);
      if (res?.code === 200) {
        message.success(intl.formatMessage({ id: 'component.docSelect.msg.scopeSaved' }));
        setMeta((prev) => (prev ? { ...prev, visibilityScope } : prev));
        onMetaChange?.({ id: pubId, visibilityScope });
      }
    } finally {
      setSavingScope(false);
    }
  };

  const isCompositeWork = isChapterContainerPubType(meta?.pubType);
  const showChapterSection = isCompositeWork || chapterTotal > 0 || Boolean(chapterKeyword.trim());
  const hideWorkPick = chapterTotal > 0 || isCompositeWork;

  const handlePickWork = () => {
    if (meta?.id) {
      onPick?.(meta.id, meta.pubTitle, meta);
    }
  };

  const handlePickChapter = (record, e) => {
    e?.stopPropagation();
    if (!record?.id) {
      return;
    }
    onPick?.(record.id, record.pubTitle || record.title, {
      ...meta,
      id: record.id,
      pubTitle: record.pubTitle,
      parentWorkId: meta?.id,
      parentWorkTitle: meta?.pubTitle,
    });
  };

  const chapterColumns = [
    {
      title: intl.formatMessage({ id: 'component.docSelect.drawer.chapterTitle' }),
      dataIndex: 'pubTitle',
      ellipsis: true,
    },
    {
      title: 'ID',
      dataIndex: 'id',
      width: 200,
      ellipsis: true,
      render: (id) => (id != null ? String(id) : '—'),
    },
    ...(mode === 'picker' && onPick
      ? [
          {
            title: intl.formatMessage({ id: 'component.docSelect.col.action' }),
            width: 72,
            render: (_, record) => (
              <Button type="link" size="small" onClick={(e) => handlePickChapter(record, e)}>
                {intl.formatMessage({ id: 'component.docSelect.action.pick' })}
              </Button>
            ),
          },
        ]
      : []),
  ];

  return (
    <Drawer
      title={meta?.pubTitle || intl.formatMessage({ id: 'component.docSelect.drawer.title' })}
      width={800}
      open={open}
      onClose={onClose}
      destroyOnClose
      extra={
        <Space>
          <Button icon={<ReloadOutlined />} onClick={loadPreview} loading={loading}>
            {intl.formatMessage({ id: 'component.docSelect.drawer.refresh' })}
          </Button>
          {mode === 'picker' && onPick && !hideWorkPick ? (
            <Button type="primary" onClick={handlePickWork} disabled={!meta?.id}>
              {intl.formatMessage({ id: 'component.docSelect.action.pick' })}
            </Button>
          ) : null}
        </Space>
      }
    >
      <Spin spinning={loading}>
        {meta ? (
          <>
            <Descriptions size="small" column={1} bordered style={{ marginBottom: 16 }}>
              <Descriptions.Item label={intl.formatMessage({ id: 'component.docSelect.drawer.workId' })}>
                {meta.id != null ? String(meta.id) : '—'}
              </Descriptions.Item>
              <Descriptions.Item label={intl.formatMessage({ id: 'component.docSelect.col.type' })}>
                {pubTypeLabelOf(meta.pubType)}
              </Descriptions.Item>
              <Descriptions.Item label={intl.formatMessage({ id: 'component.docSelect.drawer.visibility' })}>
                <Space>
                  <Select
                    style={{ minWidth: 220 }}
                    value={visibilityScope}
                    options={visibilityScopeOptions}
                    onChange={setVisibilityScope}
                  />
                  <Button size="small" loading={savingScope} onClick={handleSaveScope}>
                    {intl.formatMessage({ id: 'component.docSelect.drawer.saveScope' })}
                  </Button>
                </Space>
              </Descriptions.Item>
              <Descriptions.Item label={intl.formatMessage({ id: 'component.docSelect.drawer.editor' })}>
                <Link to={`/space/book/${meta.id}`} target="_blank" rel="noopener noreferrer">
                  {intl.formatMessage({ id: 'component.docSelect.drawer.openEditor' })}
                </Link>
              </Descriptions.Item>
            </Descriptions>

            {showChapterSection ? (
              <div style={{ marginBottom: 16 }}>
                <Space direction="vertical" style={{ width: '100%' }} size={8}>
                  <div>
                    <Text strong>{intl.formatMessage({ id: 'component.docSelect.drawer.chapters' })}</Text>
                    <Text type="secondary" style={{ marginLeft: 8 }}>
                      {intl.formatMessage({ id: 'component.docSelect.drawer.pickChapterHint' })}
                    </Text>
                  </div>
                  <Input.Search
                    allowClear
                    placeholder={intl.formatMessage({ id: 'component.docSelect.drawer.chapterSearch' })}
                    value={chapterKeyword}
                    onChange={handleChapterKeywordChange}
                    onSearch={handleChapterSearch}
                    onPressEnter={(e) => {
                      e.preventDefault();
                      e.stopPropagation();
                    }}
                    style={{ maxWidth: 360 }}
                  />
                  <Table
                    size="small"
                    loading={chapterListLoading}
                    rowKey={(row) => String(row.id)}
                    dataSource={chapters}
                    columns={chapterColumns}
                    rowClassName={(row) =>
                      String(row.id) === String(selectedChapterId) ? 'ant-table-row-selected' : ''
                    }
                    onRow={(record) => ({
                      onClick: () => loadChapterPreview(record.id, record.pubTitle),
                      style: { cursor: 'pointer' },
                    })}
                    scroll={{ y: 240 }}
                    pagination={{
                      current: chapterPage,
                      pageSize: chapterPageSize,
                      total: chapterTotal,
                      size: 'small',
                      showSizeChanger: true,
                      pageSizeOptions: ['5', '10', '20'],
                      showTotal: (t) =>
                        intl.formatMessage({ id: 'component.docSelect.drawer.chapterTotal' }, { total: t }),
                      onChange: (page, size) => loadChapterPage(page, size, chapterKeyword),
                    }}
                  />
                </Space>
              </div>
            ) : null}

            <div>
              <Text strong>
                {intl.formatMessage({ id: 'component.docSelect.drawer.previewSection' })}
                {selectedChapterTitle ? ` · ${selectedChapterTitle}` : ''}
              </Text>
              <Spin spinning={previewLoading} style={{ display: 'block', marginTop: 12 }}>
                {!htmlContent ? (
                  <Tag>{intl.formatMessage({ id: 'component.docSelect.drawer.emptyContent' })}</Tag>
                ) : (
                  <CmsPreviewBody content={htmlContent} mimeType={previewMimeType} />
                )}
              </Spin>
            </div>
          </>
        ) : (
          !loading && <Tag>{intl.formatMessage({ id: 'component.docSelect.msg.previewFail' })}</Tag>
        )}
      </Spin>
    </Drawer>
  );
};

export default PubPickerDetailDrawer;
