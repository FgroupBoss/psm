package com.fgroupboss.ai.psm.incidentgovernance.integration.model.vo;

import lombok.Data;

import java.util.Map;

/**
 * 上报报文预览（脱敏，不落库发送）。
 */
@Data
public class RegReportPreviewVO {

    private String platformCode;
    private String dataDomain;
    private int recordCount;
    private String payloadDigest;
    private Map<String, Object> payload;
}
