package com.fgroupboss.ai.psm.inspection.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.inspection.model.dto.OverdueScanRequest;
import com.fgroupboss.ai.psm.inspection.model.dto.TaskAbnormalRequest;
import com.fgroupboss.ai.psm.inspection.model.dto.TaskCreateRequest;
import com.fgroupboss.ai.psm.inspection.model.dto.TaskItemSubmitRequest;
import com.fgroupboss.ai.psm.inspection.model.dto.TaskSignInRequest;
import com.fgroupboss.ai.psm.inspection.model.dto.TaskStartRequest;
import com.fgroupboss.ai.psm.inspection.model.vo.AbnormalRecordVO;
import com.fgroupboss.ai.psm.inspection.model.vo.InspectionStatisticsVO;
import com.fgroupboss.ai.psm.inspection.model.vo.InspectionTaskVO;
import com.fgroupboss.ai.psm.inspection.model.vo.OverdueScanResultVO;
import com.fgroupboss.ai.psm.inspection.model.vo.SignRecordVO;

import java.time.LocalDateTime;
import java.util.List;

public interface InspectionTaskService {

    PageResult<InspectionTaskVO> page(Long tenantId, String status, Long executorId, int pageNo, int pageSize);

    InspectionTaskVO getById(Long tenantId, Long id);

    InspectionTaskVO create(TaskCreateRequest request);

    InspectionTaskVO start(Long taskId, TaskStartRequest request);

    SignRecordVO signIn(Long taskId, TaskSignInRequest request);

    InspectionTaskVO submitItems(Long taskId, TaskItemSubmitRequest request);

    InspectionTaskVO complete(Long tenantId, Long taskId);

    AbnormalRecordVO registerAbnormal(Long taskId, TaskAbnormalRequest request);

    OverdueScanResultVO overdueScan(OverdueScanRequest request);

    InspectionStatisticsVO statistics(Long tenantId, LocalDateTime from, LocalDateTime to);

    List<InspectionTaskVO> listRecentByMajorHazard(Long tenantId, Long majorHazardId, int limit);
}
