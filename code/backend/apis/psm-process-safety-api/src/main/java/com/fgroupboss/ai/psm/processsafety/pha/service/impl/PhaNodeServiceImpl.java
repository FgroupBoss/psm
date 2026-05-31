package com.fgroupboss.ai.psm.processsafety.pha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.processsafety.pha.mapper.PhaNodeMapper;
import com.fgroupboss.ai.psm.processsafety.pha.mapper.PhaProjectMapper;
import com.fgroupboss.ai.psm.processsafety.pha.model.dto.PhaNodeRequest;
import com.fgroupboss.ai.psm.processsafety.pha.model.entity.PhaNodeEntity;
import com.fgroupboss.ai.psm.processsafety.pha.model.entity.PhaProjectEntity;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.PhaNodeVO;
import com.fgroupboss.ai.psm.processsafety.pha.service.PhaNodeService;
import com.fgroupboss.ai.psm.common.data.EntitySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhaNodeServiceImpl implements PhaNodeService {

    private final PhaNodeMapper phaNodeMapper;
    private final PhaProjectMapper phaProjectMapper;

    @Override
    public List<PhaNodeVO> listByProject(Long tenantId, Long projectId) {
        requireProject(tenantId, projectId);
        List<PhaNodeEntity> entities = phaNodeMapper.selectList(
                new LambdaQueryWrapper<PhaNodeEntity>()
                        .eq(PhaNodeEntity::getTenantId, tenantId)
                        .eq(PhaNodeEntity::getProjectId, projectId)
                        .eq(PhaNodeEntity::getDeleted, 0)
                        .orderByAsc(PhaNodeEntity::getId));
        List<PhaNodeVO> list = new ArrayList<PhaNodeVO>();
        for (PhaNodeEntity entity : entities) {
            list.add(toNodeVO(entity));
        }
        return list;
    }

    @Override
    @Transactional
    public PhaNodeVO create(Long projectId, PhaNodeRequest request, String operator) {
        requireProject(request.getTenantId(), projectId);
        PhaNodeEntity entity = new PhaNodeEntity();
        entity.setTenantId(request.getTenantId());
        entity.setProjectId(projectId);
        entity.setNodeNo(request.getNodeNo().trim());
        entity.setNodeName(request.getNodeName().trim());
        entity.setDesignIntent(request.getDesignIntent());
        entity.setParameters(request.getParameters());
        EntitySupport.initAuditFields(entity);
        phaNodeMapper.insert(entity);
        return toNodeVO(entity);
    }

    private PhaProjectEntity requireProject(Long tenantId, Long projectId) {
        EntitySupport.requireTenantId(tenantId);
        EntitySupport.requireId(projectId);
        return EntitySupport.requireFound(
                phaProjectMapper.selectOne(new LambdaQueryWrapper<PhaProjectEntity>()
                        .eq(PhaProjectEntity::getTenantId, tenantId)
                        .eq(PhaProjectEntity::getId, projectId)
                        .eq(PhaProjectEntity::getDeleted, 0)),
                "pha project not found");
    }

    private PhaNodeVO toNodeVO(PhaNodeEntity entity) {
        PhaNodeVO vo = new PhaNodeVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setProjectId(entity.getProjectId());
        vo.setNodeNo(entity.getNodeNo());
        vo.setNodeName(entity.getNodeName());
        vo.setDesignIntent(entity.getDesignIntent());
        vo.setParameters(entity.getParameters());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
