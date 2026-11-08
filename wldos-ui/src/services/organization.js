// 统一转调现有模块化 service，避免破坏以往实现
import * as comService from '@/pages/sys/com/service';
import * as archService from '@/pages/sys/arch/service';
import * as orgService from '@/pages/sys/org/service';
import * as userService from '@/pages/sys/user/service';

// 暴露与原页面一致的更新/新增/删除函数，直接转调原有 service
export const updateCompany = comService.updateEntity;
export const addCompany = comService.addEntity;
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
  return userService.queryOrgPage({ orgId: organizationId });
}

// 公司/体系/机构管理接口均按自身父子关系返回嵌套树（父节点.children = 子节点），
// 组织架构树把它们各自单独当作一个层级（公司-体系-机构-用户）逐级按需加载，
// 这里先拍平为一维列表，否则下面重新挂载下一层 children 时会整体覆盖、丢失原始子节点（如子公司/子体系/子部门）。
function flattenTree(nodes) {
  const flat = [];
  (nodes || []).forEach((node) => {
    const { children, ...rest } = node;
    flat.push(rest);
    if (Array.isArray(children) && children.length) {
      flat.push(...flattenTree(children));
    }
  });
  return flat;
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
    return flattenTree(systems).map(system => ({
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

    // 构建机构节点，按 parentId 组装为本体系下的子树
    return buildOrgTreeUnderSystem(organizations);
  } catch (error) {
    console.error('获取机构数据失败:', error);
    return [];
  }
}

// 获取公司列表（使用现有API）
export async function queryCompanies() {
  try {
    const response = await comService.queryPage({ pageSize: 1000 });
    return flattenTree(response.data?.rows || []);
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
    return response.data?.rows || [];
  } catch (error) {
    console.error('获取体系列表失败:', error);
    return [];
  }
}

// 根据体系ID获取机构列表（走 /all 拍平再筛 archId，避免树形分页 query 漏掉 parent 挂在平台根下的机构）
export async function queryOrganizationsBySystem(systemId) {
  try {
    const response = await orgService.getOrgList();
    const all = Array.isArray(response?.data) ? response.data : [];
    return all.filter((org) => String(org.archId) === String(systemId));
  } catch (error) {
    console.error('获取机构列表失败:', error);
    return [];
  }
}

function buildOrgTreeUnderSystem(flatOrgs) {
  if (!flatOrgs?.length) {
    return [];
  }
  const idSet = new Set(flatOrgs.map((org) => String(org.id)));
  const nodeMap = new Map();
  flatOrgs.forEach((org) => {
    nodeMap.set(String(org.id), {
      ...org,
      type: 'organization',
      userCount: 0,
      children: [],
    });
  });
  const roots = [];
  nodeMap.forEach((node) => {
    const parentId = node.parentId;
    if (parentId != null && String(parentId) !== '0' && idSet.has(String(parentId))) {
      nodeMap.get(String(parentId)).children.push(node);
    } else {
      roots.push(node);
    }
  });
  return roots;
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
