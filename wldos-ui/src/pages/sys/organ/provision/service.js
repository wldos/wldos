import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function provisionTenant(params) {
  return request(`${prefix}/admin/sys/com/provision`, {
    method: 'POST',
    data: params,
  });
}

export async function searchUsers(params) {
  return request(`${prefix}/admin/sys/user/select`, {
    params,
  });
}

export async function fetchProvisionScenarioSelect() {
  return request(`${prefix}/admin/sys/domain/provision-scenarios/select`);
}

export async function fetchProvisionScenarios() {
  return request(`${prefix}/admin/sys/domain/provision-scenarios`);
}
