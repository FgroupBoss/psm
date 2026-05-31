package com.fgroupboss.ai.psm.realtime.video.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VideoWatchSessionVO {

    private Long id;
    private Long tenantId;
    private Long workPermitId;
    private List<Long> cameraIds;
    private Long operatorId;
    private String operatorName;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String remark;
}
