package com.fgroupboss.ai.psm.incidentgovernance.governance.support;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 集团治理服务通用校验与分页辅助。
 */
public final class ServiceSupport {

    private ServiceSupport() {
    }

    public static void requireTenantId(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    public static <T> T requireFound(T entity, String message) {
        if (entity == null) {
            throw new BusinessException(404, message);
        }
        return entity;
    }

    public static String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    public static Page<?> normalizePage(int pageNo, int pageSize) {
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);
        return new Page<Object>(normalizedPageNo, normalizedPageSize);
    }

    public static void touchCreate(Object entity) {
        LocalDateTime now = LocalDateTime.now();
        invokeSetter(entity, "setCreatedAt", now);
        invokeSetter(entity, "setUpdatedAt", now);
        invokeSetter(entity, "setDeleted", 0);
    }

    public static void touchUpdate(Object entity) {
        invokeSetter(entity, "setUpdatedAt", LocalDateTime.now());
    }

    public static <E, V> PageResult<V> toPageResult(Page<E> page, Function<E, V> mapper) {
        List<V> records = new ArrayList<V>();
        for (E item : page.getRecords()) {
            records.add(mapper.apply(item));
        }
        return new PageResult<V>(page.getTotal(), (int) page.getCurrent(), (int) page.getSize(), records);
    }

    private static void invokeSetter(Object entity, String method, Object value) {
        try {
            entity.getClass().getMethod(method, value.getClass()).invoke(entity, value);
        } catch (Exception ignored) {
            // entity may not expose timestamp fields
        }
    }
}
