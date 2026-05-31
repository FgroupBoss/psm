package com.fgroupboss.ai.psm.incidentgovernance.integration.controller.reg;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.dto.RegMappingSaveRequest;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegMappingVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.service.RegMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 监管字段/编码映射接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/integration/reg/mappings")
public class RegMappingController {

    private final RegMappingService mappingService;

    @GetMapping
    public ResponseVO<RegMappingVO> list(@RequestParam Long tenantId,
                                         @RequestParam String platformCode,
                                         @RequestParam(required = false) String dataDomain) {
        return ResponseVO.success(mappingService.list(tenantId, platformCode, dataDomain));
    }

    @PostMapping
    public ResponseVO<RegMappingVO> save(@Valid @RequestBody RegMappingSaveRequest request) {
        return ResponseVO.success(mappingService.save(request));
    }
}
