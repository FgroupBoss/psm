package com.fgroupboss.ai.psm.processsafety.configrule.hotwork.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.config.HotWorkSignMode;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.config.HotWorkWorkflowStatus;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.mapper.HotWorkWorkflowNodeMapper;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.mapper.HotWorkWorkflowTemplateMapper;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkApproverResolveDTO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkWorkflowNodeRequest;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkWorkflowNodeSnapshotDTO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkWorkflowQueryDTO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkWorkflowSnapshotDTO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkWorkflowTemplateRequest;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.entity.HotWorkWorkflowNodeEntity;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.entity.HotWorkWorkflowTemplateEntity;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.vo.HotWorkApproverResolveResultVO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.vo.HotWorkWorkflowDetailVO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.vo.HotWorkWorkflowSummaryVO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.service.HotWorkWorkflowService;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.support.HotWorkApproverResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotWorkWorkflowServiceImpl implements HotWorkWorkflowService {

    private static final TypeReference<List<Long>> LONG_LIST = new TypeReference<List<Long>>() {
    };

    private final HotWorkWorkflowTemplateMapper templateMapper;
    private final HotWorkWorkflowNodeMapper nodeMapper;
    private final HotWorkApproverResolver approverResolver;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public HotWorkWorkflowDetailVO create(HotWorkWorkflowTemplateRequest request, String operator) {
        validateTemplateRequest(request);
        HotWorkWorkflowTemplateEntity entity = new HotWorkWorkflowTemplateEntity();
        entity.setTenantId(request.getTenantId());
        entity.setTemplateCode(request.getTemplateCode().trim());
        entity.setTemplateName(request.getTemplateName().trim());
        entity.setHotWorkLevel(request.getHotWorkLevel().trim());
        entity.setAreaScopeType(normalizeScope(request.getAreaScopeType()));
        entity.setAreaIdsJson(writeAreaIds(request.getAreaIds()));
        entity.setVersionNo(1);
        entity.setStatus(HotWorkWorkflowStatus.DRAFT);
        entity.setRemark(trim(request.getRemark()));
        entity.setCreatedBy(operator);
        entity.setUpdatedBy(operator);
        entity.setDeleted(0);
        templateMapper.insert(entity);
        replaceNodes(entity.getTenantId(), entity.getId(), request.getNodes());
        return get(entity.getTenantId(), entity.getId());
    }

    @Override
    @Transactional
    public HotWorkWorkflowDetailVO update(Long id, HotWorkWorkflowTemplateRequest request, String operator) {
        HotWorkWorkflowTemplateEntity existing = requireTemplate(request.getTenantId(), id);
        if (!HotWorkWorkflowStatus.DRAFT.equals(existing.getStatus())) {
            throw new BusinessException(409, "only draft template can be updated");
        }
        validateTemplateRequest(request);
        existing.setTemplateName(request.getTemplateName().trim());
        existing.setHotWorkLevel(request.getHotWorkLevel().trim());
        existing.setAreaScopeType(normalizeScope(request.getAreaScopeType()));
        existing.setAreaIdsJson(writeAreaIds(request.getAreaIds()));
        existing.setRemark(trim(request.getRemark()));
        existing.setUpdatedBy(operator);
        templateMapper.updateById(existing);
        replaceNodes(existing.getTenantId(), existing.getId(), request.getNodes());
        return get(existing.getTenantId(), existing.getId());
    }

    @Override
    public HotWorkWorkflowDetailVO get(Long tenantId, Long id) {
        HotWorkWorkflowTemplateEntity entity = requireTemplate(tenantId, id);
        return toDetail(entity, loadNodes(tenantId, id));
    }

    @Override
    public List<HotWorkWorkflowSummaryVO> listAvailable(HotWorkWorkflowQueryDTO query) {
        requireTenant(query.getTenantId());
        String status = StringUtils.hasText(query.getStatus()) ? query.getStatus() : HotWorkWorkflowStatus.PUBLISHED;
        LambdaQueryWrapper<HotWorkWorkflowTemplateEntity> wrapper = new LambdaQueryWrapper<HotWorkWorkflowTemplateEntity>()
                .eq(HotWorkWorkflowTemplateEntity::getTenantId, query.getTenantId())
                .eq(HotWorkWorkflowTemplateEntity::getDeleted, 0)
                .eq(HotWorkWorkflowTemplateEntity::getStatus, status);
        if (StringUtils.hasText(query.getHotWorkLevel())) {
            wrapper.and(w -> w.eq(HotWorkWorkflowTemplateEntity::getHotWorkLevel, query.getHotWorkLevel())
                    .or().eq(HotWorkWorkflowTemplateEntity::getHotWorkLevel, "ALL"));
        }
        List<HotWorkWorkflowTemplateEntity> templates = templateMapper.selectList(wrapper);
        List<HotWorkWorkflowSummaryVO> result = new ArrayList<HotWorkWorkflowSummaryVO>();
        for (HotWorkWorkflowTemplateEntity entity : templates) {
            if (matchesArea(entity, query.getAreaId())) {
                result.add(toSummary(entity));
            }
        }
        return result;
    }

    @Override
    public HotWorkWorkflowSnapshotDTO getSnapshot(Long tenantId, Long templateId, Integer versionNo) {
        HotWorkWorkflowTemplateEntity entity = requireTemplate(tenantId, templateId);
        if (versionNo != null && !versionNo.equals(entity.getVersionNo())) {
            throw new BusinessException(404, "template version not found");
        }
        if (!HotWorkWorkflowStatus.PUBLISHED.equals(entity.getStatus())
                && !HotWorkWorkflowStatus.DISABLED.equals(entity.getStatus())) {
            throw new BusinessException(409, "template is not published");
        }
        if (!StringUtils.hasText(entity.getSnapshotJson())) {
            throw new BusinessException(409, "template snapshot missing, publish first");
        }
        try {
            return objectMapper.readValue(entity.getSnapshotJson(), HotWorkWorkflowSnapshotDTO.class);
        } catch (Exception ex) {
            log.error("parse snapshot failed templateId={}", templateId, ex);
            throw new BusinessException(500, "invalid template snapshot");
        }
    }

    @Override
    public HotWorkApproverResolveResultVO resolveApprovers(HotWorkApproverResolveDTO request) {
        return approverResolver.resolve(request);
    }

    @Override
    @Transactional
    public void publish(Long tenantId, Long id, String operator) {
        HotWorkWorkflowTemplateEntity entity = requireTemplate(tenantId, id);
        List<HotWorkWorkflowNodeEntity> nodes = loadNodes(tenantId, id);
        if (nodes.isEmpty()) {
            throw new BusinessException(409, "workflow nodes required before publish");
        }
        HotWorkWorkflowSnapshotDTO snapshot = buildSnapshot(entity, nodes);
        try {
            entity.setSnapshotJson(objectMapper.writeValueAsString(snapshot));
        } catch (Exception ex) {
            throw new BusinessException(500, "build snapshot failed");
        }
        entity.setStatus(HotWorkWorkflowStatus.PUBLISHED);
        if (entity.getVersionNo() == null) {
            entity.setVersionNo(1);
        }
        entity.setUpdatedBy(operator);
        templateMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void disable(Long tenantId, Long id, String operator) {
        HotWorkWorkflowTemplateEntity entity = requireTemplate(tenantId, id);
        entity.setStatus(HotWorkWorkflowStatus.DISABLED);
        entity.setUpdatedBy(operator);
        templateMapper.updateById(entity);
    }

    private void validateTemplateRequest(HotWorkWorkflowTemplateRequest request) {
        requireTenant(request.getTenantId());
        if (CollectionUtils.isEmpty(request.getNodes())) {
            throw new BusinessException(400, "nodes are required");
        }
        for (HotWorkWorkflowNodeRequest node : request.getNodes()) {
            if (!HotWorkSignMode.isValid(node.getSignMode())) {
                throw new BusinessException(400, "invalid signMode: " + node.getSignMode());
            }
        }
    }

    private void replaceNodes(Long tenantId, Long templateId, List<HotWorkWorkflowNodeRequest> nodes) {
        LambdaQueryWrapper<HotWorkWorkflowNodeEntity> deleteWrapper = new LambdaQueryWrapper<HotWorkWorkflowNodeEntity>()
                .eq(HotWorkWorkflowNodeEntity::getTenantId, tenantId)
                .eq(HotWorkWorkflowNodeEntity::getTemplateId, templateId);
        List<HotWorkWorkflowNodeEntity> existing = nodeMapper.selectList(deleteWrapper);
        for (HotWorkWorkflowNodeEntity item : existing) {
            item.setDeleted(1);
            nodeMapper.updateById(item);
        }
        Collections.sort(nodes, new Comparator<HotWorkWorkflowNodeRequest>() {
            @Override
            public int compare(HotWorkWorkflowNodeRequest a, HotWorkWorkflowNodeRequest b) {
                return a.getNodeSeq().compareTo(b.getNodeSeq());
            }
        });
        Date now = new Date();
        for (HotWorkWorkflowNodeRequest node : nodes) {
            HotWorkWorkflowNodeEntity entity = new HotWorkWorkflowNodeEntity();
            entity.setTenantId(tenantId);
            entity.setTemplateId(templateId);
            entity.setNodeSeq(node.getNodeSeq());
            entity.setNodeName(node.getNodeName().trim());
            entity.setSignMode(node.getSignMode());
            entity.setApproverRuleType(node.getApproverRuleType());
            entity.setApproverRuleValue(node.getApproverRuleValue());
            entity.setTimeoutHours(node.getTimeoutHours() == null ? 0 : node.getTimeoutHours());
            entity.setRequired(Boolean.FALSE.equals(node.getRequired()) ? 0 : 1);
            entity.setDeleted(0);
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            nodeMapper.insert(entity);
        }
    }

    private List<HotWorkWorkflowNodeEntity> loadNodes(Long tenantId, Long templateId) {
        LambdaQueryWrapper<HotWorkWorkflowNodeEntity> wrapper = new LambdaQueryWrapper<HotWorkWorkflowNodeEntity>()
                .eq(HotWorkWorkflowNodeEntity::getTenantId, tenantId)
                .eq(HotWorkWorkflowNodeEntity::getTemplateId, templateId)
                .eq(HotWorkWorkflowNodeEntity::getDeleted, 0)
                .orderByAsc(HotWorkWorkflowNodeEntity::getNodeSeq);
        return nodeMapper.selectList(wrapper);
    }

    private HotWorkWorkflowSnapshotDTO buildSnapshot(HotWorkWorkflowTemplateEntity entity,
                                                     List<HotWorkWorkflowNodeEntity> nodes) {
        HotWorkWorkflowSnapshotDTO snapshot = new HotWorkWorkflowSnapshotDTO();
        snapshot.setTemplateId(entity.getId());
        snapshot.setTemplateCode(entity.getTemplateCode());
        snapshot.setTemplateName(entity.getTemplateName());
        snapshot.setVersionNo(entity.getVersionNo());
        snapshot.setHotWorkLevel(entity.getHotWorkLevel());
        snapshot.setAreaScopeType(entity.getAreaScopeType());
        snapshot.setAreaIds(readAreaIds(entity.getAreaIdsJson()));
        for (HotWorkWorkflowNodeEntity node : nodes) {
            HotWorkWorkflowNodeSnapshotDTO item = new HotWorkWorkflowNodeSnapshotDTO();
            item.setNodeSeq(node.getNodeSeq());
            item.setNodeName(node.getNodeName());
            item.setSignMode(node.getSignMode());
            item.setApproverRuleType(node.getApproverRuleType());
            item.setApproverRuleValue(node.getApproverRuleValue());
            item.setTimeoutHours(node.getTimeoutHours());
            item.setRequired(node.getRequired() != null && node.getRequired() == 1);
            snapshot.getNodes().add(item);
        }
        return snapshot;
    }

    private HotWorkWorkflowDetailVO toDetail(HotWorkWorkflowTemplateEntity entity,
                                             List<HotWorkWorkflowNodeEntity> nodes) {
        HotWorkWorkflowDetailVO vo = new HotWorkWorkflowDetailVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setTemplateCode(entity.getTemplateCode());
        vo.setTemplateName(entity.getTemplateName());
        vo.setHotWorkLevel(entity.getHotWorkLevel());
        vo.setAreaScopeType(entity.getAreaScopeType());
        vo.setAreaIds(readAreaIds(entity.getAreaIdsJson()));
        vo.setVersionNo(entity.getVersionNo());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        for (HotWorkWorkflowNodeEntity node : nodes) {
            HotWorkWorkflowNodeSnapshotDTO item = new HotWorkWorkflowNodeSnapshotDTO();
            item.setNodeSeq(node.getNodeSeq());
            item.setNodeName(node.getNodeName());
            item.setSignMode(node.getSignMode());
            item.setApproverRuleType(node.getApproverRuleType());
            item.setApproverRuleValue(node.getApproverRuleValue());
            item.setTimeoutHours(node.getTimeoutHours());
            item.setRequired(node.getRequired() != null && node.getRequired() == 1);
            vo.getNodes().add(item);
        }
        return vo;
    }

    private HotWorkWorkflowSummaryVO toSummary(HotWorkWorkflowTemplateEntity entity) {
        HotWorkWorkflowSummaryVO vo = new HotWorkWorkflowSummaryVO();
        vo.setId(entity.getId());
        vo.setTemplateCode(entity.getTemplateCode());
        vo.setTemplateName(entity.getTemplateName());
        vo.setHotWorkLevel(entity.getHotWorkLevel());
        vo.setAreaScopeType(entity.getAreaScopeType());
        vo.setVersionNo(entity.getVersionNo());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        return vo;
    }

    private boolean matchesArea(HotWorkWorkflowTemplateEntity entity, Long areaId) {
        if (!"SPECIFIED".equals(entity.getAreaScopeType()) || areaId == null) {
            return true;
        }
        return readAreaIds(entity.getAreaIdsJson()).contains(areaId);
    }

    private List<Long> readAreaIds(String json) {
        if (!StringUtils.hasText(json)) {
            return new ArrayList<Long>();
        }
        try {
            return objectMapper.readValue(json, LONG_LIST);
        } catch (Exception ex) {
            return new ArrayList<Long>();
        }
    }

    private String writeAreaIds(List<Long> areaIds) {
        if (CollectionUtils.isEmpty(areaIds)) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(areaIds);
        } catch (Exception ex) {
            throw new BusinessException(400, "invalid areaIds");
        }
    }

    private HotWorkWorkflowTemplateEntity requireTemplate(Long tenantId, Long id) {
        requireTenant(tenantId);
        HotWorkWorkflowTemplateEntity entity = templateMapper.selectById(id);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() != 0
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "workflow template not found");
        }
        return entity;
    }

    private void requireTenant(Long tenantId) {
        if (tenantId == null) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    private String normalizeScope(String scope) {
        return StringUtils.hasText(scope) ? scope.trim() : "ALL";
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
