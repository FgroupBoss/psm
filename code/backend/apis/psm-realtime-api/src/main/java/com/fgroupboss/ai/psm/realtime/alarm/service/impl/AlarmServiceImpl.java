package com.fgroupboss.ai.psm.realtime.alarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fgroupboss.ai.psm.realtime.alarm.config.AlarmActionType;
import com.fgroupboss.ai.psm.realtime.alarm.config.AlarmLevel;
import com.fgroupboss.ai.psm.realtime.alarm.config.AlarmStatus;
import com.fgroupboss.ai.psm.realtime.alarm.config.AlarmStatusTransition;
import com.fgroupboss.ai.psm.realtime.alarm.mapper.AlarmActionRecordMapper;
import com.fgroupboss.ai.psm.realtime.alarm.mapper.AlarmEventMapper;
import com.fgroupboss.ai.psm.realtime.alarm.mapper.AlarmOccurrenceMapper;
import com.fgroupboss.ai.psm.realtime.api.alarm.dto.AlarmActionRequest;
import com.fgroupboss.ai.psm.realtime.api.alarm.dto.AlarmAreaActiveCheckRequest;
import com.fgroupboss.ai.psm.realtime.alarm.model.dto.AlarmFalseCloseRequest;
import com.fgroupboss.ai.psm.realtime.alarm.client.DualPreventionClient;
import com.fgroupboss.ai.psm.realtime.alarm.client.RemoteHazardCreateRequest;
import com.fgroupboss.ai.psm.realtime.alarm.client.RemoteHazardReportVO;
import com.fgroupboss.ai.psm.realtime.alarm.model.dto.AlarmIngestRequest;
import com.fgroupboss.ai.psm.realtime.alarm.model.dto.AlarmToHazardRequest;
import com.fgroupboss.ai.psm.realtime.alarm.model.entity.AlarmActionRecordEntity;
import com.fgroupboss.ai.psm.realtime.alarm.model.entity.AlarmEventEntity;
import com.fgroupboss.ai.psm.realtime.alarm.model.entity.AlarmOccurrenceEntity;
import com.fgroupboss.ai.psm.realtime.alarm.model.vo.AlarmActionSummaryVO;
import com.fgroupboss.ai.psm.realtime.api.alarm.vo.AlarmAreaActiveCheckVO;
import com.fgroupboss.ai.psm.realtime.alarm.model.vo.AlarmDetailVO;
import com.fgroupboss.ai.psm.realtime.api.alarm.vo.AlarmEventVO;
import com.fgroupboss.ai.psm.realtime.alarm.model.vo.AlarmHealthVO;
import com.fgroupboss.ai.psm.realtime.alarm.model.vo.AlarmOccurrenceVO;
import com.fgroupboss.ai.psm.realtime.alarm.service.AlarmService;
import com.fgroupboss.ai.psm.realtime.alarm.support.AlarmAuditSupport;
import com.fgroupboss.ai.psm.realtime.alarm.support.AlarmDedupSupport;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 瀹炵幇鏂瑰紡锛氭壙杞芥姤璀︿笟鍔″疄鐜帮紝鍩轰簬 Mapper銆佽繙绋嬪鎴风鎴栨敮鎾戠粍浠跺畬鎴愭牎楠屻€佺姸鎬佹祦杞拰缁撴灉缁勮銆?
 */
@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {

    private static final String DEFAULT_MIN_LEVEL = "LEVEL_2";

    private final AlarmEventMapper alarmEventMapper;
    private final AlarmOccurrenceMapper occurrenceMapper;
    private final AlarmActionRecordMapper actionRecordMapper;
    private final AlarmAuditSupport auditSupport;
    private final AlarmDedupSupport dedupSupport;
    private final DualPreventionClient dualPreventionClient;

    /**
     * 瀹炵幇鏂瑰紡锛氭煡璇㈡湇鍔″仴搴风姸鎬侊紝鍏堝畬鎴愬繀瑕佺殑鍙傛暟銆佺鎴锋垨鐘舵€佹牎楠岋紝鍐嶅鎵樻寔涔呭寲缁勪欢鎴栬繙绋嬪鎴风澶勭悊骞剁粍瑁呰繑鍥炵粨鏋溿€?
     */
    @Override
    public AlarmHealthVO health() {
        LambdaQueryWrapper<AlarmEventEntity> wrapper = new LambdaQueryWrapper<AlarmEventEntity>()
                .eq(AlarmEventEntity::getDeleted, 0);
        long count = alarmEventMapper.selectCount(wrapper);
        return new AlarmHealthVO("psm-alarm-service", "alarm", "1.0.0-batch6", count);
    }

    /**
     * 瀹炵幇鏂瑰紡锛氭帴鍏ユ姤璀︿簨浠讹紝鍏堝畬鎴愬繀瑕佺殑鍙傛暟銆佺鎴锋垨鐘舵€佹牎楠岋紝鍐嶅鎵樻寔涔呭寲缁勪欢鎴栬繙绋嬪鎴风澶勭悊骞剁粍瑁呰繑鍥炵粨鏋溿€?
     */
    @Override
    @Transactional
    public AlarmEventVO ingest(AlarmIngestRequest request) {
        requireTenantId(request.getTenantId());
        Date occurredAt = request.getOccurredAt() == null ? new Date() : request.getOccurredAt();

        AlarmEventEntity draft = buildDraftEvent(request, occurredAt);
        String dedupKey = buildDedupKey(draft);
        int windowSeconds = dedupSupport.resolveWindowSeconds(request.getTenantId(), draft.getSourceType());
        AlarmEventEntity existing = dedupSupport.findMergeCandidate(request.getTenantId(), dedupKey, occurredAt, windowSeconds);
        if (existing != null) {
            return mergeOccurrence(existing, request, occurredAt);
        }

        draft.setDedupKey(dedupKey);
        draft.setOccurrenceCount(1);
        draft.setFirstOccurredAt(occurredAt);
        draft.setLastOccurredAt(occurredAt);
        draft.setDeleted(0);
        alarmEventMapper.insert(draft);
        insertOccurrence(request.getTenantId(), draft.getId(), occurredAt, request.getRawValue());
        return toVO(draft);
    }

    /**
     * 瀹炵幇鏂瑰紡锛氬垎椤垫煡璇笟鍔℃暟鎹紝鍏堝畬鎴愬繀瑕佺殑鍙傛暟銆佺鎴锋垨鐘舵€佹牎楠岋紝鍐嶅鎵樻寔涔呭寲缁勪欢鎴栬繙绋嬪鎴风澶勭悊骞剁粍瑁呰繑鍥炵粨鏋溿€?
     */
    @Override
    public PageResult<AlarmEventVO> page(Long tenantId, String keyword, String status, String alarmLevel,
                                         Long areaId, Long hazardId, String sourceType, Date occurredFrom, Date occurredTo,
                                         int pageNo, int pageSize) {
        requireTenantId(tenantId);
        Page page = normalizePage(pageNo, pageSize);
        String normalizedKeyword = normalizeText(keyword);
        String normalizedStatus = normalizeText(status);
        String normalizedLevel = normalizeText(alarmLevel);
        String normalizedSourceType = normalizeText(sourceType);

        long total = alarmEventMapper.countByTenant(tenantId, normalizedKeyword, normalizedStatus, normalizedLevel,
                areaId, hazardId, normalizedSourceType, occurredFrom, occurredTo);
        List<AlarmEventEntity> entities = total == 0
                ? new ArrayList<AlarmEventEntity>()
                : alarmEventMapper.listByTenant(tenantId, normalizedKeyword, normalizedStatus, normalizedLevel,
                areaId, hazardId, normalizedSourceType, occurredFrom, occurredTo, page.offset, page.pageSize);

        List<AlarmEventVO> records = new ArrayList<AlarmEventVO>();
        for (AlarmEventEntity entity : entities) {
            records.add(toVO(entity));
        }
        return new PageResult<AlarmEventVO>(total, page.pageNo, page.pageSize, records);
    }

    /**
     * 瀹炵幇鏂瑰紡锛氭墽琛屼笟鍔″疄鐜帮紝鍏堝畬鎴愬繀瑕佺殑鍙傛暟銆佺鎴锋垨鐘舵€佹牎楠岋紝鍐嶅鎵樻寔涔呭寲缁勪欢鎴栬繙绋嬪鎴风澶勭悊骞剁粍瑁呰繑鍥炵粨鏋溿€?
     */
    @Override
    public AlarmDetailVO getDetail(Long tenantId, Long id) {
        AlarmEventEntity entity = requireEvent(tenantId, id);
        AlarmDetailVO detail = new AlarmDetailVO();
        detail.setEvent(toVO(entity));
        detail.setOccurrences(toOccurrenceVOs(occurrenceMapper.listByAlarmEventId(tenantId, id)));
        detail.setActions(toActionVOs(actionRecordMapper.listByAlarmEventId(tenantId, id)));
        return detail;
    }

    /**
     * 瀹炵幇鏂瑰紡锛氱‘璁ゆ姤璀︼紝鍏堝畬鎴愬繀瑕佺殑鍙傛暟銆佺鎴锋垨鐘舵€佹牎楠岋紝鍐嶅鎵樻寔涔呭寲缁勪欢鎴栬繙绋嬪鎴风澶勭悊骞剁粍瑁呰繑鍥炵粨鏋溿€?
     */
    @Override
    @Transactional
    public AlarmEventVO confirm(Long tenantId, Long id, AlarmActionRequest request, String operator) {
        return transition(tenantId, id, AlarmActionType.CONFIRM, request, operator,
                AlarmStatusTransition::confirmTarget);
    }

    /**
     * 瀹炵幇鏂瑰紡锛氭淳鍙戞姤璀︼紝鍏堝畬鎴愬繀瑕佺殑鍙傛暟銆佺鎴锋垨鐘舵€佹牎楠岋紝鍐嶅鎵樻寔涔呭寲缁勪欢鎴栬繙绋嬪鎴风澶勭悊骞剁粍瑁呰繑鍥炵粨鏋溿€?
     */
    @Override
    @Transactional
    public AlarmEventVO dispatch(Long tenantId, Long id, AlarmActionRequest request, String operator) {
        return transition(tenantId, id, AlarmActionType.DISPATCH, request, operator,
                AlarmStatusTransition::dispatchTarget);
    }

    /**
     * 瀹炵幇鏂瑰紡锛氬弽棣堟姤璀﹀鐞嗙粨鏋滐紝鍏堝畬鎴愬繀瑕佺殑鍙傛暟銆佺鎴锋垨鐘舵€佹牎楠岋紝鍐嶅鎵樻寔涔呭寲缁勪欢鎴栬繙绋嬪鎴风澶勭悊骞剁粍瑁呰繑鍥炵粨鏋溿€?
     */
    @Override
    @Transactional
    public AlarmEventVO feedback(Long tenantId, Long id, AlarmActionRequest request, String operator) {
        return transition(tenantId, id, AlarmActionType.FEEDBACK, request, operator,
                AlarmStatusTransition::feedbackTarget);
    }

    /**
     * 瀹炵幇鏂瑰紡锛氬叧闂姤璀︼紝鍏堝畬鎴愬繀瑕佺殑鍙傛暟銆佺鎴锋垨鐘舵€佹牎楠岋紝鍐嶅鎵樻寔涔呭寲缁勪欢鎴栬繙绋嬪鎴风澶勭悊骞剁粍瑁呰繑鍥炵粨鏋溿€?
     */
    @Override
    @Transactional
    public AlarmEventVO close(Long tenantId, Long id, AlarmActionRequest request, String operator) {
        return transition(tenantId, id, AlarmActionType.CLOSE, request, operator,
                AlarmStatusTransition::closeTarget);
    }

    /**
     * 瀹炵幇鏂瑰紡锛氳鎶ュ叧闂姤璀︼紝鍏堝畬鎴愬繀瑕佺殑鍙傛暟銆佺鎴锋垨鐘舵€佹牎楠岋紝鍐嶅鎵樻寔涔呭寲缁勪欢鎴栬繙绋嬪鎴风澶勭悊骞剁粍瑁呰繑鍥炵粨鏋溿€?
     */
    @Override
    @Transactional
    public AlarmEventVO falseClose(Long tenantId, Long id, AlarmFalseCloseRequest request, String operator) {
        AlarmEventEntity entity = requireEvent(tenantId, id);
        AlarmStatus current = AlarmStatus.from(entity.getStatus());
        AlarmStatus target = AlarmStatusTransition.falseCloseTarget(current);
        String before = entity.getStatus();
        entity.setStatus(target.name());
        alarmEventMapper.updateById(entity);
        auditSupport.writeAction(tenantId, id, AlarmActionType.FALSE_CLOSE.name(), request.getReason(),
                operator, before, target.name());
        return toVO(entity);
    }

    /**
     * 瀹炵幇鏂瑰紡锛氭鏌ュ尯鍩熸椿璺冩姤璀︼紝鍏堝畬鎴愬繀瑕佺殑鍙傛暟銆佺鎴锋垨鐘舵€佹牎楠岋紝鍐嶅鎵樻寔涔呭寲缁勪欢鎴栬繙绋嬪鎴风澶勭悊骞剁粍瑁呰繑鍥炵粨鏋溿€?
     */
    @Override
    public AlarmAreaActiveCheckVO areaActiveCheck(AlarmAreaActiveCheckRequest request) {
        requireTenantId(request.getTenantId());
        if (request.getAreaId() == null) {
            throw new BusinessException(400, "areaId is required");
        }
        String minLevel = normalizeText(request.getMinLevel());
        if (minLevel == null) {
            minLevel = DEFAULT_MIN_LEVEL;
        }
        List<AlarmEventEntity> active = alarmEventMapper.listActiveByArea(request.getTenantId(), request.getAreaId());
        AlarmAreaActiveCheckVO result = new AlarmAreaActiveCheckVO();
        for (AlarmEventEntity entity : active) {
            if (AlarmLevel.meetsMinLevel(entity.getAlarmLevel(), minLevel)) {
                result.getAlarms().add(toVO(entity));
            }
        }
        result.setCount(result.getAlarms().size());
        result.setHasBlocking(result.getCount() > 0);
        return result;
    }

    @Override
    public RemoteHazardReportVO toHazard(Long tenantId, Long id, AlarmToHazardRequest request) {
        requireTenantId(tenantId);
        if (request == null || !tenantId.equals(request.getTenantId())) {
            throw new BusinessException(400, "tenantId mismatch");
        }
        AlarmEventEntity entity = requireEvent(tenantId, id);

        RemoteHazardCreateRequest createRequest = new RemoteHazardCreateRequest();
        createRequest.setTenantId(tenantId);
        createRequest.setSourceType("ALARM");
        createRequest.setSourceBizId(id);
        createRequest.setAreaId(entity.getAreaId());
        createRequest.setRiskUnitId(entity.getUnitId());
        createRequest.setHazardLevel(StringUtils.hasText(request.getHazardLevel())
                ? request.getHazardLevel().trim() : "MAJOR");
        createRequest.setDescription(resolveHazardDescription(entity, request.getDescription()));

        try {
            return dualPreventionClient.createHazard(createRequest);
        } catch (RuntimeException ex) {
            throw new BusinessException(502, "failed to create hazard from alarm: " + ex.getMessage());
        }
    }

    private String resolveHazardDescription(AlarmEventEntity entity, String override) {
        if (StringUtils.hasText(override)) {
            return override.trim();
        }
        StringBuilder builder = new StringBuilder();
        if (StringUtils.hasText(entity.getTitle())) {
            builder.append(entity.getTitle().trim());
        }
        if (StringUtils.hasText(entity.getContent())) {
            if (builder.length() > 0) {
                builder.append(" - ");
            }
            builder.append(entity.getContent().trim());
        }
        if (builder.length() == 0) {
            return "alarm " + (entity.getAlarmNo() == null ? entity.getId() : entity.getAlarmNo());
        }
        return builder.toString();
    }

    private AlarmEventVO mergeOccurrence(AlarmEventEntity existing, AlarmIngestRequest request, Date occurredAt) {
        int count = existing.getOccurrenceCount() == null ? 1 : existing.getOccurrenceCount().intValue();
        existing.setOccurrenceCount(count + 1);
        existing.setLastOccurredAt(occurredAt);
        if (StringUtils.hasText(request.getContent())) {
            existing.setContent(request.getContent().trim());
        }
        alarmEventMapper.updateById(existing);
        insertOccurrence(request.getTenantId(), existing.getId(), occurredAt, request.getRawValue());
        return toVO(existing);
    }

    private AlarmEventEntity buildDraftEvent(AlarmIngestRequest request, Date occurredAt) {
        AlarmEventEntity entity = new AlarmEventEntity();
        entity.setTenantId(request.getTenantId());
        entity.setAlarmNo(generateAlarmNo());
        entity.setSourceType(normalizeRequired(request.getSourceType(), "sourceType"));
        entity.setSourceCode(normalizeText(request.getSourceCode()));
        entity.setTitle(normalizeRequired(request.getTitle(), "title"));
        entity.setContent(normalizeText(request.getContent()));
        entity.setAlarmLevel(normalizeRequired(request.getAlarmLevel(), "alarmLevel"));
        entity.setStatus(AlarmStatus.NEW.name());
        entity.setAreaId(request.getAreaId());
        entity.setUnitId(request.getUnitId());
        entity.setEquipmentId(request.getEquipmentId());
        entity.setMonitorPointId(request.getMonitorPointId());
        entity.setHazardId(request.getHazardId());
        return entity;
    }

    private void insertOccurrence(Long tenantId, Long alarmEventId, Date occurredAt, String rawValue) {
        AlarmOccurrenceEntity occurrence = new AlarmOccurrenceEntity();
        occurrence.setTenantId(tenantId);
        occurrence.setAlarmEventId(alarmEventId);
        occurrence.setOccurredAt(occurredAt);
        occurrence.setRawValue(normalizeText(rawValue));
        occurrenceMapper.insert(occurrence);
    }

    private AlarmEventVO transition(Long tenantId, Long id, AlarmActionType actionType, AlarmActionRequest request,
                                    String operator, StatusTargetResolver resolver) {
        AlarmEventEntity entity = requireEvent(tenantId, id);
        AlarmStatus current = AlarmStatus.from(entity.getStatus());
        AlarmStatusTransition.assertActionable(current);
        AlarmStatus target = resolver.resolve(current);
        String before = entity.getStatus();
        entity.setStatus(target.name());
        alarmEventMapper.updateById(entity);
        auditSupport.writeAction(tenantId, id, actionType.name(), buildActionContent(actionType, request),
                operator, before, target.name());
        return toVO(entity);
    }

    private String buildActionContent(AlarmActionType actionType, AlarmActionRequest request) {
        if (request == null) {
            return null;
        }
        if (actionType == AlarmActionType.DISPATCH && StringUtils.hasText(request.getAssignee())) {
            String content = normalizeText(request.getContent());
            String assignee = request.getAssignee().trim();
            if (content == null) {
                return "assignee=" + assignee;
            }
            return content + " | assignee=" + assignee;
        }
        return normalizeText(request.getContent());
    }

    private interface StatusTargetResolver {
        AlarmStatus resolve(AlarmStatus current);
    }

    private AlarmEventEntity requireEvent(Long tenantId, Long id) {
        requireTenantId(tenantId);
        if (id == null) {
            throw new BusinessException(400, "alarm id is required");
        }
        AlarmEventEntity entity = alarmEventMapper.selectById(id);
        if (entity == null || entity.getDeleted() != null && entity.getDeleted() != 0
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "alarm not found");
        }
        return entity;
    }

    private String buildDedupKey(AlarmEventEntity entity) {
        String sourceCode = StringUtils.hasText(entity.getSourceCode()) ? entity.getSourceCode().trim() : "-";
        String area = entity.getAreaId() == null ? "-" : String.valueOf(entity.getAreaId());
        return entity.getSourceType() + "|" + sourceCode + "|" + entity.getAlarmLevel() + "|" + area;
    }

    private String generateAlarmNo() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int suffix = (int) (Math.random() * 900 + 100);
        return "ALM-" + timestamp + "-" + suffix;
    }

    private AlarmEventVO toVO(AlarmEventEntity entity) {
        AlarmEventVO vo = new AlarmEventVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setAlarmNo(entity.getAlarmNo());
        vo.setSourceType(entity.getSourceType());
        vo.setSourceCode(entity.getSourceCode());
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setAlarmLevel(entity.getAlarmLevel());
        vo.setStatus(entity.getStatus());
        vo.setAreaId(entity.getAreaId());
        vo.setUnitId(entity.getUnitId());
        vo.setEquipmentId(entity.getEquipmentId());
        vo.setMonitorPointId(entity.getMonitorPointId());
        vo.setHazardId(entity.getHazardId());
        vo.setOccurrenceCount(entity.getOccurrenceCount());
        vo.setFirstOccurredAt(entity.getFirstOccurredAt());
        vo.setLastOccurredAt(entity.getLastOccurredAt());
        return vo;
    }

    private List<AlarmOccurrenceVO> toOccurrenceVOs(List<AlarmOccurrenceEntity> entities) {
        List<AlarmOccurrenceVO> result = new ArrayList<AlarmOccurrenceVO>();
        for (AlarmOccurrenceEntity entity : entities) {
            AlarmOccurrenceVO vo = new AlarmOccurrenceVO();
            vo.setId(entity.getId());
            vo.setOccurredAt(entity.getOccurredAt());
            vo.setRawValue(entity.getRawValue());
            result.add(vo);
        }
        return result;
    }

    private List<AlarmActionSummaryVO> toActionVOs(List<AlarmActionRecordEntity> entities) {
        List<AlarmActionSummaryVO> result = new ArrayList<AlarmActionSummaryVO>();
        for (AlarmActionRecordEntity entity : entities) {
            AlarmActionSummaryVO vo = new AlarmActionSummaryVO();
            vo.setId(entity.getId());
            vo.setActionType(entity.getActionType());
            vo.setActionContent(entity.getActionContent());
            vo.setOperatorName(entity.getOperatorName());
            vo.setOperatedAt(entity.getOperatedAt());
            result.add(vo);
        }
        return result;
    }

    private void requireTenantId(Long tenantId) {
        if (tenantId == null) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    private String normalizeText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String normalizeRequired(String value, String field) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            throw new BusinessException(400, field + " is required");
        }
        return normalized;
    }

    private Page normalizePage(int pageNo, int pageSize) {
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 100);
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

