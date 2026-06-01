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
    private HeightWorkDetailVO heightWorkDetail;
    private List<HeightWorkHazardFactorVO> heightWorkHazardFactors = new ArrayList<HeightWorkHazardFactorVO>();
    private HeightWorkFlowProgressVO heightWorkFlowProgress;
    private ConfinedSpaceDetailVO confinedSpaceDetail;
    private List<ConfinedSpaceEntryRecordVO> confinedSpaceEntryRecords = new ArrayList<ConfinedSpaceEntryRecordVO>();
    private ConfinedSpaceRescuePlanVO confinedSpaceRescuePlan;
    private HeightWorkFlowProgressVO confinedSpaceFlowProgress;
    private LiftingWorkDetailVO liftingDetail;
    private List<LiftingEquipmentCheckVO> liftingEquipmentChecks = new ArrayList<LiftingEquipmentCheckVO>();
    private List<LiftingTrialRecordVO> liftingTrialRecords = new ArrayList<LiftingTrialRecordVO>();
    private HeightWorkFlowProgressVO liftingFlowProgress;
    private TempElectricDetailVO tempElectricDetail;
    private List<TempElectricFacilityVO> tempElectricFacilities = new ArrayList<TempElectricFacilityVO>();
    private List<TempElectricInspectionVO> tempElectricInspections = new ArrayList<TempElectricInspectionVO>();
    private HeightWorkFlowProgressVO tempElectricFlowProgress;
    private BlindPlateWorkDetailVO blindPlateDetail;
    private List<BlindPlateActionRecordVO> blindPlateActionRecords = new ArrayList<BlindPlateActionRecordVO>();
    private ExcavationWorkDetailVO excavationDetail;
    private List<ExcavationUndergroundFacilityVO> excavationFacilities = new ArrayList<ExcavationUndergroundFacilityVO>();
    private List<ExcavationCountersignVO> excavationCountersigns = new ArrayList<ExcavationCountersignVO>();
    private List<ExcavationSiteCheckVO> excavationSiteChecks = new ArrayList<ExcavationSiteCheckVO>();
    private RoadBreakDetailVO roadBreakDetail;
    private RoadBreakTrafficPlanVO roadBreakTrafficPlan;
    private List<RoadBreakSiteControlVO> roadBreakSiteControls = new ArrayList<RoadBreakSiteControlVO>();
}

