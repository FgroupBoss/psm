package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ExcavationCountersignVO {

    private Long id;
    private String specialty;
    private Long signerId;
    private String result;
    private String opinion;
    private Date signedAt;
}
