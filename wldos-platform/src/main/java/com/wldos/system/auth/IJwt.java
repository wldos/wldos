/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.auth;

import java.util.Date;

public interface IJwt {
	
	Long getUserId();
	
	Long getTenantId();
	
	String getId();
	
	boolean getIsExpired();
	
	Date getStartDate();
	
	Date getExpireDate();

	String KEY_JWT_TENANT = "tenantId";
}
