package com.fgroupboss.ai.psm.inspection.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.inspection.mapper.InspectionRouteMapper;
import com.fgroupboss.ai.psm.inspection.mapper.RoutePointMapper;
import com.fgroupboss.ai.psm.inspection.model.dto.InspectionRouteRequest;
import com.fgroupboss.ai.psm.inspection.model.dto.RoutePointRequest;
import com.fgroupboss.ai.psm.inspection.model.entity.InspectionRouteEntity;
import com.fgroupboss.ai.psm.inspection.model.entity.RoutePointEntity;
import com.fgroupboss.ai.psm.inspection.model.vo.InspectionRouteVO;
import com.fgroupboss.ai.psm.inspection.model.vo.RoutePointVO;
import com.fgroupboss.ai.psm.inspection.service.InspectionRouteService;
import com.fgroupboss.ai.psm.inspection.support.InspectionSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 巡检路线业务实现。
 */
@Service
@RequiredArgsConstructor
public class InspectionRouteServiceImpl implements InspectionRouteService {

    private final InspectionRouteMapper routeMapper;
    private final RoutePointMapper pointMapper;

    @Override
    public PageResult<InspectionRouteVO> page(Long tenantId, String keyword, int pageNo, int pageSize) {
        InspectionSupport.requireTenantId(tenantId);
        InspectionSupport.Page page = InspectionSupport.normalizePage(pageNo, pageSize);
        String normalizedKeyword = InspectionSupport.normalizeText(keyword);
        long total = routeMapper.countByTenant(tenantId, normalizedKeyword);
        List<InspectionRouteEntity> entities = total == 0
                ? new ArrayList<InspectionRouteEntity>()
                : routeMapper.listByTenant(tenantId, normalizedKeyword, page.offset, page.pageSize);
        List<InspectionRouteVO> records = new ArrayList<InspectionRouteVO>();
        for (InspectionRouteEntity entity : entities) {
            records.add(toVO(entity, false));
        }
        return new PageResult<InspectionRouteVO>(total, page.pageNo, page.pageSize, records);
    }

    @Override
    public InspectionRouteVO getById(Long tenantId, Long id) {
        return toVO(requireRoute(tenantId, id), true);
    }

    @Override
    @Transactional
    public InspectionRouteVO create(InspectionRouteRequest request) {
        InspectionSupport.requireTenantId(request.getTenantId());
        assertCodeUnique(request.getTenantId(), request.getRouteCode(), null);
        InspectionRouteEntity entity = new InspectionRouteEntity();
        entity.setTenantId(request.getTenantId());
        entity.setRouteCode(request.getRouteCode().trim());
        entity.setRouteName(request.getRouteName().trim());
        entity.setAreaId(request.getAreaId());
        entity.setEstimatedMinutes(request.getEstimatedMinutes());
        entity.setStatus("ENABLED");
        entity.setRemark(request.getRemark());
        entity.setDeleted(0);
        routeMapper.insert(entity);
        savePoints(request.getTenantId(), entity.getId(), request.getPoints());
        return toVO(entity, true);
    }

    @Override
    @Transactional
    public InspectionRouteVO update(Long id, InspectionRouteRequest request) {
        InspectionRouteEntity entity = requireRoute(request.getTenantId(), id);
        assertCodeUnique(request.getTenantId(), request.getRouteCode(), id);
        entity.setRouteCode(request.getRouteCode().trim());
        entity.setRouteName(request.getRouteName().trim());
        entity.setAreaId(request.getAreaId());
        entity.setEstimatedMinutes(request.getEstimatedMinutes());
        entity.setRemark(request.getRemark());
        routeMapper.updateById(entity);
        if (!CollectionUtils.isEmpty(request.getPoints())) {
            pointMapper.softDeleteByRoute(request.getTenantId(), entity.getId());
            savePoints(request.getTenantId(), entity.getId(), request.getPoints());
        }
        return toVO(entity, true);
    }

    private void savePoints(Long tenantId, Long routeId, List<RoutePointRequest> points) {
        if (CollectionUtils.isEmpty(points)) {
            return;
        }
        int order = 0;
        for (RoutePointRequest pointRequest : points) {
            RoutePointEntity point = new RoutePointEntity();
            point.setTenantId(tenantId);
            point.setRouteId(routeId);
            point.setPointCode(pointRequest.getPointCode().trim());
            point.setPointName(pointRequest.getPointName().trim());
            point.setSignType(pointRequest.getSignType().trim());
            point.setSignCode(pointRequest.getSignCode());
            point.setAreaId(pointRequest.getAreaId());
            point.setChecklistTemplateId(pointRequest.getChecklistTemplateId());
            point.setSortOrder(pointRequest.getSortOrder() != null ? pointRequest.getSortOrder() : order++);
            point.setDeleted(0);
            pointMapper.insert(point);
        }
    }

    private void assertCodeUnique(Long tenantId, String routeCode, Long excludeId) {
        InspectionRouteEntity existing = routeMapper.findByCode(tenantId, routeCode.trim());
        if (existing != null && (excludeId == null || !excludeId.equals(existing.getId()))) {
            throw new BusinessException(400, "route code already exists: " + routeCode);
        }
    }

    InspectionRouteEntity requireRoute(Long tenantId, Long id) {
        InspectionSupport.requireTenantId(tenantId);
        InspectionRouteEntity entity = routeMapper.selectById(id);
        if (entity == null || (entity.getDeleted() != null && entity.getDeleted() == 1)
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "inspection route not found");
        }
        return entity;
    }

    private InspectionRouteVO toVO(InspectionRouteEntity entity, boolean withPoints) {
        InspectionRouteVO vo = new InspectionRouteVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setRouteCode(entity.getRouteCode());
        vo.setRouteName(entity.getRouteName());
        vo.setAreaId(entity.getAreaId());
        vo.setEstimatedMinutes(entity.getEstimatedMinutes());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        if (withPoints) {
            List<RoutePointVO> points = new ArrayList<RoutePointVO>();
            for (RoutePointEntity point : pointMapper.listByRoute(entity.getTenantId(), entity.getId())) {
                points.add(toPointVO(point));
            }
            vo.setPoints(points);
        }
        return vo;
    }

    private RoutePointVO toPointVO(RoutePointEntity entity) {
        RoutePointVO vo = new RoutePointVO();
        vo.setId(entity.getId());
        vo.setRouteId(entity.getRouteId());
        vo.setPointCode(entity.getPointCode());
        vo.setPointName(entity.getPointName());
        vo.setSignType(entity.getSignType());
        vo.setSignCode(entity.getSignCode());
        vo.setAreaId(entity.getAreaId());
        vo.setChecklistTemplateId(entity.getChecklistTemplateId());
        vo.setSortOrder(entity.getSortOrder());
        return vo;
    }
}
