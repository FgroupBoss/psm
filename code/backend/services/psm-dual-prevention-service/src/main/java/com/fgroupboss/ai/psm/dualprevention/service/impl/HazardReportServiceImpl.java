package com.fgroupboss.ai.psm.dualprevention.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.AuditBizType;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.audit.CentralAuditClient;
import com.fgroupboss.ai.psm.common.notification.CentralNotificationClient;
import com.fgroupboss.ai.psm.common.notification.NotificationSendRequest;
import com.fgroupboss.ai.psm.dualprevention.config.HazardLevelSupport;
import com.fgroupboss.ai.psm.dualprevention.config.HazardStatus;
import com.fgroupboss.ai.psm.dualprevention.config.HazardStatusTransition;
import com.fgroupboss.ai.psm.dualprevention.mapper.HazardActionMapper;
import com.fgroupboss.ai.psm.dualprevention.mapper.HazardEscalationMapper;
import com.fgroupboss.ai.psm.dualprevention.mapper.HazardReportMapper;
import com.fgroupboss.ai.psm.dualprevention.model.dto.HazardAreaOpenCheckRequest;
import com.fgroupboss.ai.psm.dualprevention.model.dto.HazardConfirmRequest;
import com.fgroupboss.ai.psm.dualprevention.model.dto.HazardEscalateRequest;
import com.fgroupboss.ai.psm.dualprevention.model.dto.HazardOverdueCheckRequest;
import com.fgroupboss.ai.psm.dualprevention.model.dto.HazardRectifyRequest;
import com.fgroupboss.ai.psm.dualprevention.model.dto.HazardReportRequest;
import com.fgroupboss.ai.psm.dualprevention.model.dto.HazardReviewRequest;
import com.fgroupboss.ai.psm.dualprevention.model.entity.HazardActionEntity;
import com.fgroupboss.ai.psm.dualprevention.model.entity.HazardEscalationEntity;
import com.fgroupboss.ai.psm.dualprevention.model.entity.HazardReportEntity;
import com.fgroupboss.ai.psm.dualprevention.model.vo.HazardAreaOpenCheckVO;
import com.fgroupboss.ai.psm.dualprevention.model.vo.HazardOverdueCheckVO;
import com.fgroupboss.ai.psm.dualprevention.model.vo.HazardReportVO;
import com.fgroupboss.ai.psm.dualprevention.model.vo.HazardStatisticsVO;
import com.fgroupboss.ai.psm.dualprevention.service.HazardReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 隐患上报与闭环治理。
 */
@Service
@RequiredArgsConstructor
public class HazardReportServiceImpl implements HazardReportService {

    private static final String ACTION_CREATE = "CREATE";
    private static final String ACTION_CONFIRM = "CONFIRM";
    private static final String ACTION_RECTIFY = "RECTIFY";
    private static final String ACTION_REVIEW = "REVIEW";
    private static final String ACTION_ESCALATE = "ESCALATE";
    private static final String TEMPLATE_HAZARD_OVERDUE = "HAZARD_OVERDUE";
    private static final String TEMPLATE_HAZARD_ESCALATION = "HAZARD_ESCALATION";
    private static final String BIZ_TYPE_HAZARD = "DUAL_PREVENTION_HAZARD";

    private final HazardReportMapper hazardReportMapper;
    private final HazardActionMapper hazardActionMapper;
    private final HazardEscalationMapper hazardEscalationMapper;
    private final CentralAuditClient centralAuditClient;
    private final CentralNotificationClient notificationClient;

    @Override
    public PageResult<HazardReportVO> page(Long tenantId, String keyword, String status, String hazardLevel,
                                           Long areaId, Long riskUnitId, Integer overdueFlag,
                                           int pageNo, int pageSize) {
        requireTenantId(tenantId);
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);

        LambdaQueryWrapper<HazardReportEntity> wrapper = baseHazardWrapper(tenantId);
        applyHazardFilters(wrapper, keyword, status, hazardLevel, areaId, riskUnitId, overdueFlag);
        wrapper.orderByDesc(HazardReportEntity::getId);

        Page<HazardReportEntity> mpPage = new Page<HazardReportEntity>(normalizedPageNo, normalizedPageSize);
        Page<HazardReportEntity> result = hazardReportMapper.selectPage(mpPage, wrapper);

        List<HazardReportVO> records = new ArrayList<HazardReportVO>();
        for (HazardReportEntity entity : result.getRecords()) {
            records.add(toVO(entity));
        }
        return new PageResult<HazardReportVO>(result.getTotal(), normalizedPageNo, normalizedPageSize, records);
    }

    @Override
    public HazardReportVO getById(Long tenantId, Long id) {
        return toVO(requireHazard(tenantId, id));
    }

    @Override
    @Transactional
    public HazardReportVO create(HazardReportRequest request, String operator) {
        requireTenantId(request.getTenantId());
        HazardReportEntity entity = new HazardReportEntity();
        entity.setTenantId(request.getTenantId());
        entity.setHazardNo(generateHazardNo(request.getTenantId()));
        entity.setHazardLevel(request.getHazardLevel().trim());
        entity.setSourceType(request.getSourceType().trim());
        entity.setSourceBizId(request.getSourceBizId());
        entity.setRiskUnitId(request.getRiskUnitId());
        entity.setAreaId(request.getAreaId());
        entity.setDescription(request.getDescription().trim());
        entity.setFoundAt(request.getFoundAt() == null ? LocalDateTime.now() : request.getFoundAt());
        entity.setRectificationDeadline(request.getRectificationDeadline());
        entity.setStatus(HazardStatus.PENDING_CONFIRM.name());
        entity.setOverdueFlag(0);
        entity.setContractorId(request.getContractorId());
        entity.setDeleted(0);
        hazardReportMapper.insert(entity);
        writeCentralAudit(request.getTenantId(), entity.getId(), ACTION_CREATE, null,
                entity.getStatus(), operator);
        return toVO(entity);
    }

    @Override
    @Transactional
    public HazardReportVO confirm(Long id, HazardConfirmRequest request, String operator) {
        HazardReportEntity entity = requireHazard(request.getTenantId(), id);
        HazardStatusTransition.assertConfirm(entity.getStatus());

        String before = entity.getStatus();
        boolean returned = Boolean.TRUE.equals(request.getReturned());
        String after = HazardStatusTransition.targetAfterConfirm(returned);

        if (!returned) {
            if (StringUtils.hasText(request.getHazardLevel())) {
                entity.setHazardLevel(request.getHazardLevel().trim());
            }
            entity.setAssigneeOrgId(request.getAssigneeOrgId());
            entity.setAssigneeUserId(request.getAssigneeUserId());
            if (request.getRectificationDeadline() != null) {
                entity.setRectificationDeadline(request.getRectificationDeadline());
            }
        }

        entity.setStatus(after);
        refreshOverdueFlag(entity);
        hazardReportMapper.updateById(entity);
        writeAction(request.getTenantId(), id, ACTION_CONFIRM, before, after, request.getContent(), null, operator);
        writeCentralAudit(request.getTenantId(), id, ACTION_CONFIRM, before, after, operator);
        return toVO(entity);
    }

    @Override
    @Transactional
    public HazardReportVO rectify(Long id, HazardRectifyRequest request, String operator) {
        HazardReportEntity entity = requireHazard(request.getTenantId(), id);
        HazardStatusTransition.assertRectify(entity.getStatus());

        String before = entity.getStatus();
        String after = HazardStatus.PENDING_REVIEW.name();
        entity.setStatus(after);
        refreshOverdueFlag(entity);
        hazardReportMapper.updateById(entity);
        writeAction(request.getTenantId(), id, ACTION_RECTIFY, before, after, request.getContent(),
                request.getEvidenceFileIds(), operator);
        writeCentralAudit(request.getTenantId(), id, ACTION_RECTIFY, before, after, operator);
        return toVO(entity);
    }

    @Override
    @Transactional
    public HazardReportVO review(Long id, HazardReviewRequest request, String operator) {
        HazardReportEntity entity = requireHazard(request.getTenantId(), id);
        HazardStatusTransition.assertReview(entity.getStatus());

        String before = entity.getStatus();
        boolean passed = Boolean.TRUE.equals(request.getPassed());
        String after = HazardStatusTransition.targetAfterReview(passed);
        entity.setStatus(after);
        if (passed) {
            entity.setOverdueFlag(0);
        } else {
            refreshOverdueFlag(entity);
        }
        hazardReportMapper.updateById(entity);
        writeAction(request.getTenantId(), id, ACTION_REVIEW, before, after, request.getContent(), null, operator);
        writeCentralAudit(request.getTenantId(), id, ACTION_REVIEW, before, after, operator);
        return toVO(entity);
    }

    @Override
    public HazardAreaOpenCheckVO areaOpenCheck(HazardAreaOpenCheckRequest request) {
        requireTenantId(request.getTenantId());
        if (request.getAreaId() == null || request.getAreaId() <= 0) {
            throw new BusinessException(400, "areaId is required");
        }
        String minLevel = StringUtils.hasText(request.getMinLevel())
                ? request.getMinLevel().trim() : HazardLevelSupport.defaultMinLevel();

        List<HazardReportEntity> openHazards = hazardReportMapper.selectList(
                new LambdaQueryWrapper<HazardReportEntity>()
                        .eq(HazardReportEntity::getTenantId, request.getTenantId())
                        .eq(HazardReportEntity::getAreaId, request.getAreaId())
                        .eq(HazardReportEntity::getDeleted, 0)
                        .ne(HazardReportEntity::getStatus, HazardStatus.CLOSED.name()));

        List<HazardReportVO> blocking = new ArrayList<HazardReportVO>();
        for (HazardReportEntity entity : openHazards) {
            if (HazardLevelSupport.meetsMinLevel(entity.getHazardLevel(), minLevel)) {
                blocking.add(toVO(entity));
            }
        }

        HazardAreaOpenCheckVO result = new HazardAreaOpenCheckVO();
        result.setHasBlocking(!blocking.isEmpty());
        result.setCount(blocking.size());
        result.setHazards(blocking);
        return result;
    }

    @Override
    @Transactional
    public HazardReportVO escalate(Long id, HazardEscalateRequest request, String operator) {
        HazardReportEntity entity = requireHazard(request.getTenantId(), id);
        if (HazardStatus.CLOSED.name().equals(entity.getStatus())) {
            throw new BusinessException(400, "closed hazard cannot be escalated");
        }

        Long notifyUserId = request.getNotifyUserId() != null
                ? request.getNotifyUserId() : entity.getAssigneeUserId();
        if (notifyUserId == null) {
            throw new BusinessException(400, "notify user is required when assignee is empty");
        }

        HazardEscalationEntity escalation = new HazardEscalationEntity();
        escalation.setTenantId(request.getTenantId());
        escalation.setHazardId(id);
        escalation.setReason(request.getReason());
        escalation.setNotifyUserId(notifyUserId);
        escalation.setOperatorName(defaultOperator(operator));
        escalation.setEscalatedAt(LocalDateTime.now());
        hazardEscalationMapper.insert(escalation);

        writeAction(request.getTenantId(), id, ACTION_ESCALATE, entity.getStatus(), entity.getStatus(),
                request.getReason(), null, operator);
        writeCentralAudit(request.getTenantId(), id, ACTION_ESCALATE, entity.getStatus(), entity.getStatus(), operator);
        sendHazardNotification(entity, notifyUserId, TEMPLATE_HAZARD_ESCALATION,
                "HAZARD-ESC-" + id, request.getReason());
        return toVO(entity);
    }

    @Override
    @Transactional
    public HazardOverdueCheckVO overdueCheck(HazardOverdueCheckRequest request) {
        requireTenantId(request.getTenantId());
        boolean markOverdue = request.getMarkOverdue() == null || Boolean.TRUE.equals(request.getMarkOverdue());
        LocalDateTime now = LocalDateTime.now();

        List<HazardReportEntity> candidates = hazardReportMapper.selectList(
                new LambdaQueryWrapper<HazardReportEntity>()
                        .eq(HazardReportEntity::getTenantId, request.getTenantId())
                        .eq(HazardReportEntity::getDeleted, 0)
                        .ne(HazardReportEntity::getStatus, HazardStatus.CLOSED.name())
                        .isNotNull(HazardReportEntity::getRectificationDeadline)
                        .lt(HazardReportEntity::getRectificationDeadline, now));

        HazardOverdueCheckVO result = new HazardOverdueCheckVO();
        result.setCheckedCount(candidates.size());

        int marked = 0;
        List<HazardReportVO> overdueList = new ArrayList<HazardReportVO>();
        for (HazardReportEntity entity : candidates) {
            if (markOverdue && (entity.getOverdueFlag() == null || entity.getOverdueFlag() == 0)) {
                entity.setOverdueFlag(1);
                hazardReportMapper.updateById(entity);
                marked++;
                notifyOverdueAssignee(entity);
            }
            overdueList.add(toVO(entity));
        }
        result.setMarkedCount(marked);
        result.setOverdueHazards(overdueList);
        return result;
    }

    @Override
    public HazardStatisticsVO statistics(Long tenantId, Long areaId) {
        requireTenantId(tenantId);
        LambdaQueryWrapper<HazardReportEntity> wrapper = baseHazardWrapper(tenantId);
        if (areaId != null) {
            wrapper.eq(HazardReportEntity::getAreaId, areaId);
        }
        List<HazardReportEntity> hazards = hazardReportMapper.selectList(wrapper);

        HazardStatisticsVO stats = new HazardStatisticsVO();
        stats.setTotalCount(hazards.size());

        Map<String, Long> statusCounts = new LinkedHashMap<String, Long>();
        Map<String, Long> levelCounts = new LinkedHashMap<String, Long>();
        long overdueCount = 0;
        long closedCount = 0;

        for (HazardReportEntity hazard : hazards) {
            increment(statusCounts, hazard.getStatus());
            increment(levelCounts, hazard.getHazardLevel());
            if (hazard.getOverdueFlag() != null && hazard.getOverdueFlag() == 1) {
                overdueCount++;
            }
            if (HazardStatus.CLOSED.name().equals(hazard.getStatus())) {
                closedCount++;
            }
        }

        stats.setOverdueCount(overdueCount);
        stats.setClosedCount(closedCount);
        stats.setStatusCounts(statusCounts);
        stats.setLevelCounts(levelCounts);
        return stats;
    }

    private void refreshOverdueFlag(HazardReportEntity entity) {
        if (HazardStatus.CLOSED.name().equals(entity.getStatus())) {
            entity.setOverdueFlag(0);
            return;
        }
        LocalDateTime deadline = entity.getRectificationDeadline();
        if (deadline != null && deadline.isBefore(LocalDateTime.now())) {
            entity.setOverdueFlag(1);
        }
    }

    private void notifyOverdueAssignee(HazardReportEntity entity) {
        if (entity.getAssigneeUserId() == null) {
            return;
        }
        sendHazardNotification(entity, entity.getAssigneeUserId(), TEMPLATE_HAZARD_OVERDUE,
                "HAZARD-OVERDUE-" + entity.getId(), "rectification deadline exceeded");
    }

    private void sendHazardNotification(HazardReportEntity entity, Long userId, String templateCode,
                                      String requestIdPrefix, String reason) {
        Map<String, String> variables = new HashMap<String, String>();
        variables.put("hazardNo", entity.getHazardNo() == null ? String.valueOf(entity.getId()) : entity.getHazardNo());
        variables.put("hazardId", String.valueOf(entity.getId()));
        variables.put("reason", reason == null ? "" : reason);
        NotificationSendRequest notification = CentralNotificationClient.build(
                entity.getTenantId(), userId, requestIdPrefix + "-" + userId,
                templateCode, BIZ_TYPE_HAZARD, entity.getId(), variables);
        notificationClient.send(notification);
    }

    private void writeCentralAudit(Long tenantId, Long hazardId, String action, String before, String after,
                                   String operator) {
        centralAuditClient.append(CentralAuditClient.build(tenantId, defaultOperator(operator), action,
                AuditBizType.DUAL_PREVENTION_HAZARD.name(), hazardId, before, after));
    }

    private void writeAction(Long tenantId, Long hazardId, String actionType, String before, String after,
                             String content, String evidenceFileIds, String operator) {
        HazardActionEntity action = new HazardActionEntity();
        action.setTenantId(tenantId);
        action.setHazardId(hazardId);
        action.setActionType(actionType);
        action.setBeforeStatus(before);
        action.setAfterStatus(after);
        action.setContent(content);
        action.setEvidenceFileIds(evidenceFileIds);
        action.setOperatorName(defaultOperator(operator));
        action.setOperatedAt(LocalDateTime.now());
        hazardActionMapper.insert(action);
    }

    private LambdaQueryWrapper<HazardReportEntity> baseHazardWrapper(Long tenantId) {
        return new LambdaQueryWrapper<HazardReportEntity>()
                .eq(HazardReportEntity::getTenantId, tenantId)
                .eq(HazardReportEntity::getDeleted, 0);
    }

    private void applyHazardFilters(LambdaQueryWrapper<HazardReportEntity> wrapper, String keyword, String status,
                                    String hazardLevel, Long areaId, Long riskUnitId, Integer overdueFlag) {
        if (StringUtils.hasText(keyword)) {
            String trimmed = keyword.trim();
            wrapper.and(w -> w.like(HazardReportEntity::getHazardNo, trimmed)
                    .or()
                    .like(HazardReportEntity::getDescription, trimmed));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(HazardReportEntity::getStatus, status.trim());
        }
        if (StringUtils.hasText(hazardLevel)) {
            wrapper.eq(HazardReportEntity::getHazardLevel, hazardLevel.trim());
        }
        if (areaId != null) {
            wrapper.eq(HazardReportEntity::getAreaId, areaId);
        }
        if (riskUnitId != null) {
            wrapper.eq(HazardReportEntity::getRiskUnitId, riskUnitId);
        }
        if (overdueFlag != null) {
            wrapper.eq(HazardReportEntity::getOverdueFlag, overdueFlag);
        }
    }

    private void increment(Map<String, Long> counter, String key) {
        if (!StringUtils.hasText(key)) {
            return;
        }
        String normalized = key.trim();
        Long current = counter.get(normalized);
        counter.put(normalized, current == null ? 1L : current + 1L);
    }

    private String generateHazardNo(Long tenantId) {
        String suffix = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
        return "HZ" + tenantId + suffix;
    }

    private HazardReportEntity requireHazard(Long tenantId, Long id) {
        requireTenantId(tenantId);
        HazardReportEntity entity = hazardReportMapper.selectById(id);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "hazard report not found");
        }
        return entity;
    }

    private HazardReportVO toVO(HazardReportEntity entity) {
        HazardReportVO vo = new HazardReportVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setHazardNo(entity.getHazardNo());
        vo.setHazardLevel(entity.getHazardLevel());
        vo.setSourceType(entity.getSourceType());
        vo.setSourceBizId(entity.getSourceBizId());
        vo.setRiskUnitId(entity.getRiskUnitId());
        vo.setAreaId(entity.getAreaId());
        vo.setDescription(entity.getDescription());
        vo.setFoundAt(entity.getFoundAt());
        vo.setRectificationDeadline(entity.getRectificationDeadline());
        vo.setStatus(entity.getStatus());
        vo.setOverdueFlag(entity.getOverdueFlag());
        vo.setAssigneeOrgId(entity.getAssigneeOrgId());
        vo.setAssigneeUserId(entity.getAssigneeUserId());
        vo.setContractorId(entity.getContractorId());
        return vo;
    }

    private String defaultOperator(String operator) {
        return StringUtils.hasText(operator) ? operator.trim() : "system";
    }

    private void requireTenantId(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is required");
        }
    }
}
