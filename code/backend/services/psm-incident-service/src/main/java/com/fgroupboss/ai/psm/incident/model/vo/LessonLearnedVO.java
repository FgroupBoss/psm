package com.fgroupboss.ai.psm.incident.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LessonLearnedVO {

    private Long id;
    private Long tenantId;
    private Long incidentId;
    private String lessonDesc;
    private String actionType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
