package com.fgroupboss.ai.psm.incidentgovernance.incident.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("lesson_learned")
public class LessonLearnedEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long incidentId;
    private String lessonDesc;
    private String actionType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
