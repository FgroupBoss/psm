package com.fgroupboss.ai.psm.inspection.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("insp_task_item")
public class TaskItemEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long taskId;
    private Long checklistItemId;
    private Long routePointId;
    private String resultValue;
    private String resultStatus;
    private String photoUrls;
    private String remark;
    private LocalDateTime checkedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
