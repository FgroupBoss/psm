package com.fgroupboss.ai.psm.processsafety.configrule.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.processsafety.configrule.config.ConfigType;
import com.fgroupboss.ai.psm.processsafety.configrule.model.dto.ConfigItemRequest;
import com.fgroupboss.ai.psm.processsafety.configrule.model.dto.RuleEvaluationRequest;
import com.fgroupboss.ai.psm.processsafety.configrule.model.vo.ConfigItemVO;
import com.fgroupboss.ai.psm.processsafety.configrule.model.vo.RuleEvaluationResultVO;
import com.fgroupboss.ai.psm.processsafety.configrule.service.ConfigRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统配置与规则管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/config")
public class ConfigRuleController {

    private final ConfigRuleService service;

    /**
     * 接口用途：创建业务数据。
     */
    @PostMapping("/{type:dictionaries|forms|workflows|rules}")
    public ResponseVO<ConfigItemVO> create(@PathVariable String type,
                                           @Valid @RequestBody ConfigItemRequest request,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.create(ConfigType.fromPath(type), request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：创建通知配置。
     */
    @PostMapping("/notifications/templates")
    public ResponseVO<ConfigItemVO> createNotification(@Valid @RequestBody ConfigItemRequest request,
                                                       @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                       @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.create(ConfigType.NOTIFICATION_TEMPLATE, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：创建附件策略配置。
     */
    @PostMapping("/attachments/policies")
    public ResponseVO<ConfigItemVO> createAttachmentPolicy(@Valid @RequestBody ConfigItemRequest request,
                                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.create(ConfigType.ATTACHMENT_POLICY, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：更新业务数据。
     */
    @PutMapping("/{type:dictionaries|forms|workflows|rules}/{id}")
    public ResponseVO<ConfigItemVO> update(@PathVariable String type,
                                           @PathVariable Long id,
                                           @Valid @RequestBody ConfigItemRequest request,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.update(ConfigType.fromPath(type), id, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：更新通知配置。
     */
    @PutMapping("/notifications/templates/{id}")
    public ResponseVO<ConfigItemVO> updateNotification(@PathVariable Long id,
                                                       @Valid @RequestBody ConfigItemRequest request,
                                                       @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                       @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                       @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.update(ConfigType.NOTIFICATION_TEMPLATE, id, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：更新附件策略配置。
     */
    @PutMapping("/attachments/policies/{id}")
    public ResponseVO<ConfigItemVO> updateAttachmentPolicy(@PathVariable Long id,
                                                           @Valid @RequestBody ConfigItemRequest request,
                                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.update(ConfigType.ATTACHMENT_POLICY, id, request, operator(userId, username, operator)));
    }

    /**
     * 接口用途：分页查询业务数据。
     */
    @GetMapping("/{type:dictionaries|forms|workflows|rules}")
    public ResponseVO<PageResult<ConfigItemVO>> page(@PathVariable String type,
                                                     @RequestParam Long tenantId,
                                                     @RequestParam(required = false) String keyword,
                                                     @RequestParam(required = false) String status,
                                                     @RequestParam(required = false) String bizScene,
                                                     @RequestParam(defaultValue = "1") int pageNo,
                                                     @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.page(ConfigType.fromPath(type), tenantId, keyword, status, bizScene, pageNo, pageSize));
    }

    /**
     * 接口用途：处理接口请求。
     */
    @GetMapping("/notifications/templates")
    public ResponseVO<PageResult<ConfigItemVO>> pageNotifications(@RequestParam Long tenantId,
                                                                  @RequestParam(required = false) String keyword,
                                                                  @RequestParam(required = false) String status,
                                                                  @RequestParam(required = false) String bizScene,
                                                                  @RequestParam(defaultValue = "1") int pageNo,
                                                                  @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.page(ConfigType.NOTIFICATION_TEMPLATE, tenantId, keyword, status, bizScene, pageNo, pageSize));
    }

    /**
     * 接口用途：处理接口请求。
     */
    @GetMapping("/attachments/policies")
    public ResponseVO<PageResult<ConfigItemVO>> pageAttachmentPolicies(@RequestParam Long tenantId,
                                                                       @RequestParam(required = false) String keyword,
                                                                       @RequestParam(required = false) String status,
                                                                       @RequestParam(required = false) String bizScene,
                                                                       @RequestParam(defaultValue = "1") int pageNo,
                                                                       @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.page(ConfigType.ATTACHMENT_POLICY, tenantId, keyword, status, bizScene, pageNo, pageSize));
    }

    /**
     * 接口用途：查询详情。
     */
    @GetMapping("/items/{id}")
    public ResponseVO<ConfigItemVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(service.get(tenantId, id));
    }

    /**
     * 接口用途：发布业务数据。
     */
    @PostMapping("/items/{id}/publish")
    public ResponseVO<Void> publish(@PathVariable Long id,
                                    @RequestParam Long tenantId,
                                    @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                    @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.publish(tenantId, id, operator(userId, username, operator));
        return ResponseVO.success();
    }

    /**
     * 接口用途：处理接口请求。
     */
    @PostMapping("/items/{id}/disable")
    public ResponseVO<Void> disable(@PathVariable Long id,
                                    @RequestParam Long tenantId,
                                    @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                    @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.disable(tenantId, id, operator(userId, username, operator));
        return ResponseVO.success();
    }

    /**
     * 接口用途：删除业务数据。
     */
    @DeleteMapping("/items/{id}")
    public ResponseVO<Void> delete(@PathVariable Long id,
                                   @RequestParam Long tenantId,
                                   @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                   @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.delete(tenantId, id, operator(userId, username, operator));
        return ResponseVO.success();
    }

    /**
     * 接口用途：处理接口请求。
     */
    @PostMapping("/rules/evaluate")
    public ResponseVO<List<RuleEvaluationResultVO>> evaluate(@Valid @RequestBody RuleEvaluationRequest request) {
        return ResponseVO.success(service.evaluate(request));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
