/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletRequest;

import com.wldos.support.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpHelper {

	private HttpHelper() {
		throw new IllegalStateException("Utility class");
	}

	public static String getBodyString(ServletRequest request) {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try (InputStream inputStream = request.getInputStream()) {
			reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		}
		catch (IOException e) {
			log.warn("getBodyString IO异常！");
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				}
				catch (IOException e) {
					log.error(ObjectUtil.string(e.getMessage()), e);
				}
			}
		}
		return sb.toString();
	}
}
