package com.fgroupboss.ai.psm.pha.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.pha.model.dto.PhaProjectRequest;
import com.fgroupboss.ai.psm.pha.model.vo.PhaProjectReportVO;
import com.fgroupboss.ai.psm.pha.model.vo.PhaProjectVO;

public interface PhaProjectService {
    PageResult<PhaProjectVO> page(Long tenantId, String keyword, String status, int pageNo, int pageSize);
    PhaProjectVO getById(Long tenantId, Long id);
    PhaProjectVO create(PhaProjectRequest request, String operator);
    PhaProjectVO update(Long id, PhaProjectRequest request, String operator);
    void delete(Long tenantId, Long id, String operator);
    PhaProjectVO submit(Long tenantId, Long id, String operator);
    PhaProjectVO publish(Long tenantId, Long id, String operator);
    PhaProjectVO archive(Long tenantId, Long id, String operator);
    PhaProjectReportVO exportReport(Long projectId, Long tenantId);
}
