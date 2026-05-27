package com.fgroupboss.ai.psm.report.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("acceptance_test_case")
public class AcceptanceTestCaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String caseCode;
    private String caseName;
    private String module;
    private String scenario;
    private String expectedResult;
    private String priority;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}
