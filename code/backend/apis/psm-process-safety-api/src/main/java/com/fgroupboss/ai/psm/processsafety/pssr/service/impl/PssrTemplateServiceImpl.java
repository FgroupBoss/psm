package com.fgroupboss.ai.psm.processsafety.pssr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.processsafety.pssr.mapper.PssrTemplateMapper;
import com.fgroupboss.ai.psm.processsafety.pssr.model.dto.PssrTemplateRequest;
import com.fgroupboss.ai.psm.processsafety.pssr.model.entity.PssrTemplateEntity;
import com.fgroupboss.ai.psm.processsafety.pssr.model.vo.PssrTemplateVO;
import com.fgroupboss.ai.psm.processsafety.pssr.service.PssrTemplateService;
import com.fgroupboss.ai.psm.common.data.EntitySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * PSSR 清单模板 CRUD。
 */
@Service
@RequiredArgsConstructor
public class PssrTemplateServiceImpl implements PssrTemplateService {

    private final PssrTemplateMapper pssrTemplateMapper;

    @Override
    public PageResult<PssrTemplateVO> page(Long tenantId, String keyword, int pageNo, int pageSize) {
        EntitySupport.requireTenantId(tenantId);
        LambdaQueryWrapper<PssrTemplateEntity> wrapper = baseWrapper(tenantId);
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(q -> q.like(PssrTemplateEntity::getTemplateCode, kw)
                    .or().like(PssrTemplateEntity::getTemplateName, kw));
        }
        wrapper.orderByDesc(PssrTemplateEntity::getId);
        Page<PssrTemplateEntity> page = pssrTemplateMapper.selectPage(
                new Page<PssrTemplateEntity>(EntitySupport.normalizePageNo(pageNo), EntitySupport.normalizePageSize(pageSize)),
                wrapper);
        return EntitySupport.toPageResult(page, this::toVO);
    }

    @Override
    public PssrTemplateVO getById(Long tenantId, Long id) {
        return toVO(requireTemplate(tenantId, id));
    }

    @Override
    @Transactional
    public PssrTemplateVO create(PssrTemplateRequest request, String operator) {
        EntitySupport.requireTenantId(request.getTenantId());
        PssrTemplateEntity entity = new PssrTemplateEntity();
        entity.setTenantId(request.getTenantId());
        entity.setTemplateCode(request.getTemplateCode().trim());
        entity.setTemplateName(request.getTemplateName().trim());
        entity.setUnitType(request.getUnitType());
        entity.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus().trim() : "ACTIVE");
        EntitySupport.initAuditFields(entity);
        pssrTemplateMapper.insert(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public PssrTemplateVO update(Long id, PssrTemplateRequest request, String operator) {
        PssrTemplateEntity entity = requireTemplate(request.getTenantId(), id);
        entity.setTemplateCode(request.getTemplateCode().trim());
        entity.setTemplateName(request.getTemplateName().trim());
        entity.setUnitType(request.getUnitType());
        if (StringUtils.hasText(request.getStatus())) {
            entity.setStatus(request.getStatus().trim());
        }
        EntitySupport.touchUpdated(entity);
        pssrTemplateMapper.updateById(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public void delete(Long tenantId, Long id, String operator) {
        PssrTemplateEntity entity = requireTemplate(tenantId, id);
        entity.setDeleted(1);
        EntitySupport.touchUpdated(entity);
        pssrTemplateMapper.updateById(entity);
    }

    private PssrTemplateEntity requireTemplate(Long tenantId, Long id) {
        EntitySupport.requireId(id);
        return EntitySupport.requireFound(
                pssrTemplateMapper.selectOne(baseWrapper(tenantId).eq(PssrTemplateEntity::getId, id)),
                "template not found");
    }

    private LambdaQueryWrapper<PssrTemplateEntity> baseWrapper(Long tenantId) {
        return new LambdaQueryWrapper<PssrTemplateEntity>()
                .eq(PssrTemplateEntity::getTenantId, tenantId)
                .eq(PssrTemplateEntity::getDeleted, 0);
    }

    private PssrTemplateVO toVO(PssrTemplateEntity entity) {
        PssrTemplateVO vo = new PssrTemplateVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setTemplateCode(entity.getTemplateCode());
        vo.setTemplateName(entity.getTemplateName());
        vo.setUnitType(entity.getUnitType());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
