package com.fgroupboss.ai.psm.barrier.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.barrier.model.dto.MiDefectCloseRequest;
import com.fgroupboss.ai.psm.barrier.model.dto.MiDefectRequest;
import com.fgroupboss.ai.psm.barrier.model.dto.MiEquipmentRequest;
import com.fgroupboss.ai.psm.barrier.model.dto.MiInspectionPlanRequest;
import com.fgroupboss.ai.psm.barrier.model.vo.MiDefectVO;
import com.fgroupboss.ai.psm.barrier.model.vo.MiEquipmentVO;
import com.fgroupboss.ai.psm.barrier.model.vo.MiInspectionPlanVO;
import com.fgroupboss.ai.psm.barrier.service.MechanicalIntegrityService;
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

/**
 * 机械完整性接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mechanical-integrity")
public class MechanicalIntegrityController {

    private final MechanicalIntegrityService mechanicalIntegrityService;

    @GetMapping("/equipment")
    public ResponseVO<PageResult<MiEquipmentVO>> pageEquipment(@RequestParam Long tenantId,
                                                               @RequestParam(required = false) String keyword,
                                                               @RequestParam(defaultValue = "1") int pageNo,
                                                               @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(mechanicalIntegrityService.pageEquipment(tenantId, keyword, pageNo, pageSize));
    }

    @PostMapping("/equipment")
    public ResponseVO<MiEquipmentVO> createEquipment(@Valid @RequestBody MiEquipmentRequest request,
                                                     @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mechanicalIntegrityService.createEquipment(request, operator));
    }

    @PutMapping("/equipment/{id}")
    public ResponseVO<MiEquipmentVO> updateEquipment(@PathVariable Long id,
                                                     @Valid @RequestBody MiEquipmentRequest request,
                                                     @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mechanicalIntegrityService.updateEquipment(id, request, operator));
    }

    @GetMapping("/inspection-plans")
    public ResponseVO<PageResult<MiInspectionPlanVO>> pageInspectionPlans(@RequestParam Long tenantId,
                                                                          @RequestParam(required = false) Long equipmentId,
                                                                          @RequestParam(defaultValue = "1") int pageNo,
                                                                          @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(mechanicalIntegrityService.pageInspectionPlans(tenantId, equipmentId, pageNo, pageSize));
    }

    @PostMapping("/inspection-plans")
    public ResponseVO<MiInspectionPlanVO> createInspectionPlan(@Valid @RequestBody MiInspectionPlanRequest request,
                                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mechanicalIntegrityService.createInspectionPlan(request, operator));
    }

    @GetMapping("/defects")
    public ResponseVO<PageResult<MiDefectVO>> pageDefects(@RequestParam Long tenantId,
                                                        @RequestParam(required = false) Long equipmentId,
                                                        @RequestParam(required = false) String status,
                                                        @RequestParam(defaultValue = "1") int pageNo,
                                                        @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(mechanicalIntegrityService.pageDefects(tenantId, equipmentId, status, pageNo, pageSize));
    }

    @PostMapping("/defects")
    public ResponseVO<MiDefectVO> createDefect(@Valid @RequestBody MiDefectRequest request,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mechanicalIntegrityService.createDefect(request, operator));
    }

    @PostMapping("/defects/{id}/close")
    public ResponseVO<MiDefectVO> closeDefect(@PathVariable Long id,
                                              @Valid @RequestBody MiDefectCloseRequest request,
                                              @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(mechanicalIntegrityService.closeDefect(id, request, operator));
    }
}
