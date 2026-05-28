package com.fgroupboss.ai.psm.video.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AiEventMediaItem {

    @NotBlank(message = "mediaType is required")
    private String mediaType;

    private Long fileId;
    private String mediaUrl;
}
