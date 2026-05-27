package com.fgroupboss.ai.psm.majorhazard.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class HazardAttachmentRequest {

    @NotBlank(message = "attachmentType is required")
    private String attachmentType;
    @NotNull(message = "fileId is required")
    private Long fileId;
    private String fileName;
}
