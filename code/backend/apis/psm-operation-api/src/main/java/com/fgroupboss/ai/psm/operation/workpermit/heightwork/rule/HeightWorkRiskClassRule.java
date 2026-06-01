package com.fgroupboss.ai.psm.operation.workpermit.heightwork.rule;

public final class HeightWorkRiskClassRule {

    public static final String CLASS_A = "A";
    public static final String CLASS_B = "B";

    private HeightWorkRiskClassRule() {
    }

    public static String calculate(int hazardFactorCount) {
        return hazardFactorCount > 0 ? CLASS_B : CLASS_A;
    }
}
