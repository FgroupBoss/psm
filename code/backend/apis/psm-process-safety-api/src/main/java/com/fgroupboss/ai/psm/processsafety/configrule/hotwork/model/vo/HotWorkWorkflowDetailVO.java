package com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.vo;

import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkWorkflowNodeSnapshotDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HotWorkWorkflowDetailVO {

    private Long id;
    private Long tenantId;
    private String templateCode;
    private String templateName;
    private String hotWorkLevel;
    private String areaScopeType;
    private List<Long> areaIds = new ArrayList<Long>();
    private Integer versionNo;
    private String status;
    private String remark;
    private List<HotWorkWorkflowNodeSnapshotDTO> nodes = new ArrayList<HotWorkWorkflowNodeSnapshotDTO>();
}
