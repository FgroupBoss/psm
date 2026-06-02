package com.fgroupboss.ai.psm.identity.file.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("file_storage_profile")
public class FileStorageProfileEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String profileCode;
    private String profileName;
    private String storageBackend;
    private String configJson;
    private Integer enabled;
    private Integer isDefault;
    private String lastTestStatus;
    private Date lastTestAt;
    private Date createdAt;
    private Date updatedAt;
}
