package com.fgroupboss.ai.psm.audit.service.impl;

import com.fgroupboss.ai.psm.audit.mapper.AuditChangeLogMapper;
import com.fgroupboss.ai.psm.audit.model.dto.AuditLogIngestRequest;
import com.fgroupboss.ai.psm.audit.model.entity.AuditChangeLogEntity;
import com.fgroupboss.ai.psm.audit.model.vo.AuditLogRecordVO;
import com.fgroupboss.ai.psm.audit.service.AuditLogService;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditChangeLogMapper auditChangeLogMapper;

    @Override
    public PageResult<AuditLogRecordVO> page(Long tenantId, String bizType, String bizTypePrefix, Long bizId,
                                             String action, String operatorName, LocalDateTime startTime,
                                             LocalDateTime endTime, int pageNo, int pageSize) {
        validateTenant(tenantId);
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (normalizedPageNo - 1) * normalizedPageSize;
        String normalizedBizType = normalizeText(bizType);
        String normalizedBizTypePrefix = normalizeText(bizTypePrefix);
        String normalizedAction = normalizeText(action);
        String normalizedOperatorName = normalizeText(operatorName);
        long total = auditChangeLogMapper.count(tenantId, normalizedBizType, normalizedBizTypePrefix, bizId,
                normalizedAction, normalizedOperatorName, startTime, endTime);
        List<AuditChangeLogEntity> entities = auditChangeLogMapper.list(tenantId, normalizedBizType,
                normalizedBizTypePrefix, bizId, normalizedAction, normalizedOperatorName, startTime, endTime,
                normalizedPageSize, offset);
        return new PageResult<AuditLogRecordVO>(total, normalizedPageNo, normalizedPageSize, toVOList(entities));
    }

    @Override
    public void append(AuditLogIngestRequest request) {
        validateTenant(request.getTenantId());
        AuditChangeLogEntity entity = new AuditChangeLogEntity();
        entity.setTenantId(request.getTenantId());
        entity.setOperatorName(StringUtils.hasText(request.getOperatorName()) ? request.getOperatorName().trim() : "system");
        entity.setAction(request.getAction().trim());
        entity.setBizType(request.getBizType().trim());
        entity.setBizId(request.getBizId());
        entity.setBeforeValue(request.getBeforeValue());
        entity.setAfterValue(request.getAfterValue());
        entity.setResult(StringUtils.hasText(request.getResult()) ? request.getResult().trim() : "SUCCESS");
        entity.setOperatedAt(LocalDateTime.now());
        auditChangeLogMapper.insert(entity);
    }

    private void validateTenant(Long tenantId) {
        if (tenantId == null || tenantId.longValue() <= 0L) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private List<AuditLogRecordVO> toVOList(List<AuditChangeLogEntity> entities) {
        List<AuditLogRecordVO> records = new ArrayList<AuditLogRecordVO>();
        for (AuditChangeLogEntity entity : entities) {
            records.add(toVO(entity));
        }
        return records;
    }

    private AuditLogRecordVO toVO(AuditChangeLogEntity entity) {
        AuditLogRecordVO vo = new AuditLogRecordVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setOperatorId(entity.getOperatorId());
        vo.setOperatorName(entity.getOperatorName());
        vo.setAction(entity.getAction());
        vo.setBizType(entity.getBizType());
        vo.setBizId(entity.getBizId());
        vo.setBeforeValue(entity.getBeforeValue());
        vo.setAfterValue(entity.getAfterValue());
        vo.setResult(entity.getResult());
        vo.setClientIp(entity.getClientIp());
        vo.setUserAgent(entity.getUserAgent());
        vo.setOperatedAt(entity.getOperatedAt());
        return vo;
    }
}
