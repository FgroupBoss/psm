package com.fgroupboss.ai.psm.identity.file.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class FileObjectVO {

    private Long id;
    private Long tenantId;
    private String fileName;
    private String contentType;
    private Long sizeBytes;
    private String sha256;
    private String bizType;
    private Long bizId;
    private String status;
    private String storageBackend;
    private String downloadUrl;
    private String previewUrl;
    private String presignUrl;
    private Date createdAt;
}
