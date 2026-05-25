package com.fgroupboss.ai.psm.masterdata.config;

import com.fgroupboss.ai.psm.common.BusinessException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum BaseDataType {
    AREA("areas", "AREA", "base_area", "area_code", "area_name",
            Arrays.asList("parent_id", "area_type", "site_id", "risk_level", "major_hazard_flag", "sort_no")),
    UNIT("units", "UNIT", "base_unit", "unit_code", "unit_name",
            Arrays.asList("area_id", "unit_type", "sort_no")),
    EQUIPMENT("equipments", "EQUIPMENT", "base_equipment", "equipment_code", "equipment_name",
            Arrays.asList("area_id", "unit_id", "equipment_type", "running_status")),
    MONITOR_POINT("monitor-points", "MONITOR_POINT", "monitor_point", "point_code", "point_name",
            Arrays.asList("area_id", "equipment_id", "source_system", "source_tag", "metric_type", "unit",
                    "high_high", "high", "low", "low_low"));

    private final String path;
    private final String bizType;
    private final String tableName;
    private final String codeColumn;
    private final String nameColumn;
    private final List<String> extraColumns;

    BaseDataType(String path, String bizType, String tableName, String codeColumn, String nameColumn, List<String> extraColumns) {
        this.path = path;
        this.bizType = bizType;
        this.tableName = tableName;
        this.codeColumn = codeColumn;
        this.nameColumn = nameColumn;
        this.extraColumns = Collections.unmodifiableList(extraColumns);
    }

    public String getPath() {
        return path;
    }

    public String getBizType() {
        return bizType;
    }

    public String getTableName() {
        return tableName;
    }

    public String getCodeColumn() {
        return codeColumn;
    }

    public String getNameColumn() {
        return nameColumn;
    }

    public List<String> getExtraColumns() {
        return extraColumns;
    }

    public static BaseDataType fromPath(String path) {
        for (BaseDataType type : values()) {
            if (type.path.equals(path)) {
                return type;
            }
        }
        throw new BusinessException(404, "unsupported base data type: " + path);
    }
}
