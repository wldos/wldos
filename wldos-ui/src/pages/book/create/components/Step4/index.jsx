import {Button, Descriptions, Result, Statistic} from 'antd';
import React from 'react';
import {connect, Link, FormattedMessage} from 'umi';
import styles from './index.less';
import GeographicView from "@/pages/account/settings/components/GeographicView";
import {bodyContent} from "@/utils/utils";
import { usePubVisibilityScopeEnum } from '@/hooks/usePubVisibilityScopeEnum';

const Step4 = (props) => {
  const { data, geographic, dispatch, showVisibilityScope = false } = props;
  const { labelOf: visibilityScopeLabelOf } = usePubVisibilityScopeEnum();
  if (!data) {
    return null;
  }

  const { pubTitle, subTitle, ornPrice, telephone, contact, pubContent, visibilityScope, reward, id } = data;
  const visibilityScopeLabel = visibilityScopeLabelOf(visibilityScope || 'PUBLIC_LISTED');

  const onFinish = () => {
    if (dispatch) {
      dispatch({
        type: 'bookInfo/clearAndSaveCurStep',
        payload: 'category',
      });
    }
  };

  const information = (
    <div className={styles.information}>
      <Descriptions column={1}>
        <Descriptions.Item label={<FormattedMessage id="book.create.step4.info.title" defaultMessage="封面标题" />}> {pubTitle}</Descriptions.Item>
        <Descriptions.Item label={<FormattedMessage id="book.create.step4.info.subtitle" defaultMessage="标榜短语" />}> {subTitle}</Descriptions.Item>
        <Descriptions.Item label={<FormattedMessage id="book.create.step4.info.contactName" defaultMessage="联系人姓名" />}> {contact}</Descriptions.Item>
        <Descriptions.Item label={<FormattedMessage id="book.create.step4.info.contactPhone" defaultMessage="联系人电话" />}> {telephone}</Descriptions.Item>
        <Descriptions.Item label={<FormattedMessage id="book.create.step4.info.region" defaultMessage="归属地区" />}> <GeographicView value={{...geographic}} /></Descriptions.Item>
        <Descriptions.Item label={<FormattedMessage id="book.create.step4.info.price" defaultMessage="查看价格" />}>
          <Statistic value={ornPrice} suffix={<FormattedMessage id="book.create.step4.info.price.unit" defaultMessage="元" />} />
        </Descriptions.Item>
        {showVisibilityScope ? (
          <Descriptions.Item label={<FormattedMessage id="book.create.step4.info.visibilityScope" defaultMessage="发现范围" />}>{visibilityScopeLabel}</Descriptions.Item>
        ) : null}
        {reward && <Descriptions.Item label={<FormattedMessage id="book.create.step4.info.reward" defaultMessage="打赏金额" />}> {reward}</Descriptions.Item>}
        <Descriptions.Item label={<FormattedMessage id="book.create.step4.info.detail" defaultMessage="详情描述" />}> {bodyContent(pubContent)}</Descriptions.Item>
      </Descriptions>
    </div>
  );
  const extra = (
    <>
      <Button type="primary" onClick={onFinish}>
        <FormattedMessage id="book.create.step4.button.again" defaultMessage="再发一条" />
      </Button>
      <Button><Link to={`/info-${id}.html`} target="_blank">查看信息</Link></Button>
    </>
  );
  return (
    <Result
      status="success"
      title={<FormattedMessage id="book.create.step4.result.title" defaultMessage="发布成功" />}
      subTitle={<FormattedMessage id="book.create.step4.result.subTitle" defaultMessage="请确保信息准确合法" />}
      extra={extra}
      className={styles.result}
    >
      {information}
    </Result>
  );
};

import {canManageVisibilityScope} from '@/utils/userPrivilege';

export default connect(({ bookInfo, user }) => ({
  data: bookInfo.step,
  geographic: bookInfo.geographic,
  showVisibilityScope: canManageVisibilityScope(user),
}))(Step4);
