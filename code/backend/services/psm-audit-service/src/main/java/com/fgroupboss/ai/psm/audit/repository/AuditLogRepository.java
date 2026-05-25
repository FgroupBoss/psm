package com.fgroupboss.ai.psm.audit.repository;

import com.fgroupboss.ai.psm.audit.model.AuditLogRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AuditLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public AuditLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AuditLogRecord> list(Long tenantId, String bizType, Long bizId, String action,
                                     String operatorName, LocalDateTime startTime, LocalDateTime endTime,
                                     int limit, int offset) {
        List<Object> values = new ArrayList<Object>();
        StringBuilder sql = baseQuery("select *", values, tenantId, bizType, bizId, action, operatorName, startTime, endTime);
        sql.append(" order by operated_at desc, id desc limit ? offset ?");
        values.add(limit);
        values.add(offset);
        return jdbcTemplate.query(sql.toString(), mapper(), values.toArray());
    }

    public long count(Long tenantId, String bizType, Long bizId, String action,
                      String operatorName, LocalDateTime startTime, LocalDateTime endTime) {
        List<Object> values = new ArrayList<Object>();
        StringBuilder sql = baseQuery("select count(1)", values, tenantId, bizType, bizId, action, operatorName, startTime, endTime);
        Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, values.toArray());
        return count == null ? 0L : count.longValue();
    }

    private StringBuilder baseQuery(String select, List<Object> values, Long tenantId, String bizType,
                                    Long bizId, String action, String operatorName,
                                    LocalDateTime startTime, LocalDateTime endTime) {
        StringBuilder sql = new StringBuilder(select).append(" from audit_change_log where tenant_id=?");
        values.add(tenantId);
        if (StringUtils.hasText(bizType)) {
            sql.append(" and biz_type=?");
            values.add(bizType);
        }
        if (bizId != null) {
            sql.append(" and biz_id=?");
            values.add(bizId);
        }
        if (StringUtils.hasText(action)) {
            sql.append(" and action=?");
            values.add(action);
        }
        if (StringUtils.hasText(operatorName)) {
            sql.append(" and operator_name like ?");
            values.add("%" + operatorName.trim() + "%");
        }
        if (startTime != null) {
            sql.append(" and operated_at>=?");
            values.add(Timestamp.valueOf(startTime));
        }
        if (endTime != null) {
            sql.append(" and operated_at<=?");
            values.add(Timestamp.valueOf(endTime));
        }
        return sql;
    }

    private RowMapper<AuditLogRecord> mapper() {
        return (rs, rowNum) -> {
            AuditLogRecord record = new AuditLogRecord();
            record.setId(rs.getLong("id"));
            record.setTenantId(rs.getLong("tenant_id"));
            Object operatorId = rs.getObject("operator_id");
            record.setOperatorId(operatorId == null ? null : rs.getLong("operator_id"));
            record.setOperatorName(rs.getString("operator_name"));
            record.setAction(rs.getString("action"));
            record.setBizType(rs.getString("biz_type"));
            Object bizId = rs.getObject("biz_id");
            record.setBizId(bizId == null ? null : rs.getLong("biz_id"));
            record.setBeforeValue(rs.getString("before_value"));
            record.setAfterValue(rs.getString("after_value"));
            record.setResult(rs.getString("result"));
            record.setClientIp(rs.getString("client_ip"));
            record.setUserAgent(rs.getString("user_agent"));
            Timestamp operatedAt = rs.getTimestamp("operated_at");
            record.setOperatedAt(operatedAt == null ? null : operatedAt.toLocalDateTime());
            return record;
        };
    }
}
