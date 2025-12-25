import { Helmet, HelmetProvider } from 'react-helmet-async';
import { Link, SelectLang, connect } from 'umi';
import React, {useEffect, useState,} from 'react';
import wldosFooterDom from '@/layouts/footAdmin';
import styles from './UserLayout.less';
import {querySiteSlogan} from "@/services/user";

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

  return (
    <HelmetProvider>
      <Helmet>
        <title>{`登录-${site.siteTitle || '请登录'}`}</title>
        <meta name="description" content={site.siteDescription} />
      </Helmet>

      <div className={styles.container}>
        {/* <div className={styles.lang}>
          <SelectLang />
        </div> */}
        <div className={styles.content}>
          <div className={styles.top}>
            <div className={styles.header}>
              <Link to="/">
                {site.siteLogo && <img alt="logo" className={styles.logo} src={site.siteLogo} />}
                {/* site.siteName && <span className={styles.title}>{site.siteName}</span> */}
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
