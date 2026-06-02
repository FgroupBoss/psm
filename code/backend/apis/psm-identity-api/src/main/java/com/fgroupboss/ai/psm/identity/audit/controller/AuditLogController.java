package com.fgroupboss.ai.psm.identity.audit.controller;

import com.fgroupboss.ai.psm.identity.audit.model.dto.AuditLogIngestRequest;
import com.fgroupboss.ai.psm.identity.audit.model.vo.AuditLogRecordVO;
import com.fgroupboss.ai.psm.identity.audit.service.AuditLogService;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * 审计日志接口。
 * <p>跨域操作留痕的查询与写入。</p>
 * <p>基础路径：{@code /api/audit}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/audit")
public class AuditLogController {

    private final AuditLogService service;

    /**
     * 按条件分页检索审计日志。
     * <p>HTTP GET {@code /api/audit/logs}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param bizType 业务类型
     * @param bizTypePrefix 业务类型前缀，用于模糊匹配
     * @param bizId 业务实体 ID
     * @param action 操作动作编码
     * @param operatorName 操作人姓名
     * @param startTime 查询起始时间（含）
     * @param endTime 查询截止时间（含）
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/logs")
    public ResponseVO<PageResult<AuditLogRecordVO>> page(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) String bizType,
                                                         @RequestParam(required = false) String bizTypePrefix,
                                                         @RequestParam(required = false) Long bizId,
                                                         @RequestParam(required = false) String action,
                                                         @RequestParam(required = false) String operatorName,
                                                         @RequestParam(required = false)
                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                 LocalDateTime startTime,
                                                         @RequestParam(required = false)
                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                 LocalDateTime endTime,
                                                         @RequestParam(defaultValue = "1") int pageNo,
                                                         @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.page(loginContext.getTenantId(), bizType, bizTypePrefix, bizId, action, operatorName,
                startTime, endTime, pageNo, pageSize));
    }

    /**
     * 写入一条审计日志（供各域埋点调用）。
     * <p>HTTP POST {@code /api/audit/logs}</p>
     * @param request 请求体
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/logs")
    public ResponseVO<Void> append(@Valid @RequestBody AuditLogIngestRequest request) {
        service.append(request);
        return ResponseVO.success(null);
    }
}
