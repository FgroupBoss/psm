package com.fgroupboss.ai.psm.operation.workpermit.lifting.rule;

import com.fgroupboss.ai.psm.common.BusinessException;

import java.math.BigDecimal;

/**
 * 按吊物重量计算吊装级别：≤40t 一级，≤100t 二级，其余三级。
 */
public final class LiftingLevelCalculator {

    public static final String LEVEL_1 = "LEVEL_1";
    public static final String LEVEL_2 = "LEVEL_2";
    public static final String LEVEL_3 = "LEVEL_3";

    private LiftingLevelCalculator() {
    }

    public static String calculate(BigDecimal loadWeightT) {
        if (loadWeightT == null || loadWeightT.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "吊物重量必须大于0");
        }
        if (loadWeightT.compareTo(new BigDecimal("40")) <= 0) {
            return LEVEL_1;
        }
        if (loadWeightT.compareTo(new BigDecimal("100")) <= 0) {
            return LEVEL_2;
        }
        return LEVEL_3;
    }
}
