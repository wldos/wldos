/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.gateway;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * rest模板相关配置。
 *
 * @author 树悉猿
 * @date 2021/5/8
 * @version 1.0
 */
@Configuration
public class RestTemplateConfig {

	@Value("${restemplate.connection.timeout}")
	private int restConnTimeout;

	@Value("${restemplate.read.timeout}")
	private int restReadTimeout;

	@Bean
	//@LoadBalanced
	public RestTemplate restTemplate(ClientHttpRequestFactory simleClientHttpRequestFactory) {// 此配置暂时没有用！
		RestTemplate restTemplate = new RestTemplate();
		//配置自定义的message转换器
		List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
		MappingJackson2HttpMessageConverter jsonConverter = new CustomMappingJackson2HttpMessageConverter();
		/* 杜绝使用
		ObjectMapper objectMapper = new ObjectMapper();

		 * 序列换成json时,将long转成string
		 * js中得数字范围小于java long取值范围
		 * /
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
		simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
		objectMapper.registerModule(simpleModule);
		jsonConverter.setObjectMapper(objectMapper); */
		messageConverters.add(jsonConverter);
		restTemplate.setMessageConverters(messageConverters);
		//配置自定义的interceptor拦截器
		/*List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		interceptors.add(new HeadClientHttpRequestInterceptor());
		interceptors.add(new TrackLogClientHttpRequestInterceptor());
		restTemplate.setInterceptors(interceptors);
		//配置自定义的异常处理
		restTemplate.setErrorHandler(new CustomResponseErrorHandler());
		restTemplate.setRequestFactory(simleClientHttpRequestFactory);*/

		return restTemplate;
	}


	@Bean
	public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
		SimpleClientHttpRequestFactory reqFactory = new SimpleClientHttpRequestFactory();
		reqFactory.setConnectTimeout(this.restConnTimeout);
		reqFactory.setReadTimeout(this.restReadTimeout);
		return reqFactory;
	}
}

/*

@Slf4j
public class TrackLogClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        trackRequest(request,body);
        ClientHttpResponse httpResponse = execution.execute(request, body);
        trackResponse(httpResponse);
        return httpResponse;
    }

    private void trackResponse(ClientHttpResponse httpResponse)throws IOException {
        log.info("============================response begin==========================================");
        log.info("Status code  : {}", httpResponse.getStatusCode());
        log.info("Status text  : {}", httpResponse.getStatusText());
        log.info("Headers      : {}", httpResponse.getHeaders());
        log.info("=======================response end=================================================");
    }

    private void trackRequest(HttpRequest request, byte[] body)throws UnsupportedEncodingException {
        log.info("======= request begin ========");
        log.info("uri : {}", request.getURI());
        log.info("method : {}", request.getMethod());
        log.info("headers : {}", request.getHeaders());
        log.info("request body : {}", new String(body, "UTF-8"));
        log.info("======= request end ========");
    }
}

@Slf4j
public class HeadClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
       log.info("#####head handle########");
        HttpHeaders headers = httpRequest.getHeaders();
        headers.add("Accept", "application/json");
        headers.add("Accept-Encoding", "gzip");
        headers.add("Content-Encoding", "UTF-8");
        headers.add("Content-Type", "application/json; charset=UTF-8");
        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
        HttpHeaders headersResponse = response.getHeaders();
        headersResponse.add("Accept", "application/json");
        return  response;
    }
}

@Slf4j
public class CustomResponseErrorHandler extends DefaultResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = response.getStatusCode();
        if(statusCode.is3xxRedirection()){
            return true;
        }
        return super.hasError(response);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = response.getStatusCode();
        if(statusCode.is3xxRedirection()){
            log.info("########30X错误，需要重定向！##########");
            return;
        }
        super.handleError(response);
    }

}
 */
