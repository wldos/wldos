import request from '@/utils/request';
import config from '@/utils/config';

const { prefix } = config;

export async function queryBookList(params) {
  const {path, ...rest} = params;
  return request(`${prefix}${path}`, {
    params: rest,
  });
}

export async function queryListSideCar(params) {
  return request(`${prefix}/cms/listSideCar/${params.pageName}`);
}

export async function fetchSeoCrumbs(params) {
  return request(`${prefix}/cms/seoCrumbs`, {params});
}
