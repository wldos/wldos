

package com.wldos.cms.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.cms.enums.TermTypeEnum;
import com.wldos.cms.vo.TermTree;
import com.wldos.support.controller.NoRepoController;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.PageQuery;
import com.wldos.support.Constants;
import com.wldos.cms.entity.KTerms;
import com.wldos.cms.service.TermService;
import com.wldos.cms.vo.Term;
import com.wldos.support.util.TreeUtil;
import com.wldos.support.vo.ViewNode;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TermController extends NoRepoController {
	private final TermService service;

	public TermController(TermService service) {
		this.service = service;
	}


	@GetMapping("/admin/cms/category")
	public PageableResult<TermTree> listCategory(@RequestParam Map<String, Object> params) {
		params.put("classType", TermTypeEnum.CATEGORY.toString());

		PageQuery pageQuery = new PageQuery(params);
		return this.service.queryTermForTree(new KTerms(), pageQuery);
	}


	@GetMapping("/admin/cms/tag")
	public PageableResult<TermTree> listTag(@RequestParam Map<String, Object> params) {
		params.put("classType", TermTypeEnum.TAG.toString());

		PageQuery pageQuery = new PageQuery(params);
		return this.service.queryTermForTree(new KTerms(), pageQuery);
	}


	@GetMapping("cms/category/{contCode}")
	public List<ViewNode> categoryTree(@PathVariable String contCode) {
		List<Term> terms = this.service.findCategoryByContCode(contCode);

		List<ViewNode> viewNodes = terms.parallelStream().map(res -> {
			ViewNode authRes = new ViewNode();
			authRes.setId(res.getId());
			authRes.setParentId(res.getParentId());
			authRes.setTitle(res.getName());
			authRes.setKey(res.getId().toString());

			return authRes;
		}).collect(Collectors.toList());

		return TreeUtil.build(viewNodes, Constants.TOP_TERM_ID);
	}


	@GetMapping("cms/tag/{contCode}")
	public List<Term> tagList(@PathVariable String contCode) {

		return this.service.findTagByContCode(contCode);
	}


	@GetMapping("info/category/tree")
	public List<ViewNode> categoryAllTree() {
		List<Term> terms = this.service.findAllCategory();

		List<ViewNode> viewNodes = terms.parallelStream().map(res -> {
			ViewNode authRes = new ViewNode();
			authRes.setId(res.getId());
			authRes.setParentId(res.getParentId());
			authRes.setTitle(res.getName());
			authRes.setKey(res.getTermTypeId().toString());

			return authRes;
		}).collect(Collectors.toList());

		return TreeUtil.build(viewNodes, Constants.TOP_TERM_ID);
	}


	@GetMapping("cms/category/all")
	public List<Term> categoryAllList() {

		return this.service.findAllCategory();
	}


	@GetMapping("cms/tag/all")
	public List<Term>  tagAllList() {

		return this.service.findAllTag();
	}

	@PostMapping("/admin/cms/category/add")
	public String addCategory(@RequestBody Term term) {
		term.setClassType(TermTypeEnum.CATEGORY.toString());
		this.service.addTerm(term);
		this.service.refreshTerm();
		return this.resJson.ok("ok");
	}

	@PostMapping("/admin/cms/category/update")
	public String updateCategory(@RequestBody Term term) {
		this.service.updateTerm(term);
		this.service.refreshTerm();
		return this.resJson.ok("ok");
	}

	@DeleteMapping("/admin/cms/category/delete")
	public String deleteCategory(@RequestBody Term term) {
		this.service.deleteTerm(term);
		this.service.refreshTerm();
		return this.resJson.ok("ok");
	}

	@SuppressWarnings("unchecked")
	@DeleteMapping("/admin/cms/category/deletes")
	public Boolean removeIds(@RequestBody Map jsonObject) {
		Object ids = jsonObject.get("ids");
		if (ids != null) {
			service.deleteByIds(((List<Object>) ids).toArray());
		}
		this.service.refreshTerm();
		return Boolean.TRUE;
	}

	@PostMapping("/admin/cms/tag/add")
	public String addTag(@RequestBody Term term) {
		term.setClassType(TermTypeEnum.TAG.toString());
		this.service.addTerm(term);
		this.service.refreshTerm();
		return this.resJson.ok("ok");
	}
}
