package com.fgroupboss.ai.psm.incidentgovernance.integration.service.support;

import com.fgroupboss.ai.psm.incidentgovernance.integration.model.entity.RegCodeMappingEntity;
import com.fgroupboss.ai.psm.incidentgovernance.integration.model.entity.RegFieldMappingEntity;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 按映射配置组装监管上报报文（基于业务服务真实抽取数据）。
 */
public final class RegPayloadBuilder {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private RegPayloadBuilder() {
    }

    public static Map<String, Object> buildPayload(Long tenantId, String platformCode, String dataDomain,
                                                   LocalDateTime windowStart, LocalDateTime windowEnd,
                                                   List<RegFieldMappingEntity> fieldMappings,
                                                   List<RegCodeMappingEntity> codeMappings,
                                                   List<Map<String, Object>> sourceRecords) {
        Map<String, Object> root = new LinkedHashMap<String, Object>();
        root.put("tenantId", tenantId);
        root.put("platformCode", platformCode);
        root.put("dataDomain", dataDomain);
        root.put("windowStart", windowStart == null ? null : ISO.format(windowStart));
        root.put("windowEnd", windowEnd == null ? null : ISO.format(windowEnd));
        root.put("reportTime", ISO.format(LocalDateTime.now()));

        List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> sources = sourceRecords == null ? new ArrayList<Map<String, Object>>() : sourceRecords;
        if (sources.isEmpty()) {
            records.add(buildFallbackRecord(fieldMappings));
        } else {
            for (Map<String, Object> source : sources) {
                records.add(mapRecord(source, fieldMappings, codeMappings));
            }
        }
        root.put("records", records);
        root.put("recordCount", records.size());

        if (codeMappings != null && !codeMappings.isEmpty()) {
            Map<String, String> codes = new LinkedHashMap<String, String>();
            for (RegCodeMappingEntity code : codeMappings) {
                if (code.getEnabled() != null && code.getEnabled() == 0) {
                    continue;
                }
                codes.put(code.getMappingType() + ":" + code.getSourceCode(), code.getTargetCode());
            }
            root.put("codeMappings", codes);
        }
        return root;
    }

    public static String digest(Map<String, Object> payload) {
        String raw = String.valueOf(payload);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(raw.hashCode());
        }
    }

    private static Map<String, Object> mapRecord(Map<String, Object> source,
                                                 List<RegFieldMappingEntity> fieldMappings,
                                                 List<RegCodeMappingEntity> codeMappings) {
        if (fieldMappings == null || fieldMappings.isEmpty()) {
            return new LinkedHashMap<String, Object>(source);
        }
        Map<String, Object> target = new LinkedHashMap<String, Object>();
        for (RegFieldMappingEntity mapping : fieldMappings) {
            if (mapping.getEnabled() != null && mapping.getEnabled() == 0) {
                continue;
            }
            Object value = resolveSourceValue(source, mapping.getSourceField());
            target.put(mapping.getTargetField(), applyCodeMapping(value, mapping, codeMappings));
        }
        return target;
    }

    private static Map<String, Object> buildFallbackRecord(List<RegFieldMappingEntity> fieldMappings) {
        Map<String, Object> sample = new LinkedHashMap<String, Object>();
        if (fieldMappings == null || fieldMappings.isEmpty()) {
            sample.put("notice", "no source records and no field mappings");
            return sample;
        }
        for (RegFieldMappingEntity mapping : fieldMappings) {
            if (mapping.getEnabled() != null && mapping.getEnabled() == 0) {
                continue;
            }
            sample.put(mapping.getTargetField(), null);
        }
        return sample;
    }

    private static Object resolveSourceValue(Map<String, Object> source, String sourceField) {
        if (!StringUtils.hasText(sourceField) || source == null) {
            return null;
        }
        if (source.containsKey(sourceField)) {
            return source.get(sourceField);
        }
        String camel = toCamelCase(sourceField);
        if (source.containsKey(camel)) {
            return source.get(camel);
        }
        return null;
    }

    private static Object applyCodeMapping(Object value, RegFieldMappingEntity fieldMapping,
                                           List<RegCodeMappingEntity> codeMappings) {
        if (value == null || codeMappings == null || codeMappings.isEmpty()) {
            return value;
        }
        String sourceCode = String.valueOf(value);
        String mappingType = StringUtils.hasText(fieldMapping.getTransformRule())
                ? fieldMapping.getTransformRule().trim() : fieldMapping.getSourceField();
        for (RegCodeMappingEntity code : codeMappings) {
            if (code.getEnabled() != null && code.getEnabled() == 0) {
                continue;
            }
            if (mappingType.equals(code.getMappingType()) && sourceCode.equals(code.getSourceCode())) {
                return code.getTargetCode();
            }
        }
        return value;
    }

    private static String toCamelCase(String sourceField) {
        if (!sourceField.contains("_")) {
            return sourceField;
        }
        StringBuilder sb = new StringBuilder();
        boolean upper = false;
        for (char c : sourceField.toCharArray()) {
            if (c == '_') {
                upper = true;
                continue;
            }
            if (upper) {
                sb.append(Character.toUpperCase(c));
                upper = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
