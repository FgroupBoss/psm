package com.fgroupboss.ai.psm.alarm.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.alarm.config.AlarmStatus;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmEscalationRecordMapper;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmEventMapper;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmNotificationRecordMapper;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmRuleMapper;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmNotificationRecordEntity;
import com.fgroupboss.ai.psm.common.notification.CentralNotificationClient;
import com.fgroupboss.ai.psm.common.notification.NotificationSendRequest;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmEscalationRecordEntity;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmEventEntity;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmRuleEntity;
import com.fgroupboss.ai.psm.alarm.service.AlarmEscalationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final AlarmNotificationRecordMapper notificationRecordMapper;
    private final CentralNotificationClient notificationClient;
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
            return escalateConfirmTimeout(rule, config, now);
        }
        if (isDisposeTimeoutRule(rule.getRuleCode())) {
            return escalateDisposeTimeout(rule, config, now);
        }
        return 0;
    }

    private int escalateConfirmTimeout(AlarmRuleEntity rule, TimeoutConfig config, Date now) {
        if (config.confirmMinutes == null || config.confirmMinutes <= 0) {
            return 0;
        }
        Date deadline = addMinutes(now, -config.confirmMinutes);
        List<AlarmEventEntity> candidates = alarmEventMapper.listTimeoutCandidates(
                rule.getTenantId(), AlarmStatus.NEW.name(), config.level, deadline);
        int count = 0;
        for (AlarmEventEntity entity : candidates) {
            count += escalate(entity, rule, "CONFIRM_TIMEOUT", config);
        }
        return count;
    }

    private int escalateDisposeTimeout(AlarmRuleEntity rule, TimeoutConfig config, Date now) {
        if (config.disposeMinutes == null || config.disposeMinutes <= 0) {
            return 0;
        }
        Date deadline = addMinutes(now, -config.disposeMinutes);
        List<AlarmEventEntity> candidates = alarmEventMapper.listDisposeTimeoutCandidates(
                rule.getTenantId(), config.level, deadline);
        int count = 0;
        for (AlarmEventEntity entity : candidates) {
            count += escalate(entity, rule, "DISPOSE_TIMEOUT", config);
        }
        return count;
    }

    private int escalate(AlarmEventEntity entity, AlarmRuleEntity rule, String reason, TimeoutConfig config) {
        if (AlarmStatus.ESCALATED.name().equals(entity.getStatus())) {
            return 0;
        }
        String before = entity.getStatus();
        entity.setStatus(AlarmStatus.ESCALATED.name());
        alarmEventMapper.updateById(entity);

        AlarmEscalationRecordEntity record = new AlarmEscalationRecordEntity();
        record.setTenantId(entity.getTenantId());
        record.setAlarmEventId(entity.getId());
        record.setEscalationLevel(config.level);
        record.setReason(reason);
        record.setOperatorName(OPERATOR_SYSTEM);
        record.setEscalatedAt(new Date());
        escalationRecordMapper.insert(record);
        dispatchEscalationNotification(entity, rule, reason, config);
        log.info("alarm escalated id={} from={} reason={}", entity.getId(), before, reason);
        return 1;
    }

    private void dispatchEscalationNotification(AlarmEventEntity entity, AlarmRuleEntity rule,
                                                String reason, TimeoutConfig config) {
        List<Long> userIds = config.notifyUserIds == null ? new ArrayList<Long>() : config.notifyUserIds;
        if (userIds.isEmpty()) {
            return;
        }
        Map<String, String> variables = new HashMap<String, String>();
        variables.put("alarmNo", entity.getAlarmNo() == null ? String.valueOf(entity.getId()) : entity.getAlarmNo());
        variables.put("reason", reason);
        variables.put("level", config.level == null ? "" : config.level);
        String requestIdPrefix = "ALARM-ESC-" + entity.getId() + "-" + reason;
        for (Long userId : userIds) {
            if (userId == null) {
                continue;
            }
            NotificationSendRequest request = CentralNotificationClient.build(
                    entity.getTenantId(), userId, requestIdPrefix + "-" + userId,
                    "ALARM_ESCALATION", "ALARM", entity.getId(), variables);
            notificationClient.send(request);
            saveAlarmNotificationRecord(entity, userId, request.getRequestId(), variables.get("alarmNo"), reason);
        }
    }

    private void saveAlarmNotificationRecord(AlarmEventEntity entity, Long userId, String requestId,
                                             String alarmNo, String reason) {
        AlarmNotificationRecordEntity record = new AlarmNotificationRecordEntity();
        record.setTenantId(entity.getTenantId());
        record.setAlarmEventId(entity.getId());
        record.setChannel("IN_APP");
        record.setNotifyTarget(String.valueOf(userId));
        record.setNotifyContent("alarm " + alarmNo + " escalated: " + reason + " requestId=" + requestId);
        record.setStatus("SENT");
        record.setSentAt(new Date());
        record.setCreatedAt(new Date());
        notificationRecordMapper.insert(record);
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
            if (node.has("notifyUserIds") && node.get("notifyUserIds").isArray()) {
                config.notifyUserIds = new ArrayList<Long>();
                for (JsonNode item : node.get("notifyUserIds")) {
                    config.notifyUserIds.add(item.asLong());
                }
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
        private List<Long> notifyUserIds;
    }
}
