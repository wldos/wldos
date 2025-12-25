import {Menu, message, Popconfirm, Popover} from "antd";
import React, {useEffect, useState} from "react";
import {connect} from "umi";
import styles from "@/pages/book/style.less";
import {QuestionCircleOutlined, SettingOutlined} from "@ant-design/icons";
import {
  deleteInfo,
  preUpdate,
  updateInfo
} from "@/pages/book/create/service";
import EditBookForm from "@/pages/book/components/EditBookForm";

const {Item} = Menu;
export const updateBook = async (bookInfo) => {
  const hide = message.loading('正在配置');

  try {
    const res = await updateInfo({
      id: bookInfo.id,
      pubTitle: bookInfo.pubTitle,
      subTitle: bookInfo.subTitle,
      pubName: bookInfo.pubName,
      pubType: bookInfo.pubType,
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
      watermarkText: bookInfo.watermarkText,
      watermarkEnabled: bookInfo.watermarkEnabled,
    });
    if (res?.data !== 'ok') {
      message.error(res?.data.error?? '信息保存异常').then();
      return false;
    }
    hide();
    message.success('配置成功');
    return true;
  } catch (error) {
    hide();
    message.error('配置失败请重试！');
    return false;
  }
};

const removeOne = async (currentBook) => {
  if (!currentBook) return true;

  if (!currentBook.isSingle && currentBook.chapter.length > 0) {
    message.error("存在内容，请先删除内容");
    return false;
  }

  const hide = message.loading('正在删除');
  try {
    const res = await deleteInfo({
      id: currentBook.id,
    });
    hide();
    if (res?.data !== 'ok') {
      message.error(res.data);
      return false;
    }
    message.success('删除成功，即将刷新');
    return true;
  } catch (error) {
    hide();
    message.error('删除失败，请重试');
    return false;
  }
};

const BookList = (props) => {
  const {
    dispatch,
    book,
    currentBook,
    categories,
    tagData,
    mode,
    history
  } = props;

  const [updateModalVisible, handleUpdateModalVisible] = useState(false);
  const [bookInfo, setBookInfo] = useState({});
  const [visible, setVisible] = useState(false);

  const clickListen = () => setVisible(false);
  const keyupListen = (e) => {if (e.key === 'Escape') setVisible(false);};

  useEffect(() => {
    window.addEventListener('mouseup', clickListen);
    window.addEventListener('keyup', keyupListen);
    return () => {
      window.removeEventListener('mouseup', clickListen);
      window.removeEventListener('keyup', keyupListen);
    }
  }, []);

  const rendItem = (curBook) => (
    <ul style={{padding: '0', marginLeft: -10}}>
        <li>
          <a onClick={async () => {
            const res = await preUpdate({id: curBook.id});
            if (res?.data) {
              const {pubTypeExt, ...otherValues} = res.data;
              const realValues = {...otherValues, ...pubTypeExt};
              setBookInfo(realValues);
            }
            handleUpdateModalVisible(true);
            setVisible(false);
          }}>配置作品</a>
        </li>
        <li style={{marginTop: 5}}>
          <Popconfirm title="您确定要删除？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
              onConfirm={async () => {
                const res = await removeOne(curBook);
                if (res) {
                  dispatch({ // 转发可以跳过effects，直接请求reducer
                    type: 'bookSpace/delCurBook',
                    payload: {data: curBook},
                  });
                  setVisible(false);
                  if (book?.length > 0)
                    history.push({pathname: `/space/book/${book[0].id}`});
                }
              }}>
            <a onClick={() => setVisible(false)}>删除作品</a>
          </Popconfirm>
        </li>
    </ul>
  );

  const getMenu = (bookList, curBook) => {
    // Link中对链接处理，如果当前章存在，并且属于当前作品，没必要再切换作品路由，保持章节路由不变，路由不变则页面不刷新，作品的配置控件就能生效
    return bookList?.length ? bookList.map(({id, pubTitle}) =>
        <Item key={id} title={pubTitle} onClick={(e) => {

          const config = document.getElementById("config");
          if (!config)
            return;
          const confPos = config.getBoundingClientRect();
          const eventX = e.domEvent.clientX;
          const eventY = e.domEvent.clientY;
          if (confPos.left < eventX && eventX < confPos.right && confPos.top < eventY && eventY < confPos.bottom) {
            config.click();
            setVisible(true);
          }
        }}>
          {id === curBook.id && <div className={styles.config}>
            <Popover placement="bottom" title={false} content={rendItem(curBook)} trigger="click" visible={visible}>
              <SettingOutlined id="config"  />
            </Popover>
          </div>
          }
          <span>{pubTitle}</span>
        </Item>)
      : "";
  };

  return (
    <>
      <Menu key="book" mode={mode} selectedKeys={currentBook.id}
            defaultSelectedKeys={[book?.length ? book[0].id : '']}
            onClick={(item) => {
              if (currentBook.id !== item.key) {
                history.push({
                  pathname: `/space/book/${item.key}`,
                });
              }
            }}
      >
        {getMenu(book, currentBook)}
      </Menu>
      {bookInfo && Object.keys(bookInfo).length ? (
        <EditBookForm
          onSubmit={async (value) => {
            const success = await updateBook(value);

            if (success) {
              dispatch({ // 转发可以跳过effects，直接请求reducer
                type: 'bookSpace/updateCurBook',
                payload: {data: value},
              });
              handleUpdateModalVisible(false);
              setBookInfo({});
            }
          }}
          onCancel={() => {
            handleUpdateModalVisible(false);
            setBookInfo({});
          }}
          updateModalVisible={updateModalVisible}
          values={bookInfo}
          categories={categories}
          tagData={tagData}
        />
      ) : null}
    </>
  );
};

export default  connect(({bookSpace, loading}) => ({
  book: bookSpace.book,
  currentBook: bookSpace.currentBook,
  currentChapter: bookSpace.currentChapter,
  categories: bookSpace.categories,
  tagData: bookSpace.tagData,
  loading: loading.models.bookSpace,
}))(BookList);
