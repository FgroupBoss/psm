package com.fgroupboss.ai.psm.risk.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.risk.inspection.mapper.InspectionPlanMapper;
import com.fgroupboss.ai.psm.risk.inspection.mapper.InspectionRouteMapper;
import com.fgroupboss.ai.psm.risk.inspection.model.entity.InspectionRouteEntity;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.InspectionPlanRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.entity.InspectionPlanEntity;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.InspectionPlanVO;
import com.fgroupboss.ai.psm.risk.inspection.service.InspectionPlanService;
import com.fgroupboss.ai.psm.risk.inspection.support.InspectionSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 巡检计划业务实现。
 */
@Service
@RequiredArgsConstructor
public class InspectionPlanServiceImpl implements InspectionPlanService {

    private final InspectionPlanMapper planMapper;
    private final InspectionRouteMapper routeMapper;

    @Override
    public PageResult<InspectionPlanVO> page(Long tenantId, String keyword, int pageNo, int pageSize) {
        InspectionSupport.requireTenantId(tenantId);
        InspectionSupport.Page page = InspectionSupport.normalizePage(pageNo, pageSize);
        String normalizedKeyword = InspectionSupport.normalizeText(keyword);
        long total = planMapper.countByTenant(tenantId, normalizedKeyword);
        List<InspectionPlanEntity> entities = total == 0
                ? new ArrayList<InspectionPlanEntity>()
                : planMapper.listByTenant(tenantId, normalizedKeyword, page.offset, page.pageSize);
        List<InspectionPlanVO> records = new ArrayList<InspectionPlanVO>();
        for (InspectionPlanEntity entity : entities) {
            records.add(toVO(entity));
        }
        return new PageResult<InspectionPlanVO>(total, page.pageNo, page.pageSize, records);
    }

    @Override
    public InspectionPlanVO getById(Long tenantId, Long id) {
        return toVO(requirePlan(tenantId, id));
    }

    @Override
    @Transactional
    public InspectionPlanVO create(InspectionPlanRequest request) {
        InspectionSupport.requireTenantId(request.getTenantId());
        requireRoute(request.getTenantId(), request.getRouteId());
        assertCodeUnique(request.getTenantId(), request.getPlanCode(), null);
        InspectionPlanEntity entity = new InspectionPlanEntity();
        entity.setTenantId(request.getTenantId());
        entity.setPlanCode(request.getPlanCode().trim());
        entity.setPlanName(request.getPlanName().trim());
        entity.setRouteId(request.getRouteId());
        entity.setCycleType(request.getCycleType().trim());
        entity.setCronExpr(request.getCronExpr());
        entity.setTeamId(request.getTeamId());
        entity.setTeamName(request.getTeamName());
        entity.setDefaultExecutorId(request.getDefaultExecutorId());
        entity.setMajorHazardId(request.getMajorHazardId());
        entity.setEnabled(Boolean.FALSE.equals(request.getEnabled()) ? 0 : 1);
        entity.setDeleted(0);
        planMapper.insert(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public InspectionPlanVO update(Long id, InspectionPlanRequest request) {
        InspectionPlanEntity entity = requirePlan(request.getTenantId(), id);
        requireRoute(request.getTenantId(), request.getRouteId());
        assertCodeUnique(request.getTenantId(), request.getPlanCode(), id);
        entity.setPlanCode(request.getPlanCode().trim());
        entity.setPlanName(request.getPlanName().trim());
        entity.setRouteId(request.getRouteId());
        entity.setCycleType(request.getCycleType().trim());
        entity.setCronExpr(request.getCronExpr());
        entity.setTeamId(request.getTeamId());
        entity.setTeamName(request.getTeamName());
        entity.setDefaultExecutorId(request.getDefaultExecutorId());
        if (request.getMajorHazardId() != null) {
            entity.setMajorHazardId(request.getMajorHazardId());
        }
        if (request.getEnabled() != null) {
            entity.setEnabled(Boolean.FALSE.equals(request.getEnabled()) ? 0 : 1);
        }
        planMapper.updateById(entity);
        return toVO(entity);
    }

    @Override
    public List<InspectionPlanVO> listByMajorHazard(Long tenantId, Long majorHazardId) {
        InspectionSupport.requireTenantId(tenantId);
        if (majorHazardId == null || majorHazardId <= 0) {
            throw new BusinessException(400, "majorHazardId is required");
        }
        LambdaQueryWrapper<InspectionPlanEntity> wrapper = new LambdaQueryWrapper<InspectionPlanEntity>();
        wrapper.eq(InspectionPlanEntity::getTenantId, tenantId)
                .eq(InspectionPlanEntity::getMajorHazardId, majorHazardId)
                .eq(InspectionPlanEntity::getDeleted, 0)
                .orderByDesc(InspectionPlanEntity::getId);
        List<InspectionPlanEntity> entities = planMapper.selectList(wrapper);
        List<InspectionPlanVO> records = new ArrayList<InspectionPlanVO>();
        for (InspectionPlanEntity entity : entities) {
            records.add(toVO(entity));
        }
        return records;
    }

    private void assertCodeUnique(Long tenantId, String planCode, Long excludeId) {
        InspectionPlanEntity existing = planMapper.findByCode(tenantId, planCode.trim());
        if (existing != null && (excludeId == null || !excludeId.equals(existing.getId()))) {
            throw new BusinessException(400, "plan code already exists: " + planCode);
        }
    }

    private void requireRoute(Long tenantId, Long routeId) {
        InspectionRouteEntity route = routeMapper.selectById(routeId);
        if (route == null || (route.getDeleted() != null && route.getDeleted() == 1)
                || !tenantId.equals(route.getTenantId())) {
            throw new BusinessException(404, "inspection route not found");
        }
    }

    private InspectionPlanEntity requirePlan(Long tenantId, Long id) {
        InspectionSupport.requireTenantId(tenantId);
        InspectionPlanEntity entity = planMapper.selectById(id);
        if (entity == null || (entity.getDeleted() != null && entity.getDeleted() == 1)
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "inspection plan not found");
        }
        return entity;
    }

    private InspectionPlanVO toVO(InspectionPlanEntity entity) {
        InspectionPlanVO vo = new InspectionPlanVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setPlanCode(entity.getPlanCode());
        vo.setPlanName(entity.getPlanName());
        vo.setRouteId(entity.getRouteId());
        vo.setCycleType(entity.getCycleType());
        vo.setCronExpr(entity.getCronExpr());
        vo.setTeamId(entity.getTeamId());
        vo.setTeamName(entity.getTeamName());
        vo.setDefaultExecutorId(entity.getDefaultExecutorId());
        vo.setMajorHazardId(entity.getMajorHazardId());
        vo.setEnabled(entity.getEnabled());
        return vo;
    }
}
