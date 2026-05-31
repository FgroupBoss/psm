package com.fgroupboss.ai.psm.processsafety.pssr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.processsafety.pssr.config.PssrIssueStatus;
import com.fgroupboss.ai.psm.processsafety.pssr.config.PssrProjectStatus;
import com.fgroupboss.ai.psm.processsafety.pssr.mapper.PssrApprovalRecordMapper;
import com.fgroupboss.ai.psm.processsafety.pssr.mapper.PssrExecutionRecordMapper;
import com.fgroupboss.ai.psm.processsafety.pssr.mapper.PssrIssueMapper;
import com.fgroupboss.ai.psm.processsafety.pssr.mapper.PssrProjectMapper;
import com.fgroupboss.ai.psm.processsafety.pssr.model.dto.PssrApprovalRequest;
import com.fgroupboss.ai.psm.processsafety.pssr.model.dto.PssrExecuteRequest;
import com.fgroupboss.ai.psm.processsafety.pssr.model.dto.PssrProjectRequest;
import com.fgroupboss.ai.psm.processsafety.pssr.model.entity.PssrApprovalRecordEntity;
import com.fgroupboss.ai.psm.processsafety.pssr.model.entity.PssrExecutionRecordEntity;
import com.fgroupboss.ai.psm.processsafety.pssr.model.entity.PssrIssueEntity;
import com.fgroupboss.ai.psm.processsafety.pssr.model.entity.PssrProjectEntity;
import com.fgroupboss.ai.psm.processsafety.pssr.model.vo.PssrExecutionRecordVO;
import com.fgroupboss.ai.psm.processsafety.pssr.model.vo.PssrProjectVO;
import com.fgroupboss.ai.psm.processsafety.pssr.model.vo.PssrStartupCheckVO;
import com.fgroupboss.ai.psm.processsafety.pssr.service.PssrProjectService;
import com.fgroupboss.ai.psm.common.data.EntitySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * PSSR 项目全生命周期：审查执行、开车批准与条件校验。
 */
@Service
@RequiredArgsConstructor
public class PssrProjectServiceImpl implements PssrProjectService {

    private static final String DECISION_APPROVE = "APPROVE";
    private static final String DECISION_REJECT = "REJECT";
    private static final String ISSUE_LEVEL_A = "A";

    private final PssrProjectMapper pssrProjectMapper;
    private final PssrExecutionRecordMapper pssrExecutionRecordMapper;
    private final PssrIssueMapper pssrIssueMapper;
    private final PssrApprovalRecordMapper pssrApprovalRecordMapper;

    @Override
    public PageResult<PssrProjectVO> page(Long tenantId, String keyword, String status, int pageNo, int pageSize) {
        EntitySupport.requireTenantId(tenantId);
        LambdaQueryWrapper<PssrProjectEntity> wrapper = baseProjectWrapper(tenantId);
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(q -> q.like(PssrProjectEntity::getPssrNo, kw)
                    .or().like(PssrProjectEntity::getProjectName, kw));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(PssrProjectEntity::getStatus, status.trim());
        }
        wrapper.orderByDesc(PssrProjectEntity::getId);
        Page<PssrProjectEntity> page = pssrProjectMapper.selectPage(
                new Page<PssrProjectEntity>(EntitySupport.normalizePageNo(pageNo), EntitySupport.normalizePageSize(pageSize)),
                wrapper);
        return EntitySupport.toPageResult(page, this::toProjectVO);
    }

    @Override
    public PssrProjectVO getById(Long tenantId, Long id) {
        return toProjectVO(requireProject(tenantId, id));
    }

    @Override
    @Transactional
    public PssrProjectVO create(PssrProjectRequest request, String operator) {
        EntitySupport.requireTenantId(request.getTenantId());
        PssrProjectEntity entity = new PssrProjectEntity();
        entity.setTenantId(request.getTenantId());
        entity.setPssrNo(resolvePssrNo(request));
        entity.setProjectName(request.getProjectName().trim());
        entity.setSourceType(request.getSourceType());
        entity.setSourceBizId(request.getSourceBizId());
        entity.setAreaId(request.getAreaId());
        entity.setEquipmentId(request.getEquipmentId());
        entity.setPlannedStartupAt(request.getPlannedStartupAt());
        entity.setApprovalStatus("UNAPPROVED");
        entity.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus().trim() : PssrProjectStatus.DRAFT.name());
        EntitySupport.initAuditFields(entity);
        pssrProjectMapper.insert(entity);
        return toProjectVO(entity);
    }

    @Override
    @Transactional
    public PssrProjectVO update(Long id, PssrProjectRequest request, String operator) {
        PssrProjectEntity entity = requireProject(request.getTenantId(), id);
        entity.setProjectName(request.getProjectName().trim());
        entity.setSourceType(request.getSourceType());
        entity.setSourceBizId(request.getSourceBizId());
        entity.setAreaId(request.getAreaId());
        entity.setEquipmentId(request.getEquipmentId());
        entity.setPlannedStartupAt(request.getPlannedStartupAt());
        if (StringUtils.hasText(request.getStatus())) {
            entity.setStatus(request.getStatus().trim());
        }
        EntitySupport.touchUpdated(entity);
        pssrProjectMapper.updateById(entity);
        return toProjectVO(entity);
    }

    @Override
    @Transactional
    public void delete(Long tenantId, Long id, String operator) {
        PssrProjectEntity entity = requireProject(tenantId, id);
        entity.setDeleted(1);
        EntitySupport.touchUpdated(entity);
        pssrProjectMapper.updateById(entity);
    }

    @Override
    @Transactional
    public PssrExecutionRecordVO execute(Long projectId, PssrExecuteRequest request, String operator) {
        PssrProjectEntity project = requireProject(request.getTenantId(), projectId);
        PssrExecutionRecordEntity record = new PssrExecutionRecordEntity();
        record.setTenantId(request.getTenantId());
        record.setProjectId(projectId);
        record.setCheckItemId(request.getCheckItemId());
        record.setResult(request.getResult().trim());
        record.setRemark(request.getRemark());
        record.setExecutorUserId(request.getExecutorUserId());
        EntitySupport.initAuditFields(record);
        pssrExecutionRecordMapper.insert(record);

        if (PssrProjectStatus.DRAFT.name().equals(project.getStatus())
                || PssrProjectStatus.PENDING_REVIEW.name().equals(project.getStatus())) {
            project.setStatus(PssrProjectStatus.REVIEWING.name());
            EntitySupport.touchUpdated(project);
            pssrProjectMapper.updateById(project);
        }
        return toExecutionVO(record);
    }

    @Override
    @Transactional
    public PssrProjectVO approveStartup(Long projectId, PssrApprovalRequest request, String operator) {
        PssrProjectEntity project = requireProject(request.getTenantId(), projectId);
        PssrProjectStatus.assertApproveStartup(project.getStatus());
        PssrStartupCheckVO check = startupCheck(request.getTenantId(), projectId);
        if (!check.isCanApprove()) {
            throw new BusinessException(400, "blocking issues must be closed before startup approval");
        }
        writeApprovalRecord(request, projectId, DECISION_APPROVE);
        project.setStatus(PssrProjectStatus.targetAfterApproveStartup());
        project.setApprovalStatus("APPROVED");
        EntitySupport.touchUpdated(project);
        pssrProjectMapper.updateById(project);
        return toProjectVO(project);
    }

    @Override
    @Transactional
    public PssrProjectVO rejectStartup(Long projectId, PssrApprovalRequest request, String operator) {
        PssrProjectEntity project = requireProject(request.getTenantId(), projectId);
        PssrProjectStatus.assertRejectStartup(project.getStatus());
        writeApprovalRecord(request, projectId, DECISION_REJECT);
        project.setStatus(PssrProjectStatus.targetAfterRejectStartup());
        project.setApprovalStatus("REJECTED");
        EntitySupport.touchUpdated(project);
        pssrProjectMapper.updateById(project);
        return toProjectVO(project);
    }

    @Override
    public PssrStartupCheckVO startupCheck(Long tenantId, Long projectId) {
        requireProject(tenantId, projectId);
        List<PssrIssueEntity> blocking = listBlockingIssues(tenantId, projectId);
        PssrStartupCheckVO vo = new PssrStartupCheckVO();
        vo.setProjectId(projectId);
        vo.setOpenBlockingIssueCount(blocking.size());
        vo.setCanApprove(blocking.isEmpty());
        List<String> reasons = new ArrayList<String>();
        for (PssrIssueEntity issue : blocking) {
            reasons.add("issue#" + issue.getId() + " level=" + issue.getIssueLevel());
        }
        vo.setBlockingReasons(reasons);
        return vo;
    }

    private void writeApprovalRecord(PssrApprovalRequest request, Long projectId, String decision) {
        PssrApprovalRecordEntity record = new PssrApprovalRecordEntity();
        record.setTenantId(request.getTenantId());
        record.setProjectId(projectId);
        record.setApproverUserId(request.getApproverUserId());
        record.setDecision(decision);
        record.setCommentText(request.getCommentText());
        EntitySupport.initAuditFields(record);
        pssrApprovalRecordMapper.insert(record);
    }

    private List<PssrIssueEntity> listBlockingIssues(Long tenantId, Long projectId) {
        LambdaQueryWrapper<PssrIssueEntity> wrapper = new LambdaQueryWrapper<PssrIssueEntity>()
                .eq(PssrIssueEntity::getTenantId, tenantId)
                .eq(PssrIssueEntity::getProjectId, projectId)
                .eq(PssrIssueEntity::getDeleted, 0)
                .ne(PssrIssueEntity::getStatus, PssrIssueStatus.CLOSED.name())
                .and(q -> q.eq(PssrIssueEntity::getIssueLevel, ISSUE_LEVEL_A)
                        .or().eq(PssrIssueEntity::getCloseRequiredBeforeStartup, 1));
        return pssrIssueMapper.selectList(wrapper);
    }

    private String resolvePssrNo(PssrProjectRequest request) {
        if (StringUtils.hasText(request.getPssrNo())) {
            return request.getPssrNo().trim();
        }
        return "PSSR-" + request.getTenantId() + "-" + System.currentTimeMillis();
    }

    private PssrProjectEntity requireProject(Long tenantId, Long id) {
        EntitySupport.requireId(id);
        return EntitySupport.requireFound(
                pssrProjectMapper.selectOne(baseProjectWrapper(tenantId).eq(PssrProjectEntity::getId, id)),
                "pssr project not found");
    }

    private LambdaQueryWrapper<PssrProjectEntity> baseProjectWrapper(Long tenantId) {
        return new LambdaQueryWrapper<PssrProjectEntity>()
                .eq(PssrProjectEntity::getTenantId, tenantId)
                .eq(PssrProjectEntity::getDeleted, 0);
    }

    private PssrProjectVO toProjectVO(PssrProjectEntity entity) {
        PssrProjectVO vo = new PssrProjectVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setPssrNo(entity.getPssrNo());
        vo.setProjectName(entity.getProjectName());
        vo.setSourceType(entity.getSourceType());
        vo.setSourceBizId(entity.getSourceBizId());
        vo.setAreaId(entity.getAreaId());
        vo.setEquipmentId(entity.getEquipmentId());
        vo.setPlannedStartupAt(entity.getPlannedStartupAt());
        vo.setApprovalStatus(entity.getApprovalStatus());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private PssrExecutionRecordVO toExecutionVO(PssrExecutionRecordEntity entity) {
        PssrExecutionRecordVO vo = new PssrExecutionRecordVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setProjectId(entity.getProjectId());
        vo.setCheckItemId(entity.getCheckItemId());
        vo.setResult(entity.getResult());
        vo.setRemark(entity.getRemark());
        vo.setExecutorUserId(entity.getExecutorUserId());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
