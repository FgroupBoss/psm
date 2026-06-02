package com.fgroupboss.ai.psm.identity.file.model.vo;

import lombok.Data;

import java.util.List;

/** 存储后端元数据：展示名、必填配置项与说明。 */
@Data
public class FileBackendSchemaVO {

    private String backend;
    private String label;
    private List<String> requiredFields;
    private String hint;
}
