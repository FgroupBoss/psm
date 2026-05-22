package com.fgroupboss.ai.psm.masterdata.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.masterdata.config.MasterDataType;
import com.fgroupboss.ai.psm.masterdata.model.MasterDataRecord;
import com.fgroupboss.ai.psm.masterdata.model.MasterDataRequest;
import com.fgroupboss.ai.psm.masterdata.repository.MasterDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MasterDataService {

    private final MasterDataRepository repository;
    private final ObjectMapper objectMapper;

    public MasterDataService(MasterDataRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public MasterDataRecord create(MasterDataType type, MasterDataRequest request, String operator) {
        validateCreate(request);
        MasterDataRecord existed = repository.findByCode(request.getTenantId(), type.getCategory(), request.getCode());
        if (existed != null) {
            throw new BusinessException(409, "code already exists in tenant: " + request.getCode());
        }
        MasterDataRecord record = toRecord(type, request);
        Long id = repository.insert(record);
        MasterDataRecord saved = repository.findById(request.getTenantId(), type.getCategory(), id);
        repository.insertAudit(request.getTenantId(), operator, "CREATE", type.getCategory(), id, null, toJson(saved));
        return saved;
    }

    @Transactional
    public MasterDataRecord update(MasterDataType type, Long id, MasterDataRequest request, String operator) {
        validateTenant(request.getTenantId());
        MasterDataRecord before = get(type, request.getTenantId(), id);
        MasterDataRecord record = toRecord(type, request);
        record.setId(id);
        record.setCode(before.getCode());
        int updated = repository.update(record);
        if (updated == 0) {
            throw new BusinessException(404, "master data not found");
        }
        MasterDataRecord after = get(type, request.getTenantId(), id);
        repository.insertAudit(request.getTenantId(), operator, "UPDATE", type.getCategory(), id, toJson(before), toJson(after));
        return after;
    }

    @Transactional
    public void disable(MasterDataType type, Long tenantId, Long id, String operator) {
        MasterDataRecord before = get(type, tenantId, id);
        int updated = repository.disable(tenantId, type.getCategory(), id);
        if (updated == 0) {
            throw new BusinessException(404, "master data not found");
        }
        MasterDataRecord after = get(type, tenantId, id);
        repository.insertAudit(tenantId, operator, "DISABLE", type.getCategory(), id, toJson(before), toJson(after));
    }

    @Transactional
    public void delete(MasterDataType type, Long tenantId, Long id, String operator) {
        MasterDataRecord before = get(type, tenantId, id);
        int updated = repository.delete(tenantId, type.getCategory(), id);
        if (updated == 0) {
            throw new BusinessException(404, "master data not found");
        }
        repository.insertAudit(tenantId, operator, "DELETE", type.getCategory(), id, toJson(before), null);
    }

    public MasterDataRecord get(MasterDataType type, Long tenantId, Long id) {
        validateTenant(tenantId);
        MasterDataRecord record = repository.findById(tenantId, type.getCategory(), id);
        if (record == null) {
            throw new BusinessException(404, "master data not found");
        }
        return record;
    }

    public PageResult<MasterDataRecord> page(MasterDataType type, Long tenantId, String keyword, int pageNo, int pageSize) {
        validateTenant(tenantId);
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (normalizedPageNo - 1) * normalizedPageSize;
        long total = repository.count(tenantId, type.getCategory(), keyword);
        List<MasterDataRecord> records = repository.list(tenantId, type.getCategory(), keyword, normalizedPageSize, offset);
        return new PageResult<MasterDataRecord>(total, normalizedPageNo, normalizedPageSize, records);
    }

    public List<MasterDataRecord> tree(MasterDataType type, Long tenantId) {
        validateTenant(tenantId);
        return repository.tree(tenantId, type.getCategory());
    }

    private MasterDataRecord toRecord(MasterDataType type, MasterDataRequest request) {
        MasterDataRecord record = new MasterDataRecord();
        record.setTenantId(request.getTenantId());
        record.setCategory(type.getCategory());
        record.setCode(request.getCode());
        record.setName(request.getName());
        record.setParentId(request.getParentId());
        record.setType(request.getType());
        record.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : "ENABLED");
        record.setAttributes(request.getAttributes());
        return record;
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

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return "{}";
        }
    }
}
