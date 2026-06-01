package com.fgroupboss.ai.psm.realtime.video.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.realtime.video.config.WatchSessionStatus;
import com.fgroupboss.ai.psm.realtime.video.mapper.VideoWatchCaptureMapper;
import com.fgroupboss.ai.psm.realtime.video.mapper.VideoWatchSessionMapper;
import com.fgroupboss.ai.psm.realtime.video.model.dto.WatchCaptureRequest;
import com.fgroupboss.ai.psm.realtime.video.model.dto.WatchSessionCloseRequest;
import com.fgroupboss.ai.psm.realtime.video.model.dto.WatchSessionCreateRequest;
import com.fgroupboss.ai.psm.realtime.video.model.entity.VideoWatchCaptureEntity;
import com.fgroupboss.ai.psm.realtime.video.model.entity.VideoWatchSessionEntity;
import com.fgroupboss.ai.psm.realtime.video.model.vo.VideoWatchCaptureVO;
import com.fgroupboss.ai.psm.realtime.video.model.vo.VideoWatchSessionVO;
import com.fgroupboss.ai.psm.realtime.video.service.VideoCameraService;
import com.fgroupboss.ai.psm.realtime.video.service.VideoWatchSessionService;
import com.fgroupboss.ai.psm.realtime.video.service.support.VideoServiceSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业过程视频监护会话实现。
 */
@Service
@RequiredArgsConstructor
public class VideoWatchSessionServiceImpl implements VideoWatchSessionService {

    private final VideoWatchSessionMapper sessionMapper;
    private final VideoWatchCaptureMapper captureMapper;
    private final VideoCameraService cameraService;

    @Override
    @Transactional
    public VideoWatchSessionVO create(WatchSessionCreateRequest request) {
        VideoServiceSupport.requireTenantId(request.getTenantId());
        assertCamerasExist(request.getTenantId(), request.getCameraIds());
        VideoWatchSessionEntity active = sessionMapper.findActiveByPermit(request.getTenantId(), request.getWorkPermitId());
        if (active != null) {
            throw new BusinessException(400, "active watch session already exists for work permit: " + request.getWorkPermitId());
        }
        VideoWatchSessionEntity entity = new VideoWatchSessionEntity();
        entity.setTenantId(request.getTenantId());
        entity.setWorkPermitId(request.getWorkPermitId());
        entity.setCameraIds(VideoServiceSupport.joinCameraIds(request.getCameraIds()));
        entity.setOperatorId(request.getOperatorId());
        entity.setOperatorName(request.getOperatorName());
        entity.setStatus(WatchSessionStatus.ACTIVE);
        entity.setStartedAt(LocalDateTime.now());
        entity.setRemark(request.getRemark());
        entity.setDeleted(0);
        sessionMapper.insert(entity);
        return toVO(entity);
    }

    @Override
    public VideoWatchSessionVO getById(Long tenantId, Long id) {
        return toVO(requireSession(tenantId, id));
    }

    @Override
    @Transactional
    public VideoWatchCaptureVO capture(Long sessionId, WatchCaptureRequest request) {
        VideoWatchSessionEntity session = requireSession(request.getTenantId(), sessionId);
        WatchSessionStatus.assertActive(session.getStatus());
        if (request.getCameraId() != null) {
            cameraService.getById(request.getTenantId(), request.getCameraId());
        }
        VideoWatchCaptureEntity capture = new VideoWatchCaptureEntity();
        capture.setTenantId(request.getTenantId());
        capture.setSessionId(sessionId);
        capture.setCameraId(request.getCameraId());
        capture.setCaptureType(request.getCaptureType().trim().toUpperCase());
        capture.setFileId(request.getFileId());
        capture.setMediaUrl(request.getMediaUrl());
        capture.setCapturedAt(request.getCapturedAt() != null ? request.getCapturedAt() : LocalDateTime.now());
        capture.setRemark(request.getRemark());
        capture.setDeleted(0);
        captureMapper.insert(capture);
        return toCaptureVO(capture);
    }

    @Override
    @Transactional
    public VideoWatchSessionVO close(Long sessionId, WatchSessionCloseRequest request) {
        VideoWatchSessionEntity session = requireSession(request.getTenantId(), sessionId);
        WatchSessionStatus.assertActive(session.getStatus());
        session.setStatus(WatchSessionStatus.CLOSED);
        session.setEndedAt(LocalDateTime.now());
        if (StringUtils.hasText(request.getRemark())) {
            session.setRemark(request.getRemark().trim());
        }
        sessionMapper.updateById(session);
        return toVO(session);
    }

    private void assertCamerasExist(Long tenantId, List<Long> cameraIds) {
        for (Long cameraId : cameraIds) {
            cameraService.getById(tenantId, cameraId);
        }
    }

    private VideoWatchSessionEntity requireSession(Long tenantId, Long id) {
        VideoServiceSupport.requireTenantId(tenantId);
        VideoWatchSessionEntity entity = sessionMapper.selectById(id);
        if (entity == null || (entity.getDeleted() != null && entity.getDeleted() == 1)
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "watch session not found");
        }
        return entity;
    }

    private VideoWatchSessionVO toVO(VideoWatchSessionEntity entity) {
        VideoWatchSessionVO vo = new VideoWatchSessionVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setWorkPermitId(entity.getWorkPermitId());
        vo.setCameraIds(VideoServiceSupport.parseCameraIds(entity.getCameraIds()));
        vo.setOperatorId(entity.getOperatorId());
        vo.setOperatorName(entity.getOperatorName());
        vo.setStatus(entity.getStatus());
        vo.setStartedAt(entity.getStartedAt());
        vo.setEndedAt(entity.getEndedAt());
        vo.setRemark(entity.getRemark());
        return vo;
    }

    private VideoWatchCaptureVO toCaptureVO(VideoWatchCaptureEntity entity) {
        VideoWatchCaptureVO vo = new VideoWatchCaptureVO();
        vo.setId(entity.getId());
        vo.setSessionId(entity.getSessionId());
        vo.setCameraId(entity.getCameraId());
        vo.setCaptureType(entity.getCaptureType());
        vo.setFileId(entity.getFileId());
        vo.setMediaUrl(entity.getMediaUrl());
        vo.setCapturedAt(entity.getCapturedAt());
        vo.setRemark(entity.getRemark());
        return vo;
    }
}
