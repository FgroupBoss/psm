package com.fgroupboss.ai.psm.report.client.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RemoteWorkPermitDetailVO {

    private RemoteWorkPermitVO permit;
    private List<RemoteWorkPermitWorkerVO> workers = new ArrayList<RemoteWorkPermitWorkerVO>();
    private List<RemoteSafetyMeasureVO> safetyMeasures = new ArrayList<RemoteSafetyMeasureVO>();
    private List<RemoteGasTestVO> gasTests = new ArrayList<RemoteGasTestVO>();
    private List<RemoteSiteConfirmVO> siteConfirms = new ArrayList<RemoteSiteConfirmVO>();
}
