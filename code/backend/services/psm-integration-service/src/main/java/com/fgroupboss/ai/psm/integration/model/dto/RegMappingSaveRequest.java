package com.fgroupboss.ai.psm.integration.model.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量保存字段/编码映射。
 */
@Data
public class RegMappingSaveRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "platformCode is required")
    private String platformCode;

    @Valid
    private List<RegFieldMappingItem> fieldMappings;

    @Valid
    private List<RegCodeMappingItem> codeMappings;

    @Data
    public static class RegFieldMappingItem {

        private Long id;

        @NotBlank(message = "dataDomain is required")
        private String dataDomain;

        @NotBlank(message = "sourceField is required")
        private String sourceField;

        @NotBlank(message = "targetField is required")
        private String targetField;

        private String transformRule;

        private Integer enabled;
    }

    @Data
    public static class RegCodeMappingItem {

        private Long id;

        @NotBlank(message = "mappingType is required")
        private String mappingType;

        @NotBlank(message = "sourceCode is required")
        private String sourceCode;

        @NotBlank(message = "targetCode is required")
        private String targetCode;

        private String description;

        private Integer enabled;
    }
}
