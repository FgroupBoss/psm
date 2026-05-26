package com.fgroupboss.ai.psm.iam.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单与权限资源树视图。
 */
@Data
public class MenuTreeVO {

    private Long id;
    private Long tenantId;
    private Long parentId;
    private String resourceType;
    private String resourceCode;
    private String resourceName;
    private String routePath;
    private String apiPath;
    private String httpMethod;
    private Integer sortOrder;
    private Boolean visible;
    private String status;
    private List<MenuTreeVO> children = new ArrayList<MenuTreeVO>();
}
