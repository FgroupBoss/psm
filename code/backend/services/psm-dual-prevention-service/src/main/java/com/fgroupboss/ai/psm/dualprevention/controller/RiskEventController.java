package com.fgroupboss.ai.psm.dualprevention.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.dualprevention.model.dto.ControlMeasureRequest;
import com.fgroupboss.ai.psm.dualprevention.model.dto.RiskEventRequest;
import com.fgroupboss.ai.psm.dualprevention.model.vo.ControlMeasureVO;
import com.fgroupboss.ai.psm.dualprevention.model.vo.RiskEventVO;
import com.fgroupboss.ai.psm.dualprevention.service.RiskEventService;
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

/**
 * 风险事件与管控措施接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dual-prevention/risk-events")
public class RiskEventController {

    private final RiskEventService riskEventService;

    @PutMapping("/{id}")
    public ResponseVO<RiskEventVO> update(@PathVariable Long id,
                                          @Valid @RequestBody RiskEventRequest request,
                                          @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                          @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                          @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(riskEventService.update(id, request, operator(userId, username, operator)));
    }

    @DeleteMapping("/{id}")
    public ResponseVO<Void> delete(@PathVariable Long id,
                                   @RequestParam Long tenantId,
                                   @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                   @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        riskEventService.delete(tenantId, id, operator(userId, username, operator));
        return ResponseVO.success(null);
    }

    @GetMapping("/{id}/measures")
    public ResponseVO<List<ControlMeasureVO>> listMeasures(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(riskEventService.listMeasures(tenantId, id));
    }

    @PostMapping("/{id}/measures")
    public ResponseVO<ControlMeasureVO> createMeasure(@PathVariable Long id,
                                                      @Valid @RequestBody ControlMeasureRequest request,
                                                      @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                      @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                      @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(riskEventService.createMeasure(id, request, operator(userId, username, operator)));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
