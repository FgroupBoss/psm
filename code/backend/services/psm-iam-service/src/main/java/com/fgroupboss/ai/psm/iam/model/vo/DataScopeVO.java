package com.fgroupboss.ai.psm.iam.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据权限视图。
 */
@Data
public class DataScopeVO {

    private Long roleId;
    private String scopeType;
    private List<Long> orgIds = new ArrayList<Long>();
    private List<Long> areaIds = new ArrayList<Long>();
    private List<Long> unitIds = new ArrayList<Long>();
    private Boolean includeChildren;
}
