package com.fgroupboss.ai.psm.common.data;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 通用实体辅助工具 — 替代各服务中复制的 EntitySupport 副本。
 * 提供参数校验、审计字段初始化、分页转换等通用方法。
 */
public final class EntitySupport {

    private EntitySupport() {
    }

    // ===== 参数校验 =====

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

    public static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new BusinessException(400, fieldName + " is required");
        }
    }

    // ===== 时间 =====

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    // ===== 审计字段 =====

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

    // ===== 分页 =====

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
