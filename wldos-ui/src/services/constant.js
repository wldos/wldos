import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function fetchOssUrl() {
  return request(`${prefix}/constant/ossUrl`);
}
export async function fetchSearchWords(params) {
  return [
    {
      label: "年谱", value: '年谱',
    },
    {
      label: "文章", value: '文章',
    },
    {
      label: "技术", value: '技术',
    },
    {
      label: "信息", value: '信息',
    },
  ];
}