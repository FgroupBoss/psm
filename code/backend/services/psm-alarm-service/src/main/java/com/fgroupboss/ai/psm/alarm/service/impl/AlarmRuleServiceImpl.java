package com.fgroupboss.ai.psm.alarm.service.impl;

import com.fgroupboss.ai.psm.alarm.mapper.AlarmRuleMapper;
import com.fgroupboss.ai.psm.alarm.model.dto.AlarmRuleSaveRequest;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmRuleEntity;
import com.fgroupboss.ai.psm.alarm.model.vo.AlarmRuleVO;
import com.fgroupboss.ai.psm.alarm.service.AlarmRuleService;
import com.fgroupboss.ai.psm.common.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmRuleServiceImpl implements AlarmRuleService {

    private final AlarmRuleMapper alarmRuleMapper;

    @Override
    public List<AlarmRuleVO> list(Long tenantId, String ruleType) {
        requireTenantId(tenantId);
        List<AlarmRuleEntity> entities = alarmRuleMapper.listByTenant(tenantId, normalizeText(ruleType));
        List<AlarmRuleVO> result = new ArrayList<AlarmRuleVO>();
        for (AlarmRuleEntity entity : entities) {
            result.add(toVO(entity));
        }
        return result;
    }

    @Override
    public AlarmRuleVO get(Long tenantId, Long id) {
        return toVO(requireRule(tenantId, id));
    }

    @Override
    public AlarmRuleVO create(AlarmRuleSaveRequest request) {
        requireTenantId(request.getTenantId());
        String ruleCode = normalizeRequired(request.getRuleCode(), "ruleCode");
        if (alarmRuleMapper.findByCode(request.getTenantId(), ruleCode) != null) {
            throw new BusinessException(409, "ruleCode already exists");
        }
        AlarmRuleEntity entity = new AlarmRuleEntity();
        entity.setTenantId(request.getTenantId());
        entity.setRuleCode(ruleCode);
        entity.setRuleName(normalizeRequired(request.getRuleName(), "ruleName"));
        entity.setRuleType(normalizeRequired(request.getRuleType(), "ruleType"));
        entity.setConfigJson(normalizeText(request.getConfigJson()));
        entity.setStatus(normalizeStatus(request.getStatus()));
        alarmRuleMapper.insert(entity);
        return toVO(entity);
    }

    @Override
    public AlarmRuleVO update(Long id, AlarmRuleSaveRequest request) {
        AlarmRuleEntity entity = requireRule(request.getTenantId(), id);
        entity.setRuleName(normalizeRequired(request.getRuleName(), "ruleName"));
        entity.setRuleType(normalizeRequired(request.getRuleType(), "ruleType"));
        entity.setConfigJson(normalizeText(request.getConfigJson()));
        entity.setStatus(normalizeStatus(request.getStatus()));
        alarmRuleMapper.updateById(entity);
        return toVO(entity);
    }

    private AlarmRuleEntity requireRule(Long tenantId, Long id) {
        requireTenantId(tenantId);
        if (id == null) {
            throw new BusinessException(400, "rule id is required");
        }
        AlarmRuleEntity entity = alarmRuleMapper.selectById(id);
        if (entity == null || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "alarm rule not found");
        }
        return entity;
    }

    private AlarmRuleVO toVO(AlarmRuleEntity entity) {
        AlarmRuleVO vo = new AlarmRuleVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setRuleCode(entity.getRuleCode());
        vo.setRuleName(entity.getRuleName());
        vo.setRuleType(entity.getRuleType());
        vo.setConfigJson(entity.getConfigJson());
        vo.setStatus(entity.getStatus());
        return vo;
    }

    private String normalizeStatus(String status) {
        String normalized = normalizeText(status);
        return normalized == null ? "ENABLED" : normalized.toUpperCase();
    }

    private void requireTenantId(Long tenantId) {
        if (tenantId == null) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    private String normalizeText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String normalizeRequired(String value, String field) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            throw new BusinessException(400, field + " is required");
        }
        return normalized;
    }
}
