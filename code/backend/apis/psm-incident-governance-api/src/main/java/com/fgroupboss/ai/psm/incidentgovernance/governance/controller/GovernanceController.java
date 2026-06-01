package com.fgroupboss.ai.psm.incidentgovernance.governance.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.incidentgovernance.governance.model.dto.BenchmarkQueryParams;
import com.fgroupboss.ai.psm.incidentgovernance.governance.model.dto.DashboardQueryParams;
import com.fgroupboss.ai.psm.incidentgovernance.governance.model.dto.GovAuditIssueRequest;
import com.fgroupboss.ai.psm.incidentgovernance.governance.model.dto.GovMetricDefinitionRequest;
import com.fgroupboss.ai.psm.incidentgovernance.governance.model.dto.GovSiteMappingRequest;
import com.fgroupboss.ai.psm.incidentgovernance.governance.model.dto.GovTemplatePublishRequest;
import com.fgroupboss.ai.psm.incidentgovernance.governance.model.dto.GovTemplateRequest;
import com.fgroupboss.ai.psm.incidentgovernance.governance.model.dto.MetricSnapshotQueryParams;
import com.fgroupboss.ai.psm.incidentgovernance.governance.model.vo.BenchmarkVO;
import com.fgroupboss.ai.psm.incidentgovernance.governance.model.vo.DashboardOverviewVO;
import com.fgroupboss.ai.psm.incidentgovernance.governance.model.vo.GovAuditIssueVO;
import com.fgroupboss.ai.psm.incidentgovernance.governance.model.vo.GovMetricDefinitionVO;
import com.fgroupboss.ai.psm.incidentgovernance.governance.model.vo.GovMetricSnapshotVO;
import com.fgroupboss.ai.psm.incidentgovernance.governance.model.vo.GovSiteMappingVO;
import com.fgroupboss.ai.psm.incidentgovernance.governance.model.vo.GovTemplateVO;
import com.fgroupboss.ai.psm.incidentgovernance.governance.model.vo.GovTemplateVersionVO;
import com.fgroupboss.ai.psm.incidentgovernance.governance.service.GovernanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * Governance 模块 HTTP API。
 * <p>隐患治理与事故事件闭环。</p>
 * <p>基础路径：{@code /api/governance}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/governance")
public class GovernanceController {

    private final GovernanceService governanceService;

    /**
     * 查询templates。
     * <p>HTTP GET {@code /api/governance/templates}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param templateType templateType 参数
     * @param status 业务状态筛选
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/templates")
    public ResponseVO<PageResult<GovTemplateVO>> pageTemplates(@RequestParam Long tenantId,
                                                              @RequestParam(required = false) String keyword,
                                                              @RequestParam(required = false) String templateType,
                                                              @RequestParam(required = false) String status,
                                                              @RequestParam(defaultValue = "1") int pageNo,
                                                              @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(governanceService.pageTemplates(tenantId, keyword, templateType, status, pageNo, pageSize));
    }

    /**
     * 新增templates或触发templates相关动作。
     * <p>HTTP POST {@code /api/governance/templates}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/templates")
    public ResponseVO<GovTemplateVO> createTemplate(@Valid @RequestBody GovTemplateRequest request) {
        return ResponseVO.success(governanceService.createTemplate(request));
    }

    /**
     * 新增publish或触发publish相关动作。
     * <p>HTTP POST {@code /api/governance/templates/{id}/publish}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/templates/{id}/publish")
    public ResponseVO<GovTemplateVersionVO> publishTemplate(@PathVariable Long id,
                                                            @Valid @RequestBody GovTemplatePublishRequest request) {
        return ResponseVO.success(governanceService.publishTemplate(id, request));
    }

    /**
     * 查询sites。
     * <p>HTTP GET {@code /api/governance/sites}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param enabledFlag enabledFlag 参数
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/sites")
    public ResponseVO<PageResult<GovSiteMappingVO>> pageSites(@RequestParam Long tenantId,
                                                                @RequestParam(required = false) String keyword,
                                                                @RequestParam(required = false) Integer enabledFlag,
                                                                @RequestParam(defaultValue = "1") int pageNo,
                                                                @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(governanceService.pageSites(tenantId, keyword, enabledFlag, pageNo, pageSize));
    }

    /**
     * 新增sites或触发sites相关动作。
     * <p>HTTP POST {@code /api/governance/sites}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/sites")
    public ResponseVO<GovSiteMappingVO> createSite(@Valid @RequestBody GovSiteMappingRequest request) {
        return ResponseVO.success(governanceService.createSite(request));
    }

    /**
     * 查询metrics。
     * <p>HTTP GET {@code /api/governance/metrics}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param metricDomain metricDomain 参数
     * @param enabledFlag enabledFlag 参数
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/metrics")
    public ResponseVO<PageResult<GovMetricDefinitionVO>> pageMetrics(@RequestParam Long tenantId,
                                                                     @RequestParam(required = false) String keyword,
                                                                     @RequestParam(required = false) String metricDomain,
                                                                     @RequestParam(required = false) Integer enabledFlag,
                                                                     @RequestParam(defaultValue = "1") int pageNo,
                                                                     @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(governanceService.pageMetrics(tenantId, keyword, metricDomain, enabledFlag, pageNo, pageSize));
    }

    /**
     * 新增metrics或触发metrics相关动作。
     * <p>HTTP POST {@code /api/governance/metrics}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/metrics")
    public ResponseVO<GovMetricDefinitionVO> createMetric(@Valid @RequestBody GovMetricDefinitionRequest request) {
        return ResponseVO.success(governanceService.createMetric(request));
    }

    /**
     * 查询snapshots。
     * <p>HTTP GET {@code /api/governance/metrics/snapshots}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param metricCode metricCode 参数
     * @param siteId site ID
     * @param periodStartFrom periodStartFrom 参数
     * @param periodStartTo periodStartTo 参数
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/metrics/snapshots")
    public ResponseVO<PageResult<GovMetricSnapshotVO>> pageSnapshots(@RequestParam Long tenantId,
                                                                       @RequestParam(required = false) String metricCode,
                                                                       @RequestParam(required = false) Long siteId,
                                                                       @RequestParam(required = false) LocalDateTime periodStartFrom,
                                                                       @RequestParam(required = false) LocalDateTime periodStartTo,
                                                                       @RequestParam(defaultValue = "1") int pageNo,
                                                                       @RequestParam(defaultValue = "20") int pageSize) {
        MetricSnapshotQueryParams params = new MetricSnapshotQueryParams();
        params.setTenantId(tenantId);
        params.setMetricCode(metricCode);
        params.setSiteId(siteId);
        params.setPeriodStartFrom(periodStartFrom);
        params.setPeriodStartTo(periodStartTo);
        params.setPageNo(pageNo);
        params.setPageSize(pageSize);
        return ResponseVO.success(governanceService.pageSnapshots(params));
    }

    /**
     * 查询audit issues。
     * <p>HTTP GET {@code /api/governance/audit-issues}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param siteId site ID
     * @param status 业务状态筛选
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/audit-issues")
    public ResponseVO<PageResult<GovAuditIssueVO>> pageAuditIssues(@RequestParam Long tenantId,
                                                                     @RequestParam(required = false) Long siteId,
                                                                     @RequestParam(required = false) String status,
                                                                     @RequestParam(defaultValue = "1") int pageNo,
                                                                     @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(governanceService.pageAuditIssues(tenantId, siteId, status, pageNo, pageSize));
    }

    /**
     * 新增audit issues或触发audit issues相关动作。
     * <p>HTTP POST {@code /api/governance/audit-issues}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/audit-issues")
    public ResponseVO<GovAuditIssueVO> createAuditIssue(@Valid @RequestBody GovAuditIssueRequest request) {
        return ResponseVO.success(governanceService.createAuditIssue(request));
    }

    /**
     * 查询overview。
     * <p>HTTP GET {@code /api/governance/dashboard/overview}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param enabledSitesOnly enabledSitesOnly 参数
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/dashboard/overview")
    public ResponseVO<DashboardOverviewVO> dashboardOverview(@RequestParam Long tenantId,
                                                             @RequestParam(required = false) Integer enabledSitesOnly) {
        DashboardQueryParams params = new DashboardQueryParams();
        params.setTenantId(tenantId);
        params.setEnabledSitesOnly(enabledSitesOnly);
        return ResponseVO.success(governanceService.dashboardOverview(params));
    }

    /**
     * 查询benchmark。
     * <p>HTTP GET {@code /api/governance/benchmark}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param metricCode metricCode 参数
     * @param periodStart periodStart 参数
     * @param periodEnd periodEnd 参数
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/benchmark")
    public ResponseVO<BenchmarkVO> benchmark(@RequestParam Long tenantId,
                                             @RequestParam String metricCode,
                                             @RequestParam(required = false) LocalDateTime periodStart,
                                             @RequestParam(required = false) LocalDateTime periodEnd) {
        BenchmarkQueryParams params = new BenchmarkQueryParams();
        params.setTenantId(tenantId);
        params.setMetricCode(metricCode);
        params.setPeriodStart(periodStart);
        params.setPeriodEnd(periodEnd);
        return ResponseVO.success(governanceService.benchmark(params));
    }
}
