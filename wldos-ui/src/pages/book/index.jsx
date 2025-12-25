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
    if (resp.message === 'ok') {
      const {id: bookId, isSingle, chapter} = resp.data;
      if (chapter?.length > 0) {
        const {id: chapterId} = chapter[0];
        // 单体无需二次查询
        if (!isSingle) {
          history.push({
            pathname: `/space/book/${bookId}/chapter/${chapterId}`,
          });
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

      let mode = 'inline';
      const {offsetWidth} = this.main;

      if (this.main.offsetWidth < 641 && offsetWidth > 400) {
        mode = 'horizontal';
      }

      if (window.innerWidth < 768 && offsetWidth > 400) {
        mode = 'horizontal';
      }

      this.setState({ mode });
    });
  };

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

    const {mode, modalVisible, darkMode} = this.state;

    if (match?.url !== localStorage?.getItem('bookUrl')) {
      this.query();
      localStorage.setItem('bookUrl', match.url);
    }

    return (
      <div
        className={styles.main}
        ref={(ref) => {
          if (ref) {
            this.main = ref;
          }
        }}
      >
        <div className={`${styles.leftMenu} ${styles.book}`}>
          <div>
            <Button type="primary" shape="round" style={{ marginLeft: 2,}} href="/" target="_parent"><HomeOutlined/>返首页</Button>
            <Button type="text" style={{ marginLeft: 2,}} onClick={() => this.setModalVisible(true)}><PlusOutlined/>新建作品</Button>
            <Switch checkedChildren="🌙" unCheckedChildren="☀" onClick={this.switchDarkMode} defaultChecked={darkMode} size={"small"} />
          </div>
          <div className={styles.itemList}>
            <BookList {...{ match, mode, history}} />
          </div>
        </div>
        <div className={styles.right}>
          {
              /* 判断是否复合结构，不是则展示一个编辑区 */
            !currentBook.isSingle ? (<div className={styles.main}>
                <div className={`${styles.leftMenu} ${styles.chapter}`}>
                  <div>
                    <Button type="text" style={{ marginLeft: 2 }} onClick={() => this.addChapter(currentBook)}><PlusOutlined/>添加内容</Button>
                  </div>
                  <div className={styles.itemList}>
                    <Chapter {...{mode, match, history}} />
                  </div>
                </div>
                <div className={styles.right}>
                  {currentBook?.chapter?.length > 0 ? <BookView {...{dispatch, currentChapter}} />
                    : <div className={styles.flow}><img alt="WLDOS" src="http://www.wldos.com/store/wldos.svg" /></div>}
                </div>
              </div>)
              : <BookView {...{dispatch, currentChapter, isSingle: true}} />
          }
        </div>
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
