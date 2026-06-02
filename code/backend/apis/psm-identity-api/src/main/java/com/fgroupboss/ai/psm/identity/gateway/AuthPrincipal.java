package com.fgroupboss.ai.psm.identity.gateway;

import lombok.Data;

@Data
public class AuthPrincipal {

    private Long id;
    private Long tenantId;
    private String username;
    private String displayName;
    private Long permissionVersion;
}
