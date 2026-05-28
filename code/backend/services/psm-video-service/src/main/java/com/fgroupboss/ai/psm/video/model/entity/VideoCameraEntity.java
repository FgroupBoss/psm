package com.fgroupboss.ai.psm.video.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("video_camera")
public class VideoCameraEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String cameraCode;
    private String cameraName;
    private String platformCode;
    private String platformCameraId;
    private Long areaId;
    private Long majorHazardId;
    private String locationDesc;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
