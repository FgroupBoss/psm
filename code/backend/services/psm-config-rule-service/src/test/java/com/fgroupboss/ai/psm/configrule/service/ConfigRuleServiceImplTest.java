package com.fgroupboss.ai.psm.configrule.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.configrule.config.ConfigType;
import com.fgroupboss.ai.psm.configrule.mapper.AuditChangeLogMapper;
import com.fgroupboss.ai.psm.configrule.mapper.ConfigItemMapper;
import com.fgroupboss.ai.psm.configrule.model.dto.ConfigItemRequest;
import com.fgroupboss.ai.psm.configrule.model.dto.RuleEvaluationRequest;
import com.fgroupboss.ai.psm.configrule.model.entity.AuditChangeLogEntity;
import com.fgroupboss.ai.psm.configrule.model.entity.ConfigItemEntity;
import com.fgroupboss.ai.psm.configrule.model.vo.ConfigItemVO;
import com.fgroupboss.ai.psm.configrule.model.vo.RuleEvaluationResultVO;
import com.fgroupboss.ai.psm.configrule.service.impl.ConfigRuleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConfigRuleServiceImplTest {

    private ConfigItemMapper configItemMapper;
    private AuditChangeLogMapper auditChangeLogMapper;
    private ConfigRuleServiceImpl service;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        configItemMapper = mock(ConfigItemMapper.class);
        auditChangeLogMapper = mock(AuditChangeLogMapper.class);
        objectMapper = new ObjectMapper();
        service = new ConfigRuleServiceImpl(configItemMapper, auditChangeLogMapper, objectMapper);
    }

    @Test
    void createUsesNextVersionAfterPublishedConfig() {
        when(configItemMapper.findLatestByCode(10L, "DICTIONARY", "RISK_LEVEL"))
                .thenReturn(entity(7L, "DICTIONARY", "RISK_LEVEL", 2, "PUBLISHED", "{}"));
        when(configItemMapper.findById(10L, null))
                .thenReturn(entity(null, "DICTIONARY", "RISK_LEVEL", 3, "DRAFT", "{}"));

        ConfigItemVO result = service.create(ConfigType.DICTIONARY, request("RISK_LEVEL"), "admin");

        assertEquals(3, result.getVersionNo());
        verify(configItemMapper).insert(any(ConfigItemEntity.class));
        verify(auditChangeLogMapper).insert(any(AuditChangeLogEntity.class));
    }

    @Test
    void createRejectsDuplicateDraft() {
        when(configItemMapper.findLatestByCode(10L, "DICTIONARY", "RISK_LEVEL"))
                .thenReturn(entity(7L, "DICTIONARY", "RISK_LEVEL", 1, "DRAFT", "{}"));

        assertThrows(BusinessException.class, () -> service.create(ConfigType.DICTIONARY, request("RISK_LEVEL"), "admin"));
    }

    @Test
    void updatePublishedConfigCreatesNextDraftVersion() {
        when(configItemMapper.findById(10L, 7L))
                .thenReturn(entity(7L, "RULE_DEFINITION", "AREA_DISABLED", 1, "PUBLISHED", "{}"));
        when(configItemMapper.findById(10L, null))
                .thenReturn(entity(null, "RULE_DEFINITION", "AREA_DISABLED", 2, "DRAFT", "{}"));

        ConfigItemVO result = service.update(ConfigType.RULE_DEFINITION, 7L, request("AREA_DISABLED"), "admin");

        assertEquals(2, result.getVersionNo());
        assertEquals("DRAFT", result.getStatus());
        verify(configItemMapper).insert(any(ConfigItemEntity.class));
    }

    @Test
    void evaluateReturnsBlockWithEvidence() throws Exception {
        HashMap<String, Object> content = new HashMap<String, Object>();
        content.put("factKey", "areaStatus");
        content.put("expectedValue", "ENABLED");
        content.put("level", "BLOCK");
        content.put("message", "作业区域已停用");
        when(configItemMapper.findPublishedRules(10L, "PERMIT_SUBMIT", null))
                .thenReturn(Collections.singletonList(entity(1L, "RULE_DEFINITION", "AREA_ENABLED", 1,
                        "PUBLISHED", objectMapper.writeValueAsString(content))));
        RuleEvaluationRequest request = new RuleEvaluationRequest();
        request.setTenantId(10L);
        request.setScene("PERMIT_SUBMIT");
        HashMap<String, Object> facts = new HashMap<String, Object>();
        facts.put("areaStatus", "DISABLED");
        request.setFacts(facts);

        List<RuleEvaluationResultVO> results = service.evaluate(request);

        assertEquals(1, results.size());
        assertFalse(results.get(0).isPassed());
        assertEquals("BLOCK", results.get(0).getLevel());
        assertEquals("AREA_ENABLED", results.get(0).getRuleCode());
        assertEquals(3, results.get(0).getEvidence().size());
    }

    private ConfigItemRequest request(String code) {
        ConfigItemRequest request = new ConfigItemRequest();
        request.setTenantId(10L);
        request.setConfigCode(code);
        request.setConfigName(code);
        request.setContent(Collections.<String, Object>emptyMap());
        return request;
    }

    private ConfigItemEntity entity(Long id, String type, String code, int version, String status, String contentJson) {
        ConfigItemEntity entity = new ConfigItemEntity();
        entity.setId(id);
        entity.setTenantId(10L);
        entity.setConfigType(type);
        entity.setConfigCode(code);
        entity.setConfigName(code);
        entity.setVersionNo(version);
        entity.setStatus(status);
        entity.setBizScene("PERMIT_SUBMIT");
        entity.setContentJson(contentJson);
        return entity;
    }
}
