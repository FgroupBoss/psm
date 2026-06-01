package com.fgroupboss.ai.psm.processsafety.pha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.processsafety.pha.config.PhaProjectStatus;
import com.fgroupboss.ai.psm.processsafety.pha.mapper.LopaScenarioMapper;
import com.fgroupboss.ai.psm.processsafety.pha.mapper.PhaNodeMapper;
import com.fgroupboss.ai.psm.processsafety.pha.mapper.PhaProjectMapper;
import com.fgroupboss.ai.psm.processsafety.pha.mapper.PhaRecommendationMapper;
import com.fgroupboss.ai.psm.processsafety.pha.model.dto.PhaProjectRequest;
import com.fgroupboss.ai.psm.processsafety.pha.model.entity.LopaScenarioEntity;
import com.fgroupboss.ai.psm.processsafety.pha.model.entity.PhaNodeEntity;
import com.fgroupboss.ai.psm.processsafety.pha.model.entity.PhaProjectEntity;
import com.fgroupboss.ai.psm.processsafety.pha.model.entity.PhaRecommendationEntity;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.LopaScenarioVO;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.PhaNodeVO;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.PhaProjectReportVO;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.PhaProjectVO;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.PhaRecommendationVO;
import com.fgroupboss.ai.psm.processsafety.pha.service.PhaProjectService;
import com.fgroupboss.ai.psm.common.data.EntitySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * PHA 项目 CRUD 与状态流转、报告摘要。
 */
@Service
@RequiredArgsConstructor
public class PhaProjectServiceImpl implements PhaProjectService {

    private final PhaProjectMapper phaProjectMapper;
    private final PhaNodeMapper phaNodeMapper;
    private final PhaRecommendationMapper phaRecommendationMapper;
    private final LopaScenarioMapper lopaScenarioMapper;

    @Override
    public PageResult<PhaProjectVO> page(Long tenantId, String keyword, String status, int pageNo, int pageSize) {
        EntitySupport.requireTenantId(tenantId);
        LambdaQueryWrapper<PhaProjectEntity> wrapper = baseWrapper(tenantId);
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(q -> q.like(PhaProjectEntity::getProjectNo, kw)
                    .or().like(PhaProjectEntity::getProjectName, kw));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(PhaProjectEntity::getStatus, status.trim());
        }
        wrapper.orderByDesc(PhaProjectEntity::getId);
        Page<PhaProjectEntity> page = phaProjectMapper.selectPage(
                new Page<PhaProjectEntity>(EntitySupport.normalizePageNo(pageNo), EntitySupport.normalizePageSize(pageSize)),
                wrapper);
        return EntitySupport.toPageResult(page, this::toVO);
    }

    @Override
    public PhaProjectVO getById(Long tenantId, Long id) {
        return toVO(requireProject(tenantId, id));
    }

    @Override
    @Transactional
    public PhaProjectVO create(PhaProjectRequest request, String operator) {
        EntitySupport.requireTenantId(request.getTenantId());
        PhaProjectEntity entity = new PhaProjectEntity();
        entity.setTenantId(request.getTenantId());
        entity.setProjectNo(resolveProjectNo(request));
        entity.setProjectName(request.getProjectName().trim());
        entity.setSiteId(request.getSiteId());
        entity.setUnitId(request.getUnitId());
        entity.setMajorHazardId(request.getMajorHazardId());
        entity.setMethod(request.getMethod());
        entity.setVersion(request.getVersion());
        entity.setReviewDueAt(request.getReviewDueAt());
        entity.setStatus(StringUtils.hasText(request.getStatus())
                ? request.getStatus().trim() : PhaProjectStatus.DRAFT.name());
        EntitySupport.initAuditFields(entity);
        phaProjectMapper.insert(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public PhaProjectVO update(Long id, PhaProjectRequest request, String operator) {
        PhaProjectEntity entity = requireProject(request.getTenantId(), id);
        PhaProjectStatus.assertEditable(entity.getStatus());
        entity.setProjectName(request.getProjectName().trim());
        entity.setSiteId(request.getSiteId());
        entity.setUnitId(request.getUnitId());
        entity.setMajorHazardId(request.getMajorHazardId());
        entity.setMethod(request.getMethod());
        entity.setVersion(request.getVersion());
        entity.setReviewDueAt(request.getReviewDueAt());
        EntitySupport.touchUpdated(entity);
        phaProjectMapper.updateById(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public void delete(Long tenantId, Long id, String operator) {
        PhaProjectEntity entity = requireProject(tenantId, id);
        entity.setDeleted(1);
        EntitySupport.touchUpdated(entity);
        phaProjectMapper.updateById(entity);
    }

    @Override
    @Transactional
    public PhaProjectVO submit(Long tenantId, Long id, String operator) {
        PhaProjectEntity entity = requireProject(tenantId, id);
        entity.setStatus(PhaProjectStatus.targetAfterSubmit(entity.getStatus()));
        EntitySupport.touchUpdated(entity);
        phaProjectMapper.updateById(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public PhaProjectVO publish(Long tenantId, Long id, String operator) {
        PhaProjectEntity entity = requireProject(tenantId, id);
        PhaProjectStatus.assertPublish(entity.getStatus());
        entity.setStatus(PhaProjectStatus.targetAfterPublish());
        EntitySupport.touchUpdated(entity);
        phaProjectMapper.updateById(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public PhaProjectVO archive(Long tenantId, Long id, String operator) {
        PhaProjectEntity entity = requireProject(tenantId, id);
        PhaProjectStatus.assertArchive(entity.getStatus());
        entity.setStatus(PhaProjectStatus.targetAfterArchive());
        EntitySupport.touchUpdated(entity);
        phaProjectMapper.updateById(entity);
        return toVO(entity);
    }

    @Override
    public PhaProjectReportVO exportReport(Long projectId, Long tenantId) {
        PhaProjectReportVO report = new PhaProjectReportVO();
        report.setProject(getById(tenantId, projectId));
        report.setNodes(listNodes(tenantId, projectId));
        report.setRecommendations(listRecommendations(tenantId, projectId));
        report.setLopaScenarios(listLopa(tenantId, projectId));
        return report;
    }

    private List<PhaNodeVO> listNodes(Long tenantId, Long projectId) {
        List<PhaNodeEntity> nodes = phaNodeMapper.selectList(new LambdaQueryWrapper<PhaNodeEntity>()
                .eq(PhaNodeEntity::getTenantId, tenantId)
                .eq(PhaNodeEntity::getProjectId, projectId)
                .eq(PhaNodeEntity::getDeleted, 0)
                .orderByAsc(PhaNodeEntity::getId));
        List<PhaNodeVO> result = new ArrayList<PhaNodeVO>();
        for (PhaNodeEntity node : nodes) {
            result.add(toNodeVO(node));
        }
        return result;
    }

    private List<PhaRecommendationVO> listRecommendations(Long tenantId, Long projectId) {
        List<PhaRecommendationEntity> list = phaRecommendationMapper.selectList(new LambdaQueryWrapper<PhaRecommendationEntity>()
                .eq(PhaRecommendationEntity::getTenantId, tenantId)
                .eq(PhaRecommendationEntity::getProjectId, projectId)
                .eq(PhaRecommendationEntity::getDeleted, 0));
        List<PhaRecommendationVO> result = new ArrayList<PhaRecommendationVO>();
        for (PhaRecommendationEntity item : list) {
            result.add(toRecommendationVO(item));
        }
        return result;
    }

    private List<LopaScenarioVO> listLopa(Long tenantId, Long projectId) {
        List<LopaScenarioEntity> list = lopaScenarioMapper.selectList(new LambdaQueryWrapper<LopaScenarioEntity>()
                .eq(LopaScenarioEntity::getTenantId, tenantId)
                .eq(LopaScenarioEntity::getProjectId, projectId)
                .eq(LopaScenarioEntity::getDeleted, 0));
        List<LopaScenarioVO> result = new ArrayList<LopaScenarioVO>();
        for (LopaScenarioEntity item : list) {
            result.add(toLopaVO(item));
        }
        return result;
    }

    private String resolveProjectNo(PhaProjectRequest request) {
        if (StringUtils.hasText(request.getProjectNo())) {
            return request.getProjectNo().trim();
        }
        return "PHA-" + request.getTenantId() + "-" + System.currentTimeMillis();
    }

    private PhaProjectEntity requireProject(Long tenantId, Long id) {
        EntitySupport.requireId(id);
        return EntitySupport.requireFound(
                phaProjectMapper.selectOne(baseWrapper(tenantId).eq(PhaProjectEntity::getId, id)),
                "pha project not found");
    }

    private LambdaQueryWrapper<PhaProjectEntity> baseWrapper(Long tenantId) {
        return new LambdaQueryWrapper<PhaProjectEntity>()
                .eq(PhaProjectEntity::getTenantId, tenantId)
                .eq(PhaProjectEntity::getDeleted, 0);
    }

    private PhaProjectVO toVO(PhaProjectEntity entity) {
        PhaProjectVO vo = new PhaProjectVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setProjectNo(entity.getProjectNo());
        vo.setProjectName(entity.getProjectName());
        vo.setSiteId(entity.getSiteId());
        vo.setUnitId(entity.getUnitId());
        vo.setMajorHazardId(entity.getMajorHazardId());
        vo.setMethod(entity.getMethod());
        vo.setVersion(entity.getVersion());
        vo.setReviewDueAt(entity.getReviewDueAt());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private PhaNodeVO toNodeVO(PhaNodeEntity entity) {
        PhaNodeVO vo = new PhaNodeVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setProjectId(entity.getProjectId());
        vo.setNodeNo(entity.getNodeNo());
        vo.setNodeName(entity.getNodeName());
        vo.setDesignIntent(entity.getDesignIntent());
        vo.setParameters(entity.getParameters());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private PhaRecommendationVO toRecommendationVO(PhaRecommendationEntity entity) {
        PhaRecommendationVO vo = new PhaRecommendationVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setProjectId(entity.getProjectId());
        vo.setDeviationId(entity.getDeviationId());
        vo.setRecNo(entity.getRecNo());
        vo.setDescription(entity.getDescription());
        vo.setOwnerUserId(entity.getOwnerUserId());
        vo.setDueAt(entity.getDueAt());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private LopaScenarioVO toLopaVO(LopaScenarioEntity entity) {
        LopaScenarioVO vo = new LopaScenarioVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setScenarioNo(entity.getScenarioNo());
        vo.setProjectId(entity.getProjectId());
        vo.setDeviationId(entity.getDeviationId());
        vo.setInitiatingEventFrequency(entity.getInitiatingEventFrequency());
        vo.setConsequenceSeverity(entity.getConsequenceSeverity());
        vo.setTargetFrequency(entity.getTargetFrequency());
        vo.setMitigatedFrequency(entity.getMitigatedFrequency());
        vo.setSilRecommendation(entity.getSilRecommendation());
        vo.setCalculationVersion(entity.getCalculationVersion());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
