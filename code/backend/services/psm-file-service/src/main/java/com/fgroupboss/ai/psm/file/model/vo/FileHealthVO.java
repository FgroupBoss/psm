package com.fgroupboss.ai.psm.file.model.vo;

import lombok.Data;

@Data
public class FileHealthVO {

    private String service;
    private String module;
    private String version;
    private long fileCount;
}
