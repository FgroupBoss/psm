package com.fgroupboss.ai.psm.contractor.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.contractor.model.dto.CompanyApproveRequest;
import com.fgroupboss.ai.psm.contractor.model.dto.CompanyReasonRequest;
import com.fgroupboss.ai.psm.contractor.model.dto.ContractorCompanyRequest;
import com.fgroupboss.ai.psm.contractor.model.dto.ContractorQualificationRequest;
import com.fgroupboss.ai.psm.contractor.model.vo.ContractorCompanyVO;
import com.fgroupboss.ai.psm.contractor.model.vo.ContractorQualificationVO;
import com.fgroupboss.ai.psm.contractor.service.ContractorCompanyService;
import com.fgroupboss.ai.psm.contractor.service.ContractorQualificationService;
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
 * 承包商单位准入接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contractors/companies")
public class ContractorCompanyController {

    private final ContractorCompanyService companyService;
    private final ContractorQualificationService qualificationService;

    /**
     * 接口用途：分页查询业务数据。
     */
    @GetMapping
    public ResponseVO<PageResult<ContractorCompanyVO>> page(@RequestParam Long tenantId,
                                                             @RequestParam(required = false) String keyword,
                                                             @RequestParam(required = false) String status,
                                                             @RequestParam(defaultValue = "1") int pageNo,
                                                             @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(companyService.page(tenantId, keyword, status, pageNo, pageSize));
    }

    /**
     * 接口用途：创建业务数据。
     */
    @PostMapping
    public ResponseVO<ContractorCompanyVO> create(@Valid @RequestBody ContractorCompanyRequest request,
                                                  @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                  @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(companyService.create(request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：查询详情。
     */
    @GetMapping("/{id}")
    public ResponseVO<ContractorCompanyVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(companyService.getById(tenantId, id));
    }

    /**
     * 接口用途：更新业务数据。
     */
    @PutMapping("/{id}")
    public ResponseVO<ContractorCompanyVO> update(@PathVariable Long id,
                                                  @Valid @RequestBody ContractorCompanyRequest request,
                                                  @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                  @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(companyService.update(id, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：提交审批。
     */
    @PostMapping("/{id}/submit")
    public ResponseVO<ContractorCompanyVO> submit(@PathVariable Long id,
                                                  @RequestParam Long tenantId,
                                                  @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                  @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(companyService.submit(tenantId, id, operator(userId, username, operator)));
    }

    /**
     * 接口用途：审批业务数据。
     */
    @PostMapping("/{id}/approve")
    public ResponseVO<ContractorCompanyVO> approve(@PathVariable Long id,
                                                   @RequestParam Long tenantId,
                                                   @Valid @RequestBody CompanyApproveRequest request,
                                                   @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                   @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(companyService.approve(tenantId, id, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：挂起业务数据。
     */
    @PostMapping("/{id}/suspend")
    public ResponseVO<ContractorCompanyVO> suspend(@PathVariable Long id,
                                                   @RequestParam Long tenantId,
                                                   @Valid @RequestBody CompanyReasonRequest request,
                                                   @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                   @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(companyService.suspend(tenantId, id, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：加入黑名单。
     */
    @PostMapping("/{id}/blacklist")
    public ResponseVO<ContractorCompanyVO> blacklist(@PathVariable Long id,
                                                     @RequestParam Long tenantId,
                                                     @Valid @RequestBody CompanyReasonRequest request,
                                                     @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                     @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                     @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(companyService.blacklist(tenantId, id, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：查询承包商资质列表。
     */
    @GetMapping("/{id}/qualifications")
    public ResponseVO<List<ContractorQualificationVO>> listQualifications(@PathVariable Long id,
                                                                          @RequestParam Long tenantId) {
        return ResponseVO.success(qualificationService.listByCompany(tenantId, id));
    }

    /**
     * 接口用途：新增承包商资质。
     */
    @PostMapping("/{id}/qualifications")
    public ResponseVO<ContractorQualificationVO> createQualification(@PathVariable Long id,
                                                                     @Valid @RequestBody ContractorQualificationRequest request,
                                                                     @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                                     @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                                     @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(qualificationService.create(id, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：更新承包商资质。
     */
    @PutMapping("/{id}/qualifications/{qualId}")
    public ResponseVO<ContractorQualificationVO> updateQualification(@PathVariable Long id,
                                                                   @PathVariable Long qualId,
                                                                   @Valid @RequestBody ContractorQualificationRequest request,
                                                                   @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                                   @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(qualificationService.update(id, qualId, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：删除承包商资质。
     */
    @DeleteMapping("/{id}/qualifications/{qualId}")
    public ResponseVO<Void> deleteQualification(@PathVariable Long id,
                                                @PathVariable Long qualId,
                                                @RequestParam Long tenantId,
                                                @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        qualificationService.delete(tenantId, id, qualId, operator(userId, username, operator));
        return ResponseVO.success(null);
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
