package com.fgroupboss.ai.psm.operation.contractor.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
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
 * 承包商人员准入接口（批次 3：人员、证书、培训、违章与资格校验）。
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
     * 接口用途：分页查询业务数据。
     */
    @GetMapping
    public ResponseVO<PageResult<ContractorWorkerVO>> page(@RequestParam Long tenantId,
                                                           @RequestParam(required = false) Long companyId,
                                                           @RequestParam(required = false) String keyword,
                                                           @RequestParam(required = false) String accessStatus,
                                                           @RequestParam(defaultValue = "1") int pageNo,
                                                           @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(workerService.page(tenantId, companyId, keyword, accessStatus, pageNo, pageSize));
    }

    /**
     * 接口用途：创建业务数据。
     */
    @PostMapping
    public ResponseVO<ContractorWorkerVO> create(@Valid @RequestBody ContractorWorkerRequest request,
                                                 @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                 @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workerService.create(request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：查询详情。
     */
    @GetMapping("/{id}")
    public ResponseVO<ContractorWorkerVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(workerService.getById(tenantId, id));
    }

    /**
     * 接口用途：更新业务数据。
     */
    @PutMapping("/{id}")
    public ResponseVO<ContractorWorkerVO> update(@PathVariable Long id,
                                                 @Valid @RequestBody ContractorWorkerRequest request,
                                                 @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                 @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workerService.update(id, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：提交审批。
     */
    @PostMapping("/{id}/submit")
    public ResponseVO<ContractorWorkerVO> submit(@PathVariable Long id,
                                                 @RequestParam Long tenantId,
                                                 @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                 @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workerService.submit(tenantId, id, operator(userId, username, operator)));
    }

    /**
     * 接口用途：审批业务数据。
     */
    @PostMapping("/{id}/approve")
    public ResponseVO<ContractorWorkerVO> approve(@PathVariable Long id,
                                                  @RequestParam Long tenantId,
                                                  @Valid @RequestBody CompanyApproveRequest request,
                                                  @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                  @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workerService.approve(tenantId, id, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：挂起业务数据。
     */
    @PostMapping("/{id}/suspend")
    public ResponseVO<ContractorWorkerVO> suspend(@PathVariable Long id,
                                                  @RequestParam Long tenantId,
                                                  @Valid @RequestBody CompanyReasonRequest request,
                                                  @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                  @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workerService.suspend(tenantId, id, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：加入黑名单。
     */
    @PostMapping("/{id}/blacklist")
    public ResponseVO<ContractorWorkerVO> blacklist(@PathVariable Long id,
                                                    @RequestParam Long tenantId,
                                                    @Valid @RequestBody CompanyReasonRequest request,
                                                    @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                    @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workerService.blacklist(tenantId, id, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：查询人员证书列表。
     */
    @GetMapping("/{id}/certificates")
    public ResponseVO<List<WorkerCertificateVO>> listCertificates(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(certificateService.listByWorker(tenantId, id));
    }

    /**
     * 接口用途：新增人员证书。
     */
    @PostMapping("/{id}/certificates")
    public ResponseVO<WorkerCertificateVO> createCertificate(@PathVariable Long id,
                                                             @Valid @RequestBody WorkerCertificateRequest request,
                                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(certificateService.create(id, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：更新人员证书。
     */
    @PutMapping("/{id}/certificates/{certId}")
    public ResponseVO<WorkerCertificateVO> updateCertificate(@PathVariable Long id,
                                                             @PathVariable Long certId,
                                                             @Valid @RequestBody WorkerCertificateRequest request,
                                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(certificateService.update(id, certId, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：删除人员证书。
     */
    @DeleteMapping("/{id}/certificates/{certId}")
    public ResponseVO<Void> deleteCertificate(@PathVariable Long id,
                                              @PathVariable Long certId,
                                              @RequestParam Long tenantId,
                                              @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                              @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                              @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        certificateService.delete(tenantId, id, certId, operator(userId, username, operator));
        return ResponseVO.success(null);
    }

    /**
     * 接口用途：查询人员培训记录。
     */
    @GetMapping("/{id}/trainings")
    public ResponseVO<List<WorkerTrainingVO>> listTrainings(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(trainingService.listByWorker(tenantId, id));
    }

    /**
     * 接口用途：新增人员培训记录。
     */
    @PostMapping("/{id}/trainings")
    public ResponseVO<WorkerTrainingVO> createTraining(@PathVariable Long id,
                                                       @Valid @RequestBody WorkerTrainingRequest request,
                                                       @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                       @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(trainingService.create(id, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：更新人员培训记录。
     */
    @PutMapping("/{id}/trainings/{trainingId}")
    public ResponseVO<WorkerTrainingVO> updateTraining(@PathVariable Long id,
                                                       @PathVariable Long trainingId,
                                                       @Valid @RequestBody WorkerTrainingRequest request,
                                                       @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                       @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(trainingService.update(id, trainingId, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：删除人员培训记录。
     */
    @DeleteMapping("/{id}/trainings/{trainingId}")
    public ResponseVO<Void> deleteTraining(@PathVariable Long id,
                                           @PathVariable Long trainingId,
                                           @RequestParam Long tenantId,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        trainingService.delete(tenantId, id, trainingId, operator(userId, username, operator));
        return ResponseVO.success(null);
    }

    /**
     * 接口用途：查询人员违章记录。
     */
    @GetMapping("/{id}/violations")
    public ResponseVO<List<WorkerViolationVO>> listViolations(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(violationService.listByWorker(tenantId, id));
    }

    /**
     * 接口用途：新增人员违章记录。
     */
    @PostMapping("/{id}/violations")
    public ResponseVO<WorkerViolationVO> createViolation(@PathVariable Long id,
                                                         @Valid @RequestBody WorkerViolationRequest request,
                                                         @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                         @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(violationService.create(id, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：执行人员准入校验。
     */
    @PostMapping("/eligibility-check")
    public ResponseVO<EligibilityCheckResultVO> eligibilityCheck(@Valid @RequestBody EligibilityCheckRequest request) {
        return ResponseVO.success(eligibilityService.check(request));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
