package com.fgroupboss.ai.psm.processsafety.pha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.processsafety.pha.config.PhaRecommendationStatus;
import com.fgroupboss.ai.psm.processsafety.pha.mapper.PhaRecommendationMapper;
import com.fgroupboss.ai.psm.processsafety.pha.model.dto.PhaRecommendationRequest;
import com.fgroupboss.ai.psm.processsafety.pha.model.dto.RecommendationActionRequest;
import com.fgroupboss.ai.psm.processsafety.pha.model.entity.PhaRecommendationEntity;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.PhaRecommendationVO;
import com.fgroupboss.ai.psm.processsafety.pha.service.PhaRecommendationService;
import com.fgroupboss.ai.psm.common.data.EntitySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhaRecommendationServiceImpl implements PhaRecommendationService {

    private final PhaRecommendationMapper phaRecommendationMapper;

    @Override
    public PageResult<PhaRecommendationVO> page(Long tenantId, Long projectId, String status, int pageNo, int pageSize) {
        EntitySupport.requireTenantId(tenantId);
        LambdaQueryWrapper<PhaRecommendationEntity> wrapper = baseWrapper(tenantId);
        if (projectId != null) {
            wrapper.eq(PhaRecommendationEntity::getProjectId, projectId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(PhaRecommendationEntity::getStatus, status.trim());
        }
        wrapper.orderByDesc(PhaRecommendationEntity::getId);
        Page<PhaRecommendationEntity> page = phaRecommendationMapper.selectPage(
                new Page<PhaRecommendationEntity>(EntitySupport.normalizePageNo(pageNo), EntitySupport.normalizePageSize(pageSize)),
                wrapper);
        return EntitySupport.toPageResult(page, this::toVO);
    }

    @Override
    public List<PhaRecommendationVO> list(Long tenantId, Long projectId, String status) {
        EntitySupport.requireTenantId(tenantId);
        LambdaQueryWrapper<PhaRecommendationEntity> wrapper = baseWrapper(tenantId);
        if (projectId != null) {
            wrapper.eq(PhaRecommendationEntity::getProjectId, projectId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(PhaRecommendationEntity::getStatus, status.trim());
        }
        List<PhaRecommendationVO> result = new ArrayList<PhaRecommendationVO>();
        for (PhaRecommendationEntity entity : phaRecommendationMapper.selectList(wrapper)) {
            result.add(toVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public PhaRecommendationVO create(PhaRecommendationRequest request, String operator) {
        EntitySupport.requireTenantId(request.getTenantId());
        PhaRecommendationEntity entity = new PhaRecommendationEntity();
        entity.setTenantId(request.getTenantId());
        entity.setProjectId(request.getProjectId());
        entity.setDeviationId(request.getDeviationId());
        entity.setRecNo(request.getRecNo().trim());
        entity.setDescription(request.getDescription().trim());
        entity.setOwnerUserId(request.getOwnerUserId());
        entity.setDueAt(request.getDueAt());
        entity.setStatus(PhaRecommendationStatus.PENDING_ASSIGN.name());
        EntitySupport.initAuditFields(entity);
        phaRecommendationMapper.insert(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public PhaRecommendationVO assign(Long tenantId, Long id, RecommendationActionRequest request, String operator) {
        PhaRecommendationEntity entity = requireOne(tenantId, id);
        PhaRecommendationStatus.assertAssign(entity.getStatus());
        if (request.getOwnerUserId() != null) {
            entity.setOwnerUserId(request.getOwnerUserId());
        }
        entity.setStatus(PhaRecommendationStatus.targetAfterAssign());
        EntitySupport.touchUpdated(entity);
        phaRecommendationMapper.updateById(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public PhaRecommendationVO rectify(Long tenantId, Long id, RecommendationActionRequest request, String operator) {
        PhaRecommendationEntity entity = requireOne(tenantId, id);
        PhaRecommendationStatus.assertRectify(entity.getStatus());
        entity.setStatus(PhaRecommendationStatus.targetAfterRectify());
        EntitySupport.touchUpdated(entity);
        phaRecommendationMapper.updateById(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public PhaRecommendationVO verify(Long tenantId, Long id, RecommendationActionRequest request, String operator) {
        PhaRecommendationEntity entity = requireOne(tenantId, id);
        PhaRecommendationStatus.assertVerify(entity.getStatus());
        boolean passed = request.getPassed() == null || request.getPassed();
        entity.setStatus(PhaRecommendationStatus.targetAfterVerify(passed));
        EntitySupport.touchUpdated(entity);
        phaRecommendationMapper.updateById(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public PhaRecommendationVO close(Long tenantId, Long id, RecommendationActionRequest request, String operator) {
        PhaRecommendationEntity entity = requireOne(tenantId, id);
        entity.setStatus(PhaRecommendationStatus.CLOSED.name());
        EntitySupport.touchUpdated(entity);
        phaRecommendationMapper.updateById(entity);
        return toVO(entity);
    }

    private PhaRecommendationEntity requireOne(Long tenantId, Long id) {
        EntitySupport.requireId(id);
        return EntitySupport.requireFound(
                phaRecommendationMapper.selectOne(baseWrapper(tenantId).eq(PhaRecommendationEntity::getId, id)),
                "pha recommendation not found");
    }

    private LambdaQueryWrapper<PhaRecommendationEntity> baseWrapper(Long tenantId) {
        return new LambdaQueryWrapper<PhaRecommendationEntity>()
                .eq(PhaRecommendationEntity::getTenantId, tenantId)
                .eq(PhaRecommendationEntity::getDeleted, 0);
    }

    private PhaRecommendationVO toVO(PhaRecommendationEntity entity) {
        PhaRecommendationVO vo = new PhaRecommendationVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setProjectId(entity.getProjectId());
        vo.setDeviationId(entity.getDeviationId());
        vo.setRecNo(entity.getRecNo());
        vo.setDescription(entity.getDescription());
        vo.setOwnerUserId(entity.getOwnerUserId());
        vo.setDueAt(entity.getDueAt());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
