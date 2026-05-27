package com.fgroupboss.ai.psm.contractor.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.contractor.mapper.ContractorWorkerMapper;
import com.fgroupboss.ai.psm.contractor.mapper.WorkerCertificateMapper;
import com.fgroupboss.ai.psm.contractor.model.dto.WorkerCertificateRequest;
import com.fgroupboss.ai.psm.contractor.model.entity.ContractorWorkerEntity;
import com.fgroupboss.ai.psm.contractor.model.entity.WorkerCertificateEntity;
import com.fgroupboss.ai.psm.contractor.model.vo.WorkerCertificateVO;
import com.fgroupboss.ai.psm.contractor.service.ContractorWorkerService;
import com.fgroupboss.ai.psm.contractor.service.WorkerCertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 实现方式：承载人员证书业务实现，基于 Mapper、远程客户端或支撑组件完成校验、状态流转和结果组装。
 */
@Service
@RequiredArgsConstructor
public class WorkerCertificateServiceImpl implements WorkerCertificateService {

    private final WorkerCertificateMapper certificateMapper;
    private final ContractorWorkerMapper workerMapper;
    private final ContractorWorkerService workerService;

    /**
     * 实现方式：按承包商人员查询证书记录，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<WorkerCertificateVO> listByWorker(Long tenantId, Long workerId) {
        requireWorker(tenantId, workerId);
        List<WorkerCertificateEntity> entities = certificateMapper.listByWorker(tenantId, workerId);
        List<WorkerCertificateVO> records = new ArrayList<WorkerCertificateVO>();
        for (WorkerCertificateEntity entity : entities) {
            records.add(toVO(entity));
        }
        return records;
    }

    /**
     * 实现方式：创建业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public WorkerCertificateVO create(Long workerId, WorkerCertificateRequest request, String operator) {
        requireWorker(request.getTenantId(), workerId);
        WorkerCertificateEntity entity = new WorkerCertificateEntity();
        entity.setTenantId(request.getTenantId());
        entity.setWorkerId(workerId);
        applyRequest(entity, request);
        entity.setStatus("ENABLED");
        entity.setDeleted(0);
        certificateMapper.insert(entity);
        workerService.refreshCompliance(request.getTenantId(), workerId);
        return toVO(entity);
    }

    /**
     * 实现方式：更新业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public WorkerCertificateVO update(Long workerId, Long certId, WorkerCertificateRequest request, String operator) {
        WorkerCertificateEntity entity = requireCertificate(request.getTenantId(), workerId, certId);
        applyRequest(entity, request);
        certificateMapper.updateById(entity);
        workerService.refreshCompliance(request.getTenantId(), workerId);
        return toVO(entity);
    }

    /**
     * 实现方式：删除业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public void delete(Long tenantId, Long workerId, Long certId, String operator) {
        WorkerCertificateEntity entity = requireCertificate(tenantId, workerId, certId);
        entity.setDeleted(1);
        certificateMapper.updateById(entity);
        workerService.refreshCompliance(tenantId, workerId);
    }

    private void applyRequest(WorkerCertificateEntity entity, WorkerCertificateRequest request) {
        entity.setCertType(request.getCertType().trim());
        entity.setCertNo(request.getCertNo());
        entity.setValidFrom(request.getValidFrom());
        entity.setValidTo(request.getValidTo());
        entity.setFileId(request.getFileId());
    }

    private WorkerCertificateVO toVO(WorkerCertificateEntity entity) {
        WorkerCertificateVO vo = new WorkerCertificateVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setWorkerId(entity.getWorkerId());
        vo.setCertType(entity.getCertType());
        vo.setCertNo(entity.getCertNo());
        vo.setValidFrom(entity.getValidFrom());
        vo.setValidTo(entity.getValidTo());
        vo.setFileId(entity.getFileId());
        vo.setStatus(entity.getStatus());
        vo.setExpired(entity.getValidTo() != null && entity.getValidTo().isBefore(LocalDate.now()));
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

    private WorkerCertificateEntity requireCertificate(Long tenantId, Long workerId, Long certId) {
        WorkerCertificateEntity entity = certificateMapper.selectById(certId);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId()) || !workerId.equals(entity.getWorkerId())) {
            throw new BusinessException(404, "worker certificate not found");
        }
        return entity;
    }
}
