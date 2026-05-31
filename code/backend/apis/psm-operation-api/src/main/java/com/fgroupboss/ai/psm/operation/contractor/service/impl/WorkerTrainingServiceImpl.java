package com.fgroupboss.ai.psm.operation.contractor.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.operation.contractor.mapper.ContractorWorkerMapper;
import com.fgroupboss.ai.psm.operation.contractor.mapper.WorkerTrainingMapper;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.WorkerTrainingRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.ContractorWorkerEntity;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.WorkerTrainingRecordEntity;
import com.fgroupboss.ai.psm.operation.contractor.model.vo.WorkerTrainingVO;
import com.fgroupboss.ai.psm.operation.contractor.service.ContractorWorkerService;
import com.fgroupboss.ai.psm.operation.contractor.service.WorkerTrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 实现方式：承载人员培训业务实现，基于 Mapper、远程客户端或支撑组件完成校验、状态流转和结果组装。
 */
@Service
@RequiredArgsConstructor
public class WorkerTrainingServiceImpl implements WorkerTrainingService {

    private final WorkerTrainingMapper trainingMapper;
    private final ContractorWorkerMapper workerMapper;
    private final ContractorWorkerService workerService;

    /**
     * 实现方式：按承包商人员查询培训记录，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<WorkerTrainingVO> listByWorker(Long tenantId, Long workerId) {
        requireWorker(tenantId, workerId);
        List<WorkerTrainingRecordEntity> entities = trainingMapper.listByWorker(tenantId, workerId);
        List<WorkerTrainingVO> records = new ArrayList<WorkerTrainingVO>();
        for (WorkerTrainingRecordEntity entity : entities) {
            records.add(toVO(entity));
        }
        return records;
    }

    /**
     * 实现方式：创建业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public WorkerTrainingVO create(Long workerId, WorkerTrainingRequest request, String operator) {
        requireWorker(request.getTenantId(), workerId);
        WorkerTrainingRecordEntity entity = new WorkerTrainingRecordEntity();
        entity.setTenantId(request.getTenantId());
        entity.setWorkerId(workerId);
        applyRequest(entity, request);
        entity.setDeleted(0);
        trainingMapper.insert(entity);
        workerService.refreshCompliance(request.getTenantId(), workerId);
        return toVO(entity);
    }

    /**
     * 实现方式：更新业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public WorkerTrainingVO update(Long workerId, Long trainingId, WorkerTrainingRequest request, String operator) {
        WorkerTrainingRecordEntity entity = requireTraining(request.getTenantId(), workerId, trainingId);
        applyRequest(entity, request);
        trainingMapper.updateById(entity);
        workerService.refreshCompliance(request.getTenantId(), workerId);
        return toVO(entity);
    }

    /**
     * 实现方式：删除业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public void delete(Long tenantId, Long workerId, Long trainingId, String operator) {
        WorkerTrainingRecordEntity entity = requireTraining(tenantId, workerId, trainingId);
        entity.setDeleted(1);
        trainingMapper.updateById(entity);
        workerService.refreshCompliance(tenantId, workerId);
    }

    private void applyRequest(WorkerTrainingRecordEntity entity, WorkerTrainingRequest request) {
        entity.setTrainingName(request.getTrainingName().trim());
        entity.setTrainingResult(request.getTrainingResult().trim());
        entity.setValidFrom(request.getValidFrom());
        entity.setValidTo(request.getValidTo());
        entity.setFileId(request.getFileId());
    }

    private WorkerTrainingVO toVO(WorkerTrainingRecordEntity entity) {
        WorkerTrainingVO vo = new WorkerTrainingVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setWorkerId(entity.getWorkerId());
        vo.setTrainingName(entity.getTrainingName());
        vo.setTrainingResult(entity.getTrainingResult());
        vo.setValidFrom(entity.getValidFrom());
        vo.setValidTo(entity.getValidTo());
        vo.setFileId(entity.getFileId());
        boolean expired = entity.getValidTo() != null && entity.getValidTo().isBefore(LocalDate.now());
        vo.setExpired(expired);
        vo.setValid("PASSED".equals(entity.getTrainingResult()) && !expired);
        return vo;
    }

    private ContractorWorkerEntity requireWorker(Long tenantId, Long workerId) {
        ContractorWorkerEntity worker = workerMapper.selectById(workerId);
        if (worker == null || worker.getDeleted() != null && worker.getDeleted() == 1
                || !tenantId.equals(worker.getTenantId())) {
            throw new BusinessException(404, "contractor worker not found");
        }
        return worker;
    }

    private WorkerTrainingRecordEntity requireTraining(Long tenantId, Long workerId, Long trainingId) {
        WorkerTrainingRecordEntity entity = trainingMapper.selectById(trainingId);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId()) || !workerId.equals(entity.getWorkerId())) {
            throw new BusinessException(404, "worker training record not found");
        }
        return entity;
    }
}
