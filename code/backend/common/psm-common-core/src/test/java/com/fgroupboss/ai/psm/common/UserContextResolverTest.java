package com.fgroupboss.ai.psm.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserContextResolverTest {

    @Test
    void operatorPrefersUserId() {
        assertEquals("8", UserContextResolver.operator("8", "admin", "system"));
    }

    @Test
    void operatorFallsBackToUsernameThenDefaultOperator() {
        assertEquals("admin", UserContextResolver.operator(" ", "admin", "system"));
        assertEquals("system", UserContextResolver.operator(null, "", "system"));
    }

    @Test
    void requireContextParsesHeaders() {
        UserContext context = UserContextResolver.requireContext("10", "8", "admin", "管理员");
        assertEquals(Long.valueOf(10L), context.getTenantId());
        assertEquals(Long.valueOf(8L), context.getUserId());
        assertEquals("admin", context.getUsername());
    }
}
