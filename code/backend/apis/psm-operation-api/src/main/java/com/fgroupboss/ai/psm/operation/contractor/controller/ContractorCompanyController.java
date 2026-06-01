package com.fgroupboss.ai.psm.operation.contractor.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.CompanyApproveRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.CompanyReasonRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.ContractorCompanyRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.dto.ContractorQualificationRequest;
import com.fgroupboss.ai.psm.operation.contractor.model.vo.ContractorCompanyVO;
import com.fgroupboss.ai.psm.operation.contractor.model.vo.ContractorQualificationVO;
import com.fgroupboss.ai.psm.operation.contractor.service.ContractorCompanyService;
import com.fgroupboss.ai.psm.operation.contractor.service.ContractorQualificationService;
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
 * <p>单位资质、审批流与准入状态管理。</p>
 * <p>基础路径：{@code /api/contractors/companies}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contractors/companies")
public class ContractorCompanyController {

    private final ContractorCompanyService companyService;
    private final ContractorQualificationService qualificationService;

    /**
     * 分页查询承包商单位。
     * <p>HTTP GET {@code /api/contractors/companies}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param status 业务状态筛选
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
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
     * 新建记录。
     * <p>HTTP POST {@code /api/contractors/companies}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<ContractorCompanyVO> create(@Valid @RequestBody ContractorCompanyRequest request,
                                                  @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                  @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(companyService.create(request, operator(userId, username, operator)));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/contractors/companies/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 承包商单位 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<ContractorCompanyVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(companyService.getById(tenantId, id));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/contractors/companies/{id}}</p>
     * @param id 承包商单位 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
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
     * 提交审批。
     * <p>HTTP POST {@code /api/contractors/companies/{id}/submit}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 承包商单位 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
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
     * 审批通过。
     * <p>HTTP POST {@code /api/contractors/companies/{id}/approve}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 承包商单位 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
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
     * 暂停/挂起。
     * <p>HTTP POST {@code /api/contractors/companies/{id}/suspend}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 承包商单位 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
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
     * 加入黑名单。
     * <p>HTTP POST {@code /api/contractors/companies/{id}/blacklist}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 承包商单位 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
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
     * 查询qualifications。
     * <p>HTTP GET {@code /api/contractors/companies/{id}/qualifications}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 承包商单位 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/qualifications")
    public ResponseVO<List<ContractorQualificationVO>> listQualifications(@PathVariable Long id,
                                                                          @RequestParam Long tenantId) {
        return ResponseVO.success(qualificationService.listByCompany(tenantId, id));
    }

    /**
     * 新增qualifications或触发qualifications相关动作。
     * <p>HTTP POST {@code /api/contractors/companies/{id}/qualifications}</p>
     * @param id 承包商单位 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
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
     * 更新qualifications。
     * <p>HTTP PUT {@code /api/contractors/companies/{id}/qualifications/{qualId}}</p>
     * @param id 承包商单位 ID
     * @param qualId qual ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
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
     * 删除qualifications。
     * <p>HTTP DELETE {@code /api/contractors/companies/{id}/qualifications/{qualId}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 承包商单位 ID
     * @param qualId qual ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
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
