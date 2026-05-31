package com.fgroupboss.ai.psm.realtime.location.service.impl;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.realtime.location.client.AlarmServiceClient;
import com.fgroupboss.ai.psm.realtime.location.client.dto.AlarmIngestPayload;
import com.fgroupboss.ai.psm.realtime.location.config.LocationConstants;
import com.fgroupboss.ai.psm.realtime.location.mapper.LocEventMapper;
import com.fgroupboss.ai.psm.realtime.location.mapper.LocRealtimeMapper;
import com.fgroupboss.ai.psm.realtime.location.mapper.LocTagBindingMapper;
import com.fgroupboss.ai.psm.realtime.location.mapper.LocTrackPointMapper;
import com.fgroupboss.ai.psm.realtime.location.model.dto.LocEventIngestRequest;
import com.fgroupboss.ai.psm.realtime.location.model.entity.LocEventEntity;
import com.fgroupboss.ai.psm.realtime.location.model.entity.LocRealtimeEntity;
import com.fgroupboss.ai.psm.realtime.location.model.entity.LocTagBindingEntity;
import com.fgroupboss.ai.psm.realtime.location.model.entity.LocTrackPointEntity;
import com.fgroupboss.ai.psm.realtime.location.model.vo.LocEventVO;
import com.fgroupboss.ai.psm.realtime.location.service.LocEventService;
import com.fgroupboss.ai.psm.realtime.location.support.LocationAuditSupport;
import com.fgroupboss.ai.psm.realtime.location.support.LocationSupport;
import com.fgroupboss.ai.psm.realtime.location.support.LocationSupport.PageSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocEventServiceImpl implements LocEventService {

    private final LocEventMapper eventMapper;
    private final LocRealtimeMapper realtimeMapper;
    private final LocTrackPointMapper trackPointMapper;
    private final LocTagBindingMapper bindingMapper;
    private final AlarmServiceClient alarmServiceClient;
    private final LocationAuditSupport auditSupport;

    @Override
    @Transactional
    public LocEventVO ingest(LocEventIngestRequest request) {
        LocationSupport.requireTenantId(request.getTenantId());
        LocalDateTime eventTime = request.getEventTime() == null ? LocalDateTime.now() : request.getEventTime();
        Long personId = resolvePersonId(request);
        LocEventEntity entity = new LocEventEntity();
        entity.setTenantId(request.getTenantId());
        entity.setEventType(request.getEventType().trim());
        entity.setTagNo(LocationSupport.normalizeText(request.getTagNo()));
        entity.setPersonId(personId);
        entity.setAreaId(request.getAreaId());
        entity.setFenceId(request.getFenceId());
        entity.setEventTime(eventTime);
        entity.setWorkPermitId(request.getWorkPermitId());
        entity.setRawPayload(request.getRawPayload());
        eventMapper.insert(entity);
        upsertRealtime(request, personId, eventTime);
        appendTrackIfPositioned(request, personId, eventTime);
        Long alarmId = forwardAlarmIfNeeded(request, entity, eventTime);
        if (alarmId != null) {
            entity.setAlarmId(alarmId);
            eventMapper.updateById(entity);
        }
        auditSupport.append(request.getTenantId(), entity.getId(), "INGEST", entity.getEventType());
        return toVO(entity);
    }

    @Override
    public PageResult<LocEventVO> page(Long tenantId, String eventType, String tagNo, Long personId,
                                       LocalDateTime fromTime, LocalDateTime toTime, int pageNo, int pageSize) {
        LocationSupport.requireTenantId(tenantId);
        PageSpec page = LocationSupport.normalizePage(pageNo, pageSize);
        String normalizedEventType = LocationSupport.normalizeText(eventType);
        String normalizedTagNo = LocationSupport.normalizeText(tagNo);
        long total = eventMapper.countByTenant(tenantId, normalizedEventType, normalizedTagNo, personId, fromTime, toTime);
        List<LocEventEntity> entities = total == 0
                ? new ArrayList<LocEventEntity>()
                : eventMapper.listByTenant(tenantId, normalizedEventType, normalizedTagNo, personId,
                fromTime, toTime, page.getOffset(), page.getPageSize());
        List<LocEventVO> records = new ArrayList<LocEventVO>();
        for (LocEventEntity entity : entities) {
            records.add(toVO(entity));
        }
        return new PageResult<LocEventVO>(total, page.getPageNo(), page.getPageSize(), records);
    }

    private Long resolvePersonId(LocEventIngestRequest request) {
        if (request.getPersonId() != null) {
            return request.getPersonId();
        }
        if (!StringUtils.hasText(request.getTagNo())) {
            return null;
        }
        LocTagBindingEntity binding = bindingMapper.findActiveByTagNo(request.getTenantId(), request.getTagNo().trim());
        return binding == null ? null : binding.getPersonId();
    }

    private void upsertRealtime(LocEventIngestRequest request, Long personId, LocalDateTime eventTime) {
        if (!StringUtils.hasText(request.getTagNo())) {
            return;
        }
        String online = StringUtils.hasText(request.getOnlineStatus())
                ? request.getOnlineStatus().trim()
                : ("OFFLINE".equals(request.getEventType()) ? LocationConstants.OFFLINE : LocationConstants.ONLINE);
        LocRealtimeEntity existing = realtimeMapper.findByTagNo(request.getTenantId(), request.getTagNo().trim());
        if (existing == null) {
            LocRealtimeEntity created = new LocRealtimeEntity();
            created.setTenantId(request.getTenantId());
            created.setTagNo(request.getTagNo().trim());
            created.setPersonId(personId);
            created.setAreaId(request.getAreaId());
            created.setLatitude(request.getLatitude());
            created.setLongitude(request.getLongitude());
            created.setOnlineStatus(online);
            created.setLastSeenAt(eventTime);
            realtimeMapper.insert(created);
            return;
        }
        existing.setPersonId(personId);
        if (request.getAreaId() != null) {
            existing.setAreaId(request.getAreaId());
        }
        if (request.getLatitude() != null) {
            existing.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            existing.setLongitude(request.getLongitude());
        }
        existing.setOnlineStatus(online);
        existing.setLastSeenAt(eventTime);
        realtimeMapper.updateById(existing);
    }

    private void appendTrackIfPositioned(LocEventIngestRequest request, Long personId, LocalDateTime eventTime) {
        if (request.getLatitude() == null && request.getLongitude() == null) {
            return;
        }
        if (!StringUtils.hasText(request.getTagNo())) {
            return;
        }
        LocTrackPointEntity point = new LocTrackPointEntity();
        point.setTenantId(request.getTenantId());
        point.setTagNo(request.getTagNo().trim());
        point.setPersonId(personId);
        point.setAreaId(request.getAreaId());
        point.setLatitude(request.getLatitude());
        point.setLongitude(request.getLongitude());
        point.setRecordedAt(eventTime);
        trackPointMapper.insert(point);
    }

    private Long forwardAlarmIfNeeded(LocEventIngestRequest request, LocEventEntity entity, LocalDateTime eventTime) {
        if (!LocationConstants.isAlarmBridgeEvent(entity.getEventType())) {
            return null;
        }
        AlarmIngestPayload payload = new AlarmIngestPayload();
        payload.setTenantId(request.getTenantId());
        payload.setSourceType(LocationConstants.ALARM_SOURCE_TYPE);
        payload.setSourceCode(String.valueOf(entity.getId()));
        payload.setTitle(buildAlarmTitle(entity.getEventType()));
        payload.setContent(buildAlarmContent(request));
        payload.setAlarmLevel(resolveAlarmLevel(entity.getEventType()));
        payload.setAreaId(request.getAreaId());
        payload.setOccurredAt(Date.from(eventTime.atZone(ZoneId.systemDefault()).toInstant()));
        return alarmServiceClient.ingest(payload);
    }

    private String buildAlarmTitle(String eventType) {
        if ("FORBIDDEN_ENTER".equals(eventType)) {
            return "禁入区闯入";
        }
        if ("OVERCAPACITY".equals(eventType)) {
            return "区域超员";
        }
        if ("SOS".equals(eventType)) {
            return "SOS紧急求救";
        }
        return "定位告警";
    }

    private String buildAlarmContent(LocEventIngestRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append("eventType=").append(request.getEventType());
        if (StringUtils.hasText(request.getTagNo())) {
            builder.append(", tagNo=").append(request.getTagNo());
        }
        if (request.getPersonId() != null) {
            builder.append(", personId=").append(request.getPersonId());
        }
        if (request.getAreaId() != null) {
            builder.append(", areaId=").append(request.getAreaId());
        }
        return builder.toString();
    }

    private String resolveAlarmLevel(String eventType) {
        if ("SOS".equals(eventType)) {
            return "CRITICAL";
        }
        return "HIGH";
    }

    private LocEventVO toVO(LocEventEntity entity) {
        LocEventVO vo = new LocEventVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setEventType(entity.getEventType());
        vo.setTagNo(entity.getTagNo());
        vo.setPersonId(entity.getPersonId());
        vo.setAreaId(entity.getAreaId());
        vo.setFenceId(entity.getFenceId());
        vo.setEventTime(entity.getEventTime());
        vo.setAlarmId(entity.getAlarmId());
        vo.setWorkPermitId(entity.getWorkPermitId());
        vo.setRawPayload(entity.getRawPayload());
        return vo;
    }
}
