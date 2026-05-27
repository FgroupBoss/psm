package com.fgroupboss.ai.psm.report.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ReportHealthVO {

    private String service;
    private String module;
    private String version;
    private long exportTaskCount;
    private long acceptanceCaseCount;
    private Date refreshedAt;
}
