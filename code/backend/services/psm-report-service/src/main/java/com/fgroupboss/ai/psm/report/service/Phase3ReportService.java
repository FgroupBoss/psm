package com.fgroupboss.ai.psm.report.service;

import com.fgroupboss.ai.psm.report.model.vo.Phase3ReportSummaryVO;

public interface Phase3ReportService {
    Phase3ReportSummaryVO summary(Long tenantId);
}
