package com.fgroupboss.ai.psm.incidentgovernance.integration.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.dto.RegPlatformConfigRequest;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegPlatformConfigVO;

import java.util.List;

public interface RegPlatformConfigService {

    PageResult<RegPlatformConfigVO> page(Long tenantId, String keyword, Integer enabled, int pageNo, int pageSize);

    List<RegPlatformConfigVO> list(Long tenantId, Integer enabled);

    RegPlatformConfigVO save(RegPlatformConfigRequest request);

    RegPlatformConfigVO requireEnabledPlatform(Long tenantId, String platformCode);

    void requirePlatform(Long tenantId, String platformCode);

    /**
     * 查询全部租户下已启用的监管平台（供定时调度使用）。
     */
    List<RegPlatformConfigVO> listAllEnabled();
}
