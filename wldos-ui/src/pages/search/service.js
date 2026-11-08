import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

/** 优先混合检索（Lucene+向量 RRF）；后端未装配 Hybrid 时自动降级 instr */
export async function search(params) {
  return request(`${prefix}/cms/pub/search-hybrid`, {
    params: { ...params, scope: params?.scope || 'public' },
  });
}
