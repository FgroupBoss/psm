package com.fgroupboss.ai.psm.operation.workpermit.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkPermitHealthVO {

    private String service;
    private String module;
    private String version;
    private long seedPermitCount;
}
