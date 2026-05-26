package com.fgroupboss.ai.psm.contractor.service;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.contractor.mapper.ContractorAuditRecordMapper;
import com.fgroupboss.ai.psm.contractor.mapper.ContractorBlacklistMapper;
import com.fgroupboss.ai.psm.contractor.mapper.ContractorCompanyMapper;
import com.fgroupboss.ai.psm.contractor.model.dto.CompanyApproveRequest;
import com.fgroupboss.ai.psm.contractor.model.entity.ContractorAuditRecordEntity;
import com.fgroupboss.ai.psm.contractor.model.entity.ContractorCompanyEntity;
import com.fgroupboss.ai.psm.contractor.model.vo.ContractorCompanyVO;
import com.fgroupboss.ai.psm.contractor.service.impl.ContractorCompanyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContractorCompanyServiceImplTest {

    private ContractorCompanyMapper companyMapper;
    private ContractorAuditRecordMapper auditRecordMapper;
    private ContractorBlacklistMapper blacklistMapper;
    private ContractorCompanyService service;

    @BeforeEach
    void setUp() {
        companyMapper = mock(ContractorCompanyMapper.class);
        auditRecordMapper = mock(ContractorAuditRecordMapper.class);
        blacklistMapper = mock(ContractorBlacklistMapper.class);
        service = new ContractorCompanyServiceImpl(companyMapper, auditRecordMapper, blacklistMapper);
    }

    @Test
    void pageShouldReturnRecords() {
        ContractorCompanyEntity entity = company("DRAFT");
        when(companyMapper.countByTenant(eq(1L), eq(null), eq(null))).thenReturn(1L);
        when(companyMapper.listByTenant(eq(1L), eq(null), eq(null), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(entity));

        PageResult<ContractorCompanyVO> page = service.page(1L, null, null, 1, 20);

        assertEquals(1L, page.getTotal());
        assertEquals("CTR-001", page.getRecords().get(0).getCompanyCode());
    }

    @Test
    void approveShouldRejectDraftCompany() {
        when(companyMapper.selectById(2L)).thenReturn(company("DRAFT"));

        CompanyApproveRequest request = new CompanyApproveRequest();
        request.setPassed(true);

        assertThrows(BusinessException.class, () -> service.approve(1L, 2L, request, "admin"));
    }

    @Test
    void submitFromDraftShouldMoveToPendingReview() {
        ContractorCompanyEntity entity = company("DRAFT");
        entity.setId(2L);
        when(companyMapper.selectById(2L)).thenReturn(entity);

        ContractorCompanyVO result = service.submit(1L, 2L, "admin");

        assertEquals("PENDING_REVIEW", result.getStatus());
        verify(companyMapper).updateById(any(ContractorCompanyEntity.class));
        verify(auditRecordMapper).insert(any(ContractorAuditRecordEntity.class));
    }

    private ContractorCompanyEntity company(String status) {
        ContractorCompanyEntity entity = new ContractorCompanyEntity();
        entity.setId(1L);
        entity.setTenantId(1L);
        entity.setCompanyCode("CTR-001");
        entity.setCompanyName("试点单位");
        entity.setStatus(status);
        entity.setDeleted(0);
        entity.setBlacklistFlag(0);
        return entity;
    }
}
