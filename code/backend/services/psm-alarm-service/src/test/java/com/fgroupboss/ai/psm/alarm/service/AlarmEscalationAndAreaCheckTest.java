package com.fgroupboss.ai.psm.alarm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.alarm.config.AlarmStatus;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmEscalationRecordMapper;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmEventMapper;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmRuleMapper;
import com.fgroupboss.ai.psm.alarm.model.dto.AlarmAreaActiveCheckRequest;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmEscalationRecordEntity;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmEventEntity;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmRuleEntity;
import com.fgroupboss.ai.psm.alarm.model.vo.AlarmAreaActiveCheckVO;
import com.fgroupboss.ai.psm.alarm.service.impl.AlarmEscalationServiceImpl;
import com.fgroupboss.ai.psm.alarm.service.impl.AlarmServiceImpl;
import com.fgroupboss.ai.psm.alarm.support.AlarmAuditSupport;
import com.fgroupboss.ai.psm.alarm.support.AlarmDedupSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AlarmEscalationAndAreaCheckTest {

    private AlarmEventMapper alarmEventMapper;
    private AlarmRuleMapper alarmRuleMapper;
    private AlarmEscalationRecordMapper escalationRecordMapper;
    private AlarmEscalationService escalationService;
    private AlarmService alarmService;

    @BeforeEach
    void setUp() {
        alarmEventMapper = mock(AlarmEventMapper.class);
        alarmRuleMapper = mock(AlarmRuleMapper.class);
        escalationRecordMapper = mock(AlarmEscalationRecordMapper.class);
        escalationService = new AlarmEscalationServiceImpl(
                alarmRuleMapper, alarmEventMapper, escalationRecordMapper, new ObjectMapper());
        alarmService = new AlarmServiceImpl(
                alarmEventMapper,
                mock(com.fgroupboss.ai.psm.alarm.mapper.AlarmOccurrenceMapper.class),
                mock(com.fgroupboss.ai.psm.alarm.mapper.AlarmActionRecordMapper.class),
                mock(AlarmAuditSupport.class),
                mock(AlarmDedupSupport.class));
    }

    @Test
    void processEscalationsShouldUpgradeNewAlarm() {
        AlarmRuleEntity rule = new AlarmRuleEntity();
        rule.setTenantId(1L);
        rule.setRuleCode("CONFIRM_TIMEOUT_L1");
        rule.setRuleType("TIMEOUT");
        rule.setStatus("ENABLED");
        rule.setConfigJson("{\"level\":\"LEVEL_1\",\"confirmMinutes\":5}");
        when(alarmRuleMapper.listEnabledByType("TIMEOUT")).thenReturn(Collections.singletonList(rule));

        AlarmEventEntity stale = new AlarmEventEntity();
        stale.setId(1L);
        stale.setTenantId(1L);
        stale.setStatus(AlarmStatus.NEW.name());
        stale.setAlarmLevel("LEVEL_1");
        stale.setFirstOccurredAt(pastMinutes(10));
        when(alarmEventMapper.listTimeoutCandidates(eq(1L), eq("NEW"), eq("LEVEL_1"), any(Date.class)))
                .thenReturn(Collections.singletonList(stale));
        when(alarmEventMapper.updateById(any(AlarmEventEntity.class))).thenReturn(1);
        when(escalationRecordMapper.insert(any(AlarmEscalationRecordEntity.class))).thenReturn(1);

        int count = escalationService.processEscalations();

        assertEquals(1, count);
        ArgumentCaptor<AlarmEventEntity> captor = ArgumentCaptor.forClass(AlarmEventEntity.class);
        verify(alarmEventMapper).updateById(captor.capture());
        assertEquals(AlarmStatus.ESCALATED.name(), captor.getValue().getStatus());
    }

    @Test
    void areaActiveCheckShouldReturnBlockingAlarms() {
        AlarmEventEntity level1 = buildActiveAlarm(1L, "LEVEL_1");
        AlarmEventEntity level3 = buildActiveAlarm(2L, "LEVEL_3");
        when(alarmEventMapper.listActiveByArea(1L, 1L)).thenReturn(Arrays.asList(level1, level3));

        AlarmAreaActiveCheckRequest request = new AlarmAreaActiveCheckRequest();
        request.setTenantId(1L);
        request.setAreaId(1L);
        request.setMinLevel("LEVEL_2");

        AlarmAreaActiveCheckVO result = alarmService.areaActiveCheck(request);

        assertTrue(result.isHasBlocking());
        assertEquals(1, result.getCount());
        assertEquals("LEVEL_1", result.getAlarms().get(0).getAlarmLevel());
    }

    private AlarmEventEntity buildActiveAlarm(Long id, String level) {
        AlarmEventEntity entity = new AlarmEventEntity();
        entity.setId(id);
        entity.setTenantId(1L);
        entity.setAlarmNo("ALM-" + id);
        entity.setSourceType("GDS");
        entity.setTitle("活跃报警");
        entity.setAlarmLevel(level);
        entity.setStatus(AlarmStatus.NEW.name());
        entity.setAreaId(1L);
        entity.setDeleted(0);
        return entity;
    }

    private Date pastMinutes(int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -minutes);
        return calendar.getTime();
    }
}
