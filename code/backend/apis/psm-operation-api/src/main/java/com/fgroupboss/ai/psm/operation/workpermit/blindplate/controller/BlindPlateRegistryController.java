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
 * <p>全厂盲板主数据维护，供作业票引用。</p>
 * <p>基础路径：{@code /api/blind-plates}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/blind-plates")
public class BlindPlateRegistryController {

    private final BlindPlateService blindPlateService;

    /**
     * 查询列表。
     * <p>HTTP GET {@code /api/blind-plates}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param status 业务状态筛选
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<List<BlindPlateRegistryVO>> list(@RequestParam Long tenantId,
                                                       @RequestParam(required = false) String status) {
        return ResponseVO.success(blindPlateService.listRegistry(tenantId, status));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/blind-plates/{registryId}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param registryId registry ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{registryId}")
    public ResponseVO<BlindPlateRegistryVO> get(@PathVariable Long registryId, @RequestParam Long tenantId) {
        return ResponseVO.success(blindPlateService.getRegistry(tenantId, registryId));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/blind-plates}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<BlindPlateRegistryVO> create(@Valid @RequestBody BlindPlateRegistryRequest request,
                                                   @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                   @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(blindPlateService.createRegistry(request.getTenantId(), request,
                operator(userId, username, operator)));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/blind-plates/{registryId}}</p>
     * @param registryId registry ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
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
