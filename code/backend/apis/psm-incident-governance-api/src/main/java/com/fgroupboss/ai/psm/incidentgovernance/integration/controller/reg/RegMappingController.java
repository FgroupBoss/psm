package com.fgroupboss.ai.psm.incidentgovernance.integration.controller.reg;

import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
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
 * RegMapping 模块 HTTP API。
 * <p>基础路径：{@code /api/integration/reg/mappings}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/integration/reg/mappings")
public class RegMappingController {

    private final RegMappingService mappingService;

    /**
     * 查询列表。
     * <p>HTTP GET {@code /api/integration/reg/mappings}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param platformCode platformCode 参数
     * @param dataDomain dataDomain 参数
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<RegMappingVO> list(@LoginContext UserContext loginContext,
                                                                                           @RequestParam String platformCode,
                                         @RequestParam(required = false) String dataDomain) {
        return ResponseVO.success(mappingService.list(loginContext.getTenantId(), platformCode, dataDomain));
    }

    /**
     * 保存数据。
     * <p>HTTP POST {@code /api/integration/reg/mappings}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<RegMappingVO> save(@Valid @RequestBody RegMappingSaveRequest request) {
        return ResponseVO.success(mappingService.save(request));
    }
}
