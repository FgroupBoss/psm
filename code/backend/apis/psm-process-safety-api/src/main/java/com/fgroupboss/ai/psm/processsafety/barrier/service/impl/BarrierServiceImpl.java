package com.fgroupboss.ai.psm.processsafety.barrier.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.processsafety.barrier.config.BarrierStatus;
import com.fgroupboss.ai.psm.processsafety.barrier.mapper.BarrierDegradationMapper;
import com.fgroupboss.ai.psm.processsafety.barrier.mapper.BarrierHealthSnapshotMapper;
import com.fgroupboss.ai.psm.processsafety.barrier.mapper.BarrierMapper;
import com.fgroupboss.ai.psm.processsafety.barrier.model.dto.BarrierDegradeRequest;
import com.fgroupboss.ai.psm.processsafety.barrier.model.dto.BarrierRequest;
import com.fgroupboss.ai.psm.processsafety.barrier.model.dto.BarrierRestoreRequest;
import com.fgroupboss.ai.psm.processsafety.barrier.model.dto.BarrierRuleCheckRequest;
import com.fgroupboss.ai.psm.processsafety.barrier.model.entity.BarrierDegradationEntity;
import com.fgroupboss.ai.psm.processsafety.barrier.model.entity.BarrierEntity;
import com.fgroupboss.ai.psm.processsafety.barrier.model.entity.BarrierHealthSnapshotEntity;
import com.fgroupboss.ai.psm.processsafety.barrier.model.vo.BarrierHealthVO;
import com.fgroupboss.ai.psm.processsafety.barrier.model.vo.BarrierRuleCheckVO;
import com.fgroupboss.ai.psm.processsafety.barrier.model.vo.BarrierVO;
import com.fgroupboss.ai.psm.processsafety.barrier.service.BarrierService;
import com.fgroupboss.ai.psm.common.data.EntitySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 屏障台账、健康度快照、降级与规则查询。
 */
@Service
@RequiredArgsConstructor
public class BarrierServiceImpl implements BarrierService {

    private static final BigDecimal DEFAULT_HEALTH = new BigDecimal("100.00");

    private final BarrierMapper barrierMapper;
    private final BarrierHealthSnapshotMapper barrierHealthSnapshotMapper;
    private final BarrierDegradationMapper barrierDegradationMapper;

    @Override
    public PageResult<BarrierVO> page(Long tenantId, String keyword, String status, int pageNo, int pageSize) {
        EntitySupport.requireTenantId(tenantId);
        LambdaQueryWrapper<BarrierEntity> wrapper = baseWrapper(tenantId);
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(q -> q.like(BarrierEntity::getBarrierCode, kw).or().like(BarrierEntity::getBarrierName, kw));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(BarrierEntity::getStatus, status.trim());
        }
        wrapper.orderByDesc(BarrierEntity::getId);
        Page<BarrierEntity> page = barrierMapper.selectPage(
                new Page<BarrierEntity>(EntitySupport.normalizePageNo(pageNo), EntitySupport.normalizePageSize(pageSize)),
                wrapper);
        return EntitySupport.toPageResult(page, this::toVO);
    }

    @Override
    public BarrierVO getById(Long tenantId, Long id) {
        return toVO(requireBarrier(tenantId, id));
    }

    @Override
    @Transactional
    public BarrierVO create(BarrierRequest request, String operator) {
        EntitySupport.requireTenantId(request.getTenantId());
        BarrierEntity entity = new BarrierEntity();
        entity.setTenantId(request.getTenantId());
        entity.setBarrierCode(request.getBarrierCode().trim());
        entity.setBarrierName(request.getBarrierName().trim());
        entity.setBarrierType(request.getBarrierType());
        entity.setMajorHazardId(request.getMajorHazardId());
        entity.setHazopScenarioId(request.getHazopScenarioId());
        entity.setOwnerOrgId(request.getOwnerOrgId());
        entity.setHealthScore(request.getHealthScore() == null ? DEFAULT_HEALTH : request.getHealthScore());
        entity.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus().trim() : BarrierStatus.NORMAL.name());
        EntitySupport.initAuditFields(entity);
        barrierMapper.insert(entity);
        saveHealthSnapshot(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public BarrierVO update(Long id, BarrierRequest request, String operator) {
        BarrierEntity entity = requireBarrier(request.getTenantId(), id);
        entity.setBarrierCode(request.getBarrierCode().trim());
        entity.setBarrierName(request.getBarrierName().trim());
        entity.setBarrierType(request.getBarrierType());
        entity.setMajorHazardId(request.getMajorHazardId());
        entity.setHazopScenarioId(request.getHazopScenarioId());
        entity.setOwnerOrgId(request.getOwnerOrgId());
        if (request.getHealthScore() != null) {
            entity.setHealthScore(request.getHealthScore());
        }
        if (StringUtils.hasText(request.getStatus())) {
            entity.setStatus(request.getStatus().trim());
        }
        EntitySupport.touchUpdated(entity);
        barrierMapper.updateById(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public void delete(Long tenantId, Long id, String operator) {
        BarrierEntity entity = requireBarrier(tenantId, id);
        entity.setDeleted(1);
        EntitySupport.touchUpdated(entity);
        barrierMapper.updateById(entity);
    }

    @Override
    public BarrierHealthVO getHealth(Long tenantId, Long id) {
        BarrierEntity barrier = requireBarrier(tenantId, id);
        BarrierHealthVO vo = new BarrierHealthVO();
        vo.setBarrierId(barrier.getId());
        vo.setHealthScore(barrier.getHealthScore());
        vo.setStatus(barrier.getStatus());
        LambdaQueryWrapper<BarrierHealthSnapshotEntity> wrapper = new LambdaQueryWrapper<BarrierHealthSnapshotEntity>()
                .eq(BarrierHealthSnapshotEntity::getTenantId, tenantId)
                .eq(BarrierHealthSnapshotEntity::getBarrierId, id)
                .eq(BarrierHealthSnapshotEntity::getDeleted, 0)
                .orderByDesc(BarrierHealthSnapshotEntity::getSnapshotAt)
                .last("limit 1");
        BarrierHealthSnapshotEntity snapshot = barrierHealthSnapshotMapper.selectOne(wrapper);
        if (snapshot != null) {
            vo.setSnapshotAt(snapshot.getSnapshotAt());
        }
        return vo;
    }

    @Override
    @Transactional
    public BarrierVO degrade(Long id, BarrierDegradeRequest request, String operator) {
        BarrierEntity entity = requireBarrier(request.getTenantId(), id);
        String fromStatus = entity.getStatus();
        BarrierStatus.assertDegrade(fromStatus);
        String toStatus = BarrierStatus.targetAfterDegrade(fromStatus);

        BarrierDegradationEntity record = new BarrierDegradationEntity();
        record.setTenantId(request.getTenantId());
        record.setBarrierId(id);
        record.setFromStatus(fromStatus);
        record.setToStatus(toStatus);
        record.setReason(request.getReason());
        EntitySupport.initAuditFields(record);
        barrierDegradationMapper.insert(record);

        entity.setStatus(toStatus);
        EntitySupport.touchUpdated(entity);
        barrierMapper.updateById(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public BarrierVO restore(Long id, BarrierRestoreRequest request, String operator) {
        BarrierEntity entity = requireBarrier(request.getTenantId(), id);
        BarrierStatus.assertRestore(entity.getStatus());
        entity.setStatus(BarrierStatus.targetAfterRestore());
        if (request.getHealthScore() != null) {
            entity.setHealthScore(request.getHealthScore());
        }
        EntitySupport.touchUpdated(entity);
        barrierMapper.updateById(entity);
        saveHealthSnapshot(entity);
        return toVO(entity);
    }

    @Override
    public BarrierRuleCheckVO ruleCheck(BarrierRuleCheckRequest request) {
        EntitySupport.requireTenantId(request.getTenantId());
        LambdaQueryWrapper<BarrierEntity> wrapper = baseWrapper(request.getTenantId());
        if (request.getMajorHazardId() != null) {
            wrapper.eq(BarrierEntity::getMajorHazardId, request.getMajorHazardId());
        }
        if (!CollectionUtils.isEmpty(request.getBarrierIds())) {
            wrapper.in(BarrierEntity::getId, request.getBarrierIds());
        }
        List<BarrierEntity> barriers = barrierMapper.selectList(wrapper);

        List<String> messages = new ArrayList<String>();
        int failed = 0;
        for (BarrierEntity barrier : barriers) {
            if (BarrierStatus.FAILED.name().equals(barrier.getStatus())
                    || BarrierStatus.DEGRADED.name().equals(barrier.getStatus())) {
                failed++;
                messages.add("barrier#" + barrier.getId() + " status=" + barrier.getStatus());
            }
        }
        BarrierRuleCheckVO vo = new BarrierRuleCheckVO();
        vo.setFailedBarrierCount(failed);
        vo.setPassed(failed == 0);
        vo.setMessages(messages);
        return vo;
    }

    private void saveHealthSnapshot(BarrierEntity entity) {
        BarrierHealthSnapshotEntity snapshot = new BarrierHealthSnapshotEntity();
        snapshot.setTenantId(entity.getTenantId());
        snapshot.setBarrierId(entity.getId());
        snapshot.setHealthScore(entity.getHealthScore());
        snapshot.setSnapshotAt(EntitySupport.now());
        EntitySupport.initAuditFields(snapshot);
        barrierHealthSnapshotMapper.insert(snapshot);
    }

    private BarrierEntity requireBarrier(Long tenantId, Long id) {
        EntitySupport.requireId(id);
        return EntitySupport.requireFound(
                barrierMapper.selectOne(baseWrapper(tenantId).eq(BarrierEntity::getId, id)),
                "barrier not found");
    }

    private LambdaQueryWrapper<BarrierEntity> baseWrapper(Long tenantId) {
        return new LambdaQueryWrapper<BarrierEntity>()
                .eq(BarrierEntity::getTenantId, tenantId)
                .eq(BarrierEntity::getDeleted, 0);
    }

    private BarrierVO toVO(BarrierEntity entity) {
        BarrierVO vo = new BarrierVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setBarrierCode(entity.getBarrierCode());
        vo.setBarrierName(entity.getBarrierName());
        vo.setBarrierType(entity.getBarrierType());
        vo.setMajorHazardId(entity.getMajorHazardId());
        vo.setHazopScenarioId(entity.getHazopScenarioId());
        vo.setOwnerOrgId(entity.getOwnerOrgId());
        vo.setHealthScore(entity.getHealthScore());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
