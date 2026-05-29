package com.fgroupboss.ai.psm.barrier.support;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 屏障与机械完整性服务通用辅助方法。
 */
public final class EntitySupport {

    private EntitySupport() {
    }

    public static void requireTenantId(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    public static void requireId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(400, "id is required");
        }
    }

    public static <T> T requireFound(T entity, String message) {
        if (entity == null) {
            throw new BusinessException(404, message);
        }
        return entity;
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static void initAuditFields(Object entity) {
        LocalDateTime now = now();
        invokeSetter(entity, "setCreatedAt", LocalDateTime.class, now);
        invokeSetter(entity, "setUpdatedAt", LocalDateTime.class, now);
        invokeSetter(entity, "setDeleted", Integer.class, 0);
    }

    public static void touchUpdated(Object entity) {
        invokeSetter(entity, "setUpdatedAt", LocalDateTime.class, now());
    }

    private static void invokeSetter(Object entity, String method, Class<?> type, Object value) {
        try {
            entity.getClass().getMethod(method, type).invoke(entity, value);
        } catch (Exception ignored) {
        }
    }

    public static <E, V> PageResult<V> toPageResult(Page<E> page, Function<E, V> mapper) {
        List<V> records = new ArrayList<V>();
        for (E item : page.getRecords()) {
            records.add(mapper.apply(item));
        }
        return new PageResult<V>(page.getTotal(), (int) page.getCurrent(), (int) page.getSize(), records);
    }

    public static int normalizePageNo(int pageNo) {
        return Math.max(pageNo, 1);
    }

    public static int normalizePageSize(int pageSize) {
        return Math.min(Math.max(pageSize, 1), 200);
    }
}
