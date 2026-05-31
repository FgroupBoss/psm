package com.fgroupboss.ai.psm.realtime.video.model.vo;

import lombok.Data;

@Data
public class VideoAiEventMediaVO {

    private Long id;
    private String mediaType;
    private Long fileId;
    private String mediaUrl;
}
