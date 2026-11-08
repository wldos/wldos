const EXAMPLE_COMPONENT_URL = 'admin/home/examples/commercial-overview';

export function parseAdminHomeConfig(raw) {
  if (raw == null || String(raw).trim() === '') {
    return {
      preset: 'inherit',
      module: '',
      url: '',
      title: '',
      extraProps: '{}',
      menuResourcePath: undefined,
    };
  }
  try {
    const m = JSON.parse(String(raw).trim());
    const module = (m.module || 'component').trim();
    const url = (m.url || '').trim();
    const title = (m.title || '').trim();
    const extraProps =
      m.extraProps == null ? '{}' : typeof m.extraProps === 'string' ? m.extraProps : JSON.stringify(m.extraProps);

    if (module === 'redirect' && url) {
      return {
        preset: 'menu',
        module,
        url,
        title,
        extraProps,
        menuResourcePath: url,
      };
    }
    if (module === 'component' && url === EXAMPLE_COMPONENT_URL) {
      return {
        preset: 'example',
        module,
        url,
        title,
        extraProps,
        menuResourcePath: undefined,
      };
    }
    return {
      preset: 'advanced',
      module,
      url,
      title,
      extraProps,
      menuResourcePath: undefined,
    };
  } catch (e) {
    return {
      preset: 'inherit',
      module: '',
      url: '',
      title: '',
      extraProps: '{}',
      menuResourcePath: undefined,
    };
  }
}

export function buildAdminHomeConfig(fields) {
  const preset = fields.adminHomePreset || 'inherit';
  if (preset === 'inherit') {
    return '';
  }
  if (preset === 'menu') {
    const path = (fields.adminHomeMenuPath || fields.adminHomeUrl || '').trim();
    if (!path) {
      return '';
    }
    return JSON.stringify({
      module: 'redirect',
      url: path,
      title: (fields.adminHomeTitle || '').trim(),
      extraProps: (fields.adminHomeExtraProps || '{}').trim() || '{}',
    });
  }
  if (preset === 'example') {
    return JSON.stringify({
      module: 'component',
      url: EXAMPLE_COMPONENT_URL,
      title: (fields.adminHomeTitle || '').trim(),
      extraProps: (fields.adminHomeExtraProps || '{}').trim() || '{}',
    });
  }
  const module = (fields.adminHomeModule || 'component').trim();
  if (module === 'builtin') {
    return '';
  }
  const url = (fields.adminHomeUrl || '').trim();
  if ((module === 'redirect' || module === 'component') && !url) {
    return '';
  }
  return JSON.stringify({
    module,
    url,
    title: (fields.adminHomeTitle || '').trim(),
    extraProps: (fields.adminHomeExtraProps || '{}').trim() || '{}',
  });
}

export { EXAMPLE_COMPONENT_URL };
