package com.fgroupboss.ai.psm.operation.workpermit.confinedspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ConfinedSpaceDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ConfinedSpaceEntryRecordRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.ConfinedSpaceRescuePlanRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ConfinedSpaceDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ConfinedSpaceEntryRecordVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ConfinedSpacePreCheckReasonVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ConfinedSpacePreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.ConfinedSpaceRescuePlanVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.confinedspace.config.ConfinedSpaceProperties;
import com.fgroupboss.ai.psm.operation.workpermit.confinedspace.config.ConfinedSpaceWorkType;
import com.fgroupboss.ai.psm.operation.workpermit.confinedspace.mapper.ConfinedSpaceDetailMapper;
import com.fgroupboss.ai.psm.operation.workpermit.confinedspace.mapper.ConfinedSpaceEntryRecordMapper;
import com.fgroupboss.ai.psm.operation.workpermit.confinedspace.mapper.ConfinedSpaceRescuePlanMapper;
import com.fgroupboss.ai.psm.operation.workpermit.confinedspace.model.entity.ConfinedSpaceDetailEntity;
import com.fgroupboss.ai.psm.operation.workpermit.confinedspace.model.entity.ConfinedSpaceEntryRecordEntity;
import com.fgroupboss.ai.psm.operation.workpermit.confinedspace.model.entity.ConfinedSpaceRescuePlanEntity;
import com.fgroupboss.ai.psm.operation.workpermit.confinedspace.service.ConfinedSpaceService;
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
public class ConfinedSpaceServiceImpl implements ConfinedSpaceService, WorkPermitSpecialtyHandler {

    private static final String LEVEL_BLOCK = "BLOCK";
    private static final String LEVEL_WARN = "WARN";

    private final ConfinedSpaceProperties properties;
    private final WorkPermitMapper workPermitMapper;
    private final ConfinedSpaceDetailMapper detailMapper;
    private final ConfinedSpaceEntryRecordMapper entryRecordMapper;
    private final ConfinedSpaceRescuePlanMapper rescuePlanMapper;
    private final SpecialtyFlowProgressBuilder flowProgressBuilder;

    @Override
    public boolean supports(WorkPermitEntity permit) {
        return properties.isEnabled() && ConfinedSpaceWorkType.isConfinedSpace(permit.getWorkType());
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
        detail.setConfinedSpaceDetail(getDetail(tenantId, permit.getId()));
        detail.setConfinedSpaceEntryRecords(listEntryRecords(tenantId, permit.getId()));
        detail.setConfinedSpaceRescuePlan(getRescuePlan(tenantId, permit.getId()));
        detail.setConfinedSpaceFlowProgress(getFlowProgress(tenantId, permit.getId()));
    }

    @Override
    public ConfinedSpaceDetailVO getDetail(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        ConfinedSpaceDetailEntity entity = findDetail(tenantId, permitId);
        return entity == null ? null : toDetailVO(entity);
    }

    @Override
    @Transactional
    public ConfinedSpaceDetailVO saveDetail(Long tenantId, Long permitId, ConfinedSpaceDetailRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertConfinedSpace(permit);
        ConfinedSpaceDetailEntity entity = findDetail(tenantId, permitId);
        Date now = new Date();
        if (entity == null) {
            entity = new ConfinedSpaceDetailEntity();
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
    public List<ConfinedSpaceEntryRecordVO> listEntryRecords(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        List<ConfinedSpaceEntryRecordVO> result = new ArrayList<ConfinedSpaceEntryRecordVO>();
        for (ConfinedSpaceEntryRecordEntity entity : entryRecordMapper.selectList(entryQuery(tenantId, permitId))) {
            result.add(toEntryVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public ConfinedSpaceEntryRecordVO addEntryRecord(Long tenantId, Long permitId,
                                                     ConfinedSpaceEntryRecordRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertConfinedSpace(permit);
        ConfinedSpaceEntryRecordEntity entity = new ConfinedSpaceEntryRecordEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(permitId);
        entity.setWorkerId(request.getWorkerId());
        entity.setWorkerName(normalizeRequired(request.getWorkerName(), "workerName"));
        entity.setAction(normalizeRequired(request.getAction(), "action").toUpperCase());
        entity.setOperatedAt(request.getOperatedAt() != null ? request.getOperatedAt() : new Date());
        entity.setLocation(normalizeText(request.getLocation()));
        entity.setOperator(normalizeOperator(operator));
        entity.setDeleted(0);
        entity.setCreatedAt(new Date());
        entryRecordMapper.insert(entity);
        return toEntryVO(entity);
    }

    @Override
    public ConfinedSpaceRescuePlanVO getRescuePlan(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        ConfinedSpaceRescuePlanEntity entity = findRescuePlan(tenantId, permitId);
        return entity == null ? null : toRescuePlanVO(entity);
    }

    @Override
    @Transactional
    public ConfinedSpaceRescuePlanVO saveRescuePlan(Long tenantId, Long permitId,
                                                    ConfinedSpaceRescuePlanRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertConfinedSpace(permit);
        ConfinedSpaceRescuePlanEntity entity = findRescuePlan(tenantId, permitId);
        Date now = new Date();
        if (entity == null) {
            entity = new ConfinedSpaceRescuePlanEntity();
            entity.setTenantId(tenantId);
            entity.setWorkPermitId(permitId);
            entity.setDeleted(0);
            entity.setCreatedAt(now);
        }
        entity.setPlanRef(normalizeRequired(request.getPlanRef(), "planRef"));
        entity.setContact(normalizeRequired(request.getContact(), "contact"));
        entity.setEquipmentJson(normalizeText(request.getEquipmentJson()));
        if (Boolean.TRUE.equals(request.getConfirm())) {
            entity.setConfirmedBy(normalizeOperator(operator));
            entity.setConfirmedAt(now);
        }
        entity.setUpdatedAt(now);
        if (entity.getId() == null) {
            rescuePlanMapper.insert(entity);
        } else {
            rescuePlanMapper.updateById(entity);
        }
        return toRescuePlanVO(entity);
    }

    @Override
    public ConfinedSpacePreCheckResultVO preCheck(Long tenantId, Long permitId, String checkPoint) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertConfinedSpace(permit);
        return buildPreCheckResult(permit, SpecialtyCheckPoint.from(checkPoint));
    }

    @Override
    public HeightWorkFlowProgressVO getFlowProgress(Long tenantId, Long permitId) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertConfinedSpace(permit);
        ConfinedSpaceDetailEntity detail = findDetail(tenantId, permitId);
        Date validUntil = detail != null ? detail.getValidUntil() : null;
        return flowProgressBuilder.build(permit, null, validUntil);
    }

    @Override
    public void applyPreCheck(WorkPermitEntity permit, SpecialtyCheckPoint checkPoint, PreCheckResultVO result) {
        if (!supports(permit)) {
            return;
        }
        ConfinedSpacePreCheckResultVO specialtyResult = buildPreCheckResult(permit, checkPoint);
        for (ConfinedSpacePreCheckReasonVO reason : specialtyResult.getReasons()) {
            if (LEVEL_WARN.equals(reason.getLevel())) {
                result.getReasons().add("[提示]" + reason.getMessage());
            } else {
                result.getReasons().add(reason.getMessage());
            }
        }
    }

    private ConfinedSpacePreCheckResultVO buildPreCheckResult(WorkPermitEntity permit, SpecialtyCheckPoint checkPoint) {
        ConfinedSpacePreCheckResultVO result = new ConfinedSpacePreCheckResultVO();
        result.setCheckPoint(checkPoint.name());
        result.setRuleVersion(properties.getRuleVersion());
        result.setPassed(true);
        ConfinedSpaceDetailEntity detail = findDetail(permit.getTenantId(), permit.getId());
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

    private void validateSave(ConfinedSpaceDetailEntity detail, ConfinedSpacePreCheckResultVO result) {
        if (detail == null) {
            addReason(result, "DETAIL_REQUIRED", LEVEL_BLOCK, "受限空间专项详情未填写");
            return;
        }
        if (!StringUtils.hasText(detail.getSpaceName())) {
            addReason(result, "SPACE_NAME_REQUIRED", LEVEL_BLOCK, "受限空间名称必填");
        }
    }

    private void validateSubmit(WorkPermitEntity permit, ConfinedSpaceDetailEntity detail, ConfinedSpacePreCheckResultVO result) {
        validateSave(detail, result);
        if (permit.getGuardianUserId() == null) {
            addReason(result, "GUARDIAN_REQUIRED", LEVEL_BLOCK, "监护人必填");
        }
    }

    private void validateSitePermit(WorkPermitEntity permit, ConfinedSpaceDetailEntity detail, ConfinedSpacePreCheckResultVO result) {
        validateSubmit(permit, detail, result);
        ConfinedSpaceRescuePlanEntity rescuePlan = findRescuePlan(permit.getTenantId(), permit.getId());
        if (rescuePlan == null || rescuePlan.getConfirmedAt() == null) {
            addReason(result, "RESCUE_PLAN_UNCONFIRMED", LEVEL_BLOCK, "救援方案未确认");
        }
        if (entryRecordMapper.selectCount(entryQuery(permit.getTenantId(), permit.getId())).intValue() == 0) {
            addReason(result, "ENTRY_RECORD_MISSING", LEVEL_BLOCK, "出入记录缺失");
        }
    }

    private void validateResume(WorkPermitEntity permit, ConfinedSpaceDetailEntity detail, ConfinedSpacePreCheckResultVO result) {
        if (detail != null && detail.getValidUntil() != null && detail.getValidUntil().before(new Date())) {
            addReason(result, "PERMIT_EXPIRED", LEVEL_BLOCK, "作业票已过期，不允许恢复");
        }
    }

    private void applyDetailRequest(ConfinedSpaceDetailEntity entity, ConfinedSpaceDetailRequest request, WorkPermitEntity permit) {
        entity.setSpaceId(request.getSpaceId());
        entity.setSpaceName(normalizeRequired(request.getSpaceName(), "spaceName"));
        entity.setEntryCount(request.getEntryCount() != null ? request.getEntryCount() : 1);
        entity.setVentilationType(normalizeRequired(request.getVentilationType(), "ventilationType").toUpperCase());
        entity.setContinuousMonitoring(Boolean.TRUE.equals(request.getContinuousMonitoring()) ? 1 : 0);
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

    private ConfinedSpaceDetailEntity findDetail(Long tenantId, Long permitId) {
        return detailMapper.selectOne(new LambdaQueryWrapper<ConfinedSpaceDetailEntity>()
                .eq(ConfinedSpaceDetailEntity::getTenantId, tenantId)
                .eq(ConfinedSpaceDetailEntity::getWorkPermitId, permitId)
                .eq(ConfinedSpaceDetailEntity::getDeleted, 0)
                .last("limit 1"));
    }

    private ConfinedSpaceRescuePlanEntity findRescuePlan(Long tenantId, Long permitId) {
        return rescuePlanMapper.selectOne(new LambdaQueryWrapper<ConfinedSpaceRescuePlanEntity>()
                .eq(ConfinedSpaceRescuePlanEntity::getTenantId, tenantId)
                .eq(ConfinedSpaceRescuePlanEntity::getWorkPermitId, permitId)
                .eq(ConfinedSpaceRescuePlanEntity::getDeleted, 0)
                .last("limit 1"));
    }

    private LambdaQueryWrapper<ConfinedSpaceEntryRecordEntity> entryQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<ConfinedSpaceEntryRecordEntity>()
                .eq(ConfinedSpaceEntryRecordEntity::getTenantId, tenantId)
                .eq(ConfinedSpaceEntryRecordEntity::getWorkPermitId, permitId)
                .eq(ConfinedSpaceEntryRecordEntity::getDeleted, 0)
                .orderByAsc(ConfinedSpaceEntryRecordEntity::getOperatedAt);
    }

    private WorkPermitEntity requirePermit(Long tenantId, Long permitId) {
        WorkPermitEntity entity = workPermitMapper.selectById(permitId);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() != 0
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "work permit not found");
        }
        return entity;
    }

    private void assertConfinedSpace(WorkPermitEntity permit) {
        if (!ConfinedSpaceWorkType.isConfinedSpace(permit.getWorkType())) {
            throw new BusinessException(409, "非受限空间作业票");
        }
        if (!properties.isEnabled()) {
            throw new BusinessException(409, "受限空间专项未启用");
        }
    }

    private void addReason(ConfinedSpacePreCheckResultVO result, String code, String level, String message) {
        ConfinedSpacePreCheckReasonVO reason = new ConfinedSpacePreCheckReasonVO();
        reason.setCode(code);
        reason.setLevel(level);
        reason.setMessage(message);
        result.getReasons().add(reason);
    }

    private boolean allPassed(List<ConfinedSpacePreCheckReasonVO> reasons) {
        for (ConfinedSpacePreCheckReasonVO reason : reasons) {
            if (LEVEL_BLOCK.equals(reason.getLevel())) {
                return false;
            }
        }
        return true;
    }

    private ConfinedSpaceDetailVO toDetailVO(ConfinedSpaceDetailEntity entity) {
        ConfinedSpaceDetailVO vo = new ConfinedSpaceDetailVO();
        vo.setId(entity.getId());
        vo.setWorkPermitId(entity.getWorkPermitId());
        vo.setSpaceId(entity.getSpaceId());
        vo.setSpaceName(entity.getSpaceName());
        vo.setEntryCount(entity.getEntryCount());
        vo.setVentilationType(entity.getVentilationType());
        vo.setContinuousMonitoring(Integer.valueOf(1).equals(entity.getContinuousMonitoring()));
        vo.setRuleVersion(entity.getRuleVersion());
        vo.setValidUntil(entity.getValidUntil());
        return vo;
    }

    private ConfinedSpaceEntryRecordVO toEntryVO(ConfinedSpaceEntryRecordEntity entity) {
        ConfinedSpaceEntryRecordVO vo = new ConfinedSpaceEntryRecordVO();
        vo.setId(entity.getId());
        vo.setWorkerId(entity.getWorkerId());
        vo.setWorkerName(entity.getWorkerName());
        vo.setAction(entity.getAction());
        vo.setOperatedAt(entity.getOperatedAt());
        vo.setLocation(entity.getLocation());
        vo.setOperator(entity.getOperator());
        return vo;
    }

    private ConfinedSpaceRescuePlanVO toRescuePlanVO(ConfinedSpaceRescuePlanEntity entity) {
        ConfinedSpaceRescuePlanVO vo = new ConfinedSpaceRescuePlanVO();
        vo.setId(entity.getId());
        vo.setPlanRef(entity.getPlanRef());
        vo.setContact(entity.getContact());
        vo.setEquipmentJson(entity.getEquipmentJson());
        vo.setConfirmedBy(entity.getConfirmedBy());
        vo.setConfirmedAt(entity.getConfirmedAt());
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
