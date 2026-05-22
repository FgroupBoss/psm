package com.fgroupboss.ai.psm.masterdata.config;

import com.fgroupboss.ai.psm.common.BusinessException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum MasterDataType {
    TENANT("tenants", "tenant"),
    ORG("orgs", "org"),
    POST("posts", "post"),
    USER("users", "user"),
    AREA("areas", "area"),
    UNIT("units", "unit"),
    EQUIPMENT("equipments", "equipment"),
    MONITOR_POINT("monitor-points", "monitor_point"),
    ROLE("roles", "role"),
    MENU("menus", "menu"),
    PERMISSION("permissions", "permission"),
    DATA_SCOPE("data-scopes", "data_scope"),
    IDENTITY_PROVIDER("identity-providers", "identity_provider");

    private static final Map<String, MasterDataType> PATH_INDEX;

    static {
        Map<String, MasterDataType> index = new HashMap<String, MasterDataType>();
        for (MasterDataType type : values()) {
            index.put(type.path, type);
        }
        PATH_INDEX = Collections.unmodifiableMap(index);
    }

    private final String path;
    private final String category;

    MasterDataType(String path, String category) {
        this.path = path;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public static MasterDataType fromPath(String path) {
        MasterDataType type = PATH_INDEX.get(path);
        if (type == null) {
            throw new BusinessException(404, "unsupported master data type: " + path);
        }
        return type;
    }
}
