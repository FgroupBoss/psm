package com.fgroupboss.ai.psm.governance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.governance.config.GovTemplateVersionStatus;
import com.fgroupboss.ai.psm.governance.mapper.DwAlarmFactMapper;
import com.fgroupboss.ai.psm.governance.mapper.DwHazardFactMapper;
import com.fgroupboss.ai.psm.governance.mapper.DwIncidentFactMapper;
import com.fgroupboss.ai.psm.governance.mapper.DwMocFactMapper;
import com.fgroupboss.ai.psm.governance.mapper.DwWorkPermitFactMapper;
import com.fgroupboss.ai.psm.governance.mapper.GovAuditIssueMapper;
import com.fgroupboss.ai.psm.governance.mapper.GovMetricDefinitionMapper;
import com.fgroupboss.ai.psm.governance.mapper.GovMetricSnapshotMapper;
import com.fgroupboss.ai.psm.governance.mapper.GovSiteMappingMapper;
import com.fgroupboss.ai.psm.governance.mapper.GovTemplateMapper;
import com.fgroupboss.ai.psm.governance.mapper.GovTemplateVersionMapper;
import com.fgroupboss.ai.psm.governance.model.dto.BenchmarkQueryParams;
import com.fgroupboss.ai.psm.governance.model.dto.DashboardQueryParams;
import com.fgroupboss.ai.psm.governance.model.dto.GovAuditIssueRequest;
import com.fgroupboss.ai.psm.governance.model.dto.GovMetricDefinitionRequest;
import com.fgroupboss.ai.psm.governance.model.dto.GovSiteMappingRequest;
import com.fgroupboss.ai.psm.governance.model.dto.GovTemplatePublishRequest;
import com.fgroupboss.ai.psm.governance.model.dto.GovTemplateRequest;
import com.fgroupboss.ai.psm.governance.model.dto.MetricSnapshotQueryParams;
import com.fgroupboss.ai.psm.governance.model.entity.DwAlarmFactEntity;
import com.fgroupboss.ai.psm.governance.model.entity.DwHazardFactEntity;
import com.fgroupboss.ai.psm.governance.model.entity.DwIncidentFactEntity;
import com.fgroupboss.ai.psm.governance.model.entity.DwMocFactEntity;
import com.fgroupboss.ai.psm.governance.model.entity.DwWorkPermitFactEntity;
import com.fgroupboss.ai.psm.governance.model.entity.GovAuditIssueEntity;
import com.fgroupboss.ai.psm.governance.model.entity.GovMetricDefinitionEntity;
import com.fgroupboss.ai.psm.governance.model.entity.GovMetricSnapshotEntity;
import com.fgroupboss.ai.psm.governance.model.entity.GovSiteMappingEntity;
import com.fgroupboss.ai.psm.governance.model.entity.GovTemplateEntity;
import com.fgroupboss.ai.psm.governance.model.entity.GovTemplateVersionEntity;
import com.fgroupboss.ai.psm.governance.model.vo.BenchmarkSiteMetricVO;
import com.fgroupboss.ai.psm.governance.model.vo.BenchmarkVO;
import com.fgroupboss.ai.psm.governance.model.vo.DashboardOverviewVO;
import com.fgroupboss.ai.psm.governance.model.vo.GovAuditIssueVO;
import com.fgroupboss.ai.psm.governance.model.vo.GovMetricDefinitionVO;
import com.fgroupboss.ai.psm.governance.model.vo.GovMetricSnapshotVO;
import com.fgroupboss.ai.psm.governance.model.vo.GovSiteMappingVO;
import com.fgroupboss.ai.psm.governance.model.vo.GovTemplateVO;
import com.fgroupboss.ai.psm.governance.model.vo.GovTemplateVersionVO;
import com.fgroupboss.ai.psm.governance.model.vo.SiteDwFactSummaryVO;
import com.fgroupboss.ai.psm.governance.service.GovernanceService;
import com.fgroupboss.ai.psm.governance.support.ServiceSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fgroupboss.ai.psm.governance.support.ServiceSupport.normalizeText;
import static com.fgroupboss.ai.psm.governance.support.ServiceSupport.requireFound;
import static com.fgroupboss.ai.psm.governance.support.ServiceSupport.requireTenantId;
import static com.fgroupboss.ai.psm.governance.support.ServiceSupport.toPageResult;
import static com.fgroupboss.ai.psm.governance.support.ServiceSupport.touchCreate;
import static com.fgroupboss.ai.psm.governance.support.ServiceSupport.touchUpdate;

/**
 * 集团治理、指标与数据仓库汇总实现。
 */
@Service
@RequiredArgsConstructor
public class GovernanceServiceImpl implements GovernanceService {

    private static final String TEMPLATE_STATUS_ACTIVE = "ACTIVE";
    private static final String VERSION_STATUS_DRAFT = "DRAFT";
    private static final String AUDIT_STATUS_PENDING = "PENDING_ASSIGN";
    private static final String AUDIT_STATUS_CLOSED = "CLOSED";

    private final GovTemplateMapper govTemplateMapper;
    private final GovTemplateVersionMapper govTemplateVersionMapper;
    private final GovSiteMappingMapper govSiteMappingMapper;
    private final GovMetricDefinitionMapper govMetricDefinitionMapper;
    private final GovMetricSnapshotMapper govMetricSnapshotMapper;
    private final GovAuditIssueMapper govAuditIssueMapper;
    private final DwWorkPermitFactMapper dwWorkPermitFactMapper;
    private final DwAlarmFactMapper dwAlarmFactMapper;
    private final DwHazardFactMapper dwHazardFactMapper;
    private final DwMocFactMapper dwMocFactMapper;
    private final DwIncidentFactMapper dwIncidentFactMapper;

    @Override
    public PageResult<GovTemplateVO> pageTemplates(Long tenantId, String keyword, String templateType, String status,
                                                   int pageNo, int pageSize) {
        requireTenantId(tenantId);
        LambdaQueryWrapper<GovTemplateEntity> wrapper = tenantNotDeleted(GovTemplateEntity.class, tenantId);
        applyKeyword(wrapper, keyword, GovTemplateEntity::getTemplateCode, GovTemplateEntity::getTemplateName);
        String normalizedType = normalizeText(templateType);
        if (normalizedType != null) {
            wrapper.eq(GovTemplateEntity::getTemplateType, normalizedType);
        }
        String normalizedStatus = normalizeText(status);
        if (normalizedStatus != null) {
            wrapper.eq(GovTemplateEntity::getStatus, normalizedStatus);
        }
        wrapper.orderByDesc(GovTemplateEntity::getId);
        Page<GovTemplateEntity> page = govTemplateMapper.selectPage(
                (Page<GovTemplateEntity>) ServiceSupport.normalizePage(pageNo, pageSize), wrapper);
        return toPageResult(page, this::toTemplateVO);
    }

    @Override
    @Transactional
    public GovTemplateVO createTemplate(GovTemplateRequest request) {
        requireTenantId(request.getTenantId());
        assertTemplateCodeUnique(request.getTenantId(), request.getTemplateCode(), null);
        GovTemplateEntity template = new GovTemplateEntity();
        template.setTenantId(request.getTenantId());
        template.setTemplateCode(request.getTemplateCode().trim());
        template.setTemplateName(request.getTemplateName().trim());
        template.setTemplateType(request.getTemplateType().trim());
        template.setStatus(TEMPLATE_STATUS_ACTIVE);
        touchCreate(template);
        govTemplateMapper.insert(template);

        GovTemplateVersionEntity version = newVersionEntity(request.getTenantId(), template.getId(),
                "v1", request.getContent(), VERSION_STATUS_DRAFT);
        govTemplateVersionMapper.insert(version);
        return toTemplateVO(template);
    }

    @Override
    @Transactional
    public GovTemplateVersionVO publishTemplate(Long templateId, GovTemplatePublishRequest request) {
        requireTenantId(request.getTenantId());
        GovTemplateEntity template = requireTemplate(request.getTenantId(), templateId);
        GovTemplateVersionEntity latest = findLatestVersion(request.getTenantId(), templateId);
        if (latest != null) {
            GovTemplateVersionStatus.assertPublish(latest.getStatus());
            disablePublishedVersions(request.getTenantId(), templateId);
        }
        String content = request.getContent();
        if (!StringUtils.hasText(content) && latest != null) {
            content = latest.getContent();
        }
        GovTemplateVersionEntity published = newVersionEntity(request.getTenantId(), templateId,
                request.getVersionNo().trim(), content, GovTemplateVersionStatus.targetAfterPublish());
        govTemplateVersionMapper.insert(published);
        touchUpdate(template);
        govTemplateMapper.updateById(template);
        return toVersionVO(published);
    }

    @Override
    public PageResult<GovSiteMappingVO> pageSites(Long tenantId, String keyword, Integer enabledFlag,
                                                  int pageNo, int pageSize) {
        requireTenantId(tenantId);
        LambdaQueryWrapper<GovSiteMappingEntity> wrapper = tenantNotDeleted(GovSiteMappingEntity.class, tenantId);
        applyKeyword(wrapper, keyword, GovSiteMappingEntity::getSiteCode, GovSiteMappingEntity::getSiteName);
        if (enabledFlag != null) {
            wrapper.eq(GovSiteMappingEntity::getEnabledFlag, enabledFlag);
        }
        wrapper.orderByDesc(GovSiteMappingEntity::getId);
        Page<GovSiteMappingEntity> page = govSiteMappingMapper.selectPage(
                (Page<GovSiteMappingEntity>) ServiceSupport.normalizePage(pageNo, pageSize), wrapper);
        return toPageResult(page, this::toSiteVO);
    }

    @Override
    @Transactional
    public GovSiteMappingVO createSite(GovSiteMappingRequest request) {
        requireTenantId(request.getTenantId());
        assertSiteCodeUnique(request.getTenantId(), request.getSiteCode(), null);
        GovSiteMappingEntity entity = new GovSiteMappingEntity();
        entity.setTenantId(request.getTenantId());
        entity.setSiteCode(request.getSiteCode().trim());
        entity.setSiteName(request.getSiteName().trim());
        entity.setOrgId(request.getOrgId());
        entity.setEnabledFlag(request.getEnabledFlag() == null ? 1 : request.getEnabledFlag());
        touchCreate(entity);
        govSiteMappingMapper.insert(entity);
        return toSiteVO(entity);
    }

    @Override
    public PageResult<GovMetricDefinitionVO> pageMetrics(Long tenantId, String keyword, String metricDomain,
                                                         Integer enabledFlag, int pageNo, int pageSize) {
        requireTenantId(tenantId);
        LambdaQueryWrapper<GovMetricDefinitionEntity> wrapper = tenantNotDeleted(GovMetricDefinitionEntity.class, tenantId);
        applyKeyword(wrapper, keyword, GovMetricDefinitionEntity::getMetricCode, GovMetricDefinitionEntity::getMetricName);
        String normalizedDomain = normalizeText(metricDomain);
        if (normalizedDomain != null) {
            wrapper.eq(GovMetricDefinitionEntity::getMetricDomain, normalizedDomain);
        }
        if (enabledFlag != null) {
            wrapper.eq(GovMetricDefinitionEntity::getEnabledFlag, enabledFlag);
        }
        wrapper.orderByDesc(GovMetricDefinitionEntity::getId);
        Page<GovMetricDefinitionEntity> page = govMetricDefinitionMapper.selectPage(
                (Page<GovMetricDefinitionEntity>) ServiceSupport.normalizePage(pageNo, pageSize), wrapper);
        return toPageResult(page, this::toMetricVO);
    }

    @Override
    @Transactional
    public GovMetricDefinitionVO createMetric(GovMetricDefinitionRequest request) {
        requireTenantId(request.getTenantId());
        assertMetricCodeUnique(request.getTenantId(), request.getMetricCode(), null);
        GovMetricDefinitionEntity entity = new GovMetricDefinitionEntity();
        entity.setTenantId(request.getTenantId());
        entity.setMetricCode(request.getMetricCode().trim());
        entity.setMetricName(request.getMetricName().trim());
        entity.setMetricDomain(request.getMetricDomain().trim());
        entity.setStatisticPeriod(request.getStatisticPeriod().trim());
        entity.setFormulaVersion(normalizeText(request.getFormulaVersion()));
        entity.setTargetValue(request.getTargetValue());
        entity.setOwnerOrgId(request.getOwnerOrgId());
        entity.setEnabledFlag(request.getEnabledFlag() == null ? 1 : request.getEnabledFlag());
        touchCreate(entity);
        govMetricDefinitionMapper.insert(entity);
        return toMetricVO(entity);
    }

    @Override
    public PageResult<GovMetricSnapshotVO> pageSnapshots(MetricSnapshotQueryParams params) {
        requireTenantId(params.getTenantId());
        LambdaQueryWrapper<GovMetricSnapshotEntity> wrapper =
                tenantNotDeleted(GovMetricSnapshotEntity.class, params.getTenantId());
        String metricCode = normalizeText(params.getMetricCode());
        if (metricCode != null) {
            wrapper.eq(GovMetricSnapshotEntity::getMetricCode, metricCode);
        }
        if (params.getSiteId() != null) {
            wrapper.eq(GovMetricSnapshotEntity::getSiteId, params.getSiteId());
        }
        if (params.getPeriodStartFrom() != null) {
            wrapper.ge(GovMetricSnapshotEntity::getPeriodStart, params.getPeriodStartFrom());
        }
        if (params.getPeriodStartTo() != null) {
            wrapper.le(GovMetricSnapshotEntity::getPeriodStart, params.getPeriodStartTo());
        }
        wrapper.orderByDesc(GovMetricSnapshotEntity::getPeriodStart);
        Page<GovMetricSnapshotEntity> page = govMetricSnapshotMapper.selectPage(
                (Page<GovMetricSnapshotEntity>) ServiceSupport.normalizePage(params.getPageNo(), params.getPageSize()),
                wrapper);
        Map<Long, GovSiteMappingEntity> siteMap = loadSiteMap(params.getTenantId());
        return toPageResult(page, entity -> toSnapshotVO(entity, siteMap));
    }

    @Override
    public PageResult<GovAuditIssueVO> pageAuditIssues(Long tenantId, Long siteId, String status,
                                                       int pageNo, int pageSize) {
        requireTenantId(tenantId);
        LambdaQueryWrapper<GovAuditIssueEntity> wrapper = tenantNotDeleted(GovAuditIssueEntity.class, tenantId);
        if (siteId != null) {
            wrapper.eq(GovAuditIssueEntity::getSiteId, siteId);
        }
        String normalizedStatus = normalizeText(status);
        if (normalizedStatus != null) {
            wrapper.eq(GovAuditIssueEntity::getStatus, normalizedStatus);
        }
        wrapper.orderByDesc(GovAuditIssueEntity::getId);
        Page<GovAuditIssueEntity> page = govAuditIssueMapper.selectPage(
                (Page<GovAuditIssueEntity>) ServiceSupport.normalizePage(pageNo, pageSize), wrapper);
        Map<Long, GovSiteMappingEntity> siteMap = loadSiteMap(tenantId);
        return toPageResult(page, entity -> toAuditIssueVO(entity, siteMap));
    }

    @Override
    @Transactional
    public GovAuditIssueVO createAuditIssue(GovAuditIssueRequest request) {
        requireTenantId(request.getTenantId());
        requireSite(request.getTenantId(), request.getSiteId());
        GovAuditIssueEntity entity = new GovAuditIssueEntity();
        entity.setTenantId(request.getTenantId());
        entity.setIssueNo(generateIssueNo(request.getTenantId()));
        entity.setSiteId(request.getSiteId());
        entity.setDescription(request.getDescription().trim());
        entity.setOwnerUserId(request.getOwnerUserId());
        entity.setDueAt(request.getDueAt());
        entity.setStatus(AUDIT_STATUS_PENDING);
        touchCreate(entity);
        govAuditIssueMapper.insert(entity);
        Map<Long, GovSiteMappingEntity> siteMap = loadSiteMap(request.getTenantId());
        return toAuditIssueVO(entity, siteMap);
    }

    @Override
    public DashboardOverviewVO dashboardOverview(DashboardQueryParams params) {
        requireTenantId(params.getTenantId());
        List<GovSiteMappingEntity> sites = listSites(params);
        DashboardOverviewVO overview = new DashboardOverviewVO();
        overview.setTenantId(params.getTenantId());
        List<SiteDwFactSummaryVO> summaries = new ArrayList<SiteDwFactSummaryVO>();
        long totalWork = 0;
        long totalAlarm = 0;
        long totalHazard = 0;
        long totalMoc = 0;
        long totalIncident = 0;
        long totalOpenAudit = 0;
        for (GovSiteMappingEntity site : sites) {
            SiteDwFactSummaryVO summary = buildSiteSummary(params.getTenantId(), site);
            summaries.add(summary);
            totalWork += summary.getWorkPermitCount();
            totalAlarm += summary.getAlarmCount();
            totalHazard += summary.getHazardCount();
            totalMoc += summary.getMocCount();
            totalIncident += summary.getIncidentCount();
            totalOpenAudit += summary.getOpenAuditIssueCount();
        }
        overview.setSiteSummaries(summaries);
        overview.setTotalWorkPermits(totalWork);
        overview.setTotalAlarms(totalAlarm);
        overview.setTotalHazards(totalHazard);
        overview.setTotalMocs(totalMoc);
        overview.setTotalIncidents(totalIncident);
        overview.setTotalOpenAuditIssues(totalOpenAudit);
        return overview;
    }

    @Override
    public BenchmarkVO benchmark(BenchmarkQueryParams params) {
        requireTenantId(params.getTenantId());
        String metricCode = normalizeText(params.getMetricCode());
        if (metricCode == null) {
            throw new BusinessException(400, "metricCode is required");
        }
        LambdaQueryWrapper<GovMetricSnapshotEntity> wrapper =
                tenantNotDeleted(GovMetricSnapshotEntity.class, params.getTenantId());
        wrapper.eq(GovMetricSnapshotEntity::getMetricCode, metricCode);
        if (params.getPeriodStart() != null) {
            wrapper.ge(GovMetricSnapshotEntity::getPeriodStart, params.getPeriodStart());
        }
        if (params.getPeriodEnd() != null) {
            wrapper.le(GovMetricSnapshotEntity::getPeriodEnd, params.getPeriodEnd());
        }
        List<GovMetricSnapshotEntity> snapshots = govMetricSnapshotMapper.selectList(wrapper);
        Map<Long, GovSiteMappingEntity> siteMap = loadSiteMap(params.getTenantId());
        List<BenchmarkSiteMetricVO> items = buildBenchmarkItems(snapshots, siteMap);
        BenchmarkVO result = new BenchmarkVO();
        result.setTenantId(params.getTenantId());
        result.setMetricCode(metricCode);
        result.setPeriodStart(params.getPeriodStart());
        result.setPeriodEnd(params.getPeriodEnd());
        result.setSiteMetrics(items);
        return result;
    }

    private SiteDwFactSummaryVO buildSiteSummary(Long tenantId, GovSiteMappingEntity site) {
        SiteDwFactSummaryVO summary = new SiteDwFactSummaryVO();
        summary.setSiteId(site.getId());
        summary.setSiteCode(site.getSiteCode());
        summary.setSiteName(site.getSiteName());
        summary.setWorkPermitCount(countWorkPermits(tenantId, site.getId()));
        summary.setAlarmCount(countAlarms(tenantId, site.getId()));
        summary.setHazardCount(countHazards(tenantId, site.getId()));
        summary.setMocCount(countMocs(tenantId, site.getId()));
        summary.setIncidentCount(countIncidents(tenantId, site.getId()));
        summary.setOpenAuditIssueCount(countOpenAuditIssues(tenantId, site.getId()));
        return summary;
    }

    private long countOpenAuditIssues(Long tenantId, Long siteId) {
        LambdaQueryWrapper<GovAuditIssueEntity> wrapper = tenantNotDeleted(GovAuditIssueEntity.class, tenantId);
        wrapper.eq(GovAuditIssueEntity::getSiteId, siteId);
        wrapper.ne(GovAuditIssueEntity::getStatus, AUDIT_STATUS_CLOSED);
        return govAuditIssueMapper.selectCount(wrapper);
    }

    private long countWorkPermits(Long tenantId, Long siteId) {
        LambdaQueryWrapper<DwWorkPermitFactEntity> wrapper = new LambdaQueryWrapper<DwWorkPermitFactEntity>();
        wrapper.eq(DwWorkPermitFactEntity::getTenantId, tenantId);
        wrapper.eq(DwWorkPermitFactEntity::getSiteId, siteId);
        wrapper.eq(DwWorkPermitFactEntity::getDeleted, 0);
        return dwWorkPermitFactMapper.selectCount(wrapper);
    }

    private long countAlarms(Long tenantId, Long siteId) {
        LambdaQueryWrapper<DwAlarmFactEntity> wrapper = new LambdaQueryWrapper<DwAlarmFactEntity>();
        wrapper.eq(DwAlarmFactEntity::getTenantId, tenantId);
        wrapper.eq(DwAlarmFactEntity::getSiteId, siteId);
        wrapper.eq(DwAlarmFactEntity::getDeleted, 0);
        return dwAlarmFactMapper.selectCount(wrapper);
    }

    private long countHazards(Long tenantId, Long siteId) {
        LambdaQueryWrapper<DwHazardFactEntity> wrapper = new LambdaQueryWrapper<DwHazardFactEntity>();
        wrapper.eq(DwHazardFactEntity::getTenantId, tenantId);
        wrapper.eq(DwHazardFactEntity::getSiteId, siteId);
        wrapper.eq(DwHazardFactEntity::getDeleted, 0);
        return dwHazardFactMapper.selectCount(wrapper);
    }

    private long countMocs(Long tenantId, Long siteId) {
        LambdaQueryWrapper<DwMocFactEntity> wrapper = new LambdaQueryWrapper<DwMocFactEntity>();
        wrapper.eq(DwMocFactEntity::getTenantId, tenantId);
        wrapper.eq(DwMocFactEntity::getSiteId, siteId);
        wrapper.eq(DwMocFactEntity::getDeleted, 0);
        return dwMocFactMapper.selectCount(wrapper);
    }

    private long countIncidents(Long tenantId, Long siteId) {
        LambdaQueryWrapper<DwIncidentFactEntity> wrapper = new LambdaQueryWrapper<DwIncidentFactEntity>();
        wrapper.eq(DwIncidentFactEntity::getTenantId, tenantId);
        wrapper.eq(DwIncidentFactEntity::getSiteId, siteId);
        wrapper.eq(DwIncidentFactEntity::getDeleted, 0);
        return dwIncidentFactMapper.selectCount(wrapper);
    }

    private List<BenchmarkSiteMetricVO> buildBenchmarkItems(List<GovMetricSnapshotEntity> snapshots,
                                                            Map<Long, GovSiteMappingEntity> siteMap) {
        Map<Long, GovMetricSnapshotEntity> latestBySite = new HashMap<Long, GovMetricSnapshotEntity>();
        for (GovMetricSnapshotEntity snapshot : snapshots) {
            GovMetricSnapshotEntity existing = latestBySite.get(snapshot.getSiteId());
            if (existing == null || snapshot.getPeriodStart().isAfter(existing.getPeriodStart())) {
                latestBySite.put(snapshot.getSiteId(), snapshot);
            }
        }
        List<BenchmarkSiteMetricVO> items = new ArrayList<BenchmarkSiteMetricVO>();
        for (GovMetricSnapshotEntity snapshot : latestBySite.values()) {
            BenchmarkSiteMetricVO item = new BenchmarkSiteMetricVO();
            item.setSiteId(snapshot.getSiteId());
            GovSiteMappingEntity site = siteMap.get(snapshot.getSiteId());
            if (site != null) {
                item.setSiteCode(site.getSiteCode());
                item.setSiteName(site.getSiteName());
            }
            item.setMetricValue(snapshot.getMetricValue());
            item.setTargetValue(snapshot.getTargetValue());
            item.setGapToTarget(calcGap(snapshot.getMetricValue(), snapshot.getTargetValue()));
            items.add(item);
        }
        items.sort(Comparator.comparing(BenchmarkSiteMetricVO::getMetricValue,
                Comparator.nullsLast(Comparator.reverseOrder())));
        int rank = 1;
        for (BenchmarkSiteMetricVO item : items) {
            item.setRank(rank++);
        }
        return items;
    }

    private BigDecimal calcGap(BigDecimal metricValue, BigDecimal targetValue) {
        if (metricValue == null || targetValue == null) {
            return null;
        }
        return metricValue.subtract(targetValue);
    }

    private void disablePublishedVersions(Long tenantId, Long templateId) {
        LambdaQueryWrapper<GovTemplateVersionEntity> wrapper =
                tenantNotDeleted(GovTemplateVersionEntity.class, tenantId);
        wrapper.eq(GovTemplateVersionEntity::getTemplateId, templateId);
        wrapper.eq(GovTemplateVersionEntity::getStatus, GovTemplateVersionStatus.PUBLISHED.name());
        List<GovTemplateVersionEntity> published = govTemplateVersionMapper.selectList(wrapper);
        for (GovTemplateVersionEntity version : published) {
            version.setStatus(GovTemplateVersionStatus.DISABLED.name());
            touchUpdate(version);
            govTemplateVersionMapper.updateById(version);
        }
    }

    private GovTemplateVersionEntity findLatestVersion(Long tenantId, Long templateId) {
        LambdaQueryWrapper<GovTemplateVersionEntity> wrapper =
                tenantNotDeleted(GovTemplateVersionEntity.class, tenantId);
        wrapper.eq(GovTemplateVersionEntity::getTemplateId, templateId);
        wrapper.orderByDesc(GovTemplateVersionEntity::getId);
        wrapper.last("limit 1");
        return govTemplateVersionMapper.selectOne(wrapper);
    }

    private GovTemplateVersionEntity newVersionEntity(Long tenantId, Long templateId, String versionNo,
                                                      String content, String status) {
        GovTemplateVersionEntity version = new GovTemplateVersionEntity();
        version.setTenantId(tenantId);
        version.setTemplateId(templateId);
        version.setVersionNo(versionNo);
        version.setContent(content);
        version.setStatus(status);
        touchCreate(version);
        return version;
    }

    private List<GovSiteMappingEntity> listSites(DashboardQueryParams params) {
        LambdaQueryWrapper<GovSiteMappingEntity> wrapper =
                tenantNotDeleted(GovSiteMappingEntity.class, params.getTenantId());
        if (params.getEnabledSitesOnly() != null && params.getEnabledSitesOnly() == 1) {
            wrapper.eq(GovSiteMappingEntity::getEnabledFlag, 1);
        }
        wrapper.orderByAsc(GovSiteMappingEntity::getSiteCode);
        return govSiteMappingMapper.selectList(wrapper);
    }

    private Map<Long, GovSiteMappingEntity> loadSiteMap(Long tenantId) {
        List<GovSiteMappingEntity> sites = govSiteMappingMapper.selectList(
                tenantNotDeleted(GovSiteMappingEntity.class, tenantId));
        Map<Long, GovSiteMappingEntity> map = new HashMap<Long, GovSiteMappingEntity>();
        for (GovSiteMappingEntity site : sites) {
            map.put(site.getId(), site);
        }
        return map;
    }

    private GovTemplateEntity requireTemplate(Long tenantId, Long id) {
        LambdaQueryWrapper<GovTemplateEntity> wrapper = tenantNotDeleted(GovTemplateEntity.class, tenantId);
        wrapper.eq(GovTemplateEntity::getId, id);
        return requireFound(govTemplateMapper.selectOne(wrapper), "template not found");
    }

    private GovSiteMappingEntity requireSite(Long tenantId, Long siteId) {
        LambdaQueryWrapper<GovSiteMappingEntity> wrapper = tenantNotDeleted(GovSiteMappingEntity.class, tenantId);
        wrapper.eq(GovSiteMappingEntity::getId, siteId);
        return requireFound(govSiteMappingMapper.selectOne(wrapper), "site not found");
    }

    private void assertTemplateCodeUnique(Long tenantId, String code, Long excludeId) {
        LambdaQueryWrapper<GovTemplateEntity> wrapper = tenantNotDeleted(GovTemplateEntity.class, tenantId);
        wrapper.eq(GovTemplateEntity::getTemplateCode, code.trim());
        if (excludeId != null) {
            wrapper.ne(GovTemplateEntity::getId, excludeId);
        }
        if (govTemplateMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "templateCode already exists");
        }
    }

    private void assertSiteCodeUnique(Long tenantId, String code, Long excludeId) {
        LambdaQueryWrapper<GovSiteMappingEntity> wrapper = tenantNotDeleted(GovSiteMappingEntity.class, tenantId);
        wrapper.eq(GovSiteMappingEntity::getSiteCode, code.trim());
        if (excludeId != null) {
            wrapper.ne(GovSiteMappingEntity::getId, excludeId);
        }
        if (govSiteMappingMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "siteCode already exists");
        }
    }

    private void assertMetricCodeUnique(Long tenantId, String code, Long excludeId) {
        LambdaQueryWrapper<GovMetricDefinitionEntity> wrapper =
                tenantNotDeleted(GovMetricDefinitionEntity.class, tenantId);
        wrapper.eq(GovMetricDefinitionEntity::getMetricCode, code.trim());
        if (excludeId != null) {
            wrapper.ne(GovMetricDefinitionEntity::getId, excludeId);
        }
        if (govMetricDefinitionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "metricCode already exists");
        }
    }

    private String generateIssueNo(Long tenantId) {
        return "AUD-" + tenantId + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private GovTemplateVO toTemplateVO(GovTemplateEntity entity) {
        GovTemplateVO vo = new GovTemplateVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setTemplateCode(entity.getTemplateCode());
        vo.setTemplateName(entity.getTemplateName());
        vo.setTemplateType(entity.getTemplateType());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        GovTemplateVersionEntity latest = findLatestVersion(entity.getTenantId(), entity.getId());
        if (latest != null) {
            vo.setLatestVersionNo(latest.getVersionNo());
            vo.setLatestVersionStatus(latest.getStatus());
        }
        return vo;
    }

    private GovTemplateVersionVO toVersionVO(GovTemplateVersionEntity entity) {
        GovTemplateVersionVO vo = new GovTemplateVersionVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setTemplateId(entity.getTemplateId());
        vo.setVersionNo(entity.getVersionNo());
        vo.setContent(entity.getContent());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private GovSiteMappingVO toSiteVO(GovSiteMappingEntity entity) {
        GovSiteMappingVO vo = new GovSiteMappingVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setSiteCode(entity.getSiteCode());
        vo.setSiteName(entity.getSiteName());
        vo.setOrgId(entity.getOrgId());
        vo.setEnabledFlag(entity.getEnabledFlag());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private GovMetricDefinitionVO toMetricVO(GovMetricDefinitionEntity entity) {
        GovMetricDefinitionVO vo = new GovMetricDefinitionVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setMetricCode(entity.getMetricCode());
        vo.setMetricName(entity.getMetricName());
        vo.setMetricDomain(entity.getMetricDomain());
        vo.setStatisticPeriod(entity.getStatisticPeriod());
        vo.setFormulaVersion(entity.getFormulaVersion());
        vo.setTargetValue(entity.getTargetValue());
        vo.setOwnerOrgId(entity.getOwnerOrgId());
        vo.setEnabledFlag(entity.getEnabledFlag());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private GovMetricSnapshotVO toSnapshotVO(GovMetricSnapshotEntity entity, Map<Long, GovSiteMappingEntity> siteMap) {
        GovMetricSnapshotVO vo = new GovMetricSnapshotVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setMetricCode(entity.getMetricCode());
        vo.setSiteId(entity.getSiteId());
        GovSiteMappingEntity site = siteMap.get(entity.getSiteId());
        if (site != null) {
            vo.setSiteName(site.getSiteName());
        }
        vo.setPeriodStart(entity.getPeriodStart());
        vo.setPeriodEnd(entity.getPeriodEnd());
        vo.setMetricValue(entity.getMetricValue());
        vo.setTargetValue(entity.getTargetValue());
        vo.setCalculationTime(entity.getCalculationTime());
        vo.setInputHash(entity.getInputHash());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private GovAuditIssueVO toAuditIssueVO(GovAuditIssueEntity entity, Map<Long, GovSiteMappingEntity> siteMap) {
        GovAuditIssueVO vo = new GovAuditIssueVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setIssueNo(entity.getIssueNo());
        vo.setSiteId(entity.getSiteId());
        GovSiteMappingEntity site = siteMap.get(entity.getSiteId());
        if (site != null) {
            vo.setSiteName(site.getSiteName());
        }
        vo.setDescription(entity.getDescription());
        vo.setOwnerUserId(entity.getOwnerUserId());
        vo.setDueAt(entity.getDueAt());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private <T> LambdaQueryWrapper<T> tenantNotDeleted(Class<T> clazz, Long tenantId) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<T>();
        if (clazz == GovTemplateEntity.class) {
            return (LambdaQueryWrapper<T>) new LambdaQueryWrapper<GovTemplateEntity>()
                    .eq(GovTemplateEntity::getTenantId, tenantId)
                    .eq(GovTemplateEntity::getDeleted, 0);
        }
        if (clazz == GovTemplateVersionEntity.class) {
            return (LambdaQueryWrapper<T>) new LambdaQueryWrapper<GovTemplateVersionEntity>()
                    .eq(GovTemplateVersionEntity::getTenantId, tenantId)
                    .eq(GovTemplateVersionEntity::getDeleted, 0);
        }
        if (clazz == GovSiteMappingEntity.class) {
            return (LambdaQueryWrapper<T>) new LambdaQueryWrapper<GovSiteMappingEntity>()
                    .eq(GovSiteMappingEntity::getTenantId, tenantId)
                    .eq(GovSiteMappingEntity::getDeleted, 0);
        }
        if (clazz == GovMetricDefinitionEntity.class) {
            return (LambdaQueryWrapper<T>) new LambdaQueryWrapper<GovMetricDefinitionEntity>()
                    .eq(GovMetricDefinitionEntity::getTenantId, tenantId)
                    .eq(GovMetricDefinitionEntity::getDeleted, 0);
        }
        if (clazz == GovMetricSnapshotEntity.class) {
            return (LambdaQueryWrapper<T>) new LambdaQueryWrapper<GovMetricSnapshotEntity>()
                    .eq(GovMetricSnapshotEntity::getTenantId, tenantId)
                    .eq(GovMetricSnapshotEntity::getDeleted, 0);
        }
        if (clazz == GovAuditIssueEntity.class) {
            return (LambdaQueryWrapper<T>) new LambdaQueryWrapper<GovAuditIssueEntity>()
                    .eq(GovAuditIssueEntity::getTenantId, tenantId)
                    .eq(GovAuditIssueEntity::getDeleted, 0);
        }
        return wrapper;
    }

    private <T> void applyKeyword(LambdaQueryWrapper<T> wrapper, String keyword,
                                  com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> codeField,
                                  com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> nameField) {
        String normalized = normalizeText(keyword);
        if (normalized == null) {
            return;
        }
        wrapper.and(w -> w.like(codeField, normalized).or().like(nameField, normalized));
    }
}
