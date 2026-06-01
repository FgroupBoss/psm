package com.fgroupboss.ai.psm.operation.workpermit.heightwork.rule;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 标准客观危险因素种子（不可删除，仅可确认与补充措施）。
 */
public final class HeightWorkStandardHazardFactors {

    private static final Map<String, String> FACTORS;

    static {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("WIND_G5", "阵风风力五级及以上");
        map.put("HOT_COLD_ENV", "高温或低温环境");
        map.put("RAIN_SNOW_ICE", "雨、雪、雾、结冰");
        map.put("COLD_WATER", "接触冷水温度低于12℃");
        map.put("SLIPPERY", "作业场地有冰、雪、霜、水、油等易滑物");
        map.put("LOW_LIGHT", "作业场所光线不足或能见度差");
        map.put("ELECTRIC", "接近或接触带电体");
        map.put("UNSTABLE", "摆动、立足处不稳或振动环境");
        map.put("OBJECT_STRIKE", "可能发生物体打击、机械伤害、灼伤等危险");
        FACTORS = Collections.unmodifiableMap(map);
    }

    private HeightWorkStandardHazardFactors() {
    }

    public static Map<String, String> all() {
        return FACTORS;
    }

    public static String nameOf(String code) {
        return FACTORS.get(code);
    }
}
