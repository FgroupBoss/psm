package com.fgroupboss.ai.psm.majorhazard.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.majorhazard.model.vo.MajorHazardVO;

public interface MajorHazardService {

    PageResult<MajorHazardVO> page(Long tenantId, String keyword, String status, String level, int pageNo, int pageSize);

    MajorHazardVO getById(Long tenantId, Long id);
}
