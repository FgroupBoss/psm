package com.fgroupboss.ai.psm.pha.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.pha.model.dto.PhaRecommendationRequest;
import com.fgroupboss.ai.psm.pha.model.dto.RecommendationActionRequest;
import com.fgroupboss.ai.psm.pha.model.vo.PhaRecommendationVO;
import com.fgroupboss.ai.psm.pha.service.PhaRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pha/recommendations")
public class PhaRecommendationController {

    private final PhaRecommendationService phaRecommendationService;

    @GetMapping
    public ResponseVO<PageResult<PhaRecommendationVO>> page(@RequestParam Long tenantId,
                                                            @RequestParam(required = false) Long projectId,
                                                            @RequestParam(required = false) String status,
                                                            @RequestParam(defaultValue = "1") int pageNo,
                                                            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(phaRecommendationService.page(tenantId, projectId, status, pageNo, pageSize));
    }

    @GetMapping("/list")
    public ResponseVO<List<PhaRecommendationVO>> list(@RequestParam Long tenantId,
                                                      @RequestParam(required = false) Long projectId,
                                                      @RequestParam(required = false) String status) {
        return ResponseVO.success(phaRecommendationService.list(tenantId, projectId, status));
    }

    @PostMapping
    public ResponseVO<PhaRecommendationVO> create(@Valid @RequestBody PhaRecommendationRequest request,
                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaRecommendationService.create(request, operator));
    }

    @PostMapping("/{id}/assign")
    public ResponseVO<PhaRecommendationVO> assign(@PathVariable Long id,
                                                   @RequestParam Long tenantId,
                                                   @RequestBody RecommendationActionRequest request,
                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaRecommendationService.assign(tenantId, id, request, operator));
    }

    @PostMapping("/{id}/rectify")
    public ResponseVO<PhaRecommendationVO> rectify(@PathVariable Long id,
                                                   @RequestParam Long tenantId,
                                                   @RequestBody RecommendationActionRequest request,
                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaRecommendationService.rectify(tenantId, id, request, operator));
    }

    @PostMapping("/{id}/verify")
    public ResponseVO<PhaRecommendationVO> verify(@PathVariable Long id,
                                                   @RequestParam Long tenantId,
                                                   @RequestBody RecommendationActionRequest request,
                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaRecommendationService.verify(tenantId, id, request, operator));
    }

    @PostMapping("/{id}/close")
    public ResponseVO<PhaRecommendationVO> close(@PathVariable Long id,
                                                 @RequestParam Long tenantId,
                                                 @RequestBody RecommendationActionRequest request,
                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaRecommendationService.close(tenantId, id, request, operator));
    }
}
