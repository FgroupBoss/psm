package com.fgroupboss.ai.psm.incidentgovernance.report.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("acceptance_test_run")
public class AcceptanceTestRunEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long caseId;
    private String runNo;
    private String executorName;
    private String runStatus;
    private String evidenceRef;
    private String remark;
    private Date executedAt;
    private Date createdAt;
    private Date updatedAt;
}
