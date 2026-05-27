package com.fgroupboss.ai.psm.majorhazard.model.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ResponsibilityReplaceRequest {

    @NotEmpty(message = "responsibilities is required")
    @Valid
    private List<ResponsibilityRequest> responsibilities;
}
