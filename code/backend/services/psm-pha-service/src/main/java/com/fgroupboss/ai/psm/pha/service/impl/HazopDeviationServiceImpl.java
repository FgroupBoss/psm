package com.fgroupboss.ai.psm.pha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.pha.mapper.HazopCauseMapper;
import com.fgroupboss.ai.psm.pha.mapper.HazopConsequenceMapper;
import com.fgroupboss.ai.psm.pha.mapper.HazopDeviationMapper;
import com.fgroupboss.ai.psm.pha.mapper.HazopSafeguardMapper;
import com.fgroupboss.ai.psm.pha.mapper.PhaNodeMapper;
import com.fgroupboss.ai.psm.pha.model.dto.HazopDeviationRequest;
import com.fgroupboss.ai.psm.pha.model.dto.HazopSubItemRequest;
import com.fgroupboss.ai.psm.pha.model.entity.HazopCauseEntity;
import com.fgroupboss.ai.psm.pha.model.entity.HazopConsequenceEntity;
import com.fgroupboss.ai.psm.pha.model.entity.HazopDeviationEntity;
import com.fgroupboss.ai.psm.pha.model.entity.HazopSafeguardEntity;
import com.fgroupboss.ai.psm.pha.model.entity.PhaNodeEntity;
import com.fgroupboss.ai.psm.pha.model.vo.HazopCauseVO;
import com.fgroupboss.ai.psm.pha.model.vo.HazopConsequenceVO;
import com.fgroupboss.ai.psm.pha.model.vo.HazopDeviationVO;
import com.fgroupboss.ai.psm.pha.model.vo.HazopSafeguardVO;
import com.fgroupboss.ai.psm.pha.service.HazopDeviationService;
import com.fgroupboss.ai.psm.pha.support.EntitySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HazopDeviationServiceImpl implements HazopDeviationService {

    private final HazopDeviationMapper hazopDeviationMapper;
    private final HazopCauseMapper hazopCauseMapper;
    private final HazopConsequenceMapper hazopConsequenceMapper;
    private final HazopSafeguardMapper hazopSafeguardMapper;
    private final PhaNodeMapper phaNodeMapper;

    @Override
    public List<HazopDeviationVO> listByNode(Long tenantId, Long nodeId) {
        EntitySupport.requireTenantId(tenantId);
        requireNode(tenantId, nodeId);
        List<HazopDeviationEntity> list = hazopDeviationMapper.selectList(new LambdaQueryWrapper<HazopDeviationEntity>()
                .eq(HazopDeviationEntity::getTenantId, tenantId)
                .eq(HazopDeviationEntity::getNodeId, nodeId)
                .eq(HazopDeviationEntity::getDeleted, 0)
                .orderByAsc(HazopDeviationEntity::getId));
        List<HazopDeviationVO> result = new ArrayList<HazopDeviationVO>();
        for (HazopDeviationEntity entity : list) {
            result.add(toDeviationVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public HazopDeviationVO create(Long nodeId, HazopDeviationRequest request, String operator) {
        EntitySupport.requireTenantId(request.getTenantId());
        requireNode(request.getTenantId(), nodeId);
        HazopDeviationEntity entity = new HazopDeviationEntity();
        entity.setTenantId(request.getTenantId());
        entity.setNodeId(nodeId);
        entity.setParameter(request.getParameter());
        entity.setGuideword(request.getGuideword());
        entity.setDeviationDesc(request.getDeviationDesc());
        entity.setRiskLevel(request.getRiskLevel());
        EntitySupport.initAuditFields(entity);
        hazopDeviationMapper.insert(entity);
        return toDeviationVO(entity);
    }

    @Override
    public List<HazopCauseVO> listCauses(Long tenantId, Long deviationId) {
        requireDeviation(tenantId, deviationId);
        List<HazopCauseEntity> list = hazopCauseMapper.selectList(new LambdaQueryWrapper<HazopCauseEntity>()
                .eq(HazopCauseEntity::getTenantId, tenantId)
                .eq(HazopCauseEntity::getDeviationId, deviationId)
                .eq(HazopCauseEntity::getDeleted, 0));
        return mapCauses(list);
    }

    @Override
    @Transactional
    public HazopCauseVO addCause(Long deviationId, HazopSubItemRequest request, String operator) {
        requireDeviation(request.getTenantId(), deviationId);
        HazopCauseEntity entity = new HazopCauseEntity();
        entity.setTenantId(request.getTenantId());
        entity.setDeviationId(deviationId);
        entity.setCauseDesc(request.getCauseDesc());
        entity.setFrequency(request.getFrequency());
        EntitySupport.initAuditFields(entity);
        hazopCauseMapper.insert(entity);
        return toCauseVO(entity);
    }

    @Override
    public List<HazopConsequenceVO> listConsequences(Long tenantId, Long deviationId) {
        requireDeviation(tenantId, deviationId);
        List<HazopConsequenceEntity> list = hazopConsequenceMapper.selectList(
                new LambdaQueryWrapper<HazopConsequenceEntity>()
                        .eq(HazopConsequenceEntity::getTenantId, tenantId)
                        .eq(HazopConsequenceEntity::getDeviationId, deviationId)
                        .eq(HazopConsequenceEntity::getDeleted, 0));
        List<HazopConsequenceVO> result = new ArrayList<HazopConsequenceVO>();
        for (HazopConsequenceEntity entity : list) {
            result.add(toConsequenceVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public HazopConsequenceVO addConsequence(Long deviationId, HazopSubItemRequest request, String operator) {
        requireDeviation(request.getTenantId(), deviationId);
        HazopConsequenceEntity entity = new HazopConsequenceEntity();
        entity.setTenantId(request.getTenantId());
        entity.setDeviationId(deviationId);
        entity.setConsequenceDesc(request.getConsequenceDesc());
        entity.setSeverity(request.getSeverity());
        EntitySupport.initAuditFields(entity);
        hazopConsequenceMapper.insert(entity);
        return toConsequenceVO(entity);
    }

    @Override
    public List<HazopSafeguardVO> listSafeguards(Long tenantId, Long deviationId) {
        requireDeviation(tenantId, deviationId);
        List<HazopSafeguardEntity> list = hazopSafeguardMapper.selectList(
                new LambdaQueryWrapper<HazopSafeguardEntity>()
                        .eq(HazopSafeguardEntity::getTenantId, tenantId)
                        .eq(HazopSafeguardEntity::getDeviationId, deviationId)
                        .eq(HazopSafeguardEntity::getDeleted, 0));
        List<HazopSafeguardVO> result = new ArrayList<HazopSafeguardVO>();
        for (HazopSafeguardEntity entity : list) {
            result.add(toSafeguardVO(entity));
        }
        return result;
    }

    @Override
    @Transactional
    public HazopSafeguardVO addSafeguard(Long deviationId, HazopSubItemRequest request, String operator) {
        requireDeviation(request.getTenantId(), deviationId);
        HazopSafeguardEntity entity = new HazopSafeguardEntity();
        entity.setTenantId(request.getTenantId());
        entity.setDeviationId(deviationId);
        entity.setSafeguardType(request.getSafeguardType());
        entity.setSafeguardDesc(request.getSafeguardDesc());
        entity.setEffectiveness(request.getEffectiveness());
        EntitySupport.initAuditFields(entity);
        hazopSafeguardMapper.insert(entity);
        return toSafeguardVO(entity);
    }

    private List<HazopCauseVO> mapCauses(List<HazopCauseEntity> list) {
        List<HazopCauseVO> result = new ArrayList<HazopCauseVO>();
        for (HazopCauseEntity entity : list) {
            result.add(toCauseVO(entity));
        }
        return result;
    }

    private PhaNodeEntity requireNode(Long tenantId, Long nodeId) {
        EntitySupport.requireId(nodeId);
        return EntitySupport.requireFound(
                phaNodeMapper.selectOne(new LambdaQueryWrapper<PhaNodeEntity>()
                        .eq(PhaNodeEntity::getTenantId, tenantId)
                        .eq(PhaNodeEntity::getId, nodeId)
                        .eq(PhaNodeEntity::getDeleted, 0)),
                "pha node not found");
    }

    private HazopDeviationEntity requireDeviation(Long tenantId, Long deviationId) {
        EntitySupport.requireId(deviationId);
        return EntitySupport.requireFound(
                hazopDeviationMapper.selectOne(new LambdaQueryWrapper<HazopDeviationEntity>()
                        .eq(HazopDeviationEntity::getTenantId, tenantId)
                        .eq(HazopDeviationEntity::getId, deviationId)
                        .eq(HazopDeviationEntity::getDeleted, 0)),
                "hazop deviation not found");
    }

    private HazopDeviationVO toDeviationVO(HazopDeviationEntity entity) {
        HazopDeviationVO vo = new HazopDeviationVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setNodeId(entity.getNodeId());
        vo.setParameter(entity.getParameter());
        vo.setGuideword(entity.getGuideword());
        vo.setDeviationDesc(entity.getDeviationDesc());
        vo.setRiskLevel(entity.getRiskLevel());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private HazopCauseVO toCauseVO(HazopCauseEntity entity) {
        HazopCauseVO vo = new HazopCauseVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setDeviationId(entity.getDeviationId());
        vo.setCauseDesc(entity.getCauseDesc());
        vo.setFrequency(entity.getFrequency());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private HazopConsequenceVO toConsequenceVO(HazopConsequenceEntity entity) {
        HazopConsequenceVO vo = new HazopConsequenceVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setDeviationId(entity.getDeviationId());
        vo.setConsequenceDesc(entity.getConsequenceDesc());
        vo.setSeverity(entity.getSeverity());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private HazopSafeguardVO toSafeguardVO(HazopSafeguardEntity entity) {
        HazopSafeguardVO vo = new HazopSafeguardVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setDeviationId(entity.getDeviationId());
        vo.setSafeguardType(entity.getSafeguardType());
        vo.setSafeguardDesc(entity.getSafeguardDesc());
        vo.setEffectiveness(entity.getEffectiveness());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
