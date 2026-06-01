package com.fgroupboss.ai.psm.operation.workpermit.blindplate.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.BlindPlateActionConfirmRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.BlindPlateWorkDetailRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlateActionRecordVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlateFlowProgressVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlatePreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlateWorkDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.blindplate.service.BlindPlateService;
import lombok.RequiredArgsConstructor;
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
 * 盲板抽堵专项接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-permits/{id}/blind-plate")
public class BlindPlateController {

    private final BlindPlateService blindPlateService;

    @GetMapping("/detail")
    public ResponseVO<BlindPlateWorkDetailVO> getDetail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(blindPlateService.getDetail(tenantId, id));
    }

    @PutMapping("/detail")
    public ResponseVO<BlindPlateWorkDetailVO> saveDetail(@PathVariable Long id,
                                                         @Valid @RequestBody BlindPlateWorkDetailRequest request,
                                                         @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                         @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(blindPlateService.saveDetail(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @GetMapping("/action-records")
    public ResponseVO<List<BlindPlateActionRecordVO>> listActionRecords(@PathVariable Long id,
                                                                          @RequestParam Long tenantId) {
        return ResponseVO.success(blindPlateService.listActionRecords(tenantId, id));
    }

    @PostMapping("/action-confirm")
    public ResponseVO<BlindPlateActionRecordVO> confirmAction(@PathVariable Long id,
                                                                @Valid @RequestBody BlindPlateActionConfirmRequest request,
                                                                @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                                @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                                @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(blindPlateService.confirmAction(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    @PostMapping("/pre-check")
    public ResponseVO<BlindPlatePreCheckResultVO> preCheck(@PathVariable Long id,
                                                             @RequestParam Long tenantId,
                                                             @RequestParam String checkPoint) {
        return ResponseVO.success(blindPlateService.preCheck(tenantId, id, checkPoint));
    }

    @GetMapping("/flow-progress")
    public ResponseVO<BlindPlateFlowProgressVO> flowProgress(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(blindPlateService.getFlowProgress(tenantId, id));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
