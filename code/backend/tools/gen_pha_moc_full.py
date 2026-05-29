# -*- coding: utf-8 -*-
"""Generate complete PHA and MOC microservices."""
import os
import sys

TOOLS = os.path.dirname(__file__)
sys.path.insert(0, TOOLS)
from generate_phase3_services import (  # noqa: E402
    SERVICES,
    STATUS_SOURCES,
    entity_to_vo,
    gen_application,
    gen_entity,
    gen_handler,
    gen_mapper,
    gen_pom,
    gen_schema,
    gen_status_test,
    gen_yml,
    service_dir,
    write,
)

ROOT = os.path.join(TOOLS, "..", "services")
TARGET = {"psm-pha-service", "psm-moc-service"}


def gen_entity_support(pkg):
    return f"""package com.fgroupboss.ai.psm.{pkg}.support;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 实体查询、分页与时间戳等通用辅助。
 */
public final class EntitySupport {{

    private EntitySupport() {{
    }}

    public static void requireTenantId(Long tenantId) {{
        if (tenantId == null || tenantId <= 0) {{
            throw new BusinessException(400, "tenantId is required");
        }}
    }}

    public static void requireId(Long id) {{
        if (id == null || id <= 0) {{
            throw new BusinessException(400, "id is required");
        }}
    }}

    public static LocalDateTime now() {{
        return LocalDateTime.now();
    }}

    public static void stampCreate(Object entity) {{
        LocalDateTime now = now();
        invokeSetter(entity, "setCreatedAt", LocalDateTime.class, now);
        invokeSetter(entity, "setUpdatedAt", LocalDateTime.class, now);
        invokeSetter(entity, "setDeleted", Integer.class, 0);
    }}

    public static void stampUpdate(Object entity) {{
        invokeSetter(entity, "setUpdatedAt", LocalDateTime.class, now());
    }}

    private static void invokeSetter(Object entity, String method, Class<?> type, Object value) {{
        try {{
            entity.getClass().getMethod(method, type).invoke(entity, value);
        }} catch (Exception ignored) {{
        }}
    }}

    public static <E, V> PageResult<V> toPageResult(Page<E> page, Function<E, V> mapper) {{
        List<V> records = new ArrayList<V>();
        for (E item : page.getRecords()) {{
            records.add(mapper.apply(item));
        }}
        return new PageResult<V>(page.getTotal(), (int) page.getCurrent(), (int) page.getSize(), records);
    }}

    public static <E> LambdaQueryWrapper<E> notDeleted() {{
        return new LambdaQueryWrapper<E>();
    }}
}}
"""


def gen_pha_status_extended():
    return """package com.fgroupboss.ai.psm.pha.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * PHA 项目状态及合法迁移。
 */
public enum PhaProjectStatus {
    DRAFT, ANALYZING, PENDING_REVIEW, PUBLISHED, REVIEWING, ARCHIVED, CANCELLED;

    public static PhaProjectStatus fromCode(String code) {
        if (code == null) {
            throw new BusinessException(400, "pha project status is required");
        }
        try {
            return PhaProjectStatus.valueOf(code.trim());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "unsupported pha project status: " + code);
        }
    }

    public static void assertEditable(String current) {
        PhaProjectStatus status = fromCode(current);
        if (status != DRAFT && status != ANALYZING) {
            throw new BusinessException(400, "only DRAFT or ANALYZING project can be edited");
        }
    }

    public static void assertSubmit(String current) {
        PhaProjectStatus status = fromCode(current);
        if (status != DRAFT && status != ANALYZING) {
            throw new BusinessException(400, "only DRAFT or ANALYZING can submit for review");
        }
    }

    public static String targetAfterSubmit(String current) {
        assertSubmit(current);
        return PENDING_REVIEW.name();
    }

    public static void assertPublish(String current) {
        if (fromCode(current) != PENDING_REVIEW) {
            throw new BusinessException(400, "only PENDING_REVIEW can publish");
        }
    }

    public static String targetAfterPublish() {
        return PUBLISHED.name();
    }

    public static void assertArchive(String current) {
        PhaProjectStatus status = fromCode(current);
        if (status != PUBLISHED && status != REVIEWING) {
            throw new BusinessException(400, "only PUBLISHED or REVIEWING can archive");
        }
    }

    public static String targetAfterArchive() {
        return ARCHIVED.name();
    }

    public static void assertCancel(String current) {
        PhaProjectStatus status = fromCode(current);
        if (status != DRAFT && status != ANALYZING) {
            throw new BusinessException(400, "only DRAFT or ANALYZING can cancel");
        }
    }

    public static String targetAfterCancel() {
        return CANCELLED.name();
    }

    public static void assertDirectTransition(String from, String to) {
        if (DRAFT.name().equals(from) && PUBLISHED.name().equals(to)) {
            throw new BusinessException(400, "illegal transition DRAFT -> PUBLISHED");
        }
        if (ARCHIVED.name().equals(from) || CANCELLED.name().equals(from)) {
            throw new BusinessException(400, "terminal status cannot transition");
        }
    }
}
"""


def gen_moc_status_extended():
    return """package com.fgroupboss.ai.psm.moc.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * MOC 变更状态及合法迁移。
 */
public enum MocChangeStatus {
    DRAFT, SUBMITTED, INITIAL_REVIEW, IMPACT_ANALYSIS, APPROVING, PENDING_IMPL, IMPLEMENTING,
    PENDING_VERIFY, CLOSED, RETURNED, CANCELLED;

    public static MocChangeStatus fromCode(String code) {
        if (code == null) {
            throw new BusinessException(400, "moc change status is required");
        }
        try {
            return MocChangeStatus.valueOf(code.trim());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "unsupported moc change status: " + code);
        }
    }

    public static void assertEditable(String current) {
        if (fromCode(current) != DRAFT && fromCode(current) != RETURNED) {
            throw new BusinessException(400, "only DRAFT or RETURNED change can be edited");
        }
    }

    public static void assertSubmit(String current) {
        if (fromCode(current) != DRAFT && fromCode(current) != RETURNED) {
            throw new BusinessException(400, "only DRAFT or RETURNED can submit");
        }
    }

    public static String targetAfterSubmit() {
        return SUBMITTED.name();
    }

    public static String targetAfterImpactAnalysis() {
        return IMPACT_ANALYSIS.name();
    }

    public static String targetAfterApprove() {
        return PENDING_IMPL.name();
    }

    public static String targetAfterVerify() {
        return PENDING_VERIFY.name();
    }

    public static void assertClose(String current) {
        if (fromCode(current) != PENDING_VERIFY) {
            throw new BusinessException(400, "only PENDING_VERIFY can close");
        }
    }

    public static String targetAfterClose() {
        return CLOSED.name();
    }

    public static void assertDirectTransition(String from, String to) {
        if (DRAFT.name().equals(from) && CLOSED.name().equals(to)) {
            throw new BusinessException(400, "illegal transition DRAFT -> CLOSED");
        }
        if (CLOSED.name().equals(from) || CANCELLED.name().equals(from)) {
            throw new BusinessException(400, "terminal status cannot transition");
        }
    }
}
"""


def gen_pha_recommendation_status():
    return """package com.fgroupboss.ai.psm.pha.config;

import com.fgroupboss.ai.psm.common.BusinessException;

/**
 * PHA 建议项状态。
 */
public enum PhaRecommendationStatus {
    PENDING_ASSIGN, RECTIFYING, PENDING_VERIFY, CLOSED, RETURNED, OVERDUE;

    public static void assertAssign(String current) {
        if (!PENDING_ASSIGN.name().equals(current) && !RETURNED.name().equals(current)) {
            throw new BusinessException(400, "only PENDING_ASSIGN or RETURNED can assign");
        }
    }

    public static String targetAfterAssign() {
        return RECTIFYING.name();
    }

    public static void assertRectify(String current) {
        if (!RECTIFYING.name().equals(current)) {
            throw new BusinessException(400, "only RECTIFYING can complete rectify");
        }
    }

    public static String targetAfterRectify() {
        return PENDING_VERIFY.name();
    }

    public static void assertVerify(String current) {
        if (!PENDING_VERIFY.name().equals(current)) {
            throw new BusinessException(400, "only PENDING_VERIFY can verify");
        }
    }

    public static String targetAfterVerify(boolean passed) {
        return passed ? CLOSED.name() : RETURNED.name();
    }
}
"""


def write_pha_business(pkg_java):
    """Write PHA DTOs, services, controllers."""
    files = {}

    files["model/dto/PhaProjectRequest.java"] = """package com.fgroupboss.ai.psm.pha.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class PhaProjectRequest {
    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "projectNo is required")
    private String projectNo;
    @NotBlank(message = "projectName is required")
    private String projectName;
    private Long siteId;
    private Long unitId;
    private Long majorHazardId;
    private String method;
    private String version;
    private LocalDateTime reviewDueAt;
    private String status;
}
"""

    files["model/dto/PhaNodeRequest.java"] = """package com.fgroupboss.ai.psm.pha.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PhaNodeRequest {
    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "nodeNo is required")
    private String nodeNo;
    @NotBlank(message = "nodeName is required")
    private String nodeName;
    private String designIntent;
    private String parameters;
}
"""

    files["model/dto/HazopDeviationRequest.java"] = """package com.fgroupboss.ai.psm.pha.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class HazopDeviationRequest {
    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private String parameter;
    private String guideword;
    private String deviationDesc;
    private String riskLevel;
}
"""

    files["model/dto/PhaRecommendationRequest.java"] = """package com.fgroupboss.ai.psm.pha.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class PhaRecommendationRequest {
    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private Long projectId;
    private Long deviationId;
    @NotBlank(message = "recNo is required")
    private String recNo;
    @NotBlank(message = "description is required")
    private String description;
    private Long ownerUserId;
    private LocalDateTime dueAt;
}
"""

    files["model/dto/LopaScenarioRequest.java"] = """package com.fgroupboss.ai.psm.pha.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class LopaScenarioRequest {
    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "scenarioNo is required")
    private String scenarioNo;
    private Long projectId;
    private Long deviationId;
    private BigDecimal initiatingEventFrequency;
    private String consequenceSeverity;
    private BigDecimal targetFrequency;
    private String silRecommendation;
    private String calculationVersion;
}
"""

    files["model/dto/RecommendationActionRequest.java"] = """package com.fgroupboss.ai.psm.pha.model.dto;

import lombok.Data;

@Data
public class RecommendationActionRequest {
    private Long ownerUserId;
    private String remark;
    private Boolean passed;
}
"""

    files["model/vo/PhaProjectReportVO.java"] = """package com.fgroupboss.ai.psm.pha.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PhaProjectReportVO {
    private PhaProjectVO project;
    private List<PhaNodeVO> nodes = new ArrayList<PhaNodeVO>();
    private List<PhaRecommendationVO> recommendations = new ArrayList<PhaRecommendationVO>();
    private List<LopaScenarioVO> lopaScenarios = new ArrayList<LopaScenarioVO>();
}
"""

    files["model/vo/LopaCalculateResultVO.java"] = """package com.fgroupboss.ai.psm.pha.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LopaCalculateResultVO {
    private Long scenarioId;
    private BigDecimal mitigatedFrequency;
    private String silRecommendation;
    private String calculationVersion;
}
"""

    # Service interfaces and impls - will be in separate write due to size
    for rel, content in files.items():
        write(os.path.join(pkg_java, rel.replace("/", os.sep)), content)


def main():
    created = []
    for svc in SERVICES:
        if svc["artifact"] not in TARGET:
            continue
        base = service_dir(svc)
        pkg = svc["pkg"]
        pkg_java = os.path.join(base, "src", "main", "java", "com", "fgroupboss", "ai", "psm", pkg)

        write(os.path.join(base, "pom.xml"), gen_pom(svc["artifact"]))
        created.append(os.path.join(base, "pom.xml"))
        write(os.path.join(base, "src", "main", "resources", "application.yml"), gen_yml(svc))
        created.append(os.path.join(base, "src/main/resources/application.yml"))
        write(os.path.join(base, "src", "main", "resources", "db", "schema.sql"), gen_schema(svc["tables"]))
        created.append(os.path.join(base, "src/main/resources/db/schema.sql"))
        write(os.path.join(pkg_java, svc["app_class"] + ".java"), gen_application(svc))
        created.append(f"{svc['artifact']}/Application")
        write(os.path.join(pkg_java, "controller", "GlobalExceptionHandler.java"), gen_handler(svc))
        write(os.path.join(pkg_java, "support", "EntitySupport.java"), gen_entity_support(pkg))

        if pkg == "pha":
            write(os.path.join(pkg_java, "config", "PhaProjectStatus.java"), gen_pha_status_extended())
            write(os.path.join(pkg_java, "config", "PhaRecommendationStatus.java"), gen_pha_recommendation_status())
        else:
            write(os.path.join(pkg_java, "config", "MocChangeStatus.java"), gen_moc_status_extended())

        test_path = os.path.join(
            base, "src", "test", "java", "com", "fgroupboss", "ai", "psm", pkg, "config", svc["status_test"] + ".java"
        )
        write(test_path, gen_status_test(pkg, svc["status_class"]))

        for table, cls, fields, default in svc["tables"]:
            write(os.path.join(pkg_java, "model", "entity", cls + "Entity.java"), gen_entity(table, cls, fields).replace("{pkg}", pkg))
            write(os.path.join(pkg_java, "mapper", cls + "Mapper.java"), gen_mapper(pkg, cls))
            write(os.path.join(pkg_java, "model", "vo", cls + "VO.java"), entity_to_vo(pkg, cls, fields))
            created.append(f"{svc['artifact']}/entity/{cls}")

        write_pha_business(pkg_java) if pkg == "pha" else None

    print("Scaffold complete for", ", ".join(TARGET))


if __name__ == "__main__":
    main()
