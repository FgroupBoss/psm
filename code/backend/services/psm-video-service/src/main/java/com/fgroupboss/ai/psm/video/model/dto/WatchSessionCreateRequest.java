package com.fgroupboss.ai.psm.video.model.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class WatchSessionCreateRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotNull(message = "workPermitId is required")
    private Long workPermitId;

    @NotEmpty(message = "cameraIds is required")
    private List<Long> cameraIds;

    private Long operatorId;
    private String operatorName;
    private String remark;
}
