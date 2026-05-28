package com.fgroupboss.ai.psm.inspection.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.inspection.mapper.ChecklistItemMapper;
import com.fgroupboss.ai.psm.inspection.mapper.ChecklistTemplateMapper;
import com.fgroupboss.ai.psm.inspection.model.dto.ChecklistItemRequest;
import com.fgroupboss.ai.psm.inspection.model.dto.ChecklistTemplateRequest;
import com.fgroupboss.ai.psm.inspection.model.entity.ChecklistItemEntity;
import com.fgroupboss.ai.psm.inspection.model.entity.ChecklistTemplateEntity;
import com.fgroupboss.ai.psm.inspection.model.vo.ChecklistItemVO;
import com.fgroupboss.ai.psm.inspection.model.vo.ChecklistTemplateVO;
import com.fgroupboss.ai.psm.inspection.service.ChecklistTemplateService;
import com.fgroupboss.ai.psm.inspection.support.InspectionSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 检查表模板业务实现。
 */
@Service
@RequiredArgsConstructor
public class ChecklistTemplateServiceImpl implements ChecklistTemplateService {

    private final ChecklistTemplateMapper templateMapper;
    private final ChecklistItemMapper itemMapper;

    @Override
    public PageResult<ChecklistTemplateVO> page(Long tenantId, String keyword, int pageNo, int pageSize) {
        InspectionSupport.requireTenantId(tenantId);
        InspectionSupport.Page page = InspectionSupport.normalizePage(pageNo, pageSize);
        String normalizedKeyword = InspectionSupport.normalizeText(keyword);
        long total = templateMapper.countByTenant(tenantId, normalizedKeyword);
        List<ChecklistTemplateEntity> entities = total == 0
                ? new ArrayList<ChecklistTemplateEntity>()
                : templateMapper.listByTenant(tenantId, normalizedKeyword, page.offset, page.pageSize);
        List<ChecklistTemplateVO> records = new ArrayList<ChecklistTemplateVO>();
        for (ChecklistTemplateEntity entity : entities) {
            records.add(toVO(entity, false));
        }
        return new PageResult<ChecklistTemplateVO>(total, page.pageNo, page.pageSize, records);
    }

    @Override
    public ChecklistTemplateVO getById(Long tenantId, Long id) {
        return toVO(requireTemplate(tenantId, id), true);
    }

    @Override
    @Transactional
    public ChecklistTemplateVO create(ChecklistTemplateRequest request) {
        InspectionSupport.requireTenantId(request.getTenantId());
        assertCodeUnique(request.getTenantId(), request.getTemplateCode(), null);
        ChecklistTemplateEntity entity = new ChecklistTemplateEntity();
        entity.setTenantId(request.getTenantId());
        entity.setTemplateCode(request.getTemplateCode().trim());
        entity.setTemplateName(request.getTemplateName().trim());
        entity.setCategory(request.getCategory());
        entity.setStatus("ENABLED");
        entity.setRemark(request.getRemark());
        entity.setDeleted(0);
        templateMapper.insert(entity);
        saveItems(request.getTenantId(), entity.getId(), request.getItems());
        return toVO(entity, true);
    }

    @Override
    @Transactional
    public ChecklistTemplateVO update(Long id, ChecklistTemplateRequest request) {
        ChecklistTemplateEntity entity = requireTemplate(request.getTenantId(), id);
        assertCodeUnique(request.getTenantId(), request.getTemplateCode(), id);
        entity.setTemplateCode(request.getTemplateCode().trim());
        entity.setTemplateName(request.getTemplateName().trim());
        entity.setCategory(request.getCategory());
        entity.setRemark(request.getRemark());
        templateMapper.updateById(entity);
        if (!CollectionUtils.isEmpty(request.getItems())) {
            itemMapper.softDeleteByTemplate(request.getTenantId(), entity.getId());
            saveItems(request.getTenantId(), entity.getId(), request.getItems());
        }
        return toVO(entity, true);
    }

    private void saveItems(Long tenantId, Long templateId, List<ChecklistItemRequest> items) {
        if (CollectionUtils.isEmpty(items)) {
            return;
        }
        int order = 0;
        for (ChecklistItemRequest itemRequest : items) {
            ChecklistItemEntity item = new ChecklistItemEntity();
            item.setTenantId(tenantId);
            item.setTemplateId(templateId);
            item.setItemCode(itemRequest.getItemCode().trim());
            item.setItemName(itemRequest.getItemName().trim());
            item.setItemType(itemRequest.getItemType());
            item.setStandardValue(itemRequest.getStandardValue());
            item.setLowerLimit(itemRequest.getLowerLimit());
            item.setUpperLimit(itemRequest.getUpperLimit());
            item.setUnit(itemRequest.getUnit());
            item.setAbnormalRule(itemRequest.getAbnormalRule());
            item.setPhotoRequired(Boolean.TRUE.equals(itemRequest.getPhotoRequired()) ? 1 : 0);
            item.setSortOrder(itemRequest.getSortOrder() != null ? itemRequest.getSortOrder() : order++);
            item.setStatus("ENABLED");
            item.setDeleted(0);
            itemMapper.insert(item);
        }
    }

    private void assertCodeUnique(Long tenantId, String templateCode, Long excludeId) {
        ChecklistTemplateEntity existing = templateMapper.findByCode(tenantId, templateCode.trim());
        if (existing != null && (excludeId == null || !excludeId.equals(existing.getId()))) {
            throw new BusinessException(400, "template code already exists: " + templateCode);
        }
    }

    private ChecklistTemplateEntity requireTemplate(Long tenantId, Long id) {
        InspectionSupport.requireTenantId(tenantId);
        ChecklistTemplateEntity entity = templateMapper.selectById(id);
        if (entity == null || (entity.getDeleted() != null && entity.getDeleted() == 1)
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "checklist template not found");
        }
        return entity;
    }

    private ChecklistTemplateVO toVO(ChecklistTemplateEntity entity, boolean withItems) {
        ChecklistTemplateVO vo = new ChecklistTemplateVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setTemplateCode(entity.getTemplateCode());
        vo.setTemplateName(entity.getTemplateName());
        vo.setCategory(entity.getCategory());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        if (withItems) {
            List<ChecklistItemVO> items = new ArrayList<ChecklistItemVO>();
            for (ChecklistItemEntity item : itemMapper.listByTemplate(entity.getTenantId(), entity.getId())) {
                items.add(toItemVO(item));
            }
            vo.setItems(items);
        }
        return vo;
    }

    private ChecklistItemVO toItemVO(ChecklistItemEntity entity) {
        ChecklistItemVO vo = new ChecklistItemVO();
        vo.setId(entity.getId());
        vo.setTemplateId(entity.getTemplateId());
        vo.setItemCode(entity.getItemCode());
        vo.setItemName(entity.getItemName());
        vo.setItemType(entity.getItemType());
        vo.setStandardValue(entity.getStandardValue());
        vo.setLowerLimit(entity.getLowerLimit());
        vo.setUpperLimit(entity.getUpperLimit());
        vo.setUnit(entity.getUnit());
        vo.setAbnormalRule(entity.getAbnormalRule());
        vo.setPhotoRequired(entity.getPhotoRequired());
        vo.setSortOrder(entity.getSortOrder());
        vo.setStatus(entity.getStatus());
        return vo;
    }
}
