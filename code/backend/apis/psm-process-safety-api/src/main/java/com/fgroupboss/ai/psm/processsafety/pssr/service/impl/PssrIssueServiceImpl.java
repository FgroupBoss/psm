package com.fgroupboss.ai.psm.processsafety.pssr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.processsafety.pssr.config.PssrIssueStatus;
import com.fgroupboss.ai.psm.processsafety.pssr.config.PssrProjectStatus;
import com.fgroupboss.ai.psm.processsafety.pssr.mapper.PssrIssueMapper;
import com.fgroupboss.ai.psm.processsafety.pssr.mapper.PssrProjectMapper;
import com.fgroupboss.ai.psm.processsafety.pssr.model.dto.PssrIssueActionRequest;
import com.fgroupboss.ai.psm.processsafety.pssr.model.dto.PssrIssueRequest;
import com.fgroupboss.ai.psm.processsafety.pssr.model.entity.PssrIssueEntity;
import com.fgroupboss.ai.psm.processsafety.pssr.model.entity.PssrProjectEntity;
import com.fgroupboss.ai.psm.processsafety.pssr.model.vo.PssrIssueVO;
import com.fgroupboss.ai.psm.processsafety.pssr.service.PssrIssueService;
import com.fgroupboss.ai.psm.common.data.EntitySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * PSSR 问题登记、整改与复查。
 */
@Service
@RequiredArgsConstructor
public class PssrIssueServiceImpl implements PssrIssueService {

    private final PssrIssueMapper pssrIssueMapper;
    private final PssrProjectMapper pssrProjectMapper;

    @Override
    public PageResult<PssrIssueVO> page(Long tenantId, Long projectId, String status, int pageNo, int pageSize) {
        EntitySupport.requireTenantId(tenantId);
        LambdaQueryWrapper<PssrIssueEntity> wrapper = baseWrapper(tenantId);
        if (projectId != null) {
            wrapper.eq(PssrIssueEntity::getProjectId, projectId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(PssrIssueEntity::getStatus, status.trim());
        }
        wrapper.orderByDesc(PssrIssueEntity::getId);
        Page<PssrIssueEntity> page = pssrIssueMapper.selectPage(
                new Page<PssrIssueEntity>(EntitySupport.normalizePageNo(pageNo), EntitySupport.normalizePageSize(pageSize)),
                wrapper);
        return EntitySupport.toPageResult(page, this::toVO);
    }

    @Override
    @Transactional
    public PssrIssueVO create(PssrIssueRequest request, String operator) {
        EntitySupport.requireTenantId(request.getTenantId());
        requireProject(request.getTenantId(), request.getProjectId());

        PssrIssueEntity entity = new PssrIssueEntity();
        entity.setTenantId(request.getTenantId());
        entity.setProjectId(request.getProjectId());
        entity.setCheckItemId(request.getCheckItemId());
        entity.setIssueLevel(request.getIssueLevel().trim());
        entity.setDescription(request.getDescription().trim());
        entity.setOwnerUserId(request.getOwnerUserId());
        entity.setDueAt(request.getDueAt());
        entity.setCloseRequiredBeforeStartup(
                request.getCloseRequiredBeforeStartup() == null ? 0 : request.getCloseRequiredBeforeStartup());
        entity.setStatus(PssrIssueStatus.PENDING_RECTIFY.name());
        EntitySupport.initAuditFields(entity);
        pssrIssueMapper.insert(entity);
        markProjectRectifying(request.getTenantId(), request.getProjectId());
        return toVO(entity);
    }

    @Override
    @Transactional
    public PssrIssueVO rectify(Long id, PssrIssueActionRequest request, String operator) {
        PssrIssueEntity entity = requireIssue(request.getTenantId(), id);
        PssrIssueStatus.assertRectify(entity.getStatus());
        entity.setStatus(PssrIssueStatus.targetAfterRectify());
        EntitySupport.touchUpdated(entity);
        pssrIssueMapper.updateById(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public PssrIssueVO review(Long id, PssrIssueActionRequest request, String operator) {
        PssrIssueEntity entity = requireIssue(request.getTenantId(), id);
        PssrIssueStatus.assertReview(entity.getStatus());
        boolean passed = request.getPassed() == null || Boolean.TRUE.equals(request.getPassed());
        entity.setStatus(PssrIssueStatus.targetAfterReview(passed));
        EntitySupport.touchUpdated(entity);
        pssrIssueMapper.updateById(entity);
        return toVO(entity);
    }

    private void markProjectRectifying(Long tenantId, Long projectId) {
        PssrProjectEntity project = requireProject(tenantId, projectId);
        if (PssrProjectStatus.REVIEWING.name().equals(project.getStatus())) {
            project.setStatus(PssrProjectStatus.RECTIFYING.name());
            EntitySupport.touchUpdated(project);
            pssrProjectMapper.updateById(project);
        }
    }

    private PssrProjectEntity requireProject(Long tenantId, Long projectId) {
        EntitySupport.requireId(projectId);
        return EntitySupport.requireFound(
                pssrProjectMapper.selectOne(new LambdaQueryWrapper<PssrProjectEntity>()
                        .eq(PssrProjectEntity::getTenantId, tenantId)
                        .eq(PssrProjectEntity::getId, projectId)
                        .eq(PssrProjectEntity::getDeleted, 0)),
                "pssr project not found");
    }

    private PssrIssueEntity requireIssue(Long tenantId, Long id) {
        EntitySupport.requireId(id);
        return EntitySupport.requireFound(
                pssrIssueMapper.selectOne(baseWrapper(tenantId).eq(PssrIssueEntity::getId, id)),
                "issue not found");
    }

    private LambdaQueryWrapper<PssrIssueEntity> baseWrapper(Long tenantId) {
        return new LambdaQueryWrapper<PssrIssueEntity>()
                .eq(PssrIssueEntity::getTenantId, tenantId)
                .eq(PssrIssueEntity::getDeleted, 0);
    }

    private PssrIssueVO toVO(PssrIssueEntity entity) {
        PssrIssueVO vo = new PssrIssueVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setProjectId(entity.getProjectId());
        vo.setCheckItemId(entity.getCheckItemId());
        vo.setIssueLevel(entity.getIssueLevel());
        vo.setDescription(entity.getDescription());
        vo.setOwnerUserId(entity.getOwnerUserId());
        vo.setDueAt(entity.getDueAt());
        vo.setCloseRequiredBeforeStartup(entity.getCloseRequiredBeforeStartup());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
