package com.fgroupboss.ai.psm.report.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ReportExportTaskVO {

    private Long id;
    private Long tenantId;
    private String reportType;
    private String exportFormat;
    private String status;
    private String filePath;
    private String downloadUrl;
    private String errorMessage;
    private String requestedBy;
    private Date startedAt;
    private Date completedAt;
    private Date createdAt;
}
