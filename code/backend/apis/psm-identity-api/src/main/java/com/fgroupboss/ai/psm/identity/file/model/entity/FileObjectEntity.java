package com.fgroupboss.ai.psm.identity.file.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("file_object")
public class FileObjectEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String fileName;
    private String contentType;
    private Long sizeBytes;
    private String sha256;
    private String storageBackend;
    private Long storageProfileId;
    private String bucket;
    private String objectKey;
    private String region;
    private String storagePath;
    private String bizType;
    private Long bizId;
    private String status;
    private String createdBy;
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
