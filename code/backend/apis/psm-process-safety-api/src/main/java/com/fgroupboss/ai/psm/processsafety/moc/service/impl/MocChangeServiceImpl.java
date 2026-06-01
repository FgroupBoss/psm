package com.fgroupboss.ai.psm.processsafety.moc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.processsafety.moc.config.MocChangeStatus;
import com.fgroupboss.ai.psm.processsafety.moc.integration.PhaIntegrationClient;
import com.fgroupboss.ai.psm.processsafety.moc.integration.PssrIntegrationClient;
import com.fgroupboss.ai.psm.processsafety.moc.mapper.MocApprovalRecordMapper;
import com.fgroupboss.ai.psm.processsafety.moc.mapper.MocChangeMapper;
import com.fgroupboss.ai.psm.processsafety.moc.mapper.MocCloseConditionMapper;
import com.fgroupboss.ai.psm.processsafety.moc.mapper.MocImpactAnalysisMapper;
import com.fgroupboss.ai.psm.processsafety.moc.mapper.MocImplementationTaskMapper;
import com.fgroupboss.ai.psm.processsafety.moc.model.dto.MocApproveRequest;
import com.fgroupboss.ai.psm.processsafety.moc.model.dto.MocChangeRequest;
import com.fgroupboss.ai.psm.processsafety.moc.model.dto.MocImpactAnalysisRequest;
import com.fgroupboss.ai.psm.processsafety.moc.model.dto.MocImplementationTaskRequest;
import com.fgroupboss.ai.psm.processsafety.moc.model.dto.MocVerifyRequest;
import com.fgroupboss.ai.psm.processsafety.moc.model.entity.MocApprovalRecordEntity;
import com.fgroupboss.ai.psm.processsafety.moc.model.entity.MocChangeEntity;
import com.fgroupboss.ai.psm.processsafety.moc.model.entity.MocCloseConditionEntity;
import com.fgroupboss.ai.psm.processsafety.moc.model.entity.MocImpactAnalysisEntity;
import com.fgroupboss.ai.psm.processsafety.moc.model.entity.MocImplementationTaskEntity;
import com.fgroupboss.ai.psm.processsafety.moc.model.vo.MocChangeVO;
import com.fgroupboss.ai.psm.processsafety.moc.model.vo.MocImpactAnalysisVO;
import com.fgroupboss.ai.psm.processsafety.moc.model.vo.MocImplementationTaskVO;
import com.fgroupboss.ai.psm.processsafety.moc.service.MocChangeService;
import com.fgroupboss.ai.psm.common.data.EntitySupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * MOC 变更申请、影响分析、审批、实施与关闭。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MocChangeServiceImpl implements MocChangeService {

    private static final String LEVEL_MAJOR = "MAJOR";

    private final MocChangeMapper mocChangeMapper;
    private final MocImpactAnalysisMapper impactAnalysisMapper;
    private final MocApprovalRecordMapper approvalRecordMapper;
    private final MocImplementationTaskMapper implementationTaskMapper;
    private final MocCloseConditionMapper closeConditionMapper;
    private final PhaIntegrationClient phaIntegrationClient;
    private final PssrIntegrationClient pssrIntegrationClient;

    @Override
    public PageResult<MocChangeVO> page(Long tenantId, String keyword, String status, int pageNo, int pageSize) {
        EntitySupport.requireTenantId(tenantId);
        LambdaQueryWrapper<MocChangeEntity> wrapper = baseWrapper(tenantId);
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(q -> q.like(MocChangeEntity::getChangeNo, kw)
                    .or().like(MocChangeEntity::getChangeTitle, kw));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(MocChangeEntity::getStatus, status.trim());
        }
        wrapper.orderByDesc(MocChangeEntity::getId);
        Page<MocChangeEntity> page = mocChangeMapper.selectPage(
                new Page<MocChangeEntity>(EntitySupport.normalizePageNo(pageNo), EntitySupport.normalizePageSize(pageSize)),
                wrapper);
        return EntitySupport.toPageResult(page, this::toVO);
    }

    @Override
    public MocChangeVO getById(Long tenantId, Long id) {
        return toVO(requireChange(tenantId, id));
    }

    @Override
    @Transactional
    public MocChangeVO create(MocChangeRequest request, String operator) {
        EntitySupport.requireTenantId(request.getTenantId());
        MocChangeEntity entity = new MocChangeEntity();
        entity.setTenantId(request.getTenantId());
        entity.setChangeNo(request.getChangeNo().trim());
        entity.setChangeTitle(request.getChangeTitle().trim());
        applyChangeFields(entity, request);
        entity.setStatus(MocChangeStatus.DRAFT.name());
        EntitySupport.initAuditFields(entity);
        mocChangeMapper.insert(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public MocChangeVO update(Long id, MocChangeRequest request, String operator) {
        MocChangeEntity entity = requireChange(request.getTenantId(), id);
        MocChangeStatus.assertEditable(entity.getStatus());
        entity.setChangeTitle(request.getChangeTitle().trim());
        applyChangeFields(entity, request);
        EntitySupport.touchUpdated(entity);
        mocChangeMapper.updateById(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public void delete(Long tenantId, Long id, String operator) {
        MocChangeEntity entity = requireChange(tenantId, id);
        entity.setDeleted(1);
        EntitySupport.touchUpdated(entity);
        mocChangeMapper.updateById(entity);
    }

    @Override
    @Transactional
    public MocChangeVO submit(Long tenantId, Long id, String operator) {
        MocChangeEntity entity = requireChange(tenantId, id);
        MocChangeStatus.assertSubmit(entity.getStatus());
        entity.setStatus(MocChangeStatus.targetAfterSubmit());
        EntitySupport.touchUpdated(entity);
        mocChangeMapper.updateById(entity);
        triggerExternalReviews(entity);
        return toVO(entity);
    }

    @Override
    public List<MocImpactAnalysisVO> listImpactAnalysis(Long tenantId, Long changeId) {
        requireChange(tenantId, changeId);
        List<MocImpactAnalysisEntity> list = impactAnalysisMapper.selectList(
                new LambdaQueryWrapper<MocImpactAnalysisEntity>()
                        .eq(MocImpactAnalysisEntity::getTenantId, tenantId)
                        .eq(MocImpactAnalysisEntity::getChangeId, changeId)
                        .eq(MocImpactAnalysisEntity::getDeleted, 0)
                        .orderByAsc(MocImpactAnalysisEntity::getId));
        List<MocImpactAnalysisVO> result = new ArrayList<MocImpactAnalysisVO>();
        for (MocImpactAnalysisEntity item : list) {
            result.add(toImpactVO(item));
        }
        return result;
    }

    @Override
    @Transactional
    public MocImpactAnalysisVO saveImpactAnalysis(Long changeId, MocImpactAnalysisRequest request, String operator) {
        MocChangeEntity change = requireChange(request.getTenantId(), changeId);
        MocImpactAnalysisEntity entity = new MocImpactAnalysisEntity();
        entity.setTenantId(request.getTenantId());
        entity.setChangeId(changeId);
        entity.setDiscipline(request.getDiscipline());
        entity.setImpactDesc(request.getImpactDesc());
        entity.setRiskLevel(request.getRiskLevel());
        EntitySupport.initAuditFields(entity);
        impactAnalysisMapper.insert(entity);
        change.setStatus(MocChangeStatus.targetAfterImpactAnalysis());
        if (StringUtils.hasText(request.getRiskLevel())) {
            change.setRiskLevel(request.getRiskLevel());
        }
        EntitySupport.touchUpdated(change);
        mocChangeMapper.updateById(change);
        return toImpactVO(entity);
    }

    @Override
    @Transactional
    public MocChangeVO approve(Long changeId, MocApproveRequest request, String operator) {
        MocChangeEntity change = requireChange(request.getTenantId(), changeId);
        boolean passed = request.getPassed() == null || Boolean.TRUE.equals(request.getPassed());
        MocApprovalRecordEntity record = new MocApprovalRecordEntity();
        record.setTenantId(request.getTenantId());
        record.setChangeId(changeId);
        record.setApproverUserId(request.getApproverUserId());
        record.setDecision(passed ? "APPROVED" : "REJECTED");
        record.setCommentText(request.getCommentText());
        EntitySupport.initAuditFields(record);
        approvalRecordMapper.insert(record);
        if (!passed) {
            change.setStatus(MocChangeStatus.RETURNED.name());
        } else {
            change.setStatus(MocChangeStatus.targetAfterApprove());
        }
        EntitySupport.touchUpdated(change);
        mocChangeMapper.updateById(change);
        return toVO(change);
    }

    @Override
    public List<MocImplementationTaskVO> listImplementationTasks(Long tenantId, Long changeId) {
        requireChange(tenantId, changeId);
        List<MocImplementationTaskEntity> list = implementationTaskMapper.selectList(
                new LambdaQueryWrapper<MocImplementationTaskEntity>()
                        .eq(MocImplementationTaskEntity::getTenantId, tenantId)
                        .eq(MocImplementationTaskEntity::getChangeId, changeId)
                        .eq(MocImplementationTaskEntity::getDeleted, 0)
                        .orderByAsc(MocImplementationTaskEntity::getId));
        List<MocImplementationTaskVO> result = new ArrayList<MocImplementationTaskVO>();
        for (MocImplementationTaskEntity item : list) {
            result.add(toTaskVO(item));
        }
        return result;
    }

    @Override
    @Transactional
    public MocImplementationTaskVO createImplementationTask(Long changeId, MocImplementationTaskRequest request,
                                                          String operator) {
        requireChange(request.getTenantId(), changeId);
        MocImplementationTaskEntity entity = new MocImplementationTaskEntity();
        entity.setTenantId(request.getTenantId());
        entity.setChangeId(changeId);
        entity.setTaskDesc(request.getTaskDesc());
        entity.setOwnerUserId(request.getOwnerUserId());
        entity.setPlannedAt(request.getPlannedAt());
        entity.setStatus("PENDING");
        EntitySupport.initAuditFields(entity);
        implementationTaskMapper.insert(entity);
        return toTaskVO(entity);
    }

    @Override
    @Transactional
    public MocChangeVO verify(Long changeId, MocVerifyRequest request, String operator) {
        MocChangeEntity change = requireChange(request.getTenantId(), changeId);
        boolean passed = request.getPassed() == null || Boolean.TRUE.equals(request.getPassed());
        if (passed) {
            change.setStatus(MocChangeStatus.targetAfterVerify());
        } else {
            change.setStatus(MocChangeStatus.RETURNED.name());
        }
        EntitySupport.touchUpdated(change);
        mocChangeMapper.updateById(change);
        return toVO(change);
    }

    @Override
    @Transactional
    public MocChangeVO close(Long tenantId, Long changeId, String operator) {
        MocChangeEntity change = requireChange(tenantId, changeId);
        MocChangeStatus.assertClose(change.getStatus());
        assertRequiredCloseConditionsMet(tenantId, changeId);
        change.setStatus(MocChangeStatus.targetAfterClose());
        EntitySupport.touchUpdated(change);
        mocChangeMapper.updateById(change);
        return toVO(change);
    }

    private void assertRequiredCloseConditionsMet(Long tenantId, Long changeId) {
        Long pending = closeConditionMapper.selectCount(
                new LambdaQueryWrapper<MocCloseConditionEntity>()
                        .eq(MocCloseConditionEntity::getTenantId, tenantId)
                        .eq(MocCloseConditionEntity::getChangeId, changeId)
                        .eq(MocCloseConditionEntity::getDeleted, 0)
                        .eq(MocCloseConditionEntity::getRequiredFlag, 1)
                        .isNull(MocCloseConditionEntity::getCompletedAt));
        if (pending != null && pending > 0) {
            throw new BusinessException(400, "required close conditions are not completed");
        }
    }

    private void triggerExternalReviews(MocChangeEntity entity) {
        if (!LEVEL_MAJOR.equalsIgnoreCase(entity.getChangeLevel())) {
            return;
        }
        String phaUrl = phaIntegrationClient.buildReviewTriggerUrl(entity.getId());
        String pssrUrl = pssrIntegrationClient.buildPssrTriggerUrl(entity.getId());
        log.info("mocChangeId={} trigger PHA review url={}", entity.getId(), phaUrl);
        log.info("mocChangeId={} trigger PSSR url={}", entity.getId(), pssrUrl);
    }

    private void applyChangeFields(MocChangeEntity entity, MocChangeRequest request) {
        entity.setChangeType(request.getChangeType());
        entity.setChangeLevel(request.getChangeLevel());
        entity.setTemporaryFlag(request.getTemporaryFlag() == null ? 0 : request.getTemporaryFlag());
        entity.setEmergencyFlag(request.getEmergencyFlag() == null ? 0 : request.getEmergencyFlag());
        entity.setAffectedAreaId(request.getAffectedAreaId());
        entity.setAffectedEquipmentId(request.getAffectedEquipmentId());
        entity.setRiskLevel(request.getRiskLevel());
    }

    private LambdaQueryWrapper<MocChangeEntity> baseWrapper(Long tenantId) {
        return new LambdaQueryWrapper<MocChangeEntity>()
                .eq(MocChangeEntity::getTenantId, tenantId)
                .eq(MocChangeEntity::getDeleted, 0);
    }

    private MocChangeEntity requireChange(Long tenantId, Long id) {
        EntitySupport.requireTenantId(tenantId);
        EntitySupport.requireId(id);
        MocChangeEntity entity = mocChangeMapper.selectById(id);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "moc change not found");
        }
        return entity;
    }

    private MocChangeVO toVO(MocChangeEntity entity) {
        MocChangeVO vo = new MocChangeVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setChangeNo(entity.getChangeNo());
        vo.setChangeTitle(entity.getChangeTitle());
        vo.setChangeType(entity.getChangeType());
        vo.setChangeLevel(entity.getChangeLevel());
        vo.setTemporaryFlag(entity.getTemporaryFlag());
        vo.setEmergencyFlag(entity.getEmergencyFlag());
        vo.setAffectedAreaId(entity.getAffectedAreaId());
        vo.setAffectedEquipmentId(entity.getAffectedEquipmentId());
        vo.setRiskLevel(entity.getRiskLevel());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private MocImpactAnalysisVO toImpactVO(MocImpactAnalysisEntity entity) {
        MocImpactAnalysisVO vo = new MocImpactAnalysisVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setChangeId(entity.getChangeId());
        vo.setDiscipline(entity.getDiscipline());
        vo.setImpactDesc(entity.getImpactDesc());
        vo.setRiskLevel(entity.getRiskLevel());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private MocImplementationTaskVO toTaskVO(MocImplementationTaskEntity entity) {
        MocImplementationTaskVO vo = new MocImplementationTaskVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setChangeId(entity.getChangeId());
        vo.setTaskDesc(entity.getTaskDesc());
        vo.setOwnerUserId(entity.getOwnerUserId());
        vo.setPlannedAt(entity.getPlannedAt());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
