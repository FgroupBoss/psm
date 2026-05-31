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
 * 集团治理与数据仓库接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/governance")
public class GovernanceController {

    private final GovernanceService governanceService;

    @GetMapping("/templates")
    public ResponseVO<PageResult<GovTemplateVO>> pageTemplates(@RequestParam Long tenantId,
                                                              @RequestParam(required = false) String keyword,
                                                              @RequestParam(required = false) String templateType,
                                                              @RequestParam(required = false) String status,
                                                              @RequestParam(defaultValue = "1") int pageNo,
                                                              @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(governanceService.pageTemplates(tenantId, keyword, templateType, status, pageNo, pageSize));
    }

    @PostMapping("/templates")
    public ResponseVO<GovTemplateVO> createTemplate(@Valid @RequestBody GovTemplateRequest request) {
        return ResponseVO.success(governanceService.createTemplate(request));
    }

    @PostMapping("/templates/{id}/publish")
    public ResponseVO<GovTemplateVersionVO> publishTemplate(@PathVariable Long id,
                                                            @Valid @RequestBody GovTemplatePublishRequest request) {
        return ResponseVO.success(governanceService.publishTemplate(id, request));
    }

    @GetMapping("/sites")
    public ResponseVO<PageResult<GovSiteMappingVO>> pageSites(@RequestParam Long tenantId,
                                                                @RequestParam(required = false) String keyword,
                                                                @RequestParam(required = false) Integer enabledFlag,
                                                                @RequestParam(defaultValue = "1") int pageNo,
                                                                @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(governanceService.pageSites(tenantId, keyword, enabledFlag, pageNo, pageSize));
    }

    @PostMapping("/sites")
    public ResponseVO<GovSiteMappingVO> createSite(@Valid @RequestBody GovSiteMappingRequest request) {
        return ResponseVO.success(governanceService.createSite(request));
    }

    @GetMapping("/metrics")
    public ResponseVO<PageResult<GovMetricDefinitionVO>> pageMetrics(@RequestParam Long tenantId,
                                                                     @RequestParam(required = false) String keyword,
                                                                     @RequestParam(required = false) String metricDomain,
                                                                     @RequestParam(required = false) Integer enabledFlag,
                                                                     @RequestParam(defaultValue = "1") int pageNo,
                                                                     @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(governanceService.pageMetrics(tenantId, keyword, metricDomain, enabledFlag, pageNo, pageSize));
    }

    @PostMapping("/metrics")
    public ResponseVO<GovMetricDefinitionVO> createMetric(@Valid @RequestBody GovMetricDefinitionRequest request) {
        return ResponseVO.success(governanceService.createMetric(request));
    }

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

    @GetMapping("/audit-issues")
    public ResponseVO<PageResult<GovAuditIssueVO>> pageAuditIssues(@RequestParam Long tenantId,
                                                                     @RequestParam(required = false) Long siteId,
                                                                     @RequestParam(required = false) String status,
                                                                     @RequestParam(defaultValue = "1") int pageNo,
                                                                     @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(governanceService.pageAuditIssues(tenantId, siteId, status, pageNo, pageSize));
    }

    @PostMapping("/audit-issues")
    public ResponseVO<GovAuditIssueVO> createAuditIssue(@Valid @RequestBody GovAuditIssueRequest request) {
        return ResponseVO.success(governanceService.createAuditIssue(request));
    }

    @GetMapping("/dashboard/overview")
    public ResponseVO<DashboardOverviewVO> dashboardOverview(@RequestParam Long tenantId,
                                                             @RequestParam(required = false) Integer enabledSitesOnly) {
        DashboardQueryParams params = new DashboardQueryParams();
        params.setTenantId(tenantId);
        params.setEnabledSitesOnly(enabledSitesOnly);
        return ResponseVO.success(governanceService.dashboardOverview(params));
    }

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
