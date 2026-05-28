package com.fgroupboss.ai.psm.inspection.service;

import com.fgroupboss.ai.psm.inspection.model.dto.TaskDraftSyncRequest;
import com.fgroupboss.ai.psm.inspection.model.vo.TaskDraftSyncResultVO;

public interface InspTaskDraftService {

    TaskDraftSyncResultVO syncDraft(TaskDraftSyncRequest request);
}
