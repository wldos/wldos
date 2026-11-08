import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function fetchSiteMenuTree(domainId) {
  return request(`${prefix}/admin/sys/domain/siteMenuTree`, {
    params: { domainId },
  });
}

export async function saveResTreeOverlay(data) {
  return request(`${prefix}/admin/sys/domain/resTreeOverlay`, {
    method: 'POST',
    data,
  });
}

export async function createSiteNavDir(data) {
  return request(`${prefix}/admin/sys/domain/siteNavDir`, {
    method: 'POST',
    data,
  });
}

export async function updateSiteNavDir(data) {
  return request(`${prefix}/admin/sys/domain/siteNavDir`, {
    method: 'PUT',
    data,
  });
}

export async function deleteSiteNavDir(data) {
  return request(`${prefix}/admin/sys/domain/siteNavDir`, {
    method: 'DELETE',
    data,
  });
}
