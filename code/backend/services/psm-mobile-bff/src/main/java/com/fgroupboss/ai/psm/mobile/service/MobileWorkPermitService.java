package com.fgroupboss.ai.psm.mobile.service;

import com.fgroupboss.ai.psm.mobile.client.WorkPermitClient;
import com.fgroupboss.ai.psm.mobile.client.dto.AcceptanceRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.CheckInRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.GasTestRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.MeasureConfirmRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.MonitorRecordRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.PermitActionRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.SafetyMeasureRequest;
import com.fgroupboss.ai.psm.mobile.client.dto.SitePermitRequest;
import com.fgroupboss.ai.psm.mobile.client.vo.GasTestVO;
import com.fgroupboss.ai.psm.mobile.client.vo.MonitorRecordVO;
import com.fgroupboss.ai.psm.mobile.client.vo.SafetyMeasureVO;
import com.fgroupboss.ai.psm.mobile.client.vo.SiteConfirmVO;
import com.fgroupboss.ai.psm.mobile.client.vo.WorkPermitDetailVO;
import com.fgroupboss.ai.psm.mobile.client.vo.WorkPermitVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

/**
 * 移动端作业票动作编排，委托 work-permit-service。
 */
@Service
@RequiredArgsConstructor
public class MobileWorkPermitService {

    private final WorkPermitClient workPermitClient;

    public WorkPermitDetailVO getDetail(Long tenantId, Long id) {
        return workPermitClient.getDetail(tenantId, id);
    }

    public SiteConfirmVO checkIn(Long id, CheckInRequest request, HttpHeaders contextHeaders) {
        return workPermitClient.checkIn(id, request, contextHeaders);
    }

    public GasTestVO addGasTest(Long id, GasTestRequest request, HttpHeaders contextHeaders) {
        return workPermitClient.addGasTest(id, request, contextHeaders);
    }

    public SafetyMeasureVO confirmMeasure(Long id, MeasureConfirmRequest request, HttpHeaders contextHeaders) {
        SafetyMeasureRequest downstream = new SafetyMeasureRequest();
        downstream.setTenantId(request.getTenantId());
        downstream.setConfirmStatus(request.getConfirmStatus());
        downstream.setRemark(request.getRemark());
        downstream.setAttachmentRef(request.getAttachmentRef());
        return workPermitClient.confirmSafetyMeasure(id, request.getMeasureId(), downstream, contextHeaders);
    }

    public WorkPermitVO sitePermit(Long id, SitePermitRequest request, HttpHeaders contextHeaders) {
        return workPermitClient.sitePermit(id, request, contextHeaders);
    }

    public MonitorRecordVO addMonitorRecord(Long id, MonitorRecordRequest request, HttpHeaders contextHeaders) {
        return workPermitClient.addMonitorRecord(id, request, contextHeaders);
    }

    public WorkPermitVO suspend(Long id, Long tenantId, PermitActionRequest request, HttpHeaders contextHeaders) {
        return workPermitClient.suspend(tenantId, id, defaultAction(request), contextHeaders);
    }

    public WorkPermitVO resume(Long id, Long tenantId, PermitActionRequest request, HttpHeaders contextHeaders) {
        return workPermitClient.resume(tenantId, id, defaultAction(request), contextHeaders);
    }

    public WorkPermitVO acceptance(Long id, AcceptanceRequest request, HttpHeaders contextHeaders) {
        return workPermitClient.acceptance(id, request, contextHeaders);
    }

    private PermitActionRequest defaultAction(PermitActionRequest request) {
        return request == null ? new PermitActionRequest() : request;
    }
}
