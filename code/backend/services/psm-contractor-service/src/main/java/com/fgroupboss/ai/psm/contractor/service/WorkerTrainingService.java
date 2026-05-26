package com.fgroupboss.ai.psm.contractor.service;

import com.fgroupboss.ai.psm.contractor.model.dto.WorkerTrainingRequest;
import com.fgroupboss.ai.psm.contractor.model.vo.WorkerTrainingVO;

import java.util.List;

public interface WorkerTrainingService {

    List<WorkerTrainingVO> listByWorker(Long tenantId, Long workerId);

    WorkerTrainingVO create(Long workerId, WorkerTrainingRequest request, String operator);

    WorkerTrainingVO update(Long workerId, Long trainingId, WorkerTrainingRequest request, String operator);

    void delete(Long tenantId, Long workerId, Long trainingId, String operator);
}
