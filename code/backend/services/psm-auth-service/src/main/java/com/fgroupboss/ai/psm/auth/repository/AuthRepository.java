package com.fgroupboss.ai.psm.auth.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.auth.model.AuthSession;
import com.fgroupboss.ai.psm.auth.model.AuthUser;
import com.fgroupboss.ai.psm.auth.model.IdentityProvider;
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
public class AuthRepository {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>() {
    };

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public AuthRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public Long insertUser(AuthUser user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into auth_user(tenant_id, username, display_name, mobile, email, password_hash, account_type, status, created_at, updated_at) values(?,?,?,?,?,?,?,?,now(),now())",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, user.getTenantId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getDisplayName());
            ps.setString(4, user.getMobile());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPasswordHash());
            ps.setString(7, user.getAccountType());
            ps.setString(8, user.getStatus());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public AuthUser findUserByUsername(Long tenantId, String username) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from auth_user where tenant_id=? and username=? and deleted=0",
                    userMapper(), tenantId, username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public AuthUser findUserById(Long tenantId, Long userId) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from auth_user where tenant_id=? and id=? and deleted=0",
                    userMapper(), tenantId, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void updateLastLogin(Long tenantId, Long userId) {
        jdbcTemplate.update("update auth_user set last_login_at=now(), updated_at=now() where tenant_id=? and id=?",
                tenantId, userId);
    }

    public Long insertSession(AuthSession session) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into auth_session(tenant_id, user_id, access_token, refresh_token, access_expires_at, refresh_expires_at, revoked, created_at, updated_at) values(?,?,?,?,?,?,0,now(),now())",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, session.getTenantId());
            ps.setLong(2, session.getUserId());
            ps.setString(3, session.getAccessToken());
            ps.setString(4, session.getRefreshToken());
            ps.setTimestamp(5, Timestamp.valueOf(session.getAccessExpiresAt()));
            ps.setTimestamp(6, Timestamp.valueOf(session.getRefreshExpiresAt()));
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public AuthSession findSessionByAccessToken(String accessToken) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from auth_session where access_token=? and revoked=0",
                    sessionMapper(), accessToken);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public AuthSession findSessionByRefreshToken(String refreshToken) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from auth_session where refresh_token=? and revoked=0",
                    sessionMapper(), refreshToken);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void revokeSession(Long id) {
        jdbcTemplate.update("update auth_session set revoked=1, updated_at=now() where id=?", id);
    }

    public List<IdentityProvider> listProviders(Long tenantId) {
        return jdbcTemplate.query(
                "select * from auth_identity_provider where tenant_id=? and enabled=1 order by id asc",
                providerMapper(), tenantId);
    }

    public IdentityProvider findProvider(Long tenantId, String providerCode) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from auth_identity_provider where tenant_id=? and provider_code=? and enabled=1",
                    providerMapper(), tenantId, providerCode);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void insertSsoState(Long tenantId, String providerCode, String state, LocalDateTime expiresAt, String redirectAfterLogin) {
        jdbcTemplate.update(
                "insert into auth_sso_state(tenant_id, provider_code, state, expires_at, redirect_after_login, consumed, created_at) values(?,?,?,?,?,0,now())",
                tenantId, providerCode, state, Timestamp.valueOf(expiresAt), redirectAfterLogin);
    }

    public boolean consumeSsoState(Long tenantId, String providerCode, String state) {
        int updated = jdbcTemplate.update(
                "update auth_sso_state set consumed=1 where tenant_id=? and provider_code=? and state=? and consumed=0 and expires_at>now()",
                tenantId, providerCode, state);
        return updated > 0;
    }

    public AuthUser findUserByIdentity(Long tenantId, String providerCode, String externalUserId) {
        try {
            return jdbcTemplate.queryForObject(
                    "select u.* from auth_user u inner join auth_user_identity i on u.id=i.user_id and u.tenant_id=i.tenant_id where i.tenant_id=? and i.provider_code=? and i.external_user_id=? and i.enabled=1 and u.deleted=0",
                    userMapper(), tenantId, providerCode, externalUserId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void insertUserIdentity(Long tenantId, Long userId, String providerCode, String externalUserId, String externalUsername) {
        jdbcTemplate.update(
                "insert into auth_user_identity(tenant_id, user_id, provider_code, external_user_id, external_username, enabled, created_at, updated_at) values(?,?,?,?,?,1,now(),now())",
                tenantId, userId, providerCode, externalUserId, externalUsername);
    }

    public void insertLoginLog(Long tenantId, Long userId, String username, String loginType, String result, String reason, String clientIp, String userAgent) {
        jdbcTemplate.update(
                "insert into login_log(tenant_id, user_id, username, login_type, result, failure_reason, client_ip, user_agent, logged_at) values(?,?,?,?,?,?,?,?,now())",
                tenantId, userId, username, loginType, result, reason, clientIp, userAgent);
    }

    private RowMapper<AuthUser> userMapper() {
        return (rs, rowNum) -> {
            AuthUser user = new AuthUser();
            user.setId(rs.getLong("id"));
            user.setTenantId(rs.getLong("tenant_id"));
            user.setUsername(rs.getString("username"));
            user.setDisplayName(rs.getString("display_name"));
            user.setMobile(rs.getString("mobile"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setAccountType(rs.getString("account_type"));
            user.setStatus(rs.getString("status"));
            Timestamp lastLoginAt = rs.getTimestamp("last_login_at");
            user.setLastLoginAt(lastLoginAt == null ? null : lastLoginAt.toLocalDateTime());
            return user;
        };
    }

    private RowMapper<AuthSession> sessionMapper() {
        return (rs, rowNum) -> {
            AuthSession session = new AuthSession();
            session.setId(rs.getLong("id"));
            session.setTenantId(rs.getLong("tenant_id"));
            session.setUserId(rs.getLong("user_id"));
            session.setAccessToken(rs.getString("access_token"));
            session.setRefreshToken(rs.getString("refresh_token"));
            session.setAccessExpiresAt(rs.getTimestamp("access_expires_at").toLocalDateTime());
            session.setRefreshExpiresAt(rs.getTimestamp("refresh_expires_at").toLocalDateTime());
            session.setRevoked(rs.getBoolean("revoked"));
            return session;
        };
    }

    private RowMapper<IdentityProvider> providerMapper() {
        return (rs, rowNum) -> {
            IdentityProvider provider = new IdentityProvider();
            provider.setId(rs.getLong("id"));
            provider.setTenantId(rs.getLong("tenant_id"));
            provider.setProviderCode(rs.getString("provider_code"));
            provider.setProviderType(rs.getString("provider_type"));
            provider.setProviderName(rs.getString("provider_name"));
            provider.setClientId(rs.getString("client_id"));
            provider.setClientSecret(rs.getString("client_secret"));
            provider.setAuthorizeUrl(rs.getString("authorize_url"));
            provider.setTokenUrl(rs.getString("token_url"));
            provider.setUserInfoUrl(rs.getString("user_info_url"));
            provider.setCallbackUrl(rs.getString("callback_url"));
            provider.setUserMappingField(rs.getString("user_mapping_field"));
            provider.setConfig(fromJson(rs.getString("config")));
            provider.setEnabled(rs.getBoolean("enabled"));
            return provider;
        };
    }

    private Map<String, Object> fromJson(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return Collections.emptyMap();
            }
            return objectMapper.readValue(value, MAP_TYPE);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
