import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/**
 * 获取当前用户的推荐信息（推荐码、推荐链接）
 * 优先调用会员营销模块 GET /marketing/referral/info：若无推荐码会自动生成并写入，再返回
 * 若该接口不可用则回退到 /user/curAccount
 */
export async function getReferralInfo() {
  try {
    const res = await request(`${prefix}/marketing/referral/info`, { method: 'GET' });
    const data = res?.data?.data != null ? res.data.data : res?.data;
    if (data && (data.referralCode || data.referralUrl)) {
      return { data: { referralCode: data.referralCode || '', referralUrl: data.referralUrl || '' } };
    }
  } catch (e) {
    // 会员营销模块未启用或接口不可用时回退
  }
  const res = await request(`${prefix}/user/curAccount`, { method: 'GET' });
  const user = res?.data?.data != null ? res.data.data : res?.data ?? res;
  const code = user?.recommendCode || user?.referralCode || '';
  const base = typeof window !== 'undefined' ? window.location.origin : '';
  return {
    data: {
      referralCode: code,
      referralUrl: code ? `${base}/product?ref=${encodeURIComponent(code)}` : '',
    },
  };
}

/** 我的佣金汇总（待结算/已结算/已扣除） */
export async function getReferralSummary() {
  return request(`${prefix}/marketing/referral/summary`, { method: 'GET' });
}
