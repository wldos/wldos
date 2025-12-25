import request from '@/utils/request';
import config from '@/utils/config';
// 统一转调现有模块化 service，避免破坏以往实现
import * as comService from '@/pages/sys/com/service';
import * as archService from '@/pages/sys/arch/service';
import * as orgService from '@/pages/sys/org/service';
import * as userService from '@/pages/sys/user/service';

const { prefix } = config;

// 暴露与原页面一致的更新/新增/删除函数，直接转调原有 service
export const updateCompany = comService.updateEntity;
export const addSystem = archService.addEntity;
export const deleteCompany = comService.removeEntity;
export const updateSystem = archService.updateEntity;
export const addOrganization = orgService.addEntity;
export const deleteSystem = archService.removeEntity;
export const updateOrganization = orgService.updateEntity;
export const deleteOrganization = orgService.removeEntity;

// 获取组织架构数据（获取完整的组织架构树）
export async function fetchOrganizationData() {
  try {
    // 获取公司列表
    const companies = await queryCompanies();

    // 确保companies是数组
    if (!Array.isArray(companies)) {
      console.warn('公司数据不是数组格式:', companies);
      return [];
    }

    // 为每个公司加载其体系数据
    const treeData = await Promise.all(companies.map(async (company) => {
      try {
        // 获取该公司的体系数据
        const systems = await loadSystemsByCompany(company.id);
        return {
          ...company,
          type: 'company',
          children: systems || []
        };
      } catch (error) {
        console.error(`加载公司 ${company.comName} 的体系数据失败:`, error);
        return {
          ...company,
          type: 'company',
          children: []
        };
      }
    }));

    return treeData;
  } catch (error) {
    console.error('获取组织架构数据失败:', error);
    throw error;
  }
}

// 按组织机构获取用户数据
export async function fetchUsersByOrganization(organizationId) {
  console.log('调用fetchUsersByOrganization，组织ID:', organizationId);
  const response = await userService.queryOrgPage({ orgId: organizationId });
  console.log('用户服务响应:', response);
  return response;
}

// 按需加载公司的体系数据
export async function loadSystemsByCompany(companyId) {
  try {
    const systems = await querySystemsByCompany(companyId);

    // 确保systems是数组
    if (!Array.isArray(systems)) {
      console.warn('体系数据不是数组格式:', systems);
      return [];
    }

    // 构建体系节点，不加载子级数据
    return systems.map(system => ({
      ...system,
      type: 'system',
      children: [] // 子级数据按需加载
    }));
  } catch (error) {
    console.error('获取体系数据失败:', error);
    return [];
  }
}

// 按需加载体系的机构数据
export async function loadOrganizationsBySystem(systemId) {
  try {
    const organizations = await queryOrganizationsBySystem(systemId);

    // 确保organizations是数组
    if (!Array.isArray(organizations)) {
      console.warn('机构数据不是数组格式:', organizations);
      return [];
    }

    // 构建机构节点，不加载用户数据
    return organizations.map(org => ({
      ...org,
      type: 'organization',
      userCount: 0, // 初始用户数量为0
      children: [] // 用户数据按需加载
    }));
  } catch (error) {
    console.error('获取机构数据失败:', error);
    return [];
  }
}

// 获取公司列表（使用现有API）
export async function queryCompanies() {
  try {
    const response = await comService.queryPage({ pageSize: 1000 });
    console.log('公司API响应:', response);
    return response.data?.rows || [];
  } catch (error) {
    console.error('获取公司列表失败:', error);
    return [];
  }
}

// 按ID获取公司详情（用于更新成功后的回查）
export async function getCompanyById(id) {
  try {
    const res = await comService.queryPage({ id, pageSize: 1 });
    const rows = res?.data?.rows || res?.data || [];
    return rows && rows[0] ? rows[0] : null;
  } catch (e) {
    console.error('按ID获取公司详情失败:', e);
    return null;
  }
}

// 根据公司ID获取体系列表
export async function querySystemsByCompany(companyId) {
  try {
    const response = await archService.queryPage({
      pageSize: 1000,
      comId: companyId
    });
    console.log('体系API响应:', response);
    return response.data?.rows || [];
  } catch (error) {
    console.error('获取体系列表失败:', error);
    return [];
  }
}

// 根据体系ID获取机构列表
export async function queryOrganizationsBySystem(systemId) {
  try {
    const response = await orgService.queryPage({
      pageSize: 1000,
      archId: systemId
    });
    console.log('机构API响应:', response);
    return response.data?.rows || [];
  } catch (error) {
    console.error('获取机构列表失败:', error);
    return [];
  }
}

// 按组织统计用户数量
export async function queryUserCountsByOrganization() {
  try {
    const response = await userService.queryPage({ pageSize: 1000 });
    const users = response.data?.rows || [];

    // 确保users是数组
    if (!Array.isArray(users)) {
      console.warn('用户数据不是数组格式:', users);
      return {};
    }

    // 按组织统计用户数量
    const userCounts = {};
    users.forEach(user => {
      if (user.organizationId) {
        userCounts[user.organizationId] = (userCounts[user.organizationId] || 0) + 1;
      }
    });

    return userCounts;
  } catch (error) {
    console.error('获取用户数量统计失败:', error);
    return {};
  }
}

// 兼容旧接口：为机构添加用户
export async function addUserToOrganization(params) {
  return request(`${prefix}/admin/sys/org/user`, {
    method: 'POST',
    data: params,
  });
}

// 兼容旧接口：更新机构权限
export async function updateOrganizationPermissions(params) {
  return request(`${prefix}/admin/sys/org/permissions`, {
    method: 'POST',
    data: params,
  });
}

// 组织架构聚合数据（用于刷新左侧树）
export async function getOrganizationArchitecture() {
  return request(`${prefix}/admin/sys/organization/architecture`);
}

