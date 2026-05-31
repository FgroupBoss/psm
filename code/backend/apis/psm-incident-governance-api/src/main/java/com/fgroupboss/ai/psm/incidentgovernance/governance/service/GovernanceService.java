package com.fgroupboss.ai.psm.incidentgovernance.governance.service;

import com.fgroupboss.ai.psm.common.PageResult;
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

/**
 * 集团治理核心业务。
 */
public interface GovernanceService {

    PageResult<GovTemplateVO> pageTemplates(Long tenantId, String keyword, String templateType, String status,
                                          int pageNo, int pageSize);

    GovTemplateVO createTemplate(GovTemplateRequest request);

    GovTemplateVersionVO publishTemplate(Long templateId, GovTemplatePublishRequest request);

    PageResult<GovSiteMappingVO> pageSites(Long tenantId, String keyword, Integer enabledFlag,
                                           int pageNo, int pageSize);

    GovSiteMappingVO createSite(GovSiteMappingRequest request);

    PageResult<GovMetricDefinitionVO> pageMetrics(Long tenantId, String keyword, String metricDomain,
                                                  Integer enabledFlag, int pageNo, int pageSize);

    GovMetricDefinitionVO createMetric(GovMetricDefinitionRequest request);

    PageResult<GovMetricSnapshotVO> pageSnapshots(MetricSnapshotQueryParams params);

    PageResult<GovAuditIssueVO> pageAuditIssues(Long tenantId, Long siteId, String status,
                                                int pageNo, int pageSize);

    GovAuditIssueVO createAuditIssue(GovAuditIssueRequest request);

    DashboardOverviewVO dashboardOverview(DashboardQueryParams params);

    BenchmarkVO benchmark(BenchmarkQueryParams params);
}
