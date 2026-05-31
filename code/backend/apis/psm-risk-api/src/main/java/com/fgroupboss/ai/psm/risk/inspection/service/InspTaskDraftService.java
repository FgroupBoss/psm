package com.fgroupboss.ai.psm.risk.inspection.service;

import com.fgroupboss.ai.psm.risk.inspection.model.dto.TaskDraftSyncRequest;
import com.fgroupboss.ai.psm.risk.inspection.model.vo.TaskDraftSyncResultVO;

public interface InspTaskDraftService {

    TaskDraftSyncResultVO syncDraft(TaskDraftSyncRequest request);
}
