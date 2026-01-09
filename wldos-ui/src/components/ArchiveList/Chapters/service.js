/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

import request from "@/utils/request";
import config from "@/utils/config";

const { prefix } = config;

export async function starObject(params) {
  const {path, ...rest} = params;
  return request(`${prefix}${path}`, {
    method: 'POST',
    data: rest,
  });
}
