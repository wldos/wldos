import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryCurrent() {
  return request(`${prefix}/user/currentUser`);
}

export async function queryBook(params) {
  return request(`${prefix}/space/book`, {
        params,
    });
}

export async function queryCurrentBook(params) {
  return request(`${prefix}/space/book/${params.bookId}`);
}

export async function queryCurrentChapter(params) {
    return request(`${prefix}/space/book/${params.bookId}/chapter/${params.chapterId}`);
}
export async function addBook(params) {
  return request(`${prefix}/space/book/add`, {
    method: 'POST',
    data: params,
  });
}
export async function addChapter(params) {
    return request(`${prefix}/space/book/newChapter`, {
        method: 'POST',
        data: params,
    });
}

export async function saveChapter(params) {
  return request(`${prefix}/space/book/saveChapter`, {
    method: 'POST',
    data: params,
  });
}

export async function uploadFile(params) {
  return request(`${prefix}/space/upload/chapter`, {
    method: 'POST',
    data: params,
  });
}
