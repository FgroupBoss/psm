package com.fgroupboss.ai.psm.masterdata.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.masterdata.model.MasterDataRecord;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class MasterDataRepository {

    private static final TypeReference<Map<String, Object>> ATTRIBUTES_TYPE = new TypeReference<Map<String, Object>>() {
    };

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public MasterDataRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public Long insert(MasterDataRecord record) {
        String attributes = toJson(record.getAttributes());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into master_data_item(tenant_id, category, item_code, item_name, parent_id, item_type, status, attributes, created_at, updated_at, deleted) values(?,?,?,?,?,?,?,?,now(),now(),0)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, record.getTenantId());
            ps.setString(2, record.getCategory());
            ps.setString(3, record.getCode());
            ps.setString(4, record.getName());
            if (record.getParentId() == null) {
                ps.setObject(5, null);
            } else {
                ps.setLong(5, record.getParentId());
            }
            ps.setString(6, record.getType());
            ps.setString(7, record.getStatus());
            ps.setString(8, attributes);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public int update(MasterDataRecord record) {
        return jdbcTemplate.update(
                "update master_data_item set item_name=?, parent_id=?, item_type=?, status=?, attributes=?, updated_at=now() where id=? and tenant_id=? and category=? and deleted=0",
                record.getName(), record.getParentId(), record.getType(), record.getStatus(), toJson(record.getAttributes()),
                record.getId(), record.getTenantId(), record.getCategory());
    }

    public int disable(Long tenantId, String category, Long id) {
        return jdbcTemplate.update(
                "update master_data_item set status='DISABLED', updated_at=now() where id=? and tenant_id=? and category=? and deleted=0",
                id, tenantId, category);
    }

    public int delete(Long tenantId, String category, Long id) {
        return jdbcTemplate.update(
                "update master_data_item set deleted=1, updated_at=now() where id=? and tenant_id=? and category=? and deleted=0",
                id, tenantId, category);
    }

    public MasterDataRecord findById(Long tenantId, String category, Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from master_data_item where id=? and tenant_id=? and category=? and deleted=0",
                    mapper(), id, tenantId, category);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public MasterDataRecord findByCode(Long tenantId, String category, String code) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from master_data_item where tenant_id=? and category=? and item_code=? and deleted=0",
                    mapper(), tenantId, category, code);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<MasterDataRecord> list(Long tenantId, String category, String keyword, int limit, int offset) {
        String like = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        return jdbcTemplate.query(
                "select * from master_data_item where tenant_id=? and category=? and deleted=0 and (item_code like ? or item_name like ?) order by id desc limit ? offset ?",
                mapper(), tenantId, category, like, like, limit, offset);
    }

    public long count(Long tenantId, String category, String keyword) {
        String like = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        Long count = jdbcTemplate.queryForObject(
                "select count(1) from master_data_item where tenant_id=? and category=? and deleted=0 and (item_code like ? or item_name like ?)",
                Long.class, tenantId, category, like, like);
        return count == null ? 0L : count.longValue();
    }

    public List<MasterDataRecord> tree(Long tenantId, String category) {
        return jdbcTemplate.query(
                "select * from master_data_item where tenant_id=? and category=? and deleted=0 order by parent_id asc, id asc",
                mapper(), tenantId, category);
    }

    public void insertAudit(Long tenantId, String operator, String action, String bizType, Long bizId, String beforeValue, String afterValue) {
        jdbcTemplate.update(
                "insert into audit_change_log(tenant_id, operator_name, action, biz_type, biz_id, before_value, after_value, result, operated_at) values(?,?,?,?,?,?,?,?,now())",
                tenantId, operator, action, bizType, bizId, beforeValue, afterValue, "SUCCESS");
    }

    private RowMapper<MasterDataRecord> mapper() {
        return (rs, rowNum) -> {
            MasterDataRecord record = new MasterDataRecord();
            record.setId(rs.getLong("id"));
            record.setTenantId(rs.getLong("tenant_id"));
            record.setCategory(rs.getString("category"));
            record.setCode(rs.getString("item_code"));
            record.setName(rs.getString("item_name"));
            Object parentId = rs.getObject("parent_id");
            record.setParentId(parentId == null ? null : rs.getLong("parent_id"));
            record.setType(rs.getString("item_type"));
            record.setStatus(rs.getString("status"));
            record.setAttributes(fromJson(rs.getString("attributes")));
            record.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
            record.setUpdatedAt(toLocalDateTime(rs.getTimestamp("updated_at")));
            return record;
        };
    }

    private String toJson(Map<String, Object> attributes) {
        try {
            return objectMapper.writeValueAsString(attributes == null ? Collections.emptyMap() : attributes);
        } catch (Exception e) {
            throw new IllegalArgumentException("attributes must be JSON serializable", e);
        }
    }

    private Map<String, Object> fromJson(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return Collections.emptyMap();
            }
            return objectMapper.readValue(value, ATTRIBUTES_TYPE);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
