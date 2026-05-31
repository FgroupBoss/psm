package com.fgroupboss.ai.psm.operation.contractor.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.operation.contractor.mapper.ContractorViolationMapper;
import com.fgroupboss.ai.psm.operation.contractor.mapper.ContractorWorkerMapper;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.WorkerViolationRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.ContractorViolationEntity;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.ContractorWorkerEntity;
import com.fgroupboss.ai.psm.operation.contractor.model.vo.WorkerViolationVO;
import com.fgroupboss.ai.psm.operation.contractor.service.WorkerViolationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现方式：承载人员违章业务实现，基于 Mapper、远程客户端或支撑组件完成校验、状态流转和结果组装。
 */
@Service
@RequiredArgsConstructor
public class WorkerViolationServiceImpl implements WorkerViolationService {

    private final ContractorViolationMapper violationMapper;
    private final ContractorWorkerMapper workerMapper;

    /**
     * 实现方式：按承包商人员查询违章记录，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<WorkerViolationVO> listByWorker(Long tenantId, Long workerId) {
        requireWorker(tenantId, workerId);
        List<ContractorViolationEntity> entities = violationMapper.listByWorker(tenantId, workerId);
        List<WorkerViolationVO> records = new ArrayList<WorkerViolationVO>();
        for (ContractorViolationEntity entity : entities) {
            records.add(toVO(entity));
        }
        return records;
    }

    /**
     * 实现方式：创建业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public WorkerViolationVO create(Long workerId, WorkerViolationRequest request, String operator) {
        ContractorWorkerEntity worker = requireWorker(request.getTenantId(), workerId);
        ContractorViolationEntity entity = new ContractorViolationEntity();
        entity.setTenantId(request.getTenantId());
        entity.setWorkerId(workerId);
        entity.setCompanyId(request.getCompanyId() != null ? request.getCompanyId() : worker.getCompanyId());
        entity.setViolationTime(request.getViolationTime());
        entity.setViolationDesc(request.getViolationDesc().trim());
        entity.setSeverity(request.getSeverity());
        entity.setRectificationStatus(request.getRectificationStatus());
        entity.setDeleted(0);
        violationMapper.insert(entity);
        return toVO(entity);
    }

    private WorkerViolationVO toVO(ContractorViolationEntity entity) {
        WorkerViolationVO vo = new WorkerViolationVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setCompanyId(entity.getCompanyId());
        vo.setWorkerId(entity.getWorkerId());
        vo.setViolationTime(entity.getViolationTime());
        vo.setViolationDesc(entity.getViolationDesc());
        vo.setSeverity(entity.getSeverity());
        vo.setRectificationStatus(entity.getRectificationStatus());
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
}
