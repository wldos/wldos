import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryLicense() {
  return request(`${prefix}/admin/sys/license`);
}
export async function queryIssueVersion() {
  return request(`${prefix}/admin/sys/license/versionEnum`);
}

/** 上传 License 文件（FormData，key 为 file） */
export async function uploadLicense(formData) {
  return request(`${prefix}/admin/sys/license/upload`, {
    method: 'POST',
    data: formData,
  });
}

/** 从 HTTPS URL 拉取公钥库并刷新（用户实例仅拉取，不在此上传公钥库） */
export async function pullPublicKeystore(url) {
  return request(`${prefix}/admin/sys/license/pull-public-keystore`, {
    method: 'POST',
    data: { url },
  });
}

/** 上传 OEM 公钥库（FormData，key 为 file） */
export async function uploadOemPublicKeystore(formData) {
  return request(`${prefix}/admin/sys/license/upload-oem-public-keystore`, {
    method: 'POST',
    data: formData,
  });
}

/** 从 HTTPS URL 拉取 OEM 公钥库 */
export async function pullOemPublicKeystore(url) {
  return request(`${prefix}/admin/sys/license/pull-oem-public-keystore`, {
    method: 'POST',
    data: { url },
  });
}

/** 从 HTTPS URL 拉取 license 并生效 */
export async function pullLicense(url) {
  return request(`${prefix}/admin/sys/license/pull-license`, {
    method: 'POST',
    data: { url },
  });
}

/** 获取本机服务器信息（硬件/环境，供申请展示与提交） */
export async function getServerInfo() {
  return request(`${prefix}/admin/sys/license/server-info`);
}

/**
 * 提交授权申请（商业闭环：须先购买许可授权，填写订单号 + 本机信息 + 商业信息）
 * 请求商业模块 POST /admin/license/application
 */
export async function submitLicenseApplication(payload) {
  return request(`${prefix}/admin/license/application`, {
    method: 'POST',
    data: payload,
  });
}

/**
 * 查询申请状态（商业模块）
 */
export async function getApplicationStatus(applicationId) {
  return request(`${prefix}/admin/license/application/${applicationId}`);
}

/**
 * 审批通过并上传许可证（商业模块，管理员；校验订单已支付）
 */
export async function approveApplication(applicationId, formData) {
  return request(`${prefix}/admin/license/application/${applicationId}/approve`, {
    method: 'POST',
    data: formData,
  });
}

/**
 * 申请状态为成功时，安装已审批的许可证到本机（运行端 agent，从商业模块拉取内容）
 */
export async function installLicenseFromApplication(applicationId) {
  return request(`${prefix}/admin/sys/license/application/${applicationId}/install-license`, {
    method: 'POST',
  });
}
