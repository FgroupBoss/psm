package com.fgroupboss.ai.psm.identity.file.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/** 租户存储配置档（密钥字段已脱敏）。 */
@Data
public class FileStorageProfileVO {

    private Long id;
    private Long tenantId;
    private String profileCode;
    private String profileName;
    private String storageBackend;
    private Map<String, Object> config;
    private boolean enabled;
    private boolean defaultProfile;
    private String lastTestStatus;
    private Date lastTestAt;
    private boolean configured;
}
