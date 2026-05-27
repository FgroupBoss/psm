package com.fgroupboss.ai.psm.alarm.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.alarm.config.AlarmStatus;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmEscalationRecordMapper;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmEventMapper;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmRuleMapper;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmEscalationRecordEntity;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmEventEntity;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmRuleEntity;
import com.fgroupboss.ai.psm.alarm.service.AlarmEscalationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
/**
 * 实现方式：承载报警升级业务实现，基于 Mapper、远程客户端或支撑组件完成校验、状态流转和结果组装。
 */
@Service
@RequiredArgsConstructor
public class AlarmEscalationServiceImpl implements AlarmEscalationService {

    private static final String RULE_TYPE_TIMEOUT = "TIMEOUT";
    private static final String STATUS_ENABLED = "ENABLED";
    private static final String OPERATOR_SYSTEM = "system-escalation";

    private final AlarmRuleMapper alarmRuleMapper;
    private final AlarmEventMapper alarmEventMapper;
    private final AlarmEscalationRecordMapper escalationRecordMapper;
    private final ObjectMapper objectMapper;

    /**
     * 实现方式：扫描并处理报警升级，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public int processEscalations() {
        List<AlarmRuleEntity> rules = alarmRuleMapper.listEnabledByType(RULE_TYPE_TIMEOUT);
        int escalated = 0;
        Date now = new Date();
        for (AlarmRuleEntity rule : rules) {
            if (!STATUS_ENABLED.equalsIgnoreCase(rule.getStatus())) {
                continue;
            }
            escalated += processRule(rule, now);
        }
        if (escalated > 0) {
            log.info("alarm escalation scan completed count={}", escalated);
        }
        return escalated;
    }

    private int processRule(AlarmRuleEntity rule, Date now) {
        TimeoutConfig config = parseTimeoutConfig(rule.getConfigJson());
        if (config == null || config.level == null) {
            return 0;
        }
        if (isConfirmTimeoutRule(rule.getRuleCode())) {
            return escalateConfirmTimeout(rule.getTenantId(), config, now);
        }
        if (isDisposeTimeoutRule(rule.getRuleCode())) {
            return escalateDisposeTimeout(rule.getTenantId(), config, now);
        }
        return 0;
    }

    private int escalateConfirmTimeout(Long tenantId, TimeoutConfig config, Date now) {
        if (config.confirmMinutes == null || config.confirmMinutes <= 0) {
            return 0;
        }
        Date deadline = addMinutes(now, -config.confirmMinutes);
        List<AlarmEventEntity> candidates = alarmEventMapper.listTimeoutCandidates(
                tenantId, AlarmStatus.NEW.name(), config.level, deadline);
        int count = 0;
        for (AlarmEventEntity entity : candidates) {
            count += escalate(entity, "CONFIRM_TIMEOUT", config.level);
        }
        return count;
    }

    private int escalateDisposeTimeout(Long tenantId, TimeoutConfig config, Date now) {
        if (config.disposeMinutes == null || config.disposeMinutes <= 0) {
            return 0;
        }
        Date deadline = addMinutes(now, -config.disposeMinutes);
        List<AlarmEventEntity> candidates = alarmEventMapper.listDisposeTimeoutCandidates(
                tenantId, config.level, deadline);
        int count = 0;
        for (AlarmEventEntity entity : candidates) {
            count += escalate(entity, "DISPOSE_TIMEOUT", config.level);
        }
        return count;
    }

    private int escalate(AlarmEventEntity entity, String reason, String escalationLevel) {
        if (AlarmStatus.ESCALATED.name().equals(entity.getStatus())) {
            return 0;
        }
        String before = entity.getStatus();
        entity.setStatus(AlarmStatus.ESCALATED.name());
        alarmEventMapper.updateById(entity);

        AlarmEscalationRecordEntity record = new AlarmEscalationRecordEntity();
        record.setTenantId(entity.getTenantId());
        record.setAlarmEventId(entity.getId());
        record.setEscalationLevel(escalationLevel);
        record.setReason(reason);
        record.setOperatorName(OPERATOR_SYSTEM);
        record.setEscalatedAt(new Date());
        escalationRecordMapper.insert(record);
        log.info("alarm escalated id={} from={} reason={}", entity.getId(), before, reason);
        return 1;
    }

    private TimeoutConfig parseTimeoutConfig(String configJson) {
        if (!StringUtils.hasText(configJson)) {
            return null;
        }
        try {
            JsonNode node = objectMapper.readTree(configJson.trim());
            TimeoutConfig config = new TimeoutConfig();
            if (node.has("level")) {
                config.level = node.get("level").asText();
            }
            if (node.has("confirmMinutes")) {
                config.confirmMinutes = node.get("confirmMinutes").asInt();
            }
            if (node.has("disposeMinutes")) {
                config.disposeMinutes = node.get("disposeMinutes").asInt();
            }
            return config;
        } catch (Exception ex) {
            log.warn("invalid alarm rule config json={}", configJson);
            return null;
        }
    }

    private boolean isConfirmTimeoutRule(String ruleCode) {
        return StringUtils.hasText(ruleCode) && ruleCode.toUpperCase().contains("CONFIRM");
    }

    private boolean isDisposeTimeoutRule(String ruleCode) {
        return StringUtils.hasText(ruleCode) && ruleCode.toUpperCase().contains("DISPOSE");
    }

    private Date addMinutes(Date base, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(base);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    private static final class TimeoutConfig {
        private String level;
        private Integer confirmMinutes;
        private Integer disposeMinutes;
    }
}
