package com.fgroupboss.ai.psm.operation.workpermit.blindplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.BlindPlateActionConfirmRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.BlindPlateRegistryRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.BlindPlateWorkDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlateActionRecordVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlateFlowNodeVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlateFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlatePreCheckReasonVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlatePreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlateRegistryVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlateWorkDetailVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.blindplate.config.BlindPlateCheckPoint;
import com.fgroupboss.ai.psm.operation.workpermit.blindplate.config.BlindPlateProperties;
import com.fgroupboss.ai.psm.operation.workpermit.blindplate.config.BlindPlateWorkType;
import com.fgroupboss.ai.psm.operation.workpermit.blindplate.mapper.BlindPlateActionRecordMapper;
import com.fgroupboss.ai.psm.operation.workpermit.blindplate.mapper.BlindPlateRegistryMapper;
import com.fgroupboss.ai.psm.operation.workpermit.blindplate.mapper.BlindPlateWorkDetailMapper;
import com.fgroupboss.ai.psm.operation.workpermit.blindplate.model.entity.BlindPlateActionRecordEntity;
import com.fgroupboss.ai.psm.operation.workpermit.blindplate.model.entity.BlindPlateRegistryEntity;
import com.fgroupboss.ai.psm.operation.workpermit.blindplate.model.entity.BlindPlateWorkDetailEntity;
import com.fgroupboss.ai.psm.operation.workpermit.blindplate.service.BlindPlateService;
import com.fgroupboss.ai.psm.operation.workpermit.config.WorkPermitStatus;
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
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlindPlateServiceImpl implements BlindPlateService, WorkPermitSpecialtyHandler {

    private static final String LEVEL_BLOCK = "BLOCK";
    private static final String STATUS_AVAILABLE = "AVAILABLE";
    private static final String STATUS_IN_USE = "IN_USE";
    private static final String STATUS_REMOVED = "REMOVED";
    private static final String OP_INSTALL = "INSTALL";
    private static final String OP_REMOVE = "REMOVE";

    private final BlindPlateProperties properties;
    private final WorkPermitMapper workPermitMapper;
    private final BlindPlateRegistryMapper registryMapper;
    private final BlindPlateWorkDetailMapper detailMapper;
    private final BlindPlateActionRecordMapper actionRecordMapper;

    @Override
    public boolean supports(WorkPermitEntity permit) {
        return properties.isEnabled() && BlindPlateWorkType.isBlindPlate(permit.getWorkType());
    }

    @Override
    public void applyPreCheck(WorkPermitEntity permit, SpecialtyCheckPoint checkPoint, PreCheckResultVO result) {
        if (!supports(permit)) {
            return;
        }
        BlindPlateCheckPoint point = mapCheckPoint(checkPoint);
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
        detail.setBlindPlateDetail(getDetail(tenantId, permit.getId()));
        detail.setBlindPlateActionRecords(listActionRecords(tenantId, permit.getId()));
    }

    @Override
    public boolean skipGasTestAtSitePermit(WorkPermitEntity permit) {
        return supports(permit);
    }

    @Override
    public List<BlindPlateRegistryVO> listRegistry(Long tenantId, String status) {
        LambdaQueryWrapper<BlindPlateRegistryEntity> wrapper = registryQuery(tenantId);
        if (StringUtils.hasText(status)) {
            wrapper.eq(BlindPlateRegistryEntity::getStatus, status.trim().toUpperCase());
        }
        List<BlindPlateRegistryVO> result = new ArrayList<BlindPlateRegistryVO>();
        for (BlindPlateRegistryEntity entity : registryMapper.selectList(wrapper)) {
            result.add(toRegistryVO(entity));
        }
        return result;
    }

    @Override
    public BlindPlateRegistryVO getRegistry(Long tenantId, Long registryId) {
        BlindPlateRegistryEntity entity = requireRegistry(tenantId, registryId);
        return toRegistryVO(entity);
    }

    @Override
    @Transactional
    public BlindPlateRegistryVO createRegistry(Long tenantId, BlindPlateRegistryRequest request, String operator) {
        assertEnabled();
        String plateNo = normalizeRequired(request.getBlindPlateNo(), "blindPlateNo");
        if (registryMapper.selectCount(registryQuery(tenantId)
                .eq(BlindPlateRegistryEntity::getBlindPlateNo, plateNo)) > 0) {
            throw new BusinessException(409, "盲板编号已存在: " + plateNo);
        }
        Date now = new Date();
        BlindPlateRegistryEntity entity = new BlindPlateRegistryEntity();
        entity.setTenantId(tenantId);
        entity.setBlindPlateNo(plateNo);
        entity.setPipelineId(normalizeRequired(request.getPipelineId(), "pipelineId"));
        entity.setPosition(normalizeRequired(request.getPosition(), "position"));
        entity.setDiagramRef(normalizeText(request.getDiagramRef()));
        entity.setSpec(normalizeText(request.getSpec()));
        entity.setMaterial(normalizeText(request.getMaterial()));
        entity.setTagNo(normalizeText(request.getTagNo()));
        entity.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus().trim().toUpperCase() : STATUS_AVAILABLE);
        entity.setVersionNo(0);
        entity.setDeleted(0);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        registryMapper.insert(entity);
        return toRegistryVO(entity);
    }

    @Override
    @Transactional
    public BlindPlateRegistryVO updateRegistry(Long tenantId, Long registryId,
                                               BlindPlateRegistryRequest request, String operator) {
        assertEnabled();
        BlindPlateRegistryEntity entity = requireRegistry(tenantId, registryId);
        Integer expectedVersion = entity.getVersionNo();
        String expectedStatus = entity.getStatus();
        BlindPlateRegistryEntity update = new BlindPlateRegistryEntity();
        update.setPipelineId(normalizeRequired(request.getPipelineId(), "pipelineId"));
        update.setPosition(normalizeRequired(request.getPosition(), "position"));
        update.setDiagramRef(normalizeText(request.getDiagramRef()));
        update.setSpec(normalizeText(request.getSpec()));
        update.setMaterial(normalizeText(request.getMaterial()));
        update.setTagNo(normalizeText(request.getTagNo()));
        if (StringUtils.hasText(request.getStatus())) {
            update.setStatus(request.getStatus().trim().toUpperCase());
        }
        update.setVersionNo(expectedVersion + 1);
        update.setUpdatedAt(new Date());
        int rows = registryMapper.update(update, new LambdaUpdateWrapper<BlindPlateRegistryEntity>()
                .eq(BlindPlateRegistryEntity::getId, registryId)
                .eq(BlindPlateRegistryEntity::getTenantId, tenantId)
                .eq(BlindPlateRegistryEntity::getVersionNo, expectedVersion)
                .eq(BlindPlateRegistryEntity::getStatus, expectedStatus)
                .eq(BlindPlateRegistryEntity::getDeleted, 0));
        if (rows == 0) {
            throw new BusinessException(409, "盲板台账状态冲突，请刷新后重试");
        }
        return toRegistryVO(requireRegistry(tenantId, registryId));
    }

    @Override
    public BlindPlateWorkDetailVO getDetail(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        BlindPlateWorkDetailEntity entity = findDetail(tenantId, permitId);
        return entity == null ? null : toDetailVO(entity);
    }

    @Override
    @Transactional
    public BlindPlateWorkDetailVO saveDetail(Long tenantId, Long permitId,
                                             BlindPlateWorkDetailRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertBlindPlate(permit);
        Long blindPlateId = request.getBlindPlateId();
        if (blindPlateId == null) {
            throw new BusinessException(400, "blindPlateId is required");
        }
        requireRegistry(tenantId, blindPlateId);
        BlindPlateWorkDetailEntity existing = findDetail(tenantId, permitId);
        if (existing != null && !blindPlateId.equals(existing.getBlindPlateId())) {
            throw new BusinessException(409, "一票一块盲板，不允许更换盲板");
        }
        assertOneTicketOnePlate(tenantId, permitId, blindPlateId);
        Date now = new Date();
        BlindPlateWorkDetailEntity entity = existing != null ? existing : new BlindPlateWorkDetailEntity();
        if (entity.getId() == null) {
            entity.setTenantId(tenantId);
            entity.setWorkPermitId(permitId);
            entity.setDeleted(0);
            entity.setActionConfirmed(0);
            entity.setCreatedAt(now);
        }
        entity.setBlindPlateId(blindPlateId);
        entity.setOperationType(normalizeRequired(request.getOperationType(), "operationType").toUpperCase());
        entity.setPipelineId(normalizeRequired(request.getPipelineId(), "pipelineId"));
        entity.setPositionDescription(normalizeRequired(request.getPositionDescription(), "positionDescription"));
        entity.setPositionDiagramRef(normalizeRequired(request.getPositionDiagramRef(), "positionDiagramRef"));
        entity.setMediumName(normalizeRequired(request.getMediumName(), "mediumName"));
        entity.setTemperature(normalizeText(request.getTemperature()));
        entity.setPressure(normalizeText(request.getPressure()));
        entity.setHazardJson(normalizeText(request.getHazardJson()));
        entity.setTagNo(normalizeRequired(request.getTagNo(), "tagNo"));
        entity.setSpec(normalizeText(request.getSpec()));
        entity.setMaterial(normalizeText(request.getMaterial()));
        entity.setUpdatedAt(now);
        if (entity.getId() == null) {
            detailMapper.insert(entity);
        } else {
            detailMapper.updateById(entity);
        }
        return toDetailVO(entity);
    }

    @Override
    public List<BlindPlateActionRecordVO> listActionRecords(Long tenantId, Long permitId) {
        requirePermit(tenantId, permitId);
        List<BlindPlateActionRecordVO> result = new ArrayList<BlindPlateActionRecordVO>();
        for (BlindPlateActionRecordEntity entity : actionRecordMapper.selectList(actionQuery(tenantId, permitId))) {
            result.add(toActionVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public BlindPlateActionRecordVO confirmAction(Long tenantId, Long permitId,
                                                  BlindPlateActionConfirmRequest request, String operator) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertBlindPlate(permit);
        BlindPlateWorkDetailEntity detail = requireDetail(tenantId, permitId);
        BlindPlateRegistryEntity registry = requireRegistry(tenantId, detail.getBlindPlateId());
        String toStatus = normalizeRequired(request.getToStatus(), "toStatus").toUpperCase();
        String fromStatus = registry.getStatus();
        validateActionTransition(detail.getOperationType(), fromStatus, toStatus);
        int rows = registryMapper.update(null, new LambdaUpdateWrapper<BlindPlateRegistryEntity>()
                .set(BlindPlateRegistryEntity::getStatus, toStatus)
                .set(BlindPlateRegistryEntity::getVersionNo, registry.getVersionNo() + 1)
                .set(BlindPlateRegistryEntity::getUpdatedAt, new Date())
                .eq(BlindPlateRegistryEntity::getId, registry.getId())
                .eq(BlindPlateRegistryEntity::getVersionNo, registry.getVersionNo())
                .eq(BlindPlateRegistryEntity::getStatus, fromStatus)
                .eq(BlindPlateRegistryEntity::getDeleted, 0));
        if (rows == 0) {
            throw new BusinessException(409, "盲板台账状态冲突，请刷新后重试");
        }
        BlindPlateActionRecordEntity record = new BlindPlateActionRecordEntity();
        record.setTenantId(tenantId);
        record.setWorkPermitId(permitId);
        record.setBlindPlateId(detail.getBlindPlateId());
        record.setFromStatus(fromStatus);
        record.setToStatus(toStatus);
        record.setAttachmentRef(normalizeText(request.getAttachmentRef()));
        record.setOperatedBy(normalizeOperator(operator));
        record.setOperatedAt(new Date());
        actionRecordMapper.insert(record);
        detail.setActionConfirmed(1);
        detail.setUpdatedAt(new Date());
        detailMapper.updateById(detail);
        return toActionVO(record);
    }

    @Override
    public BlindPlatePreCheckResultVO preCheck(Long tenantId, Long permitId, String checkPoint) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertBlindPlate(permit);
        return buildPreCheckResult(permit, BlindPlateCheckPoint.from(checkPoint));
    }

    @Override
    public BlindPlateFlowProgressVO getFlowProgress(Long tenantId, Long permitId) {
        WorkPermitEntity permit = requirePermit(tenantId, permitId);
        assertBlindPlate(permit);
        BlindPlateWorkDetailEntity detail = findDetail(tenantId, permitId);
        BlindPlateFlowProgressVO vo = new BlindPlateFlowProgressVO();
        vo.setPermitStatus(permit.getStatus());
        if (detail != null) {
            vo.setOperationType(detail.getOperationType());
            vo.setActionConfirmed(Integer.valueOf(1).equals(detail.getActionConfirmed()));
        }
        vo.setNodes(buildFlowNodes(permit));
        return vo;
    }

    @Override
    public void applyPreCheck(WorkPermitEntity permit, BlindPlateCheckPoint checkPoint, PreCheckResultVO result) {
        if (!supports(permit)) {
            return;
        }
        mergeReasons(buildPreCheckResult(permit, checkPoint), result);
    }

    private BlindPlatePreCheckResultVO buildPreCheckResult(WorkPermitEntity permit, BlindPlateCheckPoint checkPoint) {
        BlindPlatePreCheckResultVO result = new BlindPlatePreCheckResultVO();
        result.setCheckPoint(checkPoint.name());
        result.setRuleVersion(properties.getRuleVersion());
        result.setPassed(true);
        BlindPlateWorkDetailEntity detail = findDetail(permit.getTenantId(), permit.getId());
        if (checkPoint == BlindPlateCheckPoint.SAVE) {
            validateSave(detail, result);
        } else if (checkPoint == BlindPlateCheckPoint.SUBMIT) {
            validateSubmit(detail, result);
        } else if (checkPoint == BlindPlateCheckPoint.SITE_PERMIT) {
            validateSitePermit(detail, result);
        } else if (checkPoint == BlindPlateCheckPoint.ACCEPTANCE) {
            validateAcceptance(detail, result);
        }
        result.setPassed(allPassed(result.getReasons()));
        return result;
    }

    private void validateSave(BlindPlateWorkDetailEntity detail, BlindPlatePreCheckResultVO result) {
        if (detail == null) {
            addReason(result, "DETAIL_REQUIRED", LEVEL_BLOCK, "盲板抽堵专项详情未填写");
        }
    }

    private void validateSubmit(BlindPlateWorkDetailEntity detail, BlindPlatePreCheckResultVO result) {
        validateSave(detail, result);
        if (detail == null) {
            return;
        }
        if (detail.getBlindPlateId() == null) {
            addReason(result, "PLATE_REQUIRED", LEVEL_BLOCK, "必须选择一块盲板（一票一块）");
        }
        if (!StringUtils.hasText(detail.getPositionDiagramRef())) {
            addReason(result, "DIAGRAM_REQUIRED", LEVEL_BLOCK, "位置图附件必填");
        }
        if (!StringUtils.hasText(detail.getMediumName())) {
            addReason(result, "MEDIUM_REQUIRED", LEVEL_BLOCK, "介质名称必填");
        }
        if (!StringUtils.hasText(detail.getTagNo())) {
            addReason(result, "TAG_REQUIRED", LEVEL_BLOCK, "挂牌编号必填");
        }
        if (!OP_INSTALL.equals(detail.getOperationType()) && !OP_REMOVE.equals(detail.getOperationType())) {
            addReason(result, "OP_TYPE_INVALID", LEVEL_BLOCK, "动作类型必须为 INSTALL 或 REMOVE");
        }
    }

    private void validateSitePermit(BlindPlateWorkDetailEntity detail, BlindPlatePreCheckResultVO result) {
        validateSubmit(detail, result);
        if (detail != null && !Integer.valueOf(1).equals(detail.getActionConfirmed())) {
            addReason(result, "ACTION_NOT_CONFIRMED", LEVEL_BLOCK, "现场许可前须完成抽堵动作确认");
        }
    }

    private void validateAcceptance(BlindPlateWorkDetailEntity detail, BlindPlatePreCheckResultVO result) {
        validateSitePermit(detail, result);
    }

    private void assertOneTicketOnePlate(Long tenantId, Long permitId, Long blindPlateId) {
        BlindPlateWorkDetailEntity conflict = detailMapper.selectOne(new LambdaQueryWrapper<BlindPlateWorkDetailEntity>()
                .eq(BlindPlateWorkDetailEntity::getTenantId, tenantId)
                .eq(BlindPlateWorkDetailEntity::getBlindPlateId, blindPlateId)
                .ne(BlindPlateWorkDetailEntity::getWorkPermitId, permitId)
                .eq(BlindPlateWorkDetailEntity::getDeleted, 0)
                .last("limit 1"));
        if (conflict != null) {
            throw new BusinessException(409, "该盲板已被其他作业票占用");
        }
    }

    private void validateActionTransition(String operationType, String fromStatus, String toStatus) {
        if (OP_INSTALL.equals(operationType) && STATUS_AVAILABLE.equals(fromStatus) && STATUS_IN_USE.equals(toStatus)) {
            return;
        }
        if (OP_REMOVE.equals(operationType) && STATUS_IN_USE.equals(fromStatus) && STATUS_REMOVED.equals(toStatus)) {
            return;
        }
        throw new BusinessException(400, "非法盲板状态变更: " + fromStatus + " -> " + toStatus);
    }

    private BlindPlateCheckPoint mapCheckPoint(SpecialtyCheckPoint checkPoint) {
        if (checkPoint == SpecialtyCheckPoint.SUBMIT) {
            return BlindPlateCheckPoint.SUBMIT;
        }
        if (checkPoint == SpecialtyCheckPoint.SITE_PERMIT) {
            return BlindPlateCheckPoint.SITE_PERMIT;
        }
        return null;
    }

    private void mergeReasons(BlindPlatePreCheckResultVO specialtyResult, PreCheckResultVO result) {
        for (BlindPlatePreCheckReasonVO reason : specialtyResult.getReasons()) {
            result.getReasons().add(reason.getMessage());
        }
    }

    private List<BlindPlateFlowNodeVO> buildFlowNodes(WorkPermitEntity permit) {
        String status = permit.getStatus();
        List<BlindPlateFlowNodeVO> nodes = new ArrayList<BlindPlateFlowNodeVO>();
        nodes.add(flowNode("APPLY", "申请", status, WorkPermitStatus.DRAFT.name()));
        nodes.add(flowNode("IDENTIFY", "盲板识别及介质分析", status, WorkPermitStatus.DRAFT.name()));
        nodes.add(flowNode("APPROVING", "审批中", status, WorkPermitStatus.APPROVING.name()));
        nodes.add(flowNode("PENDING", "待作业", status, WorkPermitStatus.PENDING_SITE_PERMIT.name()));
        nodes.add(flowNode("ACTION", "抽堵作业", status, WorkPermitStatus.IN_PROGRESS.name()));
        nodes.add(flowNode("ACCEPTANCE", "待验收", status, WorkPermitStatus.PENDING_ACCEPTANCE.name()));
        nodes.add(flowNode("CLOSED", "关闭", status, WorkPermitStatus.CLOSED.name()));
        return nodes;
    }

    private BlindPlateFlowNodeVO flowNode(String code, String name, String currentStatus, String matchStatus) {
        BlindPlateFlowNodeVO node = new BlindPlateFlowNodeVO();
        node.setNodeCode(code);
        node.setNodeName(name);
        node.setCurrent(matchStatus.equals(currentStatus));
        node.setStatus(node.isCurrent() ? "CURRENT" : "PENDING");
        return node;
    }

    private BlindPlateWorkDetailEntity findDetail(Long tenantId, Long permitId) {
        return detailMapper.selectOne(new LambdaQueryWrapper<BlindPlateWorkDetailEntity>()
                .eq(BlindPlateWorkDetailEntity::getTenantId, tenantId)
                .eq(BlindPlateWorkDetailEntity::getWorkPermitId, permitId)
                .eq(BlindPlateWorkDetailEntity::getDeleted, 0)
                .last("limit 1"));
    }

    private BlindPlateWorkDetailEntity requireDetail(Long tenantId, Long permitId) {
        BlindPlateWorkDetailEntity entity = findDetail(tenantId, permitId);
        if (entity == null) {
            throw new BusinessException(404, "盲板抽堵详情不存在");
        }
        return entity;
    }

    private BlindPlateRegistryEntity requireRegistry(Long tenantId, Long registryId) {
        BlindPlateRegistryEntity entity = registryMapper.selectById(registryId);
        if (entity == null || !tenantId.equals(entity.getTenantId())
                || entity.getDeleted() != null && entity.getDeleted() != 0) {
            throw new BusinessException(404, "盲板台账不存在");
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

    private void assertBlindPlate(WorkPermitEntity permit) {
        if (!BlindPlateWorkType.isBlindPlate(permit.getWorkType())) {
            throw new BusinessException(409, "非盲板抽堵作业票");
        }
        assertEnabled();
    }

    private void assertEnabled() {
        if (!properties.isEnabled()) {
            throw new BusinessException(409, "盲板抽堵专项未启用");
        }
    }

    private LambdaQueryWrapper<BlindPlateRegistryEntity> registryQuery(Long tenantId) {
        return new LambdaQueryWrapper<BlindPlateRegistryEntity>()
                .eq(BlindPlateRegistryEntity::getTenantId, tenantId)
                .eq(BlindPlateRegistryEntity::getDeleted, 0)
                .orderByDesc(BlindPlateRegistryEntity::getUpdatedAt);
    }

    private LambdaQueryWrapper<BlindPlateActionRecordEntity> actionQuery(Long tenantId, Long permitId) {
        return new LambdaQueryWrapper<BlindPlateActionRecordEntity>()
                .eq(BlindPlateActionRecordEntity::getTenantId, tenantId)
                .eq(BlindPlateActionRecordEntity::getWorkPermitId, permitId)
                .orderByAsc(BlindPlateActionRecordEntity::getOperatedAt);
    }

    private void addReason(BlindPlatePreCheckResultVO result, String code, String level, String message) {
        BlindPlatePreCheckReasonVO reason = new BlindPlatePreCheckReasonVO();
        reason.setCode(code);
        reason.setLevel(level);
        reason.setMessage(message);
        result.getReasons().add(reason);
    }

    private boolean allPassed(List<BlindPlatePreCheckReasonVO> reasons) {
        for (BlindPlatePreCheckReasonVO reason : reasons) {
            if (LEVEL_BLOCK.equals(reason.getLevel())) {
                return false;
            }
        }
        return true;
    }

    private BlindPlateRegistryVO toRegistryVO(BlindPlateRegistryEntity entity) {
        BlindPlateRegistryVO vo = new BlindPlateRegistryVO();
        vo.setId(entity.getId());
        vo.setBlindPlateNo(entity.getBlindPlateNo());
        vo.setPipelineId(entity.getPipelineId());
        vo.setPosition(entity.getPosition());
        vo.setDiagramRef(entity.getDiagramRef());
        vo.setSpec(entity.getSpec());
        vo.setMaterial(entity.getMaterial());
        vo.setTagNo(entity.getTagNo());
        vo.setStatus(entity.getStatus());
        vo.setVersionNo(entity.getVersionNo());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private BlindPlateWorkDetailVO toDetailVO(BlindPlateWorkDetailEntity entity) {
        BlindPlateWorkDetailVO vo = new BlindPlateWorkDetailVO();
        vo.setId(entity.getId());
        vo.setWorkPermitId(entity.getWorkPermitId());
        vo.setBlindPlateId(entity.getBlindPlateId());
        vo.setOperationType(entity.getOperationType());
        vo.setPipelineId(entity.getPipelineId());
        vo.setPositionDescription(entity.getPositionDescription());
        vo.setPositionDiagramRef(entity.getPositionDiagramRef());
        vo.setMediumName(entity.getMediumName());
        vo.setTemperature(entity.getTemperature());
        vo.setPressure(entity.getPressure());
        vo.setHazardJson(entity.getHazardJson());
        vo.setTagNo(entity.getTagNo());
        vo.setSpec(entity.getSpec());
        vo.setMaterial(entity.getMaterial());
        vo.setActionConfirmed(Integer.valueOf(1).equals(entity.getActionConfirmed()));
        vo.setRuleVersion(properties.getRuleVersion());
        return vo;
    }

    private BlindPlateActionRecordVO toActionVO(BlindPlateActionRecordEntity entity) {
        BlindPlateActionRecordVO vo = new BlindPlateActionRecordVO();
        vo.setId(entity.getId());
        vo.setWorkPermitId(entity.getWorkPermitId());
        vo.setBlindPlateId(entity.getBlindPlateId());
        vo.setFromStatus(entity.getFromStatus());
        vo.setToStatus(entity.getToStatus());
        vo.setAttachmentRef(entity.getAttachmentRef());
        vo.setOperatedBy(entity.getOperatedBy());
        vo.setOperatedAt(entity.getOperatedAt());
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
