package com.fgroupboss.ai.psm.report.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("report_export_task")
public class ReportExportTaskEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String reportType;
    private String exportFormat;
    private String queryParamsJson;
    private String status;
    private String filePath;
    private String errorMessage;
    private String requestedBy;
    private Date startedAt;
    private Date completedAt;
    private Date createdAt;
    private Date updatedAt;
}
