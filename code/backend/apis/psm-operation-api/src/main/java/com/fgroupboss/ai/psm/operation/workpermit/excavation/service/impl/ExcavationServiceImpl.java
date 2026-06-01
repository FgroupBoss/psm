package com.fgroupboss.ai.psm.operation.workpermit.excavation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ExcavationCountersignRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ExcavationSiteCheckRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ExcavationUndergroundFacilityRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ExcavationWorkDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationCountersignVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationFlowNodeVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationPreCheckReasonVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationSiteCheckVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationUndergroundFacilityVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ExcavationWorkDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.config.WorkPermitStatus;
import com.fgroupboss.ai.psm.operation.workpermit.excavation.config.ExcavationCheckPoint;
import com.fgroupboss.ai.psm.operation.workpermit.excavation.config.ExcavationProperties;
import com.fgroupboss.ai.psm.operation.workpermit.excavation.config.ExcavationSiteCheckItems;
import com.fgroupboss.ai.psm.operation.workpermit.excavation.config.ExcavationWorkType;
import com.fgroupboss.ai.psm.operation.workpermit.excavation.mapper.ExcavationCountersignMapper;
import com.fgroupboss.ai.psm.operation.workpermit.excavation.mapper.ExcavationSiteCheckMapper;
import com.fgroupboss.ai.psm.operation.workpermit.excavation.mapper.ExcavationUndergroundFacilityMapper;
import com.fgroupboss.ai.psm.operation.workpermit.excavation.mapper.ExcavationWorkDetailMapper;
import com.fgroupboss.ai.psm.operation.workpermit.excavation.model.entity.ExcavationCountersignEntity;
import com.fgroupboss.ai.psm.operation.workpermit.excavation.model.entity.ExcavationSiteCheckEntity;
import com.fgroupboss.ai.psm.operation.workpermit.excavation.model.entity.ExcavationUndergroundFacilityEntity;
import com.fgroupboss.ai.psm.operation.workpermit.excavation.model.entity.ExcavationWorkDetailEntity;
import com.fgroupboss.ai.psm.operation.workpermit.excavation.service.ExcavationService;
import com.fgroupboss.ai.psm.operation.workpermit.mapper.WorkPermitMapper;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;
import com.fgroupboss.ai.psm.operation.workpermit.specialty.SpecialtyCheckPoint;
import com.fgroupboss.ai.psm.operation.workpermit.specialty.WorkPermitSpecialtyHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExcavationServiceImpl implements ExcavationService, WorkPermitSpecialtyHandler {

    private static final String LEVEL_BLOCK = "BLOCK";
    private static final String CHECK_PASS = "PASS";
    private static final String RESULT_APPROVED = "APPROVED";
    private static final String STAGE_SITE_PERMIT = "SITE_PERMIT";

    private final ExcavationProperties properties;
    private final WorkPermitMapper workPermitMapper;
    private final ExcavationWorkDetailMapper detailMapper;
    private final ExcavationUndergroundFacilityMapper facilityMapper;
    private final ExcavationCountersignMapper countersignMapper;
    private final ExcavationSiteCheckMapper siteCheckMapper;

    @Override
    public boolean supports(WorkPermitEntity permit) {
        return properties.isEnabled() && ExcavationWorkType.isExcavation(permit.getWorkType());
    }

    @Override
    public void applyPreCheck(WorkPermitEntity permit, SpecialtyCheckPoint checkPoint, PreCheckResultVO result) {
        if (!supports(permit)) {
            return;
        }
        ExcavationCheckPoint point = mapCheckPoint(checkPoint);
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
        detail.setExcavationDetail(getDetail(tenantId, permitId));
        detail.setExcavationFacilities(listFacilities(tenantId, permitId));
        detail.setExcavationCountersigns(listCountersigns(tenantId, permitId));
        detail.setExcavationSiteChecks(listSiteChecks(tenantId, permitId, null));
    }

    @Override
    public boolean skipGasTestAtSitePermit(WorkPermitEntity permit) {
        return supports(permit);
    }

    @Override
    public ExcavationWorkDetailVO getDetail(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        ExcavationWorkDetailEntity entity = findDetail(tenantId, permitId);
        return entity == null ? null : toDetailVO(entity);
    }

    @Override
    @Transactional
    public ExcavationWorkDetailVO saveDetail(Long tenantId, Long permitId,
                                             ExcavationWorkDetailRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertExcavation(permit);
        Date now = new Date();
        ExcavationWorkDetailEntity entity = findDetail(tenantId, permitId);
        if (entity == null) {
            entity = new ExcavationWorkDetailEntity();
            entity.setTenantId(tenantId);
            entity.setWorkPermitId(permitId);
            entity.setDeleted(0);
            entity.setCreatedAt(now);
        }
        entity.setAreaGeoJson(normalizeText(request.getAreaGeoJson()));
        entity.setDepthM(request.getDepthM());
        entity.setAreaM2(request.getAreaM2());
        entity.setMethod(normalizeRequired(request.getMethod(), "method").toUpperCase());
        entity.setDrawingRef(normalizeRequired(request.getDrawingRef(), "drawingRef"));
        entity.setValidUntil(calculateValidUntil(permit));
        entity.setUpdatedAt(now);
        if (entity.getId() == null) {
            detailMapper.insert(entity);
        } else {
            detailMapper.updateById(entity);
        }
        return toDetailVO(entity);
    }

    @Override
    public List<ExcavationUndergroundFacilityVO> listFacilities(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        List<ExcavationUndergroundFacilityVO> result = new ArrayList<ExcavationUndergroundFacilityVO>();
        for (ExcavationUndergroundFacilityEntity entity : facilityMapper.selectList(facilityQuery(tenantId, permitId))) {
            result.add(toFacilityVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public ExcavationUndergroundFacilityVO addFacility(Long tenantId, Long permitId,
                                                       ExcavationUndergroundFacilityRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertExcavation(permit);
        ExcavationUndergroundFacilityEntity entity = new ExcavationUndergroundFacilityEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(permitId);
        entity.setFacilityType(normalizeRequired(request.getFacilityType(), "facilityType"));
        entity.setOwnerUnit(normalizeText(request.getOwnerUnit()));
        entity.setPosition(normalizeRequired(request.getPosition(), "position"));
        entity.setDepthM(request.getDepthM());
        entity.setDetectionMethod(normalizeText(request.getDetectionMethod()));
        entity.setConfirmed(Boolean.TRUE.equals(request.getConfirmed()) ? 1 : 0);
        entity.setDeleted(0);
        entity.setCreatedAt(new Date());
        facilityMapper.insert(entity);
        return toFacilityVO(entity);
    }

    @Override
    public List<ExcavationCountersignVO> listCountersigns(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        List<ExcavationCountersignVO> result = new ArrayList<ExcavationCountersignVO>();
        for (ExcavationCountersignEntity entity : countersignMapper.selectList(countersignQuery(tenantId, permitId))) {
            result.add(toCountersignVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public ExcavationCountersignVO addCountersign(Long tenantId, Long permitId,
                                                  ExcavationCountersignRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertExcavation(permit);
        String specialty = normalizeRequired(request.getSpecialty(), "specialty").toUpperCase();
        if (request.getSignerId() == null) {
            throw new BusinessException(400, "signerId is required");
        }
        String result = normalizeRequired(request.getResult(), "result").toUpperCase();
        ExcavationCountersignEntity entity = new ExcavationCountersignEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(permitId);
        entity.setSpecialty(specialty);
        entity.setSignerId(request.getSignerId());
        entity.setResult(result);
        entity.setOpinion(normalizeText(request.getOpinion()));
        entity.setSignedAt(new Date());
        entity.setDeleted(0);
        entity.setCreatedAt(new Date());
        countersignMapper.insert(entity);
        return toCountersignVO(entity);
    }

    @Override
    public List<ExcavationSiteCheckVO> listSiteChecks(Long tenantId, Long permitId, String stage) {
        requirePermit(tenantId, permitId);
        LambdaQueryWrapper<ExcavationSiteCheckEntity> wrapper = siteCheckQuery(tenantId, permitId);
        if (StringUtils.hasText(stage)) {
            wrapper.eq(ExcavationSiteCheckEntity::getStage, stage.trim().toUpperCase());
        }
        List<ExcavationSiteCheckVO> result = new ArrayList<ExcavationSiteCheckVO>();
        for (ExcavationSiteCheckEntity entity : siteCheckMapper.selectList(wrapper)) {
            result.add(toSiteCheckVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public ExcavationSiteCheckVO addSiteCheck(Long tenantId, Long permitId,
                                              ExcavationSiteCheckRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertExcavation(permit);
        ExcavationSiteCheckEntity entity = new ExcavationSiteCheckEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(permitId);
        entity.setStage(normalizeRequired(request.getStage(), "stage").toUpperCase());
        entity.setItemCode(normalizeRequired(request.getItemCode(), "itemCode"));
        entity.setItemName(normalizeRequired(request.getItemName(), "itemName"));
        entity.setCheckResult(normalizeRequired(request.getCheckResult(), "checkResult").toUpperCase());
        entity.setAttachmentRef(normalizeText(request.getAttachmentRef()));
        entity.setCheckedBy(normalizeOperator(operator));
        entity.setCheckedAt(new Date());
        siteCheckMapper.insert(entity);
        return toSiteCheckVO(entity);
    }

    @Override
    public ExcavationPreCheckResultVO preCheck(Long tenantId, Long permitId, String checkPoint) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertExcavation(permit);
        return buildPreCheckResult(permit, ExcavationCheckPoint.from(checkPoint));
    }

    @Override
    public ExcavationFlowProgressVO getFlowProgress(Long tenantId, Long permitId) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertExcavation(permit);
        ExcavationFlowProgressVO vo = new ExcavationFlowProgressVO();
        vo.setPermitStatus(permit.getStatus());
        vo.setNodes(buildFlowNodes(permit));
        return vo;
    }

    @Override
    public void applyPreCheck(WorkPermitEntity permit, ExcavationCheckPoint checkPoint, PreCheckResultVO result) {
        if (!supports(permit)) {
            return;
        }
        mergeReasons(buildPreCheckResult(permit, checkPoint), result);
    }

    private ExcavationPreCheckResultVO buildPreCheckResult(WorkPermitEntity permit, ExcavationCheckPoint checkPoint) {
        ExcavationPreCheckResultVO result = new ExcavationPreCheckResultVO();
        result.setCheckPoint(checkPoint.name());
        result.setRuleVersion(properties.getRuleVersion());
        result.setPassed(true);
        Long tenantId = permit.getTenantId();
        Long permitId = permit.getId();
        ExcavationWorkDetailEntity detail = findDetail(tenantId, permitId);
        if (checkPoint == ExcavationCheckPoint.SAVE) {
            validateSave(detail, result);
        } else if (checkPoint == ExcavationCheckPoint.SUBMIT) {
            validateSubmit(detail, result);
            validateCountersigns(tenantId, permitId, result);
        } else if (checkPoint == ExcavationCheckPoint.SITE_PERMIT) {
            validateSubmit(detail, result);
            validateCountersigns(tenantId, permitId, result);
            validateSiteChecks(tenantId, permitId, STAGE_SITE_PERMIT, result);
        } else if (checkPoint == ExcavationCheckPoint.RESUME) {
            validateSiteChecks(tenantId, permitId, STAGE_SITE_PERMIT, result);
        }
        result.setPassed(allPassed(result.getReasons()));
        return result;
    }

    private void validateSave(ExcavationWorkDetailEntity detail, ExcavationPreCheckResultVO result) {
        if (detail == null) {
            addReason(result, "DETAIL_REQUIRED", LEVEL_BLOCK, "动土作业专项详情未填写");
            return;
        }
        if (detail.getDepthM() == null) {
            addReason(result, "DEPTH_REQUIRED", LEVEL_BLOCK, "开挖深度必填");
        }
        if (!StringUtils.hasText(detail.getDrawingRef())) {
            addReason(result, "DRAWING_REQUIRED", LEVEL_BLOCK, "施工图纸附件必填");
        }
    }

    private void validateSubmit(ExcavationWorkDetailEntity detail, ExcavationPreCheckResultVO result) {
        validateSave(detail, result);
    }

    private void validateCountersigns(Long tenantId, Long permitId, ExcavationPreCheckResultVO result) {
        List<ExcavationCountersignEntity> countersigns = countersignMapper.selectList(countersignQuery(tenantId, permitId));
        Map<String, ExcavationCountersignEntity> approved = new HashMap<String, ExcavationCountersignEntity>();
        for (ExcavationCountersignEntity entity : countersigns) {
            if (RESULT_APPROVED.equals(entity.getResult())) {
                approved.put(entity.getSpecialty(), entity);
            } else {
                addReason(result, "COUNTERSIGN_" + entity.getSpecialty(), LEVEL_BLOCK,
                        "专业会签未通过: " + entity.getSpecialty());
            }
        }
        for (String specialty : properties.getRequiredCountersignSpecialties()) {
            if (!approved.containsKey(specialty.toUpperCase())) {
                addReason(result, "COUNTERSIGN_MISSING_" + specialty, LEVEL_BLOCK,
                        "缺少专业会签: " + specialty);
            }
        }
    }

    private void validateSiteChecks(Long tenantId, Long permitId, String stage, ExcavationPreCheckResultVO result) {
        List<ExcavationSiteCheckEntity> checks = siteCheckMapper.selectList(
                siteCheckQuery(tenantId, permitId).eq(ExcavationSiteCheckEntity::getStage, stage));
        for (String code : ExcavationSiteCheckItems.SITE_PERMIT_REQUIRED) {
            if (!hasPassCheck(checks, code)) {
                addReason(result, "SITE_CHECK_" + code, LEVEL_BLOCK, "现场检查未通过: " + code);
            }
        }
    }

    private boolean hasPassCheck(List<ExcavationSiteCheckEntity> checks, String itemCode) {
        for (ExcavationSiteCheckEntity check : checks) {
            if (itemCode.equals(check.getItemCode()) && CHECK_PASS.equals(check.getCheckResult())) {
                return true;
            }
        }
        return false;
    }

    private ExcavationCheckPoint mapCheckPoint(SpecialtyCheckPoint checkPoint) {
        if (checkPoint == SpecialtyCheckPoint.SUBMIT) {
            return ExcavationCheckPoint.SUBMIT;
        }
        if (checkPoint == SpecialtyCheckPoint.SITE_PERMIT) {
            return ExcavationCheckPoint.SITE_PERMIT;
        }
        if (checkPoint == SpecialtyCheckPoint.RESUME) {
            return ExcavationCheckPoint.RESUME;
        }
        return null;
    }

    private void mergeReasons(ExcavationPreCheckResultVO specialtyResult, PreCheckResultVO result) {
        for (ExcavationPreCheckReasonVO reason : specialtyResult.getReasons()) {
            result.getReasons().add(reason.getMessage());
        }
    }

    private List<ExcavationFlowNodeVO> buildFlowNodes(WorkPermitEntity permit) {
        String status = permit.getStatus();
        List<ExcavationFlowNodeVO> nodes = new ArrayList<ExcavationFlowNodeVO>();
        nodes.add(flowNode("APPLY", "申请", status, WorkPermitStatus.DRAFT.name()));
        nodes.add(flowNode("FACILITY", "地下设施识别及会签", status, WorkPermitStatus.DRAFT.name()));
        nodes.add(flowNode("APPROVING", "审批中", status, WorkPermitStatus.APPROVING.name()));
        nodes.add(flowNode("PENDING", "待作业", status, WorkPermitStatus.PENDING_SITE_PERMIT.name()));
        nodes.add(flowNode("IN_PROGRESS", "开挖中", status, WorkPermitStatus.IN_PROGRESS.name()));
        nodes.add(flowNode("RESTORE", "回填恢复", status, WorkPermitStatus.PENDING_ACCEPTANCE.name()));
        nodes.add(flowNode("CLOSED", "关闭", status, WorkPermitStatus.CLOSED.name()));
        return nodes;
    }

    private ExcavationFlowNodeVO flowNode(String code, String name, String currentStatus, String matchStatus) {
        ExcavationFlowNodeVO node = new ExcavationFlowNodeVO();
        node.setNodeCode(code);
        node.setNodeName(name);
        node.setCurrent(matchStatus.equals(currentStatus));
        node.setStatus(node.isCurrent() ? "CURRENT" : "PENDING");
        return node;
    }

    private Date calculateValidUntil(WorkPermitEntity permit) {
        Calendar calendar = Calendar.getInstance();
        if (permit.getPlanEndAt() != null) {
            calendar.setTime(permit.getPlanEndAt());
        }
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        return calendar.getTime();
    }

    private ExcavationWorkDetailEntity findDetail(Long tenantId, Long permitId) {
        return detailMapper.selectOne(new LambdaQueryWrapper<ExcavationWorkDetailEntity>()
                .eq(ExcavationWorkDetailEntity::getTenantId, tenantId)
                .eq(ExcavationWorkDetailEntity::getWorkPermitId, permitId)
                .eq(ExcavationWorkDetailEntity::getDeleted, 0)
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

    private void assertExcavation(WorkPermitEntity permit) {
        if (!ExcavationWorkType.isExcavation(permit.getWorkType())) {
            throw new BusinessException(409, "非动土作业票");
        }
        if (!properties.isEnabled()) {
            throw new BusinessException(409, "动土作业专项未启用");
        }
    }

    private LambdaQueryWrapper<ExcavationUndergroundFacilityEntity> facilityQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<ExcavationUndergroundFacilityEntity>()
                .eq(ExcavationUndergroundFacilityEntity::getTenantId, tenantId)
                .eq(ExcavationUndergroundFacilityEntity::getWorkPermitId, permitId)
                .eq(ExcavationUndergroundFacilityEntity::getDeleted, 0);
    }

    private LambdaQueryWrapper<ExcavationCountersignEntity> countersignQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<ExcavationCountersignEntity>()
                .eq(ExcavationCountersignEntity::getTenantId, tenantId)
                .eq(ExcavationCountersignEntity::getWorkPermitId, permitId)
                .eq(ExcavationCountersignEntity::getDeleted, 0);
    }

    private LambdaQueryWrapper<ExcavationSiteCheckEntity> siteCheckQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<ExcavationSiteCheckEntity>()
                .eq(ExcavationSiteCheckEntity::getTenantId, tenantId)
                .eq(ExcavationSiteCheckEntity::getWorkPermitId, permitId)
                .orderByAsc(ExcavationSiteCheckEntity::getCheckedAt);
    }

    private void addReason(ExcavationPreCheckResultVO result, String code, String level, String message) {
        ExcavationPreCheckReasonVO reason = new ExcavationPreCheckReasonVO();
        reason.setCode(code);
        reason.setLevel(level);
        reason.setMessage(message);
        result.getReasons().add(reason);
    }

    private boolean allPassed(List<ExcavationPreCheckReasonVO> reasons) {
        for (ExcavationPreCheckReasonVO reason : reasons) {
            if (LEVEL_BLOCK.equals(reason.getLevel())) {
                return false;
            }
        }
        return true;
    }

    private ExcavationWorkDetailVO toDetailVO(ExcavationWorkDetailEntity entity) {
        ExcavationWorkDetailVO vo = new ExcavationWorkDetailVO();
        vo.setId(entity.getId());
        vo.setWorkPermitId(entity.getWorkPermitId());
        vo.setAreaGeoJson(entity.getAreaGeoJson());
        vo.setDepthM(entity.getDepthM());
        vo.setAreaM2(entity.getAreaM2());
        vo.setMethod(entity.getMethod());
        vo.setDrawingRef(entity.getDrawingRef());
        vo.setRuleVersion(properties.getRuleVersion());
        vo.setValidUntil(entity.getValidUntil());
        return vo;
    }

    private ExcavationUndergroundFacilityVO toFacilityVO(ExcavationUndergroundFacilityEntity entity) {
        ExcavationUndergroundFacilityVO vo = new ExcavationUndergroundFacilityVO();
        vo.setId(entity.getId());
        vo.setFacilityType(entity.getFacilityType());
        vo.setOwnerUnit(entity.getOwnerUnit());
        vo.setPosition(entity.getPosition());
        vo.setDepthM(entity.getDepthM());
        vo.setDetectionMethod(entity.getDetectionMethod());
        vo.setConfirmed(Integer.valueOf(1).equals(entity.getConfirmed()));
        vo.setCreatedAt(entity.getCreatedAt());
        return vo;
    }

    private ExcavationCountersignVO toCountersignVO(ExcavationCountersignEntity entity) {
        ExcavationCountersignVO vo = new ExcavationCountersignVO();
        vo.setId(entity.getId());
        vo.setSpecialty(entity.getSpecialty());
        vo.setSignerId(entity.getSignerId());
        vo.setResult(entity.getResult());
        vo.setOpinion(entity.getOpinion());
        vo.setSignedAt(entity.getSignedAt());
        return vo;
    }

    private ExcavationSiteCheckVO toSiteCheckVO(ExcavationSiteCheckEntity entity) {
        ExcavationSiteCheckVO vo = new ExcavationSiteCheckVO();
        vo.setId(entity.getId());
        vo.setStage(entity.getStage());
        vo.setItemCode(entity.getItemCode());
        vo.setItemName(entity.getItemName());
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
