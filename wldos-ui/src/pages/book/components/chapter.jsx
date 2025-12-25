import React, {useEffect, useState} from 'react';
import {Menu, message, Popconfirm, Popover} from 'antd';
import {connect} from "umi";
import styles from "@/pages/book/style.less";
import {QuestionCircleOutlined, SettingOutlined} from "@ant-design/icons";
import {
  deleteInfo,
  preUpdate,updateInfo
} from "@/pages/book/create/service";
import EditChapterForm from "@/pages/book/components/EditChapterForm";

const {Item} = Menu;

const updateChapter = async (contInfo) => {
  const hide = message.loading('正在配置');

  try {
    const res = await updateInfo({
      id: contInfo.id,
      pubName: contInfo.pubName,
      pubExcerpt: contInfo.pubExcerpt,
      privacyLevel: contInfo.privacyLevel,
      cover: contInfo.cover,
      tagIds: contInfo.tagIds,
      termTypeIds: contInfo.termTypeIds,
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

const removeOne = async (id) => {
  if (!id) return true;

  const hide = message.loading('正在删除');
  try {
    await deleteInfo({
      id,
    });
    hide();
    message.success('删除成功，即将刷新');
    return true;
  } catch (error) {
    hide();
    message.error('删除失败，请重试');
    return false;
  }
};

const Chapter = (props) => {
  const {
    dispatch,
    currentBook,
    currentChapter,
    mode,
    categories,
    tagData,
    history
  } = props;

  const {id: bookId, chapter} = currentBook;
  const [visible, setVisible] = useState(false);
  const [contInfo, setContInfo] = useState({});
  const [updateModalVisible, handleUpdateModalVisible] = useState(false);

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

  const rendItem = (curCont) => (
    <ul style={{padding: '0', marginLeft: -10}}>
      <li>
        <a onClick={async () => {
          const res = await preUpdate({id: curCont.id});
          if (res?.data) {
            const {pubTypeExt, ...otherValues} = res.data;
            const realValues = {...otherValues, ...pubTypeExt};
            setContInfo(realValues);
          }

          handleUpdateModalVisible(true);
          setVisible(false);
        }}>配置内容</a>
      </li>
      <li style={{marginTop: 5}}>
        <Popconfirm title="您确定要删除？" icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
              onConfirm={async () => {
                const res = await removeOne(curCont.id);
                if (res) {
                  dispatch({ // 转发可以跳过effects，直接请求reducer
                    type: 'bookSpace/delCurChapter',
                    payload: {data: curCont},
                  });
                  setVisible(false);
                }
              }}>
          <a onClick={() => setVisible(false)}>删除此条</a>
        </Popconfirm>
      </li>
    </ul>
  );

  const getMenuChapter = (chapterList, curCont) => {
    return chapterList?.length ? chapterList.map(({id, pubTitle}) =>
        <Item key={id} title={pubTitle} onClick={(e) => {
          const config = document.getElementById("subConf");
          if (!config) {
            return;
          }
          const confPos = config.getBoundingClientRect();
          const eventX = e.domEvent.clientX;
          const eventY = e.domEvent.clientY;
          if (confPos.left < eventX && eventX < confPos.right && confPos.top < eventY && eventY < confPos.bottom) {
            config.click();
            setVisible(true);
          }
        }}>
          {id === curCont.id && <div className={styles.config}>
            <Popover placement="bottomRight" title={false} content={rendItem(curCont)} trigger="click" visible={visible}>
              <SettingOutlined id="subConf" />
            </Popover>
          </div>}
          <span>{pubTitle}</span>
        </Item>)
      : "";
  };

  return (
    <>
    <Menu key="chapter" mode={mode}
          selectedKeys={currentChapter.id}
          defaultSelectedKeys={[chapter?.length ? chapter[0].id : '']}
          onClick={(item) => {
            if (currentChapter.id !== item.key) {
              history.push({
                pathname: `/space/book/${bookId}/chapter/${item.key}`,
              });
            }
          }}
    >
      {getMenuChapter(chapter, currentChapter)}
    </Menu>
    {contInfo && Object.keys(contInfo).length ? (
      <EditChapterForm
        onSubmit={async (value) => {
          const success = await updateChapter(value);

          if (success) {
            handleUpdateModalVisible(false);
            setContInfo({});
          }
        }}
        onCancel={() => {
          handleUpdateModalVisible(false);
          setContInfo({});
        }}
        updateModalVisible={updateModalVisible}
        values={contInfo}
        categories={categories}
        tagData={tagData}
      />
    ) : null}
  </>
  );
};

export default connect(({bookSpace, loading}) => ({
  currentBook: bookSpace.currentBook,
  currentChapter: bookSpace.currentChapter,
  categories: bookSpace.categories,
  tagData: bookSpace.tagData,
  loading: loading.models.bookSpace,
}))(Chapter);
