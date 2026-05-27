package com.fgroupboss.ai.psm.majorhazard.service;

import com.fgroupboss.ai.psm.common.AuditBizType;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.majorhazard.mapper.MajorHazardMapper;
import com.fgroupboss.ai.psm.majorhazard.mapper.MajorHazardPointRelMapper;
import com.fgroupboss.ai.psm.majorhazard.model.dto.HazardPointRequest;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardEntity;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardPointRelEntity;
import com.fgroupboss.ai.psm.majorhazard.model.vo.HazardPointVO;
import com.fgroupboss.ai.psm.majorhazard.service.impl.MajorHazardPointServiceImpl;
import com.fgroupboss.ai.psm.majorhazard.support.MajorHazardAuditSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MajorHazardPointServiceImplTest {

    private MajorHazardMapper hazardMapper;
    private MajorHazardPointRelMapper pointRelMapper;
    private MajorHazardAuditSupport auditSupport;
    private MajorHazardPointService service;

    @BeforeEach
    void setUp() {
        hazardMapper = mock(MajorHazardMapper.class);
        pointRelMapper = mock(MajorHazardPointRelMapper.class);
        auditSupport = mock(MajorHazardAuditSupport.class);
        service = new MajorHazardPointServiceImpl(hazardMapper, pointRelMapper, auditSupport);
    }

    @Test
    void bindPointShouldRejectDuplicate() {
        when(hazardMapper.selectById(1L)).thenReturn(hazard());
        when(pointRelMapper.findActive(1L, 1L, 10L)).thenReturn(new MajorHazardPointRelEntity());
        HazardPointRequest request = new HazardPointRequest();
        request.setMonitorPointId(10L);
        assertThrows(BusinessException.class, () -> service.bindPoint(1L, 1L, request, "admin"));
    }

    @Test
    void bindPointShouldWriteAudit() {
        when(hazardMapper.selectById(1L)).thenReturn(hazard());
        when(pointRelMapper.findActive(1L, 1L, 10L)).thenReturn(null);
        HazardPointRequest request = new HazardPointRequest();
        request.setMonitorPointId(10L);
        request.setPointCode("MP-010");
        request.setPointName("测试点位");

        HazardPointVO result = service.bindPoint(1L, 1L, request, "admin");

        assertEquals(10L, result.getMonitorPointId());
        verify(pointRelMapper).insert(any(MajorHazardPointRelEntity.class));
        verify(auditSupport).write(eq(1L), eq(1L), eq(AuditBizType.MAJOR_HAZARD_POINT.name()), eq("BIND"), any(String.class), eq("admin"));
    }

    private MajorHazardEntity hazard() {
        MajorHazardEntity entity = new MajorHazardEntity();
        entity.setId(1L);
        entity.setTenantId(1L);
        entity.setDeleted(0);
        return entity;
    }
}
