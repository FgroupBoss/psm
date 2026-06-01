package com.fgroupboss.ai.psm.incidentgovernance.incident.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.CapaVerifyRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.IncidentCapaRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.IncidentEvidenceRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.IncidentFromSourceRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.IncidentReportRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.IncidentRootCauseRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.IncidentTimelineRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.LessonLearnedRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto.StartInvestigationRequest;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.vo.IncidentCapaVO;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.vo.IncidentEvidenceVO;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.vo.IncidentReportVO;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.vo.IncidentRootCauseVO;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.vo.IncidentTimelineVO;
import com.fgroupboss.ai.psm.incidentgovernance.incident.model.vo.LessonLearnedVO;
import com.fgroupboss.ai.psm.incidentgovernance.incident.service.IncidentService;
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
 * 事故调查与 CAPA 接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    @GetMapping
    public ResponseVO<PageResult<IncidentReportVO>> page(@RequestParam Long tenantId,
                                                         @RequestParam(required = false) String keyword,
                                                         @RequestParam(required = false) String status,
                                                         @RequestParam(required = false) String incidentType,
                                                         @RequestParam(required = false) String incidentLevel,
                                                         @RequestParam(defaultValue = "1") int pageNo,
                                                         @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(incidentService.page(tenantId, keyword, status, incidentType,
                incidentLevel, pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseVO<IncidentReportVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(incidentService.getById(tenantId, id));
    }

    @PostMapping
    public ResponseVO<IncidentReportVO> create(@Valid @RequestBody IncidentReportRequest request,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(incidentService.create(request, resolveOperator(userId, username, operator)));
    }

    @PutMapping("/{id}")
    public ResponseVO<IncidentReportVO> update(@PathVariable Long id,
                                               @Valid @RequestBody IncidentReportRequest request,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(incidentService.update(id, request, resolveOperator(userId, username, operator)));
    }

    @PostMapping("/from-source")
    public ResponseVO<IncidentReportVO> createFromSource(@Valid @RequestBody IncidentFromSourceRequest request,
                                                         @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                         @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                         @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(incidentService.createFromSource(request,
                resolveOperator(userId, username, operator)));
    }

    @PostMapping("/{id}/start-investigation")
    public ResponseVO<IncidentReportVO> startInvestigation(@PathVariable Long id,
                                                           @Valid @RequestBody StartInvestigationRequest request,
                                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(incidentService.startInvestigation(id, request,
                resolveOperator(userId, username, operator)));
    }

    @GetMapping("/{id}/timeline")
    public ResponseVO<List<IncidentTimelineVO>> listTimeline(@PathVariable Long id,
                                                             @RequestParam Long tenantId) {
        return ResponseVO.success(incidentService.listTimeline(tenantId, id));
    }

    @PostMapping("/{id}/timeline")
    public ResponseVO<IncidentTimelineVO> addTimeline(@PathVariable Long id,
                                                      @Valid @RequestBody IncidentTimelineRequest request,
                                                      @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                      @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                      @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(incidentService.addTimeline(id, request,
                resolveOperator(userId, username, operator)));
    }

    @GetMapping("/{id}/evidence")
    public ResponseVO<List<IncidentEvidenceVO>> listEvidence(@PathVariable Long id,
                                                             @RequestParam Long tenantId) {
        return ResponseVO.success(incidentService.listEvidence(tenantId, id));
    }

    @PostMapping("/{id}/evidence")
    public ResponseVO<IncidentEvidenceVO> addEvidence(@PathVariable Long id,
                                                      @Valid @RequestBody IncidentEvidenceRequest request,
                                                      @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                      @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                      @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(incidentService.addEvidence(id, request,
                resolveOperator(userId, username, operator)));
    }

    @GetMapping("/{id}/root-causes")
    public ResponseVO<List<IncidentRootCauseVO>> listRootCauses(@PathVariable Long id,
                                                                @RequestParam Long tenantId) {
        return ResponseVO.success(incidentService.listRootCauses(tenantId, id));
    }

    @PostMapping("/{id}/root-causes")
    public ResponseVO<IncidentRootCauseVO> addRootCause(@PathVariable Long id,
                                                        @Valid @RequestBody IncidentRootCauseRequest request,
                                                        @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                        @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(incidentService.addRootCause(id, request,
                resolveOperator(userId, username, operator)));
    }

    @GetMapping("/{id}/capa")
    public ResponseVO<List<IncidentCapaVO>> listCapa(@PathVariable Long id,
                                                     @RequestParam Long tenantId) {
        return ResponseVO.success(incidentService.listCapa(tenantId, id));
    }

    @PostMapping("/{id}/capa")
    public ResponseVO<IncidentCapaVO> addCapa(@PathVariable Long id,
                                              @Valid @RequestBody IncidentCapaRequest request,
                                              @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                              @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                              @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(incidentService.addCapa(id, request,
                resolveOperator(userId, username, operator)));
    }

    @PostMapping("/capa/{id}/verify")
    public ResponseVO<IncidentCapaVO> verifyCapa(@PathVariable Long id,
                                               @Valid @RequestBody CapaVerifyRequest request,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(incidentService.verifyCapa(id, request,
                resolveOperator(userId, username, operator)));
    }

    @PostMapping("/{id}/lessons-learned")
    public ResponseVO<LessonLearnedVO> addLessonLearned(@PathVariable Long id,
                                                        @Valid @RequestBody LessonLearnedRequest request,
                                                        @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                        @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(incidentService.addLessonLearned(id, request,
                resolveOperator(userId, username, operator)));
    }

    private String resolveOperator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
