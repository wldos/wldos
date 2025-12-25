import {Button, Descriptions, Result, Statistic} from 'antd';
import React from 'react';
import {connect, Link} from 'umi';
import styles from './index.less';
import GeographicView from "@/pages/account/settings/components/GeographicView";
import {bodyContent} from "@/utils/utils";

const Step4 = (props) => {
  const {data, privacyEnum, geographic, dispatch} = props;
  if (!data) {
    return null;
  }

  const {pubTitle, subTitle, ornPrice, telephone, contact, pubContent, privacyLevel, reward, id} = data;

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
        <Descriptions.Item label="封面标题"> {pubTitle}</Descriptions.Item>
        <Descriptions.Item label="标榜短语"> {subTitle}</Descriptions.Item>
        <Descriptions.Item label="联系人姓名"> {contact}</Descriptions.Item>
        <Descriptions.Item label="联系人电话"> {telephone}</Descriptions.Item>
        <Descriptions.Item label="归属地区"> <GeographicView value={{...geographic}} /></Descriptions.Item>
        <Descriptions.Item label="查看价格">
          <Statistic value={ornPrice} suffix="元"/>
        </Descriptions.Item>
        <Descriptions.Item label="查看方式"> {privacyEnum?.find(e => e.value === privacyLevel)?.label || ''}</Descriptions.Item>
        {reward && <Descriptions.Item label="打赏金额"> {reward}</Descriptions.Item>}
        <Descriptions.Item label="详情描述"> {bodyContent(pubContent)}</Descriptions.Item>
      </Descriptions>
    </div>
  );
  const extra = (
    <>
      <Button type="primary" onClick={onFinish}>
        再发一条
      </Button>
      <Button><Link to={`/info-${id}.html`} target="_blank">查看信息</Link></Button>
    </>
  );
  return (
    <Result
      status="success"
      title="发布成功"
      subTitle="请确保信息准确合法"
      extra={extra}
      className={styles.result}
    >
      {information}
    </Result>
  );
};

export default connect(({bookInfo}) => ({
  data: bookInfo.step,
  privacyEnum: bookInfo.privacyEnum,
  geographic: bookInfo.geographic,
}))(Step4);
