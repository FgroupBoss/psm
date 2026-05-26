package com.fgroupboss.ai.psm.configrule.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.configrule.config.ConfigType;
import com.fgroupboss.ai.psm.configrule.model.dto.ConfigItemRequest;
import com.fgroupboss.ai.psm.configrule.model.dto.RuleEvaluationRequest;
import com.fgroupboss.ai.psm.configrule.model.vo.ConfigItemVO;
import com.fgroupboss.ai.psm.configrule.model.vo.RuleEvaluationResultVO;

import java.util.List;

/**
 * 系统配置与规则服务。
 */
public interface ConfigRuleService {

    ConfigItemVO create(ConfigType type, ConfigItemRequest request, String operator);

    ConfigItemVO update(ConfigType type, Long id, ConfigItemRequest request, String operator);

    ConfigItemVO get(Long tenantId, Long id);

    PageResult<ConfigItemVO> page(ConfigType type, Long tenantId, String keyword, String status,
                                  String bizScene, int pageNo, int pageSize);

    void publish(Long tenantId, Long id, String operator);

    void disable(Long tenantId, Long id, String operator);

    void delete(Long tenantId, Long id, String operator);

    List<RuleEvaluationResultVO> evaluate(RuleEvaluationRequest request);
}
