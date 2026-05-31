package com.fgroupboss.ai.psm.identity.iam.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织树视图。
 */
@Data
public class OrgTreeVO {

    private Long id;
    private Long tenantId;
    private Long parentId;
    private String orgCode;
    private String orgName;
    private String orgType;
    private Integer sortOrder;
    private String status;
    private List<OrgTreeVO> children = new ArrayList<OrgTreeVO>();
}
