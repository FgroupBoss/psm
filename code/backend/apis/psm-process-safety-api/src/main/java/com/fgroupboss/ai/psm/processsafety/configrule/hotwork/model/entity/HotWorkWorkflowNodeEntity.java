package com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("hot_work_workflow_node")
public class HotWorkWorkflowNodeEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long templateId;
    private Integer nodeSeq;
    private String nodeName;
    private String signMode;
    private String approverRuleType;
    private String approverRuleValue;
    private Integer timeoutHours;
    private Integer required;
    private Date createdAt;
    private Date updatedAt;
    private Integer deleted;
}
