package com.fgroupboss.ai.psm.contractor.service;

import com.fgroupboss.ai.psm.contractor.model.dto.WorkerViolationRequest;
import com.fgroupboss.ai.psm.contractor.model.vo.WorkerViolationVO;

import java.util.List;

public interface WorkerViolationService {

    List<WorkerViolationVO> listByWorker(Long tenantId, Long workerId);

    WorkerViolationVO create(Long workerId, WorkerViolationRequest request, String operator);
}
