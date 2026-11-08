import { message, Popconfirm, Dropdown, Menu } from "antd";

import React, { useEffect, useState } from "react";

import { connect } from "umi";

import styles from "@/pages/book/style.less";

import { QuestionCircleOutlined, SettingOutlined } from "@ant-design/icons";

import {

  deleteInfo,

  preUpdate,

  updateInfo

} from "@/pages/book/create/service";

import EditBookForm from "@/pages/book/components/EditBookForm";

import WorkbenchListPager from "@/pages/book/components/WorkbenchListPager";

import WorkbenchSortableList from "@/pages/book/components/WorkbenchSortableList";

import { fetchWorkbenchPolicyLimits, reorderWorks, shiftWork } from "@/pages/book/service";
import {
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

      visibilityScope: bookInfo.visibilityScope,

      reward: bookInfo.reward,

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

    history,

    onMobileBookSelected,

    bookPage,

    onBookPageChange,

    onReloadBooks,

    bookTotal,

    bookPubTypesApplied,

    bookKeywordApplied,

  } = props;



  const [updateModalVisible, handleUpdateModalVisible] = useState(false);

  const [bookInfo, setBookInfo] = useState({});

  const [visible, setVisible] = useState(false);

  const [workQuotaLine, setWorkQuotaLine] = useState(null);

  useEffect(() => {
    fetchWorkbenchPolicyLimits().then((res) => {
      const items = res?.data?.items;
      setWorkQuotaLine(formatQuotaLine('作品', pickPolicyLimit(items, ACTION_WORK)));
    });
  }, []);



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



  const shiftBookOne = async (id, step) => {
    const res = await shiftWork({
      id,
      step,
      ...workbenchListScopeFields({
        pubTypes: bookPubTypesApplied,
        keyword: bookKeywordApplied,
      }),
    });
    if (res?.code !== 200) {
      return;
    }
    setVisible(false);
    if (onReloadBooks) {
      onReloadBooks();
    }
  };

  const buildConfigMenu = (curBook) => {
    const index = (book || []).findIndex((b) => String(b.id) === String(curBook.id));
    const canUp = canMoveUpInPagedList(index, bookPage?.current);
    const canDown = canMoveDownInPagedList(
      index,
      book?.length || 0,
      bookPage?.current,
      bookPage?.pageSize,
      bookTotal,
    );

    return (
    <Menu>

      <Menu.Item

        key="config"

        onClick={async () => {

          const res = await preUpdate({ id: curBook.id });

          if (res?.data) {

            const { pubTypeExt, ...otherValues } = res.data;

            setBookInfo({ ...otherValues, ...pubTypeExt });

          }

          handleUpdateModalVisible(true);

          setVisible(false);

        }}

      >

        配置作品

      </Menu.Item>

      <Menu.Item
        key="moveUp"
        disabled={!canUp}
        onClick={() => shiftBookOne(curBook.id, -1)}
      >
        上移
      </Menu.Item>

      <Menu.Item
        key="moveDown"
        disabled={!canDown}
        onClick={() => shiftBookOne(curBook.id, 1)}
      >
        下移
      </Menu.Item>

      <Menu.Item key="delete">

        <Popconfirm

          title="您确定要删除？"

          icon={<QuestionCircleOutlined style={{ color: 'red' }} />}

          onConfirm={async () => {

            const res = await removeOne(curBook);

            if (res) {

              dispatch({

                type: 'bookSpace/delCurBook',

                payload: { data: curBook },

              });

              setVisible(false);

              if (book?.length > 0) {

                history.push({ pathname: `/space/book/${book[0].id}` });

              }

            }

          }}

        >

          <span onClick={(e) => e.stopPropagation()}>删除作品</span>

        </Popconfirm>

      </Menu.Item>

    </Menu>
    );
  };



  const handleReorder = async (orderedIds) => {
    const prevIds = (book || []).map((b) => String(b.id));
    const nextIds = orderedIds.map((id) => String(id));
    if (prevIds.join(',') === nextIds.join(',')) {
      return;
    }
    const ok = await persistWorkbenchListReorder({
      orderedIds,
      reorderPage: (ids) => reorderWorks(buildWorkbenchReorderBody({
        orderedIds: ids,
        pubTypes: bookPubTypesApplied,
        keyword: bookKeywordApplied,
      })),
    });
    if (ok && onReloadBooks) {
      onReloadBooks();
    }
  };



  const renderSelectedExtra = (curBook) => (

    <div className={styles.config}>

      <Dropdown

        overlay={buildConfigMenu(curBook)}

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

          <SettingOutlined aria-label="作品配置" />

        </span>

      </Dropdown>

    </div>

  );



  return (

    <>

      <div className={styles.workbenchListPanel}>
        {workQuotaLine ? (
          <div className={styles.workbenchQuotaHint}>{workQuotaLine}</div>
        ) : null}
        <div className={styles.listScroll}>
          <WorkbenchSortableList
            items={book || []}
            selectedKey={currentBook?.id}
            onReorder={handleReorder}
            renderSelectedExtra={renderSelectedExtra}
            onSelect={(selected) => {
              if (onMobileBookSelected) {
                onMobileBookSelected(selected);
              }
              if (currentBook?.id !== selected.id) {
                history.push({
                  pathname: `/space/book/${selected.id}`,
                });
              }
            }}
          />
        </div>
        <WorkbenchListPager
          current={bookPage?.current}
          pageSize={bookPage?.pageSize}
          total={bookTotal}
          onChange={onBookPageChange}
        />
      </div>

      {bookInfo && Object.keys(bookInfo).length ? (

        <EditBookForm

          onSubmit={async (value) => {

            const success = await updateBook(value);



            if (success) {

              dispatch({

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

  bookTotal: bookSpace.total,

  loading: loading.models.bookSpace,

}))(BookList);

