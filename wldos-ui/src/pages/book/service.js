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

export async function fetchWorkbenchPolicyLimits(bookId) {
  const params = bookId != null ? { bookId } : {};
  return request(`${prefix}/space/workbench/policy-limits`, { params });
}

export async function queryCurrentBook(params) {
  const { bookId, chapterCurrent, chapterPageSize, chapterSorter, chapterKeyword } = params;
  return request(`${prefix}/space/book/${bookId}`, {
    params: {
      chapterCurrent,
      chapterPageSize,
      chapterSorter,
      chapterKeyword,
    },
  });
}

export async function shiftWork(data) {
  return request(`${prefix}/space/book/shift`, {
    method: 'POST',
    data,
  });
}

export async function reorderWorks(data) {
  return request(`${prefix}/space/book/reorder`, {
    method: 'POST',
    data,
  });
}

export async function shiftChapter(bookId, data) {
  return request(`${prefix}/space/book/${bookId}/chapters/shift`, {
    method: 'POST',
    data,
  });
}

export async function reorderChapters(bookId, data) {
  return request(`${prefix}/space/book/${bookId}/reorderChapters`, {
    method: 'POST',
    data,
  });
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
