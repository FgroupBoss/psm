package com.fgroupboss.ai.psm.identity.file.model.vo;

import lombok.Data;

/** 对象存储限时下载 URL 及有效期。 */
@Data
public class FilePresignVO {

    private Long fileId;
    private String url;
    private int expiresInSeconds;
    private String downloadMode;
}
