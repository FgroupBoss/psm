package com.fgroupboss.ai.psm.pha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.pha.mapper.LopaIplMapper;
import com.fgroupboss.ai.psm.pha.mapper.LopaScenarioMapper;
import com.fgroupboss.ai.psm.pha.model.dto.LopaScenarioRequest;
import com.fgroupboss.ai.psm.pha.model.entity.LopaIplEntity;
import com.fgroupboss.ai.psm.pha.model.entity.LopaScenarioEntity;
import com.fgroupboss.ai.psm.pha.model.vo.LopaCalculateResultVO;
import com.fgroupboss.ai.psm.pha.model.vo.LopaScenarioVO;
import com.fgroupboss.ai.psm.pha.service.LopaScenarioService;
import com.fgroupboss.ai.psm.pha.support.EntitySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LopaScenarioServiceImpl implements LopaScenarioService {

    private final LopaScenarioMapper lopaScenarioMapper;
    private final LopaIplMapper lopaIplMapper;

    @Override
    public PageResult<LopaScenarioVO> page(Long tenantId, Long projectId, int pageNo, int pageSize) {
        EntitySupport.requireTenantId(tenantId);
        LambdaQueryWrapper<LopaScenarioEntity> wrapper = new LambdaQueryWrapper<LopaScenarioEntity>()
                .eq(LopaScenarioEntity::getTenantId, tenantId)
                .eq(LopaScenarioEntity::getDeleted, 0);
        if (projectId != null) {
            wrapper.eq(LopaScenarioEntity::getProjectId, projectId);
        }
        wrapper.orderByDesc(LopaScenarioEntity::getId);
        Page<LopaScenarioEntity> page = lopaScenarioMapper.selectPage(
                new Page<LopaScenarioEntity>(EntitySupport.normalizePageNo(pageNo), EntitySupport.normalizePageSize(pageSize)),
                wrapper);
        return EntitySupport.toPageResult(page, this::toVO);
    }

    @Override
    @Transactional
    public LopaScenarioVO create(LopaScenarioRequest request, String operator) {
        EntitySupport.requireTenantId(request.getTenantId());
        LopaScenarioEntity entity = new LopaScenarioEntity();
        entity.setTenantId(request.getTenantId());
        entity.setScenarioNo(request.getScenarioNo().trim());
        entity.setProjectId(request.getProjectId());
        entity.setDeviationId(request.getDeviationId());
        entity.setInitiatingEventFrequency(request.getInitiatingEventFrequency());
        entity.setConsequenceSeverity(request.getConsequenceSeverity());
        entity.setTargetFrequency(request.getTargetFrequency());
        entity.setSilRecommendation(request.getSilRecommendation());
        entity.setCalculationVersion(request.getCalculationVersion());
        EntitySupport.initAuditFields(entity);
        lopaScenarioMapper.insert(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public LopaCalculateResultVO calculate(Long id, Long tenantId) {
        LopaScenarioEntity scenario = requireScenario(tenantId, id);
        if (scenario.getInitiatingEventFrequency() == null) {
            throw new BusinessException(400, "initiating event frequency is required for LOPA calculation");
        }
        BigDecimal mitigated = scenario.getInitiatingEventFrequency();
        List<LopaIplEntity> ipls = lopaIplMapper.selectList(new LambdaQueryWrapper<LopaIplEntity>()
                .eq(LopaIplEntity::getTenantId, tenantId)
                .eq(LopaIplEntity::getScenarioId, id)
                .eq(LopaIplEntity::getDeleted, 0));
        for (LopaIplEntity ipl : ipls) {
            if (ipl.getPfd() != null) {
                mitigated = mitigated.multiply(ipl.getPfd());
            }
        }
        mitigated = mitigated.setScale(8, RoundingMode.HALF_UP);
        String sil = resolveSil(mitigated, scenario.getTargetFrequency());
        String version = "v1-" + System.currentTimeMillis();
        scenario.setMitigatedFrequency(mitigated);
        scenario.setSilRecommendation(sil);
        scenario.setCalculationVersion(version);
        EntitySupport.touchUpdated(scenario);
        lopaScenarioMapper.updateById(scenario);

        LopaCalculateResultVO result = new LopaCalculateResultVO();
        result.setScenarioId(id);
        result.setMitigatedFrequency(mitigated);
        result.setSilRecommendation(sil);
        result.setCalculationVersion(version);
        return result;
    }

    private String resolveSil(BigDecimal mitigated, BigDecimal target) {
        if (target == null) {
            return "SIL-NA";
        }
        return mitigated.compareTo(target) <= 0 ? "SIL-OK" : "SIL-REQUIRED";
    }

    private LopaScenarioEntity requireScenario(Long tenantId, Long id) {
        EntitySupport.requireId(id);
        return EntitySupport.requireFound(
                lopaScenarioMapper.selectOne(new LambdaQueryWrapper<LopaScenarioEntity>()
                        .eq(LopaScenarioEntity::getTenantId, tenantId)
                        .eq(LopaScenarioEntity::getId, id)
                        .eq(LopaScenarioEntity::getDeleted, 0)),
                "lopa scenario not found");
    }

    private LopaScenarioVO toVO(LopaScenarioEntity entity) {
        LopaScenarioVO vo = new LopaScenarioVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setScenarioNo(entity.getScenarioNo());
        vo.setProjectId(entity.getProjectId());
        vo.setDeviationId(entity.getDeviationId());
        vo.setInitiatingEventFrequency(entity.getInitiatingEventFrequency());
        vo.setConsequenceSeverity(entity.getConsequenceSeverity());
        vo.setTargetFrequency(entity.getTargetFrequency());
        vo.setMitigatedFrequency(entity.getMitigatedFrequency());
        vo.setSilRecommendation(entity.getSilRecommendation());
        vo.setCalculationVersion(entity.getCalculationVersion());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
