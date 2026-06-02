package com.fgroupboss.ai.psm.operation.contractor.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.CompanyApproveRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.CompanyReasonRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.ContractorWorkerRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.EligibilityCheckRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.WorkerCertificateRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.WorkerTrainingRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.WorkerViolationRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.vo.ContractorWorkerVO;
import com.fgroupboss.ai.psm.operation.contractor.model.vo.EligibilityCheckResultVO;
import com.fgroupboss.ai.psm.operation.contractor.model.vo.WorkerCertificateVO;
import com.fgroupboss.ai.psm.operation.contractor.model.vo.WorkerTrainingVO;
import com.fgroupboss.ai.psm.operation.contractor.model.vo.WorkerViolationVO;
import com.fgroupboss.ai.psm.operation.contractor.service.ContractorWorkerService;
import com.fgroupboss.ai.psm.operation.contractor.service.WorkerCertificateService;
import com.fgroupboss.ai.psm.operation.contractor.service.WorkerEligibilityService;
import com.fgroupboss.ai.psm.operation.contractor.service.WorkerTrainingService;
import com.fgroupboss.ai.psm.operation.contractor.service.WorkerViolationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 承包商人员准入接口。
 * <p>人员档案、证书/培训/违章子资源及作业前资格校验。</p>
 * <p>基础路径：{@code /api/contractors/workers}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contractors/workers")
public class ContractorWorkerController {

    private final ContractorWorkerService workerService;
    private final WorkerCertificateService certificateService;
    private final WorkerTrainingService trainingService;
    private final WorkerViolationService violationService;
    private final WorkerEligibilityService eligibilityService;

    /**
     * 分页查询承包商作业人员。
     * <p>HTTP GET {@code /api/contractors/workers}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param companyId 承包商单位 ID
     * @param keyword 模糊搜索关键字
     * @param accessStatus 准入/授权状态
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<ContractorWorkerVO>> page(@LoginContext UserContext loginContext,
                                                                                                             @RequestParam(required = false) Long companyId,
                                                           @RequestParam(required = false) String keyword,
                                                           @RequestParam(required = false) String accessStatus,
                                                           @RequestParam(defaultValue = "1") int pageNo,
                                                           @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(workerService.page(loginContext.getTenantId(), companyId, keyword, accessStatus, pageNo, pageSize));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/contractors/workers}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<ContractorWorkerVO> create(@LoginContext UserContext loginContext,
                                                  @Valid @RequestBody ContractorWorkerRequest request,
                                                                                                                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(workerService.create(request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/contractors/workers/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 承包商人员 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<ContractorWorkerVO> get(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(workerService.getById(loginContext.getTenantId(), id));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/contractors/workers/{id}}</p>
     * @param id 承包商人员 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}")
    public ResponseVO<ContractorWorkerVO> update(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                 @Valid @RequestBody ContractorWorkerRequest request,
                                                                                                                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(workerService.update(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 提交审批。
     * <p>HTTP POST {@code /api/contractors/workers/{id}/submit}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 承包商人员 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/submit")
    public ResponseVO<ContractorWorkerVO> submit(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                                                                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workerService.submit(loginContext.getTenantId(), id, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 审批通过。
     * <p>HTTP POST {@code /api/contractors/workers/{id}/approve}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 承包商人员 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/approve")
    public ResponseVO<ContractorWorkerVO> approve(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                    @Valid @RequestBody CompanyApproveRequest request,
                                                                                                                                                      @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workerService.approve(loginContext.getTenantId(), id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 暂停/挂起。
     * <p>HTTP POST {@code /api/contractors/workers/{id}/suspend}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 承包商人员 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/suspend")
    public ResponseVO<ContractorWorkerVO> suspend(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                    @Valid @RequestBody CompanyReasonRequest request,
                                                                                                                                                      @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workerService.suspend(loginContext.getTenantId(), id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 加入黑名单。
     * <p>HTTP POST {@code /api/contractors/workers/{id}/blacklist}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 承包商人员 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/blacklist")
    public ResponseVO<ContractorWorkerVO> blacklist(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                        @Valid @RequestBody CompanyReasonRequest request,
                                                                                                                                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workerService.blacklist(loginContext.getTenantId(), id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询人员证书。
     * <p>HTTP GET {@code /api/contractors/workers/{id}/certificates}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 承包商人员 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/certificates")
    public ResponseVO<List<WorkerCertificateVO>> listCertificates(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(certificateService.listByWorker(loginContext.getTenantId(), id));
    }

    /**
     * 新增人员证书或触发人员证书相关动作。
     * <p>HTTP POST {@code /api/contractors/workers/{id}/certificates}</p>
     * @param id 承包商人员 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/certificates")
    public ResponseVO<WorkerCertificateVO> createCertificate(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                             @Valid @RequestBody WorkerCertificateRequest request,
                                                                                                                                                                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(certificateService.create(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新人员证书。
     * <p>HTTP PUT {@code /api/contractors/workers/{id}/certificates/{certId}}</p>
     * @param id 承包商人员 ID
     * @param certId 证书记录 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}/certificates/{certId}")
    public ResponseVO<WorkerCertificateVO> updateCertificate(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                             @PathVariable Long certId,
                                                             @Valid @RequestBody WorkerCertificateRequest request,
                                                                                                                                                                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(certificateService.update(id, certId, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 删除人员证书。
     * <p>HTTP DELETE {@code /api/contractors/workers/{id}/certificates/{certId}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 承包商人员 ID
     * @param certId 证书记录 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/{id}/certificates/{certId}")
    public ResponseVO<Void> deleteCertificate(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                              @PathVariable Long certId,
                                                                                                                                                                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        certificateService.delete(loginContext.getTenantId(), id, certId, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success(null);
    }

    /**
     * 查询培训记录。
     * <p>HTTP GET {@code /api/contractors/workers/{id}/trainings}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 承包商人员 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/trainings")
    public ResponseVO<List<WorkerTrainingVO>> listTrainings(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(trainingService.listByWorker(loginContext.getTenantId(), id));
    }

    /**
     * 新增培训记录或触发培训记录相关动作。
     * <p>HTTP POST {@code /api/contractors/workers/{id}/trainings}</p>
     * @param id 承包商人员 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/trainings")
    public ResponseVO<WorkerTrainingVO> createTraining(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                       @Valid @RequestBody WorkerTrainingRequest request,
                                                                                                                                                                     @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(trainingService.create(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新培训记录。
     * <p>HTTP PUT {@code /api/contractors/workers/{id}/trainings/{trainingId}}</p>
     * @param id 承包商人员 ID
     * @param trainingId 培训记录 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}/trainings/{trainingId}")
    public ResponseVO<WorkerTrainingVO> updateTraining(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                       @PathVariable Long trainingId,
                                                       @Valid @RequestBody WorkerTrainingRequest request,
                                                                                                                                                                     @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(trainingService.update(id, trainingId, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 删除培训记录。
     * <p>HTTP DELETE {@code /api/contractors/workers/{id}/trainings/{trainingId}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 承包商人员 ID
     * @param trainingId 培训记录 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/{id}/trainings/{trainingId}")
    public ResponseVO<Void> deleteTraining(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                           @PathVariable Long trainingId,
                                                                                                                                                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        trainingService.delete(loginContext.getTenantId(), id, trainingId, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success(null);
    }

    /**
     * 查询违章记录。
     * <p>HTTP GET {@code /api/contractors/workers/{id}/violations}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 承包商人员 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/violations")
    public ResponseVO<List<WorkerViolationVO>> listViolations(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(violationService.listByWorker(loginContext.getTenantId(), id));
    }

    /**
     * 新增违章记录或触发违章记录相关动作。
     * <p>HTTP POST {@code /api/contractors/workers/{id}/violations}</p>
     * @param id 承包商人员 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/violations")
    public ResponseVO<WorkerViolationVO> createViolation(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                         @Valid @RequestBody WorkerViolationRequest request,
                                                                                                                                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(violationService.create(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 新增eligibility check或触发eligibility check相关动作。
     * <p>HTTP POST {@code /api/contractors/workers/eligibility-check}</p>
     * <p>用于作业票选人前的准入规则校验。</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/eligibility-check")
    public ResponseVO<EligibilityCheckResultVO> eligibilityCheck(@Valid @RequestBody EligibilityCheckRequest request) {
        return ResponseVO.success(eligibilityService.check(request));
    }

}
