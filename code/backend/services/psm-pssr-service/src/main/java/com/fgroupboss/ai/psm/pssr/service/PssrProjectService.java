package com.fgroupboss.ai.psm.pssr.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.pssr.model.dto.PssrApprovalRequest;
import com.fgroupboss.ai.psm.pssr.model.dto.PssrExecuteRequest;
import com.fgroupboss.ai.psm.pssr.model.dto.PssrProjectRequest;
import com.fgroupboss.ai.psm.pssr.model.vo.PssrExecutionRecordVO;
import com.fgroupboss.ai.psm.pssr.model.vo.PssrProjectVO;
import com.fgroupboss.ai.psm.pssr.model.vo.PssrStartupCheckVO;

/**
 * PSSR 项目管理。
 */
public interface PssrProjectService {

    PageResult<PssrProjectVO> page(Long tenantId, String keyword, String status, int pageNo, int pageSize);

    PssrProjectVO getById(Long tenantId, Long id);

    PssrProjectVO create(PssrProjectRequest request, String operator);

    PssrProjectVO update(Long id, PssrProjectRequest request, String operator);

    void delete(Long tenantId, Long id, String operator);

    PssrExecutionRecordVO execute(Long projectId, PssrExecuteRequest request, String operator);

    PssrProjectVO approveStartup(Long projectId, PssrApprovalRequest request, String operator);

    PssrProjectVO rejectStartup(Long projectId, PssrApprovalRequest request, String operator);

    PssrStartupCheckVO startupCheck(Long tenantId, Long projectId);
}
