import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryPage(params) {
  return request(`${prefix}/admin/sys/domain`, {
    params,
  });
}
export async function removeEntity(params) {
  return request(`${prefix}/admin/sys/domain/delete`, {
    method: 'DELETE',
    data: params,
  });
}
export async function removeEntities(params) {
  return request(`${prefix}/admin/sys/domain/deletes`, {
    method: 'DELETE',
    data: params,
  });
}
export async function addEntity(params) {
  return request(`${prefix}/admin/sys/domain/add`, {
    method: 'POST',
    data: params,
  });
}
export async function updateEntity(params) {
  return request(`${prefix}/admin/sys/domain/update`, {
    method: 'POST',
    data: params,
  });
}

export async function queryDomainResList(params) {
  return request(`${prefix}/admin/sys/domain/resList`, {
    params,
  });
}
export async function addDomainApp(params) {
  return request(`${prefix}/admin/sys/domain/app`, {
    method: 'POST',
    data: params,
  });
}
export async function addDomainRes(params) {
  return request(`${prefix}/admin/sys/domain/res`, {
    method: 'POST',
    data: params,
  });
}
export async function getPlatDomain() {
  return request(`${prefix}/admin/sys/domain/root`);
}

export async function cloneSite(params) {
  return request(`${prefix}/admin/sys/domain/clone-site`, {
    method: 'POST',
    data: params,
    requestType: 'json',
  });
}

export async function queryDomainAdmins(params) {
  return request(`${prefix}/admin/sys/domain/admin`, {
    params,
  });
}

export async function bindDomainAdmin(data) {
  return request(`${prefix}/admin/sys/domain/admin`, {
    method: 'POST',
    data,
  });
}

export async function bindDomainAdmins(data) {
  return request(`${prefix}/admin/sys/domain/admins`, {
    method: 'POST',
    data,
  });
}

export async function unbindDomainAdmin(data) {
  return request(`${prefix}/admin/sys/domain/admin`, {
    method: 'DELETE',
    data,
  });
}

export async function unbindDomainAdmins(data) {
  return request(`${prefix}/admin/sys/domain/admins`, {
    method: 'DELETE',
    data,
  });
}

export async function fetchProvisionScenarioCodeDict() {
  return request(`${prefix}/dict/select/provisionScenarioCode`);
}

export async function fetchProvisionScenarios() {
  return request(`${prefix}/admin/sys/domain/provision-scenarios`);
}

export async function fetchProvisionScenarioSelect() {
  return request(`${prefix}/admin/sys/domain/provision-scenarios/select`);
}

export async function saveProvisionScenarios(data) {
  return request(`${prefix}/admin/sys/domain/provision-scenarios`, {
    method: 'PUT',
    data,
  });
}
