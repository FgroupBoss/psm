package com.fgroupboss.ai.psm.realtime.video.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AiEventIgnoreRequest {

    @NotBlank(message = "reason is required")
    private String reason;
}
