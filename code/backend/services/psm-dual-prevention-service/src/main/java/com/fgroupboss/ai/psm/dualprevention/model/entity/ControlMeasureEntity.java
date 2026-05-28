package com.fgroupboss.ai.psm.dualprevention.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dp_control_measure")
public class ControlMeasureEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long riskEventId;
    private String measureType;
    private String measureContent;
    private String responsiblePost;
    private Integer checkCycleDays;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
