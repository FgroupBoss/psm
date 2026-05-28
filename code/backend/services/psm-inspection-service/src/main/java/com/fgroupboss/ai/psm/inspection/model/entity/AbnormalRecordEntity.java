package com.fgroupboss.ai.psm.inspection.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("insp_abnormal_record")
public class AbnormalRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long taskId;
    private Long taskItemId;
    private Long routePointId;
    private String abnormalDesc;
    private String photoUrls;
    private String severity;
    private String handleStatus;
    private Long hazardDraftId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
