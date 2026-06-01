package com.fgroupboss.ai.psm.realtime.video.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.realtime.video.model.dto.VideoCameraRequest;
import com.fgroupboss.ai.psm.realtime.video.model.vo.VideoCameraVO;
import com.fgroupboss.ai.psm.realtime.video.model.vo.VideoStreamUrlVO;
import com.fgroupboss.ai.psm.realtime.video.service.VideoCameraService;
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
 * VideoCamera 模块 HTTP API。
 * <p>基础路径：{@code /api/video/cameras}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video/cameras")
public class VideoCameraController {

    private final VideoCameraService cameraService;

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/video/cameras}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param areaId 区域 ID
     * @param status 业务状态筛选
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<VideoCameraVO>> page(@RequestParam Long tenantId,
                                                      @RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) Long areaId,
                                                      @RequestParam(required = false) String status,
                                                      @RequestParam(defaultValue = "1") int pageNo,
                                                      @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(cameraService.page(tenantId, keyword, areaId, status, pageNo, pageSize));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/video/cameras/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<VideoCameraVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(cameraService.getById(tenantId, id));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/video/cameras}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<VideoCameraVO> create(@Valid @RequestBody VideoCameraRequest request) {
        return ResponseVO.success(cameraService.create(request));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/video/cameras/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}")
    public ResponseVO<VideoCameraVO> update(@PathVariable Long id, @Valid @RequestBody VideoCameraRequest request) {
        return ResponseVO.success(cameraService.update(id, request));
    }

    /**
     * 删除记录。
     * <p>HTTP DELETE {@code /api/video/cameras/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/{id}")
    public ResponseVO<Void> delete(@PathVariable Long id, @RequestParam Long tenantId) {
        cameraService.delete(tenantId, id);
        return ResponseVO.success(null);
    }

    /**
     * 查询live url。
     * <p>HTTP GET {@code /api/video/cameras/{id}/live-url}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/live-url")
    public ResponseVO<VideoStreamUrlVO> liveUrl(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(cameraService.liveUrl(tenantId, id));
    }

    /**
     * 查询playback url。
     * <p>HTTP GET {@code /api/video/cameras/{id}/playback-url}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @param startAt startAt 参数
     * @param endAt endAt 参数
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/playback-url")
    public ResponseVO<VideoStreamUrlVO> playbackUrl(@PathVariable Long id,
                                                    @RequestParam Long tenantId,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startAt,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endAt) {
        return ResponseVO.success(cameraService.playbackUrl(tenantId, id, startAt, endAt));
    }
}
