package com.fgroupboss.ai.psm.operation.workpermit.heightwork.rule;

import com.fgroupboss.ai.psm.common.BusinessException;

import java.math.BigDecimal;

/**
 * 按 GB 30871 / GB 3608 边界计算高度级别。
 */
public final class HeightWorkLevelCalculator {

    public static final String LEVEL_1 = "LEVEL_1";
    public static final String LEVEL_2 = "LEVEL_2";
    public static final String LEVEL_3 = "LEVEL_3";
    public static final String LEVEL_4 = "LEVEL_4";

    private HeightWorkLevelCalculator() {
    }

    public static String calculate(BigDecimal workHeightM) {
        if (workHeightM == null || workHeightM.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "作业高度必须大于0");
        }
        if (workHeightM.compareTo(new BigDecimal("2")) < 0) {
            return LEVEL_1;
        }
        if (workHeightM.compareTo(new BigDecimal("5")) <= 0) {
            return LEVEL_1;
        }
        if (workHeightM.compareTo(new BigDecimal("15")) <= 0) {
            return LEVEL_2;
        }
        if (workHeightM.compareTo(new BigDecimal("30")) <= 0) {
            return LEVEL_3;
        }
        return LEVEL_4;
    }

    public static int levelOrder(String level) {
        if (LEVEL_1.equals(level)) {
            return 1;
        }
        if (LEVEL_2.equals(level)) {
            return 2;
        }
        if (LEVEL_3.equals(level)) {
            return 3;
        }
        if (LEVEL_4.equals(level)) {
            return 4;
        }
        return 0;
    }
}
