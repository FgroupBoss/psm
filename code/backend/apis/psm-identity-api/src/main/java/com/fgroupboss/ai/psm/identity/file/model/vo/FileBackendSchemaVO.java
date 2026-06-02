package com.fgroupboss.ai.psm.identity.file.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class FileBackendSchemaVO {

    private String backend;
    private String label;
    private List<String> requiredFields;
    private String hint;
}
