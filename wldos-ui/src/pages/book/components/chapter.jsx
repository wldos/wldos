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
    history,
    onMobileChapterSelected,
  } = props;

  const {id: bookId, chapter} = currentBook;
  const [visible, setVisible] = useState(false);
  const [contInfo, setContInfo] = useState({});
  const [updateModalVisible, handleUpdateModalVisible] = useState(false);

  /** 仅在点击浮层外时收起；浮层内 mouseup 不能关（否则先于 link 的 click 卸载 Popover，click 会落到下层 Menu 触发移动端切编辑区） */
  const clickListen = (e) => {
    const el = e.target?.nodeType === 3 ? e.target.parentElement : e.target;
    if (el && typeof el.closest === 'function' && el.closest('.ant-popover')) {
      return;
    }
    setVisible(false);
  };
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
        <a
          href="#"
          onClick={async (e) => {
            e.preventDefault();
            e.stopPropagation();
            const res = await preUpdate({id: curCont.id});
            if (res?.data) {
              const {pubTypeExt, ...otherValues} = res.data;
              const realValues = {...otherValues, ...pubTypeExt};
              setContInfo(realValues);
            }

            handleUpdateModalVisible(true);
            setVisible(false);
          }}
        >配置内容</a>
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
          <a href="#" onClick={(e) => { e.preventDefault(); e.stopPropagation(); setVisible(false); }}>删除此条</a>
        </Popconfirm>
      </li>
    </ul>
  );

  const getMenuChapter = (chapterList, curCont) => {
    return chapterList?.length ? chapterList.map(({id, pubTitle}) =>
        <Item key={id} title={pubTitle}>
          {id === curCont.id && <div className={styles.config}>
            <Popover placement="bottomRight" title={false} content={rendItem(curCont)} trigger="click" visible={visible}
              onVisibleChange={setVisible}>
              <span
                role="button"
                tabIndex={0}
                className={styles.menuConfigTrigger}
                onClick={(e) => e.stopPropagation()}
                onPointerDown={(e) => e.stopPropagation()}
              >
                <SettingOutlined aria-label="内容配置" />
              </span>
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
            // 点击齿轮或 Popover 内菜单：不触发章节选中（否则移动端会切到「编辑」区）
            const t = item.domEvent?.target;
            if (t?.closest?.(`.${styles.menuConfigTrigger}`) || t?.closest?.('.ant-popover')) {
              return;
            }
            // 无论是否为当前选中项，都先拉取一次章节详情，保证编辑区内容完整
            dispatch({
              type: 'bookSpace/fetchCurrentChapter',
              payload: {
                bookId,
                chapterId: item.key,
              },
            });
            if (onMobileChapterSelected) {
              onMobileChapterSelected();
            }
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
