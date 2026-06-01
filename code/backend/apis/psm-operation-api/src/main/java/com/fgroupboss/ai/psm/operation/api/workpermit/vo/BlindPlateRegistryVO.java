package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class BlindPlateRegistryVO {

    private Long id;
    private String blindPlateNo;
    private String pipelineId;
    private String position;
    private String diagramRef;
    private String spec;
    private String material;
    private String tagNo;
    private String status;
    private Integer versionNo;
    private Date createdAt;
    private Date updatedAt;
}
