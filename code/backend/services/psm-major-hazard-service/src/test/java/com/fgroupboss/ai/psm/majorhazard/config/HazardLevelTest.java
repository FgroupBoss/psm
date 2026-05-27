package com.fgroupboss.ai.psm.majorhazard.config;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HazardLevelTest {

    @Test
    void maxLevelShouldPickMostSevere() {
        assertEquals("LEVEL_1", HazardLevel.maxLevel(Arrays.asList("LEVEL_2", "LEVEL_1", "LEVEL_3")));
    }

    @Test
    void maxLevelShouldReturnNullForEmptyList() {
        assertNull(HazardLevel.maxLevel(Collections.<String>emptyList()));
    }
}
