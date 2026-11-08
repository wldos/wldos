/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Shared portal section: optional CMS HTML inject or default children.
 */
import React from 'react';

/**
 * @param {string} sectionKey - portalStatic / portalStaticOrg block key
 * @param {string} sectionClass - CSS module class for <section>
 * @param {string} [html] - injected HTML from extraProps
 */
export default function PortalSection({ sectionKey, sectionClass, html, children }) {
  const trimmed = html != null && String(html).trim();
  if (trimmed) {
    return (
      <section
        className={`${sectionClass} portal-static-inject portal-static-${sectionKey}`}
        // eslint-disable-next-line react/no-danger
        dangerouslySetInnerHTML={{ __html: trimmed }}
      />
    );
  }
  return <section className={sectionClass}>{children}</section>;
}
