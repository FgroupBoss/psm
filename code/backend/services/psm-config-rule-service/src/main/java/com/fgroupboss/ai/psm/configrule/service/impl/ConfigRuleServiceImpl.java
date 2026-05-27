package com.fgroupboss.ai.psm.configrule.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.configrule.config.ConfigType;
import com.fgroupboss.ai.psm.configrule.mapper.AuditChangeLogMapper;
import com.fgroupboss.ai.psm.configrule.mapper.ConfigItemMapper;
import com.fgroupboss.ai.psm.configrule.model.dto.ConfigItemRequest;
import com.fgroupboss.ai.psm.configrule.model.dto.RuleEvaluationRequest;
import com.fgroupboss.ai.psm.configrule.model.entity.AuditChangeLogEntity;
import com.fgroupboss.ai.psm.configrule.model.entity.ConfigItemEntity;
import com.fgroupboss.ai.psm.configrule.model.vo.ConfigItemVO;
import com.fgroupboss.ai.psm.configrule.model.vo.RuleEvaluationResultVO;
import com.fgroupboss.ai.psm.configrule.service.ConfigRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 系统配置与规则服务实现。
 *
 * <p>一期先以统一配置项承载多类配置，保证发布版本可追溯；复杂规则 DSL 在作业票和报警接入时再扩展。</p>
 */
@Service
@RequiredArgsConstructor
public class ConfigRuleServiceImpl implements ConfigRuleService {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>() {
    };

    private final ConfigItemMapper configItemMapper;
    private final AuditChangeLogMapper auditChangeLogMapper;
    private final ObjectMapper objectMapper;

    /**
     * 实现方式：创建业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public ConfigItemVO create(ConfigType type, ConfigItemRequest request, String operator) {
        validateTenant(request.getTenantId());
        ConfigItemEntity latest = configItemMapper.findLatestByCode(request.getTenantId(), type.getCode(), request.getConfigCode());
        if (latest != null && "DRAFT".equals(latest.getStatus())) {
            throw new BusinessException(409, "draft config already exists: " + request.getConfigCode());
        }
        ConfigItemEntity entity = toEntity(type, request);
        entity.setVersionNo(latest == null ? 1 : latest.getVersionNo() + 1);
        configItemMapper.insert(entity);
        ConfigItemVO saved = get(request.getTenantId(), entity.getId());
        writeAudit(request.getTenantId(), operator, "CREATE", type.getCode(), entity.getId(), null, saved);
        return saved;
    }

    /**
     * 实现方式：更新业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public ConfigItemVO update(ConfigType type, Long id, ConfigItemRequest request, String operator) {
        ConfigItemVO before = get(request.getTenantId(), id);
        if (!type.getCode().equals(before.getConfigType())) {
            throw new BusinessException(400, "config type mismatch");
        }
        ConfigItemVO after;
        if ("DRAFT".equals(before.getStatus())) {
            ConfigItemEntity entity = toEntity(type, request);
            entity.setId(id);
            entity.setVersionNo(before.getVersionNo());
            if (configItemMapper.updateDraft(entity) == 0) {
                throw new BusinessException(409, "only draft config can be updated");
            }
            after = get(request.getTenantId(), id);
        } else {
            after = createNextDraft(type, before, request);
        }
        writeAudit(request.getTenantId(), operator, "UPDATE", type.getCode(), after.getId(), before, after);
        return after;
    }

    /**
     * 实现方式：查询详情，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public ConfigItemVO get(Long tenantId, Long id) {
        validateTenant(tenantId);
        ConfigItemEntity entity = configItemMapper.findById(tenantId, id);
        if (entity == null) {
            throw new BusinessException(404, "config not found");
        }
        return toVO(entity);
    }

    /**
     * 实现方式：分页查询业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public PageResult<ConfigItemVO> page(ConfigType type, Long tenantId, String keyword, String status,
                                         String bizScene, int pageNo, int pageSize) {
        validateTenant(tenantId);
        Page page = page(pageNo, pageSize);
        String normalizedKeyword = normalizeText(keyword);
        String normalizedStatus = normalizeText(status);
        String normalizedScene = normalizeText(bizScene);
        long total = configItemMapper.count(tenantId, type.getCode(), normalizedKeyword, normalizedStatus, normalizedScene);
        List<ConfigItemVO> records = new ArrayList<ConfigItemVO>();
        for (ConfigItemEntity entity : configItemMapper.list(tenantId, type.getCode(), normalizedKeyword,
                normalizedStatus, normalizedScene, page.limit, page.offset)) {
            records.add(toVO(entity));
        }
        return new PageResult<ConfigItemVO>(total, page.pageNo, page.pageSize, records);
    }

    /**
     * 实现方式：发布业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void publish(Long tenantId, Long id, String operator) {
        changeStatus(tenantId, id, "PUBLISHED", "PUBLISH", operator);
    }

    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void disable(Long tenantId, Long id, String operator) {
        changeStatus(tenantId, id, "DISABLED", "DISABLE", operator);
    }

    /**
     * 实现方式：删除业务数据，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    @Transactional
    public void delete(Long tenantId, Long id, String operator) {
        ConfigItemVO before = get(tenantId, id);
        if ("PUBLISHED".equals(before.getStatus())) {
            throw new BusinessException(409, "published config must be disabled before delete");
        }
        if (configItemMapper.softDelete(tenantId, id) == 0) {
            throw new BusinessException(404, "config not found");
        }
        writeAudit(tenantId, operator, "DELETE", before.getConfigType(), id, before, null);
    }

    /**
     * 实现方式：执行业务实现，先完成必要的参数、租户或状态校验，再委托持久化组件或远程客户端处理并组装返回结果。
     */
    @Override
    public List<RuleEvaluationResultVO> evaluate(RuleEvaluationRequest request) {
        validateTenant(request.getTenantId());
        List<ConfigItemEntity> rules = configItemMapper.findPublishedRules(request.getTenantId(),
                normalizeText(request.getScene()), normalizeText(request.getRuleCode()));
        if (CollectionUtils.isEmpty(rules)) {
            return Collections.singletonList(pass("NO_RULE", "未命中阻断规则", "无需处理"));
        }
        List<RuleEvaluationResultVO> results = new ArrayList<RuleEvaluationResultVO>();
        for (ConfigItemEntity rule : rules) {
            results.add(evaluateRule(rule, request.getFacts()));
        }
        return results;
    }

    private ConfigItemVO createNextDraft(ConfigType type, ConfigItemVO before, ConfigItemRequest request) {
        ConfigItemEntity entity = toEntity(type, request);
        entity.setConfigCode(before.getConfigCode());
        entity.setVersionNo(before.getVersionNo() + 1);
        entity.setStatus("DRAFT");
        configItemMapper.insert(entity);
        return get(request.getTenantId(), entity.getId());
    }

    private RuleEvaluationResultVO evaluateRule(ConfigItemEntity rule, Map<String, Object> facts) {
        Map<String, Object> content = readMap(rule.getContentJson());
        String factKey = text(content.get("factKey"));
        Object expected = content.get("expectedValue");
        String message = defaultText(text(content.get("message")), "规则校验未通过");
        String suggestion = defaultText(text(content.get("suggestion")), "请按现场制度处理后重试");
        String level = defaultText(text(content.get("level")), "BLOCK");
        if (!StringUtils.hasText(factKey) || matches(facts, factKey, expected)) {
            return pass(rule.getConfigCode(), "规则校验通过", "无需处理");
        }
        RuleEvaluationResultVO result = new RuleEvaluationResultVO();
        result.setRuleCode(rule.getConfigCode());
        result.setLevel(level);
        result.setPassed(!"BLOCK".equals(level));
        result.setMessage(message);
        result.setSuggestion(suggestion);
        result.getEvidence().add("factKey=" + factKey);
        result.getEvidence().add("expectedValue=" + String.valueOf(expected));
        result.getEvidence().add("actualValue=" + String.valueOf(facts == null ? null : facts.get(factKey)));
        return result;
    }

    private boolean matches(Map<String, Object> facts, String factKey, Object expected) {
        if (facts == null || !facts.containsKey(factKey)) {
            return false;
        }
        Object actual = facts.get(factKey);
        if (expected == null) {
            return actual == null;
        }
        return String.valueOf(expected).equals(String.valueOf(actual));
    }

    private RuleEvaluationResultVO pass(String ruleCode, String message, String suggestion) {
        RuleEvaluationResultVO result = new RuleEvaluationResultVO();
        result.setPassed(true);
        result.setLevel("PASS");
        result.setRuleCode(ruleCode);
        result.setMessage(message);
        result.setSuggestion(suggestion);
        return result;
    }

    private void changeStatus(Long tenantId, Long id, String status, String action, String operator) {
        ConfigItemVO before = get(tenantId, id);
        if (configItemMapper.setStatus(tenantId, id, status) == 0) {
            throw new BusinessException(404, "config not found");
        }
        ConfigItemVO after = get(tenantId, id);
        writeAudit(tenantId, operator, action, before.getConfigType(), id, before, after);
    }

    private ConfigItemEntity toEntity(ConfigType type, ConfigItemRequest request) {
        ConfigItemEntity entity = new ConfigItemEntity();
        entity.setTenantId(request.getTenantId());
        entity.setConfigType(type.getCode());
        entity.setConfigCode(request.getConfigCode());
        entity.setConfigName(request.getConfigName());
        entity.setBizScene(request.getBizScene());
        entity.setContentJson(toJson(request.getContent()));
        entity.setRemark(request.getRemark());
        entity.setStatus(defaultText(request.getStatus(), "DRAFT"));
        return entity;
    }

    private ConfigItemVO toVO(ConfigItemEntity entity) {
        ConfigItemVO vo = new ConfigItemVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setConfigType(entity.getConfigType());
        vo.setConfigCode(entity.getConfigCode());
        vo.setConfigName(entity.getConfigName());
        vo.setVersionNo(entity.getVersionNo());
        vo.setStatus(entity.getStatus());
        vo.setBizScene(entity.getBizScene());
        vo.setContent(readMap(entity.getContentJson()));
        vo.setRemark(entity.getRemark());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private void writeAudit(Long tenantId, String operator, String action, String bizType, Long bizId,
                            Object before, Object after) {
        AuditChangeLogEntity entity = new AuditChangeLogEntity();
        entity.setTenantId(tenantId);
        entity.setOperatorName(defaultText(operator, "system"));
        entity.setAction(action);
        entity.setBizType(bizType);
        entity.setBizId(bizId);
        entity.setBeforeValue(toJson(before));
        entity.setAfterValue(toJson(after));
        entity.setResult("SUCCESS");
        entity.setOperatedAt(LocalDateTime.now());
        auditChangeLogMapper.insert(entity);
    }

    private void validateTenant(Long tenantId) {
        if (tenantId == null || tenantId.longValue() <= 0L) {
            throw new BusinessException(400, "tenantId is required");
        }
    }

    private Page page(int pageNo, int pageSize) {
        int normalizedPageNo = Math.max(pageNo, 1);
        int normalizedPageSize = Math.min(Math.max(pageSize, 1), 200);
        return new Page(normalizedPageNo, normalizedPageSize);
    }

    private Map<String, Object> readMap(String value) {
        if (!StringUtils.hasText(value)) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(value, MAP_TYPE);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new BusinessException(400, "content must be JSON serializable");
        }
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String defaultText(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    private String text(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static class Page {
        private final int pageNo;
        private final int pageSize;
        private final int limit;
        private final int offset;

        private Page(int pageNo, int pageSize) {
            this.pageNo = pageNo;
            this.pageSize = pageSize;
            this.limit = pageSize;
            this.offset = (pageNo - 1) * pageSize;
        }
    }
}
