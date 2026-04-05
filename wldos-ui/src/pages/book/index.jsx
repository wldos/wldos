import React, {Component} from 'react';
import {connect, history} from 'umi';
import {Button, message, Switch} from 'antd';
import {PlusOutlined} from '@ant-design/icons';
import styles from './style.less';
import HomeOutlined from "@ant-design/icons/HomeOutlined";
import Chapter from "@/pages/book/components/chapter";
import BookList from "@/pages/book/components/booklist";
import BookView from "@/pages/book/components/BookView";
import EditBookForm from "@/pages/book/components/EditBookForm";
import {addBook, addChapter} from "@/pages/book/service";
import updateDarkTheme from "@/components/DarkTheme/UpdateTheme";

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
  darkMode = localStorage.getItem("darkMode") === "1";

  constructor(props) {
    super(props);
    this.state = {
      mode: 'inline',
      modalVisible: false,
      url: '',
      darkMode: false,
      isPhone: false,
      mobilePane: null,
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
    updateDarkTheme(this.darkMode).then();
  }

  componentWillUnmount() {
    window.removeEventListener('resize', this.resize);
  }

  switchDarkMode = () => {
    localStorage.setItem("darkMode", this.darkMode ? "0" : "1");
    this.darkMode = !this.darkMode;
    this.setState({darkMode: this.darkMode});
    updateDarkTheme(this.darkMode).then();
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

  queryBooks = (dispatch, params, callback) => {
    dispatch({
      type: 'bookSpace/fetchBook',
      payload: {
        listStyle: 'archive', // 查询详细属性
        current: 1,
        pageSize: 50, // 普通会员最多50个作品
        sorter: {"createTime":"descend"}
      },
      callback,
    });
  };

  queryCurrentBook = (dispatch, params, callback) => {
    dispatch({
      type: 'bookSpace/fetchCurrentBook',
      payload: {
        bookId: params.bookId,
      },
      callback,
    });
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
      this.query();
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
    const {dispatch} = this.props;
    const { id } = currentBook;
    if (!id) {
      message.info('请先创建作品').then();
    }

      dispatch({
      type: 'bookSpace/addChapter',
      payload: {
        parentId: id,
      },
      });
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

    const {mode, modalVisible, darkMode, mobilePane, isPhone} = this.state;
    let activeMobilePane = mobilePane ?? this.getMobilePane(this.props);
    const isSingleBook = this.isSingleWork(currentBook);
    // 桌面端：仅复合型展示章节；手机端：为保证可达性，始终展示章节 tab
    const showChapterTab = !!currentBook?.id && (!isSingleBook || isPhone);
    const hasChapter = (currentBook?.chapter?.length || 0) > 0;

    const listMode = isPhone ? 'inline' : mode;

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
            <Button size="small" type="primary" shape="round" style={{ marginLeft: 2,}} href="/" target="_parent"><HomeOutlined/>返首页</Button>
            <Button size="small" type="text" style={{ marginLeft: 2,}} onClick={() => this.setModalVisible(true)}><PlusOutlined/>新建作品</Button>
            <Switch
              checkedChildren={<span className={styles.switchIcon}>🌙</span>}
              unCheckedChildren={<span className={styles.switchIcon}>☀</span>}
              onClick={this.switchDarkMode}
              defaultChecked={darkMode}
              size={"small"}
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
              {showChapterTab && (
                <Button
                  size="small"
                  type={activeMobilePane === 'chapters' ? 'primary' : 'default'}
                  onClick={() => this.setState((prevState, props) => ({ mobilePane: this.getMobilePane(props, 'chapters') }))}
                >
                  章节
                </Button>
              )}
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
                    <div>
                      <Button type="text" style={{ marginLeft: 2 }} onClick={() => this.addChapter(currentBook)}><PlusOutlined/>添加内容</Button>
                    </div>
                    <div className={styles.itemList}>
                      {hasChapter ? <Chapter {...{
                        mode: listMode,
                        match,
                        history,
                        onMobileChapterSelected: this.onMobileChapterSelected,
                      }} /> : (
                        <div className={styles.emptyTips}>暂无章节，请先添加内容</div>
                      )}
                    </div>
                  </div>
                )}
                {(!isPhone || activeMobilePane === 'editor') && (
                  <div className={styles.right}>
                    {hasChapter ? <BookView {...{dispatch, currentChapter}} />
                      : <div className={styles.emptyTips}>暂无可编辑章节，请先到“章节”添加内容</div>}
                  </div>
                )}
              </div>
            ) : (
              (!isPhone || activeMobilePane === 'editor') && (
                currentBook?.id ? <BookView {...{dispatch, currentChapter, isSingle: true}} />
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
