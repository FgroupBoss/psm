package com.fgroupboss.ai.psm.identity.masterdata.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.identity.masterdata.config.MasterDataType;
import com.fgroupboss.ai.psm.identity.masterdata.mapper.AuditChangeLogMapper;
import com.fgroupboss.ai.psm.identity.masterdata.mapper.MasterDataItemMapper;
import com.fgroupboss.ai.psm.identity.masterdata.model.dto.MasterDataRequest;
import com.fgroupboss.ai.psm.identity.masterdata.model.entity.AuditChangeLogEntity;
import com.fgroupboss.ai.psm.identity.masterdata.model.entity.MasterDataItemEntity;
import com.fgroupboss.ai.psm.identity.masterdata.model.vo.MasterDataRecordVO;
import com.fgroupboss.ai.psm.identity.masterdata.service.MasterDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 主数据业务服务实现。
 *
 * <p>attributes 字段在数据库中为 JSON，本实现保守地在 Service 层完成字符串和 Map 的转换。</p>
 */
@Service
@RequiredArgsConstructor
public class MasterDataServiceImpl implements MasterDataService {

    private static final TypeReference<Map<String, Object>> ATTRIBUTES_TYPE = new TypeReference<Map<String, Object>>() {
    };

    private final MasterDataItemMapper masterDataItemMapper;
    private final AuditChangeLogMapper auditChangeLogMapper;
    private final ObjectMapper objectMapper;

    /**
     * 实现方式：创建业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public MasterDataRecordVO create(MasterDataType type, MasterDataRequest request, String operator) {
        validateCreate(request);
        MasterDataItemEntity existed = masterDataItemMapper.findByCode(request.getTenantId(), type.getCategory(), request.getCode());
        if (existed != null) {
            throw new BusinessException(409, "code already exists in tenant: " + request.getCode());
        }
        MasterDataItemEntity entity = toEntity(type, request);
        masterDataItemMapper.insert(entity);
        MasterDataRecordVO saved = get(type, request.getTenantId(), entity.getId());
        writeAudit(request.getTenantId(), operator, "CREATE", type.getCategory(), entity.getId(), null, saved);
        return saved;
    }

    /**
     * 实现方式：更新业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public MasterDataRecordVO update(MasterDataType type, Long id, MasterDataRequest request, String operator) {
        validateTenant(request.getTenantId());
        MasterDataRecordVO before = get(type, request.getTenantId(), id);
        MasterDataItemEntity entity = toEntity(type, request);
        entity.setId(id);
        entity.setItemCode(before.getCode());
        int updated = masterDataItemMapper.updateItem(entity);
        if (updated == 0) {
            throw new BusinessException(404, "master data not found");
        }
        MasterDataRecordVO after = get(type, request.getTenantId(), id);
        writeAudit(request.getTenantId(), operator, "UPDATE", type.getCategory(), id, before, after);
        return after;
    }

    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void disable(MasterDataType type, Long tenantId, Long id, String operator) {
        MasterDataRecordVO before = get(type, tenantId, id);
        int updated = masterDataItemMapper.setStatusDisabled(tenantId, type.getCategory(), id);
        if (updated == 0) {
            throw new BusinessException(404, "master data not found");
        }
        MasterDataRecordVO after = get(type, tenantId, id);
        writeAudit(tenantId, operator, "DISABLE", type.getCategory(), id, before, after);
    }

    /**
     * 实现方式：删除业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void delete(MasterDataType type, Long tenantId, Long id, String operator) {
        MasterDataRecordVO before = get(type, tenantId, id);
        int updated = masterDataItemMapper.softDelete(tenantId, type.getCategory(), id);
        if (updated == 0) {
            throw new BusinessException(404, "master data not found");
        }
        writeAudit(tenantId, operator, "DELETE", type.getCategory(), id, before, null);
    }

    /**
     * 实现方式：查询详情，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public MasterDataRecordVO get(MasterDataType type, Long tenantId, Long id) {
        validateTenant(tenantId);
        MasterDataItemEntity entity = masterDataItemMapper.findById(tenantId, type.getCategory(), id);
        if (entity == null) {
            throw new BusinessException(404, "master data not found");
        }
        return toVO(entity);
    }

    /**
     * 实现方式：分页查询业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public PageResult<MasterDataRecordVO> page(MasterDataType type, Long tenantId, String keyword, int pageNo, int pageSize) {
        validateTenant(tenantId);
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (normalizedPageNo - 1) * normalizedPageSize;
        String normalizedKeyword = normalizeText(keyword);
        long total = masterDataItemMapper.count(tenantId, type.getCategory(), normalizedKeyword);
        List<MasterDataItemEntity> entities = masterDataItemMapper.list(tenantId, type.getCategory(), normalizedKeyword,
                normalizedPageSize, offset);
        return new PageResult<MasterDataRecordVO>(total, normalizedPageNo, normalizedPageSize, toVOList(entities));
    }

    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<MasterDataRecordVO> tree(MasterDataType type, Long tenantId) {
        validateTenant(tenantId);
        return toVOList(masterDataItemMapper.tree(tenantId, type.getCategory()));
    }

    private MasterDataItemEntity toEntity(MasterDataType type, MasterDataRequest request) {
        MasterDataItemEntity entity = new MasterDataItemEntity();
        entity.setTenantId(request.getTenantId());
        entity.setCategory(type.getCategory());
        entity.setItemCode(request.getCode());
        entity.setItemName(request.getName());
        entity.setParentId(request.getParentId());
        entity.setItemType(request.getType());
        entity.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : "ENABLED");
        entity.setAttributes(toJson(request.getAttributes() == null ? Collections.emptyMap() : request.getAttributes()));
        return entity;
    }

    private MasterDataRecordVO toVO(MasterDataItemEntity entity) {
        MasterDataRecordVO vo = new MasterDataRecordVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setCategory(entity.getCategory());
        vo.setCode(entity.getItemCode());
        vo.setName(entity.getItemName());
        vo.setParentId(entity.getParentId());
        vo.setType(entity.getItemType());
        vo.setStatus(entity.getStatus());
        vo.setAttributes(fromJson(entity.getAttributes()));
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private List<MasterDataRecordVO> toVOList(List<MasterDataItemEntity> entities) {
        List<MasterDataRecordVO> records = new ArrayList<MasterDataRecordVO>();
        for (MasterDataItemEntity entity : entities) {
            records.add(toVO(entity));
        }
        return records;
    }

    private void validateCreate(MasterDataRequest request) {
        validateTenant(request.getTenantId());
        if (!StringUtils.hasText(request.getCode())) {
            throw new BusinessException(400, "code is required");
        }
        if (!StringUtils.hasText(request.getName())) {
            throw new BusinessException(400, "name is required");
        }
    }

    private void validateTenant(Long tenantId) {
        if (tenantId == null || tenantId.longValue() <= 0L) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    private void writeAudit(Long tenantId, String operator, String action, String bizType, Long bizId,
                            Object beforeValue, Object afterValue) {
        AuditChangeLogEntity entity = new AuditChangeLogEntity();
        entity.setTenantId(tenantId);
        entity.setOperatorName(operator);
        entity.setAction(action);
        entity.setBizType(bizType);
        entity.setBizId(bizId);
        entity.setBeforeValue(toJsonOrNull(beforeValue));
        entity.setAfterValue(toJsonOrNull(afterValue));
        entity.setResult("SUCCESS");
        entity.setOperatedAt(LocalDateTime.now());
        auditChangeLogMapper.insert(entity);
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("attributes must be JSON serializable", e);
        }
    }

    private String toJsonOrNull(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return "{}";
        }
    }

    private Map<String, Object> fromJson(String value) {
        try {
            if (!StringUtils.hasText(value)) {
                return Collections.emptyMap();
            }
            return objectMapper.readValue(value, ATTRIBUTES_TYPE);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
