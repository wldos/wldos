/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.auth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wldos.auth2.model.AccessTokenEntity;
import com.wldos.auth2.model.OAuthConfig;
import com.wldos.common.res.ResultJson;

/**
 * 测试json的序列化和反序列化。
 *
 * @author 树悉猿
 * @date 2022/10/29
 * @version 1.0
 */
public class Test {
	public static void main(String[] args) throws JsonProcessingException {
		String json = "{\"access_token\":\"62_Qdp8WfC5h46fSdGExYhmpZZzXQqV3xmHu9juxczJV5n7BxnuIG2Uc-HDNwnANcozRMEaK4WsMSoFFYbwuK8czAl8yB9vdBGVDRvy1Xx9LHM\",\"expires_in\":7200,\"refresh_token\":\"62_jJ2Y5YK-ROvM-g68o11QnP_BovXMquKF6gdaaTcaXRztrSdQ_VxmRSsUEAKFRIzcDqsvPttD8Jc1IRgW17jZJLcmUk_ct1CdW1Sci3NjZXI\",\"openid\":\"oTKkX6BqkQWVx2YmBUUNQYZZuJh4\",\"scope\":\"snsapi_login\",\"unionid\":\"oV8zev1tgSG2LWbfrFJLMGUsfgJw\"}";
				// "{\"appId\": \"wx9eb6a939f6d50acf\", \"appSecret\": \"31ea77230ac9f1badfa4c6fe929972ff\", \"redirectUri\": \"http://www.wldos.com/user/auth/login/wechat\", \"scope\": \"snsapi_login\", \"codeUri\": \"https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect\", \"accessTokenUri\": \"https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code\", \"refreshTokenUri\": \"\", \"userInfoUri\": \"https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s\", \"state\": \"123\"}";
		ResultJson rj = new ResultJson();
		AccessTokenEntity oAuthConfig = rj.readEntity(json, new TypeReference<AccessTokenEntity>() {});
				// new ObjectMapper().readValue(json, new TypeReference<OAuthConfig>() {});
		String toJson = rj.toJson(oAuthConfig, false);
		System.out.println(toJson);
	}
}
