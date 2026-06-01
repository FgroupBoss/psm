package com.fgroupboss.ai.psm.risk.majorhazard.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.risk.majorhazard.client.AlarmServiceClient;
import com.fgroupboss.ai.psm.risk.majorhazard.config.HazardLevel;
import com.fgroupboss.ai.psm.risk.majorhazard.mapper.MajorHazardMapper;
import com.fgroupboss.ai.psm.risk.majorhazard.model.dto.RiskContextRequest;
import com.fgroupboss.ai.psm.risk.majorhazard.model.entity.MajorHazardEntity;
import com.fgroupboss.ai.psm.risk.majorhazard.model.vo.RiskContextHazardVO;
import com.fgroupboss.ai.psm.risk.majorhazard.model.vo.RiskContextVO;
import com.fgroupboss.ai.psm.risk.majorhazard.service.RiskContextService;
import com.fgroupboss.ai.psm.risk.majorhazard.client.dto.AlarmAreaActiveCheckResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 瀹炵幇鏂瑰紡锛氭壙杞介闄╀笂涓嬫枃涓氬姟瀹炵幇锛屽熀浜?Mapper銆佽繙绋嬪鎴风鎴栨敮鎾戠粍浠跺畬鎴愭牎楠屻€佺姸鎬佹祦杞拰缁撴灉缁勮銆?
 */
@Service
@RequiredArgsConstructor
public class RiskContextServiceImpl implements RiskContextService {

    private static final String DEFAULT_BLOCKING_MIN_LEVEL = "LEVEL_2";

    private final MajorHazardMapper hazardMapper;
    private final AlarmServiceClient alarmServiceClient;

    @Value("${psm.blocking-alarm-enabled:true}")
    private boolean blockingAlarmEnabled;

    /**
     * 瀹炵幇鏂瑰紡锛氭煡璇笟鍔′笂涓嬫枃锛屽厛瀹屾垚蹇呰鐨勫弬鏁般€佺鎴锋垨鐘舵€佹牎楠岋紝鍐嶅鎵樻寔涔呭寲缁勪欢鎴栬繙绋嬪鎴风澶勭悊骞剁粍瑁呰繑鍥炵粨鏋溿€?
     */
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

