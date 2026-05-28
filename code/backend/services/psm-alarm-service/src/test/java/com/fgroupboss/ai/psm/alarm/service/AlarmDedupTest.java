package com.fgroupboss.ai.psm.alarm.service;

import com.fgroupboss.ai.psm.alarm.client.DualPreventionClient;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmActionRecordMapper;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmEventMapper;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmOccurrenceMapper;
import com.fgroupboss.ai.psm.alarm.model.dto.AlarmIngestRequest;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmDedupRuleEntity;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmEventEntity;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmOccurrenceEntity;
import com.fgroupboss.ai.psm.alarm.model.vo.AlarmEventVO;
import com.fgroupboss.ai.psm.alarm.service.impl.AlarmServiceImpl;
import com.fgroupboss.ai.psm.alarm.support.AlarmAuditSupport;
import com.fgroupboss.ai.psm.alarm.support.AlarmDedupSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AlarmDedupTest {

    private AlarmEventMapper alarmEventMapper;
    private AlarmOccurrenceMapper occurrenceMapper;
    private AlarmActionRecordMapper actionRecordMapper;
    private AlarmAuditSupport auditSupport;
    private AlarmDedupSupport dedupSupport;
    private AlarmService service;

    @BeforeEach
    void setUp() {
        alarmEventMapper = mock(AlarmEventMapper.class);
        occurrenceMapper = mock(AlarmOccurrenceMapper.class);
        actionRecordMapper = mock(AlarmActionRecordMapper.class);
        auditSupport = mock(AlarmAuditSupport.class);
        dedupSupport = mock(AlarmDedupSupport.class);
        service = new AlarmServiceImpl(alarmEventMapper, occurrenceMapper, actionRecordMapper,
                auditSupport, dedupSupport, mock(DualPreventionClient.class));
    }

    @Test
    void ingestShouldMergeWithinWindowAndIncrementCount() {
        Date now = new Date();
        AlarmEventEntity existing = new AlarmEventEntity();
        existing.setId(10L);
        existing.setTenantId(1L);
        existing.setAlarmNo("ALM-001");
        existing.setSourceType("GDS");
        existing.setSourceCode("MP-020-H2");
        existing.setTitle("测试报警");
        existing.setAlarmLevel("LEVEL_1");
        existing.setStatus("NEW");
        existing.setAreaId(1L);
        existing.setDedupKey("GDS|MP-020-H2|LEVEL_1|1");
        existing.setOccurrenceCount(2);
        existing.setLastOccurredAt(now);

        when(dedupSupport.resolveWindowSeconds(1L, "GDS")).thenReturn(300);
        when(dedupSupport.findMergeCandidate(eq(1L), eq("GDS|MP-020-H2|LEVEL_1|1"), any(Date.class), eq(300)))
                .thenReturn(existing);
        when(alarmEventMapper.updateById(any(AlarmEventEntity.class))).thenReturn(1);

        AlarmIngestRequest request = buildRequest();
        AlarmEventVO result = service.ingest(request);

        assertEquals(10L, result.getId().longValue());
        assertEquals(3, result.getOccurrenceCount().intValue());
        verify(alarmEventMapper, never()).insert(any(AlarmEventEntity.class));

        ArgumentCaptor<AlarmOccurrenceEntity> occurrenceCaptor = ArgumentCaptor.forClass(AlarmOccurrenceEntity.class);
        verify(occurrenceMapper).insert(occurrenceCaptor.capture());
        assertEquals(10L, occurrenceCaptor.getValue().getAlarmEventId().longValue());
    }

    @Test
    void ingestShouldCreateNewWhenOutsideWindow() {
        when(dedupSupport.resolveWindowSeconds(1L, "GDS")).thenReturn(300);
        when(dedupSupport.findMergeCandidate(eq(1L), any(String.class), any(Date.class), eq(300)))
                .thenReturn(null);
        when(alarmEventMapper.insert(any(AlarmEventEntity.class))).thenAnswer(invocation -> {
            AlarmEventEntity entity = invocation.getArgument(0);
            entity.setId(20L);
            return 1;
        });

        AlarmEventVO result = service.ingest(buildRequest());

        assertEquals(20L, result.getId().longValue());
        assertEquals(1, result.getOccurrenceCount().intValue());
        verify(alarmEventMapper).insert(any(AlarmEventEntity.class));
    }

    private AlarmIngestRequest buildRequest() {
        AlarmIngestRequest request = new AlarmIngestRequest();
        request.setTenantId(1L);
        request.setSourceType("GDS");
        request.setSourceCode("MP-020-H2");
        request.setTitle("测试报警");
        request.setAlarmLevel("LEVEL_1");
        request.setAreaId(1L);
        request.setRawValue("15.0");
        request.setOccurredAt(new Date());
        return request;
    }
}
