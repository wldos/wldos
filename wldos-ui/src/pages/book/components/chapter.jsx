import React, {useEffect, useState} from 'react';

import {Menu, message, Popconfirm, Dropdown} from 'antd';

import {connect} from "umi";

import styles from "@/pages/book/style.less";

import {QuestionCircleOutlined, SettingOutlined} from "@ant-design/icons";

import {

  deleteInfo,

  preUpdate,updateInfo

} from "@/pages/book/create/service";

import EditChapterForm from "@/pages/book/components/EditChapterForm";

import WorkbenchListPager from "@/pages/book/components/WorkbenchListPager";

import WorkbenchSortableList from "@/pages/book/components/WorkbenchSortableList";

import { fetchWorkbenchPolicyLimits, reorderChapters, shiftChapter } from "@/pages/book/service";
import {
  ACTION_CHAPTER,
  ACTION_WORK,
  formatQuotaLine,
  pickPolicyLimit,
} from "@/pages/book/components/workbenchPolicyQuota";

import {
  canMoveDownInPagedList,
  canMoveUpInPagedList,
  buildWorkbenchReorderBody,
  persistWorkbenchListReorder,
  workbenchListScopeFields,
} from "@/pages/book/components/workbenchListOrder";



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

    categories,

    tagData,

    history,

    onMobileChapterSelected,

    chapterPage,

    onChapterPageChange,

    onReloadChapters,

    chapterKeywordApplied,

  } = props;



  const {id: bookId, chapter, chapterTotal} = currentBook;

  const [visible, setVisible] = useState(false);

  const [contInfo, setContInfo] = useState({});

  const [updateModalVisible, handleUpdateModalVisible] = useState(false);

  const [quotaHint, setQuotaHint] = useState(null);

  useEffect(() => {
    if (!bookId) {
      return;
    }
    fetchWorkbenchPolicyLimits(bookId).then((res) => {
      const items = res?.data?.items;
      const parts = [
        formatQuotaLine('作品', pickPolicyLimit(items, ACTION_WORK)),
        formatQuotaLine('章节', pickPolicyLimit(items, ACTION_CHAPTER)),
      ].filter(Boolean);
      setQuotaHint(parts.length ? parts.join(' · ') : null);
    });
  }, [bookId]);

  const clickListen = (e) => {

    const el = e.target?.nodeType === 3 ? e.target.parentElement : e.target;

    if (el && typeof el.closest === 'function' && el.closest('.ant-dropdown')) {

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



  const shiftChapterOne = async (id, step) => {
    const res = await shiftChapter(bookId, {
      id,
      step,
      ...workbenchListScopeFields({ keyword: chapterKeywordApplied }),
    });
    if (res?.code !== 200) {
      return;
    }
    setVisible(false);
    if (onReloadChapters) {
      onReloadChapters();
    }
  };

  const buildConfigMenu = (curCont) => {
    const index = (chapter || []).findIndex((c) => String(c.id) === String(curCont.id));
    const canUp = canMoveUpInPagedList(index, chapterPage?.current);
    const canDown = canMoveDownInPagedList(
      index,
      chapter?.length || 0,
      chapterPage?.current,
      chapterPage?.pageSize,
      chapterTotal,
    );

    return (
    <Menu>

      <Menu.Item

        key="config"

        onClick={async () => {

          const res = await preUpdate({ id: curCont.id });

          if (res?.data) {

            const { pubTypeExt, ...otherValues } = res.data;

            setContInfo({ ...otherValues, ...pubTypeExt });

          }

          handleUpdateModalVisible(true);

          setVisible(false);

        }}

      >

        配置内容

      </Menu.Item>

      <Menu.Item
        key="moveUp"
        disabled={!canUp}
        onClick={() => shiftChapterOne(curCont.id, -1)}
      >
        上移
      </Menu.Item>

      <Menu.Item
        key="moveDown"
        disabled={!canDown}
        onClick={() => shiftChapterOne(curCont.id, 1)}
      >
        下移
      </Menu.Item>

      <Menu.Item key="delete">

        <Popconfirm

          title="您确定要删除？"

          icon={<QuestionCircleOutlined style={{ color: 'red' }} />}

          onConfirm={async () => {

            const res = await removeOne(curCont.id);

            if (res) {

              dispatch({

                type: 'bookSpace/delCurChapter',

                payload: { data: curCont },

              });

              setVisible(false);

            }

          }}

        >

          <span onClick={(e) => e.stopPropagation()}>删除此条</span>

        </Popconfirm>

      </Menu.Item>

    </Menu>
    );
  };



  const handleReorder = async (orderedIds) => {
    const prevIds = (chapter || []).map((c) => String(c.id));
    const nextIds = orderedIds.map((id) => String(id));
    if (prevIds.join(',') === nextIds.join(',')) {
      return;
    }
    const ok = await persistWorkbenchListReorder({
      orderedIds,
      reorderPage: (ids) => reorderChapters(bookId, buildWorkbenchReorderBody({
        orderedIds: ids,
        keyword: chapterKeywordApplied,
      })),
    });
    if (ok && onReloadChapters) {
      onReloadChapters();
    }
  };



  const renderSelectedExtra = (curCont) => (

    <div className={styles.config}>

      <Dropdown

        overlay={buildConfigMenu(curCont)}

        trigger={['click']}

        visible={visible}

        onVisibleChange={setVisible}

        overlayClassName={styles.workspaceConfigDropdown}

      >

        <span

          role="button"

          tabIndex={0}

          className={styles.menuConfigTrigger}

          onClick={(e) => e.stopPropagation()}

          onPointerDown={(e) => e.stopPropagation()}

        >

          <SettingOutlined aria-label="内容配置" />

        </span>

      </Dropdown>

    </div>

  );



  return (

    <>

    <div className={styles.workbenchListPanel}>
      {quotaHint ? <div className={styles.workbenchQuotaHint}>{quotaHint}</div> : null}
      <div className={styles.listScroll}>
        <WorkbenchSortableList
          items={chapter || []}
          selectedKey={currentChapter?.id}
          onReorder={handleReorder}
          renderSelectedExtra={renderSelectedExtra}
          onItemClickGuard={(e) => {
            const t = e.target;
            if (t?.closest?.(`.${styles.menuConfigTrigger}`) || t?.closest?.('.ant-dropdown')) {
              return true;
            }
            return false;
          }}
          onSelect={(selected) => {
            dispatch({
              type: 'bookSpace/fetchCurrentChapter',
              payload: {
                bookId,
                chapterId: selected.id,
              },
            });
            if (onMobileChapterSelected) {
              onMobileChapterSelected();
            }
            if (currentChapter?.id !== selected.id) {
              history.push({
                pathname: `/space/book/${bookId}/chapter/${selected.id}`,
              });
            }
          }}
        />
      </div>
      <WorkbenchListPager
        compact
        current={chapterPage?.current}
        pageSize={chapterPage?.pageSize}
        total={chapterTotal}
        onChange={onChapterPageChange}
      />
    </div>

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

