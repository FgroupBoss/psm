package com.fgroupboss.ai.psm.report.service;

import com.fgroupboss.ai.psm.report.model.vo.Phase2ReportSummaryVO;

/**
 * 二期模块跨服务聚合报表。
 */
public interface Phase2ReportService {

    Phase2ReportSummaryVO summary(Long tenantId);
}
