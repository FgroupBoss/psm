package com.fgroupboss.ai.psm.identity.file.model.vo;

import lombok.Data;

@Data
public class FilePresignVO {

    private Long fileId;
    private String url;
    private int expiresInSeconds;
    private String downloadMode;
}
