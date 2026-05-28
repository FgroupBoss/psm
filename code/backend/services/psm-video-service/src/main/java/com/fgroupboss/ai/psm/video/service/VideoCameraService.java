package com.fgroupboss.ai.psm.video.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.video.model.dto.VideoCameraRequest;
import com.fgroupboss.ai.psm.video.model.vo.VideoCameraVO;
import com.fgroupboss.ai.psm.video.model.vo.VideoStreamUrlVO;

import java.time.LocalDateTime;

public interface VideoCameraService {

    PageResult<VideoCameraVO> page(Long tenantId, String keyword, Long areaId, String status, int pageNo, int pageSize);

    VideoCameraVO getById(Long tenantId, Long id);

    VideoCameraVO create(VideoCameraRequest request);

    VideoCameraVO update(Long id, VideoCameraRequest request);

    void delete(Long tenantId, Long id);

    VideoStreamUrlVO liveUrl(Long tenantId, Long id);

    VideoStreamUrlVO playbackUrl(Long tenantId, Long id, LocalDateTime startAt, LocalDateTime endAt);
}
