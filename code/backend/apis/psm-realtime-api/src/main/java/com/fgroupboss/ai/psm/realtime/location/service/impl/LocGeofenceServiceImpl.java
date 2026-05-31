package com.fgroupboss.ai.psm.realtime.location.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.realtime.location.mapper.LocGeofenceMapper;
import com.fgroupboss.ai.psm.realtime.location.mapper.LocGeofenceRuleMapper;
import com.fgroupboss.ai.psm.realtime.location.model.dto.LocGeofenceRequest;
import com.fgroupboss.ai.psm.realtime.location.model.dto.LocGeofenceRuleRequest;
import com.fgroupboss.ai.psm.realtime.location.model.entity.LocGeofenceEntity;
import com.fgroupboss.ai.psm.realtime.location.model.entity.LocGeofenceRuleEntity;
import com.fgroupboss.ai.psm.realtime.location.model.vo.LocGeofenceRuleVO;
import com.fgroupboss.ai.psm.realtime.location.model.vo.LocGeofenceVO;
import com.fgroupboss.ai.psm.realtime.location.service.LocGeofenceService;
import com.fgroupboss.ai.psm.realtime.location.support.LocationSupport;
import com.fgroupboss.ai.psm.realtime.location.support.LocationSupport.PageSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocGeofenceServiceImpl implements LocGeofenceService {

    private final LocGeofenceMapper geofenceMapper;
    private final LocGeofenceRuleMapper ruleMapper;

    @Override
    public PageResult<LocGeofenceVO> page(Long tenantId, String keyword, String fenceType, int pageNo, int pageSize) {
        LocationSupport.requireTenantId(tenantId);
        PageSpec page = LocationSupport.normalizePage(pageNo, pageSize);
        String normalizedKeyword = LocationSupport.normalizeText(keyword);
        String normalizedFenceType = LocationSupport.normalizeText(fenceType);
        long total = geofenceMapper.countByTenant(tenantId, normalizedKeyword, normalizedFenceType);
        List<LocGeofenceEntity> entities = total == 0
                ? new ArrayList<LocGeofenceEntity>()
                : geofenceMapper.listByTenant(tenantId, normalizedKeyword, normalizedFenceType,
                page.getOffset(), page.getPageSize());
        List<LocGeofenceVO> records = new ArrayList<LocGeofenceVO>();
        for (LocGeofenceEntity entity : entities) {
            records.add(toVO(entity, ruleMapper.listByFenceId(tenantId, entity.getId())));
        }
        return new PageResult<LocGeofenceVO>(total, page.getPageNo(), page.getPageSize(), records);
    }

    @Override
    @Transactional
    public LocGeofenceVO create(LocGeofenceRequest request) {
        LocationSupport.requireTenantId(request.getTenantId());
        assertFenceCodeUnique(request.getTenantId(), request.getFenceCode(), null);
        LocGeofenceEntity entity = new LocGeofenceEntity();
        entity.setTenantId(request.getTenantId());
        entity.setFenceCode(request.getFenceCode().trim());
        entity.setFenceName(request.getFenceName().trim());
        entity.setAreaId(request.getAreaId());
        entity.setFenceType(request.getFenceType().trim());
        entity.setGeometryJson(request.getGeometryJson());
        entity.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus().trim() : "ENABLED");
        entity.setDeleted(0);
        geofenceMapper.insert(entity);
        List<LocGeofenceRuleEntity> rules = saveRules(request, entity);
        return toVO(entity, rules);
    }

    private List<LocGeofenceRuleEntity> saveRules(LocGeofenceRequest request, LocGeofenceEntity fence) {
        List<LocGeofenceRuleEntity> saved = new ArrayList<LocGeofenceRuleEntity>();
        if (request.getRules() == null || request.getRules().isEmpty()) {
            return saved;
        }
        for (LocGeofenceRuleRequest ruleRequest : request.getRules()) {
            LocGeofenceRuleEntity rule = new LocGeofenceRuleEntity();
            rule.setTenantId(request.getTenantId());
            rule.setFenceId(fence.getId());
            rule.setRuleType(ruleRequest.getRuleType().trim());
            rule.setThresholdValue(ruleRequest.getThresholdValue());
            rule.setEnabled(Boolean.FALSE.equals(ruleRequest.getEnabled()) ? 0 : 1);
            rule.setDeleted(0);
            ruleMapper.insert(rule);
            saved.add(rule);
        }
        return saved;
    }

    private void assertFenceCodeUnique(Long tenantId, String fenceCode, Long excludeId) {
        LocGeofenceEntity existing = geofenceMapper.findByCode(tenantId, fenceCode.trim());
        if (existing != null && (excludeId == null || !excludeId.equals(existing.getId()))) {
            throw new BusinessException(400, "fenceCode already exists: " + fenceCode);
        }
    }

    private LocGeofenceVO toVO(LocGeofenceEntity entity, List<LocGeofenceRuleEntity> rules) {
        LocGeofenceVO vo = new LocGeofenceVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setFenceCode(entity.getFenceCode());
        vo.setFenceName(entity.getFenceName());
        vo.setAreaId(entity.getAreaId());
        vo.setFenceType(entity.getFenceType());
        vo.setGeometryJson(entity.getGeometryJson());
        vo.setStatus(entity.getStatus());
        List<LocGeofenceRuleVO> ruleVos = new ArrayList<LocGeofenceRuleVO>();
        if (rules != null) {
            for (LocGeofenceRuleEntity rule : rules) {
                ruleVos.add(toRuleVO(rule));
            }
        }
        vo.setRules(ruleVos);
        return vo;
    }

    private LocGeofenceRuleVO toRuleVO(LocGeofenceRuleEntity entity) {
        LocGeofenceRuleVO vo = new LocGeofenceRuleVO();
        vo.setId(entity.getId());
        vo.setFenceId(entity.getFenceId());
        vo.setRuleType(entity.getRuleType());
        vo.setThresholdValue(entity.getThresholdValue());
        vo.setEnabled(entity.getEnabled() != null && entity.getEnabled() == 1);
        return vo;
    }
}
