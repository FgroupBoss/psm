package com.fgroupboss.ai.psm.dualprevention.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.dualprevention.model.dto.RiskEventRequest;
import com.fgroupboss.ai.psm.dualprevention.model.dto.RiskUnitRequest;
import com.fgroupboss.ai.psm.dualprevention.model.vo.RiskColorStatVO;
import com.fgroupboss.ai.psm.dualprevention.model.vo.RiskEventVO;
import com.fgroupboss.ai.psm.dualprevention.model.vo.RiskUnitTreeNodeVO;
import com.fgroupboss.ai.psm.dualprevention.model.vo.RiskUnitVO;
import com.fgroupboss.ai.psm.dualprevention.service.RiskUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 风险单元与风险清单接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dual-prevention/risk-units")
public class RiskUnitController {

    private final RiskUnitService riskUnitService;

    @GetMapping
    public ResponseVO<PageResult<RiskUnitVO>> page(@RequestParam Long tenantId,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) String status,
                                                   @RequestParam(required = false) Long areaId,
                                                   @RequestParam(defaultValue = "1") int pageNo,
                                                   @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(riskUnitService.page(tenantId, keyword, status, areaId, pageNo, pageSize));
    }

    @GetMapping("/tree")
    public ResponseVO<List<RiskUnitTreeNodeVO>> tree(@RequestParam Long tenantId,
                                                     @RequestParam(required = false) Long areaId) {
        return ResponseVO.success(riskUnitService.tree(tenantId, areaId));
    }

    @GetMapping("/color-map")
    public ResponseVO<Map<String, Long>> colorMap(@RequestParam Long tenantId,
                                                  @RequestParam(required = false) Long areaId) {
        return ResponseVO.success(riskUnitService.colorMap(tenantId, areaId));
    }

    @GetMapping("/color-stats")
    public ResponseVO<List<RiskColorStatVO>> colorStats(@RequestParam Long tenantId,
                                                        @RequestParam(required = false) Long areaId) {
        return ResponseVO.success(riskUnitService.colorStats(tenantId, areaId));
    }

    @GetMapping("/{id}")
    public ResponseVO<RiskUnitVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(riskUnitService.getById(tenantId, id));
    }

    @PostMapping
    public ResponseVO<RiskUnitVO> create(@Valid @RequestBody RiskUnitRequest request,
                                         @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                         @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(riskUnitService.create(request, operator(userId, username, operator)));
    }

    @PutMapping("/{id}")
    public ResponseVO<RiskUnitVO> update(@PathVariable Long id,
                                         @Valid @RequestBody RiskUnitRequest request,
                                         @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                         @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(riskUnitService.update(id, request, operator(userId, username, operator)));
    }

    @DeleteMapping("/{id}")
    public ResponseVO<Void> delete(@PathVariable Long id,
                                   @RequestParam Long tenantId,
                                   @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                   @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        riskUnitService.delete(tenantId, id, operator(userId, username, operator));
        return ResponseVO.success(null);
    }

    @GetMapping("/{id}/events")
    public ResponseVO<List<RiskEventVO>> listEvents(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(riskUnitService.listEvents(tenantId, id));
    }

    @PostMapping("/{id}/events")
    public ResponseVO<RiskEventVO> createEvent(@PathVariable Long id,
                                               @Valid @RequestBody RiskEventRequest request,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(riskUnitService.createEvent(id, request, operator(userId, username, operator)));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
