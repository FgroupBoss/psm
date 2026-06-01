package com.fgroupboss.ai.psm.realtime.location.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.realtime.location.model.dto.LocTagBindRequest;
import com.fgroupboss.ai.psm.realtime.location.model.dto.LocTagRequest;
import com.fgroupboss.ai.psm.realtime.location.model.vo.LocTagBindingVO;
import com.fgroupboss.ai.psm.realtime.location.model.vo.LocTagVO;
import com.fgroupboss.ai.psm.realtime.location.service.LocTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * LocTag 模块 HTTP API。
 * <p>人员/车辆/访客定位与出入记录。</p>
 * <p>基础路径：{@code /api/location/tags}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location/tags")
public class LocTagController {

    private final LocTagService tagService;

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/location/tags}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param status 业务状态筛选
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<LocTagVO>> page(@RequestParam Long tenantId,
                                                 @RequestParam(required = false) String keyword,
                                                 @RequestParam(required = false) String status,
                                                 @RequestParam(defaultValue = "1") int pageNo,
                                                 @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(tagService.page(tenantId, keyword, status, pageNo, pageSize));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/location/tags/{tagNo}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tagNo tagNo 参数
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{tagNo}")
    public ResponseVO<LocTagVO> get(@PathVariable String tagNo, @RequestParam Long tenantId) {
        return ResponseVO.success(tagService.getByTagNo(tenantId, tagNo));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/location/tags}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<LocTagVO> create(@Valid @RequestBody LocTagRequest request) {
        return ResponseVO.success(tagService.create(request));
    }

    /**
     * 新增bind或触发bind相关动作。
     * <p>HTTP POST {@code /api/location/tags/{tagNo}/bind}</p>
     * @param tagNo tagNo 参数
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{tagNo}/bind")
    public ResponseVO<LocTagBindingVO> bind(@PathVariable String tagNo,
                                            @Valid @RequestBody LocTagBindRequest request) {
        return ResponseVO.success(tagService.bind(tagNo, request));
    }

    /**
     * 新增unbind或触发unbind相关动作。
     * <p>HTTP POST {@code /api/location/tags/{tagNo}/unbind}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tagNo tagNo 参数
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{tagNo}/unbind")
    public ResponseVO<LocTagBindingVO> unbind(@PathVariable String tagNo, @RequestParam Long tenantId) {
        return ResponseVO.success(tagService.unbind(tagNo, tenantId));
    }
}
