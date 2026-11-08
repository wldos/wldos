/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import { Helmet, HelmetProvider } from 'react-helmet-async';
import { Link, SelectLang, connect } from 'umi';
import React, {useEffect, useState,} from 'react';
import wldosFooterDom from '@/layouts/footAdmin';
import styles from './UserLayout.less';
import {querySiteSlogan} from "@/services/user";
import { isDesktopEmbedded, mergeDesktopShellSite } from '@/utils/desktopEmbeddedBridge';

const UserLayout = (props) => {
  const {
    dispatch,
    children,
    site = {siteTitle: '', siteDescription: '', siteLogo: '', version: 'V1.0.1'},
  } = props;
  const [slogan, setSlogan] = useState('');

  useEffect(() => {
    if (dispatch) {

      dispatch({
        type: 'user/fetchSite',
      });
    }
  }, []);

  useEffect(async () => {
    const res = await querySiteSlogan();

    if (res && res.data) {
      const { slogan : slg } = res.data;
      setSlogan(slg);
    }
  }, []);

  const displaySite = isDesktopEmbedded() ? mergeDesktopShellSite(site) : site;
  const loginTitle = displaySite?.siteTitle || '请登录';

  return (
    <HelmetProvider>
      <Helmet>
        <title>{`登录-${loginTitle}`}</title>
        <meta name="description" content={displaySite?.siteDescription || site.siteDescription} />
      </Helmet>

      <div className={styles.container}>
        {/* <div className={styles.lang}>
          <SelectLang />
        </div> */}
        <div className={styles.content}>
          <div className={styles.top}>
            <div className={styles.header}>
              <Link to="/">
                {displaySite?.siteLogo ? (
                  <img alt="logo" className={styles.logo} src={displaySite.siteLogo} />
                ) : (
                  isDesktopEmbedded() && displaySite?.siteTitle ? (
                    <span className={styles.title}>{displaySite.siteTitle}</span>
                  ) : null
                )}
              </Link>
            </div>
            <div className={styles.desc}>{slogan}</div>
          </div>
          {children}
        </div>
        {wldosFooterDom(site.version)}
      </div>
    </HelmetProvider>
  );
};

export default connect(({ settings, user }) => ({
  ...settings,
  site: user.site,
}))(UserLayout);
