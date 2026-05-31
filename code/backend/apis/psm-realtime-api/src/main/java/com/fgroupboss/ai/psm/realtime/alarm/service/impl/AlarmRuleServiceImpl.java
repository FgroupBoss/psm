package com.fgroupboss.ai.psm.realtime.alarm.service.impl;

import com.fgroupboss.ai.psm.realtime.alarm.mapper.AlarmRuleMapper;
import com.fgroupboss.ai.psm.realtime.alarm.model.dto.AlarmRuleSaveRequest;
import com.fgroupboss.ai.psm.realtime.alarm.model.entity.AlarmRuleEntity;
import com.fgroupboss.ai.psm.realtime.alarm.model.vo.AlarmRuleVO;
import com.fgroupboss.ai.psm.realtime.alarm.service.AlarmRuleService;
import com.fgroupboss.ai.psm.common.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现方式：承载报警规则业务实现，基于 Mapper、远程客户端或支撑组件完成校验、状态流转和结果组装。
 */
@Service
@RequiredArgsConstructor
public class AlarmRuleServiceImpl implements AlarmRuleService {

    private final AlarmRuleMapper alarmRuleMapper;

    /**
     * 实现方式：查询列表数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
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

    /**
     * 实现方式：查询详情，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public AlarmRuleVO get(Long tenantId, Long id) {
        return toVO(requireRule(tenantId, id));
    }

    /**
     * 实现方式：创建业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
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

    /**
     * 实现方式：更新业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
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
