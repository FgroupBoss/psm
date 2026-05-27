package com.fgroupboss.ai.psm.majorhazard.service;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.audit.CentralAuditClient;
import com.fgroupboss.ai.psm.majorhazard.mapper.MajorHazardAuditRecordMapper;
import com.fgroupboss.ai.psm.majorhazard.mapper.MajorHazardMapper;
import com.fgroupboss.ai.psm.majorhazard.mapper.MajorHazardResponsibilityMapper;
import com.fgroupboss.ai.psm.majorhazard.mapper.MajorHazardStatusLogMapper;
import com.fgroupboss.ai.psm.majorhazard.model.dto.HazardStatusRequest;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardAuditRecordEntity;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardEntity;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardResponsibilityEntity;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardStatusLogEntity;
import com.fgroupboss.ai.psm.majorhazard.model.vo.MajorHazardVO;
import com.fgroupboss.ai.psm.majorhazard.service.impl.MajorHazardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MajorHazardServiceImplTest {

    private MajorHazardMapper hazardMapper;
    private MajorHazardResponsibilityMapper responsibilityMapper;
    private MajorHazardStatusLogMapper statusLogMapper;
    private MajorHazardAuditRecordMapper auditRecordMapper;
    private CentralAuditClient centralAuditClient;
    private MajorHazardService service;

    @BeforeEach
    void setUp() {
        hazardMapper = mock(MajorHazardMapper.class);
        responsibilityMapper = mock(MajorHazardResponsibilityMapper.class);
        statusLogMapper = mock(MajorHazardStatusLogMapper.class);
        auditRecordMapper = mock(MajorHazardAuditRecordMapper.class);
        centralAuditClient = mock(CentralAuditClient.class);
        service = new MajorHazardServiceImpl(hazardMapper, responsibilityMapper, statusLogMapper, auditRecordMapper,
                centralAuditClient);
    }

    @Test
    void publishShouldRejectWhenAreaMissing() {
        when(hazardMapper.selectById(2L)).thenReturn(hazard("DRAFT", null));

        assertThrows(BusinessException.class, () -> service.publish(1L, 2L, "admin"));
    }

    @Test
    void publishShouldRejectWhenResponsibilitiesIncomplete() {
        when(hazardMapper.selectById(2L)).thenReturn(hazard("DRAFT", 1L));
        when(responsibilityMapper.listByHazardId(1L, 2L))
                .thenReturn(Collections.singletonList(responsibility("PRIMARY")));

        assertThrows(BusinessException.class, () -> service.publish(1L, 2L, "admin"));
    }

    @Test
    void publishFromDraftShouldMoveToPublished() {
        when(hazardMapper.selectById(2L)).thenReturn(hazard("DRAFT", 1L));
        when(responsibilityMapper.listByHazardId(1L, 2L)).thenReturn(Arrays.asList(
                responsibility("PRIMARY"),
                responsibility("TECHNICAL"),
                responsibility("OPERATION")));

        MajorHazardVO result = service.publish(1L, 2L, "admin");

        assertEquals("PUBLISHED", result.getStatus());
        verify(hazardMapper).updateById(any(MajorHazardEntity.class));
        verify(statusLogMapper).insert(any(MajorHazardStatusLogEntity.class));
        verify(auditRecordMapper).insert(any(MajorHazardAuditRecordEntity.class));
    }

    @Test
    void changeStatusShouldRejectDraftHazard() {
        when(hazardMapper.selectById(2L)).thenReturn(hazard("DRAFT", 1L));
        HazardStatusRequest request = new HazardStatusRequest();
        request.setTargetStatus("SUSPENDED");

        assertThrows(BusinessException.class, () -> service.changeStatus(1L, 2L, request, "admin"));
    }

    @Test
    void changeStatusFromPublishedShouldSucceed() {
        when(hazardMapper.selectById(2L)).thenReturn(hazard("PUBLISHED", 1L));
        HazardStatusRequest request = new HazardStatusRequest();
        request.setTargetStatus("SUSPENDED");
        request.setReason("检修停罐");

        MajorHazardVO result = service.changeStatus(1L, 2L, request, "admin");

        assertEquals("SUSPENDED", result.getStatus());
        verify(statusLogMapper).insert(any(MajorHazardStatusLogEntity.class));
    }

    private MajorHazardEntity hazard(String status, Long areaId) {
        MajorHazardEntity entity = new MajorHazardEntity();
        entity.setId(2L);
        entity.setTenantId(1L);
        entity.setHazardNo("HZ-003");
        entity.setName("测试危险源");
        entity.setLevel("LEVEL_1");
        entity.setAreaId(areaId);
        entity.setStatus(status);
        entity.setDeleted(0);
        return entity;
    }

    private MajorHazardResponsibilityEntity responsibility(String type) {
        MajorHazardResponsibilityEntity entity = new MajorHazardResponsibilityEntity();
        entity.setResponsibilityType(type);
        entity.setPersonName("责任人");
        return entity;
    }
}
