package com.fgroupboss.ai.psm.realtime.video.service.impl;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.realtime.video.mapper.VideoCameraMapper;
import com.fgroupboss.ai.psm.realtime.video.model.dto.VideoCameraRequest;
import com.fgroupboss.ai.psm.realtime.video.model.entity.VideoCameraEntity;
import com.fgroupboss.ai.psm.realtime.video.model.vo.VideoCameraVO;
import com.fgroupboss.ai.psm.realtime.video.model.vo.VideoStreamUrlVO;
import com.fgroupboss.ai.psm.realtime.video.service.VideoCameraService;
import com.fgroupboss.ai.psm.realtime.video.service.support.VideoServiceSupport;
import com.fgroupboss.ai.psm.realtime.video.service.support.VideoServiceSupport.Page;
import com.fgroupboss.ai.psm.realtime.video.service.support.VideoStreamUrlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 摄像头台账业务实现。
 */
@Service
@RequiredArgsConstructor
public class VideoCameraServiceImpl implements VideoCameraService {

    private final VideoCameraMapper cameraMapper;
    private final VideoStreamUrlBuilder streamUrlBuilder;

    @Override
    public PageResult<VideoCameraVO> page(Long tenantId, String keyword, Long areaId, String status,
                                          int pageNo, int pageSize) {
        VideoServiceSupport.requireTenantId(tenantId);
        Page page = VideoServiceSupport.normalizePage(pageNo, pageSize);
        String normalizedKeyword = VideoServiceSupport.normalizeText(keyword);
        String normalizedStatus = VideoServiceSupport.normalizeText(status);
        long total = cameraMapper.countByTenant(tenantId, normalizedKeyword, areaId, normalizedStatus);
        if (total == 0) {
            return VideoServiceSupport.emptyPage(page);
        }
        List<VideoCameraEntity> entities = cameraMapper.listByTenant(tenantId, normalizedKeyword, areaId,
                normalizedStatus, page.offset, page.pageSize);
        List<VideoCameraVO> records = new ArrayList<VideoCameraVO>();
        for (VideoCameraEntity entity : entities) {
            records.add(toVO(entity));
        }
        return new PageResult<VideoCameraVO>(total, page.pageNo, page.pageSize, records);
    }

    @Override
    public VideoCameraVO getById(Long tenantId, Long id) {
        return toVO(requireCamera(tenantId, id));
    }

    @Override
    @Transactional
    public VideoCameraVO create(VideoCameraRequest request) {
        VideoServiceSupport.requireTenantId(request.getTenantId());
        assertCodeUnique(request.getTenantId(), request.getCameraCode(), null);
        VideoCameraEntity entity = new VideoCameraEntity();
        entity.setTenantId(request.getTenantId());
        applyRequest(entity, request);
        entity.setStatus(defaultStatus(request.getStatus()));
        entity.setDeleted(0);
        cameraMapper.insert(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public VideoCameraVO update(Long id, VideoCameraRequest request) {
        VideoCameraEntity entity = requireCamera(request.getTenantId(), id);
        assertCodeUnique(request.getTenantId(), request.getCameraCode(), id);
        applyRequest(entity, request);
        if (StringUtils.hasText(request.getStatus())) {
            entity.setStatus(request.getStatus().trim());
        }
        cameraMapper.updateById(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public void delete(Long tenantId, Long id) {
        VideoCameraEntity entity = requireCamera(tenantId, id);
        entity.setDeleted(1);
        cameraMapper.updateById(entity);
    }

    @Override
    public VideoStreamUrlVO liveUrl(Long tenantId, Long id) {
        VideoCameraEntity camera = requireCamera(tenantId, id);
        return streamUrlBuilder.buildLiveUrl(camera);
    }

    @Override
    public VideoStreamUrlVO playbackUrl(Long tenantId, Long id, LocalDateTime startAt, LocalDateTime endAt) {
        VideoCameraEntity camera = requireCamera(tenantId, id);
        return streamUrlBuilder.buildPlaybackUrl(camera, startAt, endAt);
    }

    private void applyRequest(VideoCameraEntity entity, VideoCameraRequest request) {
        entity.setCameraCode(request.getCameraCode().trim());
        entity.setCameraName(request.getCameraName().trim());
        entity.setPlatformCode(request.getPlatformCode().trim());
        entity.setPlatformCameraId(request.getPlatformCameraId());
        entity.setAreaId(request.getAreaId());
        entity.setMajorHazardId(request.getMajorHazardId());
        entity.setLocationDesc(request.getLocationDesc());
    }

    private String defaultStatus(String status) {
        return StringUtils.hasText(status) ? status.trim() : "ENABLED";
    }

    private void assertCodeUnique(Long tenantId, String cameraCode, Long excludeId) {
        VideoCameraEntity existing = cameraMapper.findByCode(tenantId, cameraCode.trim());
        if (existing != null && (excludeId == null || !excludeId.equals(existing.getId()))) {
            throw new BusinessException(400, "camera code already exists: " + cameraCode);
        }
    }

    VideoCameraEntity requireCamera(Long tenantId, Long id) {
        VideoServiceSupport.requireTenantId(tenantId);
        VideoCameraEntity entity = cameraMapper.selectById(id);
        if (entity == null || (entity.getDeleted() != null && entity.getDeleted() == 1)
                || !tenantId.equals(entity.getTenantId())) {
            throw new BusinessException(404, "camera not found");
        }
        return entity;
    }

    private VideoCameraVO toVO(VideoCameraEntity entity) {
        VideoCameraVO vo = new VideoCameraVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setCameraCode(entity.getCameraCode());
        vo.setCameraName(entity.getCameraName());
        vo.setPlatformCode(entity.getPlatformCode());
        vo.setPlatformCameraId(entity.getPlatformCameraId());
        vo.setAreaId(entity.getAreaId());
        vo.setMajorHazardId(entity.getMajorHazardId());
        vo.setLocationDesc(entity.getLocationDesc());
        vo.setStatus(entity.getStatus());
        return vo;
    }
}
