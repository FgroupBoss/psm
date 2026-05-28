package com.fgroupboss.ai.psm.location.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.location.model.dto.LocTagBindRequest;
import com.fgroupboss.ai.psm.location.model.dto.LocTagRequest;
import com.fgroupboss.ai.psm.location.model.vo.LocTagBindingVO;
import com.fgroupboss.ai.psm.location.model.vo.LocTagVO;
import com.fgroupboss.ai.psm.location.service.LocTagService;
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
 * 定位标签台账与绑定接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location/tags")
public class LocTagController {

    private final LocTagService tagService;

    @GetMapping
    public ResponseVO<PageResult<LocTagVO>> page(@RequestParam Long tenantId,
                                                 @RequestParam(required = false) String keyword,
                                                 @RequestParam(required = false) String status,
                                                 @RequestParam(defaultValue = "1") int pageNo,
                                                 @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(tagService.page(tenantId, keyword, status, pageNo, pageSize));
    }

    @GetMapping("/{tagNo}")
    public ResponseVO<LocTagVO> get(@PathVariable String tagNo, @RequestParam Long tenantId) {
        return ResponseVO.success(tagService.getByTagNo(tenantId, tagNo));
    }

    @PostMapping
    public ResponseVO<LocTagVO> create(@Valid @RequestBody LocTagRequest request) {
        return ResponseVO.success(tagService.create(request));
    }

    @PostMapping("/{tagNo}/bind")
    public ResponseVO<LocTagBindingVO> bind(@PathVariable String tagNo,
                                            @Valid @RequestBody LocTagBindRequest request) {
        return ResponseVO.success(tagService.bind(tagNo, request));
    }

    @PostMapping("/{tagNo}/unbind")
    public ResponseVO<LocTagBindingVO> unbind(@PathVariable String tagNo, @RequestParam Long tenantId) {
        return ResponseVO.success(tagService.unbind(tagNo, tenantId));
    }
}
