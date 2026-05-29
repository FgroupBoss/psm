package com.fgroupboss.ai.psm.incident.support;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * 事故调查服务通用分页、租户校验与编号生成。
 */
public final class ServiceSupport {

    private static final AtomicLong SEQ = new AtomicLong(System.currentTimeMillis() % 100000);

    private ServiceSupport() {
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

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static String nextNo(String prefix) {
        return prefix + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", SEQ.incrementAndGet() % 10000);
    }

    public static <E, V> PageResult<V> toPage(Page<E> page, Function<E, V> mapper) {
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
