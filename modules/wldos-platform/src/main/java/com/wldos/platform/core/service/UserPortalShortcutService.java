/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 */
package com.wldos.platform.core.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.core.dao.UserPortalShortcutDao;
import com.wldos.platform.core.entity.WoUserPortalShortcut;
import com.wldos.platform.core.vo.MyShortcutCreateBody;
import com.wldos.platform.core.vo.MyShortcutTouchBody;
import com.wldos.platform.core.vo.MyShortcutUpdateBody;
import com.wldos.platform.core.vo.PortalShortcutVo;
import io.github.wldos.platform.support.resource.vo.Menu;

import io.github.wldos.common.res.Result;
import io.github.wldos.common.res.ResultCode;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 门户「我的常用」：按用户、租户公司、访问域隔离；保存前按 GET 方式做路由可访问性校验。
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserPortalShortcutService extends EntityService<UserPortalShortcutDao, WoUserPortalShortcut, Long> {

	private static final int MAX_SHORTCUTS_PER_SCOPE = 50;

	private final UserService userService;

	public UserPortalShortcutService(UserService userService) {
		this.userService = userService;
	}

	@Transactional(readOnly = true)
	public List<PortalShortcutVo> listVo(Long userId, Long comId, Long domainId) {
		if (userId == null || comId == null || domainId == null) {
			return new ArrayList<>(0);
		}
		List<WoUserPortalShortcut> rows = this.entityRepo.listByScope(userId, comId, domainId);
		List<PortalShortcutVo> out = new ArrayList<>(rows.size());
		for (WoUserPortalShortcut r : rows) {
			out.add(toVo(r));
		}
		return out;
	}

	public Result<PortalShortcutVo> addMine(MyShortcutCreateBody body, Long userId, Long comId, Long domainId,
			HttpServletRequest request) {
		String path;
		try {
			path = normalizePortalPath(body.getPath());
		}
		catch (IllegalArgumentException e) {
			return Result.error(ResultCode.BAD_REQUEST, e.getMessage());
		}
		if (!isRouteAllowed(path, userId, domainId, comId, request)) {
			return Result.error(ResultCode.FORBIDDEN, "无权收藏该路径");
		}
		TouchResolved resolved = resolveTouchTitleAndResource(path, userId, comId, domainId,
				body.getTitle() == null ? "" : body.getTitle().trim(), body.getResourceId());
		if (resolved.title.isEmpty()) {
			return Result.error(ResultCode.BAD_REQUEST, "标题不能为空");
		}
		if (this.entityRepo.countActive(userId, comId, domainId) >= MAX_SHORTCUTS_PER_SCOPE) {
			return Result.error(ResultCode.BAD_REQUEST, "常用入口数量已达上限（" + MAX_SHORTCUTS_PER_SCOPE + "）");
		}
		WoUserPortalShortcut e = new WoUserPortalShortcut();
		e.setUserId(userId);
		e.setComId(comId);
		e.setDomainId(domainId);
		e.setTitle(resolved.title);
		e.setPath(path);
		e.setResourceId(resolved.resourceId);
		e.setIcon(body.getIcon());
		e.setHitCount(0);
		e.setLastHitAt(null);
		e.setPinned("0");
		e.setDisplayOrder(this.entityRepo.maxDisplayOrder(userId, comId, domainId) + 1);
		try {
			this.saveOrUpdate(e);
		}
		catch (DataIntegrityViolationException ex) {
			return Result.error(ResultCode.CONFLICT, "该路径已存在于我的常用中");
		}
		return Result.ok(toVo(e));
	}

	/**
	 * 记录门户菜单访问：同一路径累加次数；不存在则插入（超出上限时淘汰一条未置顶且最冷门记录）。
	 */
	public Result<PortalShortcutVo> touchMine(MyShortcutTouchBody body, Long userId, Long comId, Long domainId,
			HttpServletRequest request) {
		if (userId == null || comId == null || domainId == null) {
			return Result.error(ResultCode.UNAUTHORIZED, "未登录或租户/域无效");
		}
		String path;
		try {
			path = normalizePortalPath(body.getPath());
		}
		catch (IllegalArgumentException e) {
			return Result.error(ResultCode.BAD_REQUEST, e.getMessage());
		}
		if (!isRouteAllowed(path, userId, domainId, comId, request)) {
			return Result.error(ResultCode.FORBIDDEN, "无权访问该路径");
		}
		TouchResolved resolved = resolveTouchTitleAndResource(path, userId, comId, domainId,
				body.getTitle() == null ? "" : body.getTitle().trim(), body.getResourceId());
		if (resolved.title.isEmpty()) {
			return Result.error(ResultCode.BAD_REQUEST, "标题不能为空");
		}
		Optional<WoUserPortalShortcut> existing = this.entityRepo.findByPath(userId, comId, domainId, path);
		if (existing.isPresent()) {
			WoUserPortalShortcut row = existing.get();
			int hc = row.getHitCount() == null ? 0 : row.getHitCount();
			row.setHitCount(hc + 1);
			row.setLastHitAt(LocalDateTime.now());
			row.setTitle(resolved.title);
			row.setResourceId(resolved.resourceId);
			if (body.getIcon() != null) {
				row.setIcon(body.getIcon());
			}
			this.saveOrUpdate(row);
			return Result.ok(toVo(row));
		}
		while (this.entityRepo.countActive(userId, comId, domainId) >= MAX_SHORTCUTS_PER_SCOPE) {
			Long evictId = this.entityRepo.findEvictUnpinnedId(userId, comId, domainId);
			if (evictId == null) {
				return Result.error(ResultCode.BAD_REQUEST, "记录已达上限，请先取消置顶或删除部分历史记录");
			}
			this.deleteById(evictId);
		}
		WoUserPortalShortcut e = new WoUserPortalShortcut();
		e.setUserId(userId);
		e.setComId(comId);
		e.setDomainId(domainId);
		e.setTitle(resolved.title);
		e.setPath(path);
		e.setResourceId(resolved.resourceId);
		e.setIcon(body.getIcon());
		e.setHitCount(1);
		e.setLastHitAt(LocalDateTime.now());
		e.setPinned("0");
		e.setDisplayOrder(this.entityRepo.maxDisplayOrder(userId, comId, domainId) + 1);
		try {
			this.saveOrUpdate(e);
		}
		catch (DataIntegrityViolationException ex) {
			return Result.error(ResultCode.CONFLICT, "该路径已存在");
		}
		return Result.ok(toVo(e));
	}

	public Result<PortalShortcutVo> updateMine(long id, MyShortcutUpdateBody body, Long userId, Long comId, Long domainId,
			HttpServletRequest request) {
		WoUserPortalShortcut row = this.findById(id);
		if (row == null || !isMine(row, userId, comId, domainId)) {
			return Result.error(ResultCode.NOT_FOUND, "记录不存在");
		}
		boolean touched = false;
		boolean pathUpdated = false;
		if (body.getTitle() != null) {
			row.setTitle(body.getTitle() == null ? "" : body.getTitle().trim());
			touched = true;
		}
		if (body.getPath() != null) {
			String path;
			try {
				path = normalizePortalPath(body.getPath());
			}
			catch (IllegalArgumentException e) {
				return Result.error(ResultCode.BAD_REQUEST, e.getMessage());
			}
			if (!isRouteAllowed(path, userId, domainId, comId, request)) {
				return Result.error(ResultCode.FORBIDDEN, "无权收藏该路径");
			}
			row.setPath(path);
			touched = true;
			pathUpdated = true;
			String hintTitle = body.getTitle() != null ? body.getTitle().trim()
					: (row.getTitle() == null ? "" : row.getTitle().trim());
			Long hintRes = body.getResourceId() != null ? body.getResourceId() : row.getResourceId();
			TouchResolved pathResolved = resolveTouchTitleAndResource(path, userId, comId, domainId, hintTitle, hintRes);
			if (pathResolved.title.isEmpty()) {
				return Result.error(ResultCode.BAD_REQUEST, "标题不能为空");
			}
			row.setTitle(pathResolved.title);
			row.setResourceId(pathResolved.resourceId);
		}
		if (body.getResourceId() != null && !pathUpdated) {
			row.setResourceId(body.getResourceId());
			touched = true;
		}
		if (body.getIcon() != null) {
			row.setIcon(body.getIcon());
			touched = true;
		}
		if (body.getDisplayOrder() != null) {
			row.setDisplayOrder(body.getDisplayOrder());
			touched = true;
		}
		if (body.getPinned() != null) {
			boolean on = Boolean.TRUE.equals(body.getPinned());
			row.setPinned(on ? "1" : "0");
			if (on) {
				Integer minOrd = this.entityRepo.minPinnedDisplayOrder(userId, comId, domainId);
				int base = minOrd == null ? 0 : minOrd;
				row.setDisplayOrder(base - 1);
			}
			touched = true;
		}
		if (!touched) {
			return Result.error(ResultCode.BAD_REQUEST, "无更新字段");
		}
		try {
			this.saveOrUpdate(row);
		}
		catch (DataIntegrityViolationException ex) {
			return Result.error(ResultCode.CONFLICT, "该路径已存在于我的常用中");
		}
		return Result.ok(toVo(row));
	}

	public Result<Void> deleteMine(long id, Long userId, Long comId, Long domainId) {
		WoUserPortalShortcut row = this.findById(id);
		if (row == null || !isMine(row, userId, comId, domainId)) {
			return Result.error(ResultCode.NOT_FOUND, "记录不存在");
		}
		this.deleteById(id);
		return Result.ok();
	}

	private static boolean isMine(WoUserPortalShortcut row, Long userId, Long comId, Long domainId) {
		return row != null && Objects.equals(row.getUserId(), userId) && Objects.equals(row.getComId(), comId)
				&& Objects.equals(row.getDomainId(), domainId);
	}

	private static PortalShortcutVo toVo(WoUserPortalShortcut r) {
		PortalShortcutVo v = new PortalShortcutVo();
		v.setId(r.getId());
		v.setTitle(r.getTitle());
		v.setPath(r.getPath());
		v.setIcon(r.getIcon());
		v.setResourceId(r.getResourceId());
		v.setDisplayOrder(r.getDisplayOrder());
		v.setHitCount(r.getHitCount());
		if (r.getLastHitAt() != null) {
			v.setLastHitAtMs(r.getLastHitAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		}
		v.setPinned("1".equals(r.getPinned()));
		return v;
	}

	/**
	 * 仅允许站内相对路径，禁止外链与非法 scheme。
	 */
	static String normalizePortalPath(String raw) {
		if (raw == null) {
			throw new IllegalArgumentException("path 不能为空");
		}
		String p = raw.trim();
		if (p.isEmpty()) {
			throw new IllegalArgumentException("path 不能为空");
		}
		String lower = p.toLowerCase();
		if (lower.startsWith("javascript:") || lower.startsWith("data:") || lower.startsWith("vbscript:")) {
			throw new IllegalArgumentException("非法路径");
		}
		if (lower.contains("://") || p.startsWith("//")) {
			throw new IllegalArgumentException("仅支持站内相对路径");
		}
		if (!p.startsWith("/")) {
			p = "/" + p;
		}
		return p;
	}

	private boolean isRouteAllowed(String path, Long userId, Long domainId, Long tenantId, HttpServletRequest request) {
		HttpServletRequest probe = new HttpServletRequestWrapper(request) {
			@Override
			public String getMethod() {
				return "GET";
			}
		};
		return "200".equals(this.userService.checkRoute(path, userId, domainId, tenantId, probe));
	}

	private static final class TouchResolved {
		final String title;
		final Long resourceId;

		TouchResolved(String title, Long resourceId) {
			this.title = title == null ? "" : title;
			this.resourceId = resourceId;
		}
	}

	private static final class AdminMenuMatch {
		final Menu menu;
		final int pathLen;
		final boolean exact;

		AdminMenuMatch(Menu menu, int pathLen, boolean exact) {
			this.menu = menu;
			this.pathLen = pathLen;
			this.exact = exact;
		}
	}

	private static final Set<String> GENERIC_ADMIN_MENU_TITLES;

	static {
		Set<String> s = new HashSet<>();
		s.add("系统");
		s.add("系统管理");
		s.add("管理");
		s.add("菜单");
		s.add("导航");
		GENERIC_ADMIN_MENU_TITLES = Collections.unmodifiableSet(s);
	}

	/**
	 * /admin 下按当前用户管理菜单最长前缀匹配解析标题与资源 id；其它路径沿用客户端传入值。
	 */
	private TouchResolved resolveTouchTitleAndResource(String path, Long userId, Long comId, Long domainId,
			String clientTitle, Long clientResourceId) {
		String title = clientTitle == null ? "" : clientTitle.trim();
		Long resourceId = clientResourceId;
		if (path.startsWith("/admin")) {
			Menu menu = resolveAdminMenuForPath(path, domainId, comId, userId);
			if (menu != null) {
				String menuName = menu.getName() == null ? "" : menu.getName().trim();
				String resolvedTitle;
				if (!menuName.isEmpty() && !GENERIC_ADMIN_MENU_TITLES.contains(menuName)) {
					resolvedTitle = truncateShortcutTitle(menuName);
				}
				else {
					resolvedTitle = truncateShortcutTitle(fallbackTitleFromPath(path));
				}
				return new TouchResolved(resolvedTitle, menu.getId());
			}
			if (title.isEmpty()) {
				title = fallbackTitleFromPath(path);
			}
			return new TouchResolved(truncateShortcutTitle(title), resourceId);
		}
		return new TouchResolved(truncateShortcutTitle(title), resourceId);
	}

	private Menu resolveAdminMenuForPath(String normPath, Long domainId, Long comId, Long userId) {
		List<Menu> roots = this.userService.queryAdminMenuByUser(domainId, comId, userId);
		if (roots == null || roots.isEmpty()) {
			return null;
		}
		List<AdminMenuMatch> candidates = new ArrayList<>();
		collectAdminMenuPathMatches(roots, normPath, candidates);
		if (candidates.isEmpty()) {
			return null;
		}
		candidates.sort((a, b) -> {
			if (a.exact != b.exact) {
				return a.exact ? -1 : 1;
			}
			return Integer.compare(b.pathLen, a.pathLen);
		});
		return candidates.get(0).menu;
	}

	private static void collectAdminMenuPathMatches(List<Menu> nodes, String normPath, List<AdminMenuMatch> out) {
		if (nodes == null) {
			return;
		}
		for (Menu n : nodes) {
			if (n.getPath() != null) {
				String p = normalizeMenuPathForMatch(n.getPath());
				if (!p.isEmpty() && !"/".equals(p)) {
					if (normPath.equals(p)) {
						out.add(new AdminMenuMatch(n, p.length(), true));
					}
					else if (normPath.startsWith(p + "/")) {
						out.add(new AdminMenuMatch(n, p.length(), false));
					}
				}
			}
			collectAdminMenuPathMatches(n.getChildren(), normPath, out);
		}
	}

	private static String normalizeMenuPathForMatch(String raw) {
		if (raw == null) {
			return "";
		}
		String p = raw.trim();
		if (p.isEmpty()) {
			return "";
		}
		if (!p.startsWith("/")) {
			p = "/" + p;
		}
		while (p.length() > 1 && p.endsWith("/")) {
			p = p.substring(0, p.length() - 1);
		}
		return p;
	}

	private static String truncateShortcutTitle(String t) {
		if (t == null || t.isEmpty()) {
			return "";
		}
		return t.length() > 128 ? t.substring(0, 128) : t;
	}

	private static String fallbackTitleFromPath(String normPath) {
		if (normPath == null || normPath.isEmpty()) {
			return "页面";
		}
		String p = normPath;
		while (p.length() > 1 && p.endsWith("/")) {
			p = p.substring(0, p.length() - 1);
		}
		int slash = p.lastIndexOf('/');
		String last = slash >= 0 ? p.substring(slash + 1) : p;
		if (last.isEmpty()) {
			return "页面";
		}
		return last.length() > 128 ? last.substring(0, 128) : last;
	}
}
