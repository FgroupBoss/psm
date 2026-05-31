package com.fgroupboss.ai.psm.incidentgovernance.incident.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.incidentgovernance.incident.config.CapaStatus;
import com.fgroupboss.ai.psm.incidentgovernance.incident.config.IncidentStatus;
import com.fgroupboss.ai.psm.incidentgovernance.incident.mapper.IncidentCapaMapper;
import com.fgroupboss.ai.psm.incidentgovernance.incident.mapper.IncidentEvidenceMapper;
import com.fgroupboss.ai.psm.incidentgovernance.incident.mapper.IncidentInvestigationMapper;
import com.fgroupboss.ai.psm.incidentgovernance.incident.mapper.IncidentReportMapper;
import com.fgroupboss.ai.psm.incidentgovernance.incident.mapper.IncidentRootCauseMapper;
import com.fgroupboss.ai.psm.incidentgovernance.incident.mapper.IncidentTimelineMapper;
import com.fgroupboss.ai.psm.incidentgovernance.incident.mapper.LessonLearnedMapper;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.CapaVerifyRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.IncidentCapaRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.IncidentEvidenceRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.IncidentFromSourceRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.IncidentReportRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.IncidentRootCauseRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.IncidentTimelineRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.LessonLearnedRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.StartInvestigationRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.entity.IncidentCapaEntity;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.entity.IncidentEvidenceEntity;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.entity.IncidentInvestigationEntity;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.entity.IncidentReportEntity;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.entity.IncidentRootCauseEntity;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.entity.IncidentTimelineEntity;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.entity.LessonLearnedEntity;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.vo.IncidentCapaVO;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.vo.IncidentEvidenceVO;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.vo.IncidentReportVO;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.vo.IncidentRootCauseVO;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.vo.IncidentTimelineVO;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.vo.LessonLearnedVO;
import com.fgroupboss.ai.psm.incidentgovernance.incident.service.IncidentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fgroupboss.ai.psm.incidentgovernance.incident.support.ServiceSupport.nextNo;
import static com.fgroupboss.ai.psm.incidentgovernance.incident.support.ServiceSupport.normalizePageNo;
import static com.fgroupboss.ai.psm.incidentgovernance.incident.support.ServiceSupport.normalizePageSize;
import static com.fgroupboss.ai.psm.incidentgovernance.incident.support.ServiceSupport.now;
import static com.fgroupboss.ai.psm.incidentgovernance.incident.support.ServiceSupport.requireTenantId;
import static com.fgroupboss.ai.psm.incidentgovernance.incident.support.ServiceSupport.toPage;

/**
 * 事故调查、根因与 CAPA 闭环实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService {

    private static final String INVESTIGATION_IN_PROGRESS = "IN_PROGRESS";
    private static final String INCIDENT_NO_PREFIX = "INC";
    private static final String CAPA_NO_PREFIX = "CAPA";

    private final IncidentReportMapper incidentReportMapper;
    private final IncidentInvestigationMapper incidentInvestigationMapper;
    private final IncidentTimelineMapper incidentTimelineMapper;
    private final IncidentEvidenceMapper incidentEvidenceMapper;
    private final IncidentRootCauseMapper incidentRootCauseMapper;
    private final IncidentCapaMapper incidentCapaMapper;
    private final LessonLearnedMapper lessonLearnedMapper;

    @Override
    public PageResult<IncidentReportVO> page(Long tenantId, String keyword, String status,
                                             String incidentType, String incidentLevel,
                                             int pageNo, int pageSize) {
        requireTenantId(tenantId);
        int pNo = normalizePageNo(pageNo);
        int pSize = normalizePageSize(pageSize);

        LambdaQueryWrapper<IncidentReportEntity> wrapper = baseReportWrapper(tenantId);
        if (StringUtils.hasText(keyword)) {
            String trimmed = keyword.trim();
            wrapper.and(w -> w.like(IncidentReportEntity::getIncidentNo, trimmed)
                    .or()
                    .like(IncidentReportEntity::getIncidentType, trimmed));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(IncidentReportEntity::getStatus, status.trim());
        }
        if (StringUtils.hasText(incidentType)) {
            wrapper.eq(IncidentReportEntity::getIncidentType, incidentType.trim());
        }
        if (StringUtils.hasText(incidentLevel)) {
            wrapper.eq(IncidentReportEntity::getIncidentLevel, incidentLevel.trim());
        }
        wrapper.orderByDesc(IncidentReportEntity::getId);

        Page<IncidentReportEntity> mpPage = new Page<IncidentReportEntity>(pNo, pSize);
        Page<IncidentReportEntity> result = incidentReportMapper.selectPage(mpPage, wrapper);
        return toPage(result, IncidentServiceImpl::toReportVO);
    }

    @Override
    public IncidentReportVO getById(Long tenantId, Long id) {
        return toReportVO(requireIncident(tenantId, id));
    }

    @Override
    @Transactional
    public IncidentReportVO create(IncidentReportRequest request, String operator) {
        requireTenantId(request.getTenantId());
        IncidentReportEntity entity = buildReportEntity(request.getTenantId());
        applyReportRequest(entity, request);
        entity.setStatus(IncidentStatus.REPORTED.name());
        if (entity.getOccurredAt() == null) {
            entity.setOccurredAt(now());
        }
        incidentReportMapper.insert(entity);
        log.info("incident created incidentId={} operator={}", entity.getId(), operator);
        return toReportVO(entity);
    }

    @Override
    @Transactional
    public IncidentReportVO update(Long id, IncidentReportRequest request, String operator) {
        IncidentReportEntity entity = requireIncident(request.getTenantId(), id);
        assertEditableIncident(entity.getStatus());
        applyReportRequest(entity, request);
        touchUpdated(entity);
        incidentReportMapper.updateById(entity);
        log.info("incident updated incidentId={} operator={}", id, operator);
        return toReportVO(entity);
    }

    @Override
    @Transactional
    public IncidentReportVO createFromSource(IncidentFromSourceRequest request, String operator) {
        requireTenantId(request.getTenantId());
        IncidentReportEntity entity = buildReportEntity(request.getTenantId());
        entity.setIncidentType(request.getIncidentType().trim());
        entity.setIncidentLevel(request.getIncidentLevel().trim());
        entity.setOccurredAt(request.getOccurredAt() == null ? now() : request.getOccurredAt());
        entity.setAreaId(request.getAreaId());
        entity.setEquipmentId(request.getEquipmentId());
        entity.setSourceType(request.getSourceType().trim());
        entity.setSourceBizId(request.getSourceBizId());
        entity.setStatus(IncidentStatus.REPORTED.name());
        incidentReportMapper.insert(entity);
        log.info("incident from source incidentId={} sourceType={} sourceBizId={} operator={}",
                entity.getId(), entity.getSourceType(), entity.getSourceBizId(), operator);
        return toReportVO(entity);
    }

    @Override
    @Transactional
    public IncidentReportVO startInvestigation(Long id, StartInvestigationRequest request, String operator) {
        IncidentReportEntity entity = requireIncident(request.getTenantId(), id);
        String before = entity.getStatus();
        IncidentStatus.assertStartInvestigation(before);
        String after = IncidentStatus.targetAfterStartInvestigation();
        IncidentStatus.assertDirectTransition(before, after);
        entity.setStatus(after);
        touchUpdated(entity);
        incidentReportMapper.updateById(entity);

        IncidentInvestigationEntity investigation = new IncidentInvestigationEntity();
        investigation.setTenantId(request.getTenantId());
        investigation.setIncidentId(id);
        investigation.setLeadUserId(request.getLeadUserId());
        investigation.setScopeDesc(trimToNull(request.getScopeDesc()));
        investigation.setStatus(INVESTIGATION_IN_PROGRESS);
        initAudit(investigation);
        incidentInvestigationMapper.insert(investigation);

        log.info("investigation started incidentId={} investigationId={} operator={}",
                id, investigation.getId(), operator);
        return toReportVO(entity);
    }

    @Override
    public List<IncidentTimelineVO> listTimeline(Long tenantId, Long incidentId) {
        requireIncident(tenantId, incidentId);
        List<IncidentTimelineEntity> entities = incidentTimelineMapper.selectList(
                timelineWrapper(tenantId, incidentId)
                        .orderByAsc(IncidentTimelineEntity::getEventAt)
                        .orderByAsc(IncidentTimelineEntity::getId));
        return mapTimelineList(entities);
    }

    @Override
    @Transactional
    public IncidentTimelineVO addTimeline(Long incidentId, IncidentTimelineRequest request, String operator) {
        requireIncident(request.getTenantId(), incidentId);
        IncidentTimelineEntity entity = new IncidentTimelineEntity();
        entity.setTenantId(request.getTenantId());
        entity.setIncidentId(incidentId);
        entity.setEventAt(request.getEventAt());
        entity.setEventDesc(request.getEventDesc().trim());
        initAudit(entity);
        incidentTimelineMapper.insert(entity);
        log.info("timeline added incidentId={} timelineId={} operator={}", incidentId, entity.getId(), operator);
        return toTimelineVO(entity);
    }

    @Override
    public List<IncidentEvidenceVO> listEvidence(Long tenantId, Long incidentId) {
        requireIncident(tenantId, incidentId);
        List<IncidentEvidenceEntity> entities = incidentEvidenceMapper.selectList(
                evidenceWrapper(tenantId, incidentId).orderByDesc(IncidentEvidenceEntity::getId));
        return mapEvidenceList(entities);
    }

    @Override
    @Transactional
    public IncidentEvidenceVO addEvidence(Long incidentId, IncidentEvidenceRequest request, String operator) {
        requireIncident(request.getTenantId(), incidentId);
        IncidentEvidenceEntity entity = new IncidentEvidenceEntity();
        entity.setTenantId(request.getTenantId());
        entity.setIncidentId(incidentId);
        entity.setEvidenceType(request.getEvidenceType().trim());
        entity.setFileId(request.getFileId());
        entity.setDescription(trimToNull(request.getDescription()));
        initAudit(entity);
        incidentEvidenceMapper.insert(entity);
        log.info("evidence added incidentId={} evidenceId={} operator={}", incidentId, entity.getId(), operator);
        return toEvidenceVO(entity);
    }

    @Override
    public List<IncidentRootCauseVO> listRootCauses(Long tenantId, Long incidentId) {
        requireIncident(tenantId, incidentId);
        List<IncidentRootCauseEntity> entities = incidentRootCauseMapper.selectList(
                rootCauseWrapper(tenantId, incidentId).orderByDesc(IncidentRootCauseEntity::getId));
        return mapRootCauseList(entities);
    }

    @Override
    @Transactional
    public IncidentRootCauseVO addRootCause(Long incidentId, IncidentRootCauseRequest request, String operator) {
        requireIncident(request.getTenantId(), incidentId);
        IncidentRootCauseEntity entity = new IncidentRootCauseEntity();
        entity.setTenantId(request.getTenantId());
        entity.setIncidentId(incidentId);
        entity.setCauseType(request.getCauseType().trim());
        entity.setCauseDesc(request.getCauseDesc().trim());
        initAudit(entity);
        incidentRootCauseMapper.insert(entity);
        log.info("root cause added incidentId={} rootCauseId={} operator={}", incidentId, entity.getId(), operator);
        return toRootCauseVO(entity);
    }

    @Override
    public List<IncidentCapaVO> listCapa(Long tenantId, Long incidentId) {
        requireIncident(tenantId, incidentId);
        List<IncidentCapaEntity> entities = incidentCapaMapper.selectList(
                capaWrapper(tenantId, incidentId).orderByDesc(IncidentCapaEntity::getId));
        return mapCapaList(entities);
    }

    @Override
    @Transactional
    public IncidentCapaVO addCapa(Long incidentId, IncidentCapaRequest request, String operator) {
        IncidentReportEntity incident = requireIncident(request.getTenantId(), incidentId);
        IncidentCapaEntity entity = new IncidentCapaEntity();
        entity.setTenantId(request.getTenantId());
        entity.setIncidentId(incidentId);
        entity.setCapaNo(nextNo(CAPA_NO_PREFIX));
        entity.setCapaType(request.getCapaType().trim());
        entity.setOwnerUserId(request.getOwnerUserId());
        entity.setDueAt(request.getDueAt());
        entity.setVerificationUserId(request.getVerificationUserId());
        entity.setStatus(CapaStatus.EXECUTING.name());
        initAudit(entity);
        incidentCapaMapper.insert(entity);

        if (!IncidentStatus.CAPA_EXECUTING.name().equals(incident.getStatus())
                && !IncidentStatus.CLOSED.name().equals(incident.getStatus())) {
            incident.setStatus(IncidentStatus.CAPA_EXECUTING.name());
            touchUpdated(incident);
            incidentReportMapper.updateById(incident);
        }
        log.info("capa added incidentId={} capaId={} operator={}", incidentId, entity.getId(), operator);
        return toCapaVO(entity);
    }

    @Override
    @Transactional
    public IncidentCapaVO verifyCapa(Long capaId, CapaVerifyRequest request, String operator) {
        IncidentCapaEntity entity = requireCapa(request.getTenantId(), capaId);
        CapaStatus.assertVerify(entity.getStatus());
        boolean passed = Boolean.TRUE.equals(request.getPassed());
        String after = CapaStatus.targetAfterVerify(passed);
        entity.setStatus(after);
        if (request.getEvidenceFileId() != null) {
            entity.setEvidenceFileId(request.getEvidenceFileId());
        }
        touchUpdated(entity);
        incidentCapaMapper.updateById(entity);
        log.info("capa verified capaId={} passed={} operator={}", capaId, passed, operator);
        return toCapaVO(entity);
    }

    @Override
    @Transactional
    public LessonLearnedVO addLessonLearned(Long incidentId, LessonLearnedRequest request, String operator) {
        requireIncident(request.getTenantId(), incidentId);
        LessonLearnedEntity entity = new LessonLearnedEntity();
        entity.setTenantId(request.getTenantId());
        entity.setIncidentId(incidentId);
        entity.setLessonDesc(request.getLessonDesc().trim());
        entity.setActionType(request.getActionType().trim());
        initAudit(entity);
        lessonLearnedMapper.insert(entity);
        log.info("lesson learned added incidentId={} lessonId={} operator={}", incidentId, entity.getId(), operator);
        return toLessonVO(entity);
    }

    private LambdaQueryWrapper<IncidentReportEntity> baseReportWrapper(Long tenantId) {
        return new LambdaQueryWrapper<IncidentReportEntity>()
                .eq(IncidentReportEntity::getTenantId, tenantId)
                .eq(IncidentReportEntity::getDeleted, 0);
    }

    private LambdaQueryWrapper<IncidentTimelineEntity> timelineWrapper(Long tenantId, Long incidentId) {
        return new LambdaQueryWrapper<IncidentTimelineEntity>()
                .eq(IncidentTimelineEntity::getTenantId, tenantId)
                .eq(IncidentTimelineEntity::getIncidentId, incidentId)
                .eq(IncidentTimelineEntity::getDeleted, 0);
    }

    private LambdaQueryWrapper<IncidentEvidenceEntity> evidenceWrapper(Long tenantId, Long incidentId) {
        return new LambdaQueryWrapper<IncidentEvidenceEntity>()
                .eq(IncidentEvidenceEntity::getTenantId, tenantId)
                .eq(IncidentEvidenceEntity::getIncidentId, incidentId)
                .eq(IncidentEvidenceEntity::getDeleted, 0);
    }

    private LambdaQueryWrapper<IncidentRootCauseEntity> rootCauseWrapper(Long tenantId, Long incidentId) {
        return new LambdaQueryWrapper<IncidentRootCauseEntity>()
                .eq(IncidentRootCauseEntity::getTenantId, tenantId)
                .eq(IncidentRootCauseEntity::getIncidentId, incidentId)
                .eq(IncidentRootCauseEntity::getDeleted, 0);
    }

    private LambdaQueryWrapper<IncidentCapaEntity> capaWrapper(Long tenantId, Long incidentId) {
        return new LambdaQueryWrapper<IncidentCapaEntity>()
                .eq(IncidentCapaEntity::getTenantId, tenantId)
                .eq(IncidentCapaEntity::getIncidentId, incidentId)
                .eq(IncidentCapaEntity::getDeleted, 0);
    }

    private IncidentReportEntity buildReportEntity(Long tenantId) {
        IncidentReportEntity entity = new IncidentReportEntity();
        entity.setTenantId(tenantId);
        entity.setIncidentNo(nextNo(INCIDENT_NO_PREFIX));
        initAudit(entity);
        return entity;
    }

    private void applyReportRequest(IncidentReportEntity entity, IncidentReportRequest request) {
        entity.setIncidentType(request.getIncidentType().trim());
        entity.setIncidentLevel(request.getIncidentLevel().trim());
        if (request.getOccurredAt() != null) {
            entity.setOccurredAt(request.getOccurredAt());
        }
        entity.setAreaId(request.getAreaId());
        entity.setEquipmentId(request.getEquipmentId());
        if (StringUtils.hasText(request.getSourceType())) {
            entity.setSourceType(request.getSourceType().trim());
        }
        entity.setSourceBizId(request.getSourceBizId());
    }

    private void assertEditableIncident(String status) {
        if (IncidentStatus.CLOSED.name().equals(status) || IncidentStatus.CANCELLED.name().equals(status)) {
            throw new BusinessException(400, "incident cannot be edited in status " + status);
        }
    }

    private IncidentReportEntity requireIncident(Long tenantId, Long id) {
        requireTenantId(tenantId);
        if (id == null || id <= 0) {
            throw new BusinessException(400, "id is required");
        }
        IncidentReportEntity entity = incidentReportMapper.selectById(id);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "incident not found");
        }
        return entity;
    }

    private IncidentCapaEntity requireCapa(Long tenantId, Long id) {
        requireTenantId(tenantId);
        if (id == null || id <= 0) {
            throw new BusinessException(400, "id is required");
        }
        IncidentCapaEntity entity = incidentCapaMapper.selectById(id);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() == 1
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "CAPA not found");
        }
        return entity;
    }

    private void initAudit(Object entity) {
        LocalDateTime ts = now();
        if (entity instanceof IncidentReportEntity) {
            IncidentReportEntity e = (IncidentReportEntity) entity;
            e.setCreatedAt(ts);
            e.setUpdatedAt(ts);
            e.setDeleted(0);
        } else if (entity instanceof IncidentInvestigationEntity) {
            IncidentInvestigationEntity e = (IncidentInvestigationEntity) entity;
            e.setCreatedAt(ts);
            e.setUpdatedAt(ts);
            e.setDeleted(0);
        } else if (entity instanceof IncidentTimelineEntity) {
            IncidentTimelineEntity e = (IncidentTimelineEntity) entity;
            e.setCreatedAt(ts);
            e.setUpdatedAt(ts);
            e.setDeleted(0);
        } else if (entity instanceof IncidentEvidenceEntity) {
            IncidentEvidenceEntity e = (IncidentEvidenceEntity) entity;
            e.setCreatedAt(ts);
            e.setUpdatedAt(ts);
            e.setDeleted(0);
        } else if (entity instanceof IncidentRootCauseEntity) {
            IncidentRootCauseEntity e = (IncidentRootCauseEntity) entity;
            e.setCreatedAt(ts);
            e.setUpdatedAt(ts);
            e.setDeleted(0);
        } else if (entity instanceof IncidentCapaEntity) {
            IncidentCapaEntity e = (IncidentCapaEntity) entity;
            e.setCreatedAt(ts);
            e.setUpdatedAt(ts);
            e.setDeleted(0);
        } else if (entity instanceof LessonLearnedEntity) {
            LessonLearnedEntity e = (LessonLearnedEntity) entity;
            e.setCreatedAt(ts);
            e.setUpdatedAt(ts);
            e.setDeleted(0);
        }
    }

    private void touchUpdated(IncidentReportEntity entity) {
        entity.setUpdatedAt(now());
    }

    private void touchUpdated(IncidentCapaEntity entity) {
        entity.setUpdatedAt(now());
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private static List<IncidentTimelineVO> mapTimelineList(List<IncidentTimelineEntity> entities) {
        List<IncidentTimelineVO> list = new ArrayList<IncidentTimelineVO>();
        for (IncidentTimelineEntity entity : entities) {
            list.add(toTimelineVO(entity));
        }
        return list;
    }

    private static List<IncidentEvidenceVO> mapEvidenceList(List<IncidentEvidenceEntity> entities) {
        List<IncidentEvidenceVO> list = new ArrayList<IncidentEvidenceVO>();
        for (IncidentEvidenceEntity entity : entities) {
            list.add(toEvidenceVO(entity));
        }
        return list;
    }

    private static List<IncidentRootCauseVO> mapRootCauseList(List<IncidentRootCauseEntity> entities) {
        List<IncidentRootCauseVO> list = new ArrayList<IncidentRootCauseVO>();
        for (IncidentRootCauseEntity entity : entities) {
            list.add(toRootCauseVO(entity));
        }
        return list;
    }

    private static List<IncidentCapaVO> mapCapaList(List<IncidentCapaEntity> entities) {
        List<IncidentCapaVO> list = new ArrayList<IncidentCapaVO>();
        for (IncidentCapaEntity entity : entities) {
            list.add(toCapaVO(entity));
        }
        return list;
    }

    private static IncidentReportVO toReportVO(IncidentReportEntity entity) {
        IncidentReportVO vo = new IncidentReportVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setIncidentNo(entity.getIncidentNo());
        vo.setIncidentType(entity.getIncidentType());
        vo.setIncidentLevel(entity.getIncidentLevel());
        vo.setOccurredAt(entity.getOccurredAt());
        vo.setAreaId(entity.getAreaId());
        vo.setEquipmentId(entity.getEquipmentId());
        vo.setSourceType(entity.getSourceType());
        vo.setSourceBizId(entity.getSourceBizId());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private static IncidentTimelineVO toTimelineVO(IncidentTimelineEntity entity) {
        IncidentTimelineVO vo = new IncidentTimelineVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setIncidentId(entity.getIncidentId());
        vo.setEventAt(entity.getEventAt());
        vo.setEventDesc(entity.getEventDesc());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private static IncidentEvidenceVO toEvidenceVO(IncidentEvidenceEntity entity) {
        IncidentEvidenceVO vo = new IncidentEvidenceVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setIncidentId(entity.getIncidentId());
        vo.setEvidenceType(entity.getEvidenceType());
        vo.setFileId(entity.getFileId());
        vo.setDescription(entity.getDescription());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private static IncidentRootCauseVO toRootCauseVO(IncidentRootCauseEntity entity) {
        IncidentRootCauseVO vo = new IncidentRootCauseVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setIncidentId(entity.getIncidentId());
        vo.setCauseType(entity.getCauseType());
        vo.setCauseDesc(entity.getCauseDesc());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private static IncidentCapaVO toCapaVO(IncidentCapaEntity entity) {
        IncidentCapaVO vo = new IncidentCapaVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setIncidentId(entity.getIncidentId());
        vo.setCapaNo(entity.getCapaNo());
        vo.setCapaType(entity.getCapaType());
        vo.setOwnerUserId(entity.getOwnerUserId());
        vo.setDueAt(entity.getDueAt());
        vo.setVerificationUserId(entity.getVerificationUserId());
        vo.setEvidenceFileId(entity.getEvidenceFileId());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private static LessonLearnedVO toLessonVO(LessonLearnedEntity entity) {
        LessonLearnedVO vo = new LessonLearnedVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setIncidentId(entity.getIncidentId());
        vo.setLessonDesc(entity.getLessonDesc());
        vo.setActionType(entity.getActionType());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
