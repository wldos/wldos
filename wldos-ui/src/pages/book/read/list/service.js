import request from "@/utils/request";
import config from "@/utils/config";

const { prefix } = config;

export async function test(params) {
  const {path, ...rest} = params;
  return request(`${prefix}${path}`, {
    method: 'POST',
    data: rest,
  });
}