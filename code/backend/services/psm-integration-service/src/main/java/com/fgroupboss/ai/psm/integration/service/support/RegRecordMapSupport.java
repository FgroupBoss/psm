package com.fgroupboss.ai.psm.integration.service.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 将业务 VO 转为监管报文字段源 Map。
 */
public final class RegRecordMapSupport {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private RegRecordMapSupport() {
    }

    public static List<Map<String, Object>> toMaps(List<?> records) {
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        if (records == null) {
            return maps;
        }
        for (Object record : records) {
            maps.add(toMap(record));
        }
        return maps;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object record) {
        if (record == null) {
            return new LinkedHashMap<String, Object>();
        }
        if (record instanceof Map) {
            return new LinkedHashMap<String, Object>((Map<String, Object>) record);
        }
        return MAPPER.convertValue(record, new TypeReference<LinkedHashMap<String, Object>>() {
        });
    }
}
