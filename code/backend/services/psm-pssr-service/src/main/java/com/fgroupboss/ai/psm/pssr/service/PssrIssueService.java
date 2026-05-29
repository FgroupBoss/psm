package com.fgroupboss.ai.psm.pssr.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.pssr.model.dto.PssrIssueActionRequest;
import com.fgroupboss.ai.psm.pssr.model.dto.PssrIssueRequest;
import com.fgroupboss.ai.psm.pssr.model.vo.PssrIssueVO;

/**
 * PSSR 审查问题闭环。
 */
public interface PssrIssueService {

    PageResult<PssrIssueVO> page(Long tenantId, Long projectId, String status, int pageNo, int pageSize);

    PssrIssueVO create(PssrIssueRequest request, String operator);

    PssrIssueVO rectify(Long id, PssrIssueActionRequest request, String operator);

    PssrIssueVO review(Long id, PssrIssueActionRequest request, String operator);
}
