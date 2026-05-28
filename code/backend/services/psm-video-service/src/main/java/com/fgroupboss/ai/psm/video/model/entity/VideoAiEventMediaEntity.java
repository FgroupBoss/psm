package com.fgroupboss.ai.psm.video.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("video_ai_event_media")
public class VideoAiEventMediaEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long eventId;
    private String mediaType;
    private Long fileId;
    private String mediaUrl;
    private LocalDateTime createdAt;
    private Integer deleted;
}
