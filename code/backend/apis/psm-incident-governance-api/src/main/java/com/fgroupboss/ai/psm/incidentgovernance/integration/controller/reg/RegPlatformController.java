package com.fgroupboss.ai.psm.incidentgovernance.integration.controller.reg;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.dto.RegPlatformConfigRequest;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegPlatformConfigVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.service.RegPlatformConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 监管平台配置接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/integration/reg/platforms")
public class RegPlatformController {

    private final RegPlatformConfigService platformConfigService;

    @GetMapping
    public ResponseVO<?> list(@RequestParam Long tenantId,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Integer enabled,
                              @RequestParam(required = false) Integer pageNo,
                              @RequestParam(required = false) Integer pageSize) {
        if (pageNo != null || pageSize != null) {
            int normalizedPageNo = pageNo == null ? 1 : pageNo;
            int normalizedPageSize = pageSize == null ? 20 : pageSize;
            PageResult<RegPlatformConfigVO> page = platformConfigService.page(
                    tenantId, keyword, enabled, normalizedPageNo, normalizedPageSize);
            return ResponseVO.success(page);
        }
        List<RegPlatformConfigVO> list = platformConfigService.list(tenantId, enabled);
        return ResponseVO.success(list);
    }

    @PostMapping
    public ResponseVO<RegPlatformConfigVO> save(@Valid @RequestBody RegPlatformConfigRequest request) {
        return ResponseVO.success(platformConfigService.save(request));
    }
}
