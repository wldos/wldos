package com.wldos.platform.core.controller;

import java.util.List;

import com.wldos.framework.mvc.controller.NonEntityController;
import com.wldos.platform.core.service.DictService;
import io.github.wldos.common.vo.SelectOption;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 统一字典 API（Java 枚举 + Term 双源）。
 * 
 * @author Yuanxi Universe (306991142@qq.com)
 * @since 3.0.1
 * @version 1.0.0
 */
@Api(tags = "统一字典")
@RestController
@RequestMapping("dict")
public class DictController extends NonEntityController<DictService> {

	@ApiOperation(value = "字典下拉", notes = "按 dictCode 返回 [{label,value}]；已绑定 Term 时优先 k_terms，Term 为空时回退 Java 枚举注册")
	@GetMapping("select/{code}")
	public List<SelectOption> select(@ApiParam(value = "字典编码", required = true) @PathVariable String code) {
		return this.service.select(code);
	}
}
