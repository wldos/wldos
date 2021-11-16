

package com.wldos.cms.controller;

import java.util.Map;

import com.wldos.cms.enums.PostStatusEnum;
import com.wldos.cms.service.KCMSService;
import com.wldos.cms.vo.BookUnit;
import com.wldos.cms.vo.Product;
import com.wldos.support.controller.NoRepoController;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.PageQuery;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ProductController extends NoRepoController {
	private final KCMSService kcmsService;

	public ProductController(KCMSService kcmsService) {
		this.kcmsService = kcmsService;
	}

	
	@GetMapping("product-{pid:\\d+}.html")
	public Product productInfo(@PathVariable Long pid) {
		return this.kcmsService.productInfo(pid);
	}

	
	@GetMapping("product/{contentType}")
	public PageableResult<BookUnit> productArchives(@PathVariable String contentType, @RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("contentType", contentType);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		return this.kcmsService.queryProductDomain(this.getDomain(), pageQuery);
	}

	
	@GetMapping("product/{contentType}/category/{slugCategory}")
	public PageableResult<BookUnit> productCategory(@PathVariable String contentType, @PathVariable String slugCategory, @RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("contentType", contentType);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());
		return this.kcmsService.queryProductCategory(this.getDomain(), slugCategory, pageQuery);
	}

	
	@GetMapping("product/{contentType}/tag/{xxTag}")
	public String productTag(@PathVariable String contentType, @PathVariable String xxTag) {

		return this.resJson.ok("");
	}

	
	@GetMapping("product-author/{xxAuthor}")
	public String productAuthor(@PathVariable String xxAuthor) {

		return this.resJson.ok("");
	}
}
