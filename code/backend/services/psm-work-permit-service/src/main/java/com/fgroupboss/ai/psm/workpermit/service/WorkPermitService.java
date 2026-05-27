package com.fgroupboss.ai.psm.workpermit.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.workpermit.model.dto.AcceptanceRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.CheckInRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.GasTestRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.MobileDraftSyncRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.MonitorRecordRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.PermitActionRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.PreCheckRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.RiskAnalysisRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.SafetyMeasureRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.SitePermitRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.WorkPermitRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.WorkPermitWorkerRequest;
import com.fgroupboss.ai.psm.workpermit.model.vo.AcceptanceRecordVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.GasTestVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.MobileDraftSyncResultVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.MonitorRecordVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.PreCheckResultVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.RiskAnalysisVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.SafetyMeasureVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.SiteConfirmVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.TimelineItemVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.WorkPermitDetailVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.WorkPermitHealthVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.WorkPermitVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.WorkPermitWorkerVO;

import java.util.List;

public interface WorkPermitService {

    WorkPermitHealthVO health();

    PageResult<WorkPermitVO> page(Long tenantId, String keyword, String status, String workType,
                                  Long areaId, Long hazardId, int pageNo, int pageSize);

    List<WorkPermitVO> listByHazard(Long tenantId, Long hazardId);

    WorkPermitDetailVO getDetail(Long tenantId, Long id);

    WorkPermitVO create(WorkPermitRequest request, String operator);

    WorkPermitVO update(Long id, WorkPermitRequest request, String operator);

    List<WorkPermitWorkerVO> listWorkers(Long tenantId, Long id);

    WorkPermitWorkerVO addWorker(Long tenantId, Long id, WorkPermitWorkerRequest request, String operator);

    void removeWorker(Long tenantId, Long id, Long workerId, String operator);

    WorkPermitVO submit(Long tenantId, Long id, String operator);

    WorkPermitVO approve(Long tenantId, Long id, PermitActionRequest request, String operator);

    WorkPermitVO returnPermit(Long tenantId, Long id, PermitActionRequest request, String operator);

    WorkPermitVO reject(Long tenantId, Long id, PermitActionRequest request, String operator);

    List<RiskAnalysisVO> listRiskAnalysis(Long tenantId, Long id);

    RiskAnalysisVO saveRiskAnalysis(Long tenantId, Long id, RiskAnalysisRequest request, String operator);

    List<SafetyMeasureVO> listSafetyMeasures(Long tenantId, Long id);

    SafetyMeasureVO confirmSafetyMeasure(Long tenantId, Long id, Long measureId,
                                         SafetyMeasureRequest request, String operator);

    List<GasTestVO> listGasTests(Long tenantId, Long id);

    GasTestVO addGasTest(Long tenantId, Long id, GasTestRequest request, String operator);

    PreCheckResultVO preCheck(Long tenantId, Long id, PreCheckRequest request);

    WorkPermitVO sitePermit(Long tenantId, Long id, SitePermitRequest request, String operator);

    SiteConfirmVO checkIn(Long tenantId, Long id, CheckInRequest request, String operator);

    List<MonitorRecordVO> listMonitorRecords(Long tenantId, Long id);

    MonitorRecordVO addMonitorRecord(Long tenantId, Long id, MonitorRecordRequest request, String operator);

    WorkPermitVO suspend(Long tenantId, Long id, PermitActionRequest request, String operator);

    WorkPermitVO resume(Long tenantId, Long id, PermitActionRequest request, String operator);

    WorkPermitVO terminate(Long tenantId, Long id, PermitActionRequest request, String operator);

    WorkPermitVO acceptance(Long tenantId, Long id, AcceptanceRequest request, String operator);

    List<TimelineItemVO> timeline(Long tenantId, Long id);

    MobileDraftSyncResultVO syncMobileDraft(MobileDraftSyncRequest request);
}
