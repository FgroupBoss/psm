package com.fgroupboss.ai.psm.inspection.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("insp_checklist_template")
public class ChecklistTemplateEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String templateCode;
    private String templateName;
    private String category;
    private String status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
