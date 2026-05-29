package com.fgroupboss.ai.psm.pssr.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class PssrIssueRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotNull(message = "projectId is required")
    private Long projectId;
    private Long checkItemId;
    @NotBlank(message = "issueLevel is required")
    private String issueLevel;
    @NotBlank(message = "description is required")
    private String description;
    private Long ownerUserId;
    private LocalDateTime dueAt;
    private Integer closeRequiredBeforeStartup;
}
