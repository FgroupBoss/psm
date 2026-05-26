package com.fgroupboss.ai.psm.iam.model.vo;

import lombok.Data;

/**
 * 岗位视图。
 */
@Data
public class PostVO {

    private Long id;
    private Long tenantId;
    private String postCode;
    private String postName;
    private Long orgId;
    private String status;
}
