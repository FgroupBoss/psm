package com.fgroupboss.ai.psm.audit.controller;

import com.fgroupboss.ai.psm.audit.model.dto.AuditLogIngestRequest;
import com.fgroupboss.ai.psm.audit.model.vo.AuditLogRecordVO;
import com.fgroupboss.ai.psm.audit.service.AuditLogService;
import com.fgroupboss.ai.psm.common.PageResult;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/audit")
public class AuditLogController {

    private final AuditLogService service;

    @GetMapping("/logs")
    public ResponseVO<PageResult<AuditLogRecordVO>> page(@RequestParam Long tenantId,
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
        return ResponseVO.success(service.page(tenantId, bizType, bizTypePrefix, bizId, action, operatorName,
                startTime, endTime, pageNo, pageSize));
    }

    @PostMapping("/logs")
    public ResponseVO<Void> append(@Valid @RequestBody AuditLogIngestRequest request) {
        service.append(request);
        return ResponseVO.success(null);
    }
}
