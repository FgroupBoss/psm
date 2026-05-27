package com.fgroupboss.ai.psm.workpermit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.workpermit.client.AlarmAreaActiveClient;
import com.fgroupboss.ai.psm.workpermit.client.ContractorEligibilityClient;
import com.fgroupboss.ai.psm.workpermit.client.dto.AlarmAreaActiveCheckRequest;
import com.fgroupboss.ai.psm.workpermit.client.dto.AlarmAreaActiveCheckResult;
import com.fgroupboss.ai.psm.workpermit.client.dto.ContractorEligibilityReason;
import com.fgroupboss.ai.psm.workpermit.client.dto.ContractorEligibilityRequest;
import com.fgroupboss.ai.psm.workpermit.client.dto.ContractorEligibilityResult;
import com.fgroupboss.ai.psm.workpermit.config.PermitCheckPoint;
import com.fgroupboss.ai.psm.workpermit.config.WorkPermitStatus;
import com.fgroupboss.ai.psm.workpermit.config.WorkPermitStatusTransition;
import com.fgroupboss.ai.psm.workpermit.mapper.GasTestRecordMapper;
import com.fgroupboss.ai.psm.workpermit.mapper.MobileDraftSyncMapper;
import com.fgroupboss.ai.psm.workpermit.mapper.PermitAcceptanceRecordMapper;
import com.fgroupboss.ai.psm.workpermit.mapper.PermitApprovalRecordMapper;
import com.fgroupboss.ai.psm.workpermit.mapper.PermitArchiveSnapshotMapper;
import com.fgroupboss.ai.psm.workpermit.mapper.PermitMonitorRecordMapper;
import com.fgroupboss.ai.psm.workpermit.mapper.PermitRiskAnalysisMapper;
import com.fgroupboss.ai.psm.workpermit.mapper.PermitSafetyMeasureMapper;
import com.fgroupboss.ai.psm.workpermit.mapper.PermitSiteConfirmMapper;
import com.fgroupboss.ai.psm.workpermit.mapper.PermitStatusLogMapper;
import com.fgroupboss.ai.psm.workpermit.mapper.WorkPermitMapper;
import com.fgroupboss.ai.psm.workpermit.mapper.WorkPermitWorkerMapper;
import com.fgroupboss.ai.psm.workpermit.model.dto.AcceptanceRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.CheckInRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.GasTestRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.MobileDraftSyncRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.MonitorRecordRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.PermitActionRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.PreCheckRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.RiskAnalysisRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.SafetyMeasureRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.SitePermitRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.WorkPermitRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.WorkPermitWorkerRequest;
import com.fgroupboss.ai.psm.workpermit.model.entity.GasTestRecordEntity;
import com.fgroupboss.ai.psm.workpermit.model.entity.MobileDraftSyncEntity;
import com.fgroupboss.ai.psm.workpermit.model.entity.PermitAcceptanceRecordEntity;
import com.fgroupboss.ai.psm.workpermit.model.entity.PermitApprovalRecordEntity;
import com.fgroupboss.ai.psm.workpermit.model.entity.PermitArchiveSnapshotEntity;
import com.fgroupboss.ai.psm.workpermit.model.entity.PermitMonitorRecordEntity;
import com.fgroupboss.ai.psm.workpermit.model.entity.PermitRiskAnalysisEntity;
import com.fgroupboss.ai.psm.workpermit.model.entity.PermitSafetyMeasureEntity;
import com.fgroupboss.ai.psm.workpermit.model.entity.PermitSiteConfirmEntity;
import com.fgroupboss.ai.psm.workpermit.model.entity.PermitStatusLogEntity;
import com.fgroupboss.ai.psm.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.workpermit.model.entity.WorkPermitWorkerEntity;
import com.fgroupboss.ai.psm.workpermit.model.vo.AcceptanceRecordVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.ApprovalRecordVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.GasTestVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.MobileDraftSyncResultVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.MonitorRecordVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.PreCheckResultVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.RiskAnalysisVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.SafetyMeasureVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.SiteConfirmVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.TimelineItemVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.WorkPermitDetailVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.WorkPermitHealthVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.WorkPermitVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.WorkPermitWorkerVO;
import com.fgroupboss.ai.psm.workpermit.service.WorkPermitService;
import com.fgroupboss.ai.psm.workpermit.support.DefaultSafetyMeasuresSupport;
import com.fgroupboss.ai.psm.workpermit.support.WorkPermitAuditSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Slf4j
/**
 * 实现方式：承载作业票业务实现，基于 Mapper、远程客户端或支撑组件完成校验、状态流转和结果组装。
 */
@Service
@RequiredArgsConstructor
public class WorkPermitServiceImpl implements WorkPermitService {

    private static final String CONFIRM_STATUS_CONFIRMED = "CONFIRMED";
    private static final String WORKER_TYPE_CONTRACTOR = "CONTRACTOR";
    private static final String DEFAULT_MIN_ALARM_LEVEL = "LEVEL_2";
    private static final String UPSTREAM_UNAVAILABLE = "UPSTREAM_UNAVAILABLE";

    private final WorkPermitMapper workPermitMapper;
    private final WorkPermitWorkerMapper workerMapper;
    private final PermitRiskAnalysisMapper riskAnalysisMapper;
    private final PermitSafetyMeasureMapper safetyMeasureMapper;
    private final PermitApprovalRecordMapper approvalRecordMapper;
    private final GasTestRecordMapper gasTestRecordMapper;
    private final PermitSiteConfirmMapper siteConfirmMapper;
    private final PermitMonitorRecordMapper monitorRecordMapper;
    private final PermitAcceptanceRecordMapper acceptanceRecordMapper;
    private final PermitStatusLogMapper statusLogMapper;
    private final PermitArchiveSnapshotMapper archiveSnapshotMapper;
    private final MobileDraftSyncMapper mobileDraftSyncMapper;
    private final WorkPermitAuditSupport auditSupport;
    private final DefaultSafetyMeasuresSupport defaultSafetyMeasuresSupport;
    private final ContractorEligibilityClient eligibilityClient;
    private final AlarmAreaActiveClient alarmAreaActiveClient;

    @Value("${psm.gas-test-valid-minutes:30}")
    private int gasTestValidMinutes;

    /**
     * 实现方式：查询服务健康状态，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public WorkPermitHealthVO health() {
        LambdaQueryWrapper<WorkPermitEntity> wrapper = new LambdaQueryWrapper<WorkPermitEntity>()
                .eq(WorkPermitEntity::getDeleted, 0);
        long count = workPermitMapper.selectCount(wrapper);
        return new WorkPermitHealthVO("psm-work-permit-service", "work-permit", "1.0.0-batch6", count);
    }

    /**
     * 实现方式：分页查询业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public PageResult<WorkPermitVO> page(Long tenantId, String keyword, String status, String workType,
                                         Long areaId, Long hazardId, int pageNo, int pageSize) {
        requireTenantId(tenantId);
        PageSpec spec = normalizePage(pageNo, pageSize);
        LambdaQueryWrapper<WorkPermitEntity> wrapper = basePermitQuery(tenantId)
                .eq(StringUtils.hasText(status), WorkPermitEntity::getStatus, normalizeText(status))
                .eq(StringUtils.hasText(workType), WorkPermitEntity::getWorkType, normalizeText(workType))
                .eq(areaId != null, WorkPermitEntity::getAreaId, areaId)
                .eq(hazardId != null, WorkPermitEntity::getHazardId, hazardId);
        applyKeyword(wrapper, keyword);
        wrapper.orderByDesc(WorkPermitEntity::getUpdatedAt);
        long total = workPermitMapper.selectCount(wrapper);
        List<WorkPermitEntity> entities = total == 0
                ? new ArrayList<WorkPermitEntity>()
                : workPermitMapper.selectList(wrapper.last("limit " + spec.offset + "," + spec.pageSize));
        List<WorkPermitVO> records = new ArrayList<WorkPermitVO>();
        for (WorkPermitEntity entity : entities) {
            records.add(toVO(entity));
        }
        return new PageResult<WorkPermitVO>(total, spec.pageNo, spec.pageSize, records);
    }

    /**
     * 实现方式：按重大危险源查询关联作业票，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<WorkPermitVO> listByHazard(Long tenantId, Long hazardId) {
        requireTenantId(tenantId);
        if (hazardId == null) {
            throw new BusinessException(400, "hazardId is required");
        }
        LambdaQueryWrapper<WorkPermitEntity> wrapper = basePermitQuery(tenantId)
                .eq(WorkPermitEntity::getHazardId, hazardId)
                .orderByDesc(WorkPermitEntity::getUpdatedAt);
        List<WorkPermitEntity> entities = workPermitMapper.selectList(wrapper);
        List<WorkPermitVO> result = new ArrayList<WorkPermitVO>();
        for (WorkPermitEntity entity : entities) {
            result.add(toVO(entity));
        }
        return result;
    }

    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public WorkPermitDetailVO getDetail(Long tenantId, Long id) {
        WorkPermitEntity entity = requirePermit(tenantId, id);
        WorkPermitDetailVO detail = new WorkPermitDetailVO();
        detail.setPermit(toVO(entity));
        detail.setWorkers(listWorkers(tenantId, id));
        detail.setRiskAnalyses(listRiskAnalysis(tenantId, id));
        detail.setSafetyMeasures(listSafetyMeasures(tenantId, id));
        detail.setApprovalRecords(listApprovalRecords(tenantId, id));
        detail.setGasTests(listGasTests(tenantId, id));
        detail.setSiteConfirms(listSiteConfirms(tenantId, id));
        detail.setMonitorRecords(listMonitorRecords(tenantId, id));
        detail.setAcceptanceRecords(listAcceptanceRecords(tenantId, id));
        return detail;
    }

    /**
     * 实现方式：创建业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public WorkPermitVO create(WorkPermitRequest request, String operator) {
        requireTenantId(request.getTenantId());
        WorkPermitEntity entity = new WorkPermitEntity();
        entity.setTenantId(request.getTenantId());
        entity.setPermitNo(generatePermitNo());
        entity.setWorkType(normalizeRequired(request.getWorkType(), "workType"));
        entity.setStatus(WorkPermitStatus.DRAFT.name());
        applyPermitRequest(entity, request);
        entity.setCreatedBy(normalizeOperator(operator));
        entity.setUpdatedBy(normalizeOperator(operator));
        entity.setDeleted(0);
        workPermitMapper.insert(entity);
        defaultSafetyMeasuresSupport.seedDefaultMeasures(entity.getTenantId(), entity.getId(), entity.getWorkType());
        auditSupport.writeStatusChange(entity.getTenantId(), entity.getId(), "CREATE", null,
                operator, null, entity.getStatus());
        return toVO(entity);
    }

    /**
     * 实现方式：更新业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public WorkPermitVO update(Long id, WorkPermitRequest request, String operator) {
        WorkPermitEntity entity = requirePermit(request.getTenantId(), id);
        assertEditable(entity);
        applyPermitRequest(entity, request);
        entity.setUpdatedBy(normalizeOperator(operator));
        workPermitMapper.updateById(entity);
        return toVO(entity);
    }

    /**
     * 实现方式：查询作业人员，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<WorkPermitWorkerVO> listWorkers(Long tenantId, Long id) {
        requirePermit(tenantId, id);
        List<WorkPermitWorkerEntity> entities = workerMapper.selectList(workerQuery(tenantId, id));
        List<WorkPermitWorkerVO> result = new ArrayList<WorkPermitWorkerVO>();
        for (WorkPermitWorkerEntity entity : entities) {
            result.add(toWorkerVO(entity));
        }
        return result;
    }

    /**
     * 实现方式：新增作业人员，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public WorkPermitWorkerVO addWorker(Long tenantId, Long id, WorkPermitWorkerRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, id);
        assertEditable(permit);
        WorkPermitWorkerEntity entity = new WorkPermitWorkerEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(id);
        entity.setWorkerType(normalizeRequired(request.getWorkerType(), "workerType"));
        entity.setWorkerId(request.getWorkerId());
        entity.setWorkerName(normalizeRequired(request.getWorkerName(), "workerName"));
        entity.setRoleCode(normalizeText(request.getRoleCode()));
        entity.setCompanyId(request.getCompanyId());
        entity.setDeleted(0);
        workerMapper.insert(entity);
        return toWorkerVO(entity);
    }

    /**
     * 实现方式：移除作业人员，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void removeWorker(Long tenantId, Long id, Long workerId, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, id);
        assertEditable(permit);
        WorkPermitWorkerEntity entity = workerMapper.selectById(workerId);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() != 0
                || !tenantId.equals(entity.getTenantId()) || !id.equals(entity.getWorkPermitId())) {
            throw new BusinessException(404, "worker not found");
        }
        entity.setDeleted(1);
        workerMapper.updateById(entity);
    }

    /**
     * 实现方式：提交审批，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public WorkPermitVO submit(Long tenantId, Long id, String operator) {
        WorkPermitEntity entity = requirePermit(tenantId, id);
        PreCheckResultVO check = runPreCheck(entity, PermitCheckPoint.SUBMIT, false);
        if (!check.isPassed()) {
            throw new BusinessException(409, joinReasons(check.getReasons()));
        }
        return transition(entity, WorkPermitStatusTransition::submitTarget, "SUBMIT", null, operator);
    }

    /**
     * 实现方式：审批业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public WorkPermitVO approve(Long tenantId, Long id, PermitActionRequest request, String operator) {
        WorkPermitEntity entity = requirePermit(tenantId, id);
        WorkPermitVO result = transition(entity, WorkPermitStatusTransition::approveTarget, "APPROVE",
                actionOpinion(request), operator);
        saveApprovalRecord(tenantId, id, "APPROVE", request, operator);
        return result;
    }

    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public WorkPermitVO returnPermit(Long tenantId, Long id, PermitActionRequest request, String operator) {
        WorkPermitEntity entity = requirePermit(tenantId, id);
        WorkPermitVO result = transition(entity, WorkPermitStatusTransition::returnTarget, "RETURN",
                actionOpinion(request), operator);
        saveApprovalRecord(tenantId, id, "RETURN", request, operator);
        return result;
    }

    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public WorkPermitVO reject(Long tenantId, Long id, PermitActionRequest request, String operator) {
        WorkPermitEntity entity = requirePermit(tenantId, id);
        String reason = resolveReason(request);
        entity.setRejectReason(reason);
        WorkPermitVO result = transition(entity, WorkPermitStatusTransition::rejectTarget, "REJECT", reason, operator);
        saveApprovalRecord(tenantId, id, "REJECT", request, operator);
        archivePermit(entity, operator);
        return result;
    }

    /**
     * 实现方式：查询风险分析列表，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<RiskAnalysisVO> listRiskAnalysis(Long tenantId, Long id) {
        requirePermit(tenantId, id);
        List<PermitRiskAnalysisEntity> entities = riskAnalysisMapper.selectList(
                riskQuery(tenantId, id).orderByDesc(PermitRiskAnalysisEntity::getAnalyzedAt));
        List<RiskAnalysisVO> result = new ArrayList<RiskAnalysisVO>();
        for (PermitRiskAnalysisEntity entity : entities) {
            result.add(toRiskVO(entity));
        }
        return result;
    }

    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public RiskAnalysisVO saveRiskAnalysis(Long tenantId, Long id, RiskAnalysisRequest request, String operator) {
        requirePermit(tenantId, id);
        PermitRiskAnalysisEntity entity = new PermitRiskAnalysisEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(id);
        entity.setHazardDesc(normalizeText(request.getHazardDesc()));
        entity.setControlMeasure(normalizeText(request.getControlMeasure()));
        entity.setRiskLevel(normalizeText(request.getRiskLevel()));
        entity.setAnalystName(StringUtils.hasText(request.getAnalystName())
                ? request.getAnalystName().trim() : normalizeOperator(operator));
        entity.setAnalyzedAt(new Date());
        entity.setDeleted(0);
        riskAnalysisMapper.insert(entity);
        return toRiskVO(entity);
    }

    /**
     * 实现方式：查询安全措施，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<SafetyMeasureVO> listSafetyMeasures(Long tenantId, Long id) {
        requirePermit(tenantId, id);
        List<PermitSafetyMeasureEntity> entities = safetyMeasureMapper.selectList(
                measureQuery(tenantId, id).orderByAsc(PermitSafetyMeasureEntity::getMeasureCode));
        List<SafetyMeasureVO> result = new ArrayList<SafetyMeasureVO>();
        for (PermitSafetyMeasureEntity entity : entities) {
            result.add(toMeasureVO(entity));
        }
        return result;
    }

    /**
     * 实现方式：确认安全措施，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public SafetyMeasureVO confirmSafetyMeasure(Long tenantId, Long id, Long measureId,
                                                  SafetyMeasureRequest request, String operator) {
        requirePermit(tenantId, id);
        PermitSafetyMeasureEntity entity = safetyMeasureMapper.selectById(measureId);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() != 0
                || !tenantId.equals(entity.getTenantId()) || !id.equals(entity.getWorkPermitId())) {
            throw new BusinessException(404, "safety measure not found");
        }
        String confirmStatus = normalizeText(request.getConfirmStatus());
        entity.setConfirmStatus(StringUtils.hasText(confirmStatus) ? confirmStatus : CONFIRM_STATUS_CONFIRMED);
        entity.setConfirmBy(normalizeOperator(operator));
        entity.setConfirmAt(new Date());
        entity.setRemark(normalizeText(request.getRemark()));
        entity.setAttachmentRef(normalizeText(request.getAttachmentRef()));
        safetyMeasureMapper.updateById(entity);
        return toMeasureVO(entity);
    }

    /**
     * 实现方式：查询气体检测记录，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<GasTestVO> listGasTests(Long tenantId, Long id) {
        requirePermit(tenantId, id);
        List<GasTestRecordEntity> entities = gasTestRecordMapper.selectList(
                gasQuery(tenantId, id).orderByDesc(GasTestRecordEntity::getTestedAt));
        List<GasTestVO> result = new ArrayList<GasTestVO>();
        for (GasTestRecordEntity entity : entities) {
            result.add(toGasVO(entity));
        }
        return result;
    }

    /**
     * 实现方式：新增气体检测记录，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public GasTestVO addGasTest(Long tenantId, Long id, GasTestRequest request, String operator) {
        requirePermit(tenantId, id);
        GasTestRecordEntity entity = new GasTestRecordEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(id);
        entity.setGasName(normalizeRequired(request.getGasName(), "gasName"));
        entity.setMeasuredValue(normalizeText(request.getMeasuredValue()));
        entity.setUnit(normalizeText(request.getUnit()));
        entity.setQualified(Boolean.TRUE.equals(request.getQualified()) ? 1 : 0);
        entity.setTestedAt(request.getTestedAt() == null ? new Date() : request.getTestedAt());
        entity.setTesterName(StringUtils.hasText(request.getTesterName())
                ? request.getTesterName().trim() : normalizeOperator(operator));
        entity.setRemark(normalizeText(request.getRemark()));
        entity.setDeleted(0);
        gasTestRecordMapper.insert(entity);
        return toGasVO(entity);
    }

    /**
     * 实现方式：执行作业前置校验，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public PreCheckResultVO preCheck(Long tenantId, Long id, PreCheckRequest request) {
        WorkPermitEntity entity = requirePermit(tenantId, id);
        PermitCheckPoint checkPoint = PermitCheckPoint.from(request.getCheckPoint());
        return runPreCheck(entity, checkPoint, checkPoint == PermitCheckPoint.SITE_PERMIT);
    }

    /**
     * 实现方式：提交现场许可，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public WorkPermitVO sitePermit(Long tenantId, Long id, SitePermitRequest request, String operator) {
        WorkPermitEntity entity = requirePermit(tenantId, id);
        PreCheckResultVO check = runPreCheck(entity, PermitCheckPoint.SITE_PERMIT, true);
        if (!check.isPassed()) {
            throw new BusinessException(409, joinReasons(check.getReasons()));
        }
        entity.setActualStartAt(new Date());
        WorkPermitVO result = transition(entity, WorkPermitStatusTransition::sitePermitTarget, "SITE_PERMIT",
                normalizeText(request.getRemark()), operator);
        saveSiteConfirm(tenantId, id, "SIGNATURE", request.getSignatureText(), request.getLocationText(),
                null, null, operator);
        return result;
    }

    /**
     * 实现方式：提交现场签到，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public SiteConfirmVO checkIn(Long tenantId, Long id, CheckInRequest request, String operator) {
        requirePermit(tenantId, id);
        PermitSiteConfirmEntity entity = saveSiteConfirm(tenantId, id, "CHECK_IN", request.getConfirmContent(),
                request.getLocationText(), request.getScanCode(), request.getTerminalId(), operator);
        return toSiteConfirmVO(entity);
    }

    /**
     * 实现方式：查询监护记录，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<MonitorRecordVO> listMonitorRecords(Long tenantId, Long id) {
        requirePermit(tenantId, id);
        List<PermitMonitorRecordEntity> entities = monitorRecordMapper.selectList(
                monitorQuery(tenantId, id).orderByDesc(PermitMonitorRecordEntity::getRecordedAt));
        List<MonitorRecordVO> result = new ArrayList<MonitorRecordVO>();
        for (PermitMonitorRecordEntity entity : entities) {
            result.add(toMonitorVO(entity));
        }
        return result;
    }

    /**
     * 实现方式：新增监护记录，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public MonitorRecordVO addMonitorRecord(Long tenantId, Long id, MonitorRecordRequest request, String operator) {
        requirePermit(tenantId, id);
        PermitMonitorRecordEntity entity = new PermitMonitorRecordEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(id);
        entity.setRecordType(normalizeRequired(request.getRecordType(), "recordType"));
        entity.setContent(normalizeText(request.getContent()));
        entity.setAbnormalFlag(Boolean.TRUE.equals(request.getAbnormalFlag()) ? 1 : 0);
        entity.setAttachmentRef(normalizeText(request.getAttachmentRef()));
        entity.setOperatorName(normalizeOperator(operator));
        entity.setRecordedAt(new Date());
        monitorRecordMapper.insert(entity);
        return toMonitorVO(entity);
    }

    /**
     * 实现方式：挂起业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public WorkPermitVO suspend(Long tenantId, Long id, PermitActionRequest request, String operator) {
        WorkPermitEntity entity = requirePermit(tenantId, id);
        return transition(entity, WorkPermitStatusTransition::suspendTarget, "SUSPEND", actionOpinion(request), operator);
    }

    /**
     * 实现方式：恢复业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public WorkPermitVO resume(Long tenantId, Long id, PermitActionRequest request, String operator) {
        WorkPermitEntity entity = requirePermit(tenantId, id);
        return transition(entity, WorkPermitStatusTransition::resumeTarget, "RESUME", actionOpinion(request), operator);
    }

    /**
     * 实现方式：终止作业票，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public WorkPermitVO terminate(Long tenantId, Long id, PermitActionRequest request, String operator) {
        WorkPermitEntity entity = requirePermit(tenantId, id);
        String reason = resolveReason(request);
        entity.setRejectReason(reason);
        entity.setActualEndAt(new Date());
        WorkPermitVO result = transition(entity, WorkPermitStatusTransition::terminateTarget, "TERMINATE", reason, operator);
        archivePermit(entity, operator);
        return result;
    }

    /**
     * 实现方式：提交验收，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public WorkPermitVO acceptance(Long tenantId, Long id, AcceptanceRequest request, String operator) {
        WorkPermitEntity entity = requirePermit(tenantId, id);
        WorkPermitStatus current = WorkPermitStatus.from(entity.getStatus());
        saveAcceptanceRecord(tenantId, id, request, operator);
        if (current == WorkPermitStatus.IN_PROGRESS) {
            return transition(entity, WorkPermitStatusTransition::requestAcceptanceTarget, "REQUEST_ACCEPTANCE",
                    normalizeText(request.getOpinion()), operator);
        }
        if (current == WorkPermitStatus.PENDING_ACCEPTANCE) {
            entity.setActualEndAt(new Date());
            WorkPermitVO result = transition(entity, WorkPermitStatusTransition::acceptanceTarget, "ACCEPTANCE",
                    normalizeText(request.getOpinion()), operator);
            archivePermit(entity, operator);
            return result;
        }
        throw new BusinessException(409, "当前状态不允许验收");
    }

    /**
     * 实现方式：查询作业票时间线，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<TimelineItemVO> timeline(Long tenantId, Long id) {
        requirePermit(tenantId, id);
        List<TimelineItemVO> items = new ArrayList<TimelineItemVO>();
        appendStatusLogs(tenantId, id, items);
        appendApprovalRecords(tenantId, id, items);
        appendSiteConfirms(tenantId, id, items);
        appendMonitorRecords(tenantId, id, items);
        appendAcceptanceRecords(tenantId, id, items);
        Collections.sort(items, new Comparator<TimelineItemVO>() {
    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
            @Override
            public int compare(TimelineItemVO a, TimelineItemVO b) {
                Date left = a.getOccurredAt();
                Date right = b.getOccurredAt();
                if (left == null && right == null) {
                    return 0;
                }
                if (left == null) {
                    return 1;
                }
                if (right == null) {
                    return -1;
                }
                return left.compareTo(right);
            }
        });
        return items;
    }

    /**
     * 实现方式：同步移动端草稿，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public MobileDraftSyncResultVO syncMobileDraft(MobileDraftSyncRequest request) {
        requireTenantId(request.getTenantId());
        String clientDraftId = normalizeRequired(request.getClientDraftId(), "clientDraftId");
        String draftType = normalizeRequired(request.getDraftType(), "draftType");
        LambdaQueryWrapper<MobileDraftSyncEntity> wrapper = new LambdaQueryWrapper<MobileDraftSyncEntity>()
                .eq(MobileDraftSyncEntity::getTenantId, request.getTenantId())
                .eq(MobileDraftSyncEntity::getClientDraftId, clientDraftId);
        MobileDraftSyncEntity existing = mobileDraftSyncMapper.selectOne(wrapper);
        Date now = new Date();
        if (existing == null) {
            MobileDraftSyncEntity entity = new MobileDraftSyncEntity();
            entity.setTenantId(request.getTenantId());
            entity.setClientDraftId(clientDraftId);
            entity.setUserId(request.getUserId());
            entity.setDraftType(draftType);
            entity.setBizId(request.getBizId());
            entity.setPayloadJson(request.getPayloadJson() == null ? "{}" : request.getPayloadJson());
            entity.setSyncStatus("SYNCED");
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            mobileDraftSyncMapper.insert(entity);
        } else {
            existing.setUserId(request.getUserId());
            existing.setDraftType(draftType);
            existing.setBizId(request.getBizId());
            existing.setPayloadJson(request.getPayloadJson() == null ? existing.getPayloadJson() : request.getPayloadJson());
            existing.setSyncStatus("SYNCED");
            existing.setLastError(null);
            existing.setUpdatedAt(now);
            mobileDraftSyncMapper.updateById(existing);
        }
        MobileDraftSyncResultVO result = new MobileDraftSyncResultVO();
        result.setClientDraftId(clientDraftId);
        result.setSyncStatus("SYNCED");
        result.setStorage("WORK_PERMIT_DB");
        return result;
    }

    private PreCheckResultVO runPreCheck(WorkPermitEntity permit, PermitCheckPoint checkPoint, boolean throwOnUpstreamFailure) {
        PreCheckResultVO result = new PreCheckResultVO();
        result.setPassed(true);
        checkRequiredMeasures(permit, result);
        checkEligibility(permit, checkPoint, throwOnUpstreamFailure, result);
        if (checkPoint == PermitCheckPoint.SITE_PERMIT) {
            checkAreaAlarm(permit, throwOnUpstreamFailure, result);
            checkGasTest(permit, result);
        }
        result.setPassed(result.getReasons().isEmpty());
        return result;
    }

    private void checkRequiredMeasures(WorkPermitEntity permit, PreCheckResultVO result) {
        List<PermitSafetyMeasureEntity> measures = safetyMeasureMapper.selectList(
                measureQuery(permit.getTenantId(), permit.getId())
                        .eq(PermitSafetyMeasureEntity::getRequiredFlag, 1));
        for (PermitSafetyMeasureEntity measure : measures) {
            if (!CONFIRM_STATUS_CONFIRMED.equals(measure.getConfirmStatus())) {
                result.getReasons().add("必需安全措施未确认: " + measure.getMeasureName());
            }
        }
    }

    private void checkEligibility(WorkPermitEntity permit, PermitCheckPoint checkPoint,
                                  boolean throwOnUpstreamFailure, PreCheckResultVO result) {
        if (permit.getContractorCompanyId() == null) {
            return;
        }
        List<Long> workerIds = collectContractorWorkerIds(permit.getTenantId(), permit.getId());
        if (workerIds.isEmpty()) {
            return;
        }
        try {
            ContractorEligibilityRequest request = new ContractorEligibilityRequest();
            request.setTenantId(permit.getTenantId());
            request.setCompanyId(permit.getContractorCompanyId());
            request.setWorkerIds(workerIds);
            request.setWorkType(permit.getWorkType());
            request.setCheckPoint(checkPoint.name());
            ContractorEligibilityResult eligibility = eligibilityClient.check(request);
            if (eligibility != null && !eligibility.isPassed() && eligibility.getReasons() != null) {
                for (ContractorEligibilityReason reason : eligibility.getReasons()) {
                    result.getReasons().add(formatEligibilityReason(reason));
                }
            }
        } catch (RestClientException ex) {
            log.warn("eligibility-check failed permitId={} reason={}", permit.getId(), ex.getMessage());
            if (throwOnUpstreamFailure) {
                throw new BusinessException(503, UPSTREAM_UNAVAILABLE);
            }
            result.getReasons().add("承包商准入服务不可用");
        }
    }

    private void checkAreaAlarm(WorkPermitEntity permit, boolean throwOnUpstreamFailure, PreCheckResultVO result) {
        if (permit.getAreaId() == null) {
            result.getReasons().add("作业区域未设置");
            return;
        }
        try {
            AlarmAreaActiveCheckRequest request = new AlarmAreaActiveCheckRequest();
            request.setTenantId(permit.getTenantId());
            request.setAreaId(permit.getAreaId());
            request.setMinLevel(DEFAULT_MIN_ALARM_LEVEL);
            AlarmAreaActiveCheckResult active = alarmAreaActiveClient.areaActiveCheck(request);
            if (active != null && active.isHasBlocking()) {
                result.getReasons().add("作业区域存在未关闭高等级报警(" + active.getCount() + "条)");
            }
        } catch (RestClientException ex) {
            log.warn("area-active-check failed permitId={} reason={}", permit.getId(), ex.getMessage());
            if (throwOnUpstreamFailure) {
                throw new BusinessException(503, UPSTREAM_UNAVAILABLE);
            }
            result.getReasons().add("报警服务不可用");
        }
    }

    private void checkGasTest(WorkPermitEntity permit, PreCheckResultVO result) {
        Date deadline = new Date(System.currentTimeMillis() - gasTestValidMinutes * 60L * 1000L);
        LambdaQueryWrapper<GasTestRecordEntity> wrapper = gasQuery(permit.getTenantId(), permit.getId())
                .eq(GasTestRecordEntity::getQualified, 1)
                .ge(GasTestRecordEntity::getTestedAt, deadline)
                .orderByDesc(GasTestRecordEntity::getTestedAt)
                .last("limit 1");
        GasTestRecordEntity latest = gasTestRecordMapper.selectOne(wrapper);
        if (latest == null) {
            result.getReasons().add("许可前" + gasTestValidMinutes + "分钟内无合格气体检测记录");
        }
    }

    private List<Long> collectContractorWorkerIds(Long tenantId, Long permitId) {
        List<WorkPermitWorkerEntity> workers = workerMapper.selectList(workerQuery(tenantId, permitId));
        List<Long> workerIds = new ArrayList<Long>();
        for (WorkPermitWorkerEntity worker : workers) {
            if (WORKER_TYPE_CONTRACTOR.equalsIgnoreCase(worker.getWorkerType()) && worker.getWorkerId() != null) {
                workerIds.add(worker.getWorkerId());
            }
        }
        return workerIds;
    }

    private WorkPermitVO transition(WorkPermitEntity entity, StatusTargetResolver resolver,
                                    String action, String remark, String operator) {
        WorkPermitStatus current = WorkPermitStatus.from(entity.getStatus());
        WorkPermitStatus target = resolver.resolve(current);
        String before = entity.getStatus();
        entity.setStatus(target.name());
        workPermitMapper.updateById(entity);
        auditSupport.writeStatusChange(entity.getTenantId(), entity.getId(), action, remark, operator, before, target.name());
        return toVO(entity);
    }

    private interface StatusTargetResolver {
        WorkPermitStatus resolve(WorkPermitStatus current);
    }

    private void saveApprovalRecord(Long tenantId, Long permitId, String action,
                                    PermitActionRequest request, String operator) {
        PermitApprovalRecordEntity record = new PermitApprovalRecordEntity();
        record.setTenantId(tenantId);
        record.setWorkPermitId(permitId);
        record.setAction(action);
        record.setOpinion(actionOpinion(request));
        record.setOperatorName(normalizeOperator(operator));
        record.setOperatedAt(new Date());
        approvalRecordMapper.insert(record);
    }

    private PermitSiteConfirmEntity saveSiteConfirm(Long tenantId, Long permitId, String confirmType, String content,
                                                    String location, String scanCode, String terminalId, String operator) {
        PermitSiteConfirmEntity entity = new PermitSiteConfirmEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(permitId);
        entity.setConfirmType(confirmType);
        entity.setConfirmContent(normalizeText(content));
        entity.setLocationText(normalizeText(location));
        entity.setScanCode(normalizeText(scanCode));
        entity.setTerminalId(normalizeText(terminalId));
        entity.setOperatorName(normalizeOperator(operator));
        entity.setConfirmedAt(new Date());
        siteConfirmMapper.insert(entity);
        return entity;
    }

    private void saveAcceptanceRecord(Long tenantId, Long permitId, AcceptanceRequest request, String operator) {
        PermitAcceptanceRecordEntity entity = new PermitAcceptanceRecordEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(permitId);
        entity.setAcceptanceResult(normalizeRequired(request.getAcceptanceResult(), "acceptanceResult"));
        entity.setOpinion(normalizeText(request.getOpinion()));
        entity.setSignatureText(normalizeText(request.getSignatureText()));
        entity.setOperatorName(normalizeOperator(operator));
        entity.setAcceptedAt(new Date());
        acceptanceRecordMapper.insert(entity);
    }

    private void archivePermit(WorkPermitEntity entity, String operator) {
        PermitArchiveSnapshotEntity snapshot = new PermitArchiveSnapshotEntity();
        snapshot.setTenantId(entity.getTenantId());
        snapshot.setWorkPermitId(entity.getId());
        snapshot.setSnapshotJson(buildSnapshotJson(entity));
        snapshot.setArchivedBy(normalizeOperator(operator));
        snapshot.setArchivedAt(new Date());
        archiveSnapshotMapper.insert(snapshot);
    }

    private String buildSnapshotJson(WorkPermitEntity entity) {
        return "{\"permitNo\":\"" + escapeJson(entity.getPermitNo()) + "\",\"status\":\""
                + escapeJson(entity.getStatus()) + "\",\"workType\":\"" + escapeJson(entity.getWorkType()) + "\"}";
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private List<ApprovalRecordVO> listApprovalRecords(Long tenantId, Long id) {
        LambdaQueryWrapper<PermitApprovalRecordEntity> wrapper = new LambdaQueryWrapper<PermitApprovalRecordEntity>()
                .eq(PermitApprovalRecordEntity::getTenantId, tenantId)
                .eq(PermitApprovalRecordEntity::getWorkPermitId, id)
                .orderByDesc(PermitApprovalRecordEntity::getOperatedAt);
        List<PermitApprovalRecordEntity> entities = approvalRecordMapper.selectList(wrapper);
        List<ApprovalRecordVO> result = new ArrayList<ApprovalRecordVO>();
        for (PermitApprovalRecordEntity entity : entities) {
            ApprovalRecordVO vo = new ApprovalRecordVO();
            vo.setId(entity.getId());
            vo.setAction(entity.getAction());
            vo.setOpinion(entity.getOpinion());
            vo.setOperatorName(entity.getOperatorName());
            vo.setOperatedAt(entity.getOperatedAt());
            result.add(vo);
        }
        return result;
    }

    private List<SiteConfirmVO> listSiteConfirms(Long tenantId, Long id) {
        List<PermitSiteConfirmEntity> entities = siteConfirmMapper.selectList(
                siteConfirmQuery(tenantId, id).orderByDesc(PermitSiteConfirmEntity::getConfirmedAt));
        List<SiteConfirmVO> result = new ArrayList<SiteConfirmVO>();
        for (PermitSiteConfirmEntity entity : entities) {
            result.add(toSiteConfirmVO(entity));
        }
        return result;
    }

    private List<AcceptanceRecordVO> listAcceptanceRecords(Long tenantId, Long id) {
        List<PermitAcceptanceRecordEntity> entities = acceptanceRecordMapper.selectList(
                acceptanceQuery(tenantId, id).orderByDesc(PermitAcceptanceRecordEntity::getAcceptedAt));
        List<AcceptanceRecordVO> result = new ArrayList<AcceptanceRecordVO>();
        for (PermitAcceptanceRecordEntity entity : entities) {
            result.add(toAcceptanceVO(entity));
        }
        return result;
    }

    private void appendStatusLogs(Long tenantId, Long id, List<TimelineItemVO> items) {
        List<PermitStatusLogEntity> logs = statusLogMapper.selectList(
                statusLogQuery(tenantId, id).orderByAsc(PermitStatusLogEntity::getOperatedAt));
        for (PermitStatusLogEntity logEntity : logs) {
            TimelineItemVO item = new TimelineItemVO();
            item.setItemType("STATUS");
            item.setAction(logEntity.getAction());
            item.setContent(buildStatusContent(logEntity));
            item.setOperatorName(logEntity.getOperatorName());
            item.setOccurredAt(logEntity.getOperatedAt());
            items.add(item);
        }
    }

    private void appendApprovalRecords(Long tenantId, Long id, List<TimelineItemVO> items) {
        for (ApprovalRecordVO record : listApprovalRecords(tenantId, id)) {
            TimelineItemVO item = new TimelineItemVO();
            item.setItemType("APPROVAL");
            item.setAction(record.getAction());
            item.setContent(record.getOpinion());
            item.setOperatorName(record.getOperatorName());
            item.setOccurredAt(record.getOperatedAt());
            items.add(item);
        }
    }

    private void appendSiteConfirms(Long tenantId, Long id, List<TimelineItemVO> items) {
        for (SiteConfirmVO record : listSiteConfirms(tenantId, id)) {
            TimelineItemVO item = new TimelineItemVO();
            item.setItemType("SITE_CONFIRM");
            item.setAction(record.getConfirmType());
            item.setContent(record.getConfirmContent());
            item.setOperatorName(record.getOperatorName());
            item.setOccurredAt(record.getConfirmedAt());
            items.add(item);
        }
    }

    private void appendMonitorRecords(Long tenantId, Long id, List<TimelineItemVO> items) {
        for (MonitorRecordVO record : listMonitorRecords(tenantId, id)) {
            TimelineItemVO item = new TimelineItemVO();
            item.setItemType("MONITOR");
            item.setAction(record.getRecordType());
            item.setContent(record.getContent());
            item.setOperatorName(record.getOperatorName());
            item.setOccurredAt(record.getRecordedAt());
            items.add(item);
        }
    }

    private void appendAcceptanceRecords(Long tenantId, Long id, List<TimelineItemVO> items) {
        for (AcceptanceRecordVO record : listAcceptanceRecords(tenantId, id)) {
            TimelineItemVO item = new TimelineItemVO();
            item.setItemType("ACCEPTANCE");
            item.setAction(record.getAcceptanceResult());
            item.setContent(record.getOpinion());
            item.setOperatorName(record.getOperatorName());
            item.setOccurredAt(record.getAcceptedAt());
            items.add(item);
        }
    }

    private String buildStatusContent(PermitStatusLogEntity logEntity) {
        if (logEntity.getFromStatus() == null) {
            return logEntity.getRemark();
        }
        return logEntity.getFromStatus() + " -> " + logEntity.getToStatus()
                + (StringUtils.hasText(logEntity.getRemark()) ? " | " + logEntity.getRemark() : "");
    }

    private String formatEligibilityReason(ContractorEligibilityReason reason) {
        if (reason == null) {
            return "承包商人员准入未通过";
        }
        if (StringUtils.hasText(reason.getMessage())) {
            return reason.getMessage();
        }
        return reason.getCode() == null ? "承包商人员准入未通过" : reason.getCode();
    }

    private WorkPermitEntity requirePermit(Long tenantId, Long id) {
        requireTenantId(tenantId);
        if (id == null) {
            throw new BusinessException(400, "work permit id is required");
        }
        WorkPermitEntity entity = workPermitMapper.selectById(id);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() != 0
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "work permit not found");
        }
        return entity;
    }

    private void assertEditable(WorkPermitEntity entity) {
        WorkPermitStatus status = WorkPermitStatus.from(entity.getStatus());
        if (status != WorkPermitStatus.DRAFT && status != WorkPermitStatus.RETURNED) {
            throw new BusinessException(409, "当前状态不允许编辑");
        }
    }

    private LambdaQueryWrapper<WorkPermitEntity> basePermitQuery(Long tenantId) {
        return new LambdaQueryWrapper<WorkPermitEntity>()
                .eq(WorkPermitEntity::getTenantId, tenantId)
                .eq(WorkPermitEntity::getDeleted, 0);
    }

    private LambdaQueryWrapper<WorkPermitWorkerEntity> workerQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<WorkPermitWorkerEntity>()
                .eq(WorkPermitWorkerEntity::getTenantId, tenantId)
                .eq(WorkPermitWorkerEntity::getWorkPermitId, permitId)
                .eq(WorkPermitWorkerEntity::getDeleted, 0);
    }

    private LambdaQueryWrapper<PermitRiskAnalysisEntity> riskQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<PermitRiskAnalysisEntity>()
                .eq(PermitRiskAnalysisEntity::getTenantId, tenantId)
                .eq(PermitRiskAnalysisEntity::getWorkPermitId, permitId)
                .eq(PermitRiskAnalysisEntity::getDeleted, 0);
    }

    private LambdaQueryWrapper<PermitSafetyMeasureEntity> measureQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<PermitSafetyMeasureEntity>()
                .eq(PermitSafetyMeasureEntity::getTenantId, tenantId)
                .eq(PermitSafetyMeasureEntity::getWorkPermitId, permitId)
                .eq(PermitSafetyMeasureEntity::getDeleted, 0);
    }

    private LambdaQueryWrapper<GasTestRecordEntity> gasQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<GasTestRecordEntity>()
                .eq(GasTestRecordEntity::getTenantId, tenantId)
                .eq(GasTestRecordEntity::getWorkPermitId, permitId)
                .eq(GasTestRecordEntity::getDeleted, 0);
    }

    private LambdaQueryWrapper<PermitSiteConfirmEntity> siteConfirmQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<PermitSiteConfirmEntity>()
                .eq(PermitSiteConfirmEntity::getTenantId, tenantId)
                .eq(PermitSiteConfirmEntity::getWorkPermitId, permitId);
    }

    private LambdaQueryWrapper<PermitMonitorRecordEntity> monitorQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<PermitMonitorRecordEntity>()
                .eq(PermitMonitorRecordEntity::getTenantId, tenantId)
                .eq(PermitMonitorRecordEntity::getWorkPermitId, permitId);
    }

    private LambdaQueryWrapper<PermitAcceptanceRecordEntity> acceptanceQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<PermitAcceptanceRecordEntity>()
                .eq(PermitAcceptanceRecordEntity::getTenantId, tenantId)
                .eq(PermitAcceptanceRecordEntity::getWorkPermitId, permitId);
    }

    private LambdaQueryWrapper<PermitStatusLogEntity> statusLogQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<PermitStatusLogEntity>()
                .eq(PermitStatusLogEntity::getTenantId, tenantId)
                .eq(PermitStatusLogEntity::getWorkPermitId, permitId);
    }

    private void applyKeyword(LambdaQueryWrapper<WorkPermitEntity> wrapper, String keyword) {
        String normalized = normalizeText(keyword);
        if (!StringUtils.hasText(normalized)) {
            return;
        }
        wrapper.and(w -> w.like(WorkPermitEntity::getPermitNo, normalized)
                .or().like(WorkPermitEntity::getTitle, normalized)
                .or().like(WorkPermitEntity::getWorkContent, normalized));
    }

    private void applyPermitRequest(WorkPermitEntity entity, WorkPermitRequest request) {
        entity.setWorkType(normalizeRequired(request.getWorkType(), "workType"));
        entity.setTitle(normalizeText(request.getTitle()));
        entity.setWorkContent(normalizeText(request.getWorkContent()));
        entity.setAreaId(request.getAreaId());
        entity.setUnitId(request.getUnitId());
        entity.setEquipmentId(request.getEquipmentId());
        entity.setHazardId(request.getHazardId());
        entity.setContractorCompanyId(request.getContractorCompanyId());
        entity.setPlanStartAt(request.getPlanStartAt());
        entity.setPlanEndAt(request.getPlanEndAt());
        entity.setSupervisorUserId(request.getSupervisorUserId());
        entity.setPermitIssuerUserId(request.getPermitIssuerUserId());
        entity.setGuardianUserId(request.getGuardianUserId());
    }

    private String generatePermitNo() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int suffix = (int) (Math.random() * 900 + 100);
        return "WKP-" + timestamp + "-" + suffix;
    }

    private String joinReasons(List<String> reasons) {
        if (reasons == null || reasons.isEmpty()) {
            return "pre-check failed";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < reasons.size(); i++) {
            if (i > 0) {
                builder.append("; ");
            }
            builder.append(reasons.get(i));
        }
        return builder.toString();
    }

    private String actionOpinion(PermitActionRequest request) {
        if (request == null) {
            return null;
        }
        return StringUtils.hasText(request.getOpinion()) ? request.getOpinion().trim() : normalizeText(request.getReason());
    }

    private String resolveReason(PermitActionRequest request) {
        if (request == null) {
            return null;
        }
        if (StringUtils.hasText(request.getReason())) {
            return request.getReason().trim();
        }
        return normalizeText(request.getOpinion());
    }

    private WorkPermitVO toVO(WorkPermitEntity entity) {
        WorkPermitVO vo = new WorkPermitVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setPermitNo(entity.getPermitNo());
        vo.setWorkType(entity.getWorkType());
        vo.setStatus(entity.getStatus());
        vo.setTitle(entity.getTitle());
        vo.setWorkContent(entity.getWorkContent());
        vo.setAreaId(entity.getAreaId());
        vo.setUnitId(entity.getUnitId());
        vo.setEquipmentId(entity.getEquipmentId());
        vo.setHazardId(entity.getHazardId());
        vo.setContractorCompanyId(entity.getContractorCompanyId());
        vo.setPlanStartAt(entity.getPlanStartAt());
        vo.setPlanEndAt(entity.getPlanEndAt());
        vo.setActualStartAt(entity.getActualStartAt());
        vo.setActualEndAt(entity.getActualEndAt());
        vo.setSupervisorUserId(entity.getSupervisorUserId());
        vo.setPermitIssuerUserId(entity.getPermitIssuerUserId());
        vo.setGuardianUserId(entity.getGuardianUserId());
        vo.setRejectReason(entity.getRejectReason());
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setUpdatedBy(entity.getUpdatedBy());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private WorkPermitWorkerVO toWorkerVO(WorkPermitWorkerEntity entity) {
        WorkPermitWorkerVO vo = new WorkPermitWorkerVO();
        vo.setId(entity.getId());
        vo.setWorkerType(entity.getWorkerType());
        vo.setWorkerId(entity.getWorkerId());
        vo.setWorkerName(entity.getWorkerName());
        vo.setRoleCode(entity.getRoleCode());
        vo.setCompanyId(entity.getCompanyId());
        vo.setCreatedAt(entity.getCreatedAt());
        return vo;
    }

    private RiskAnalysisVO toRiskVO(PermitRiskAnalysisEntity entity) {
        RiskAnalysisVO vo = new RiskAnalysisVO();
        vo.setId(entity.getId());
        vo.setHazardDesc(entity.getHazardDesc());
        vo.setControlMeasure(entity.getControlMeasure());
        vo.setRiskLevel(entity.getRiskLevel());
        vo.setAnalystName(entity.getAnalystName());
        vo.setAnalyzedAt(entity.getAnalyzedAt());
        return vo;
    }

    private SafetyMeasureVO toMeasureVO(PermitSafetyMeasureEntity entity) {
        SafetyMeasureVO vo = new SafetyMeasureVO();
        vo.setId(entity.getId());
        vo.setMeasureCode(entity.getMeasureCode());
        vo.setMeasureName(entity.getMeasureName());
        vo.setRequiredFlag(entity.getRequiredFlag() != null && entity.getRequiredFlag() == 1);
        vo.setConfirmStatus(entity.getConfirmStatus());
        vo.setConfirmBy(entity.getConfirmBy());
        vo.setConfirmAt(entity.getConfirmAt());
        vo.setRemark(entity.getRemark());
        vo.setAttachmentRef(entity.getAttachmentRef());
        return vo;
    }

    private GasTestVO toGasVO(GasTestRecordEntity entity) {
        GasTestVO vo = new GasTestVO();
        vo.setId(entity.getId());
        vo.setGasName(entity.getGasName());
        vo.setMeasuredValue(entity.getMeasuredValue());
        vo.setUnit(entity.getUnit());
        vo.setQualified(entity.getQualified() != null && entity.getQualified() == 1);
        vo.setTestedAt(entity.getTestedAt());
        vo.setTesterName(entity.getTesterName());
        vo.setRemark(entity.getRemark());
        return vo;
    }

    private SiteConfirmVO toSiteConfirmVO(PermitSiteConfirmEntity entity) {
        SiteConfirmVO vo = new SiteConfirmVO();
        vo.setId(entity.getId());
        vo.setConfirmType(entity.getConfirmType());
        vo.setConfirmContent(entity.getConfirmContent());
        vo.setLocationText(entity.getLocationText());
        vo.setScanCode(entity.getScanCode());
        vo.setTerminalId(entity.getTerminalId());
        vo.setOperatorName(entity.getOperatorName());
        vo.setConfirmedAt(entity.getConfirmedAt());
        return vo;
    }

    private MonitorRecordVO toMonitorVO(PermitMonitorRecordEntity entity) {
        MonitorRecordVO vo = new MonitorRecordVO();
        vo.setId(entity.getId());
        vo.setRecordType(entity.getRecordType());
        vo.setContent(entity.getContent());
        vo.setAbnormalFlag(entity.getAbnormalFlag() != null && entity.getAbnormalFlag() == 1);
        vo.setAttachmentRef(entity.getAttachmentRef());
        vo.setOperatorName(entity.getOperatorName());
        vo.setRecordedAt(entity.getRecordedAt());
        return vo;
    }

    private AcceptanceRecordVO toAcceptanceVO(PermitAcceptanceRecordEntity entity) {
        AcceptanceRecordVO vo = new AcceptanceRecordVO();
        vo.setId(entity.getId());
        vo.setAcceptanceResult(entity.getAcceptanceResult());
        vo.setOpinion(entity.getOpinion());
        vo.setSignatureText(entity.getSignatureText());
        vo.setOperatorName(entity.getOperatorName());
        vo.setAcceptedAt(entity.getAcceptedAt());
        return vo;
    }

    private void requireTenantId(Long tenantId) {
        if (tenantId == null) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    private String normalizeOperator(String operator) {
        return StringUtils.hasText(operator) ? operator.trim() : "system";
    }

    private String normalizeText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String normalizeRequired(String value, String field) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            throw new BusinessException(400, field + " is required");
        }
        return normalized;
    }

    private PageSpec normalizePage(int pageNo, int pageSize) {
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 100);
        return new PageSpec(normalizedPageNo, normalizedPageSize);
    }

    private static final class PageSpec {
        private final int pageNo;
        private final int pageSize;
        private final int offset;

        private PageSpec(int pageNo, int pageSize) {
            this.pageNo = pageNo;
            this.pageSize = pageSize;
            this.offset = (pageNo - 1) * pageSize;
        }
    }
}
