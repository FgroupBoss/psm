package com.fgroupboss.ai.psm.incidentgovernance.incident.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
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
 * Incident 模块 HTTP API。
 * <p>隐患治理与事故事件闭环。</p>
 * <p>基础路径：{@code /api/incidents}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/incidents}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param status 业务状态筛选
     * @param incidentType incidentType 参数
     * @param incidentLevel incidentLevel 参数
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<PageResult<IncidentReportVO>> page(@LoginContext UserContext loginContext,
                                                                                                           @RequestParam(required = false) String keyword,
                                                         @RequestParam(required = false) String status,
                                                         @RequestParam(required = false) String incidentType,
                                                         @RequestParam(required = false) String incidentLevel,
                                                         @RequestParam(defaultValue = "1") int pageNo,
                                                         @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(incidentService.page(loginContext.getTenantId(), keyword, status, incidentType,
                incidentLevel, pageNo, pageSize));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/incidents/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<IncidentReportVO> get(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(incidentService.getById(loginContext.getTenantId(), id));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/incidents}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<IncidentReportVO> create(@LoginContext UserContext loginContext,
                                                  @Valid @RequestBody IncidentReportRequest request,
                                                                                                                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(incidentService.create(request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/incidents/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}")
    public ResponseVO<IncidentReportVO> update(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                               @Valid @RequestBody IncidentReportRequest request,
                                                                                                                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(incidentService.update(id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 新增from source或触发from source相关动作。
     * <p>HTTP POST {@code /api/incidents/from-source}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/from-source")
    public ResponseVO<IncidentReportVO> createFromSource(@LoginContext UserContext loginContext,
                                        @Valid @RequestBody IncidentFromSourceRequest request,
                                                                                                                                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(incidentService.createFromSource(request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 新增start investigation或触发start investigation相关动作。
     * <p>HTTP POST {@code /api/incidents/{id}/start-investigation}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/start-investigation")
    public ResponseVO<IncidentReportVO> startInvestigation(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                           @Valid @RequestBody StartInvestigationRequest request,
                                                                                                                                                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(incidentService.startInvestigation(id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询timeline。
     * <p>HTTP GET {@code /api/incidents/{id}/timeline}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/timeline")
    public ResponseVO<List<IncidentTimelineVO>> listTimeline(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(incidentService.listTimeline(loginContext.getTenantId(), id));
    }

    /**
     * 新增timeline或触发timeline相关动作。
     * <p>HTTP POST {@code /api/incidents/{id}/timeline}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/timeline")
    public ResponseVO<IncidentTimelineVO> addTimeline(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                      @Valid @RequestBody IncidentTimelineRequest request,
                                                                                                                                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(incidentService.addTimeline(id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询evidence。
     * <p>HTTP GET {@code /api/incidents/{id}/evidence}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/evidence")
    public ResponseVO<List<IncidentEvidenceVO>> listEvidence(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(incidentService.listEvidence(loginContext.getTenantId(), id));
    }

    /**
     * 新增evidence或触发evidence相关动作。
     * <p>HTTP POST {@code /api/incidents/{id}/evidence}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/evidence")
    public ResponseVO<IncidentEvidenceVO> addEvidence(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                      @Valid @RequestBody IncidentEvidenceRequest request,
                                                                                                                                                                  @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(incidentService.addEvidence(id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询root causes。
     * <p>HTTP GET {@code /api/incidents/{id}/root-causes}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/root-causes")
    public ResponseVO<List<IncidentRootCauseVO>> listRootCauses(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(incidentService.listRootCauses(loginContext.getTenantId(), id));
    }

    /**
     * 新增root causes或触发root causes相关动作。
     * <p>HTTP POST {@code /api/incidents/{id}/root-causes}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/root-causes")
    public ResponseVO<IncidentRootCauseVO> addRootCause(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                        @Valid @RequestBody IncidentRootCauseRequest request,
                                                                                                                                                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(incidentService.addRootCause(id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 查询capa。
     * <p>HTTP GET {@code /api/incidents/{id}/capa}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/capa")
    public ResponseVO<List<IncidentCapaVO>> listCapa(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(incidentService.listCapa(loginContext.getTenantId(), id));
    }

    /**
     * 新增capa或触发capa相关动作。
     * <p>HTTP POST {@code /api/incidents/{id}/capa}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/capa")
    public ResponseVO<IncidentCapaVO> addCapa(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                              @Valid @RequestBody IncidentCapaRequest request,
                                                                                                                                          @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(incidentService.addCapa(id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 新增verify或触发verify相关动作。
     * <p>HTTP POST {@code /api/incidents/capa/{id}/verify}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/capa/{id}/verify")
    public ResponseVO<IncidentCapaVO> verifyCapa(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                               @Valid @RequestBody CapaVerifyRequest request,
                                                                                                                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(incidentService.verifyCapa(id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 新增lessons learned或触发lessons learned相关动作。
     * <p>HTTP POST {@code /api/incidents/{id}/lessons-learned}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{id}/lessons-learned")
    public ResponseVO<LessonLearnedVO> addLessonLearned(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                        @Valid @RequestBody LessonLearnedRequest request,
                                                                                                                                                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(incidentService.addLessonLearned(id, request,
                UserContextResolver.operator(loginContext, operator)));
    }

}
