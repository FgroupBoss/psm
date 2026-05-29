package com.fgroupboss.ai.psm.barrier.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.barrier.config.MiDefectStatus;
import com.fgroupboss.ai.psm.barrier.mapper.MiDefectMapper;
import com.fgroupboss.ai.psm.barrier.mapper.MiEquipmentMapper;
import com.fgroupboss.ai.psm.barrier.mapper.MiInspectionPlanMapper;
import com.fgroupboss.ai.psm.barrier.model.dto.MiDefectCloseRequest;
import com.fgroupboss.ai.psm.barrier.model.dto.MiDefectRequest;
import com.fgroupboss.ai.psm.barrier.model.dto.MiEquipmentRequest;
import com.fgroupboss.ai.psm.barrier.model.dto.MiInspectionPlanRequest;
import com.fgroupboss.ai.psm.barrier.model.entity.MiDefectEntity;
import com.fgroupboss.ai.psm.barrier.model.entity.MiEquipmentEntity;
import com.fgroupboss.ai.psm.barrier.model.entity.MiInspectionPlanEntity;
import com.fgroupboss.ai.psm.barrier.model.vo.MiDefectVO;
import com.fgroupboss.ai.psm.barrier.model.vo.MiEquipmentVO;
import com.fgroupboss.ai.psm.barrier.model.vo.MiInspectionPlanVO;
import com.fgroupboss.ai.psm.barrier.service.MechanicalIntegrityService;
import com.fgroupboss.ai.psm.barrier.support.EntitySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 机械完整性设备、检验计划与缺陷维护。
 */
@Service
@RequiredArgsConstructor
public class MechanicalIntegrityServiceImpl implements MechanicalIntegrityService {

    private final MiEquipmentMapper miEquipmentMapper;
    private final MiInspectionPlanMapper miInspectionPlanMapper;
    private final MiDefectMapper miDefectMapper;

    @Override
    public PageResult<MiEquipmentVO> pageEquipment(Long tenantId, String keyword, int pageNo, int pageSize) {
        EntitySupport.requireTenantId(tenantId);
        LambdaQueryWrapper<MiEquipmentEntity> wrapper = equipmentWrapper(tenantId);
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(q -> q.like(MiEquipmentEntity::getEquipmentCode, kw)
                    .or().like(MiEquipmentEntity::getEquipmentName, kw));
        }
        wrapper.orderByDesc(MiEquipmentEntity::getId);
        Page<MiEquipmentEntity> page = miEquipmentMapper.selectPage(
                new Page<MiEquipmentEntity>(EntitySupport.normalizePageNo(pageNo), EntitySupport.normalizePageSize(pageSize)),
                wrapper);
        return EntitySupport.toPageResult(page, this::toEquipmentVO);
    }

    @Override
    @Transactional
    public MiEquipmentVO createEquipment(MiEquipmentRequest request, String operator) {
        EntitySupport.requireTenantId(request.getTenantId());
        MiEquipmentEntity entity = new MiEquipmentEntity();
        entity.setTenantId(request.getTenantId());
        entity.setEquipmentCode(request.getEquipmentCode().trim());
        entity.setEquipmentName(request.getEquipmentName().trim());
        entity.setAreaId(request.getAreaId());
        entity.setCriticalFlag(request.getCriticalFlag() == null ? 0 : request.getCriticalFlag());
        entity.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus().trim() : "ACTIVE");
        EntitySupport.initAuditFields(entity);
        miEquipmentMapper.insert(entity);
        return toEquipmentVO(entity);
    }

    @Override
    @Transactional
    public MiEquipmentVO updateEquipment(Long id, MiEquipmentRequest request, String operator) {
        MiEquipmentEntity entity = requireEquipment(request.getTenantId(), id);
        entity.setEquipmentCode(request.getEquipmentCode().trim());
        entity.setEquipmentName(request.getEquipmentName().trim());
        entity.setAreaId(request.getAreaId());
        if (request.getCriticalFlag() != null) {
            entity.setCriticalFlag(request.getCriticalFlag());
        }
        if (StringUtils.hasText(request.getStatus())) {
            entity.setStatus(request.getStatus().trim());
        }
        EntitySupport.touchUpdated(entity);
        miEquipmentMapper.updateById(entity);
        return toEquipmentVO(entity);
    }

    @Override
    public PageResult<MiInspectionPlanVO> pageInspectionPlans(Long tenantId, Long equipmentId, int pageNo, int pageSize) {
        EntitySupport.requireTenantId(tenantId);
        LambdaQueryWrapper<MiInspectionPlanEntity> wrapper = planWrapper(tenantId);
        if (equipmentId != null) {
            wrapper.eq(MiInspectionPlanEntity::getEquipmentId, equipmentId);
        }
        wrapper.orderByDesc(MiInspectionPlanEntity::getId);
        Page<MiInspectionPlanEntity> page = miInspectionPlanMapper.selectPage(
                new Page<MiInspectionPlanEntity>(EntitySupport.normalizePageNo(pageNo), EntitySupport.normalizePageSize(pageSize)),
                wrapper);
        return EntitySupport.toPageResult(page, this::toPlanVO);
    }

    @Override
    @Transactional
    public MiInspectionPlanVO createInspectionPlan(MiInspectionPlanRequest request, String operator) {
        EntitySupport.requireTenantId(request.getTenantId());
        requireEquipment(request.getTenantId(), request.getEquipmentId());
        MiInspectionPlanEntity entity = new MiInspectionPlanEntity();
        entity.setTenantId(request.getTenantId());
        entity.setEquipmentId(request.getEquipmentId());
        entity.setPlanName(request.getPlanName().trim());
        entity.setCycleDays(request.getCycleDays());
        entity.setNextDueAt(request.getNextDueAt());
        entity.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus().trim() : "ACTIVE");
        EntitySupport.initAuditFields(entity);
        miInspectionPlanMapper.insert(entity);
        return toPlanVO(entity);
    }

    @Override
    public PageResult<MiDefectVO> pageDefects(Long tenantId, Long equipmentId, String status, int pageNo, int pageSize) {
        EntitySupport.requireTenantId(tenantId);
        LambdaQueryWrapper<MiDefectEntity> wrapper = defectWrapper(tenantId);
        if (equipmentId != null) {
            wrapper.eq(MiDefectEntity::getEquipmentId, equipmentId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(MiDefectEntity::getStatus, status.trim());
        }
        wrapper.orderByDesc(MiDefectEntity::getId);
        Page<MiDefectEntity> page = miDefectMapper.selectPage(
                new Page<MiDefectEntity>(EntitySupport.normalizePageNo(pageNo), EntitySupport.normalizePageSize(pageSize)),
                wrapper);
        return EntitySupport.toPageResult(page, this::toDefectVO);
    }

    @Override
    @Transactional
    public MiDefectVO createDefect(MiDefectRequest request, String operator) {
        EntitySupport.requireTenantId(request.getTenantId());
        requireEquipment(request.getTenantId(), request.getEquipmentId());
        MiDefectEntity entity = new MiDefectEntity();
        entity.setTenantId(request.getTenantId());
        entity.setDefectNo(resolveDefectNo(request));
        entity.setEquipmentId(request.getEquipmentId());
        entity.setDefectLevel(request.getDefectLevel().trim());
        entity.setSourceType(request.getSourceType());
        entity.setDescription(request.getDescription().trim());
        entity.setRepairDeadline(request.getRepairDeadline());
        entity.setStatus(MiDefectStatus.PENDING_CONFIRM.name());
        EntitySupport.initAuditFields(entity);
        miDefectMapper.insert(entity);
        return toDefectVO(entity);
    }

    @Override
    @Transactional
    public MiDefectVO closeDefect(Long id, MiDefectCloseRequest request, String operator) {
        MiDefectEntity entity = requireDefect(request.getTenantId(), id);
        MiDefectStatus.assertClose(entity.getStatus());
        entity.setStatus(MiDefectStatus.targetAfterClose());
        EntitySupport.touchUpdated(entity);
        miDefectMapper.updateById(entity);
        return toDefectVO(entity);
    }

    private String resolveDefectNo(MiDefectRequest request) {
        if (StringUtils.hasText(request.getDefectNo())) {
            return request.getDefectNo().trim();
        }
        return "DEF-" + request.getTenantId() + "-" + System.currentTimeMillis();
    }

    private MiEquipmentEntity requireEquipment(Long tenantId, Long id) {
        EntitySupport.requireId(id);
        return EntitySupport.requireFound(
                miEquipmentMapper.selectOne(equipmentWrapper(tenantId).eq(MiEquipmentEntity::getId, id)),
                "equipment not found");
    }

    private MiDefectEntity requireDefect(Long tenantId, Long id) {
        EntitySupport.requireId(id);
        return EntitySupport.requireFound(
                miDefectMapper.selectOne(defectWrapper(tenantId).eq(MiDefectEntity::getId, id)),
                "defect not found");
    }

    private LambdaQueryWrapper<MiEquipmentEntity> equipmentWrapper(Long tenantId) {
        return new LambdaQueryWrapper<MiEquipmentEntity>()
                .eq(MiEquipmentEntity::getTenantId, tenantId)
                .eq(MiEquipmentEntity::getDeleted, 0);
    }

    private LambdaQueryWrapper<MiInspectionPlanEntity> planWrapper(Long tenantId) {
        return new LambdaQueryWrapper<MiInspectionPlanEntity>()
                .eq(MiInspectionPlanEntity::getTenantId, tenantId)
                .eq(MiInspectionPlanEntity::getDeleted, 0);
    }

    private LambdaQueryWrapper<MiDefectEntity> defectWrapper(Long tenantId) {
        return new LambdaQueryWrapper<MiDefectEntity>()
                .eq(MiDefectEntity::getTenantId, tenantId)
                .eq(MiDefectEntity::getDeleted, 0);
    }

    private MiEquipmentVO toEquipmentVO(MiEquipmentEntity entity) {
        MiEquipmentVO vo = new MiEquipmentVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setEquipmentCode(entity.getEquipmentCode());
        vo.setEquipmentName(entity.getEquipmentName());
        vo.setAreaId(entity.getAreaId());
        vo.setCriticalFlag(entity.getCriticalFlag());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private MiInspectionPlanVO toPlanVO(MiInspectionPlanEntity entity) {
        MiInspectionPlanVO vo = new MiInspectionPlanVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setEquipmentId(entity.getEquipmentId());
        vo.setPlanName(entity.getPlanName());
        vo.setCycleDays(entity.getCycleDays());
        vo.setNextDueAt(entity.getNextDueAt());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private MiDefectVO toDefectVO(MiDefectEntity entity) {
        MiDefectVO vo = new MiDefectVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setDefectNo(entity.getDefectNo());
        vo.setEquipmentId(entity.getEquipmentId());
        vo.setDefectLevel(entity.getDefectLevel());
        vo.setSourceType(entity.getSourceType());
        vo.setDescription(entity.getDescription());
        vo.setRepairDeadline(entity.getRepairDeadline());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
