package com.fgroupboss.ai.psm.operation.workpermit.blindplate.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.operation.api.workpermit.dto.BlindPlateRegistryRequest;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.BlindPlateRegistryVO;
import com.fgroupboss.ai.psm.operation.workpermit.blindplate.service.BlindPlateService;
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
 * 盲板台账接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/blind-plates")
public class BlindPlateRegistryController {

    private final BlindPlateService blindPlateService;

    @GetMapping
    public ResponseVO<List<BlindPlateRegistryVO>> list(@RequestParam Long tenantId,
                                                       @RequestParam(required = false) String status) {
        return ResponseVO.success(blindPlateService.listRegistry(tenantId, status));
    }

    @GetMapping("/{registryId}")
    public ResponseVO<BlindPlateRegistryVO> get(@PathVariable Long registryId, @RequestParam Long tenantId) {
        return ResponseVO.success(blindPlateService.getRegistry(tenantId, registryId));
    }

    @PostMapping
    public ResponseVO<BlindPlateRegistryVO> create(@Valid @RequestBody BlindPlateRegistryRequest request,
                                                   @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                   @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(blindPlateService.createRegistry(request.getTenantId(), request,
                operator(userId, username, operator)));
    }

    @PutMapping("/{registryId}")
    public ResponseVO<BlindPlateRegistryVO> update(@PathVariable Long registryId,
                                                   @Valid @RequestBody BlindPlateRegistryRequest request,
                                                   @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                   @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(blindPlateService.updateRegistry(request.getTenantId(), registryId, request,
                operator(userId, username, operator)));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
