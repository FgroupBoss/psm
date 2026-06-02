package com.fgroupboss.ai.psm.processsafety.configrule.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
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
 * ConfigRule 模块 HTTP API。
 * <p>基础路径：{@code /api/config}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/config")
public class ConfigRuleController {

    private final ConfigRuleService service;

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/config/{type:dictionaries|forms|workflows|rules}}</p>
     * @param type type 参数
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{type:dictionaries|forms|workflows|rules}")
    public ResponseVO<ConfigItemVO> create(@LoginContext UserContext loginContext,
                                        @PathVariable String type,
                                           @Valid @RequestBody ConfigItemRequest request,
                                                                                                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.create(ConfigType.fromPath(type), request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 新增templates或触发templates相关动作。
     * <p>HTTP POST {@code /api/config/notifications/templates}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/notifications/templates")
    public ResponseVO<ConfigItemVO> createNotification(@LoginContext UserContext loginContext,
                                        @Valid @RequestBody ConfigItemRequest request,
                                                                                                                                                                     @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.create(ConfigType.NOTIFICATION_TEMPLATE, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 新增policies或触发policies相关动作。
     * <p>HTTP POST {@code /api/config/attachments/policies}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/attachments/policies")
    public ResponseVO<ConfigItemVO> createAttachmentPolicy(@LoginContext UserContext loginContext,
                                        @Valid @RequestBody ConfigItemRequest request,
                                                                                                                                                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.create(ConfigType.ATTACHMENT_POLICY, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/config/{type:dictionaries|forms|workflows|rules}/{id}}</p>
     * @param type type 参数
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{type:dictionaries|forms|workflows|rules}/{id}")
    public ResponseVO<ConfigItemVO> update(@LoginContext UserContext loginContext,
                                        @PathVariable String type,
                                           @PathVariable Long id,
                                           @Valid @RequestBody ConfigItemRequest request,
                                                                                                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.update(ConfigType.fromPath(type), id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新templates。
     * <p>HTTP PUT {@code /api/config/notifications/templates/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/notifications/templates/{id}")
    public ResponseVO<ConfigItemVO> updateNotification(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                       @Valid @RequestBody ConfigItemRequest request,
                                                                                                                                                                     @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.update(ConfigType.NOTIFICATION_TEMPLATE, id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新policies。
     * <p>HTTP PUT {@code /api/config/attachments/policies/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/attachments/policies/{id}")
    public ResponseVO<ConfigItemVO> updateAttachmentPolicy(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                           @Valid @RequestBody ConfigItemRequest request,
                                                                                                                                                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.update(ConfigType.ATTACHMENT_POLICY, id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/config/{type:dictionaries|forms|workflows|rules}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param type type 参数
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param status 业务状态筛选
     * @param bizScene bizScene 参数
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{type:dictionaries|forms|workflows|rules}")
    public ResponseVO<PageResult<ConfigItemVO>> page(@LoginContext UserContext loginContext,
                                        @PathVariable String type,
                                                                                                          @RequestParam(required = false) String keyword,
                                                     @RequestParam(required = false) String status,
                                                     @RequestParam(required = false) String bizScene,
                                                     @RequestParam(defaultValue = "1") int pageNo,
                                                     @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.page(ConfigType.fromPath(type), loginContext.getTenantId(), keyword, status, bizScene, pageNo, pageSize));
    }

    /**
     * 查询templates。
     * <p>HTTP GET {@code /api/config/notifications/templates}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param status 业务状态筛选
     * @param bizScene bizScene 参数
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/notifications/templates")
    public ResponseVO<PageResult<ConfigItemVO>> pageNotifications(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) String keyword,
                                                                  @RequestParam(required = false) String status,
                                                                  @RequestParam(required = false) String bizScene,
                                                                  @RequestParam(defaultValue = "1") int pageNo,
                                                                  @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.page(ConfigType.NOTIFICATION_TEMPLATE, loginContext.getTenantId(), keyword, status, bizScene, pageNo, pageSize));
    }

    /**
     * 查询policies。
     * <p>HTTP GET {@code /api/config/attachments/policies}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param status 业务状态筛选
     * @param bizScene bizScene 参数
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/attachments/policies")
    public ResponseVO<PageResult<ConfigItemVO>> pageAttachmentPolicies(@LoginContext UserContext loginContext,
                                        @RequestParam(required = false) String keyword,
                                                                       @RequestParam(required = false) String status,
                                                                       @RequestParam(required = false) String bizScene,
                                                                       @RequestParam(defaultValue = "1") int pageNo,
                                                                       @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.page(ConfigType.ATTACHMENT_POLICY, loginContext.getTenantId(), keyword, status, bizScene, pageNo, pageSize));
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/config/items/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/items/{id}")
    public ResponseVO<ConfigItemVO> get(@LoginContext UserContext loginContext,
                                        @PathVariable Long id) {
        return ResponseVO.success(service.get(loginContext.getTenantId(), id));
    }

    /**
     * 新增publish或触发publish相关动作。
     * <p>HTTP POST {@code /api/config/items/{id}/publish}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/items/{id}/publish")
    public ResponseVO<Void> publish(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                                                @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.publish(loginContext.getTenantId(), id, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

    /**
     * 新增disable或触发disable相关动作。
     * <p>HTTP POST {@code /api/config/items/{id}/disable}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/items/{id}/disable")
    public ResponseVO<Void> disable(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                                                @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.disable(loginContext.getTenantId(), id, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

    /**
     * 删除记录。
     * <p>HTTP DELETE {@code /api/config/items/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/items/{id}")
    public ResponseVO<Void> delete(@LoginContext UserContext loginContext,
                                        @PathVariable Long id,
                                                                                                                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.delete(loginContext.getTenantId(), id, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

    /**
     * 新增evaluate或触发evaluate相关动作。
     * <p>HTTP POST {@code /api/config/rules/evaluate}</p>
     * @param request 请求体
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/rules/evaluate")
    public ResponseVO<List<RuleEvaluationResultVO>> evaluate(@Valid @RequestBody RuleEvaluationRequest request) {
        return ResponseVO.success(service.evaluate(request));
    }

}
