package com.fgroupboss.ai.psm.identity.file.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/** 保存租户存储配置档的请求体。 */
@Data
public class FileStorageProfileRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "profileCode is required")
    private String profileCode;

    @NotBlank(message = "profileName is required")
    private String profileName;

    @NotBlank(message = "storageBackend is required")
    private String storageBackend;

    private Map<String, Object> config = new HashMap<String, Object>();
    private Boolean enabled = true;
    private Boolean defaultProfile = false;
}
