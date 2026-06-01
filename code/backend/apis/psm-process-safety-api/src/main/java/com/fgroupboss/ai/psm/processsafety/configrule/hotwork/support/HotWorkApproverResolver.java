package com.fgroupboss.ai.psm.processsafety.configrule.hotwork.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.config.ApproverRuleType;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto.HotWorkApproverResolveDTO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.vo.HotWorkApproverResolveResultVO;
import com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.vo.HotWorkApproverVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 动火审批人规则解析（一期：FIXED_USERS / FORM_FIELD）。
 */
@Component
@RequiredArgsConstructor
public class HotWorkApproverResolver {

    private final ObjectMapper objectMapper;

    public HotWorkApproverResolveResultVO resolve(HotWorkApproverResolveDTO request) {
        HotWorkApproverResolveResultVO result = new HotWorkApproverResolveResultVO();
        result.setNode(request.getNode());
        if (request.getNode() == null) {
            result.setResolved(false);
            result.setFailReason("node is required");
            return result;
        }
        String ruleType = request.getNode().getApproverRuleType();
        String ruleValue = request.getNode().getApproverRuleValue();
        List<HotWorkApproverVO> approvers = new ArrayList<HotWorkApproverVO>();
        try {
            if (ApproverRuleType.FIXED_USERS.equals(ruleType)) {
                approvers.addAll(parseFixedUsers(ruleValue));
            } else if (ApproverRuleType.FORM_FIELD.equals(ruleType)) {
                approvers.addAll(parseFormField(request, ruleValue));
            } else {
                result.setResolved(false);
                result.setFailReason("unsupported approver rule: " + ruleType);
                return result;
            }
        } catch (Exception ex) {
            result.setResolved(false);
            result.setFailReason("invalid approver rule: " + ex.getMessage());
            return result;
        }
        if (approvers.isEmpty()) {
            result.setResolved(false);
            result.setFailReason("no approver resolved for node " + request.getNode().getNodeName());
            return result;
        }
        result.setResolved(true);
        result.setApprovers(approvers);
        return result;
    }

    private List<HotWorkApproverVO> parseFixedUsers(String ruleValue) throws Exception {
        List<HotWorkApproverVO> list = new ArrayList<HotWorkApproverVO>();
        JsonNode root = objectMapper.readTree(ruleValue);
        JsonNode userIds = root.get("userIds");
        if (userIds == null || !userIds.isArray()) {
            return list;
        }
        Iterator<JsonNode> it = userIds.elements();
        while (it.hasNext()) {
            Long userId = it.next().asLong();
            HotWorkApproverVO vo = new HotWorkApproverVO();
            vo.setUserId(userId);
            vo.setUserName("用户" + userId);
            vo.setRoleLabel("固定审批人");
            list.add(vo);
        }
        return list;
    }

    private List<HotWorkApproverVO> parseFormField(HotWorkApproverResolveDTO request, String ruleValue) throws Exception {
        List<HotWorkApproverVO> list = new ArrayList<HotWorkApproverVO>();
        JsonNode root = objectMapper.readTree(ruleValue);
        String field = root.path("field").asText(null);
        if (!StringUtils.hasText(field)) {
            return list;
        }
        Long userId = null;
        if ("permitIssuerUserId".equals(field)) {
            userId = request.getPermitIssuerUserId();
        } else if ("supervisorUserId".equals(field)) {
            userId = request.getSupervisorUserId();
        }
        if (userId == null) {
            return list;
        }
        HotWorkApproverVO vo = new HotWorkApproverVO();
        vo.setUserId(userId);
        vo.setUserName("用户" + userId);
        vo.setRoleLabel(field);
        list.add(vo);
        return list;
    }
}
