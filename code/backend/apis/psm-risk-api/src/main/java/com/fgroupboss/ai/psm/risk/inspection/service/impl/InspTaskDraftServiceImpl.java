package com.fgroupboss.ai.psm.risk.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.risk.inspection.mapper.InspTaskDraftMapper;
import com.fgroupboss.ai.psm.risk.inspection.model.dto.TaskDraftSyncRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.entity.InspTaskDraftEntity;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.TaskDraftSyncResultVO;
import com.fgroupboss.ai.psm.risk.inspection.service.InspTaskDraftService;
import com.fgroupboss.ai.psm.risk.inspection.support.InspectionSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 巡检任务离线草稿同步。
 */
@Service
@RequiredArgsConstructor
public class InspTaskDraftServiceImpl implements InspTaskDraftService {

    private final InspTaskDraftMapper draftMapper;

    @Override
    @Transactional
    public TaskDraftSyncResultVO syncDraft(TaskDraftSyncRequest request) {
        InspectionSupport.requireTenantId(request.getTenantId());
        String clientDraftId = normalizeRequired(request.getClientDraftId(), "clientDraftId");
        LambdaQueryWrapper<InspTaskDraftEntity> wrapper = new LambdaQueryWrapper<InspTaskDraftEntity>()
                .eq(InspTaskDraftEntity::getTenantId, request.getTenantId())
                .eq(InspTaskDraftEntity::getClientDraftId, clientDraftId);
        InspTaskDraftEntity existing = draftMapper.selectOne(wrapper);
        LocalDateTime now = LocalDateTime.now();
        if (existing == null) {
            InspTaskDraftEntity entity = new InspTaskDraftEntity();
            entity.setTenantId(request.getTenantId());
            entity.setClientDraftId(clientDraftId);
            entity.setTaskId(request.getTaskId());
            entity.setPayloadJson(request.getPayloadJson() == null ? "{}" : request.getPayloadJson());
            entity.setSyncStatus("SYNCED");
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            draftMapper.insert(entity);
        } else {
            existing.setTaskId(request.getTaskId());
            existing.setPayloadJson(request.getPayloadJson() == null ? existing.getPayloadJson() : request.getPayloadJson());
            existing.setSyncStatus("SYNCED");
            existing.setLastError(null);
            existing.setUpdatedAt(now);
            draftMapper.updateById(existing);
        }
        TaskDraftSyncResultVO result = new TaskDraftSyncResultVO();
        result.setClientDraftId(clientDraftId);
        result.setTaskId(request.getTaskId());
        result.setSyncStatus("SYNCED");
        result.setStorage("INSPECTION_DB");
        return result;
    }

    private String normalizeRequired(String value, String field) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(400, field + " is required");
        }
        return value.trim();
    }
}
