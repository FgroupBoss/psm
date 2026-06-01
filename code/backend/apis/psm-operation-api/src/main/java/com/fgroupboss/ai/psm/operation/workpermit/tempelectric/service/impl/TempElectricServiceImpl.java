package com.fgroupboss.ai.psm.operation.workpermit.tempelectric.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.TempElectricDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.TempElectricFacilityRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.TempElectricInspectionRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.TempElectricDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.TempElectricFacilityVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.TempElectricInspectionVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.TempElectricPreCheckReasonVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.TempElectricPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.mapper.WorkPermitMapper;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;
import com.fgroupboss.ai.psm.operation.workpermit.specialty.SpecialtyCheckPoint;
import com.fgroupboss.ai.psm.operation.workpermit.specialty.SpecialtyFlowProgressBuilder;
import com.fgroupboss.ai.psm.operation.workpermit.specialty.WorkPermitSpecialtyHandler;
import com.fgroupboss.ai.psm.operation.workpermit.tempelectric.config.TempElectricProperties;
import com.fgroupboss.ai.psm.operation.workpermit.tempelectric.config.TempElectricWorkType;
import com.fgroupboss.ai.psm.operation.workpermit.tempelectric.mapper.TempElectricDetailMapper;
import com.fgroupboss.ai.psm.operation.workpermit.tempelectric.mapper.TempElectricFacilityMapper;
import com.fgroupboss.ai.psm.operation.workpermit.tempelectric.mapper.TempElectricInspectionMapper;
import com.fgroupboss.ai.psm.operation.workpermit.tempelectric.model.entity.TempElectricDetailEntity;
import com.fgroupboss.ai.psm.operation.workpermit.tempelectric.model.entity.TempElectricFacilityEntity;
import com.fgroupboss.ai.psm.operation.workpermit.tempelectric.model.entity.TempElectricInspectionEntity;
import com.fgroupboss.ai.psm.operation.workpermit.tempelectric.service.TempElectricService;
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
public class TempElectricServiceImpl implements TempElectricService, WorkPermitSpecialtyHandler {

    private static final String CHECK_PASS = "PASS";
    private static final String LEVEL_BLOCK = "BLOCK";
    private static final String LEVEL_WARN = "WARN";
    private static final String FACILITY_ACTIVE = "ACTIVE";

    private final TempElectricProperties properties;
    private final WorkPermitMapper workPermitMapper;
    private final TempElectricDetailMapper detailMapper;
    private final TempElectricFacilityMapper facilityMapper;
    private final TempElectricInspectionMapper inspectionMapper;
    private final SpecialtyFlowProgressBuilder flowProgressBuilder;

    @Override
    public boolean supports(WorkPermitEntity permit) {
        return properties.isEnabled() && TempElectricWorkType.isTempElectric(permit.getWorkType());
    }

    @Override
    public boolean skipGasTestAtSitePermit(WorkPermitEntity permit) {
        return supports(permit);
    }

    @Override
    public void enrichDetail(Long tenantId, WorkPermitEntity permit, WorkPermitDetailVO detail) {
        if (!supports(permit)) {
            return;
        }
        detail.setTempElectricDetail(getDetail(tenantId, permit.getId()));
        detail.setTempElectricFacilities(listFacilities(tenantId, permit.getId()));
        detail.setTempElectricInspections(listInspections(tenantId, permit.getId()));
        detail.setTempElectricFlowProgress(getFlowProgress(tenantId, permit.getId()));
    }

    @Override
    public TempElectricDetailVO getDetail(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        TempElectricDetailEntity entity = findDetail(tenantId, permitId);
        return entity == null ? null : toDetailVO(entity);
    }

    @Override
    @Transactional
    public TempElectricDetailVO saveDetail(Long tenantId, Long permitId, TempElectricDetailRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertTempElectric(permit);
        TempElectricDetailEntity entity = findDetail(tenantId, permitId);
        Date now = new Date();
        if (entity == null) {
            entity = new TempElectricDetailEntity();
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
    public List<TempElectricFacilityVO> listFacilities(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        List<TempElectricFacilityVO> result = new ArrayList<TempElectricFacilityVO>();
        for (TempElectricFacilityEntity entity : facilityMapper.selectList(facilityQuery(tenantId, permitId))) {
            result.add(toFacilityVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public TempElectricFacilityVO addFacility(Long tenantId, Long permitId,
                                              TempElectricFacilityRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertTempElectric(permit);
        TempElectricFacilityEntity entity = new TempElectricFacilityEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(permitId);
        entity.setFacilityType(normalizeRequired(request.getFacilityType(), "facilityType").toUpperCase());
        entity.setFacilityNo(normalizeRequired(request.getFacilityNo(), "facilityNo"));
        entity.setProtectionType(normalizeRequired(request.getProtectionType(), "protectionType").toUpperCase());
        entity.setGroundingResult(normalizeRequired(request.getGroundingResult(), "groundingResult").toUpperCase());
        entity.setQrCode(normalizeText(request.getQrCode()));
        entity.setStatus(FACILITY_ACTIVE);
        entity.setDeleted(0);
        entity.setCreatedAt(new Date());
        facilityMapper.insert(entity);
        return toFacilityVO(entity);
    }

    @Override
    public List<TempElectricInspectionVO> listInspections(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        List<TempElectricInspectionVO> result = new ArrayList<TempElectricInspectionVO>();
        for (TempElectricInspectionEntity entity : inspectionMapper.selectList(inspectionQuery(tenantId, permitId))) {
            result.add(toInspectionVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public TempElectricInspectionVO addInspection(Long tenantId, Long permitId,
                                                  TempElectricInspectionRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertTempElectric(permit);
        TempElectricInspectionEntity entity = new TempElectricInspectionEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(permitId);
        entity.setFacilityId(normalizeRequiredLong(request.getFacilityId(), "facilityId"));
        entity.setInspectionResult(normalizeRequired(request.getInspectionResult(), "inspectionResult").toUpperCase());
        entity.setIssueDesc(normalizeText(request.getIssueDesc()));
        entity.setRectification(normalizeText(request.getRectification()));
        entity.setInspectedBy(normalizeOperator(operator));
        entity.setInspectedAt(request.getInspectedAt() != null ? request.getInspectedAt() : new Date());
        entity.setCreatedAt(new Date());
        inspectionMapper.insert(entity);
        return toInspectionVO(entity);
    }

    @Override
    public TempElectricPreCheckResultVO preCheck(Long tenantId, Long permitId, String checkPoint) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertTempElectric(permit);
        return buildPreCheckResult(permit, SpecialtyCheckPoint.from(checkPoint));
    }

    @Override
    public HeightWorkFlowProgressVO getFlowProgress(Long tenantId, Long permitId) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertTempElectric(permit);
        TempElectricDetailEntity detail = findDetail(tenantId, permitId);
        Date validUntil = detail != null ? detail.getValidUntil() : null;
        return flowProgressBuilder.build(permit, null, validUntil);
    }

    @Override
    public void applyPreCheck(WorkPermitEntity permit, SpecialtyCheckPoint checkPoint, PreCheckResultVO result) {
        if (!supports(permit)) {
            return;
        }
        TempElectricPreCheckResultVO specialtyResult = buildPreCheckResult(permit, checkPoint);
        for (TempElectricPreCheckReasonVO reason : specialtyResult.getReasons()) {
            if (LEVEL_WARN.equals(reason.getLevel())) {
                result.getReasons().add("[提示]" + reason.getMessage());
            } else {
                result.getReasons().add(reason.getMessage());
            }
        }
    }

    private TempElectricPreCheckResultVO buildPreCheckResult(WorkPermitEntity permit, SpecialtyCheckPoint checkPoint) {
        TempElectricPreCheckResultVO result = new TempElectricPreCheckResultVO();
        result.setCheckPoint(checkPoint.name());
        result.setRuleVersion(properties.getRuleVersion());
        result.setPassed(true);
        TempElectricDetailEntity detail = findDetail(permit.getTenantId(), permit.getId());
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

    private void validateSave(TempElectricDetailEntity detail, TempElectricPreCheckResultVO result) {
        if (detail == null) {
            addReason(result, "DETAIL_REQUIRED", LEVEL_BLOCK, "临时用电专项详情未填写");
            return;
        }
        if (!StringUtils.hasText(detail.getVoltage())) {
            addReason(result, "VOLTAGE_REQUIRED", LEVEL_BLOCK, "电压等级必填");
        }
    }

    private void validateSubmit(WorkPermitEntity permit, TempElectricDetailEntity detail, TempElectricPreCheckResultVO result) {
        validateSave(detail, result);
        if (permit.getGuardianUserId() == null) {
            addReason(result, "GUARDIAN_REQUIRED", LEVEL_BLOCK, "监护人必填");
        }
    }

    private void validateSitePermit(WorkPermitEntity permit, TempElectricDetailEntity detail, TempElectricPreCheckResultVO result) {
        validateSubmit(permit, detail, result);
        validateGroundingFacilities(permit.getTenantId(), permit.getId(), result);
    }

    private void validateResume(WorkPermitEntity permit, TempElectricDetailEntity detail, TempElectricPreCheckResultVO result) {
        if (detail != null && detail.getValidUntil() != null && detail.getValidUntil().before(new Date())) {
            addReason(result, "PERMIT_EXPIRED", LEVEL_BLOCK, "作业票已过期，不允许恢复");
        }
    }

    private void validateGroundingFacilities(Long tenantId, Long permitId, TempElectricPreCheckResultVO result) {
        List<TempElectricFacilityEntity> facilities = facilityMapper.selectList(
                facilityQuery(tenantId, permitId).eq(TempElectricFacilityEntity::getStatus, FACILITY_ACTIVE));
        if (facilities.isEmpty()) {
            addReason(result, "FACILITY_MISSING", LEVEL_BLOCK, "配电设施缺失");
            return;
        }
        boolean hasPassGrounding = false;
        for (TempElectricFacilityEntity facility : facilities) {
            if (CHECK_PASS.equals(facility.getGroundingResult())) {
                hasPassGrounding = true;
                break;
            }
        }
        if (!hasPassGrounding) {
            addReason(result, "GROUNDING_FAILED", LEVEL_BLOCK, "接地检测未通过");
        }
    }

    private void applyDetailRequest(TempElectricDetailEntity entity, TempElectricDetailRequest request, WorkPermitEntity permit) {
        entity.setSourceId(request.getSourceId());
        entity.setVoltage(normalizeRequired(request.getVoltage(), "voltage"));
        entity.setLoadKw(request.getLoadKw());
        entity.setHazardousAreaFlag(Boolean.TRUE.equals(request.getHazardousAreaFlag()) ? 1 : 0);
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

    private TempElectricDetailEntity findDetail(Long tenantId, Long permitId) {
        return detailMapper.selectOne(new LambdaQueryWrapper<TempElectricDetailEntity>()
                .eq(TempElectricDetailEntity::getTenantId, tenantId)
                .eq(TempElectricDetailEntity::getWorkPermitId, permitId)
                .eq(TempElectricDetailEntity::getDeleted, 0)
                .last("limit 1"));
    }

    private LambdaQueryWrapper<TempElectricFacilityEntity> facilityQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<TempElectricFacilityEntity>()
                .eq(TempElectricFacilityEntity::getTenantId, tenantId)
                .eq(TempElectricFacilityEntity::getWorkPermitId, permitId)
                .eq(TempElectricFacilityEntity::getDeleted, 0);
    }

    private LambdaQueryWrapper<TempElectricInspectionEntity> inspectionQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<TempElectricInspectionEntity>()
                .eq(TempElectricInspectionEntity::getTenantId, tenantId)
                .eq(TempElectricInspectionEntity::getWorkPermitId, permitId)
                .orderByAsc(TempElectricInspectionEntity::getInspectedAt);
    }

    private WorkPermitEntity requirePermit(Long tenantId, Long permitId) {
        WorkPermitEntity entity = workPermitMapper.selectById(permitId);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() != 0
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "work permit not found");
        }
        return entity;
    }

    private void assertTempElectric(WorkPermitEntity permit) {
        if (!TempElectricWorkType.isTempElectric(permit.getWorkType())) {
            throw new BusinessException(409, "非临时用电作业票");
        }
        if (!properties.isEnabled()) {
            throw new BusinessException(409, "临时用电专项未启用");
        }
    }

    private void addReason(TempElectricPreCheckResultVO result, String code, String level, String message) {
        TempElectricPreCheckReasonVO reason = new TempElectricPreCheckReasonVO();
        reason.setCode(code);
        reason.setLevel(level);
        reason.setMessage(message);
        result.getReasons().add(reason);
    }

    private boolean allPassed(List<TempElectricPreCheckReasonVO> reasons) {
        for (TempElectricPreCheckReasonVO reason : reasons) {
            if (LEVEL_BLOCK.equals(reason.getLevel())) {
                return false;
            }
        }
        return true;
    }

    private TempElectricDetailVO toDetailVO(TempElectricDetailEntity entity) {
        TempElectricDetailVO vo = new TempElectricDetailVO();
        vo.setId(entity.getId());
        vo.setWorkPermitId(entity.getWorkPermitId());
        vo.setSourceId(entity.getSourceId());
        vo.setVoltage(entity.getVoltage());
        vo.setLoadKw(entity.getLoadKw());
        vo.setHazardousAreaFlag(Integer.valueOf(1).equals(entity.getHazardousAreaFlag()));
        vo.setPlanRef(entity.getPlanRef());
        vo.setRuleVersion(entity.getRuleVersion());
        vo.setValidUntil(entity.getValidUntil());
        return vo;
    }

    private TempElectricFacilityVO toFacilityVO(TempElectricFacilityEntity entity) {
        TempElectricFacilityVO vo = new TempElectricFacilityVO();
        vo.setId(entity.getId());
        vo.setFacilityType(entity.getFacilityType());
        vo.setFacilityNo(entity.getFacilityNo());
        vo.setProtectionType(entity.getProtectionType());
        vo.setGroundingResult(entity.getGroundingResult());
        vo.setQrCode(entity.getQrCode());
        vo.setStatus(entity.getStatus());
        return vo;
    }

    private TempElectricInspectionVO toInspectionVO(TempElectricInspectionEntity entity) {
        TempElectricInspectionVO vo = new TempElectricInspectionVO();
        vo.setId(entity.getId());
        vo.setFacilityId(entity.getFacilityId());
        vo.setInspectionResult(entity.getInspectionResult());
        vo.setIssueDesc(entity.getIssueDesc());
        vo.setRectification(entity.getRectification());
        vo.setInspectedBy(entity.getInspectedBy());
        vo.setInspectedAt(entity.getInspectedAt());
        return vo;
    }

    private String normalizeRequired(String value, String field) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(400, field + " is required");
        }
        return value.trim();
    }

    private Long normalizeRequiredLong(Long value, String field) {
        if (value == null) {
            throw new BusinessException(400, field + " is required");
        }
        return value;
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String normalizeOperator(String operator) {
        return StringUtils.hasText(operator) ? operator.trim() : "system";
    }
}
