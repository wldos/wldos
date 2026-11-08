import React, {Component} from 'react';
import {connect, history} from 'umi';
import {Button, message} from 'antd';
import {PlusOutlined, HomeOutlined, SettingOutlined, SunOutlined, MoonOutlined} from '@ant-design/icons';
import styles from './style.less';
import Chapter from "@/pages/book/components/chapter";
import BookList from "@/pages/book/components/booklist";
import BookView from "@/pages/book/components/BookView";
import EditBookForm from "@/pages/book/components/EditBookForm";
import {addBook, addChapter} from "@/pages/book/service";
import WorkbenchInlineSearch from '@/pages/book/components/WorkbenchInlineSearch';
import WorkbenchInlineTypeFilter from '@/pages/book/components/WorkbenchInlineTypeFilter';
import { DARK_MODE_CHANGE_EVENT, readDarkMode, toggleDarkMode } from '@/components/DarkTheme/darkMode';
import { isDesktopEmbedded, requestCloseShellContent } from '@/utils/desktopEmbeddedBridge';

// 移动端三 pane 状态机（books/chapters/editor）收口函数：
// - 优先使用显式传入的目标 pane（事件驱动）
// - 否则按路由 + 作品类型推导默认 pane（数据驱动）
const resolveMobilePane = ({forcedPane, match = {}, currentBook = {}, isSingleWork}) => {
  if (forcedPane) return forcedPane;
  const path = match?.path || '';
  if (path === '/space/book') return 'books';
  if (isSingleWork(currentBook)) return 'editor';
  if (path === '/space/book/:bookId/chapter/:chapterId') return 'editor';
  return 'chapters';
};

class Book extends Component {
  main = undefined;
  constructor(props) {
    super(props);
    this.state = {
      mode: 'inline',
      modalVisible: false,
      url: '',
      darkMode: readDarkMode(),
      isPhone: false,
      mobilePane: null,
      assistantSettingsSignal: 0,
      bookPage: { current: 1, pageSize: 30 },
      chapterPage: { current: 1, pageSize: 30 },
      bookSearchOpen: false,
      bookKeyword: '',
      bookKeywordApplied: '',
      chapterSearchOpen: false,
      chapterKeyword: '',
      chapterKeywordApplied: '',
      bookPubTypesApplied: [],
    };
  }

  componentDidMount() {
    // 首次访问，清除bookUrl，防止历史数据干扰查询
    localStorage.removeItem('bookUrl');
    this.queryCategory(this.props.dispatch);
    this.queryTag(this.props.dispatch);
    window.addEventListener('resize', this.resize);
    this.resize();
    this.query();
    if (this.props.match)
      localStorage.setItem('bookUrl', this.props.match.url);
    this.onDarkModeChange = (event) => {
      this.setState({ darkMode: !!event.detail?.enabled });
    };
    window.addEventListener(DARK_MODE_CHANGE_EVENT, this.onDarkModeChange);
  }

  componentWillUnmount() {
    window.removeEventListener('resize', this.resize);
    if (this.onDarkModeChange) {
      window.removeEventListener(DARK_MODE_CHANGE_EVENT, this.onDarkModeChange);
    }
  }

  switchDarkMode = () => {
    this.setState({ darkMode: toggleDarkMode() });
  };

  query = () => {
    const {dispatch, match: {path, params}} = this.props;
    if (path === '/space/book') {
      this.queryBooks(dispatch, params, (res) => {
        const {data: {rows = [],},} = res;
        if (rows?.length > 0) {
          history.push({
            pathname: `/space/book/${rows[0].id}`,
          });
        }
      });
    } else if (path === '/space/book/:bookId/chapter/:chapterId') {
      if (this.props.book.length === 0) {
        this.queryBooks(dispatch, params, null);
      }
      if (!this.props.currentBook?.id) {
        this.queryCurrentBook(dispatch, params, null);
      }
      this.queryCurrentChapter(dispatch, params);
    } else { // match '/space/book/:bookId'
      if (this.props.book.length === 0) {
        this.queryBooks(dispatch, params, null);
      }
      this.queryCurrentBook(dispatch, params, (res) => this.callback4Chapter(dispatch, res), );
    }
  }

  callback4Chapter = (dispatch, resp) => {
    if (resp.success) {
      const {id: bookId, isSingle, chapter} = resp.data;
      const { isPhone } = this.state;
      // 移动端：始终以接口返回类型决定当前 pane，避免首击不切换
      if (isPhone) {
        this.setState({ mobilePane: isSingle ? 'editor' : 'chapters' });
      }
      if (chapter?.length > 0) {
        const {id: chapterId} = chapter[0];
        // 单体无需二次查询
        if (!isSingle) {
          if (isPhone) {
            // 移动端保留在作品路由，但必须拉取章节详情，保证进入编辑区有完整内容
            this.queryCurrentChapter(dispatch, {bookId, chapterId});
          } else {
            history.push({
              pathname: `/space/book/${bookId}/chapter/${chapterId}`,
            });
          }
        } else {
          if (!chapter || !chapter[0])
            return;
          dispatch({
            type: 'bookSpace/saveCurrentChapter',
            payload: {data: chapter[0]},
          });
        }
      } else {
        this.queryCurrentChapter(dispatch, {bookId, chapterId: ''});
      }
    }
  };

  queryBooks = (dispatch, params, callback, pageOverride, keywordOverride) => {
    const { bookPage, bookKeywordApplied, bookPubTypesApplied } = this.state;
    const page = pageOverride || bookPage;
    const keyword = keywordOverride !== undefined ? keywordOverride : bookKeywordApplied;
    const payload = {
      listStyle: 'archive',
      current: page.current,
      pageSize: page.pageSize,
      sorter: { displayOrder: 'ascend', id: 'ascend' },
    };
    if (keyword) {
      payload.keyword = keyword;
    }
    if (bookPubTypesApplied?.length) {
      payload.filter = JSON.stringify({ pubType: bookPubTypesApplied });
    }
    dispatch({
      type: 'bookSpace/fetchBook',
      payload,
      callback,
    });
  };

  queryCurrentBook = (dispatch, params, callback, chapterPageOverride, chapterKeywordOverride) => {
    const { chapterPage, chapterKeywordApplied } = this.state;
    const cp = chapterPageOverride || chapterPage;
    const chapterKeyword = chapterKeywordOverride !== undefined ? chapterKeywordOverride : chapterKeywordApplied;
    const payload = {
      bookId: params.bookId,
      chapterCurrent: cp.current,
      chapterPageSize: cp.pageSize,
      chapterSorter: JSON.stringify({ displayOrder: 'ascend', id: 'ascend' }),
    };
    if (chapterKeyword) {
      payload.chapterKeyword = chapterKeyword;
    }
    dispatch({
      type: 'bookSpace/fetchCurrentBook',
      payload,
      callback,
    });
  };

  submitBookSearch = (text) => {
    const keyword = (text || '').trim();
    const bookPage = { ...this.state.bookPage, current: 1 };
    this.setState({ bookKeywordApplied: keyword, bookKeyword: keyword, bookPage }, () => {
      this.queryBooks(this.props.dispatch, this.props.match?.params, null, bookPage, keyword);
    });
  };

  applyBookTypeFilter = (pubTypes) => {
    const nextTypes = Array.isArray(pubTypes) ? pubTypes : [];
    const bookPage = { ...this.state.bookPage, current: 1 };
    this.setState({ bookPubTypesApplied: nextTypes, bookPage }, () => {
      this.queryBooks(this.props.dispatch, this.props.match?.params, null, bookPage);
    });
  };

  setBookSearchOpen = (open) => {
    this.setState({ bookSearchOpen: open });
  };

  submitChapterSearch = (text) => {
    const keyword = (text || '').trim();
    const chapterPage = { ...this.state.chapterPage, current: 1 };
    this.setState({ chapterKeywordApplied: keyword, chapterKeyword: keyword, chapterPage }, () => {
      const bookId = this.props.currentBook?.id || this.props.match?.params?.bookId;
      if (bookId) {
        this.queryCurrentBook(this.props.dispatch, { bookId }, null, chapterPage, keyword);
      }
    });
  };

  onBookPageChange = (current, pageSize) => {
    const prev = this.state.bookPage;
    const nextSize = pageSize || prev.pageSize;
    const bookPage = {
      current: nextSize !== prev.pageSize ? 1 : current,
      pageSize: nextSize,
    };
    this.setState({ bookPage }, () => {
      this.queryBooks(this.props.dispatch, this.props.match?.params, null, bookPage);
    });
  };

  onChapterPageChange = (current, pageSize) => {
    const prev = this.state.chapterPage;
    const nextSize = pageSize || prev.pageSize;
    const chapterPage = {
      current: nextSize !== prev.pageSize ? 1 : current,
      pageSize: nextSize,
    };
    this.setState({ chapterPage }, () => {
      const bookId = this.props.currentBook?.id || this.props.match?.params?.bookId;
      if (bookId) {
        this.queryCurrentBook(this.props.dispatch, { bookId }, null, chapterPage);
      }
    });
  };

  reloadBooks = () => {
    this.queryBooks(this.props.dispatch, this.props.match?.params);
  };

  reloadChapters = () => {
    const bookId = this.props.currentBook?.id || this.props.match?.params?.bookId;
    if (bookId) {
      this.queryCurrentBook(this.props.dispatch, { bookId });
    }
  };

  queryCurrentChapter = (dispatch, params) => {
    dispatch({
      type: 'bookSpace/fetchCurrentChapter',
      payload: {
        bookId: params.bookId,
        chapterId: params.chapterId,
      },
    });
  };

  resize = () => {
    if (!this.main) {
      return;
    }

    requestAnimationFrame(() => {
      if (!this.main) {
        return;
      }

      const isPhone = window.innerWidth <= 640;
      const mode = isPhone ? 'horizontal' : 'inline';
      this.setState({ mode, isPhone });
    });
  };

  componentDidUpdate(prevProps) {
    const prevPath = prevProps?.match?.path;
    const nextPath = this.props?.match?.path;
    const prevBookParam = prevProps?.match?.params?.bookId;
    const nextBookParam = this.props?.match?.params?.bookId;
    const prevChapterParam = prevProps?.match?.params?.chapterId;
    const nextChapterParam = this.props?.match?.params?.chapterId;
    const routeChanged = prevPath !== nextPath
      || prevBookParam !== nextBookParam
      || prevChapterParam !== nextChapterParam;

    if (routeChanged) {
      if (prevBookParam !== nextBookParam && nextBookParam) {
        this.setState({
          chapterPage: { ...this.state.chapterPage, current: 1 },
          chapterKeyword: '',
          chapterKeywordApplied: '',
          chapterSearchOpen: false,
        });
      }
      this.query();
    }

    // 无章节（单体）作品不展示章节 pane：若仍停留在 chapters 则切回编辑区（避免隐藏 tab 后仍停留在无效 pane）
    const { isPhone, mobilePane } = this.state;
    if (isPhone && this.isSingleWork(this.props.currentBook) && mobilePane === 'chapters') {
      this.setState({ mobilePane: 'editor' });
    }

    // 不在这里全局重置 mobilePane，避免用户手动切 pane 后被异步状态回写覆盖
  }

  getMobilePane = (props, forcedPane) => resolveMobilePane({
    forcedPane,
    match: props?.match,
    currentBook: props?.currentBook,
    isSingleWork: this.isSingleWork,
  });

  queryCategory = (dispatch) => {
    dispatch({
      type: 'bookSpace/fetchCategory',
    });
  };

  queryTag = (dispatch) => {
    dispatch({
      type: 'bookSpace/fetchTag',
    });
  };

  setModalVisible = (bl) => {
    this.setState({modalVisible: bl});
  };

  isSingleWork = (bookItem) => {
    const raw = bookItem?.isSingle;
    return raw === true || raw === 1 || raw === '1' || raw === 'true';
  };

  onMobileBookSelected = (bookItem) => {
    if (!bookItem) return;
    // 点击作品后由 fetchCurrentBook 回调统一决定切换到章节或编辑
  };

  onMobileChapterSelected = () => {
    this.setState((prevState, props) => ({
      mobilePane: this.getMobilePane(props, 'editor'),
    }));
  };

  addChapter = async (currentBook) => {
    const { id } = currentBook;
    if (!id) {
      message.info('请先创建作品').then();
      return;
    }
    try {
      const res = await addChapter({ parentId: id });
      const chapterId = res?.data?.id;
      if (chapterId) {
        this.reloadChapters();
        history.push(`/space/book/${id}/chapter/${chapterId}`);
      }
    } catch (e) {
      message.error('添加章节失败');
    }
  };

  addBook = async (bookInfo) => {
    const hide = message.loading('正在创建');

    try {
      const res = await addBook({
        pubTitle: bookInfo.pubTitle,
        subTitle: bookInfo.subTitle,
        pubType: bookInfo.pubType,
        pubName: bookInfo.pubName,
        province: bookInfo.province,
        city: bookInfo.city,
        ornPrice: bookInfo.ornPrice,
        contact: bookInfo.contact,
        telephone: bookInfo.telephone,
        pubExcerpt: bookInfo.pubExcerpt,
        privacyLevel: bookInfo.privacyLevel,
        visibilityScope: bookInfo.visibilityScope,
        reward: bookInfo.reward,
        cover: bookInfo.cover,
        mainPic1: bookInfo.mainPic1,
        mainPic2: bookInfo.mainPic2,
        mainPic3: bookInfo.mainPic3,
        mainPic4: bookInfo.mainPic4,
        tagIds: bookInfo.tagIds,
        termTypeIds: bookInfo.termTypeIds,
      });
      const { id, error }= res?.data || {};
      if (!id) {
        message.error(error?? '信息保存异常').then();
        return false;
      }
      history.push('/space');
      hide();
      message.success('创建成功');
      return true;
    } catch (error) {
      hide();
      message.error('创建失败请重试！');
      return false;
    }
  };

  render() {
    const {
      dispatch,
      currentBook,
      currentChapter,
      categories,
      tagData,
      match,
    } = this.props;

    const {
      mode, modalVisible, darkMode, mobilePane, isPhone, assistantSettingsSignal,
      bookSearchOpen, bookKeyword, bookPubTypesApplied,
      chapterSearchOpen, chapterKeyword,
    } = this.state;
    let activeMobilePane = mobilePane ?? this.getMobilePane(this.props);
    const isSingleBook = this.isSingleWork(currentBook);
    const hasChapter = (currentBook?.chapter?.length || 0) > 0;

    const listMode = isPhone ? 'inline' : mode;

    /** 桌面端依据：桌面壳 JCEF 注入 window.cefQuery（见 isDesktopEmbedded）；云端无此前提 */
    const showAssistantSettings = isDesktopEmbedded();

    return (
      <div
        className={styles.main}
        ref={(ref) => {
          if (ref) {
            this.main = ref;
          }
        }}
      >
        <div
          className={`${styles.leftMenu} ${styles.book} ${
            isPhone && activeMobilePane !== 'books' ? styles.mobileOnlyNav : ''
          }`}
        >
          <div className={styles.topActions}>
            <Button
              size="small"
              type="default"
              href="/"
              target="_parent"
              icon={<HomeOutlined />}
              className={styles.topIconBtn}
              aria-label="返回首页"
              onClick={(e) => {
                // 桌面壳工作台模式下，"首页"应回到管家原生首屏（卸载本内容叠层），不是站内
                // 门户首页——门户首页在桌面嵌入场景下会被 bootstrapDesktopEmbeddedApp 的
                // entryPath 重定向逻辑再次弹回工作台本身，点了跟没点一样（回应"主页图标地址
                // 应该是返回管家首屏，而不是工作台"）。非桌面环境保留原有 href="/" 站内跳转。
                if (isDesktopEmbedded()) {
                  e.preventDefault();
                  const dispatched = requestCloseShellContent();
                  if (!dispatched && typeof window !== 'undefined' && window.history.length > 1) {
                    window.history.back();
                  }
                }
              }}
            />
            <WorkbenchInlineTypeFilter
              value={bookPubTypesApplied}
              onApply={this.applyBookTypeFilter}
            />
            <WorkbenchInlineSearch
              open={bookSearchOpen}
              onOpenChange={this.setBookSearchOpen}
              value={bookKeyword}
              onChange={(v) => this.setState({ bookKeyword: v })}
              onSubmit={this.submitBookSearch}
              placeholder="搜索作品"
            />
            <Button
              size="small"
              type="default"
              icon={<PlusOutlined />}
              className={styles.topIconBtn}
              aria-label="新建作品"
              onClick={() => this.setModalVisible(true)}
            />
            {showAssistantSettings ? (
              <Button
                size="small"
                type="default"
                icon={<SettingOutlined />}
                className={styles.topIconBtn}
                aria-label="自媒体发布设置"
                onClick={() =>
                  this.setState((prevState) => ({
                    assistantSettingsSignal: (prevState.assistantSettingsSignal || 0) + 1,
                    mobilePane: this.getMobilePane(this.props, 'editor'),
                  }))
                }
              />
            ) : null}
            <Button
              size="small"
              type="default"
              icon={darkMode ? <MoonOutlined /> : <SunOutlined />}
              className={styles.topIconBtn}
              aria-label={darkMode ? '切换为浅色' : '切换为深色'}
              onClick={this.switchDarkMode}
            />
          </div>
          {isPhone ? (
            <div className={styles.mobileNav}>
              <Button
                size="small"
                type={activeMobilePane === 'books' ? 'primary' : 'default'}
                onClick={() => this.setState((prevState, props) => ({ mobilePane: this.getMobilePane(props, 'books') }))}
              >
                作品
              </Button>
              {currentBook?.id && !isSingleBook ? (
                <Button
                  size="small"
                  type={activeMobilePane === 'chapters' ? 'primary' : 'default'}
                  onClick={() => this.setState((prevState, props) => ({ mobilePane: this.getMobilePane(props, 'chapters') }))}
                >
                  章节
                </Button>
              ) : null}
              <Button
                size="small"
                type={activeMobilePane === 'editor' ? 'primary' : 'default'}
                onClick={() => this.setState((prevState, props) => ({ mobilePane: this.getMobilePane(props, 'editor') }))}
              >
                编辑
              </Button>
            </div>
          ) : null}
          {(!isPhone || activeMobilePane === 'books') && (
            <div className={styles.itemList}>
              <BookList {...{
                match,
                mode: listMode,
                history,
                onMobileBookSelected: this.onMobileBookSelected,
                bookPage: this.state.bookPage,
                onBookPageChange: this.onBookPageChange,
                onReloadBooks: this.reloadBooks,
                bookPubTypesApplied: this.state.bookPubTypesApplied,
                bookKeywordApplied: this.state.bookKeywordApplied,
              }} />
            </div>
          )}
        </div>
        {(!isPhone || activeMobilePane !== 'books') && (
          <div className={`${styles.right} ${isPhone ? styles.mobileRight : ''}`}>
          {
              /* 判断是否复合结构，不是则展示一个编辑区 */
            !currentBook.isSingle ? (
              <div className={styles.main}>
                {(!isPhone || activeMobilePane === 'chapters') && (
                  <div className={`${styles.leftMenu} ${styles.chapter}`}>
                    <div className={styles.chapterToolbar}>
                      <Button type="text" style={{ marginLeft: 2 }} onClick={() => this.addChapter(currentBook)}><PlusOutlined/>添加内容</Button>
                      <WorkbenchInlineSearch
                        compact
                        open={chapterSearchOpen}
                        onOpenChange={(open) => this.setState({ chapterSearchOpen: open })}
                        value={chapterKeyword}
                        onChange={(v) => this.setState({ chapterKeyword: v })}
                        onSubmit={this.submitChapterSearch}
                        placeholder="搜索章节"
                      />
                    </div>
                    <div className={styles.itemList}>
                      {hasChapter ? <Chapter {...{
                        mode: listMode,
                        match,
                        history,
                        onMobileChapterSelected: this.onMobileChapterSelected,
                        chapterPage: this.state.chapterPage,
                        onChapterPageChange: this.onChapterPageChange,
                        onReloadChapters: this.reloadChapters,
                        chapterKeywordApplied: this.state.chapterKeywordApplied,
                      }} /> : (
                        <div className={styles.emptyTips}>暂无章节，请先添加内容</div>
                      )}
                    </div>
                  </div>
                )}
                {(!isPhone || activeMobilePane === 'editor') && (
                  <div className={styles.right}>
                    {hasChapter ? <BookView {...{dispatch, currentChapter, assistantSettingsSignal}} />
                      : <div className={styles.emptyTips}>暂无可编辑章节，请先到“章节”添加内容</div>}
                  </div>
                )}
              </div>
            ) : (
              (!isPhone || activeMobilePane === 'editor') && (
                currentBook?.id ? <BookView {...{dispatch, currentChapter, isSingle: true, assistantSettingsSignal}} />
                  : <div className={styles.emptyTips}>请先在“作品”中选择一个作品</div>
              )
            )
          }
          </div>
        )}
        {modalVisible ? (<EditBookForm
          onSubmit={async (value) => {
            const success = await this.addBook(value);

            if (success) {
              this.setModalVisible(false);
            }
          }}
          onCancel={() => {
            this.setModalVisible(false);
          }}
          updateModalVisible={modalVisible}
          values={{}}
          categories={categories}
          tagData={tagData}
        />) : null}
      </div>
    );
  }
}

export default connect(({bookSpace, loading}) => ({
  book: bookSpace.book,
  currentBook: bookSpace.currentBook,
  currentChapter: bookSpace.currentChapter,
  categories: bookSpace.categories,
  tagData: bookSpace.tagData,
  loading: loading.models.bookSpace,
}))(Book);
