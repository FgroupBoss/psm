package com.fgroupboss.ai.psm.incidentgovernance.integration.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.dto.RegReportPreviewRequest;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.dto.RegReportTriggerRequest;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegReconciliationVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegReportPreviewVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegReportReceiptVO;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo.RegReportTaskVO;

public interface RegReportService {

    PageResult<RegReportTaskVO> pageTasks(Long tenantId, String platformCode, String dataDomain,
                                          String status, int pageNo, int pageSize);

    RegReportTaskVO trigger(RegReportTriggerRequest request);

    RegReportTaskVO retry(Long tenantId, Long taskId);

    PageResult<RegReportReceiptVO> pageReceipts(Long tenantId, Long taskId, int pageNo, int pageSize);

    RegReconciliationVO reconciliation(Long tenantId, String platformCode, String dataDomain);

    RegReportPreviewVO preview(RegReportPreviewRequest request);
}
