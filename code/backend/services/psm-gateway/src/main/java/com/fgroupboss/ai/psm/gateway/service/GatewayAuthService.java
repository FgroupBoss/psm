package com.fgroupboss.ai.psm.gateway.service;

import com.fgroupboss.ai.psm.gateway.AuthPrincipal;

public interface GatewayAuthService {

    AuthPrincipal authenticate(String authorization);
}
