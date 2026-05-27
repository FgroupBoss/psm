package com.fgroupboss.ai.psm.majorhazard.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.majorhazard.client.AlarmServiceClient;
import com.fgroupboss.ai.psm.majorhazard.client.dto.AlarmAreaActiveCheckResult;
import com.fgroupboss.ai.psm.majorhazard.config.HazardLevel;
import com.fgroupboss.ai.psm.majorhazard.mapper.MajorHazardMapper;
import com.fgroupboss.ai.psm.majorhazard.model.dto.RiskContextRequest;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardEntity;
import com.fgroupboss.ai.psm.majorhazard.model.vo.RiskContextHazardVO;
import com.fgroupboss.ai.psm.majorhazard.model.vo.RiskContextVO;
import com.fgroupboss.ai.psm.majorhazard.service.RiskContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RiskContextServiceImpl implements RiskContextService {

    private static final String DEFAULT_BLOCKING_MIN_LEVEL = "LEVEL_2";

    private final MajorHazardMapper hazardMapper;
    private final AlarmServiceClient alarmServiceClient;

    @Value("${psm.blocking-alarm-enabled:true}")
    private boolean blockingAlarmEnabled;

    @Override
    public RiskContextVO query(RiskContextRequest request) {
        requireTenantId(request.getTenantId());
        assertHasScope(request);
        List<Long> pointIds = normalizePointIds(request.getPointIds());
        List<MajorHazardEntity> entities = hazardMapper.listForRiskContext(
                request.getTenantId(), request.getAreaId(), request.getUnitId(), pointIds);

        Map<Long, MajorHazardEntity> dedup = new LinkedHashMap<Long, MajorHazardEntity>();
        for (MajorHazardEntity entity : entities) {
            dedup.put(entity.getId(), entity);
        }

        RiskContextVO result = new RiskContextVO();
        result.setAreaId(request.getAreaId());
        result.setUnitId(request.getUnitId());
        result.setBlockingAlarm(false);
        result.setBlockingReason(null);

        List<String> levels = new ArrayList<String>();
        for (MajorHazardEntity entity : dedup.values()) {
            RiskContextHazardVO summary = new RiskContextHazardVO();
            summary.setId(entity.getId());
            summary.setName(entity.getName());
            summary.setLevel(entity.getLevel());
            summary.setStatus(entity.getStatus());
            result.getHazards().add(summary);
            levels.add(entity.getLevel());
        }
        result.setMaxLevel(HazardLevel.maxLevel(levels));
        applyBlockingAlarm(result, request);
        return result;
    }

    private void applyBlockingAlarm(RiskContextVO result, RiskContextRequest request) {
        if (!blockingAlarmEnabled || request.getAreaId() == null) {
            return;
        }
        if (request.getCheckBlockingAlarm() != null && !request.getCheckBlockingAlarm().booleanValue()) {
            return;
        }
        AlarmAreaActiveCheckResult check = alarmServiceClient.areaActiveCheck(
                request.getTenantId(), request.getAreaId(), DEFAULT_BLOCKING_MIN_LEVEL);
        if (check != null && check.isHasBlocking()) {
            result.setBlockingAlarm(true);
            result.setBlockingReason("区域存在 " + check.getCount() + " 条未关闭高等级报警");
        }
    }

    private void assertHasScope(RiskContextRequest request) {
        boolean hasArea = request.getAreaId() != null && request.getAreaId() > 0;
        boolean hasUnit = request.getUnitId() != null && request.getUnitId() > 0;
        boolean hasPoints = !CollectionUtils.isEmpty(request.getPointIds());
        if (!hasArea && !hasUnit && !hasPoints) {
            throw new BusinessException(400, "at least one of areaId, unitId, pointIds is required");
        }
    }

    private List<Long> normalizePointIds(List<Long> pointIds) {
        if (CollectionUtils.isEmpty(pointIds)) {
            return null;
        }
        List<Long> normalized = new ArrayList<Long>();
        for (Long pointId : pointIds) {
            if (pointId != null && pointId > 0 && !normalized.contains(pointId)) {
                normalized.add(pointId);
            }
        }
        return normalized.isEmpty() ? null : normalized;
    }

    private void requireTenantId(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is required");
        }
    }
}
