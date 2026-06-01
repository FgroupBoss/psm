package com.fgroupboss.ai.psm.incidentgovernance.incident.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LessonLearnedRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "lessonDesc is required")
    private String lessonDesc;

    @NotBlank(message = "actionType is required")
    private String actionType;
}
