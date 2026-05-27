package com.fgroupboss.ai.psm.mobile.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 移动端附件上传响应（来自 psm-file-service）。
 */
@Data
public class FileUploadVO {

    private String fileId;
    private String fileName;
    private String contentType;
    private Long sizeBytes;
    private String sha256;
    private String url;
    private Date uploadedAt;
}
