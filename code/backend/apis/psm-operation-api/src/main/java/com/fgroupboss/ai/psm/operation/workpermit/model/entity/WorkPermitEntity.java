package com.fgroupboss.ai.psm.operation.workpermit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("work_permit")
public class WorkPermitEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String permitNo;
    private String workType;
    private String hotWorkLevel;
    private Long workflowTemplateId;
    private Integer workflowTemplateVersion;
    private String workflowTemplateName;
    private String status;
    private String title;
    private String workContent;
    private Long areaId;
    private Long unitId;
    private Long equipmentId;
    private Long hazardId;
    private Long contractorCompanyId;
    private Date planStartAt;
    private Date planEndAt;
    private Date actualStartAt;
    private Date actualEndAt;
    private Long supervisorUserId;
    private Long permitIssuerUserId;
    private Long guardianUserId;
    private String rejectReason;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
