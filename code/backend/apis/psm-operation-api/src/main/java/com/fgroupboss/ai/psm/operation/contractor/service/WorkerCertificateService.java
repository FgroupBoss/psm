package com.fgroupboss.ai.psm.operation.contractor.service;

import com.fgroupboss.ai.psm.operation.contractor.model.dto.WorkerCertificateRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.vo.WorkerCertificateVO;

import java.util.List;

public interface WorkerCertificateService {

    List<WorkerCertificateVO> listByWorker(Long tenantId, Long workerId);

    WorkerCertificateVO create(Long workerId, WorkerCertificateRequest request, String operator);

    WorkerCertificateVO update(Long workerId, Long certId, WorkerCertificateRequest request, String operator);

    void delete(Long tenantId, Long workerId, Long certId, String operator);
}
