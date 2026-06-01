package com.fgroupboss.ai.psm.incidentgovernance.report.client.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RemoteGasTestVO {

    private Long id;
    private String testResult;
    private Date testedAt;
}
