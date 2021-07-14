/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wldos.support.controller.web.ResultJson;
import com.wldos.support.util.constant.PubConstants;
import com.wldos.support.util.ObjectUtil;
import com.wldos.system.auth.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

/**
 * @Title RestService
 * @Package com.wldos.system.gateway
 * @Description: 解析请求并转发。
 * @author 树悉猿
 * @date 2021-04-01
 * @version V1.0
 */
@Slf4j
@Service
public class RestService {

	public ResponseEntity<String> redirect(HttpServletRequest request, HttpServletResponse response, String routeUrl, String prefix, ResultJson res) {
		String redirectUrl = null;
		try {
			redirectUrl = createRedirectUrl(request, routeUrl, prefix);
			RequestEntity requestEntity = createRequestEntity(request, redirectUrl);
			ResponseEntity<String> responseEntity = route(requestEntity);
			return responseEntity;
		}
		catch (URISyntaxException | IOException | ServletException e) {
			log.error("请求响应异常, 请检查请求资源是否存在：requestUri=" + redirectUrl);
			return new ResponseEntity("网络异常，请刷新！", HttpStatus.OK);
		}
	}

	private String createRedirectUrl(HttpServletRequest request, String routeUrl, String prefix) {
		String queryString = request.getQueryString();
		return routeUrl + request.getRequestURI().replace(prefix, "") +
				(queryString != null ? "?" + queryString : "");
	}

	private RequestEntity createRequestEntity(HttpServletRequest request, String url) throws URISyntaxException, IOException, ServletException {
		String method = request.getMethod();
		HttpMethod httpMethod = HttpMethod.resolve(method);
		MultiValueMap<String, String> headers = parseRequestHeader(request);
		try {
			UserInfo user = (UserInfo) request.getAttribute(PubConstants.CONTEXT_KEY_USER);
			headers.add(PubConstants.CONTEXT_KEY_USER_ID, ObjectUtil.string(user.getId()));
			headers.add(PubConstants.CONTEXT_KEY_USER_NAME, ObjectUtil.string(user.getUsername()));
			headers.add(PubConstants.CONTEXT_KEY_USER_TENANT, ObjectUtil.string(user.getTenantId()));
		}
		catch (NullPointerException e) {
			//
		}
		HttpHeaders httpHeaders = new HttpHeaders();
		Object body = null;
		if (ServletFileUpload.isMultipartContent(request)) {
			StandardMultipartHttpServletRequest httpServletRequest = (StandardMultipartHttpServletRequest) request;
			Iterator<String> fileNames = httpServletRequest.getFileNames();
			Enumeration<String> params = httpServletRequest.getParameterNames();
			MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();

			while (fileNames.hasNext()) {
				String fileName = fileNames.next(); // 文件参数名
				MultipartFile file = httpServletRequest.getFile(fileName);

				byte[] binaryFile = null;
				try {
					binaryFile = file.getBytes();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				ByteArrayResource fileAsResource = new ByteArrayResource(binaryFile) {
					@Override
					public String getFilename() {
						return file.getOriginalFilename();
					}

					@Override
					public long contentLength() {
						return file.getSize();
					}
				};
				multipartRequest.add(fileName, fileAsResource);
			}

			while (params.hasMoreElements()) {
				String paramName = params.nextElement();
				String paramValue = httpServletRequest.getParameter(paramName);
				log.info("param="+paramName + ", " + paramValue);
				multipartRequest.add(paramName, paramValue);
			}

			body = multipartRequest;
		}
		else {
			body = parseRequestBody(request);
		}

		httpHeaders.addAll(headers);
		return new RequestEntity<>(body, httpHeaders, httpMethod, new URI(url));
	}

	private ResponseEntity<String> route(RequestEntity requestEntity) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.exchange(requestEntity, String.class);
	}

	private byte[] parseRequestBody(HttpServletRequest request) throws IOException {

		InputStream inputStream = request.getInputStream();
		return StreamUtils.copyToByteArray(inputStream);
	}

	private MultiValueMap<String, String> parseRequestHeader(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		List<String> headerNames = Collections.list(request.getHeaderNames());
		for (String headerName : headerNames) {
			List<String> headerValues = Collections.list(request.getHeaders(headerName));
			for (String headerValue : headerValues) {
				headers.add(headerName, headerValue);
			}
		}
		return headers;
	}
}
