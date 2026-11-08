/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * wldos.org 开源门户首页（home/static-org）
 */
import React, { useEffect, useMemo } from 'react';
import { connect, useIntl } from 'umi';
import {
  LaptopOutlined,
  AppstoreOutlined,
  TeamOutlined,
  DownloadOutlined,
  GithubOutlined,
  FileTextOutlined,
  CodeOutlined,
  PullRequestOutlined,
} from '@ant-design/icons';
import PortalSection from '@/pages/home/components/PortalSection';
import { parsePortalStaticOrgSections } from './portalStaticOrg';
import { GITHUB_REPO_URL, GITEE_REPO_URL, GITHUB_RELEASES_URL, GITEE_RELEASES_URL } from './siteLinks';
import styles from './index.less';
import './portalStaticOrgInject.less';

const StaticOrgHome = (props) => {
  const { site, route } = props;
  const intl = useIntl();
  const portalSections = useMemo(
    () => parsePortalStaticOrgSections(route?.['/']?.extraProps),
    [route?.['/']?.extraProps],
  );

  const siteTitle = site?.siteTitle || intl.formatMessage({ id: 'portalOrg.meta.title' });
  const siteDescription =
    site?.siteDescription || intl.formatMessage({ id: 'portalOrg.meta.description' });

  useEffect(() => {
    if (site) {
      document.title = siteTitle;
      const metaDesc = document.querySelector('meta[name="description"]');
      if (metaDesc) {
        metaDesc.setAttribute('content', siteDescription);
      }
    }
  }, [site, siteTitle, siteDescription, intl.locale]);

  return (
    <div className={styles.staticOrgContainer}>
      <main>
        <PortalSection sectionKey="hero" sectionClass={styles.hero} html={portalSections.hero}>
          <div className={styles.heroContent}>
            <div className={styles.heroBadge}>
              {intl.formatMessage({ id: 'portalOrg.hero.badge' })}
            </div>
            <h1>WLDOS</h1>
            <p className={styles.heroLead}>{intl.formatMessage({ id: 'portalOrg.hero.lead' })}</p>
            <p className={styles.heroSubtitle}>
              {intl.formatMessage({ id: 'portalOrg.hero.subtitle' })}
            </p>
            <p className={styles.heroMeta}>{intl.formatMessage({ id: 'portalOrg.hero.meta' })}</p>
            <div className={styles.heroActions}>
              <a href="#get-started" className={styles.btnPrimary}>
                <DownloadOutlined /> {intl.formatMessage({ id: 'portalOrg.hero.cta.download' })}
              </a>
              <a href="/doc" target="_blank" rel="noopener noreferrer" className={styles.btnSecondary}>
                <FileTextOutlined /> {intl.formatMessage({ id: 'portalOrg.hero.cta.docs' })}
              </a>
            </div>
            <div className={styles.heroStats}>
              <div className={styles.statItem}>
                <div className={styles.statNumber}>
                  {intl.formatMessage({ id: 'portalOrg.hero.stat.deploy' })}
                </div>
                <div className={styles.statLabel}>
                  {intl.formatMessage({ id: 'portalOrg.hero.stat.deployLabel' })}
                </div>
              </div>
              <div className={styles.statItem}>
                <div className={styles.statNumber}>
                  {intl.formatMessage({ id: 'portalOrg.hero.stat.plugins' })}
                </div>
                <div className={styles.statLabel}>
                  {intl.formatMessage({ id: 'portalOrg.hero.stat.pluginsLabel' })}
                </div>
              </div>
              <div className={styles.statItem}>
                <div className={styles.statNumber}>
                  {intl.formatMessage({ id: 'portalOrg.hero.stat.community' })}
                </div>
                <div className={styles.statLabel}>
                  {intl.formatMessage({ id: 'portalOrg.hero.stat.communityLabel' })}
                </div>
              </div>
            </div>
          </div>
        </PortalSection>

        <PortalSection sectionKey="pillars" sectionClass={styles.pillars} html={portalSections.pillars}>
          <div className={styles.sectionHeader}>
            <h2>{intl.formatMessage({ id: 'portalOrg.pillars.title' })}</h2>
            <p>{intl.formatMessage({ id: 'portalOrg.pillars.subtitle' })}</p>
          </div>
          <div className={styles.pillarGrid}>
            <div className={styles.pillarItem}>
              <div className={styles.pillarIcon}>
                <LaptopOutlined />
              </div>
              <h3>{intl.formatMessage({ id: 'portalOrg.pillars.local.title' })}</h3>
              <p>{intl.formatMessage({ id: 'portalOrg.pillars.local.desc' })}</p>
            </div>
            <div className={styles.pillarItem}>
              <div className={styles.pillarIcon}>
                <AppstoreOutlined />
              </div>
              <h3>{intl.formatMessage({ id: 'portalOrg.pillars.plugins.title' })}</h3>
              <p>{intl.formatMessage({ id: 'portalOrg.pillars.plugins.desc' })}</p>
            </div>
            <div className={styles.pillarItem}>
              <div className={styles.pillarIcon}>
                <TeamOutlined />
              </div>
              <h3>{intl.formatMessage({ id: 'portalOrg.pillars.community.title' })}</h3>
              <p>{intl.formatMessage({ id: 'portalOrg.pillars.community.desc' })}</p>
            </div>
          </div>
        </PortalSection>

        <PortalSection
          sectionKey="getStarted"
          sectionClass={styles.getStarted}
          html={portalSections.getStarted}
        >
          <div id="get-started" className={styles.getStartedInner}>
            <div className={styles.sectionHeader}>
              <h2>{intl.formatMessage({ id: 'portalOrg.getStarted.title' })}</h2>
              <p>{intl.formatMessage({ id: 'portalOrg.getStarted.subtitle' })}</p>
            </div>
            <p className={styles.heroSubtitle} style={{ marginBottom: 0 }}>
              {intl.formatMessage({ id: 'portalOrg.getStarted.desc' })}
            </p>
            <div className={styles.getStartedActions}>
              <a
                href={GITHUB_RELEASES_URL}
                target="_blank"
                rel="noopener noreferrer"
                className={styles.btnPrimary}
              >
                <GithubOutlined /> GitHub Releases
              </a>
              <a
                href={GITEE_RELEASES_URL}
                target="_blank"
                rel="noopener noreferrer"
                className={styles.btnSecondary}
              >
                Gitee Releases
              </a>
              <a href="/doc" target="_blank" rel="noopener noreferrer" className={styles.btnSecondary}>
                <FileTextOutlined /> {intl.formatMessage({ id: 'portalOrg.getStarted.readDocs' })}
              </a>
            </div>
          </div>
        </PortalSection>

        <PortalSection
          sectionKey="contribute"
          sectionClass={styles.contribute}
          html={portalSections.contribute}
        >
          <div className={styles.sectionHeader}>
            <h2>{intl.formatMessage({ id: 'portalOrg.contribute.title' })}</h2>
            <p>{intl.formatMessage({ id: 'portalOrg.contribute.subtitle' })}</p>
          </div>
          <div className={styles.contributeGrid}>
            <div className={styles.contributeCard}>
              <h3>
                <CodeOutlined /> {intl.formatMessage({ id: 'portalOrg.contribute.code.title' })}
              </h3>
              <p>{intl.formatMessage({ id: 'portalOrg.contribute.code.desc' })}</p>
              <a
                href={GITHUB_REPO_URL}
                target="_blank"
                rel="noopener noreferrer"
                className={styles.btnSecondary}
              >
                {intl.formatMessage({ id: 'portalOrg.contribute.code.cta' })}
              </a>
            </div>
            <div className={styles.contributeCard}>
              <h3>
                <PullRequestOutlined />{' '}
                {intl.formatMessage({ id: 'portalOrg.contribute.plugin.title' })}
              </h3>
              <p>{intl.formatMessage({ id: 'portalOrg.contribute.plugin.desc' })}</p>
              <a href="/doc" className={styles.btnSecondary}>
                {intl.formatMessage({ id: 'portalOrg.contribute.plugin.cta' })}
              </a>
            </div>
          </div>
        </PortalSection>

        <PortalSection sectionKey="techStack" sectionClass={styles.techStack} html={portalSections.techStack}>
          <div className={styles.sectionHeader}>
            <h2>{intl.formatMessage({ id: 'portalOrg.techStack.title' })}</h2>
            <p>{intl.formatMessage({ id: 'portalOrg.techStack.subtitle' })}</p>
          </div>
          <div className={styles.techList}>
            <span className={styles.techTag}>Java</span>
            <span className={styles.techTag}>Spring Boot</span>
            <span className={styles.techTag}>React</span>
            <span className={styles.techTag}>UmiJS</span>
            <span className={styles.techTag}>
              {intl.formatMessage({ id: 'portalOrg.techStack.pluginArch' })}
            </span>
            <span className={styles.techTag}>
              {intl.formatMessage({ id: 'portalOrg.techStack.crossStackExt' })}
            </span>
          </div>
        </PortalSection>
      </main>
    </div>
  );
};

export default connect(({ user }) => ({
  site: user.site,
  route: user.route || {},
}))(StaticOrgHome);
