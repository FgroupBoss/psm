package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WorkPermitDetailVO {

    private WorkPermitVO permit;
    private List<WorkPermitWorkerVO> workers = new ArrayList<WorkPermitWorkerVO>();
    private List<RiskAnalysisVO> riskAnalyses = new ArrayList<RiskAnalysisVO>();
    private List<SafetyMeasureVO> safetyMeasures = new ArrayList<SafetyMeasureVO>();
    private List<ApprovalRecordVO> approvalRecords = new ArrayList<ApprovalRecordVO>();
    private List<GasTestVO> gasTests = new ArrayList<GasTestVO>();
    private List<SiteConfirmVO> siteConfirms = new ArrayList<SiteConfirmVO>();
    private List<MonitorRecordVO> monitorRecords = new ArrayList<MonitorRecordVO>();
    private List<AcceptanceRecordVO> acceptanceRecords = new ArrayList<AcceptanceRecordVO>();
}

