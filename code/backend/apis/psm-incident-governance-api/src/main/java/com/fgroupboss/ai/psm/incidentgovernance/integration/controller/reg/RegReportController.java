package com.fgroupboss.ai.psm.incidentgovernance.integration.controller.reg;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.dto.RegReportPreviewRequest;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.dto.RegReportTriggerRequest;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegReconciliationVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegReportPreviewVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegReportReceiptVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegReportTaskVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.service.RegReportService;
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
 * RegReport 模块 HTTP API。
 * <p>基础路径：{@code /api/integration/reg}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/integration/reg")
public class RegReportController {

    private final RegReportService reportService;

    /**
     * 查询tasks。
     * <p>HTTP GET {@code /api/integration/reg/tasks}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param platformCode platformCode 参数
     * @param dataDomain dataDomain 参数
     * @param status 业务状态筛选
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/tasks")
    public ResponseVO<PageResult<RegReportTaskVO>> pageTasks(@RequestParam Long tenantId,
                                                             @RequestParam(required = false) String platformCode,
                                                             @RequestParam(required = false) String dataDomain,
                                                             @RequestParam(required = false) String status,
                                                             @RequestParam(defaultValue = "1") int pageNo,
                                                             @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(reportService.pageTasks(tenantId, platformCode, dataDomain, status, pageNo, pageSize));
    }

    /**
     * 新增trigger或触发trigger相关动作。
     * <p>HTTP POST {@code /api/integration/reg/tasks/trigger}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/tasks/trigger")
    public ResponseVO<RegReportTaskVO> trigger(@Valid @RequestBody RegReportTriggerRequest request) {
        return ResponseVO.success(reportService.trigger(request));
    }

    /**
     * 新增retry或触发retry相关动作。
     * <p>HTTP POST {@code /api/integration/reg/tasks/{id}/retry}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/tasks/{id}/retry")
    public ResponseVO<RegReportTaskVO> retry(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(reportService.retry(tenantId, id));
    }

    /**
     * 查询receipts。
     * <p>HTTP GET {@code /api/integration/reg/receipts}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param taskId task ID
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/receipts")
    public ResponseVO<PageResult<RegReportReceiptVO>> pageReceipts(@RequestParam Long tenantId,
                                                                   @RequestParam(required = false) Long taskId,
                                                                   @RequestParam(defaultValue = "1") int pageNo,
                                                                   @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(reportService.pageReceipts(tenantId, taskId, pageNo, pageSize));
    }

    /**
     * 查询reconciliation。
     * <p>HTTP GET {@code /api/integration/reg/reconciliation}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param platformCode platformCode 参数
     * @param dataDomain dataDomain 参数
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/reconciliation")
    public ResponseVO<RegReconciliationVO> reconciliation(@RequestParam Long tenantId,
                                                          @RequestParam(required = false) String platformCode,
                                                          @RequestParam(required = false) String dataDomain) {
        return ResponseVO.success(reportService.reconciliation(tenantId, platformCode, dataDomain));
    }

    /**
     * 新增preview或触发preview相关动作。
     * <p>HTTP POST {@code /api/integration/reg/preview}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/preview")
    public ResponseVO<RegReportPreviewVO> preview(@Valid @RequestBody RegReportPreviewRequest request) {
        return ResponseVO.success(reportService.preview(request));
    }
}
