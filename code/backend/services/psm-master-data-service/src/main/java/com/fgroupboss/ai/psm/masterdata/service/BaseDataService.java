package com.fgroupboss.ai.psm.masterdata.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.masterdata.config.BaseDataType;
import com.fgroupboss.ai.psm.masterdata.model.BaseDataRecord;
import com.fgroupboss.ai.psm.masterdata.model.BaseDataRequest;
import com.fgroupboss.ai.psm.masterdata.repository.BaseDataRepository;
import com.fgroupboss.ai.psm.masterdata.repository.MasterDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class BaseDataService {

    private final BaseDataRepository repository;
    private final MasterDataRepository auditRepository;
    private final ObjectMapper objectMapper;

    public BaseDataService(BaseDataRepository repository, MasterDataRepository auditRepository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.auditRepository = auditRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public BaseDataRecord create(BaseDataType type, BaseDataRequest request, String operator) {
        validateCreate(type, request);
        ensureCodeAvailable(type, request.getTenantId(), request.getCode());
        BaseDataRecord record = toRecord(request);
        Long id = repository.insert(type, record);
        BaseDataRecord saved = get(type, request.getTenantId(), id);
        writeAudit(request.getTenantId(), operator, "CREATE", type, id, null, saved);
        return saved;
    }

    @Transactional
    public BaseDataRecord update(BaseDataType type, Long id, BaseDataRequest request, String operator) {
        validateTenant(request.getTenantId());
        BaseDataRecord before = get(type, request.getTenantId(), id);
        validateReferences(type, request.getTenantId(), id, request);
        BaseDataRecord record = toRecord(request);
        record.setId(id);
        record.setCode(before.getCode());
        int updated = repository.update(type, record);
        if (updated == 0) {
            throw new BusinessException(404, "base data not found");
        }
        BaseDataRecord after = get(type, request.getTenantId(), id);
        writeAudit(request.getTenantId(), operator, "UPDATE", type, id, before, after);
        return after;
    }

    @Transactional
    public void enable(BaseDataType type, Long tenantId, Long id, String operator) {
        changeStatus(type, tenantId, id, "ENABLED", "ENABLE", operator);
    }

    @Transactional
    public void disable(BaseDataType type, Long tenantId, Long id, String operator) {
        changeStatus(type, tenantId, id, "DISABLED", "DISABLE", operator);
    }

    @Transactional
    public void delete(BaseDataType type, Long tenantId, Long id, String operator) {
        BaseDataRecord before = get(type, tenantId, id);
        int updated = repository.delete(type, tenantId, id);
        if (updated == 0) {
            throw new BusinessException(404, "base data not found");
        }
        writeAudit(tenantId, operator, "DELETE", type, id, before, null);
    }

    public BaseDataRecord get(BaseDataType type, Long tenantId, Long id) {
        validateTenant(tenantId);
        BaseDataRecord record = repository.findById(type, tenantId, id);
        if (record == null) {
            throw new BusinessException(404, "base data not found");
        }
        return record;
    }

    public PageResult<BaseDataRecord> page(BaseDataType type, Long tenantId, String keyword, String status, int pageNo, int pageSize) {
        validateTenant(tenantId);
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (normalizedPageNo - 1) * normalizedPageSize;
        long total = repository.count(type, tenantId, keyword, status);
        List<BaseDataRecord> records = repository.list(type, tenantId, keyword, status, normalizedPageSize, offset);
        return new PageResult<BaseDataRecord>(total, normalizedPageNo, normalizedPageSize, records);
    }

    public List<BaseDataRecord> tree(BaseDataType type, Long tenantId) {
        validateTenant(tenantId);
        return repository.tree(type, tenantId);
    }

    private void changeStatus(BaseDataType type, Long tenantId, Long id, String status, String action, String operator) {
        BaseDataRecord before = get(type, tenantId, id);
        int updated = repository.setStatus(type, tenantId, id, status);
        if (updated == 0) {
            throw new BusinessException(404, "base data not found");
        }
        BaseDataRecord after = get(type, tenantId, id);
        writeAudit(tenantId, operator, action, type, id, before, after);
    }

    private void validateCreate(BaseDataType type, BaseDataRequest request) {
        validateTenant(request.getTenantId());
        if (!StringUtils.hasText(request.getCode())) {
            throw new BusinessException(400, "code is required");
        }
        if (!StringUtils.hasText(request.getName())) {
            throw new BusinessException(400, "name is required");
        }
        validateReferences(type, request.getTenantId(), null, request);
    }

    private void ensureCodeAvailable(BaseDataType type, Long tenantId, String code) {
        BaseDataRecord existed = repository.findByCode(type, tenantId, code);
        if (existed != null) {
            throw new BusinessException(409, "code already exists in tenant: " + code);
        }
    }

    private void validateReferences(BaseDataType type, Long tenantId, Long currentId, BaseDataRequest request) {
        if (type == BaseDataType.AREA && request.getParentId() != null) {
            if (request.getParentId().equals(currentId)) {
                throw new BusinessException(400, "parentId cannot be self");
            }
            requireExists(BaseDataType.AREA, tenantId, request.getParentId(), "parent area");
        }
        if ((type == BaseDataType.UNIT || type == BaseDataType.EQUIPMENT || type == BaseDataType.MONITOR_POINT)
                && request.getAreaId() != null) {
            requireEnabled(BaseDataType.AREA, tenantId, request.getAreaId(), "area");
        }
        if (type == BaseDataType.UNIT && request.getAreaId() == null) {
            throw new BusinessException(400, "areaId is required");
        }
        if (type == BaseDataType.EQUIPMENT) {
            if (request.getAreaId() == null) {
                throw new BusinessException(400, "areaId is required");
            }
            if (request.getUnitId() != null) {
                requireEnabled(BaseDataType.UNIT, tenantId, request.getUnitId(), "unit");
            }
        }
        if (type == BaseDataType.MONITOR_POINT) {
            if (request.getAreaId() == null) {
                throw new BusinessException(400, "areaId is required");
            }
            if (request.getEquipmentId() != null) {
                requireEnabled(BaseDataType.EQUIPMENT, tenantId, request.getEquipmentId(), "equipment");
            }
        }
    }

    private void requireExists(BaseDataType type, Long tenantId, Long id, String name) {
        if (repository.findById(type, tenantId, id) == null) {
            throw new BusinessException(400, name + " not found");
        }
    }

    private void requireEnabled(BaseDataType type, Long tenantId, Long id, String name) {
        if (repository.findEnabledById(type, tenantId, id) == null) {
            throw new BusinessException(400, name + " not found or disabled");
        }
    }

    private BaseDataRecord toRecord(BaseDataRequest request) {
        BaseDataRecord record = new BaseDataRecord();
        record.setTenantId(request.getTenantId());
        record.setCode(request.getCode());
        record.setName(request.getName());
        record.setParentId(request.getParentId());
        record.setAreaId(request.getAreaId());
        record.setUnitId(request.getUnitId());
        record.setEquipmentId(request.getEquipmentId());
        record.setType(request.getType());
        record.setSiteId(request.getSiteId());
        record.setRiskLevel(request.getRiskLevel());
        record.setMajorHazardFlag(request.getMajorHazardFlag());
        record.setSortNo(request.getSortNo());
        record.setRunningStatus(request.getRunningStatus());
        record.setSourceSystem(request.getSourceSystem());
        record.setSourceTag(request.getSourceTag());
        record.setMetricType(request.getMetricType());
        record.setUnit(request.getUnit());
        record.setHighHigh(request.getHighHigh());
        record.setHigh(request.getHigh());
        record.setLow(request.getLow());
        record.setLowLow(request.getLowLow());
        record.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : "ENABLED");
        return record;
    }

    private void validateTenant(Long tenantId) {
        if (tenantId == null || tenantId.longValue() <= 0L) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    private void writeAudit(Long tenantId, String operator, String action, BaseDataType type, Long id,
                            BaseDataRecord before, BaseDataRecord after) {
        auditRepository.insertAudit(tenantId, operator, action, type.getBizType(), id, toJson(before), toJson(after));
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return "{}";
        }
    }
}
