/*

 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.

 * Created by Yuanxi Universe (306991142@qq.com)

 * Licensed under the Apache License, Version 2.0 or a commercial license.

 * For Apache License Version 2.0 see License in the project root for license information.

 * For commercial licenses see term.md or contact 306991142@qq.com

 */


package com.wldos.cms.service;


import com.wldos.cms.dao.PubDao;
import com.wldos.cms.entity.KPubs;
import com.wldos.cms.vo.DomainCloneOptions;
import com.wldos.cms.vo.DomainCloneResult;
import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.platform.core.dao.DomainResourceDao;
import com.wldos.platform.core.dao.DomainOptionDao;
import com.wldos.platform.core.dao.ResourceDao;
import com.wldos.platform.core.dao.TermObjectDao;
import com.wldos.platform.core.entity.KTermObject;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoDomainResource;
import com.wldos.platform.core.entity.WoDomainOption;
import com.wldos.platform.core.enums.PubTypeEnum;
import com.wldos.platform.core.enums.SysOptionEnum;
import com.wldos.platform.core.service.DomainService;
import com.wldos.platform.core.service.TermService;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.platform.support.cms.entity.KPubmeta;
import io.github.wldos.platform.support.resource.entity.WoResource;
import io.github.wldos.platform.support.resource.enums.ResourceEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 子站复制（P1）：域资源关联 + CMS page/doc；维护 wo_domain_content_map。
 *
 * <p>
 * <p>
 * {@code wo_resource} 为全平台共享资源池，复制时<strong>仅新增 {@code wo_domain_resource} 关联</strong>，
 * <p>
 * 不克隆资源本体。CMS 内容（{@code k_pubs}）按域隔离，复制为新正式行。
 *
 * </p>
 */

@Service

public class DomainCloneService extends NonEntityService {

    private static final String MAP_TYPE_CLONE = "clone";

    private static final Set<String> PORTAL_MENU_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            ResourceEnum.MENU.getValue(),
            ResourceEnum.PLUGIN_MENU.getValue())));

    private static final Set<String> SITE_CONFIG_OPTION_KEYS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            SysOptionEnum.PORTAL_QR_URL.getKey(),
            SysOptionEnum.PORTAL_FOOTER_I18N.getKey(),
            SysOptionEnum.ADMIN_HOME_CONFIG.getKey())));

    @Autowired
    private DomainService domainService;

    @Autowired
    private DomainResourceDao domainResourceDao;

    @Autowired
    private DomainOptionDao domainOptionDao;

    @Autowired
    private ResourceDao resourceDao;

    @Autowired
    private PubDao pubDao;

    @Autowired
    private PubmetaService pubmetaService;

    @Autowired
    private TermObjectDao termObjectDao;

    @Autowired
    private TermService termService;

    @Transactional(rollbackFor = Exception.class)
    public DomainCloneResult cloneSite(Long sourceDomainId, Long targetDomainId, DomainCloneOptions options) {
        if (sourceDomainId == null || targetDomainId == null) {
            throw new BaseException("sourceDomainId 与 targetDomainId 不能为空", ResultCode.VALIDATION_ERROR.getCode());
        }

        if (sourceDomainId.equals(targetDomainId)) {
            throw new BaseException("源域与目标域不能相同", ResultCode.VALIDATION_ERROR.getCode());
        }

        WoDomain source = domainService.findById(sourceDomainId);
        WoDomain target = domainService.findById(targetDomainId);

        if (source == null || target == null) {
            throw new BaseException("源域或目标域不存在", ResultCode.DATA_NOT_FOUND.getCode());
        }

        DomainCloneOptions opts = options != null ? options : new DomainCloneOptions();
        DomainCloneResult result = new DomainCloneResult();

        result.setSourceDomainId(sourceDomainId);
        result.setTargetDomainId(targetDomainId);

        if (opts.isCopyMenu() || opts.isCopyDomainResource()) {
            int[] bindingCounts = cloneDomainResourceBindings(sourceDomainId, targetDomainId, opts);
            result.setMenuResourceCount(bindingCounts[0]);
            result.setDomainResourceCount(bindingCounts[1]);
        }

        if (opts.isCopyPages()) {
            result.setPageCount(clonePages(sourceDomainId, targetDomainId, opts, result));
        }

        if (opts.isCopyDocs()) {
            int[] docStats = cloneDocs(sourceDomainId, targetDomainId, opts, result);

            result.setDocCount(docStats[0]);

            result.setDocChapterCount(docStats[1]);
        }

        if (opts.isCopyDomainOptions()) {
            result.setDomainOptionCount(cloneDomainOptions(sourceDomainId, targetDomainId));
        }

        if (opts.isCopySiteConfig()) {
            copySiteConfigColumns(source, target);
        }

        return result;
    }

    /** 复制 wo_domain 站点物理配置列（页脚/QR/i18n/管理端首页）。 */
    private void copySiteConfigColumns(WoDomain source, WoDomain target) {
        target.setFoot(source.getFoot());
        target.setFlink(source.getFlink());
        target.setCopy(source.getCopy());
        target.setPortalQrUrl(source.getPortalQrUrl());
        target.setPortalFooterI18n(source.getPortalFooterI18n());
        target.setAdminHomeConfig(source.getAdminHomeConfig());
        domainService.saveOrUpdate(target);
        domainService.refreshDomain(target);
    }

    /** 复制域级系统选项；跳过已迁至 wo_domain 的站点配置键。 */
    private int cloneDomainOptions(Long sourceDomainId, Long targetDomainId) {
        List<WoDomainOption> rows = domainOptionDao.findAllByDomainId(sourceDomainId);
        if (rows == null || rows.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (WoDomainOption src : rows) {
            if (src == null || src.getOptionKey() == null) {
                continue;
            }
            if (SITE_CONFIG_OPTION_KEYS.contains(src.getOptionKey())) {
                continue;
            }
            WoDomainOption existing = domainOptionDao.findByDomainIdAndOptionKey(targetDomainId, src.getOptionKey());
            if (existing != null) {
                continue;
            }
            WoDomainOption copy = new WoDomainOption();
            BeanUtils.copyProperties(src, copy);
            copy.setId(nextId());
            copy.setDomainId(targetDomainId);
            domainOptionDao.saveOrUpdate(copy);
            count++;
        }
        return count;
    }


    /**
     * 复制域资源关联：共享同一 {@code wo_resource.id}，在目标域新建 {@code wo_domain_resource}。
     *
     * @return [menuBindingCount, otherBindingCount]
     */
    private int[] cloneDomainResourceBindings(Long sourceDomainId, Long targetDomainId, DomainCloneOptions opts) {

        List<WoDomainResource> bindings = domainResourceDao.queryAllByDomainIdAndIsValidAndDeleteFlag(
                sourceDomainId, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());

        if (bindings == null || bindings.isEmpty()) {

            return new int[]{0, 0};

        }

        Map<Long, String> resourceTypes = loadResourceTypes(bindings);
        Long operatorId = getUserId();
        String operatorIp = getUserIp();
        int menuCount = 0;
        int otherCount = 0;

        for (WoDomainResource src : bindings) {
            if (!shouldCopyDomainBinding(src, resourceTypes, opts)) {
                continue;
            }

            if (src.getResourceId() != null && src.getAppId() != null
                    && domainResourceDao.existsByResourceIdAndAppIdAndDomainId(
                    src.getResourceId(), src.getAppId(), targetDomainId)) {
                continue;
            }

            WoDomainResource copy = new WoDomainResource();
            BeanUtils.copyProperties(src, copy);
            copy.setId(nextId());
            copy.setDomainId(targetDomainId);
            copy.setCreateBy(operatorId);
            copy.setUpdateBy(operatorId);
            copy.setCreateIp(operatorIp);
            copy.setUpdateIp(operatorIp);
            copy.setVersions(null);
            domainResourceDao.saveOrUpdate(copy);

            if (isPortalMenuBinding(src.getResourceId(), resourceTypes)) {

                menuCount++;

            } else {

                otherCount++;

            }
        }

        return new int[]{menuCount, otherCount};
    }


    private boolean shouldCopyDomainBinding(WoDomainResource binding, Map<Long, String> resourceTypes,
                                            DomainCloneOptions opts) {
        if (opts.isCopyDomainResource()) {

            return true;

        }

        return opts.isCopyMenu() && isPortalMenuBinding(binding.getResourceId(), resourceTypes);
    }


    private boolean isPortalMenuBinding(Long resourceId, Map<Long, String> resourceTypes) {

        if (resourceId == null) {

            return false;

        }

        String type = resourceTypes.get(resourceId);
        return type != null && PORTAL_MENU_TYPES.contains(type);
    }


    private Map<Long, String> loadResourceTypes(List<WoDomainResource> bindings) {
        Set<Long> resourceIds = bindings.stream()
                .map(WoDomainResource::getResourceId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (resourceIds.isEmpty()) {

            return Collections.emptyMap();

        }

        Map<Long, String> types = new HashMap<>();

        for (WoResource resource : resourceDao.findAllById(resourceIds)) {

            if (resource != null && resource.getId() != null) {

                types.put(resource.getId(), resource.getResourceType());

            }

        }

        return types;
    }

    private int clonePages(Long sourceDomainId, Long targetDomainId, DomainCloneOptions opts, DomainCloneResult result) {
        String sql = "SELECT * FROM k_pubs WHERE delete_flag = ? AND pub_type = ? AND pub_status = 'publish' "
                + "AND domain_id = ? AND origin_pub_id IS NULL ORDER BY id ASC";

        List<KPubs> pages = commonOperate.getJdbcOperations().query(
                sql,
                new BeanPropertyRowMapper<>(KPubs.class),
                DeleteFlagEnum.NORMAL.toString(),
                PubTypeEnum.PAGE.getName(),
                sourceDomainId);

        if (pages == null || pages.isEmpty()) {

            return 0;

        }

        int count = 0;

        for (KPubs src : pages) {

            if (src.getOriginPubId() != null) {

                continue;

            }

            Long newId = copyPubWithExtras(src, targetDomainId, null, opts, false, result);

            recordContentMap(sourceDomainId, src.getId(), targetDomainId, newId, result);

            count++;

        }

        return count;
    }


    /**
     * @return [docCount, chapterCount]
     */
    private int[] cloneDocs(Long sourceDomainId, Long targetDomainId, DomainCloneOptions opts, DomainCloneResult result) {

        String docSql = "SELECT * FROM k_pubs WHERE delete_flag = ? AND pub_type = ? AND pub_status = 'publish' "
                + "AND domain_id = ? AND origin_pub_id IS NULL ORDER BY id ASC";

        List<KPubs> docs = commonOperate.getJdbcOperations().query(
                docSql,
                new BeanPropertyRowMapper<>(KPubs.class),
                DeleteFlagEnum.NORMAL.toString(),
                PubTypeEnum.DOC.getName(),
                sourceDomainId);

        if (docs == null || docs.isEmpty()) {

            return new int[]{0, 0};

        }

        String chapterSql = "SELECT * FROM k_pubs WHERE delete_flag = ? AND pub_type = ? "
                + "AND domain_id = ? AND parent_id = ? AND origin_pub_id IS NULL "
                + "AND pub_status IN ('publish', 'inherit') ORDER BY id ASC";

        int docCount = 0;

        int chapterCount = 0;

        for (KPubs doc : docs) {

            Long newDocId = copyPubWithExtras(doc, targetDomainId, null, opts, true, result);

            recordContentMap(sourceDomainId, doc.getId(), targetDomainId, newDocId, result);

            docCount++;

            List<KPubs> chapters = commonOperate.getJdbcOperations().query(

                    chapterSql,

                    new BeanPropertyRowMapper<>(KPubs.class),

                    DeleteFlagEnum.NORMAL.toString(),

                    PubTypeEnum.CHAPTER.getName(),

                    sourceDomainId,

                    doc.getId());

            if (chapters == null || chapters.isEmpty()) {
                continue;
            }

            for (KPubs chapter : chapters) {

                Long newChapterId = copyPubWithExtras(chapter, targetDomainId, newDocId, opts, true, result);

                recordContentMap(sourceDomainId, chapter.getId(), targetDomainId, newChapterId, result);

                chapterCount++;

            }

        }

        return new int[]{docCount, chapterCount};
    }


    private Long copyPubWithExtras(KPubs src, Long targetDomainId, Long remappedParentId, DomainCloneOptions opts,
                                   boolean copyMeta, DomainCloneResult result) {

        Long newId = copyPub(src, targetDomainId, remappedParentId);

        if (copyMeta) {

            copyPubMeta(src.getId(), newId);

        }

        if (opts.isCopyTermBindings()) {

            copyTermObjects(src.getId(), newId, result);

        }

        return newId;
    }


    private void copyTermObjects(Long sourcePubId, Long targetPubId, DomainCloneResult result) {

        List<KTermObject> bindings = termObjectDao.findAllByObjectId(sourcePubId);

        if (bindings == null || bindings.isEmpty()) {

            return;

        }

        List<Long> termTypeIds = bindings.stream()
                .map(KTermObject::getTermTypeId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (termTypeIds.isEmpty()) {

            return;

        }

        termService.saveTermObject(termTypeIds, targetPubId);

        result.setTermBindingCount(result.getTermBindingCount() + termTypeIds.size());
    }

    private Long copyPub(KPubs src, Long targetDomainId, Long remappedParentId) {

        KPubs copy = new KPubs();
        BeanUtils.copyProperties(src, copy);
        Long newId = nextId();
        copy.setId(newId);
        copy.setDomainId(targetDomainId);

        if (remappedParentId != null) {

            copy.setParentId(remappedParentId);

        }

        copy.setViews(0);
        copy.setCommentCount(0);
        copy.setStarCount(0);
        copy.setLikeCount(0);
        copy.setLikeCount(0);
        copy.setCreateBy(getUserId());
        copy.setUpdateBy(getUserId());
        copy.setCreateIp(getUserIp());
        copy.setUpdateIp(getUserIp());
        copy.setVersions(null);
        pubDao.saveOrUpdate(copy);

        return newId;
    }

    private void copyPubMeta(Long sourcePubId, Long targetPubId) {

        List<KPubmeta> metas = pubmetaService.queryPubMetaByPubId(sourcePubId);

        if (metas == null || metas.isEmpty()) {

            return;

        }

        for (KPubmeta meta : metas) {

            KPubmeta copy = new KPubmeta();
            copy.setId(nextId());
            copy.setPubId(targetPubId);
            copy.setMetaKey(meta.getMetaKey());
            copy.setMetaValue(meta.getMetaValue());
            pubmetaService.saveOrUpdate(copy);
        }
    }

    private void recordContentMap(Long sourceDomainId, Long sourcePubId, Long targetDomainId, Long targetPubId,
                                  DomainCloneResult result) {

        insertContentMap(sourceDomainId, sourcePubId, targetDomainId, targetPubId);

        result.setContentMapCount(result.getContentMapCount() + 1);

    }


    private void insertContentMap(Long sourceDomainId, Long sourcePubId, Long targetDomainId, Long targetPubId) {

        try {

            long id = nextId();

            String sql = "INSERT INTO wo_domain_content_map (id, source_domain_id, source_pub_id, target_domain_id, target_pub_id, map_type) "
                    + "VALUES (?, ?, ?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE target_pub_id = VALUES(target_pub_id), map_type = VALUES(map_type)";

            commonOperate.getJdbcOperations().update(sql, id, sourceDomainId, sourcePubId, targetDomainId, targetPubId, MAP_TYPE_CLONE);

        } catch (Exception e) {
            // 映射表未迁移时仍允许复制 page
        }
    }
}
