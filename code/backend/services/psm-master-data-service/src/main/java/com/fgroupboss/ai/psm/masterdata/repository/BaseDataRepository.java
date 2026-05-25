package com.fgroupboss.ai.psm.masterdata.repository;

import com.fgroupboss.ai.psm.masterdata.config.BaseDataType;
import com.fgroupboss.ai.psm.masterdata.model.BaseDataRecord;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BaseDataRepository {

    private final JdbcTemplate jdbcTemplate;

    public BaseDataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long insert(BaseDataType type, BaseDataRecord record) {
        String sql = insertSql(type);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            bindCommonValues(ps, type, record, false);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public int update(BaseDataType type, BaseDataRecord record) {
        List<Object> values = new ArrayList<Object>();
        values.add(record.getName());
        for (String column : type.getExtraColumns()) {
            values.add(valueOf(record, column));
        }
        values.add(record.getStatus());
        values.add(record.getId());
        values.add(record.getTenantId());
        String sql = updateSql(type);
        return jdbcTemplate.update(sql, values.toArray());
    }

    public int setStatus(BaseDataType type, Long tenantId, Long id, String status) {
        return jdbcTemplate.update("update " + type.getTableName()
                + " set status=?, updated_at=now() where id=? and tenant_id=? and deleted=0", status, id, tenantId);
    }

    public int delete(BaseDataType type, Long tenantId, Long id) {
        return jdbcTemplate.update("update " + type.getTableName()
                + " set deleted=1, updated_at=now() where id=? and tenant_id=? and deleted=0", id, tenantId);
    }

    public BaseDataRecord findById(BaseDataType type, Long tenantId, Long id) {
        try {
            return jdbcTemplate.queryForObject("select * from " + type.getTableName()
                    + " where id=? and tenant_id=? and deleted=0", mapper(type), id, tenantId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public BaseDataRecord findEnabledById(BaseDataType type, Long tenantId, Long id) {
        try {
            return jdbcTemplate.queryForObject("select * from " + type.getTableName()
                    + " where id=? and tenant_id=? and status='ENABLED' and deleted=0", mapper(type), id, tenantId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public BaseDataRecord findByCode(BaseDataType type, Long tenantId, String code) {
        try {
            return jdbcTemplate.queryForObject("select * from " + type.getTableName()
                    + " where tenant_id=? and " + type.getCodeColumn() + "=? and deleted=0", mapper(type), tenantId, code);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<BaseDataRecord> list(BaseDataType type, Long tenantId, String keyword, String status, int limit, int offset) {
        List<Object> values = new ArrayList<Object>();
        values.add(tenantId);
        StringBuilder sql = new StringBuilder("select * from " + type.getTableName()
                + " where tenant_id=? and deleted=0");
        appendFilters(sql, values, type, keyword, status);
        sql.append(" order by id desc limit ? offset ?");
        values.add(limit);
        values.add(offset);
        return jdbcTemplate.query(sql.toString(), mapper(type), values.toArray());
    }

    public long count(BaseDataType type, Long tenantId, String keyword, String status) {
        List<Object> values = new ArrayList<Object>();
        values.add(tenantId);
        StringBuilder sql = new StringBuilder("select count(1) from " + type.getTableName()
                + " where tenant_id=? and deleted=0");
        appendFilters(sql, values, type, keyword, status);
        Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, values.toArray());
        return count == null ? 0L : count.longValue();
    }

    public List<BaseDataRecord> tree(BaseDataType type, Long tenantId) {
        if (type != BaseDataType.AREA) {
            return list(type, tenantId, null, null, 200, 0);
        }
        return jdbcTemplate.query("select * from base_area where tenant_id=? and deleted=0 order by parent_id asc, sort_no asc, id asc",
                mapper(type), tenantId);
    }

    private void appendFilters(StringBuilder sql, List<Object> values, BaseDataType type, String keyword, String status) {
        if (keyword != null && keyword.trim().length() > 0) {
            String like = "%" + keyword.trim() + "%";
            sql.append(" and (").append(type.getCodeColumn()).append(" like ? or ")
                    .append(type.getNameColumn()).append(" like ?)");
            values.add(like);
            values.add(like);
        }
        if (status != null && status.trim().length() > 0) {
            sql.append(" and status=?");
            values.add(status.trim());
        }
    }

    private String insertSql(BaseDataType type) {
        StringBuilder columns = new StringBuilder("tenant_id, ")
                .append(type.getCodeColumn()).append(", ")
                .append(type.getNameColumn());
        StringBuilder placeholders = new StringBuilder("?,?,?");
        for (String column : type.getExtraColumns()) {
            columns.append(", ").append(column);
            placeholders.append(",?");
        }
        columns.append(", status, created_at, updated_at, deleted");
        placeholders.append(",?,now(),now(),0");
        return "insert into " + type.getTableName() + "(" + columns + ") values(" + placeholders + ")";
    }

    private String updateSql(BaseDataType type) {
        StringBuilder sql = new StringBuilder("update " + type.getTableName() + " set ")
                .append(type.getNameColumn()).append("=?");
        for (String column : type.getExtraColumns()) {
            sql.append(", ").append(column).append("=?");
        }
        sql.append(", status=?, updated_at=now() where id=? and tenant_id=? and deleted=0");
        return sql.toString();
    }

    private void bindCommonValues(PreparedStatement ps, BaseDataType type, BaseDataRecord record, boolean update) throws java.sql.SQLException {
        int index = 1;
        if (!update) {
            ps.setLong(index++, record.getTenantId());
            ps.setString(index++, record.getCode());
        }
        ps.setString(index++, record.getName());
        for (String column : type.getExtraColumns()) {
            ps.setObject(index++, valueOf(record, column));
        }
        ps.setString(index, record.getStatus());
    }

    private Object valueOf(BaseDataRecord record, String column) {
        if ("parent_id".equals(column)) {
            return record.getParentId();
        }
        if ("area_id".equals(column)) {
            return record.getAreaId();
        }
        if ("unit_id".equals(column)) {
            return record.getUnitId();
        }
        if ("equipment_id".equals(column)) {
            return record.getEquipmentId();
        }
        if (column.endsWith("_type")) {
            return record.getType();
        }
        if ("site_id".equals(column)) {
            return record.getSiteId();
        }
        if ("risk_level".equals(column)) {
            return record.getRiskLevel();
        }
        if ("major_hazard_flag".equals(column)) {
            return Boolean.TRUE.equals(record.getMajorHazardFlag()) ? 1 : 0;
        }
        if ("sort_no".equals(column)) {
            return record.getSortNo() == null ? 0 : record.getSortNo();
        }
        if ("running_status".equals(column)) {
            return record.getRunningStatus();
        }
        if ("source_system".equals(column)) {
            return record.getSourceSystem();
        }
        if ("source_tag".equals(column)) {
            return record.getSourceTag();
        }
        if ("metric_type".equals(column)) {
            return record.getMetricType();
        }
        if ("unit".equals(column)) {
            return record.getUnit();
        }
        if ("high_high".equals(column)) {
            return record.getHighHigh();
        }
        if ("high".equals(column)) {
            return record.getHigh();
        }
        if ("low".equals(column)) {
            return record.getLow();
        }
        if ("low_low".equals(column)) {
            return record.getLowLow();
        }
        return null;
    }

    private RowMapper<BaseDataRecord> mapper(BaseDataType type) {
        return (rs, rowNum) -> {
            BaseDataRecord record = new BaseDataRecord();
            record.setId(rs.getLong("id"));
            record.setTenantId(rs.getLong("tenant_id"));
            record.setCode(rs.getString(type.getCodeColumn()));
            record.setName(rs.getString(type.getNameColumn()));
            record.setStatus(rs.getString("status"));
            record.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
            record.setUpdatedAt(toLocalDateTime(rs.getTimestamp("updated_at")));
            mapExtraValues(record, type, rs);
            return record;
        };
    }

    private void mapExtraValues(BaseDataRecord record, BaseDataType type, java.sql.ResultSet rs) throws java.sql.SQLException {
        for (String column : type.getExtraColumns()) {
            if ("parent_id".equals(column)) {
                record.setParentId(nullableLong(rs, column));
            } else if ("area_id".equals(column)) {
                record.setAreaId(nullableLong(rs, column));
            } else if ("unit_id".equals(column)) {
                record.setUnitId(nullableLong(rs, column));
            } else if ("equipment_id".equals(column)) {
                record.setEquipmentId(nullableLong(rs, column));
            } else if (column.endsWith("_type")) {
                record.setType(rs.getString(column));
            } else if ("site_id".equals(column)) {
                record.setSiteId(nullableLong(rs, column));
            } else if ("risk_level".equals(column)) {
                record.setRiskLevel(rs.getString(column));
            } else if ("major_hazard_flag".equals(column)) {
                record.setMajorHazardFlag(rs.getBoolean(column));
            } else if ("sort_no".equals(column)) {
                record.setSortNo(rs.getInt(column));
            } else if ("running_status".equals(column)) {
                record.setRunningStatus(rs.getString(column));
            } else if ("source_system".equals(column)) {
                record.setSourceSystem(rs.getString(column));
            } else if ("source_tag".equals(column)) {
                record.setSourceTag(rs.getString(column));
            } else if ("metric_type".equals(column)) {
                record.setMetricType(rs.getString(column));
            } else if ("unit".equals(column)) {
                record.setUnit(rs.getString(column));
            } else if ("high_high".equals(column)) {
                record.setHighHigh(rs.getBigDecimal(column));
            } else if ("high".equals(column)) {
                record.setHigh(rs.getBigDecimal(column));
            } else if ("low".equals(column)) {
                record.setLow(rs.getBigDecimal(column));
            } else if ("low_low".equals(column)) {
                record.setLowLow(rs.getBigDecimal(column));
            }
        }
    }

    private Long nullableLong(java.sql.ResultSet rs, String column) throws java.sql.SQLException {
        Object value = rs.getObject(column);
        return value == null ? null : rs.getLong(column);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
