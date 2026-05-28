package com.fgroupboss.ai.psm.workpermit.client.dto;

import lombok.Data;

@Data
public class DualPreventionAreaOpenCheckResult {

    private boolean hasBlocking;
    private int count;
}
