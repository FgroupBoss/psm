package com.fgroupboss.ai.psm.operation.workpermit.roadbreak.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.RoadBreakDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.RoadBreakSiteControlRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.RoadBreakTrafficPlanRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RoadBreakDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RoadBreakFlowNodeVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RoadBreakFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RoadBreakPreCheckReasonVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RoadBreakPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RoadBreakSiteControlVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RoadBreakTrafficPlanVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.config.WorkPermitStatus;
import com.fgroupboss.ai.psm.operation.workpermit.mapper.WorkPermitMapper;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;
import com.fgroupboss.ai.psm.operation.workpermit.roadbreak.config.RoadBreakCheckPoint;
import com.fgroupboss.ai.psm.operation.workpermit.roadbreak.config.RoadBreakProperties;
import com.fgroupboss.ai.psm.operation.workpermit.roadbreak.config.RoadBreakSiteControlItems;
import com.fgroupboss.ai.psm.operation.workpermit.roadbreak.config.RoadBreakWorkType;
import com.fgroupboss.ai.psm.operation.workpermit.roadbreak.mapper.RoadBreakDetailMapper;
import com.fgroupboss.ai.psm.operation.workpermit.roadbreak.mapper.RoadBreakSiteControlMapper;
import com.fgroupboss.ai.psm.operation.workpermit.roadbreak.mapper.RoadBreakTrafficPlanMapper;
import com.fgroupboss.ai.psm.operation.workpermit.roadbreak.model.entity.RoadBreakDetailEntity;
import com.fgroupboss.ai.psm.operation.workpermit.roadbreak.model.entity.RoadBreakSiteControlEntity;
import com.fgroupboss.ai.psm.operation.workpermit.roadbreak.model.entity.RoadBreakTrafficPlanEntity;
import com.fgroupboss.ai.psm.operation.workpermit.roadbreak.service.RoadBreakService;
import com.fgroupboss.ai.psm.operation.workpermit.specialty.SpecialtyCheckPoint;
import com.fgroupboss.ai.psm.operation.workpermit.specialty.WorkPermitSpecialtyHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoadBreakServiceImpl implements RoadBreakService, WorkPermitSpecialtyHandler {

    private static final String LEVEL_BLOCK = "BLOCK";
    private static final String CHECK_PASS = "PASS";

    private final RoadBreakProperties properties;
    private final WorkPermitMapper workPermitMapper;
    private final RoadBreakDetailMapper detailMapper;
    private final RoadBreakTrafficPlanMapper trafficPlanMapper;
    private final RoadBreakSiteControlMapper siteControlMapper;

    @Override
    public boolean supports(WorkPermitEntity permit) {
        return properties.isEnabled() && RoadBreakWorkType.isRoadBreak(permit.getWorkType());
    }

    @Override
    public void applyPreCheck(WorkPermitEntity permit, SpecialtyCheckPoint checkPoint, PreCheckResultVO result) {
        if (!supports(permit)) {
            return;
        }
        RoadBreakCheckPoint point = mapCheckPoint(checkPoint);
        if (point == null) {
            return;
        }
        mergeReasons(buildPreCheckResult(permit, point), result);
    }

    @Override
    public void enrichDetail(Long tenantId, WorkPermitEntity permit, WorkPermitDetailVO detail) {
        if (!supports(permit)) {
            return;
        }
        Long permitId = permit.getId();
        detail.setRoadBreakDetail(getDetail(tenantId, permitId));
        detail.setRoadBreakTrafficPlan(getTrafficPlan(tenantId, permitId));
        detail.setRoadBreakSiteControls(listSiteControls(tenantId, permitId));
    }

    @Override
    public boolean skipGasTestAtSitePermit(WorkPermitEntity permit) {
        return supports(permit);
    }

    @Override
    public RoadBreakDetailVO getDetail(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        RoadBreakDetailEntity entity = findDetail(tenantId, permitId);
        return entity == null ? null : toDetailVO(entity);
    }

    @Override
    @Transactional
    public RoadBreakDetailVO saveDetail(Long tenantId, Long permitId,
                                        RoadBreakDetailRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertRoadBreak(permit);
        Date now = new Date();
        RoadBreakDetailEntity entity = findDetail(tenantId, permitId);
        if (entity == null) {
            entity = new RoadBreakDetailEntity();
            entity.setTenantId(tenantId);
            entity.setWorkPermitId(permitId);
            entity.setDeleted(0);
            entity.setCreatedAt(now);
        }
        entity.setRoadId(normalizeRequired(request.getRoadId(), "roadId"));
        entity.setAreaGeoJson(normalizeText(request.getAreaGeoJson()));
        entity.setReason(normalizeRequired(request.getReason(), "reason"));
        entity.setStartAt(request.getStartAt());
        entity.setEndAt(request.getEndAt());
        entity.setResponsibleUnit(normalizeRequired(request.getResponsibleUnit(), "responsibleUnit"));
        entity.setUpdatedAt(now);
        if (entity.getStartAt() == null || entity.getEndAt() == null) {
            throw new BusinessException(400, "startAt and endAt are required");
        }
        if (entity.getId() == null) {
            detailMapper.insert(entity);
        } else {
            detailMapper.updateById(entity);
        }
        return toDetailVO(entity);
    }

    @Override
    public RoadBreakTrafficPlanVO getTrafficPlan(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        RoadBreakTrafficPlanEntity entity = findTrafficPlan(tenantId, permitId);
        return entity == null ? null : toTrafficPlanVO(entity);
    }

    @Override
    @Transactional
    public RoadBreakTrafficPlanVO saveTrafficPlan(Long tenantId, Long permitId,
                                                  RoadBreakTrafficPlanRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertRoadBreak(permit);
        Date now = new Date();
        RoadBreakTrafficPlanEntity entity = findTrafficPlan(tenantId, permitId);
        if (entity == null) {
            entity = new RoadBreakTrafficPlanEntity();
            entity.setTenantId(tenantId);
            entity.setWorkPermitId(permitId);
            entity.setDeleted(0);
            entity.setCreatedAt(now);
        }
        entity.setDetourGeoJson(normalizeText(request.getDetourGeoJson()));
        entity.setEmergencyLaneGeoJson(normalizeText(request.getEmergencyLaneGeoJson()));
        entity.setPlanRef(normalizeRequired(request.getPlanRef(), "planRef"));
        entity.setConfirmed(Boolean.TRUE.equals(request.getConfirmed()) ? 1 : 0);
        entity.setUpdatedAt(now);
        if (entity.getId() == null) {
            trafficPlanMapper.insert(entity);
        } else {
            trafficPlanMapper.updateById(entity);
        }
        return toTrafficPlanVO(entity);
    }

    @Override
    public List<RoadBreakSiteControlVO> listSiteControls(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        List<RoadBreakSiteControlVO> result = new ArrayList<RoadBreakSiteControlVO>();
        for (RoadBreakSiteControlEntity entity : siteControlMapper.selectList(siteControlQuery(tenantId, permitId))) {
            result.add(toSiteControlVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public RoadBreakSiteControlVO addSiteControl(Long tenantId, Long permitId,
                                                 RoadBreakSiteControlRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertRoadBreak(permit);
        RoadBreakSiteControlEntity entity = new RoadBreakSiteControlEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(permitId);
        entity.setItemCode(normalizeRequired(request.getItemCode(), "itemCode"));
        entity.setItemName(normalizeRequired(request.getItemName(), "itemName"));
        entity.setPosition(normalizeText(request.getPosition()));
        entity.setCheckResult(normalizeRequired(request.getCheckResult(), "checkResult").toUpperCase());
        entity.setAttachmentRef(normalizeText(request.getAttachmentRef()));
        entity.setCheckedBy(normalizeOperator(operator));
        entity.setCheckedAt(new Date());
        siteControlMapper.insert(entity);
        return toSiteControlVO(entity);
    }

    @Override
    public RoadBreakPreCheckResultVO preCheck(Long tenantId, Long permitId, String checkPoint) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertRoadBreak(permit);
        return buildPreCheckResult(permit, RoadBreakCheckPoint.from(checkPoint));
    }

    @Override
    public RoadBreakFlowProgressVO getFlowProgress(Long tenantId, Long permitId) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertRoadBreak(permit);
        RoadBreakTrafficPlanEntity plan = findTrafficPlan(tenantId, permitId);
        RoadBreakFlowProgressVO vo = new RoadBreakFlowProgressVO();
        vo.setPermitStatus(permit.getStatus());
        vo.setTrafficPlanConfirmed(plan != null && Integer.valueOf(1).equals(plan.getConfirmed()));
        vo.setNodes(buildFlowNodes(permit));
        return vo;
    }

    @Override
    public void applyPreCheck(WorkPermitEntity permit, RoadBreakCheckPoint checkPoint, PreCheckResultVO result) {
        if (!supports(permit)) {
            return;
        }
        mergeReasons(buildPreCheckResult(permit, checkPoint), result);
    }

    private RoadBreakPreCheckResultVO buildPreCheckResult(WorkPermitEntity permit, RoadBreakCheckPoint checkPoint) {
        RoadBreakPreCheckResultVO result = new RoadBreakPreCheckResultVO();
        result.setCheckPoint(checkPoint.name());
        result.setRuleVersion(properties.getRuleVersion());
        result.setPassed(true);
        Long tenantId = permit.getTenantId();
        Long permitId = permit.getId();
        RoadBreakDetailEntity detail = findDetail(tenantId, permitId);
        RoadBreakTrafficPlanEntity plan = findTrafficPlan(tenantId, permitId);
        if (checkPoint == RoadBreakCheckPoint.SAVE) {
            validateSave(detail, result);
        } else if (checkPoint == RoadBreakCheckPoint.SUBMIT) {
            validateSubmit(detail, plan, result);
        } else if (checkPoint == RoadBreakCheckPoint.SITE_PERMIT) {
            validateSubmit(detail, plan, result);
            validateSiteControls(tenantId, permitId, result);
        } else if (checkPoint == RoadBreakCheckPoint.MONITOR) {
            validateEmergencyLane(tenantId, permitId, result);
        }
        result.setPassed(allPassed(result.getReasons()));
        return result;
    }

    private void validateSave(RoadBreakDetailEntity detail, RoadBreakPreCheckResultVO result) {
        if (detail == null) {
            addReason(result, "DETAIL_REQUIRED", LEVEL_BLOCK, "断路作业专项详情未填写");
        }
    }

    private void validateSubmit(RoadBreakDetailEntity detail, RoadBreakTrafficPlanEntity plan,
                                RoadBreakPreCheckResultVO result) {
        validateSave(detail, result);
        if (plan == null || !Integer.valueOf(1).equals(plan.getConfirmed())) {
            addReason(result, "TRAFFIC_PLAN_REQUIRED", LEVEL_BLOCK, "提交前须确认交通组织方案");
        } else if (!StringUtils.hasText(plan.getPlanRef())) {
            addReason(result, "PLAN_REF_REQUIRED", LEVEL_BLOCK, "交通方案附件必填");
        }
    }

    private void validateSiteControls(Long tenantId, Long permitId, RoadBreakPreCheckResultVO result) {
        List<RoadBreakSiteControlEntity> controls = siteControlMapper.selectList(siteControlQuery(tenantId, permitId));
        for (String code : RoadBreakSiteControlItems.SITE_PERMIT_REQUIRED) {
            if (!hasPassControl(controls, code)) {
                addReason(result, "SITE_CONTROL_" + code, LEVEL_BLOCK, "现场布控未通过: " + code);
            }
        }
    }

    private void validateEmergencyLane(Long tenantId, Long permitId, RoadBreakPreCheckResultVO result) {
        if (!hasPassControl(siteControlMapper.selectList(siteControlQuery(tenantId, permitId)), "RBSC_EMERGENCY")) {
            addReason(result, "EMERGENCY_LANE_BLOCKED", LEVEL_BLOCK, "应急通道未保持畅通");
        }
    }

    private boolean hasPassControl(List<RoadBreakSiteControlEntity> controls, String itemCode) {
        for (RoadBreakSiteControlEntity control : controls) {
            if (itemCode.equals(control.getItemCode()) && CHECK_PASS.equals(control.getCheckResult())) {
                return true;
            }
        }
        return false;
    }

    private RoadBreakCheckPoint mapCheckPoint(SpecialtyCheckPoint checkPoint) {
        if (checkPoint == SpecialtyCheckPoint.SUBMIT) {
            return RoadBreakCheckPoint.SUBMIT;
        }
        if (checkPoint == SpecialtyCheckPoint.SITE_PERMIT) {
            return RoadBreakCheckPoint.SITE_PERMIT;
        }
        if (checkPoint == SpecialtyCheckPoint.MONITOR) {
            return RoadBreakCheckPoint.MONITOR;
        }
        return null;
    }

    private void mergeReasons(RoadBreakPreCheckResultVO specialtyResult, PreCheckResultVO result) {
        for (RoadBreakPreCheckReasonVO reason : specialtyResult.getReasons()) {
            result.getReasons().add(reason.getMessage());
        }
    }

    private List<RoadBreakFlowNodeVO> buildFlowNodes(WorkPermitEntity permit) {
        String status = permit.getStatus();
        List<RoadBreakFlowNodeVO> nodes = new ArrayList<RoadBreakFlowNodeVO>();
        nodes.add(flowNode("APPLY", "申请", status, WorkPermitStatus.DRAFT.name()));
        nodes.add(flowNode("TRAFFIC", "交通影响评估", status, WorkPermitStatus.DRAFT.name()));
        nodes.add(flowNode("APPROVING", "审批中", status, WorkPermitStatus.APPROVING.name()));
        nodes.add(flowNode("PENDING", "待断路", status, WorkPermitStatus.PENDING_SITE_PERMIT.name()));
        nodes.add(flowNode("IN_PROGRESS", "断路中", status, WorkPermitStatus.IN_PROGRESS.name()));
        nodes.add(flowNode("RESTORE", "道路恢复", status, WorkPermitStatus.PENDING_ACCEPTANCE.name()));
        nodes.add(flowNode("CLOSED", "关闭", status, WorkPermitStatus.CLOSED.name()));
        return nodes;
    }

    private RoadBreakFlowNodeVO flowNode(String code, String name, String currentStatus, String matchStatus) {
        RoadBreakFlowNodeVO node = new RoadBreakFlowNodeVO();
        node.setNodeCode(code);
        node.setNodeName(name);
        node.setCurrent(matchStatus.equals(currentStatus));
        node.setStatus(node.isCurrent() ? "CURRENT" : "PENDING");
        return node;
    }

    private RoadBreakDetailEntity findDetail(Long tenantId, Long permitId) {
        return detailMapper.selectOne(new LambdaQueryWrapper<RoadBreakDetailEntity>()
                .eq(RoadBreakDetailEntity::getTenantId, tenantId)
                .eq(RoadBreakDetailEntity::getWorkPermitId, permitId)
                .eq(RoadBreakDetailEntity::getDeleted, 0)
                .last("limit 1"));
    }

    private RoadBreakTrafficPlanEntity findTrafficPlan(Long tenantId, Long permitId) {
        return trafficPlanMapper.selectOne(new LambdaQueryWrapper<RoadBreakTrafficPlanEntity>()
                .eq(RoadBreakTrafficPlanEntity::getTenantId, tenantId)
                .eq(RoadBreakTrafficPlanEntity::getWorkPermitId, permitId)
                .eq(RoadBreakTrafficPlanEntity::getDeleted, 0)
                .last("limit 1"));
    }

    private WorkPermitEntity requirePermit(Long tenantId, Long permitId) {
        WorkPermitEntity entity = workPermitMapper.selectById(permitId);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() != 0
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "work permit not found");
        }
        return entity;
    }

    private void assertRoadBreak(WorkPermitEntity permit) {
        if (!RoadBreakWorkType.isRoadBreak(permit.getWorkType())) {
            throw new BusinessException(409, "非断路作业票");
        }
        if (!properties.isEnabled()) {
            throw new BusinessException(409, "断路作业专项未启用");
        }
    }

    private LambdaQueryWrapper<RoadBreakSiteControlEntity> siteControlQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<RoadBreakSiteControlEntity>()
                .eq(RoadBreakSiteControlEntity::getTenantId, tenantId)
                .eq(RoadBreakSiteControlEntity::getWorkPermitId, permitId)
                .orderByAsc(RoadBreakSiteControlEntity::getCheckedAt);
    }

    private void addReason(RoadBreakPreCheckResultVO result, String code, String level, String message) {
        RoadBreakPreCheckReasonVO reason = new RoadBreakPreCheckReasonVO();
        reason.setCode(code);
        reason.setLevel(level);
        reason.setMessage(message);
        result.getReasons().add(reason);
    }

    private boolean allPassed(List<RoadBreakPreCheckReasonVO> reasons) {
        for (RoadBreakPreCheckReasonVO reason : reasons) {
            if (LEVEL_BLOCK.equals(reason.getLevel())) {
                return false;
            }
        }
        return true;
    }

    private RoadBreakDetailVO toDetailVO(RoadBreakDetailEntity entity) {
        RoadBreakDetailVO vo = new RoadBreakDetailVO();
        vo.setId(entity.getId());
        vo.setWorkPermitId(entity.getWorkPermitId());
        vo.setRoadId(entity.getRoadId());
        vo.setAreaGeoJson(entity.getAreaGeoJson());
        vo.setReason(entity.getReason());
        vo.setStartAt(entity.getStartAt());
        vo.setEndAt(entity.getEndAt());
        vo.setResponsibleUnit(entity.getResponsibleUnit());
        vo.setRuleVersion(properties.getRuleVersion());
        return vo;
    }

    private RoadBreakTrafficPlanVO toTrafficPlanVO(RoadBreakTrafficPlanEntity entity) {
        RoadBreakTrafficPlanVO vo = new RoadBreakTrafficPlanVO();
        vo.setId(entity.getId());
        vo.setWorkPermitId(entity.getWorkPermitId());
        vo.setDetourGeoJson(entity.getDetourGeoJson());
        vo.setEmergencyLaneGeoJson(entity.getEmergencyLaneGeoJson());
        vo.setPlanRef(entity.getPlanRef());
        vo.setConfirmed(Integer.valueOf(1).equals(entity.getConfirmed()));
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private RoadBreakSiteControlVO toSiteControlVO(RoadBreakSiteControlEntity entity) {
        RoadBreakSiteControlVO vo = new RoadBreakSiteControlVO();
        vo.setId(entity.getId());
        vo.setItemCode(entity.getItemCode());
        vo.setItemName(entity.getItemName());
        vo.setPosition(entity.getPosition());
        vo.setCheckResult(entity.getCheckResult());
        vo.setAttachmentRef(entity.getAttachmentRef());
        vo.setCheckedBy(entity.getCheckedBy());
        vo.setCheckedAt(entity.getCheckedAt());
        return vo;
    }

    private String normalizeRequired(String value, String field) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(400, field + " is required");
        }
        return value.trim();
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String normalizeOperator(String operator) {
        return StringUtils.hasText(operator) ? operator.trim() : "system";
    }
}
