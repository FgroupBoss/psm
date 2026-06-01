package com.fgroupboss.ai.psm.operation.client.dto.hotwork;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HotWorkWorkflowSnapshotDTO {

    private Long templateId;
    private Integer versionNo;
    private String templateCode;
    private String templateName;
    private String hotWorkLevel;
    private String areaScopeType;
    private List<Long> areaIds = new ArrayList<Long>();
    private List<HotWorkWorkflowNodeSnapshotDTO> nodes = new ArrayList<HotWorkWorkflowNodeSnapshotDTO>();
}
