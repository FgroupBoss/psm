package com.fgroupboss.ai.psm.configrule.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 配置项实体，统一承载字典、表单、流程、规则、通知和附件策略。
 */
@Data
@TableName("config_item")
public class ConfigItemEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String configType;
    private String configCode;
    private String configName;
    private Integer versionNo;
    private String status;
    private String bizScene;
    private String contentJson;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
