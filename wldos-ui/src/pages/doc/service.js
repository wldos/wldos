import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryDocList(params) {
  return request(`${prefix}/doc`, {
    params
  });
}
export async function queryCurrentDoc(params) {
  return request(`${prefix}/doc/book/${params.bookId}.html`);
}

export async function queryCurrentChapter(params) {
  return request(`${prefix}/doc/book/${params.bookId}/chapter/${params.chapterId}.html`);
}