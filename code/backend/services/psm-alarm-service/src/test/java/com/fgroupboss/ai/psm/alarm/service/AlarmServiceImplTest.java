package com.fgroupboss.ai.psm.alarm.service;

import com.fgroupboss.ai.psm.alarm.client.DualPreventionClient;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmActionRecordMapper;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmEventMapper;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmOccurrenceMapper;
import com.fgroupboss.ai.psm.alarm.model.dto.AlarmIngestRequest;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmEventEntity;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmOccurrenceEntity;
import com.fgroupboss.ai.psm.realtime.api.alarm.vo.AlarmEventVO;
import com.fgroupboss.ai.psm.alarm.service.impl.AlarmServiceImpl;
import com.fgroupboss.ai.psm.alarm.support.AlarmAuditSupport;
import com.fgroupboss.ai.psm.alarm.support.AlarmDedupSupport;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AlarmServiceImplTest {

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
        when(dedupSupport.resolveWindowSeconds(any(Long.class), any(String.class))).thenReturn(300);
        when(dedupSupport.findMergeCandidate(any(Long.class), any(String.class), any(Date.class), anyInt()))
                .thenReturn(null);
    }

    @Test
    void healthShouldReturnSeedCount() {
        when(alarmEventMapper.selectCount(any())).thenReturn(5L);

        assertEquals(5L, service.health().getSeedEventCount());
        assertEquals("1.0.0-batch6", service.health().getVersion());
    }

    @Test
    void ingestShouldCreateNewEventAndOccurrence() {
        when(alarmEventMapper.insert(any(AlarmEventEntity.class))).thenAnswer(invocation -> {
            AlarmEventEntity entity = invocation.getArgument(0);
            entity.setId(100L);
            return 1;
        });

        AlarmIngestRequest request = new AlarmIngestRequest();
        request.setTenantId(1L);
        request.setSourceType("GDS");
        request.setSourceCode("MP-020-H2");
        request.setTitle("娴嬭瘯鎶ヨ");
        request.setAlarmLevel("LEVEL_1");
        request.setAreaId(1L);
        request.setMonitorPointId(20L);
        request.setHazardId(1L);
        request.setRawValue("12.5");

        AlarmEventVO result = service.ingest(request);

        assertEquals("NEW", result.getStatus());
        assertEquals("LEVEL_1", result.getAlarmLevel());
        assertEquals(1L, result.getAreaId().longValue());
        assertEquals(20L, result.getMonitorPointId().longValue());

        ArgumentCaptor<AlarmOccurrenceEntity> occurrenceCaptor = ArgumentCaptor.forClass(AlarmOccurrenceEntity.class);
        verify(occurrenceMapper).insert(occurrenceCaptor.capture());
        assertEquals(100L, occurrenceCaptor.getValue().getAlarmEventId().longValue());
        assertEquals("12.5", occurrenceCaptor.getValue().getRawValue());
    }

    @Test
    void ingestShouldRejectMissingTitle() {
        AlarmIngestRequest request = new AlarmIngestRequest();
        request.setTenantId(1L);
        request.setSourceType("GDS");
        request.setAlarmLevel("LEVEL_2");

        assertThrows(BusinessException.class, () -> service.ingest(request));
    }

    @Test
    void pageShouldFilterByStatusAndLevel() {
        AlarmEventEntity entity = new AlarmEventEntity();
        entity.setId(1L);
        entity.setTenantId(1L);
        entity.setAlarmNo("ALM-001");
        entity.setSourceType("GDS");
        entity.setTitle("楂樻姤");
        entity.setAlarmLevel("LEVEL_1");
        entity.setStatus("NEW");
        entity.setOccurrenceCount(1);
        entity.setDeleted(0);

        when(alarmEventMapper.countByTenant(eq(1L), eq(null), eq("NEW"), eq("LEVEL_1"),
                eq(null), eq(null), eq(null), eq(null), eq(null))).thenReturn(1L);
        when(alarmEventMapper.listByTenant(eq(1L), eq(null), eq("NEW"), eq("LEVEL_1"),
                eq(null), eq(null), eq(null), eq(null), eq(null), eq(0), eq(20)))
                .thenReturn(Collections.singletonList(entity));

        PageResult<AlarmEventVO> page = service.page(1L, null, "NEW", "LEVEL_1",
                null, null, null, null, null, 1, 20);

        assertEquals(1L, page.getTotal());
        assertEquals("LEVEL_1", page.getRecords().get(0).getAlarmLevel());
    }

    @Test
    void getDetailShouldIncludeOccurrences() {
        AlarmEventEntity entity = new AlarmEventEntity();
        entity.setId(2L);
        entity.setTenantId(1L);
        entity.setAlarmNo("ALM-002");
        entity.setSourceType("GDS");
        entity.setTitle("璇︽儏娴嬭瘯");
        entity.setAlarmLevel("LEVEL_2");
        entity.setStatus("CONFIRMED");
        entity.setDeleted(0);

        when(alarmEventMapper.selectById(2L)).thenReturn(entity);

        AlarmOccurrenceEntity occurrence = new AlarmOccurrenceEntity();
        occurrence.setId(10L);
        occurrence.setOccurredAt(new Date());
        occurrence.setRawValue("8.8");
        when(occurrenceMapper.listByAlarmEventId(1L, 2L)).thenReturn(Collections.singletonList(occurrence));
        when(actionRecordMapper.listByAlarmEventId(1L, 2L)).thenReturn(Collections.emptyList());

        assertEquals(1, service.getDetail(1L, 2L).getOccurrences().size());
        assertEquals("璇︽儏娴嬭瘯", service.getDetail(1L, 2L).getEvent().getTitle());
    }

    @Test
    void getDetailShouldRejectMissingAlarm() {
        when(alarmEventMapper.selectById(99L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> service.getDetail(1L, 99L));
    }
}

