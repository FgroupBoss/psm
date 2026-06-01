package com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("hot_work_workflow_template")
public class HotWorkWorkflowTemplateEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String templateCode;
    private String templateName;
    private String hotWorkLevel;
    private String areaScopeType;
    private String areaIdsJson;
    private Integer versionNo;
    private String status;
    private String snapshotJson;
    private String remark;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
