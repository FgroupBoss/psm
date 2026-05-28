package com.fgroupboss.ai.psm.location.model.vo;

import lombok.Data;

@Data
public class LocTagVO {

    private Long id;
    private Long tenantId;
    private String tagNo;
    private String tagType;
    private String vendorCode;
    private String status;
    private String remark;
    private LocTagBindingVO activeBinding;
}
