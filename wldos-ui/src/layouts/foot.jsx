import React from 'react';
import styles from './foot.less';
import {connect} from "umi";

const wldosFooterDom = (props) => {
  const { site: {foot = {}, flink = {}, copy = {}} } = props;
  return (
    <>
      <footer className={styles.footer}>
        <div className={styles.inner}>
          <div className={styles.footBar} dangerouslySetInnerHTML={{__html: foot}}/>
          <div className={styles.friendLink} dangerouslySetInnerHTML={{__html: flink}}/>
        </div>
      </footer>
      <div className={styles.clearfix}/>
      <div className={styles.copyright}>
        <div className={styles.inner} dangerouslySetInnerHTML={{__html: copy}}/>
      </div></>
  );
};

export default connect(({ user, loading }) => ({
  site: user.site,
  loading: loading.effects['user/fetchCurrent'],
}))(wldosFooterDom);
