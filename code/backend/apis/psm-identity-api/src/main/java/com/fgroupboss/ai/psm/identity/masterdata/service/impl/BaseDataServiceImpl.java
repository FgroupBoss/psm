package com.fgroupboss.ai.psm.identity.masterdata.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.identity.masterdata.config.BaseDataType;
import com.fgroupboss.ai.psm.identity.masterdata.mapper.MdAuditChangeLogMapper;
import com.fgroupboss.ai.psm.identity.masterdata.mapper.BaseAreaMapper;
import com.fgroupboss.ai.psm.identity.masterdata.mapper.BaseEquipmentMapper;
import com.fgroupboss.ai.psm.identity.masterdata.mapper.BaseUnitMapper;
import com.fgroupboss.ai.psm.identity.masterdata.mapper.MonitorPointMapper;
import com.fgroupboss.ai.psm.identity.masterdata.model.dto.BaseDataRequest;
import com.fgroupboss.ai.psm.identity.masterdata.model.entity.AuditChangeLogEntity;
import com.fgroupboss.ai.psm.identity.masterdata.model.entity.BaseAreaEntity;
import com.fgroupboss.ai.psm.identity.masterdata.model.entity.BaseEquipmentEntity;
import com.fgroupboss.ai.psm.identity.masterdata.model.entity.BaseUnitEntity;
import com.fgroupboss.ai.psm.identity.masterdata.model.entity.MonitorPointEntity;
import com.fgroupboss.ai.psm.identity.masterdata.model.vo.BaseDataRecordVO;
import com.fgroupboss.ai.psm.identity.masterdata.service.BaseDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础台账业务服务实现。
 *
 * <p>按台账类型路由到固定 Mapper，避免旧实现中动态拼接表名和字段名带来的 SQL 风险。</p>
 */
@Service
@RequiredArgsConstructor
public class BaseDataServiceImpl implements BaseDataService {

    private final BaseAreaMapper baseAreaMapper;
    private final BaseUnitMapper baseUnitMapper;
    private final BaseEquipmentMapper baseEquipmentMapper;
    private final MonitorPointMapper monitorPointMapper;
    private final MdAuditChangeLogMapper auditChangeLogMapper;
    private final ObjectMapper objectMapper;

    /**
     * 实现方式：创建业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public BaseDataRecordVO create(BaseDataType type, BaseDataRequest request, String operator) {
        validateCreate(type, request);
        ensureCodeAvailable(type, request.getTenantId(), request.getCode());
        Long id = insert(type, request);
        BaseDataRecordVO saved = get(type, request.getTenantId(), id);
        writeAudit(request.getTenantId(), operator, "CREATE", type, id, null, saved);
        return saved;
    }

    /**
     * 实现方式：更新业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public BaseDataRecordVO update(BaseDataType type, Long id, BaseDataRequest request, String operator) {
        validateTenant(request.getTenantId());
        BaseDataRecordVO before = get(type, request.getTenantId(), id);
        validateReferences(type, request.getTenantId(), id, request);
        int updated = updateRecord(type, id, before.getCode(), request);
        if (updated == 0) {
            throw new BusinessException(404, "base data not found");
        }
        BaseDataRecordVO after = get(type, request.getTenantId(), id);
        writeAudit(request.getTenantId(), operator, "UPDATE", type, id, before, after);
        return after;
    }

    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void enable(BaseDataType type, Long tenantId, Long id, String operator) {
        changeStatus(type, tenantId, id, "ENABLED", "ENABLE", operator);
    }

    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void disable(BaseDataType type, Long tenantId, Long id, String operator) {
        changeStatus(type, tenantId, id, "DISABLED", "DISABLE", operator);
    }

    /**
     * 实现方式：删除业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void delete(BaseDataType type, Long tenantId, Long id, String operator) {
        BaseDataRecordVO before = get(type, tenantId, id);
        int updated = softDelete(type, tenantId, id);
        if (updated == 0) {
            throw new BusinessException(404, "base data not found");
        }
        writeAudit(tenantId, operator, "DELETE", type, id, before, null);
    }

    /**
     * 实现方式：查询详情，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public BaseDataRecordVO get(BaseDataType type, Long tenantId, Long id) {
        validateTenant(tenantId);
        BaseDataRecordVO record = findById(type, tenantId, id);
        if (record == null) {
            throw new BusinessException(404, "base data not found");
        }
        return record;
    }

    /**
     * 实现方式：分页查询业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public PageResult<BaseDataRecordVO> page(BaseDataType type, Long tenantId, String keyword, String status, int pageNo, int pageSize) {
        validateTenant(tenantId);
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);
        int offset = (normalizedPageNo - 1) * normalizedPageSize;
        String normalizedKeyword = normalizeText(keyword);
        String normalizedStatus = normalizeText(status);
        long total = count(type, tenantId, normalizedKeyword, normalizedStatus);
        List<BaseDataRecordVO> records = list(type, tenantId, normalizedKeyword, normalizedStatus, normalizedPageSize, offset);
        return new PageResult<BaseDataRecordVO>(total, normalizedPageNo, normalizedPageSize, records);
    }

    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<BaseDataRecordVO> tree(BaseDataType type, Long tenantId) {
        validateTenant(tenantId);
        if (type == BaseDataType.AREA) {
            return toAreaVOList(baseAreaMapper.tree(tenantId));
        }
        return list(type, tenantId, null, null, 200, 0);
    }

    private Long insert(BaseDataType type, BaseDataRequest request) {
        if (type == BaseDataType.AREA) {
            BaseAreaEntity entity = toAreaEntity(request);
            baseAreaMapper.insert(entity);
            return entity.getId();
        }
        if (type == BaseDataType.UNIT) {
            BaseUnitEntity entity = toUnitEntity(request);
            baseUnitMapper.insert(entity);
            return entity.getId();
        }
        if (type == BaseDataType.EQUIPMENT) {
            BaseEquipmentEntity entity = toEquipmentEntity(request);
            baseEquipmentMapper.insert(entity);
            return entity.getId();
        }
        MonitorPointEntity entity = toMonitorPointEntity(request);
        monitorPointMapper.insert(entity);
        return entity.getId();
    }

    private int updateRecord(BaseDataType type, Long id, String code, BaseDataRequest request) {
        if (type == BaseDataType.AREA) {
            BaseAreaEntity entity = toAreaEntity(request);
            entity.setId(id);
            entity.setAreaCode(code);
            return baseAreaMapper.updateRecord(entity);
        }
        if (type == BaseDataType.UNIT) {
            BaseUnitEntity entity = toUnitEntity(request);
            entity.setId(id);
            entity.setUnitCode(code);
            return baseUnitMapper.updateRecord(entity);
        }
        if (type == BaseDataType.EQUIPMENT) {
            BaseEquipmentEntity entity = toEquipmentEntity(request);
            entity.setId(id);
            entity.setEquipmentCode(code);
            return baseEquipmentMapper.updateRecord(entity);
        }
        MonitorPointEntity entity = toMonitorPointEntity(request);
        entity.setId(id);
        entity.setPointCode(code);
        return monitorPointMapper.updateRecord(entity);
    }

    private BaseDataRecordVO findById(BaseDataType type, Long tenantId, Long id) {
        if (type == BaseDataType.AREA) {
            return toVO(baseAreaMapper.findById(tenantId, id));
        }
        if (type == BaseDataType.UNIT) {
            return toVO(baseUnitMapper.findById(tenantId, id));
        }
        if (type == BaseDataType.EQUIPMENT) {
            return toVO(baseEquipmentMapper.findById(tenantId, id));
        }
        return toVO(monitorPointMapper.findById(tenantId, id));
    }

    private BaseDataRecordVO findEnabledById(BaseDataType type, Long tenantId, Long id) {
        if (type == BaseDataType.AREA) {
            return toVO(baseAreaMapper.findEnabledById(tenantId, id));
        }
        if (type == BaseDataType.UNIT) {
            return toVO(baseUnitMapper.findEnabledById(tenantId, id));
        }
        if (type == BaseDataType.EQUIPMENT) {
            return toVO(baseEquipmentMapper.findEnabledById(tenantId, id));
        }
        return toVO(monitorPointMapper.findEnabledById(tenantId, id));
    }

    private BaseDataRecordVO findByCode(BaseDataType type, Long tenantId, String code) {
        if (type == BaseDataType.AREA) {
            return toVO(baseAreaMapper.findByCode(tenantId, code));
        }
        if (type == BaseDataType.UNIT) {
            return toVO(baseUnitMapper.findByCode(tenantId, code));
        }
        if (type == BaseDataType.EQUIPMENT) {
            return toVO(baseEquipmentMapper.findByCode(tenantId, code));
        }
        return toVO(monitorPointMapper.findByCode(tenantId, code));
    }

    private List<BaseDataRecordVO> list(BaseDataType type, Long tenantId, String keyword, String status, int limit, int offset) {
        if (type == BaseDataType.AREA) {
            return toAreaVOList(baseAreaMapper.list(tenantId, keyword, status, limit, offset));
        }
        if (type == BaseDataType.UNIT) {
            return toUnitVOList(baseUnitMapper.list(tenantId, keyword, status, limit, offset));
        }
        if (type == BaseDataType.EQUIPMENT) {
            return toEquipmentVOList(baseEquipmentMapper.list(tenantId, keyword, status, limit, offset));
        }
        return toMonitorPointVOList(monitorPointMapper.list(tenantId, keyword, status, limit, offset));
    }

    private long count(BaseDataType type, Long tenantId, String keyword, String status) {
        if (type == BaseDataType.AREA) {
            return baseAreaMapper.count(tenantId, keyword, status);
        }
        if (type == BaseDataType.UNIT) {
            return baseUnitMapper.count(tenantId, keyword, status);
        }
        if (type == BaseDataType.EQUIPMENT) {
            return baseEquipmentMapper.count(tenantId, keyword, status);
        }
        return monitorPointMapper.count(tenantId, keyword, status);
    }

    private int setStatus(BaseDataType type, Long tenantId, Long id, String status) {
        if (type == BaseDataType.AREA) {
            return baseAreaMapper.setStatus(tenantId, id, status);
        }
        if (type == BaseDataType.UNIT) {
            return baseUnitMapper.setStatus(tenantId, id, status);
        }
        if (type == BaseDataType.EQUIPMENT) {
            return baseEquipmentMapper.setStatus(tenantId, id, status);
        }
        return monitorPointMapper.setStatus(tenantId, id, status);
    }

    private int softDelete(BaseDataType type, Long tenantId, Long id) {
        if (type == BaseDataType.AREA) {
            return baseAreaMapper.softDelete(tenantId, id);
        }
        if (type == BaseDataType.UNIT) {
            return baseUnitMapper.softDelete(tenantId, id);
        }
        if (type == BaseDataType.EQUIPMENT) {
            return baseEquipmentMapper.softDelete(tenantId, id);
        }
        return monitorPointMapper.softDelete(tenantId, id);
    }

    private void changeStatus(BaseDataType type, Long tenantId, Long id, String status, String action, String operator) {
        BaseDataRecordVO before = get(type, tenantId, id);
        int updated = setStatus(type, tenantId, id, status);
        if (updated == 0) {
            throw new BusinessException(404, "base data not found");
        }
        BaseDataRecordVO after = get(type, tenantId, id);
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
        if (findByCode(type, tenantId, code) != null) {
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
        validateEquipmentReferences(type, tenantId, request);
        validateMonitorPointReferences(type, tenantId, request);
    }

    private void validateEquipmentReferences(BaseDataType type, Long tenantId, BaseDataRequest request) {
        if (type != BaseDataType.EQUIPMENT) {
            return;
        }
        if (request.getAreaId() == null) {
            throw new BusinessException(400, "areaId is required");
        }
        if (request.getUnitId() != null) {
            requireEnabled(BaseDataType.UNIT, tenantId, request.getUnitId(), "unit");
        }
    }

    private void validateMonitorPointReferences(BaseDataType type, Long tenantId, BaseDataRequest request) {
        if (type != BaseDataType.MONITOR_POINT) {
            return;
        }
        if (request.getAreaId() == null) {
            throw new BusinessException(400, "areaId is required");
        }
        if (request.getEquipmentId() != null) {
            requireEnabled(BaseDataType.EQUIPMENT, tenantId, request.getEquipmentId(), "equipment");
        }
    }

    private void requireExists(BaseDataType type, Long tenantId, Long id, String name) {
        if (findById(type, tenantId, id) == null) {
            throw new BusinessException(400, name + " not found");
        }
    }

    private void requireEnabled(BaseDataType type, Long tenantId, Long id, String name) {
        if (findEnabledById(type, tenantId, id) == null) {
            throw new BusinessException(400, name + " not found or disabled");
        }
    }

    private void validateTenant(Long tenantId) {
        if (tenantId == null || tenantId.longValue() <= 0L) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    private BaseAreaEntity toAreaEntity(BaseDataRequest request) {
        BaseAreaEntity entity = new BaseAreaEntity();
        entity.setTenantId(request.getTenantId());
        entity.setAreaCode(request.getCode());
        entity.setAreaName(request.getName());
        entity.setParentId(request.getParentId());
        entity.setAreaType(request.getType());
        entity.setSiteId(request.getSiteId());
        entity.setRiskLevel(request.getRiskLevel());
        entity.setMajorHazardFlag(Boolean.TRUE.equals(request.getMajorHazardFlag()));
        entity.setSortNo(defaultSortNo(request.getSortNo()));
        entity.setStatus(defaultStatus(request.getStatus()));
        return entity;
    }

    private BaseUnitEntity toUnitEntity(BaseDataRequest request) {
        BaseUnitEntity entity = new BaseUnitEntity();
        entity.setTenantId(request.getTenantId());
        entity.setUnitCode(request.getCode());
        entity.setUnitName(request.getName());
        entity.setAreaId(request.getAreaId());
        entity.setUnitType(request.getType());
        entity.setSortNo(defaultSortNo(request.getSortNo()));
        entity.setStatus(defaultStatus(request.getStatus()));
        return entity;
    }

    private BaseEquipmentEntity toEquipmentEntity(BaseDataRequest request) {
        BaseEquipmentEntity entity = new BaseEquipmentEntity();
        entity.setTenantId(request.getTenantId());
        entity.setEquipmentCode(request.getCode());
        entity.setEquipmentName(request.getName());
        entity.setAreaId(request.getAreaId());
        entity.setUnitId(request.getUnitId());
        entity.setEquipmentType(request.getType());
        entity.setRunningStatus(request.getRunningStatus());
        entity.setStatus(defaultStatus(request.getStatus()));
        return entity;
    }

    private MonitorPointEntity toMonitorPointEntity(BaseDataRequest request) {
        MonitorPointEntity entity = new MonitorPointEntity();
        entity.setTenantId(request.getTenantId());
        entity.setPointCode(request.getCode());
        entity.setPointName(request.getName());
        entity.setAreaId(request.getAreaId());
        entity.setEquipmentId(request.getEquipmentId());
        entity.setSourceSystem(request.getSourceSystem());
        entity.setSourceTag(request.getSourceTag());
        entity.setMetricType(request.getMetricType());
        entity.setUnit(request.getUnit());
        entity.setHighHigh(request.getHighHigh());
        entity.setHigh(request.getHigh());
        entity.setLow(request.getLow());
        entity.setLowLow(request.getLowLow());
        entity.setStatus(defaultStatus(request.getStatus()));
        return entity;
    }

    private BaseDataRecordVO toVO(BaseAreaEntity entity) {
        if (entity == null) {
            return null;
        }
        BaseDataRecordVO vo = new BaseDataRecordVO();
        fillCommon(vo, entity.getId(), entity.getTenantId(), entity.getAreaCode(), entity.getAreaName(),
                entity.getStatus(), entity.getCreatedAt(), entity.getUpdatedAt());
        vo.setParentId(entity.getParentId());
        vo.setType(entity.getAreaType());
        vo.setSiteId(entity.getSiteId());
        vo.setRiskLevel(entity.getRiskLevel());
        vo.setMajorHazardFlag(Boolean.TRUE.equals(entity.getMajorHazardFlag()));
        vo.setSortNo(entity.getSortNo());
        return vo;
    }

    private BaseDataRecordVO toVO(BaseUnitEntity entity) {
        if (entity == null) {
            return null;
        }
        BaseDataRecordVO vo = new BaseDataRecordVO();
        fillCommon(vo, entity.getId(), entity.getTenantId(), entity.getUnitCode(), entity.getUnitName(),
                entity.getStatus(), entity.getCreatedAt(), entity.getUpdatedAt());
        vo.setAreaId(entity.getAreaId());
        vo.setType(entity.getUnitType());
        vo.setSortNo(entity.getSortNo());
        return vo;
    }

    private BaseDataRecordVO toVO(BaseEquipmentEntity entity) {
        if (entity == null) {
            return null;
        }
        BaseDataRecordVO vo = new BaseDataRecordVO();
        fillCommon(vo, entity.getId(), entity.getTenantId(), entity.getEquipmentCode(), entity.getEquipmentName(),
                entity.getStatus(), entity.getCreatedAt(), entity.getUpdatedAt());
        vo.setAreaId(entity.getAreaId());
        vo.setUnitId(entity.getUnitId());
        vo.setType(entity.getEquipmentType());
        vo.setRunningStatus(entity.getRunningStatus());
        return vo;
    }

    private BaseDataRecordVO toVO(MonitorPointEntity entity) {
        if (entity == null) {
            return null;
        }
        BaseDataRecordVO vo = new BaseDataRecordVO();
        fillCommon(vo, entity.getId(), entity.getTenantId(), entity.getPointCode(), entity.getPointName(),
                entity.getStatus(), entity.getCreatedAt(), entity.getUpdatedAt());
        vo.setAreaId(entity.getAreaId());
        vo.setEquipmentId(entity.getEquipmentId());
        vo.setSourceSystem(entity.getSourceSystem());
        vo.setSourceTag(entity.getSourceTag());
        vo.setMetricType(entity.getMetricType());
        vo.setUnit(entity.getUnit());
        vo.setHighHigh(entity.getHighHigh());
        vo.setHigh(entity.getHigh());
        vo.setLow(entity.getLow());
        vo.setLowLow(entity.getLowLow());
        return vo;
    }

    private void fillCommon(BaseDataRecordVO vo, Long id, Long tenantId, String code, String name,
                            String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        vo.setId(id);
        vo.setTenantId(tenantId);
        vo.setCode(code);
        vo.setName(name);
        vo.setStatus(status);
        vo.setCreatedAt(createdAt);
        vo.setUpdatedAt(updatedAt);
    }

    private List<BaseDataRecordVO> toAreaVOList(List<BaseAreaEntity> entities) {
        List<BaseDataRecordVO> records = new ArrayList<BaseDataRecordVO>();
        for (BaseAreaEntity entity : entities) {
            records.add(toVO(entity));
        }
        return records;
    }

    private List<BaseDataRecordVO> toUnitVOList(List<BaseUnitEntity> entities) {
        List<BaseDataRecordVO> records = new ArrayList<BaseDataRecordVO>();
        for (BaseUnitEntity entity : entities) {
            records.add(toVO(entity));
        }
        return records;
    }

    private List<BaseDataRecordVO> toEquipmentVOList(List<BaseEquipmentEntity> entities) {
        List<BaseDataRecordVO> records = new ArrayList<BaseDataRecordVO>();
        for (BaseEquipmentEntity entity : entities) {
            records.add(toVO(entity));
        }
        return records;
    }

    private List<BaseDataRecordVO> toMonitorPointVOList(List<MonitorPointEntity> entities) {
        List<BaseDataRecordVO> records = new ArrayList<BaseDataRecordVO>();
        for (MonitorPointEntity entity : entities) {
            records.add(toVO(entity));
        }
        return records;
    }

    private void writeAudit(Long tenantId, String operator, String action, BaseDataType type, Long id,
                            Object before, Object after) {
        AuditChangeLogEntity entity = new AuditChangeLogEntity();
        entity.setTenantId(tenantId);
        entity.setOperatorName(operator);
        entity.setAction(action);
        entity.setBizType(type.getBizType());
        entity.setBizId(id);
        entity.setBeforeValue(toJson(before));
        entity.setAfterValue(toJson(after));
        entity.setResult("SUCCESS");
        entity.setOperatedAt(LocalDateTime.now());
        auditChangeLogMapper.insert(entity);
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

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String defaultStatus(String status) {
        return StringUtils.hasText(status) ? status : "ENABLED";
    }

    private Integer defaultSortNo(Integer sortNo) {
        return sortNo == null ? 0 : sortNo;
    }
}
