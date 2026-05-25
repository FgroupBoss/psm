package com.fgroupboss.ai.psm.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GatewayAuthServiceTest {

    private final GatewayAuthService service = new GatewayAuthService(
            new GatewayProperties(),
            new RestTemplate(),
            new ObjectMapper());

    @Test
    void parsePrincipalReadsAuthResponseData() {
        AuthPrincipal principal = service.parsePrincipal("{\"code\":0,\"message\":\"success\",\"data\":{\"id\":7,\"tenantId\":1,\"username\":\"admin\",\"displayName\":\"系统管理员\"}}");

        assertEquals(7L, principal.getId());
        assertEquals(1L, principal.getTenantId());
        assertEquals("admin", principal.getUsername());
        assertEquals("系统管理员", principal.getDisplayName());
    }

    @Test
    void parsePrincipalRejectsFailureResponse() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.parsePrincipal("{\"code\":401,\"message\":\"access token expired\",\"data\":null}"));

        assertEquals(401, exception.getCode());
        assertEquals("access token expired", exception.getMessage());
    }

    @Test
    void parsePrincipalRejectsMissingUserIdentity() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.parsePrincipal("{\"code\":0,\"message\":\"success\",\"data\":{\"username\":\"admin\"}}"));

        assertEquals(401, exception.getCode());
        assertEquals("invalid auth response", exception.getMessage());
    }
}
