/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 */

import React, { useMemo } from 'react';
import QRCode from 'qrcode.react';
import styles from './foot.less';
import { connect, getLocale } from 'umi';

const pickLocaleBlock = (site, locale) => {
  const raw = site?.portalFooterI18n;
  if (!raw) {
    return {
      foot: site?.foot || '',
      flink: site?.flink || '',
      copy: site?.copy || '',
    };
  }
  try {
    const map = typeof raw === 'string' ? JSON.parse(raw) : raw;
    const block = map[locale] || map['zh-CN'] || map[Object.keys(map)[0]] || {};
    return {
      foot: block.foot || site?.foot || '',
      flink: block.flink || site?.flink || '',
      copy: block.copy || site?.copy || '',
    };
  } catch (e) {
    return {
      foot: site?.foot || '',
      flink: site?.flink || '',
      copy: site?.copy || '',
    };
  }
};

const wldosFooterDom = (props) => {
  const { site = {} } = props;
  const locale = getLocale() || 'zh-CN';
  const { foot, flink, copy } = useMemo(() => pickLocaleBlock(site, locale), [site, locale]);
  const qrUrl = site.portalQrUrl || '';

  return (
    <>
      <footer className={styles.footer}>
        <div className={styles.inner}>
          <div className={styles.footBar} dangerouslySetInnerHTML={{ __html: foot }} />
          <div className={styles.friendLink} dangerouslySetInnerHTML={{ __html: flink }} />
          {qrUrl ? (
            <div className={styles.qrWrap}>
              <QRCode value={qrUrl} size={88} level="M" />
            </div>
          ) : null}
        </div>
      </footer>
      <div className={styles.clearfix} />
      <div className={styles.copyright}>
        <div className={styles.inner} dangerouslySetInnerHTML={{ __html: copy }} />
      </div>
    </>
  );
};

export default connect(({ user, loading }) => ({
  site: user.site,
  loading: loading.effects['user/fetchCurrent'],
}))(wldosFooterDom);
