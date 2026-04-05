import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 获取本机服务器信息（供申请展示与提交） */
export async function getServerInfo() {
  return request(`${prefix}/admin/sys/license/server-info`);
}

/** 获取 machine-request 公钥（兼容旧版；当前 machine-request 封装优先复用实例侧 license 验签公钥库） */
export async function getMachineRequestPubkey({ issuer = 'oem', tenantId, endpoint } = {}) {
  const base = endpoint || `${prefix}/oem/license/crypto/machine-request-pubkey`;
  return request(base, {
    method: 'GET',
    params: { issuer, tenantId },
  });
}

/** 提交授权申请（商业闭环：须先购买许可授权） */
export async function submitLicenseApplication(payload) {
  return request(`${prefix}/admin/license/application`, {
    method: 'POST',
    data: payload,
  });
}

/** 查询申请状态 */
export async function getApplicationStatus(applicationId) {
  return request(`${prefix}/admin/license/application/${applicationId}`);
}

/** 审批通过并上传许可证（管理员） */
export async function approveApplication(applicationId, formData) {
  return request(`${prefix}/admin/license/application/${applicationId}/approve`, {
    method: 'POST',
    data: formData,
  });
}

/** 申请通过后安装许可证到本机 */
export async function installLicenseFromApplication(applicationId) {
  return request(`${prefix}/admin/sys/license/application/${applicationId}/install-license`, {
    method: 'POST',
  });
}

/** 上传 License 到本实例（用户侧：覆盖本地主许可证文件） */
export async function uploadLicense(formData) {
  return request(`${prefix}/admin/sys/license/upload`, {
    method: 'POST',
    data: formData,
  });
}

/** 拉取公钥库到本实例 */
export async function pullPublicKeystore(url) {
  return request(`${prefix}/admin/sys/license/pull-public-keystore`, {
    method: 'POST',
    params: { url },
  });
}

/** 上传 OEM 公钥库 */
export async function uploadOemPublicKeystore(formData) {
  return request(`${prefix}/admin/sys/license/upload-oem-public-keystore`, {
    method: 'POST',
    data: formData,
  });
}

/** 拉取 OEM 公钥库到本实例 */
export async function pullOemPublicKeystore(url) {
  return request(`${prefix}/admin/sys/license/pull-oem-public-keystore`, {
    method: 'POST',
    params: { url },
  });
}

/** 拉取 License 到本实例 */
export async function pullLicense(url) {
  return request(`${prefix}/admin/sys/license/pull-license`, {
    method: 'POST',
    params: { url },
  });
}
