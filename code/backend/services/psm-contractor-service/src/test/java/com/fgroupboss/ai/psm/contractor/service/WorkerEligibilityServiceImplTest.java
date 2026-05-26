package com.fgroupboss.ai.psm.contractor.service;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.contractor.config.CompanyStatus;
import com.fgroupboss.ai.psm.contractor.config.EligibilityReasonCode;
import com.fgroupboss.ai.psm.contractor.config.WorkerStatus;
import com.fgroupboss.ai.psm.contractor.mapper.ContractorBlacklistMapper;
import com.fgroupboss.ai.psm.contractor.mapper.ContractorCompanyMapper;
import com.fgroupboss.ai.psm.contractor.mapper.ContractorWorkerMapper;
import com.fgroupboss.ai.psm.contractor.mapper.WorkerCertificateMapper;
import com.fgroupboss.ai.psm.contractor.mapper.WorkerTrainingMapper;
import com.fgroupboss.ai.psm.contractor.model.dto.EligibilityCheckRequest;
import com.fgroupboss.ai.psm.contractor.model.entity.ContractorCompanyEntity;
import com.fgroupboss.ai.psm.contractor.model.entity.ContractorWorkerEntity;
import com.fgroupboss.ai.psm.contractor.model.entity.WorkerCertificateEntity;
import com.fgroupboss.ai.psm.contractor.model.entity.WorkerTrainingRecordEntity;
import com.fgroupboss.ai.psm.contractor.model.vo.EligibilityCheckResultVO;
import com.fgroupboss.ai.psm.contractor.service.impl.WorkerEligibilityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WorkerEligibilityServiceImplTest {

    private ContractorCompanyMapper companyMapper;
    private ContractorWorkerMapper workerMapper;
    private ContractorBlacklistMapper blacklistMapper;
    private WorkerCertificateMapper certificateMapper;
    private WorkerTrainingMapper trainingMapper;
    private WorkerEligibilityService service;

    @BeforeEach
    void setUp() {
        companyMapper = mock(ContractorCompanyMapper.class);
        workerMapper = mock(ContractorWorkerMapper.class);
        blacklistMapper = mock(ContractorBlacklistMapper.class);
        certificateMapper = mock(WorkerCertificateMapper.class);
        trainingMapper = mock(WorkerTrainingMapper.class);
        service = new WorkerEligibilityServiceImpl(
                companyMapper, workerMapper, blacklistMapper, certificateMapper, trainingMapper);
    }

    @Test
    void approvedWorkerWithValidCertAndTrainingShouldPass() {
        mockApprovedCompany(1L);
        mockApprovedWorker(1L, 1L, "王强");
        when(certificateMapper.listByWorker(1L, 1L)).thenReturn(Collections.singletonList(validCert()));
        when(trainingMapper.listByWorker(1L, 1L)).thenReturn(Collections.singletonList(validTraining()));

        EligibilityCheckResultVO result = service.check(request(1L, Collections.singletonList(1L)));

        assertTrue(result.isPassed());
    }

    @Test
    void pendingReviewCompanyShouldFail() {
        ContractorCompanyEntity company = company(2L, CompanyStatus.PENDING_REVIEW.name());
        when(companyMapper.selectById(2L)).thenReturn(company);
        mockApprovedWorker(2L, 1L, "王强");
        when(certificateMapper.listByWorker(1L, 1L)).thenReturn(Collections.emptyList());
        when(trainingMapper.listByWorker(1L, 1L)).thenReturn(Collections.singletonList(validTraining()));

        EligibilityCheckResultVO result = service.check(request(2L, Collections.singletonList(1L)));

        assertFalse(result.isPassed());
        assertReasonCode(result, EligibilityReasonCode.COMPANY_NOT_APPROVED);
    }

    @Test
    void suspendedCompanyShouldFail() {
        ContractorCompanyEntity company = company(1L, CompanyStatus.SUSPENDED.name());
        when(companyMapper.selectById(1L)).thenReturn(company);
        mockApprovedWorker(1L, 1L, "王强");

        EligibilityCheckResultVO result = service.check(request(1L, Collections.singletonList(1L)));

        assertFalse(result.isPassed());
        assertReasonCode(result, EligibilityReasonCode.COMPANY_SUSPENDED);
    }

    @Test
    void blacklistedCompanyShouldFail() {
        ContractorCompanyEntity company = company(1L, CompanyStatus.BLACKLIST.name());
        company.setBlacklistFlag(1);
        when(companyMapper.selectById(1L)).thenReturn(company);
        mockApprovedWorker(1L, 1L, "王强");

        EligibilityCheckResultVO result = service.check(request(1L, Collections.singletonList(1L)));

        assertFalse(result.isPassed());
        assertReasonCode(result, EligibilityReasonCode.COMPANY_BLACKLIST);
    }

    @Test
    void pendingReviewWorkerShouldFail() {
        mockApprovedCompany(1L);
        ContractorWorkerEntity worker = worker(4L, 1L, WorkerStatus.PENDING_REVIEW.name(), "陈刚");
        when(workerMapper.selectById(4L)).thenReturn(worker);
        when(certificateMapper.listByWorker(1L, 4L)).thenReturn(Collections.emptyList());
        when(trainingMapper.listByWorker(1L, 4L)).thenReturn(Collections.emptyList());

        EligibilityCheckResultVO result = service.check(request(1L, Collections.singletonList(4L)));

        assertFalse(result.isPassed());
        assertWorkerReasonCode(result, EligibilityReasonCode.WORKER_NOT_APPROVED, 4L);
    }

    @Test
    void blacklistedWorkerShouldFail() {
        mockApprovedCompany(1L);
        ContractorWorkerEntity worker = worker(5L, 1L, WorkerStatus.BLACKLIST.name(), "周婷");
        when(workerMapper.selectById(5L)).thenReturn(worker);

        EligibilityCheckResultVO result = service.check(request(1L, Collections.singletonList(5L)));

        assertFalse(result.isPassed());
        assertWorkerReasonCode(result, EligibilityReasonCode.WORKER_BLACKLIST, 5L);
    }

    @Test
    void expiredCertificateShouldFailWithReadableMessage() {
        mockApprovedCompany(1L);
        mockApprovedWorker(1L, 1L, "王强");
        WorkerCertificateEntity expired = validCert();
        expired.setCertType("SPECIAL_WELDER");
        expired.setValidTo(LocalDate.now().minusDays(1));
        when(certificateMapper.listByWorker(1L, 1L)).thenReturn(Collections.singletonList(expired));
        when(trainingMapper.listByWorker(1L, 1L)).thenReturn(Collections.singletonList(validTraining()));

        EligibilityCheckResultVO result = service.check(request(1L, Collections.singletonList(1L)));

        assertFalse(result.isPassed());
        assertWorkerReasonCode(result, EligibilityReasonCode.WORKER_CERT_EXPIRED, 1L);
        assertTrue(result.getReasons().get(0).getMessage().contains("王强"));
        assertTrue(result.getReasons().get(0).getMessage().contains("SPECIAL_WELDER"));
    }

    @Test
    void failedTrainingShouldFail() {
        mockApprovedCompany(1L);
        mockApprovedWorker(1L, 1L, "王强");
        when(certificateMapper.listByWorker(1L, 1L)).thenReturn(Collections.singletonList(validCert()));
        WorkerTrainingRecordEntity failed = validTraining();
        failed.setTrainingResult("FAILED");
        when(trainingMapper.listByWorker(1L, 1L)).thenReturn(Collections.singletonList(failed));

        EligibilityCheckResultVO result = service.check(request(1L, Collections.singletonList(1L)));

        assertFalse(result.isPassed());
        assertWorkerReasonCode(result, EligibilityReasonCode.WORKER_TRAINING_INVALID, 1L);
    }

    @Test
    void expiredTrainingShouldFail() {
        mockApprovedCompany(1L);
        mockApprovedWorker(1L, 1L, "王强");
        when(certificateMapper.listByWorker(1L, 1L)).thenReturn(Collections.singletonList(validCert()));
        WorkerTrainingRecordEntity expired = validTraining();
        expired.setValidTo(LocalDate.now().minusDays(1));
        when(trainingMapper.listByWorker(1L, 1L)).thenReturn(Collections.singletonList(expired));

        EligibilityCheckResultVO result = service.check(request(1L, Collections.singletonList(1L)));

        assertFalse(result.isPassed());
        assertWorkerReasonCode(result, EligibilityReasonCode.WORKER_TRAINING_INVALID, 1L);
    }

    @Test
    void multipleWorkersShouldCollectAllReasons() {
        mockApprovedCompany(1L);
        mockApprovedWorker(1L, 1L, "王强");
        ContractorWorkerEntity worker2 = worker(2L, 1L, WorkerStatus.SUSPENDED.name(), "赵敏");
        when(workerMapper.selectById(2L)).thenReturn(worker2);
        when(certificateMapper.listByWorker(1L, 2L)).thenReturn(Collections.emptyList());
        when(trainingMapper.listByWorker(1L, 2L)).thenReturn(Collections.singletonList(validTraining()));
        when(certificateMapper.listByWorker(1L, 1L)).thenReturn(Collections.singletonList(validCert()));
        when(trainingMapper.listByWorker(1L, 1L)).thenReturn(Collections.singletonList(validTraining()));

        EligibilityCheckRequest req = request(1L, Arrays.asList(1L, 2L));
        EligibilityCheckResultVO result = service.check(req);

        assertFalse(result.isPassed());
        assertEquals(1, result.getReasons().size());
        assertWorkerReasonCode(result, EligibilityReasonCode.WORKER_SUSPENDED, 2L);
    }

    @Test
    void emptyWorkerIdsShouldFailValidation() {
        EligibilityCheckRequest req = request(1L, Collections.<Long>emptyList());
        assertThrows(BusinessException.class, () -> service.check(req));
    }

    private void mockApprovedCompany(Long companyId) {
        when(companyMapper.selectById(companyId)).thenReturn(company(companyId, CompanyStatus.APPROVED.name()));
        when(blacklistMapper.findActiveByTarget(1L, "COMPANY", companyId)).thenReturn(null);
    }

    private void mockApprovedWorker(Long companyId, Long workerId, String name) {
        ContractorWorkerEntity worker = worker(workerId, companyId, WorkerStatus.APPROVED.name(), name);
        when(workerMapper.selectById(workerId)).thenReturn(worker);
        when(blacklistMapper.findActiveByTarget(1L, "WORKER", workerId)).thenReturn(null);
    }

    private EligibilityCheckRequest request(Long companyId, java.util.List<Long> workerIds) {
        EligibilityCheckRequest req = new EligibilityCheckRequest();
        req.setTenantId(1L);
        req.setCompanyId(companyId);
        req.setWorkerIds(workerIds);
        req.setWorkType("HOT_WORK");
        return req;
    }

    private ContractorCompanyEntity company(Long id, String status) {
        ContractorCompanyEntity entity = new ContractorCompanyEntity();
        entity.setId(id);
        entity.setTenantId(1L);
        entity.setCompanyName("华东维保工程有限公司");
        entity.setStatus(status);
        entity.setDeleted(0);
        entity.setBlacklistFlag(0);
        return entity;
    }

    private ContractorWorkerEntity worker(Long id, Long companyId, String status, String name) {
        ContractorWorkerEntity entity = new ContractorWorkerEntity();
        entity.setId(id);
        entity.setTenantId(1L);
        entity.setCompanyId(companyId);
        entity.setName(name);
        entity.setAccessStatus(status);
        entity.setDeleted(0);
        return entity;
    }

    private WorkerCertificateEntity validCert() {
        WorkerCertificateEntity cert = new WorkerCertificateEntity();
        cert.setCertType("SPECIAL_WELDER");
        cert.setValidTo(LocalDate.now().plusYears(1));
        return cert;
    }

    private WorkerTrainingRecordEntity validTraining() {
        WorkerTrainingRecordEntity training = new WorkerTrainingRecordEntity();
        training.setTrainingResult("PASSED");
        training.setValidTo(LocalDate.now().plusYears(1));
        return training;
    }

    private void assertReasonCode(EligibilityCheckResultVO result, String code) {
        assertTrue(result.getReasons().stream().anyMatch(r -> code.equals(r.getCode())));
    }

    private void assertWorkerReasonCode(EligibilityCheckResultVO result, String code, Long workerId) {
        assertTrue(result.getReasons().stream()
                .anyMatch(r -> code.equals(r.getCode()) && workerId.equals(r.getWorkerId())));
    }
}
