package com.fgroupboss.ai.psm.integration.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class RegMappingVO {

    private List<RegFieldMappingVO> fieldMappings;
    private List<RegCodeMappingVO> codeMappings;

    @Data
    public static class RegFieldMappingVO {
        private Long id;
        private Long tenantId;
        private String platformCode;
        private String dataDomain;
        private String sourceField;
        private String targetField;
        private String transformRule;
        private Integer enabled;
    }

    @Data
    public static class RegCodeMappingVO {
        private Long id;
        private Long tenantId;
        private String platformCode;
        private String mappingType;
        private String sourceCode;
        private String targetCode;
        private String description;
        private Integer enabled;
    }
}
