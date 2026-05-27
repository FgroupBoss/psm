package com.fgroupboss.ai.psm.majorhazard.service.impl;

import com.fgroupboss.ai.psm.common.AuditBizType;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.audit.CentralAuditClient;
import com.fgroupboss.ai.psm.majorhazard.config.HazardStatus;
import com.fgroupboss.ai.psm.majorhazard.config.ResponsibilityType;
import com.fgroupboss.ai.psm.majorhazard.mapper.MajorHazardAuditRecordMapper;
import com.fgroupboss.ai.psm.majorhazard.mapper.MajorHazardMapper;
import com.fgroupboss.ai.psm.majorhazard.mapper.MajorHazardResponsibilityMapper;
import com.fgroupboss.ai.psm.majorhazard.mapper.MajorHazardStatusLogMapper;
import com.fgroupboss.ai.psm.majorhazard.model.dto.HazardStatusRequest;
import com.fgroupboss.ai.psm.majorhazard.model.dto.MajorHazardRequest;
import com.fgroupboss.ai.psm.majorhazard.model.dto.ResponsibilityReplaceRequest;
import com.fgroupboss.ai.psm.majorhazard.model.dto.ResponsibilityRequest;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardAuditRecordEntity;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardEntity;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardResponsibilityEntity;
import com.fgroupboss.ai.psm.majorhazard.model.entity.MajorHazardStatusLogEntity;
import com.fgroupboss.ai.psm.majorhazard.model.vo.MajorHazardResponsibilityVO;
import com.fgroupboss.ai.psm.majorhazard.model.vo.MajorHazardVO;
import com.fgroupboss.ai.psm.majorhazard.service.MajorHazardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MajorHazardServiceImpl implements MajorHazardService {

    private final MajorHazardMapper hazardMapper;
    private final MajorHazardResponsibilityMapper responsibilityMapper;
    private final MajorHazardStatusLogMapper statusLogMapper;
    private final MajorHazardAuditRecordMapper auditRecordMapper;
    private final CentralAuditClient centralAuditClient;

    @Override
    public PageResult<MajorHazardVO> page(Long tenantId, String keyword, String status, String level,
                                          int pageNo, int pageSize) {
        requireTenantId(tenantId);
        Page page = normalizePage(pageNo, pageSize);
        String normalizedKeyword = normalizeText(keyword);
        String normalizedStatus = normalizeText(status);
        String normalizedLevel = normalizeText(level);
        long total = hazardMapper.countByTenant(tenantId, normalizedKeyword, normalizedStatus, normalizedLevel);
        List<MajorHazardEntity> entities = total == 0
                ? new ArrayList<MajorHazardEntity>()
                : hazardMapper.listByTenant(tenantId, normalizedKeyword, normalizedStatus, normalizedLevel,
                page.offset, page.pageSize);
        List<MajorHazardVO> records = new ArrayList<MajorHazardVO>();
        for (MajorHazardEntity entity : entities) {
            records.add(toVO(entity));
        }
        return new PageResult<MajorHazardVO>(total, page.pageNo, page.pageSize, records);
    }

    @Override
    public MajorHazardVO getById(Long tenantId, Long id) {
        return toVO(requireHazard(tenantId, id));
    }

    @Override
    @Transactional
    public MajorHazardVO create(MajorHazardRequest request, String operator) {
        requireTenantId(request.getTenantId());
        assertHazardNoUnique(request.getTenantId(), request.getHazardNo(), null);
        MajorHazardEntity entity = new MajorHazardEntity();
        entity.setTenantId(request.getTenantId());
        applyRequest(entity, request);
        entity.setStatus(HazardStatus.DRAFT.name());
        entity.setDeleted(0);
        hazardMapper.insert(entity);
        writeAudit(request.getTenantId(), entity.getId(), AuditBizType.MAJOR_HAZARD.name(),
                "CREATE", null, entity.getStatus(), null, operator);
        return toVO(entity);
    }

    @Override
    @Transactional
    public MajorHazardVO update(Long id, MajorHazardRequest request, String operator) {
        MajorHazardEntity entity = requireHazard(request.getTenantId(), id);
        HazardStatus.assertEditable(entity.getStatus());
        assertHazardNoUnique(request.getTenantId(), request.getHazardNo(), id);
        String before = entity.getStatus();
        applyRequest(entity, request);
        hazardMapper.updateById(entity);
        writeAudit(request.getTenantId(), id, AuditBizType.MAJOR_HAZARD.name(),
                "UPDATE", before, entity.getStatus(), null, operator);
        return toVO(entity);
    }

    @Override
    @Transactional
    public MajorHazardVO publish(Long tenantId, Long id, String operator) {
        MajorHazardEntity entity = requireHazard(tenantId, id);
        String before = entity.getStatus();
        HazardStatus.assertPublish(before);
        assertPublishReady(entity);
        String after = HazardStatus.PUBLISHED.name();
        entity.setStatus(after);
        entity.setPublishedAt(LocalDateTime.now());
        hazardMapper.updateById(entity);
        writeStatusLog(tenantId, id, before, after, "发布", operator);
        writeAudit(tenantId, id, AuditBizType.MAJOR_HAZARD.name(),
                "PUBLISH", before, after, null, operator);
        return toVO(entity);
    }

    @Override
    @Transactional
    public MajorHazardVO changeStatus(Long tenantId, Long id, HazardStatusRequest request, String operator) {
        MajorHazardEntity entity = requireHazard(tenantId, id);
        String before = entity.getStatus();
        String after = request.getTargetStatus().trim();
        HazardStatus.assertStatusChange(before, after);
        entity.setStatus(after);
        hazardMapper.updateById(entity);
        writeStatusLog(tenantId, id, before, after, request.getReason(), operator);
        writeAudit(tenantId, id, AuditBizType.MAJOR_HAZARD.name(),
                "STATUS_CHANGE", before, after, request.getReason(), operator);
        return toVO(entity);
    }

    @Override
    public List<MajorHazardResponsibilityVO> listResponsibilities(Long tenantId, Long hazardId) {
        requireHazard(tenantId, hazardId);
        List<MajorHazardResponsibilityEntity> entities = responsibilityMapper.listByHazardId(tenantId, hazardId);
        List<MajorHazardResponsibilityVO> records = new ArrayList<MajorHazardResponsibilityVO>();
        for (MajorHazardResponsibilityEntity entity : entities) {
            records.add(toResponsibilityVO(entity));
        }
        return records;
    }

    @Override
    @Transactional
    public List<MajorHazardResponsibilityVO> replaceResponsibilities(Long tenantId, Long hazardId,
                                                                     ResponsibilityReplaceRequest request,
                                                                     String operator) {
        requireHazard(tenantId, hazardId);
        validateResponsibilityPayload(request);
        for (ResponsibilityRequest item : request.getResponsibilities()) {
            upsertResponsibility(tenantId, hazardId, item);
        }
        writeAudit(tenantId, hazardId, AuditBizType.MAJOR_HAZARD_RESPONSIBILITY.name(),
                "REPLACE", null, null, "更新包保责任人", operator);
        return listResponsibilities(tenantId, hazardId);
    }

    private void upsertResponsibility(Long tenantId, Long hazardId, ResponsibilityRequest item) {
        ResponsibilityType.assertValid(item.getResponsibilityType());
        MajorHazardResponsibilityEntity existing = responsibilityMapper.findByType(
                tenantId, hazardId, item.getResponsibilityType().trim());
        if (existing == null) {
            MajorHazardResponsibilityEntity entity = new MajorHazardResponsibilityEntity();
            entity.setTenantId(tenantId);
            entity.setHazardId(hazardId);
            applyResponsibility(entity, item);
            responsibilityMapper.insert(entity);
            return;
        }
        applyResponsibility(existing, item);
        responsibilityMapper.updateById(existing);
    }

    private void applyResponsibility(MajorHazardResponsibilityEntity entity, ResponsibilityRequest item) {
        entity.setResponsibilityType(item.getResponsibilityType().trim());
        entity.setPersonName(item.getPersonName().trim());
        entity.setPersonPhone(normalizeText(item.getPersonPhone()));
        entity.setPersonId(item.getPersonId());
        entity.setSortNo(item.getSortNo() != null ? item.getSortNo() : defaultSortNo(item.getResponsibilityType()));
    }

    private int defaultSortNo(String type) {
        if (ResponsibilityType.PRIMARY.name().equals(type)) {
            return 1;
        }
        if (ResponsibilityType.TECHNICAL.name().equals(type)) {
            return 2;
        }
        return 3;
    }

    private void validateResponsibilityPayload(ResponsibilityReplaceRequest request) {
        Set<String> types = new HashSet<String>();
        for (ResponsibilityRequest item : request.getResponsibilities()) {
            ResponsibilityType.assertValid(item.getResponsibilityType());
            types.add(item.getResponsibilityType().trim());
        }
        if (types.size() != request.getResponsibilities().size()) {
            throw new BusinessException(400, "duplicate responsibility type in request");
        }
    }

    private void assertPublishReady(MajorHazardEntity entity) {
        if (entity.getAreaId() == null || entity.getAreaId() <= 0) {
            throw new BusinessException(400, "areaId is required before publish");
        }
        List<MajorHazardResponsibilityEntity> responsibilities = responsibilityMapper.listByHazardId(
                entity.getTenantId(), entity.getId());
        Set<String> types = new HashSet<String>();
        for (MajorHazardResponsibilityEntity item : responsibilities) {
            types.add(item.getResponsibilityType());
        }
        for (String required : ResponsibilityType.REQUIRED_TYPES) {
            if (!types.contains(required)) {
                throw new BusinessException(400, "missing responsibility type before publish: " + required);
            }
        }
    }

    private void applyRequest(MajorHazardEntity entity, MajorHazardRequest request) {
        entity.setHazardNo(request.getHazardNo().trim());
        entity.setName(request.getName().trim());
        entity.setHazardType(normalizeText(request.getHazardType()));
        entity.setLevel(request.getLevel().trim());
        entity.setAreaId(request.getAreaId());
        entity.setUnitId(request.getUnitId());
        entity.setMaterial(normalizeText(request.getMaterial()));
        entity.setDesignCapacity(normalizeText(request.getDesignCapacity()));
        entity.setActualCapacity(normalizeText(request.getActualCapacity()));
        entity.setCriticalQuantity(normalizeText(request.getCriticalQuantity()));
        entity.setEmergencyPlanId(request.getEmergencyPlanId());
    }

    private void assertHazardNoUnique(Long tenantId, String hazardNo, Long excludeId) {
        MajorHazardEntity existing = hazardMapper.findByHazardNo(tenantId, hazardNo.trim());
        if (existing != null && (excludeId == null || !excludeId.equals(existing.getId()))) {
            throw new BusinessException(400, "hazard no already exists: " + hazardNo);
        }
    }

    private MajorHazardEntity requireHazard(Long tenantId, Long id) {
        requireTenantId(tenantId);
        MajorHazardEntity entity = hazardMapper.selectById(id);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "major hazard not found");
        }
        return entity;
    }

    private void writeStatusLog(Long tenantId, Long hazardId, String fromStatus, String toStatus,
                                String reason, String operator) {
        MajorHazardStatusLogEntity log = new MajorHazardStatusLogEntity();
        log.setTenantId(tenantId);
        log.setHazardId(hazardId);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setReason(normalizeText(reason));
        log.setOperatorName(defaultOperator(operator));
        log.setOperatedAt(LocalDateTime.now());
        statusLogMapper.insert(log);
    }

    private void writeAudit(Long tenantId, Long hazardId, String targetType, String action,
                            String beforeStatus, String afterStatus, String opinion, String operator) {
        MajorHazardAuditRecordEntity record = new MajorHazardAuditRecordEntity();
        record.setTenantId(tenantId);
        record.setTargetType(targetType);
        record.setTargetId(hazardId);
        record.setAction(action);
        record.setBeforeStatus(beforeStatus);
        record.setAfterStatus(afterStatus);
        record.setOpinion(opinion);
        record.setOperatorName(defaultOperator(operator));
        record.setOperatedAt(LocalDateTime.now());
        auditRecordMapper.insert(record);
        centralAuditClient.append(CentralAuditClient.build(tenantId, record.getOperatorName(), action,
                targetType, hazardId, beforeStatus, afterStatus));
    }

    private MajorHazardVO toVO(MajorHazardEntity entity) {
        MajorHazardVO vo = new MajorHazardVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setHazardNo(entity.getHazardNo());
        vo.setName(entity.getName());
        vo.setHazardType(entity.getHazardType());
        vo.setLevel(entity.getLevel());
        vo.setAreaId(entity.getAreaId());
        vo.setUnitId(entity.getUnitId());
        vo.setMaterial(entity.getMaterial());
        vo.setDesignCapacity(entity.getDesignCapacity());
        vo.setActualCapacity(entity.getActualCapacity());
        vo.setCriticalQuantity(entity.getCriticalQuantity());
        vo.setEmergencyPlanId(entity.getEmergencyPlanId());
        vo.setStatus(entity.getStatus());
        vo.setPublishedAt(entity.getPublishedAt());
        return vo;
    }

    private MajorHazardResponsibilityVO toResponsibilityVO(MajorHazardResponsibilityEntity entity) {
        MajorHazardResponsibilityVO vo = new MajorHazardResponsibilityVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setHazardId(entity.getHazardId());
        vo.setResponsibilityType(entity.getResponsibilityType());
        vo.setPersonName(entity.getPersonName());
        vo.setPersonPhone(entity.getPersonPhone());
        vo.setPersonId(entity.getPersonId());
        vo.setSortNo(entity.getSortNo());
        return vo;
    }

    private String defaultOperator(String operator) {
        return StringUtils.hasText(operator) ? operator.trim() : "system";
    }

    private void requireTenantId(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private Page normalizePage(int pageNo, int pageSize) {
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);
        return new Page(normalizedPageNo, normalizedPageSize);
    }

    private static final class Page {
        private final int pageNo;
        private final int pageSize;
        private final int offset;

        private Page(int pageNo, int pageSize) {
            this.pageNo = pageNo;
            this.pageSize = pageSize;
            this.offset = (pageNo - 1) * pageSize;
        }
    }
}
