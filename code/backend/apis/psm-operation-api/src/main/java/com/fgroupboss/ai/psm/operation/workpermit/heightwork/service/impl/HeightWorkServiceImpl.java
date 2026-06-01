package com.fgroupboss.ai.psm.operation.workpermit.heightwork.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.HeightWorkDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.HeightWorkEnvironmentCheckRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.HeightWorkHazardFactorRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.HeightWorkProtectionCheckRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkEnvironmentCheckVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowNodeVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkHazardFactorVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkPreCheckReasonVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkPreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.HeightWorkProtectionCheckVO;
import com.fgroupboss.ai.psm.operation.workpermit.config.WorkPermitStatus;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.config.HeightWorkCheckPoint;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.config.HeightWorkCheckStage;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.config.HeightWorkProperties;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.config.HeightWorkWorkType;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.mapper.HeightWorkDetailMapper;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.mapper.HeightWorkEnvironmentCheckMapper;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.mapper.HeightWorkHazardFactorMapper;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.mapper.HeightWorkProtectionCheckMapper;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.model.entity.HeightWorkDetailEntity;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.model.entity.HeightWorkEnvironmentCheckEntity;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.model.entity.HeightWorkHazardFactorEntity;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.model.entity.HeightWorkProtectionCheckEntity;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.rule.HeightWorkLevelCalculator;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.rule.HeightWorkRiskClassRule;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.rule.HeightWorkStandardHazardFactors;
import com.fgroupboss.ai.psm.operation.workpermit.heightwork.service.HeightWorkService;
import com.fgroupboss.ai.psm.operation.workpermit.mapper.WorkPermitMapper;
import com.fgroupboss.ai.psm.operation.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;
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
public class HeightWorkServiceImpl implements HeightWorkService {

    private static final String CHECK_PASS = "PASS";
    private static final String CHECK_MANUAL_REVIEW = "MANUAL_REVIEW";
    private static final String LEVEL_BLOCK = "BLOCK";
    private static final String LEVEL_WARN = "WARN";

    private static final String[] SITE_PERMIT_REQUIRED_ITEMS = {
            "HWPC_FACILITY", "HWPC_HARNESS", "HWPC_ANCHOR", "HWPC_BELOW_GUARD", "HWPC_TOOL_TETHER"
    };

    private final HeightWorkProperties properties;
    private final WorkPermitMapper workPermitMapper;
    private final HeightWorkDetailMapper detailMapper;
    private final HeightWorkHazardFactorMapper hazardFactorMapper;
    private final HeightWorkProtectionCheckMapper protectionCheckMapper;
    private final HeightWorkEnvironmentCheckMapper environmentCheckMapper;

    @Override
    public boolean supportsHeightWork(WorkPermitEntity permit) {
        return properties.isEnabled() && HeightWorkWorkType.isHeightWork(permit.getWorkType());
    }

    @Override
    public HeightWorkDetailVO getDetail(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        HeightWorkDetailEntity entity = findDetail(tenantId, permitId);
        return entity == null ? null : toDetailVO(entity);
    }

    @Override
    @Transactional
    public HeightWorkDetailVO saveDetail(Long tenantId, Long permitId, HeightWorkDetailRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertHeightWork(permit);
        HeightWorkDetailEntity entity = findDetail(tenantId, permitId);
        Date now = new Date();
        if (entity == null) {
            entity = new HeightWorkDetailEntity();
            entity.setTenantId(tenantId);
            entity.setWorkPermitId(permitId);
            entity.setDeleted(0);
            entity.setCreatedAt(now);
        }
        applyDetailRequest(entity, request, permit);
        recalculateLevels(entity, tenantId, permitId);
        entity.setUpdatedAt(now);
        if (entity.getId() == null) {
            detailMapper.insert(entity);
        } else {
            detailMapper.updateById(entity);
        }
        return toDetailVO(entity);
    }

    @Override
    public List<HeightWorkHazardFactorVO> listHazardFactors(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        List<HeightWorkHazardFactorEntity> entities = hazardFactorMapper.selectList(
                hazardQuery(tenantId, permitId));
        List<HeightWorkHazardFactorVO> result = new ArrayList<HeightWorkHazardFactorVO>();
        for (HeightWorkHazardFactorEntity entity : entities) {
            result.add(toHazardVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public HeightWorkHazardFactorVO addHazardFactor(Long tenantId, Long permitId,
                                                    HeightWorkHazardFactorRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertHeightWork(permit);
        String code = normalizeRequired(request.getFactorCode(), "factorCode");
        String name = HeightWorkStandardHazardFactors.nameOf(code);
        if (!StringUtils.hasText(name)) {
            name = normalizeRequired(request.getFactorName(), "factorName");
        }
        HeightWorkHazardFactorEntity existing = hazardFactorMapper.selectOne(hazardQuery(tenantId, permitId)
                .eq(HeightWorkHazardFactorEntity::getFactorCode, code)
                .last("limit 1"));
        if (existing != null) {
            throw new BusinessException(409, "危险因素已存在: " + code);
        }
        HeightWorkHazardFactorEntity entity = new HeightWorkHazardFactorEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(permitId);
        entity.setFactorCode(code);
        entity.setFactorName(name);
        entity.setHitSource(StringUtils.hasText(request.getHitSource()) ? request.getHitSource().trim() : "MANUAL");
        entity.setControlMeasure(normalizeText(request.getControlMeasure()));
        entity.setAttachmentRef(normalizeText(request.getAttachmentRef()));
        entity.setDeleted(0);
        entity.setCreatedAt(new Date());
        hazardFactorMapper.insert(entity);
        refreshRiskClass(tenantId, permitId);
        return toHazardVO(entity);
    }

    @Override
    @Transactional
    public HeightWorkHazardFactorVO confirmHazardFactor(Long tenantId, Long permitId, Long factorId, String operator) {
        requirePermit(tenantId, permitId);
        HeightWorkHazardFactorEntity entity = hazardFactorMapper.selectById(factorId);
        if (entity == null || !tenantId.equals(entity.getTenantId()) || !permitId.equals(entity.getWorkPermitId())) {
            throw new BusinessException(404, "危险因素不存在");
        }
        entity.setConfirmedBy(normalizeOperator(operator));
        entity.setConfirmedAt(new Date());
        hazardFactorMapper.updateById(entity);
        return toHazardVO(entity);
    }

    @Override
    public List<HeightWorkProtectionCheckVO> listProtectionChecks(Long tenantId, Long permitId, String checkStage) {
        requirePermit(tenantId, permitId);
        LambdaQueryWrapper<HeightWorkProtectionCheckEntity> wrapper = protectionQuery(tenantId, permitId);
        if (StringUtils.hasText(checkStage)) {
            wrapper.eq(HeightWorkProtectionCheckEntity::getCheckStage, checkStage.trim().toUpperCase());
        }
        List<HeightWorkProtectionCheckVO> result = new ArrayList<HeightWorkProtectionCheckVO>();
        for (HeightWorkProtectionCheckEntity entity : protectionCheckMapper.selectList(wrapper)) {
            result.add(toProtectionVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public HeightWorkProtectionCheckVO addProtectionCheck(Long tenantId, Long permitId,
                                                          HeightWorkProtectionCheckRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertHeightWork(permit);
        HeightWorkProtectionCheckEntity entity = new HeightWorkProtectionCheckEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(permitId);
        entity.setCheckStage(normalizeRequired(request.getCheckStage(), "checkStage").toUpperCase());
        entity.setItemCode(normalizeRequired(request.getItemCode(), "itemCode"));
        entity.setItemName(normalizeRequired(request.getItemName(), "itemName"));
        entity.setRequiredFlag(Boolean.FALSE.equals(request.getRequiredFlag()) ? 0 : 1);
        entity.setCheckResult(normalizeRequired(request.getCheckResult(), "checkResult").toUpperCase());
        entity.setAttachmentRef(normalizeText(request.getAttachmentRef()));
        entity.setLocationText(normalizeText(request.getLocationText()));
        entity.setCheckedBy(normalizeOperator(operator));
        entity.setCheckedAt(new Date());
        protectionCheckMapper.insert(entity);
        return toProtectionVO(entity);
    }

    @Override
    public List<HeightWorkEnvironmentCheckVO> listEnvironmentChecks(Long tenantId, Long permitId, String checkStage) {
        requirePermit(tenantId, permitId);
        LambdaQueryWrapper<HeightWorkEnvironmentCheckEntity> wrapper = environmentQuery(tenantId, permitId);
        if (StringUtils.hasText(checkStage)) {
            wrapper.eq(HeightWorkEnvironmentCheckEntity::getCheckStage, checkStage.trim().toUpperCase());
        }
        List<HeightWorkEnvironmentCheckVO> result = new ArrayList<HeightWorkEnvironmentCheckVO>();
        for (HeightWorkEnvironmentCheckEntity entity : environmentCheckMapper.selectList(wrapper)) {
            result.add(toEnvironmentVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public HeightWorkEnvironmentCheckVO addEnvironmentCheck(Long tenantId, Long permitId,
                                                            HeightWorkEnvironmentCheckRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertHeightWork(permit);
        HeightWorkEnvironmentCheckEntity entity = new HeightWorkEnvironmentCheckEntity();
        entity.setTenantId(tenantId);
        entity.setWorkPermitId(permitId);
        entity.setCheckStage(normalizeRequired(request.getCheckStage(), "checkStage").toUpperCase());
        entity.setWindLevel(normalizeText(request.getWindLevel()));
        entity.setWeatherType(normalizeText(request.getWeatherType()));
        entity.setTemperatureC(request.getTemperatureC());
        entity.setVisibilityM(request.getVisibilityM());
        entity.setIlluminationLux(request.getIlluminationLux());
        entity.setGroundCondition(normalizeText(request.getGroundCondition()));
        entity.setPowerProximityFlag(Boolean.TRUE.equals(request.getPowerProximityFlag()) ? 1 : 0);
        entity.setDataSource(StringUtils.hasText(request.getDataSource()) ? request.getDataSource().trim() : "MANUAL");
        entity.setSourceSampledAt(request.getSourceSampledAt());
        entity.setCheckResult(normalizeRequired(request.getCheckResult(), "checkResult").toUpperCase());
        entity.setReviewReason(normalizeText(request.getReviewReason()));
        entity.setCheckedBy(normalizeOperator(operator));
        entity.setCheckedAt(new Date());
        environmentCheckMapper.insert(entity);
        return toEnvironmentVO(entity);
    }

    @Override
    @Transactional
    public HeightWorkDetailVO recalculate(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        HeightWorkDetailEntity entity = requireDetail(tenantId, permitId);
        recalculateLevels(entity, tenantId, permitId);
        entity.setUpdatedAt(new Date());
        detailMapper.updateById(entity);
        return toDetailVO(entity);
    }

    @Override
    public HeightWorkPreCheckResultVO preCheck(Long tenantId, Long permitId, String checkPoint) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertHeightWork(permit);
        return buildPreCheckResult(permit, HeightWorkCheckPoint.from(checkPoint));
    }

    @Override
    public HeightWorkFlowProgressVO getFlowProgress(Long tenantId, Long permitId) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertHeightWork(permit);
        HeightWorkDetailEntity detail = findDetail(tenantId, permitId);
        HeightWorkFlowProgressVO vo = new HeightWorkFlowProgressVO();
        vo.setPermitStatus(permit.getStatus());
        if (detail != null) {
            vo.setHeightLevel(detail.getHeightLevel());
            vo.setRiskClass(detail.getRiskClass());
            vo.setValidUntil(detail.getValidUntil());
        }
        vo.setNodes(buildFlowNodes(permit));
        return vo;
    }

    @Override
    public void applyPreCheck(WorkPermitEntity permit, HeightWorkCheckPoint checkPoint, PreCheckResultVO result) {
        if (!supportsHeightWork(permit)) {
            return;
        }
        HeightWorkPreCheckResultVO heightResult = buildPreCheckResult(permit, checkPoint);
        for (HeightWorkPreCheckReasonVO reason : heightResult.getReasons()) {
            if (LEVEL_WARN.equals(reason.getLevel())) {
                result.getReasons().add("[提示]" + reason.getMessage());
            } else {
                result.getReasons().add(reason.getMessage());
            }
        }
    }

    private HeightWorkPreCheckResultVO buildPreCheckResult(WorkPermitEntity permit, HeightWorkCheckPoint checkPoint) {
        HeightWorkPreCheckResultVO result = new HeightWorkPreCheckResultVO();
        result.setCheckPoint(checkPoint.name());
        result.setRuleVersion(properties.getRuleVersion());
        result.setPassed(true);
        HeightWorkDetailEntity detail = findDetail(permit.getTenantId(), permit.getId());
        if (checkPoint == HeightWorkCheckPoint.SAVE) {
            validateSave(detail, result);
        } else if (checkPoint == HeightWorkCheckPoint.SUBMIT) {
            validateSubmit(permit, detail, result);
        } else if (checkPoint == HeightWorkCheckPoint.SITE_PERMIT) {
            validateSitePermit(permit, detail, result);
        } else if (checkPoint == HeightWorkCheckPoint.RESUME) {
            validateResume(permit, detail, result);
        }
        result.setPassed(allPassed(result.getReasons()));
        return result;
    }

    private void validateSave(HeightWorkDetailEntity detail, HeightWorkPreCheckResultVO result) {
        if (detail == null) {
            addReason(result, "DETAIL_REQUIRED", LEVEL_BLOCK, "高处作业专项详情未填写");
            return;
        }
        if (detail.getWorkHeightM() == null) {
            addReason(result, "HEIGHT_REQUIRED", LEVEL_BLOCK, "作业高度必填");
        }
    }

    private void validateSubmit(WorkPermitEntity permit, HeightWorkDetailEntity detail, HeightWorkPreCheckResultVO result) {
        validateSave(detail, result);
        if (permit.getGuardianUserId() == null) {
            addReason(result, "GUARDIAN_REQUIRED", LEVEL_BLOCK, "监护人必填");
        }
        if (permit.getPermitIssuerUserId() == null) {
            addReason(result, "ISSUER_REQUIRED", LEVEL_BLOCK, "许可人必填");
        }
        if (permit.getPlanStartAt() == null || permit.getPlanEndAt() == null) {
            addReason(result, "PLAN_TIME_REQUIRED", LEVEL_BLOCK, "计划起止时间必填");
        } else if (isPlanSpanExceeded(permit.getPlanStartAt(), permit.getPlanEndAt())) {
            addReason(result, "PLAN_SPAN_EXCEEDED", LEVEL_BLOCK,
                    "计划跨度超过" + properties.getMaxValidDays() + "天");
        }
        if (detail != null && HeightWorkRiskClassRule.CLASS_B.equals(detail.getRiskClass())) {
            validateBClassFactorsConfirmed(permit.getTenantId(), permit.getId(), result);
        }
    }

    private void validateSitePermit(WorkPermitEntity permit, HeightWorkDetailEntity detail, HeightWorkPreCheckResultVO result) {
        validateSubmit(permit, detail, result);
        validateProtectionChecks(permit.getTenantId(), permit.getId(), HeightWorkCheckStage.SITE_PERMIT, result);
        validateEnvironmentChecks(permit.getTenantId(), permit.getId(), HeightWorkCheckStage.SITE_PERMIT, result);
        if (detail != null && HeightWorkLevelCalculator.LEVEL_4.equals(detail.getHeightLevel())) {
            validateLevel4Rescue(detail, result);
        }
    }

    private void validateResume(WorkPermitEntity permit, HeightWorkDetailEntity detail, HeightWorkPreCheckResultVO result) {
        if (detail != null && detail.getValidUntil() != null && detail.getValidUntil().before(new Date())) {
            addReason(result, "PERMIT_EXPIRED", LEVEL_BLOCK, "作业票已过期，不允许恢复");
        }
        validateProtectionChecks(permit.getTenantId(), permit.getId(), HeightWorkCheckStage.RESUME, result);
        validateEnvironmentChecks(permit.getTenantId(), permit.getId(), HeightWorkCheckStage.RESUME, result);
    }

    private void validateProtectionChecks(Long tenantId, Long permitId, String stage, HeightWorkPreCheckResultVO result) {
        List<HeightWorkProtectionCheckEntity> checks = protectionCheckMapper.selectList(
                protectionQuery(tenantId, permitId).eq(HeightWorkProtectionCheckEntity::getCheckStage, stage));
        for (String code : SITE_PERMIT_REQUIRED_ITEMS) {
            if (!hasPassProtection(checks, code)) {
                addReason(result, "PROTECTION_" + code, LEVEL_BLOCK, "防护检查未通过: " + code);
            }
        }
    }

    private void validateEnvironmentChecks(Long tenantId, Long permitId, String stage, HeightWorkPreCheckResultVO result) {
        List<HeightWorkEnvironmentCheckEntity> checks = environmentCheckMapper.selectList(
                environmentQuery(tenantId, permitId).eq(HeightWorkEnvironmentCheckEntity::getCheckStage, stage));
        if (checks.isEmpty()) {
            addReason(result, "ENV_CHECK_MISSING", LEVEL_BLOCK, "环境检查记录缺失");
            return;
        }
        HeightWorkEnvironmentCheckEntity latest = checks.get(checks.size() - 1);
        if (CHECK_PASS.equals(latest.getCheckResult())) {
            result.setWeatherSampledAt(latest.getSourceSampledAt());
            return;
        }
        if (CHECK_MANUAL_REVIEW.equals(latest.getCheckResult()) && StringUtils.hasText(latest.getReviewReason())) {
            result.setWeatherSampledAt(latest.getSourceSampledAt());
            return;
        }
        addReason(result, "ENV_CHECK_FAILED", LEVEL_BLOCK, "环境检查未通过或未填写复核原因");
    }

    private void validateLevel4Rescue(HeightWorkDetailEntity detail, HeightWorkPreCheckResultVO result) {
        if (!Integer.valueOf(1).equals(detail.getCommunicationConfirmed())) {
            addReason(result, "COMM_NOT_CONFIRMED", LEVEL_BLOCK, "四级高处未确认通信联络");
        }
        if (!StringUtils.hasText(detail.getRescuePlanRef())) {
            addReason(result, "RESCUE_PLAN_MISSING", LEVEL_BLOCK, "四级高处未上传应急救援方案");
        }
        if (!StringUtils.hasText(detail.getRescueContact())) {
            addReason(result, "RESCUE_CONTACT_MISSING", LEVEL_BLOCK, "四级高处未填写救援联络人");
        }
    }

    private void validateBClassFactorsConfirmed(Long tenantId, Long permitId, HeightWorkPreCheckResultVO result) {
        List<HeightWorkHazardFactorEntity> factors = hazardFactorMapper.selectList(hazardQuery(tenantId, permitId));
        for (HeightWorkHazardFactorEntity factor : factors) {
            if (factor.getConfirmedAt() == null) {
                addReason(result, "FACTOR_UNCONFIRMED", LEVEL_BLOCK,
                        "B类危险因素未确认: " + factor.getFactorName());
            }
        }
    }

    private void recalculateLevels(HeightWorkDetailEntity entity, Long tenantId, Long permitId) {
        String calculated = HeightWorkLevelCalculator.calculate(entity.getWorkHeightM());
        if (Integer.valueOf(1).equals(entity.getManualUpgradeFlag()) && StringUtils.hasText(entity.getHeightLevel())) {
            if (HeightWorkLevelCalculator.levelOrder(entity.getHeightLevel()) < HeightWorkLevelCalculator.levelOrder(calculated)) {
                throw new BusinessException(400, "人工调整只允许提高控制等级");
            }
        } else {
            entity.setHeightLevel(calculated);
        }
        int factorCount = hazardFactorMapper.selectCount(hazardQuery(tenantId, permitId)).intValue();
        entity.setRiskClass(HeightWorkRiskClassRule.calculate(factorCount));
        entity.setRuleVersion(properties.getRuleVersion());
    }

    private void refreshRiskClass(Long tenantId, Long permitId) {
        HeightWorkDetailEntity detail = findDetail(tenantId, permitId);
        if (detail == null) {
            return;
        }
        int factorCount = hazardFactorMapper.selectCount(hazardQuery(tenantId, permitId)).intValue();
        detail.setRiskClass(HeightWorkRiskClassRule.calculate(factorCount));
        detail.setUpdatedAt(new Date());
        detailMapper.updateById(detail);
    }

    private void applyDetailRequest(HeightWorkDetailEntity entity, HeightWorkDetailRequest request, WorkPermitEntity permit) {
        entity.setWorkHeightM(request.getWorkHeightM());
        entity.setFallDatumDescription(normalizeRequired(request.getFallDatumDescription(), "fallDatumDescription"));
        entity.setWorkLocation(normalizeRequired(request.getWorkLocation(), "workLocation"));
        entity.setWorkMethod(normalizeRequired(request.getWorkMethod(), "workMethod").toUpperCase());
        entity.setManualUpgradeFlag(Boolean.TRUE.equals(request.getManualUpgradeFlag()) ? 1 : 0);
        entity.setManualUpgradeReason(normalizeText(request.getManualUpgradeReason()));
        entity.setRescuePlanRef(normalizeText(request.getRescuePlanRef()));
        entity.setRescueContact(normalizeText(request.getRescueContact()));
        entity.setCommunicationConfirmed(Boolean.TRUE.equals(request.getCommunicationConfirmed()) ? 1 : 0);
        entity.setRuleVersion(properties.getRuleVersion());
        entity.setValidUntil(calculateValidUntil(permit));
        if (Boolean.TRUE.equals(request.getManualUpgradeFlag()) && request.getWorkHeightM() != null) {
            String calculated = HeightWorkLevelCalculator.calculate(request.getWorkHeightM());
            if (!StringUtils.hasText(entity.getManualUpgradeReason())) {
                throw new BusinessException(400, "人工提高等级必须填写原因");
            }
            entity.setHeightLevel(calculated);
        }
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

    private boolean isPlanSpanExceeded(Date start, Date end) {
        long diffMs = end.getTime() - start.getTime();
        long maxMs = properties.getMaxValidDays() * 24L * 60L * 60L * 1000L;
        return diffMs > maxMs;
    }

    private boolean hasPassProtection(List<HeightWorkProtectionCheckEntity> checks, String itemCode) {
        for (HeightWorkProtectionCheckEntity check : checks) {
            if (itemCode.equals(check.getItemCode()) && CHECK_PASS.equals(check.getCheckResult())) {
                return true;
            }
        }
        return false;
    }

    private List<HeightWorkFlowNodeVO> buildFlowNodes(WorkPermitEntity permit) {
        String status = permit.getStatus();
        List<HeightWorkFlowNodeVO> nodes = new ArrayList<HeightWorkFlowNodeVO>();
        nodes.add(flowNode("APPLY", "申请", status, WorkPermitStatus.DRAFT.name()));
        nodes.add(flowNode("CLASSIFY", "分级分类", status, WorkPermitStatus.DRAFT.name()));
        nodes.add(flowNode("JSA", "安全分析及交底", status, WorkPermitStatus.DRAFT.name()));
        nodes.add(flowNode("DISCLOSURE", "安全交底签字", status, WorkPermitStatus.DRAFT.name()));
        nodes.add(flowNode("APPROVING", "审批中", status, WorkPermitStatus.APPROVING.name()));
        nodes.add(flowNode("PENDING", "待作业", status, WorkPermitStatus.PENDING_SITE_PERMIT.name()));
        nodes.add(flowNode("IN_PROGRESS", "作业中", status, WorkPermitStatus.IN_PROGRESS.name()));
        nodes.add(flowNode("SUSPENDED", "暂停中", status, WorkPermitStatus.SUSPENDED.name()));
        nodes.add(flowNode("ACCEPTANCE", "待验收", status, WorkPermitStatus.PENDING_ACCEPTANCE.name()));
        nodes.add(flowNode("CLOSURE", "待关闭", status, WorkPermitStatus.PENDING_ACCEPTANCE.name()));
        nodes.add(flowNode("CLOSED", "作业关闭", status, WorkPermitStatus.CLOSED.name()));
        return nodes;
    }

    private HeightWorkFlowNodeVO flowNode(String code, String name, String currentStatus, String matchStatus) {
        HeightWorkFlowNodeVO node = new HeightWorkFlowNodeVO();
        node.setNodeCode(code);
        node.setNodeName(name);
        node.setCurrent(matchStatus.equals(currentStatus));
        node.setStatus(node.isCurrent() ? "CURRENT" : "PENDING");
        return node;
    }

    private HeightWorkDetailEntity findDetail(Long tenantId, Long permitId) {
        return detailMapper.selectOne(new LambdaQueryWrapper<HeightWorkDetailEntity>()
                .eq(HeightWorkDetailEntity::getTenantId, tenantId)
                .eq(HeightWorkDetailEntity::getWorkPermitId, permitId)
                .eq(HeightWorkDetailEntity::getDeleted, 0)
                .last("limit 1"));
    }

    private HeightWorkDetailEntity requireDetail(Long tenantId, Long permitId) {
        HeightWorkDetailEntity entity = findDetail(tenantId, permitId);
        if (entity == null) {
            throw new BusinessException(404, "高处作业详情不存在");
        }
        return entity;
    }

    private WorkPermitEntity requirePermit(Long tenantId, Long permitId) {
        WorkPermitEntity entity = workPermitMapper.selectById(permitId);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() != 0
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "work permit not found");
        }
        return entity;
    }

    private void assertHeightWork(WorkPermitEntity permit) {
        if (!HeightWorkWorkType.isHeightWork(permit.getWorkType())) {
            throw new BusinessException(409, "非高处作业票");
        }
        if (!properties.isEnabled()) {
            throw new BusinessException(409, "高处作业专项未启用");
        }
    }

    private LambdaQueryWrapper<HeightWorkHazardFactorEntity> hazardQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<HeightWorkHazardFactorEntity>()
                .eq(HeightWorkHazardFactorEntity::getTenantId, tenantId)
                .eq(HeightWorkHazardFactorEntity::getWorkPermitId, permitId)
                .eq(HeightWorkHazardFactorEntity::getDeleted, 0);
    }

    private LambdaQueryWrapper<HeightWorkProtectionCheckEntity> protectionQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<HeightWorkProtectionCheckEntity>()
                .eq(HeightWorkProtectionCheckEntity::getTenantId, tenantId)
                .eq(HeightWorkProtectionCheckEntity::getWorkPermitId, permitId)
                .orderByAsc(HeightWorkProtectionCheckEntity::getCheckedAt);
    }

    private LambdaQueryWrapper<HeightWorkEnvironmentCheckEntity> environmentQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<HeightWorkEnvironmentCheckEntity>()
                .eq(HeightWorkEnvironmentCheckEntity::getTenantId, tenantId)
                .eq(HeightWorkEnvironmentCheckEntity::getWorkPermitId, permitId)
                .orderByAsc(HeightWorkEnvironmentCheckEntity::getCheckedAt);
    }

    private void addReason(HeightWorkPreCheckResultVO result, String code, String level, String message) {
        HeightWorkPreCheckReasonVO reason = new HeightWorkPreCheckReasonVO();
        reason.setCode(code);
        reason.setLevel(level);
        reason.setMessage(message);
        result.getReasons().add(reason);
    }

    private boolean allPassed(List<HeightWorkPreCheckReasonVO> reasons) {
        for (HeightWorkPreCheckReasonVO reason : reasons) {
            if (LEVEL_BLOCK.equals(reason.getLevel())) {
                return false;
            }
        }
        return true;
    }

    private HeightWorkDetailVO toDetailVO(HeightWorkDetailEntity entity) {
        HeightWorkDetailVO vo = new HeightWorkDetailVO();
        vo.setId(entity.getId());
        vo.setWorkPermitId(entity.getWorkPermitId());
        vo.setWorkHeightM(entity.getWorkHeightM());
        vo.setFallDatumDescription(entity.getFallDatumDescription());
        vo.setWorkLocation(entity.getWorkLocation());
        vo.setWorkMethod(entity.getWorkMethod());
        vo.setHeightLevel(entity.getHeightLevel());
        vo.setRiskClass(entity.getRiskClass());
        vo.setManualUpgradeFlag(Integer.valueOf(1).equals(entity.getManualUpgradeFlag()));
        vo.setManualUpgradeReason(entity.getManualUpgradeReason());
        vo.setRescuePlanRef(entity.getRescuePlanRef());
        vo.setRescueContact(entity.getRescueContact());
        vo.setCommunicationConfirmed(Integer.valueOf(1).equals(entity.getCommunicationConfirmed()));
        vo.setRuleVersion(entity.getRuleVersion());
        vo.setValidUntil(entity.getValidUntil());
        return vo;
    }

    private HeightWorkHazardFactorVO toHazardVO(HeightWorkHazardFactorEntity entity) {
        HeightWorkHazardFactorVO vo = new HeightWorkHazardFactorVO();
        vo.setId(entity.getId());
        vo.setFactorCode(entity.getFactorCode());
        vo.setFactorName(entity.getFactorName());
        vo.setHitSource(entity.getHitSource());
        vo.setControlMeasure(entity.getControlMeasure());
        vo.setAttachmentRef(entity.getAttachmentRef());
        vo.setConfirmedBy(entity.getConfirmedBy());
        vo.setConfirmedAt(entity.getConfirmedAt());
        return vo;
    }

    private HeightWorkProtectionCheckVO toProtectionVO(HeightWorkProtectionCheckEntity entity) {
        HeightWorkProtectionCheckVO vo = new HeightWorkProtectionCheckVO();
        vo.setId(entity.getId());
        vo.setCheckStage(entity.getCheckStage());
        vo.setItemCode(entity.getItemCode());
        vo.setItemName(entity.getItemName());
        vo.setRequiredFlag(Integer.valueOf(1).equals(entity.getRequiredFlag()));
        vo.setCheckResult(entity.getCheckResult());
        vo.setAttachmentRef(entity.getAttachmentRef());
        vo.setLocationText(entity.getLocationText());
        vo.setCheckedBy(entity.getCheckedBy());
        vo.setCheckedAt(entity.getCheckedAt());
        return vo;
    }

    private HeightWorkEnvironmentCheckVO toEnvironmentVO(HeightWorkEnvironmentCheckEntity entity) {
        HeightWorkEnvironmentCheckVO vo = new HeightWorkEnvironmentCheckVO();
        vo.setId(entity.getId());
        vo.setCheckStage(entity.getCheckStage());
        vo.setWindLevel(entity.getWindLevel());
        vo.setWeatherType(entity.getWeatherType());
        vo.setTemperatureC(entity.getTemperatureC());
        vo.setVisibilityM(entity.getVisibilityM());
        vo.setIlluminationLux(entity.getIlluminationLux());
        vo.setGroundCondition(entity.getGroundCondition());
        vo.setPowerProximityFlag(Integer.valueOf(1).equals(entity.getPowerProximityFlag()));
        vo.setDataSource(entity.getDataSource());
        vo.setSourceSampledAt(entity.getSourceSampledAt());
        vo.setCheckResult(entity.getCheckResult());
        vo.setReviewReason(entity.getReviewReason());
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
