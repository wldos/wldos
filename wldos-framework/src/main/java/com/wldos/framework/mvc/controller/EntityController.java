/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.mvc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wldos.framework.mvc.service.EntityService;
import io.github.wldos.common.Constants;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.framework.support.audit.annotation.OpLog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 标准实体基础controller，用于收口业务系统请求、响应边界。
 * 非实体controller基础上增加CRUD方法。
 *
 * @param <S> 实体service
 * @param <E> 拥有数据库表的实体
 * @author 元悉宇宙
 * @date 2021-04-16
 * @version 1.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class EntityController<S extends EntityService, E> extends NonEntityController<S> {

	/**
	 * 标准实体新增。
	 *
	 * <p><b>审计</b>：自动记入 {@code wo_op_log}，由切面 {@code OpLogAspect} 拦截：
	 * <ul>
	 *   <li>{@code module} 缺省 → 取类上 {@code @Api.tags}（项目惯例必填，无需重复定义）；</li>
	 *   <li>{@code action} 显式 = "新增"；</li>
	 *   <li>{@code resourceId} 取 {@code #entity.id}（saveOrUpdate 内部分配雪花 ID 后回填到入参对象上，
	 *       因此切面 {@code @Around} 在业务方法返回后能拿到 id；解析失败回填 null 不影响业务）；</li>
	 *   <li>{@code recordParams} = true：默认脱敏字段（password/passwd/token/secret/captcha 等）已覆盖最常见敏感项；
	 *       业务实体若有额外敏感字段，请在子类覆写并补 {@code sensitiveFields}。</li>
	 * </ul>
	 *
	 * <p><b>覆写注意</b>：子类如显式覆写本方法，**必须**重新声明 {@code @OpLog}；
	 * Java 方法注解默认不被覆写所继承（参见 {@code 02-OpLog-API覆盖清单.md § 1.3}）。
	 */
	@PostMapping("add")
	@OpLog(action = "新增", resourceId = "#entity.id")
	public String addEntity(@RequestBody E entity) {
		this.preAdd(entity);
		service.saveOrUpdate(entity);  // 统一使用 saveOrUpdate() 方法，自动判断 insert/update
		this.postAdd(entity);
		return resJson.ok(Boolean.TRUE);
	}

	/**
	 * 新增实体时，对实体二次处理的hook
	 *
	 * @param entity 实体bean的实例
	 */
	protected void preAdd(E entity) {
	}

	/**
	 * 新增完成后，触发的操作，比如刷新缓存
	 */
	protected void postAdd(E entity) {
	}

	/**
	 * 标准实体更新。审计语义与 {@link #addEntity(Object)} 一致；详见类上方说明与 {@code @OpLog}。
	 *
	 * <p>子类如覆写本方法（如解决 Ambiguous mapping），<b>必须</b>重新贴 {@code @OpLog} 注解。
	 */
	@PostMapping("update")
	@OpLog(action = "更新", resourceId = "#entity.id")
	public String updateEntity(@RequestBody E entity) {
		this.preUpdate(entity);
		service.saveOrUpdate(entity);  // 统一使用 saveOrUpdate() 方法，自动判断 insert/update
		this.postUpdate(entity);
		return resJson.ok(Boolean.TRUE);
	}

	/**
	 * 更新实体时，对实体二次处理的hook
	 *
	 * @param entity 实体bean的实例
	 */
	protected void preUpdate(E entity) {
	}

	/**
	 * 更新完成后，触发的操作，比如刷新缓存
	 */
	protected void postUpdate(E entity) {
	}

	/**
	 * 标准实体单条删除。审计语义同上。
	 *
	 * <p>{@code recordParams} = false：删除一般不需要在日志里记录被删实体的全部字段值
	 * （如需保留删除前快照，业务侧应在 {@link #preDelete(Object)} hook 里另外记业务追溯日志）。
	 */
	@DeleteMapping("delete")
	@OpLog(action = "删除", resourceId = "#entity.id", recordParams = false)
	public String removeEntity(@RequestBody E entity) {
		this.preDelete(entity);
		this.service.delete(entity);
		this.postDelete(entity);
		return resJson.ok(Boolean.TRUE);
	}

	/**
	 * 删除实体时，对实体二次处理的hook
	 *
	 * @param entity 实体bean的实例
	 */
	protected void preDelete(E entity) {
	}

	/**
	 * 删除完成后，触发的操作，比如刷新缓存
	 */
	protected void postDelete(E entity) {
	}

	/**
	 * 批量物理删
	 * @param jsonObject 批量参数 Map<String, Object> key="ids"
	 *
	 * <p>审计：{@code resourceId} 取 {@code #jsonObject['ids']}，落表的会是 {@code "[123, 456, ...]"}
	 * 字符串形式（{@code Object#toString}），便于跨条 group by 分析批量操作影响面。
	 */
	@DeleteMapping("deletes")
	@OpLog(action = "批量删除", resourceId = "#jsonObject['ids']")
	public String removeEntitiesByIds(@RequestBody Map<String, Object> jsonObject) {
		Object ids = jsonObject.get("ids");
		if (ids != null) {
			List<Object> objects = (List<Object>) ids;
			this.preDeletes(objects);
			service.deletePhysicalByIds(objects.toArray());

			this.postDeletes(objects);
		}

		return resJson.ok(Boolean.TRUE);
	}

	/**
	 * 批量删除实体时，对实体二次处理的hook
	 *
	 * @param ids 实例ids
	 */
	protected void preDeletes(List<Object> ids) {
	}

	/**
	 * 批量删除完成后，触发的操作，比如刷新缓存
	 */
	protected void postDeletes(List<Object> ids) {
	}

	@GetMapping("get")
	public Object getEntityById(@RequestParam long id) {
		return service.findById(id);
	}

	@GetMapping("all")
	public List<E> allEntities() {
		if (this.isMultiTenancy() && !this.service.isAdmin(this.getUserId())) {
			Map<String, Object> cond = new HashMap<>();
			Class<E> clazz = this.service.getEntityClass(1);
			cond.put(clazz.getSimpleName().equals(Constants.CLASS_NAME_COMPANY) ? "id" : Constants.COMMON_KEY_TENANT_COLUMN, this.getTenantId());
			return this.doFilter(this.service.findAllWithCond(clazz, cond));
		}
		else
			return this.doFilter(service.findAll());
	}

	/**
	 * 结果集过滤器
	 *
	 * @param res 结果集
	 * @return 过滤后的结果集
	 */
	protected List<E> doFilter(List<E> res) {
		return res;
	}

	/**
	 * 不带参数的分页查询，标准实体。带参数的查询，请参照ResourceController。
	 *
	 * @param params 请求参数
	 * @return 分页数据
	 */
	@Deprecated
	@GetMapping("page")
	public Page<E> entityList(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		Pageable page = PageRequest.of(pageQuery.getCurrent() - 1, pageQuery.getPageSize(), pageQuery.getSorter());

		return service.findAll(page);
	}
}