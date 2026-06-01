package com.fgroupboss.ai.psm.operation.workpermit.lifting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.LiftingEquipmentCheckRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.LiftingTrialRecordRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.LiftingWorkDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.LiftingEquipmentCheckVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.LiftingPreCheckReasonVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.LiftingPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.LiftingTrialRecordVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.LiftingWorkDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.lifting.config.LiftingProperties;
import com.fgroupboss.ai.psm.operation.workpermit.lifting.config.LiftingWorkType;
import com.fgroupboss.ai.psm.operation.workpermit.lifting.mapper.LiftingEquipmentCheckMapper;
import com.fgroupboss.ai.psm.operation.workpermit.lifting.mapper.LiftingTrialRecordMapper;
import com.fgroupboss.ai.psm.operation.workpermit.lifting.mapper.LiftingWorkDetailMapper;
import com.fgroupboss.ai.psm.operation.workpermit.lifting.model.entity.LiftingEquipmentCheckEntity;
import com.fgroupboss.ai.psm.operation.workpermit.lifting.model.entity.LiftingTrialRecordEntity;
import com.fgroupboss.ai.psm.operation.workpermit.lifting.model.entity.LiftingWorkDetailEntity;
import com.fgroupboss.ai.psm.operation.workpermit.lifting.rule.LiftingLevelCalculator;
import com.fgroupboss.ai.psm.operation.workpermit.lifting.service.LiftingService;
import com.fgroupboss.ai.psm.operation.workpermit.mapper.WorkPermitMapper;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;
import com.fgroupboss.ai.psm.operation.workpermit.specialty.SpecialtyCheckPoint;
import com.fgroupboss.ai.psm.operation.workpermit.specialty.SpecialtyFlowProgressBuilder;
import com.fgroupboss.ai.psm.operation.workpermit.specialty.WorkPermitSpecialtyHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LiftingServiceImpl implements LiftingService, WorkPermitSpecialtyHandler {

    private static final String CHECK_PASS = "PASS";
    private static final String LEVEL_BLOCK = "BLOCK";
    private static final String LEVEL_WARN = "WARN";

    private static final String[] SITE_PERMIT_REQUIRED_ITEMS = {
            "LWEC_CRANE", "LWEC_SLING", "LWEC_HOOK", "LWEC_GROUND"
    };

    private final LiftingProperties properties;
    private final WorkPermitMapper workPermitMapper;
    private final LiftingWorkDetailMapper detailMapper;
    private final LiftingEquipmentCheckMapper equipmentCheckMapper;
    private final LiftingTrialRecordMapper trialRecordMapper;
    private final SpecialtyFlowProgressBuilder flowProgressBuilder;

    @Override
    public boolean supports(WorkPermitEntity permit) {
        return properties.isEnabled() && LiftingWorkType.isLifting(permit.getWorkType());
    }

    @Override
    public boolean skipGasTestAtSitePermit(WorkPermitEntity permit) {
        return false;
    }

    @Override
    public void enrichDetail(Long tenantId, WorkPermitEntity permit, WorkPermitDetailVO detail) {
        if (!supports(permit)) {
            return;
        }
        detail.setLiftingDetail(getDetail(tenantId, permit.getId()));
        detail.setLiftingEquipmentChecks(listEquipmentChecks(tenantId, permit.getId()));
        detail.setLiftingTrialRecords(listTrialRecords(tenantId, permit.getId()));
        detail.setLiftingFlowProgress(getFlowProgress(tenantId, permit.getId()));
    }

    @Override
    public LiftingWorkDetailVO getDetail(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        LiftingWorkDetailEntity entity = findDetail(tenantId, permitId);
        return entity == null ? null : toDetailVO(entity);
    }

    @Override
    @Transactional
    public LiftingWorkDetailVO saveDetail(Long tenantId, Long permitId, LiftingWorkDetailRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertLifting(permit);
        LiftingWorkDetailEntity entity = findDetail(tenantId, permitId);
        Date now = new Date();
        if (entity == null) {
            entity = new LiftingWorkDetailEntity();
            entity.setTenantId(tenantId);
            entity.setWorkPermitId(permitId);
            entity.setDeleted(0);
            entity.setCreatedAt(now);
        }
        applyDetailRequest(entity, request, permit);
        entity.setUpdatedAt(now);
        if (entity.getId() == null) {
            detailMapper.insert(entity);
        } else {
            detailMapper.updateById(entity);
        }
        return toDetailVO(entity);
    }

    @Override
    public List<LiftingEquipmentCheckVO> listEquipmentChecks(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        List<LiftingEquipmentCheckVO> result = new ArrayList<LiftingEquipmentCheckVO>();
        for (LiftingEquipmentCheckEntity entity : equipmentCheckMapper.selectList(equipmentQuery(tenantId, permitId))) {
            result.add(toEquipmentVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public LiftingEquipmentCheckVO addEquipmentCheck(Long tenantId, Long permitId,
                                                     LiftingEquipmentCheckRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertLifting(permit);
        LiftingEquipmentCheckEntity entity = new LiftingEquipmentCheckEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(permitId);
        entity.setEquipmentType(normalizeRequired(request.getEquipmentType(), "equipmentType").toUpperCase());
        entity.setEquipmentNo(normalizeRequired(request.getEquipmentNo(), "equipmentNo"));
        entity.setItemCode(normalizeRequired(request.getItemCode(), "itemCode"));
        entity.setItemName(normalizeRequired(request.getItemName(), "itemName"));
        entity.setCheckResult(normalizeRequired(request.getCheckResult(), "checkResult").toUpperCase());
        entity.setAttachmentRef(normalizeText(request.getAttachmentRef()));
        entity.setCheckedBy(normalizeOperator(operator));
        entity.setCheckedAt(new Date());
        equipmentCheckMapper.insert(entity);
        return toEquipmentVO(entity);
    }

    @Override
    public List<LiftingTrialRecordVO> listTrialRecords(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        List<LiftingTrialRecordVO> result = new ArrayList<LiftingTrialRecordVO>();
        for (LiftingTrialRecordEntity entity : trialRecordMapper.selectList(trialQuery(tenantId, permitId))) {
            result.add(toTrialVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public LiftingTrialRecordVO addTrialRecord(Long tenantId, Long permitId,
                                               LiftingTrialRecordRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertLifting(permit);
        LiftingTrialRecordEntity entity = new LiftingTrialRecordEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(permitId);
        entity.setTrialResult(normalizeRequired(request.getTrialResult(), "trialResult").toUpperCase());
        entity.setIssueDesc(normalizeText(request.getIssueDesc()));
        entity.setAttachmentRef(normalizeText(request.getAttachmentRef()));
        entity.setConfirmedBy(normalizeOperator(operator));
        entity.setTrialAt(request.getTrialAt() != null ? request.getTrialAt() : new Date());
        entity.setCreatedAt(new Date());
        trialRecordMapper.insert(entity);
        return toTrialVO(entity);
    }

    @Override
    public LiftingPreCheckResultVO preCheck(Long tenantId, Long permitId, String checkPoint) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertLifting(permit);
        return buildPreCheckResult(permit, SpecialtyCheckPoint.from(checkPoint));
    }

    @Override
    public HeightWorkFlowProgressVO getFlowProgress(Long tenantId, Long permitId) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertLifting(permit);
        LiftingWorkDetailEntity detail = findDetail(tenantId, permitId);
        String level = detail != null ? detail.getLiftingLevel() : null;
        Date validUntil = detail != null ? detail.getValidUntil() : null;
        return flowProgressBuilder.build(permit, level, validUntil);
    }

    @Override
    public void applyPreCheck(WorkPermitEntity permit, SpecialtyCheckPoint checkPoint, PreCheckResultVO result) {
        if (!supports(permit)) {
            return;
        }
        LiftingPreCheckResultVO specialtyResult = buildPreCheckResult(permit, checkPoint);
        for (LiftingPreCheckReasonVO reason : specialtyResult.getReasons()) {
            if (LEVEL_WARN.equals(reason.getLevel())) {
                result.getReasons().add("[提示]" + reason.getMessage());
            } else {
                result.getReasons().add(reason.getMessage());
            }
        }
    }

    private LiftingPreCheckResultVO buildPreCheckResult(WorkPermitEntity permit, SpecialtyCheckPoint checkPoint) {
        LiftingPreCheckResultVO result = new LiftingPreCheckResultVO();
        result.setCheckPoint(checkPoint.name());
        result.setRuleVersion(properties.getRuleVersion());
        result.setPassed(true);
        LiftingWorkDetailEntity detail = findDetail(permit.getTenantId(), permit.getId());
        if (checkPoint == SpecialtyCheckPoint.SAVE) {
            validateSave(detail, result);
        } else if (checkPoint == SpecialtyCheckPoint.SUBMIT) {
            validateSubmit(permit, detail, result);
        } else if (checkPoint == SpecialtyCheckPoint.SITE_PERMIT) {
            validateSitePermit(permit, detail, result);
        } else if (checkPoint == SpecialtyCheckPoint.RESUME) {
            validateResume(permit, detail, result);
        }
        result.setPassed(allPassed(result.getReasons()));
        return result;
    }

    private void validateSave(LiftingWorkDetailEntity detail, LiftingPreCheckResultVO result) {
        if (detail == null) {
            addReason(result, "DETAIL_REQUIRED", LEVEL_BLOCK, "吊装作业专项详情未填写");
            return;
        }
        if (detail.getLoadWeightT() == null) {
            addReason(result, "LOAD_WEIGHT_REQUIRED", LEVEL_BLOCK, "吊物重量必填");
        }
    }

    private void validateSubmit(WorkPermitEntity permit, LiftingWorkDetailEntity detail, LiftingPreCheckResultVO result) {
        validateSave(detail, result);
        if (permit.getGuardianUserId() == null) {
            addReason(result, "GUARDIAN_REQUIRED", LEVEL_BLOCK, "监护人必填");
        }
        if (detail != null && !StringUtils.hasText(detail.getLiftingLevel())) {
            addReason(result, "LEVEL_REQUIRED", LEVEL_BLOCK, "吊装级别未计算");
        }
    }

    private void validateSitePermit(WorkPermitEntity permit, LiftingWorkDetailEntity detail, LiftingPreCheckResultVO result) {
        validateSubmit(permit, detail, result);
        validateEquipmentChecks(permit.getTenantId(), permit.getId(), result);
        validateTrialRecord(permit.getTenantId(), permit.getId(), result);
    }

    private void validateResume(WorkPermitEntity permit, LiftingWorkDetailEntity detail, LiftingPreCheckResultVO result) {
        if (detail != null && detail.getValidUntil() != null && detail.getValidUntil().before(new Date())) {
            addReason(result, "PERMIT_EXPIRED", LEVEL_BLOCK, "作业票已过期，不允许恢复");
        }
    }

    private void validateEquipmentChecks(Long tenantId, Long permitId, LiftingPreCheckResultVO result) {
        List<LiftingEquipmentCheckEntity> checks = equipmentCheckMapper.selectList(equipmentQuery(tenantId, permitId));
        for (String code : SITE_PERMIT_REQUIRED_ITEMS) {
            if (!hasPassEquipmentCheck(checks, code)) {
                addReason(result, "EQUIPMENT_" + code, LEVEL_BLOCK, "设备检查未通过: " + code);
            }
        }
    }

    private void validateTrialRecord(Long tenantId, Long permitId, LiftingPreCheckResultVO result) {
        List<LiftingTrialRecordEntity> records = trialRecordMapper.selectList(trialQuery(tenantId, permitId));
        if (records.isEmpty()) {
            addReason(result, "TRIAL_MISSING", LEVEL_BLOCK, "试吊记录缺失");
            return;
        }
        LiftingTrialRecordEntity latest = records.get(records.size() - 1);
        if (!CHECK_PASS.equals(latest.getTrialResult())) {
            addReason(result, "TRIAL_FAILED", LEVEL_BLOCK, "试吊未通过");
        }
    }

    private boolean hasPassEquipmentCheck(List<LiftingEquipmentCheckEntity> checks, String itemCode) {
        for (LiftingEquipmentCheckEntity check : checks) {
            if (itemCode.equals(check.getItemCode()) && CHECK_PASS.equals(check.getCheckResult())) {
                return true;
            }
        }
        return false;
    }

    private void applyDetailRequest(LiftingWorkDetailEntity entity, LiftingWorkDetailRequest request, WorkPermitEntity permit) {
        entity.setLoadName(normalizeRequired(request.getLoadName(), "loadName"));
        entity.setLoadWeightT(request.getLoadWeightT());
        entity.setLiftingLevel(LiftingLevelCalculator.calculate(request.getLoadWeightT()));
        entity.setCraneId(request.getCraneId());
        entity.setRadiusM(request.getRadiusM());
        entity.setLiftingPoint(normalizeRequired(request.getLiftingPoint(), "liftingPoint"));
        entity.setLandingPoint(normalizeRequired(request.getLandingPoint(), "landingPoint"));
        entity.setPlanRef(normalizeText(request.getPlanRef()));
        entity.setRuleVersion(properties.getRuleVersion());
        entity.setValidUntil(calculateValidUntil(permit));
    }

    private Date calculateValidUntil(WorkPermitEntity permit) {
        Calendar calendar = Calendar.getInstance();
        if (permit.getPlanEndAt() != null) {
            calendar.setTime(permit.getPlanEndAt());
        }
        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.add(Calendar.DAY_OF_YEAR, properties.getMaxValidDays());
        if (calendar.after(maxCalendar)) {
            return maxCalendar.getTime();
        }
        return calendar.getTime();
    }

    private LiftingWorkDetailEntity findDetail(Long tenantId, Long permitId) {
        return detailMapper.selectOne(new LambdaQueryWrapper<LiftingWorkDetailEntity>()
                .eq(LiftingWorkDetailEntity::getTenantId, tenantId)
                .eq(LiftingWorkDetailEntity::getWorkPermitId, permitId)
                .eq(LiftingWorkDetailEntity::getDeleted, 0)
                .last("limit 1"));
    }

    private LambdaQueryWrapper<LiftingEquipmentCheckEntity> equipmentQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<LiftingEquipmentCheckEntity>()
                .eq(LiftingEquipmentCheckEntity::getTenantId, tenantId)
                .eq(LiftingEquipmentCheckEntity::getWorkPermitId, permitId)
                .orderByAsc(LiftingEquipmentCheckEntity::getCheckedAt);
    }

    private LambdaQueryWrapper<LiftingTrialRecordEntity> trialQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<LiftingTrialRecordEntity>()
                .eq(LiftingTrialRecordEntity::getTenantId, tenantId)
                .eq(LiftingTrialRecordEntity::getWorkPermitId, permitId)
                .orderByAsc(LiftingTrialRecordEntity::getTrialAt);
    }

    private WorkPermitEntity requirePermit(Long tenantId, Long permitId) {
        WorkPermitEntity entity = workPermitMapper.selectById(permitId);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() != 0
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "work permit not found");
        }
        return entity;
    }

    private void assertLifting(WorkPermitEntity permit) {
        if (!LiftingWorkType.isLifting(permit.getWorkType())) {
            throw new BusinessException(409, "非吊装作业票");
        }
        if (!properties.isEnabled()) {
            throw new BusinessException(409, "吊装作业专项未启用");
        }
    }

    private void addReason(LiftingPreCheckResultVO result, String code, String level, String message) {
        LiftingPreCheckReasonVO reason = new LiftingPreCheckReasonVO();
        reason.setCode(code);
        reason.setLevel(level);
        reason.setMessage(message);
        result.getReasons().add(reason);
    }

    private boolean allPassed(List<LiftingPreCheckReasonVO> reasons) {
        for (LiftingPreCheckReasonVO reason : reasons) {
            if (LEVEL_BLOCK.equals(reason.getLevel())) {
                return false;
            }
        }
        return true;
    }

    private LiftingWorkDetailVO toDetailVO(LiftingWorkDetailEntity entity) {
        LiftingWorkDetailVO vo = new LiftingWorkDetailVO();
        vo.setId(entity.getId());
        vo.setWorkPermitId(entity.getWorkPermitId());
        vo.setLoadName(entity.getLoadName());
        vo.setLoadWeightT(entity.getLoadWeightT());
        vo.setLiftingLevel(entity.getLiftingLevel());
        vo.setCraneId(entity.getCraneId());
        vo.setRadiusM(entity.getRadiusM());
        vo.setLiftingPoint(entity.getLiftingPoint());
        vo.setLandingPoint(entity.getLandingPoint());
        vo.setPlanRef(entity.getPlanRef());
        vo.setRuleVersion(entity.getRuleVersion());
        vo.setValidUntil(entity.getValidUntil());
        return vo;
    }

    private LiftingEquipmentCheckVO toEquipmentVO(LiftingEquipmentCheckEntity entity) {
        LiftingEquipmentCheckVO vo = new LiftingEquipmentCheckVO();
        vo.setId(entity.getId());
        vo.setEquipmentType(entity.getEquipmentType());
        vo.setEquipmentNo(entity.getEquipmentNo());
        vo.setItemCode(entity.getItemCode());
        vo.setItemName(entity.getItemName());
        vo.setCheckResult(entity.getCheckResult());
        vo.setAttachmentRef(entity.getAttachmentRef());
        vo.setCheckedBy(entity.getCheckedBy());
        vo.setCheckedAt(entity.getCheckedAt());
        return vo;
    }

    private LiftingTrialRecordVO toTrialVO(LiftingTrialRecordEntity entity) {
        LiftingTrialRecordVO vo = new LiftingTrialRecordVO();
        vo.setId(entity.getId());
        vo.setTrialResult(entity.getTrialResult());
        vo.setIssueDesc(entity.getIssueDesc());
        vo.setConfirmedBy(entity.getConfirmedBy());
        vo.setAttachmentRef(entity.getAttachmentRef());
        vo.setTrialAt(entity.getTrialAt());
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
