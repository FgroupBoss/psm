package com.fgroupboss.ai.psm.realtime.video.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.realtime.video.client.AlarmServiceClient;
import com.fgroupboss.ai.psm.realtime.video.config.AiEventStatus;
import com.fgroupboss.ai.psm.realtime.video.config.VideoPlatformProperties;
import com.fgroupboss.ai.psm.realtime.video.mapper.VideoAiEventMapper;
import com.fgroupboss.ai.psm.realtime.video.mapper.VideoAiEventMediaMapper;
import com.fgroupboss.ai.psm.realtime.video.model.dto.AiEventIgnoreRequest;
import com.fgroupboss.ai.psm.realtime.video.model.dto.AiEventIngestRequest;
import com.fgroupboss.ai.psm.realtime.video.model.dto.AiEventMediaItem;
import com.fgroupboss.ai.psm.realtime.video.model.entity.VideoAiEventEntity;
import com.fgroupboss.ai.psm.realtime.video.model.entity.VideoAiEventMediaEntity;
import com.fgroupboss.ai.psm.realtime.video.model.entity.VideoCameraEntity;
import com.fgroupboss.ai.psm.realtime.video.model.vo.VideoAiEventMediaVO;
import com.fgroupboss.ai.psm.realtime.video.model.vo.VideoAiEventVO;
import com.fgroupboss.ai.psm.realtime.video.model.vo.VideoCameraVO;
import com.fgroupboss.ai.psm.realtime.video.service.VideoAiEventService;
import com.fgroupboss.ai.psm.realtime.video.service.VideoCameraService;
import com.fgroupboss.ai.psm.realtime.video.support.VideoAuditSupport;
import com.fgroupboss.ai.psm.realtime.video.service.support.VideoServiceSupport;
import com.fgroupboss.ai.psm.realtime.video.service.support.VideoServiceSupport.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * AI 视频事件接入与处置实现。
 */
@Service
@RequiredArgsConstructor
public class VideoAiEventServiceImpl implements VideoAiEventService {

    private final VideoAiEventMapper eventMapper;
    private final VideoAiEventMediaMapper mediaMapper;
    private final VideoCameraService cameraService;
    private final AlarmServiceClient alarmServiceClient;
    private final VideoPlatformProperties platformProperties;
    private final VideoAuditSupport auditSupport;

    @Override
    @Transactional
    public VideoAiEventVO ingest(AiEventIngestRequest request) {
        VideoServiceSupport.requireTenantId(request.getTenantId());
        VideoCameraVO cameraVo = cameraService.getById(request.getTenantId(), request.getCameraId());
        VideoCameraEntity camera = toCameraEntity(cameraVo);
        String dedupKey = buildDedupKey(request);
        VideoAiEventEntity duplicate = findDuplicate(request.getTenantId(), dedupKey);
        if (duplicate != null) {
            return toVO(duplicate, loadMedia(duplicate));
        }

        VideoAiEventEntity entity = new VideoAiEventEntity();
        entity.setTenantId(request.getTenantId());
        entity.setEventNo(generateEventNo());
        entity.setSourcePlatform(request.getSourcePlatform().trim());
        entity.setCameraId(request.getCameraId());
        entity.setEventType(request.getEventType().trim().toUpperCase());
        entity.setEventTime(request.getEventTime());
        entity.setAreaId(firstNonNull(request.getAreaId(), camera.getAreaId()));
        entity.setMajorHazardId(firstNonNull(request.getMajorHazardId(), camera.getMajorHazardId()));
        entity.setWorkPermitId(request.getWorkPermitId());
        entity.setSeverity(request.getSeverity().trim().toUpperCase());
        entity.setStatus(AiEventStatus.NEW);
        entity.setTitle(resolveTitle(request));
        entity.setDescription(request.getDescription());
        entity.setDedupKey(dedupKey);
        entity.setDeleted(0);
        eventMapper.insert(entity);
        saveMedia(request.getTenantId(), entity.getId(), request.getMediaItems());

        if (platformProperties.shouldAutoAlarm(entity.getSeverity())) {
            forwardToAlarm(entity);
        }
        auditSupport.append(request.getTenantId(), entity.getId(), "INGEST", null, entity.getStatus(), "system");
        return toVO(entity, loadMedia(entity));
    }

    @Override
    public PageResult<VideoAiEventVO> page(Long tenantId, Long cameraId, String eventType, String status,
                                           String severity, Long areaId, LocalDateTime occurredFrom,
                                           LocalDateTime occurredTo, int pageNo, int pageSize) {
        VideoServiceSupport.requireTenantId(tenantId);
        Page page = VideoServiceSupport.normalizePage(pageNo, pageSize);
        String normalizedType = normalizeUpper(eventType);
        String normalizedStatus = VideoServiceSupport.normalizeText(status);
        String normalizedSeverity = normalizeUpper(severity);
        long total = eventMapper.countByTenant(tenantId, cameraId, normalizedType, normalizedStatus,
                normalizedSeverity, areaId, occurredFrom, occurredTo);
        if (total == 0) {
            return VideoServiceSupport.emptyPage(page);
        }
        List<VideoAiEventEntity> entities = eventMapper.listByTenant(tenantId, cameraId, normalizedType,
                normalizedStatus, normalizedSeverity, areaId, occurredFrom, occurredTo, page.offset, page.pageSize);
        List<VideoAiEventVO> records = new ArrayList<VideoAiEventVO>();
        for (VideoAiEventEntity entity : entities) {
            records.add(toVO(entity, loadMedia(entity)));
        }
        return new PageResult<VideoAiEventVO>(total, page.pageNo, page.pageSize, records);
    }

    @Override
    public VideoAiEventVO getById(Long tenantId, Long id) {
        VideoAiEventEntity entity = requireEvent(tenantId, id);
        return toVO(entity, loadMedia(entity));
    }

    @Override
    @Transactional
    public VideoAiEventVO toAlarm(Long tenantId, Long id) {
        VideoAiEventEntity entity = requireEvent(tenantId, id);
        String before = entity.getStatus();
        AiEventStatus.assertCanAlarm(entity.getStatus());
        forwardToAlarm(entity);
        eventMapper.updateById(entity);
        auditSupport.append(tenantId, id, "TO_ALARM", before, entity.getStatus(), "system");
        return toVO(entity, loadMedia(entity));
    }

    @Override
    @Transactional
    public VideoAiEventVO ignore(Long tenantId, Long id, AiEventIgnoreRequest request, String operator) {
        VideoAiEventEntity entity = requireEvent(tenantId, id);
        String before = entity.getStatus();
        AiEventStatus.assertCanIgnore(entity.getStatus());
        entity.setStatus(AiEventStatus.IGNORED);
        entity.setIgnoreReason(request.getReason().trim());
        eventMapper.updateById(entity);
        auditSupport.append(tenantId, id, "IGNORE", before, entity.getStatus(), operator);
        return toVO(entity, loadMedia(entity));
    }

    private void forwardToAlarm(VideoAiEventEntity entity) {
        Long alarmId = alarmServiceClient.forwardAiEvent(entity);
        if (alarmId != null) {
            entity.setAlarmId(alarmId);
            entity.setStatus(AiEventStatus.ALARMED);
        }
    }

    private VideoAiEventEntity findDuplicate(Long tenantId, String dedupKey) {
        if (!StringUtils.hasText(dedupKey)) {
            return null;
        }
        LocalDateTime since = LocalDateTime.now().minusMinutes(platformProperties.getIngestDedupMinutes());
        return eventMapper.findRecentByDedupKey(tenantId, dedupKey, since);
    }

    private String buildDedupKey(AiEventIngestRequest request) {
        if (StringUtils.hasText(request.getExternalEventId())) {
            return request.getSourcePlatform().trim() + ":" + request.getExternalEventId().trim();
        }
        return request.getCameraId() + ":" + request.getEventType().trim().toUpperCase();
    }

    private void saveMedia(Long tenantId, Long eventId, List<AiEventMediaItem> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        for (AiEventMediaItem item : items) {
            VideoAiEventMediaEntity media = new VideoAiEventMediaEntity();
            media.setTenantId(tenantId);
            media.setEventId(eventId);
            media.setMediaType(item.getMediaType().trim().toUpperCase());
            media.setFileId(item.getFileId());
            media.setMediaUrl(item.getMediaUrl());
            media.setDeleted(0);
            mediaMapper.insert(media);
        }
    }

    private List<VideoAiEventMediaVO> loadMedia(VideoAiEventEntity entity) {
        List<VideoAiEventMediaEntity> entities = mediaMapper.listByEventId(entity.getTenantId(), entity.getId());
        List<VideoAiEventMediaVO> result = new ArrayList<VideoAiEventMediaVO>();
        for (VideoAiEventMediaEntity media : entities) {
            VideoAiEventMediaVO vo = new VideoAiEventMediaVO();
            vo.setId(media.getId());
            vo.setMediaType(media.getMediaType());
            vo.setFileId(media.getFileId());
            vo.setMediaUrl(media.getMediaUrl());
            result.add(vo);
        }
        return result;
    }

    private VideoAiEventEntity requireEvent(Long tenantId, Long id) {
        VideoServiceSupport.requireTenantId(tenantId);
        VideoAiEventEntity entity = eventMapper.selectById(id);
        if (entity == null || (entity.getDeleted() != null && entity.getDeleted() == 1)
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "ai event not found");
        }
        return entity;
    }

    private String generateEventNo() {
        return "VE-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String resolveTitle(AiEventIngestRequest request) {
        if (StringUtils.hasText(request.getTitle())) {
            return request.getTitle().trim();
        }
        return request.getEventType().trim();
    }

    private Long firstNonNull(Long primary, Long fallback) {
        return primary != null ? primary : fallback;
    }

    private String normalizeUpper(String value) {
        return StringUtils.hasText(value) ? value.trim().toUpperCase() : null;
    }

    private VideoCameraEntity toCameraEntity(VideoCameraVO vo) {
        VideoCameraEntity entity = new VideoCameraEntity();
        entity.setId(vo.getId());
        entity.setTenantId(vo.getTenantId());
        entity.setAreaId(vo.getAreaId());
        entity.setMajorHazardId(vo.getMajorHazardId());
        return entity;
    }

    private VideoAiEventVO toVO(VideoAiEventEntity entity, List<VideoAiEventMediaVO> mediaItems) {
        VideoAiEventVO vo = new VideoAiEventVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setEventNo(entity.getEventNo());
        vo.setSourcePlatform(entity.getSourcePlatform());
        vo.setCameraId(entity.getCameraId());
        vo.setEventType(entity.getEventType());
        vo.setEventTime(entity.getEventTime());
        vo.setAreaId(entity.getAreaId());
        vo.setMajorHazardId(entity.getMajorHazardId());
        vo.setAlarmId(entity.getAlarmId());
        vo.setWorkPermitId(entity.getWorkPermitId());
        vo.setSeverity(entity.getSeverity());
        vo.setStatus(entity.getStatus());
        vo.setTitle(entity.getTitle());
        vo.setDescription(entity.getDescription());
        vo.setIgnoreReason(entity.getIgnoreReason());
        vo.setMediaItems(mediaItems);
        return vo;
    }
}
