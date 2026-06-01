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
 * RegPlatform 模块 HTTP API。
 * <p>基础路径：{@code /api/integration/reg/platforms}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/integration/reg/platforms")
public class RegPlatformController {

    private final RegPlatformConfigService platformConfigService;

    /**
     * 查询列表。
     * <p>HTTP GET {@code /api/integration/reg/platforms}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param enabled enabled 参数
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
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

    /**
     * 保存数据。
     * <p>HTTP POST {@code /api/integration/reg/platforms}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<RegPlatformConfigVO> save(@Valid @RequestBody RegPlatformConfigRequest request) {
        return ResponseVO.success(platformConfigService.save(request));
    }
}
