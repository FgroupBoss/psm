package com.fgroupboss.ai.psm.location.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LocTagBindingVO {

    private Long id;
    private Long tenantId;
    private String tagNo;
    private String personType;
    private Long personId;
    private Long contractorId;
    private LocalDateTime bindAt;
    private LocalDateTime unbindAt;
    private String status;
}
