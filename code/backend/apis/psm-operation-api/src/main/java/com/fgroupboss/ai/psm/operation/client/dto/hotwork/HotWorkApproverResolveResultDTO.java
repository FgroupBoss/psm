package com.fgroupboss.ai.psm.operation.client.dto.hotwork;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HotWorkApproverResolveResultDTO {

    private boolean resolved;
    private String failReason;
    private List<HotWorkApproverDTO> approvers = new ArrayList<HotWorkApproverDTO>();
}
