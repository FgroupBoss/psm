package com.fgroupboss.ai.psm.mobile.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 移动端附件上传 mock 响应。
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
