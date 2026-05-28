package com.fgroupboss.ai.psm.video.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.video.model.dto.VideoCameraRequest;
import com.fgroupboss.ai.psm.video.model.vo.VideoCameraVO;
import com.fgroupboss.ai.psm.video.model.vo.VideoStreamUrlVO;
import com.fgroupboss.ai.psm.video.service.VideoCameraService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * 摄像头台账接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video/cameras")
public class VideoCameraController {

    private final VideoCameraService cameraService;

    @GetMapping
    public ResponseVO<PageResult<VideoCameraVO>> page(@RequestParam Long tenantId,
                                                      @RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) Long areaId,
                                                      @RequestParam(required = false) String status,
                                                      @RequestParam(defaultValue = "1") int pageNo,
                                                      @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(cameraService.page(tenantId, keyword, areaId, status, pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseVO<VideoCameraVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(cameraService.getById(tenantId, id));
    }

    @PostMapping
    public ResponseVO<VideoCameraVO> create(@Valid @RequestBody VideoCameraRequest request) {
        return ResponseVO.success(cameraService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseVO<VideoCameraVO> update(@PathVariable Long id, @Valid @RequestBody VideoCameraRequest request) {
        return ResponseVO.success(cameraService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseVO<Void> delete(@PathVariable Long id, @RequestParam Long tenantId) {
        cameraService.delete(tenantId, id);
        return ResponseVO.success(null);
    }

    @GetMapping("/{id}/live-url")
    public ResponseVO<VideoStreamUrlVO> liveUrl(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(cameraService.liveUrl(tenantId, id));
    }

    @GetMapping("/{id}/playback-url")
    public ResponseVO<VideoStreamUrlVO> playbackUrl(@PathVariable Long id,
                                                    @RequestParam Long tenantId,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startAt,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endAt) {
        return ResponseVO.success(cameraService.playbackUrl(tenantId, id, startAt, endAt));
    }
}
