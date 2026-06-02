package com.fgroupboss.ai.psm.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long tenantId;
    private Long userId;
    private String username;
    private String displayName;
    private String sessionId;
    private Long permissionVersion;
}
