package com.fgroupboss.ai.psm.identity.gateway.service;

import com.fgroupboss.ai.psm.identity.gateway.AuthPrincipal;

public interface GatewayAuthService {

    AuthPrincipal authenticate(String authorization);
}
