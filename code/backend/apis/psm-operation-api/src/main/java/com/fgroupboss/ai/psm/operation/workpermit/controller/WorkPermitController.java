package com.fgroupboss.ai.psm.operation.workpermit.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.AcceptanceRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.CheckInRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.GasTestRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.MobileDraftSyncRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.MonitorRecordRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.PermitActionRequest;
import com.fgroupboss.ai.psm.operation.workpermit.model.dto.PreCheckRequest;
import com.fgroupboss.ai.psm.operation.workpermit.model.dto.RiskAnalysisRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.SafetyMeasureRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.SitePermitRequest;
import com.fgroupboss.ai.psm.operation.workpermit.model.dto.WorkPermitRequest;
import com.fgroupboss.ai.psm.operation.workpermit.model.dto.WorkPermitWorkerRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.GasTestVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.MobileDraftSyncResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.MonitorRecordVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.PreCheckResultVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.RiskAnalysisVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.SafetyMeasureVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.SiteConfirmVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.TimelineItemVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitDetailVO;
import com.fgroupboss.ai.psm.operation.workpermit.model.vo.WorkPermitHealthVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitVO;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitWorkerVO;
import com.fgroupboss.ai.psm.operation.workpermit.service.WorkPermitService;
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
 * 鍗遍櫓宸ヤ綔绁ㄦ帴鍙ｏ紙M06 鍔ㄧ伀/鍙楅檺绌洪棿闂幆锛夈€?
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-permits")
public class WorkPermitController {

    private final WorkPermitService workPermitService;

    /**
     * 鎺ュ彛鐢ㄩ€旓細鏌ヨ鏈嶅姟鍋ュ悍鐘舵€併€?
     */
    @GetMapping("/health")
    public ResponseVO<WorkPermitHealthVO> health() {
        return ResponseVO.success(workPermitService.health());
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鍒嗛〉鏌ヨ涓氬姟鏁版嵁銆?
     */
    @GetMapping
    public ResponseVO<PageResult<WorkPermitVO>> page(@RequestParam Long tenantId,
                                                       @RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) String status,
                                                       @RequestParam(required = false) String workType,
                                                       @RequestParam(required = false) Long areaId,
                                                       @RequestParam(required = false) Long hazardId,
                                                       @RequestParam(defaultValue = "1") int pageNo,
                                                       @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(workPermitService.page(tenantId, keyword, status, workType, areaId, hazardId, pageNo, pageSize));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鎸夐噸澶у嵄闄╂簮鏌ヨ鍏宠仈浣滀笟绁ㄣ€?     */
    @GetMapping("/by-hazard")
    public ResponseVO<List<WorkPermitVO>> listByHazard(@RequestParam Long tenantId, @RequestParam Long hazardId) {
        return ResponseVO.success(workPermitService.listByHazard(tenantId, hazardId));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鏌ヨ璇︽儏銆?
     */
    @GetMapping("/{id}")
    public ResponseVO<WorkPermitDetailVO> detail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(workPermitService.getDetail(tenantId, id));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鍒涘缓涓氬姟鏁版嵁銆?
     */
    @PostMapping
    public ResponseVO<WorkPermitVO> create(@Valid @RequestBody WorkPermitRequest request,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.create(request, operator(userId, username, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鏇存柊涓氬姟鏁版嵁銆?
     */
    @PutMapping("/{id}")
    public ResponseVO<WorkPermitVO> update(@PathVariable Long id,
                                           @Valid @RequestBody WorkPermitRequest request,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.update(id, request, operator(userId, username, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鏌ヨ浣滀笟浜哄憳銆?
     */
    @GetMapping("/{id}/workers")
    public ResponseVO<List<WorkPermitWorkerVO>> listWorkers(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(workPermitService.listWorkers(tenantId, id));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鏂板浣滀笟浜哄憳銆?
     */
    @PostMapping("/{id}/workers")
    public ResponseVO<WorkPermitWorkerVO> addWorker(@PathVariable Long id,
                                                      @Valid @RequestBody WorkPermitWorkerRequest request,
                                                      @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                      @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                      @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.addWorker(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細绉婚櫎浣滀笟浜哄憳銆?
     */
    @DeleteMapping("/{id}/workers/{workerId}")
    public ResponseVO<Void> removeWorker(@PathVariable Long id,
                                         @PathVariable Long workerId,
                                         @RequestParam Long tenantId,
                                         @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                         @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        workPermitService.removeWorker(tenantId, id, workerId, operator(userId, username, operator));
        return ResponseVO.success(null);
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鎻愪氦瀹℃壒銆?
     */
    @PostMapping("/{id}/submit")
    public ResponseVO<WorkPermitVO> submit(@PathVariable Long id,
                                           @RequestParam Long tenantId,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.submit(tenantId, id, operator(userId, username, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細瀹℃壒涓氬姟鏁版嵁銆?
     */
    @PostMapping("/{id}/approve")
    public ResponseVO<WorkPermitVO> approve(@PathVariable Long id,
                                            @RequestParam Long tenantId,
                                            @RequestBody(required = false) PermitActionRequest request,
                                            @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                            @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.approve(tenantId, id, defaultAction(request),
                operator(userId, username, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細澶勭悊鎺ュ彛璇锋眰銆?
     */
    @PostMapping("/{id}/return")
    public ResponseVO<WorkPermitVO> returnPermit(@PathVariable Long id,
                                                 @RequestParam Long tenantId,
                                                 @RequestBody(required = false) PermitActionRequest request,
                                                 @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                 @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.returnPermit(tenantId, id, defaultAction(request),
                operator(userId, username, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細澶勭悊鎺ュ彛璇锋眰銆?
     */
    @PostMapping("/{id}/reject")
    public ResponseVO<WorkPermitVO> reject(@PathVariable Long id,
                                           @RequestParam Long tenantId,
                                           @RequestBody(required = false) PermitActionRequest request,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.reject(tenantId, id, defaultAction(request),
                operator(userId, username, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鏌ヨ椋庨櫓鍒嗘瀽鍒楄〃銆?     */
    @GetMapping("/{id}/risk-analysis")
    public ResponseVO<List<RiskAnalysisVO>> listRiskAnalysis(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(workPermitService.listRiskAnalysis(tenantId, id));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細澶勭悊鎺ュ彛璇锋眰銆?
     */
    @PostMapping("/{id}/risk-analysis")
    public ResponseVO<RiskAnalysisVO> saveRiskAnalysis(@PathVariable Long id,
                                                         @Valid @RequestBody RiskAnalysisRequest request,
                                                         @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                         @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.saveRiskAnalysis(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鏌ヨ瀹夊叏鎺柦銆?
     */
    @GetMapping("/{id}/safety-measures")
    public ResponseVO<List<SafetyMeasureVO>> listSafetyMeasures(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(workPermitService.listSafetyMeasures(tenantId, id));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細纭瀹夊叏鎺柦銆?
     */
    @PostMapping("/{id}/safety-measures/{measureId}")
    public ResponseVO<SafetyMeasureVO> confirmSafetyMeasure(@PathVariable Long id,
                                                            @PathVariable Long measureId,
                                                            @Valid @RequestBody SafetyMeasureRequest request,
                                                            @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                            @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.confirmSafetyMeasure(request.getTenantId(), id, measureId, request,
                operator(userId, username, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鏌ヨ姘斾綋妫€娴嬭褰曘€?
     */
    @GetMapping("/{id}/gas-tests")
    public ResponseVO<List<GasTestVO>> listGasTests(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(workPermitService.listGasTests(tenantId, id));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鏂板姘斾綋妫€娴嬭褰曘€?
     */
    @PostMapping("/{id}/gas-tests")
    public ResponseVO<GasTestVO> addGasTest(@PathVariable Long id,
                                            @Valid @RequestBody GasTestRequest request,
                                            @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                            @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.addGasTest(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鎵ц浣滀笟鍓嶇疆鏍￠獙銆?
     */
    @PostMapping("/{id}/pre-check")
    public ResponseVO<PreCheckResultVO> preCheck(@PathVariable Long id, @Valid @RequestBody PreCheckRequest request) {
        return ResponseVO.success(workPermitService.preCheck(request.getTenantId(), id, request));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鎻愪氦鐜板満璁稿彲銆?
     */
    @PostMapping("/{id}/site-permit")
    public ResponseVO<WorkPermitVO> sitePermit(@PathVariable Long id,
                                               @Valid @RequestBody SitePermitRequest request,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.sitePermit(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鎻愪氦鐜板満绛惧埌銆?
     */
    @PostMapping("/{id}/check-in")
    public ResponseVO<SiteConfirmVO> checkIn(@PathVariable Long id,
                                             @Valid @RequestBody CheckInRequest request,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.checkIn(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鏌ヨ鐩戞姢璁板綍銆?
     */
    @GetMapping("/{id}/monitor-records")
    public ResponseVO<List<MonitorRecordVO>> listMonitorRecords(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(workPermitService.listMonitorRecords(tenantId, id));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鏂板鐩戞姢璁板綍銆?
     */
    @PostMapping("/{id}/monitor-records")
    public ResponseVO<MonitorRecordVO> addMonitorRecord(@PathVariable Long id,
                                                        @Valid @RequestBody MonitorRecordRequest request,
                                                        @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                        @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.addMonitorRecord(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鎸傝捣涓氬姟鏁版嵁銆?
     */
    @PostMapping("/{id}/suspend")
    public ResponseVO<WorkPermitVO> suspend(@PathVariable Long id,
                                            @RequestParam Long tenantId,
                                            @RequestBody(required = false) PermitActionRequest request,
                                            @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                            @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.suspend(tenantId, id, defaultAction(request),
                operator(userId, username, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鎭㈠涓氬姟鏁版嵁銆?
     */
    @PostMapping("/{id}/resume")
    public ResponseVO<WorkPermitVO> resume(@PathVariable Long id,
                                           @RequestParam Long tenantId,
                                           @RequestBody(required = false) PermitActionRequest request,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.resume(tenantId, id, defaultAction(request),
                operator(userId, username, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細缁堟浣滀笟绁ㄣ€?
     */
    @PostMapping("/{id}/terminate")
    public ResponseVO<WorkPermitVO> terminate(@PathVariable Long id,
                                              @RequestParam Long tenantId,
                                              @RequestBody(required = false) PermitActionRequest request,
                                              @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                              @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                              @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.terminate(tenantId, id, defaultAction(request),
                operator(userId, username, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鎻愪氦楠屾敹銆?
     */
    @PostMapping("/{id}/acceptance")
    public ResponseVO<WorkPermitVO> acceptance(@PathVariable Long id,
                                               @Valid @RequestBody AcceptanceRequest request,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(workPermitService.acceptance(request.getTenantId(), id, request,
                operator(userId, username, operator)));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鏌ヨ浣滀笟绁ㄦ椂闂寸嚎銆?
     */
    @GetMapping("/{id}/timeline")
    public ResponseVO<List<TimelineItemVO>> timeline(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(workPermitService.timeline(tenantId, id));
    }

    /**
     * 鎺ュ彛鐢ㄩ€旓細鍚屾绉诲姩绔崏绋裤€?
     */
    @PostMapping("/mobile/drafts/sync")
    public ResponseVO<MobileDraftSyncResultVO> syncMobileDraft(@Valid @RequestBody MobileDraftSyncRequest request) {
        return ResponseVO.success(workPermitService.syncMobileDraft(request));
    }

    private PermitActionRequest defaultAction(PermitActionRequest request) {
        return request == null ? new PermitActionRequest() : request;
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}

