package com.fgroupboss.ai.psm.majorhazard.service;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.majorhazard.client.AlarmServiceClient;
import com.fgroupboss.ai.psm.majorhazard.client.dto.AlarmAreaActiveCheckResult;
import com.fgroupboss.ai.psm.majorhazard.mapper.MajorHazardMapper;
import com.fgroupboss.ai.psm.majorhazard.model.dto.RiskContextRequest;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardEntity;
import com.fgroupboss.ai.psm.majorhazard.model.vo.RiskContextVO;
import com.fgroupboss.ai.psm.majorhazard.service.impl.RiskContextServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RiskContextServiceImplTest {

    private MajorHazardMapper hazardMapper;
    private AlarmServiceClient alarmServiceClient;
    private RiskContextService service;

    @BeforeEach
    void setUp() {
        hazardMapper = mock(MajorHazardMapper.class);
        alarmServiceClient = mock(AlarmServiceClient.class);
        service = new RiskContextServiceImpl(hazardMapper, alarmServiceClient);
    }

    @Test
    void queryShouldRequireScope() {
        RiskContextRequest request = new RiskContextRequest();
        request.setTenantId(1L);
        assertThrows(BusinessException.class, () -> service.query(request));
    }

    @Test
    void queryByAreaShouldReturnMaxLevel() {
        RiskContextRequest request = new RiskContextRequest();
        request.setTenantId(1L);
        request.setAreaId(1L);
        when(hazardMapper.listForRiskContext(eq(1L), eq(1L), isNull(), isNull()))
                .thenReturn(Arrays.asList(hazard(1L, "罐区A", "LEVEL_1"), hazard(2L, "罐区B", "LEVEL_2")));

        RiskContextVO result = service.query(request);

        assertEquals(2, result.getHazards().size());
        assertEquals("LEVEL_1", result.getMaxLevel());
        assertFalse(result.isBlockingAlarm());
    }

    @Test
    void queryShouldSetBlockingAlarmWhenAreaHasActiveAlarms() {
        ReflectionTestUtils.setField(service, "blockingAlarmEnabled", true);
        RiskContextRequest request = new RiskContextRequest();
        request.setTenantId(1L);
        request.setAreaId(1L);
        when(hazardMapper.listForRiskContext(eq(1L), eq(1L), isNull(), isNull()))
                .thenReturn(Collections.singletonList(hazard(1L, "罐区A", "LEVEL_1")));
        AlarmAreaActiveCheckResult check = new AlarmAreaActiveCheckResult();
        check.setHasBlocking(true);
        check.setCount(2);
        when(alarmServiceClient.areaActiveCheck(1L, 1L, "LEVEL_2")).thenReturn(check);

        RiskContextVO result = service.query(request);

        assertTrue(result.isBlockingAlarm());
        assertEquals("区域存在 2 条未关闭高等级报警", result.getBlockingReason());
    }

    @Test
    void queryShouldReturnEmptyWhenNoMatch() {
        RiskContextRequest request = new RiskContextRequest();
        request.setTenantId(1L);
        request.setAreaId(99L);
        when(hazardMapper.listForRiskContext(eq(1L), eq(99L), isNull(), isNull()))
                .thenReturn(Collections.<MajorHazardEntity>emptyList());

        RiskContextVO result = service.query(request);

        assertTrue(result.getHazards().isEmpty());
        assertEquals(null, result.getMaxLevel());
    }

    private MajorHazardEntity hazard(Long id, String name, String level) {
        MajorHazardEntity entity = new MajorHazardEntity();
        entity.setId(id);
        entity.setTenantId(1L);
        entity.setName(name);
        entity.setLevel(level);
        entity.setStatus("PUBLISHED");
        entity.setDeleted(0);
        return entity;
    }
}
