package com.fgroupboss.ai.psm.identity.masterdata.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.identity.masterdata.config.BaseDataType;
import com.fgroupboss.ai.psm.identity.masterdata.model.dto.BaseDataRequest;
import com.fgroupboss.ai.psm.identity.masterdata.model.vo.BaseDataRecordVO;
import com.fgroupboss.ai.psm.identity.masterdata.service.BaseDataService;
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
 * BaseData 模块 HTTP API。
 * <p>基础路径：{@code /api}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BaseDataController {

    private final BaseDataService service;

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/{type:areas|units|equipments|monitor-points}}</p>
     * @param type type 参数
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{type:areas|units|equipments|monitor-points}")
    public ResponseVO<BaseDataRecordVO> create(@LoginContext UserContext loginContext,
                                        @PathVariable String type,
                                               @Valid @RequestBody BaseDataRequest request,
                                                                                                                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.create(BaseDataType.fromPath(type), request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/{type:areas|units|equipments|monitor-points}/{id}}</p>
     * @param type type 参数
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{type:areas|units|equipments|monitor-points}/{id}")
    public ResponseVO<BaseDataRecordVO> update(@LoginContext UserContext loginContext,
                                        @PathVariable String type,
                                               @PathVariable Long id,
                                               @Valid @RequestBody BaseDataRequest request,
                                                                                                                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(service.update(BaseDataType.fromPath(type), id, request, UserContextResolver.operator(loginContext, operator)));
    }

    /**
     * 新增enable或触发enable相关动作。
     * <p>HTTP POST {@code /api/{type:areas|units|equipments|monitor-points}/{id}/enable}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param type type 参数
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{type:areas|units|equipments|monitor-points}/{id}/enable")
    public ResponseVO<Void> enable(@LoginContext UserContext loginContext,
                                        @PathVariable String type,
                                   @PathVariable Long id,
                                                                                                                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.enable(BaseDataType.fromPath(type), loginContext.getTenantId(), id, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

    /**
     * 新增disable或触发disable相关动作。
     * <p>HTTP POST {@code /api/{type:areas|units|equipments|monitor-points}/{id}/disable}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param type type 参数
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{type:areas|units|equipments|monitor-points}/{id}/disable")
    public ResponseVO<Void> disable(@LoginContext UserContext loginContext,
                                        @PathVariable String type,
                                    @PathVariable Long id,
                                                                                                                                                @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.disable(BaseDataType.fromPath(type), loginContext.getTenantId(), id, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

    /**
     * 删除记录。
     * <p>HTTP DELETE {@code /api/{type:areas|units|equipments|monitor-points}/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param type type 参数
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/{type:areas|units|equipments|monitor-points}/{id}")
    public ResponseVO<Void> delete(@LoginContext UserContext loginContext,
                                        @PathVariable String type,
                                   @PathVariable Long id,
                                                                                                                                            @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.delete(BaseDataType.fromPath(type), loginContext.getTenantId(), id, UserContextResolver.operator(loginContext, operator));
        return ResponseVO.success();
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/{type:areas|units|equipments|monitor-points}/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param type type 参数
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{type:areas|units|equipments|monitor-points}/{id}")
    public ResponseVO<BaseDataRecordVO> get(@LoginContext UserContext loginContext,
                                        @PathVariable String type,
                                            @PathVariable Long id) {
        return ResponseVO.success(service.get(BaseDataType.fromPath(type), loginContext.getTenantId(), id));
    }

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/{type:areas|units|equipments|monitor-points}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param type type 参数
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param status 业务状态筛选
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{type:areas|units|equipments|monitor-points}")
    public ResponseVO<PageResult<BaseDataRecordVO>> page(@LoginContext UserContext loginContext,
                                        @PathVariable String type,
                                                                                                                  @RequestParam(required = false) String keyword,
                                                         @RequestParam(required = false) String status,
                                                         @RequestParam(defaultValue = "1") int pageNo,
                                                         @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.page(BaseDataType.fromPath(type), loginContext.getTenantId(), keyword, status, pageNo, pageSize));
    }

    /**
     * 查询tree。
     * <p>HTTP GET {@code /api/{type:areas|units|equipments|monitor-points}/tree}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param type type 参数
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{type:areas|units|equipments|monitor-points}/tree")
    public ResponseVO<List<BaseDataRecordVO>> tree(@LoginContext UserContext loginContext,
                                        @PathVariable String type) {
        return ResponseVO.success(service.tree(BaseDataType.fromPath(type), loginContext.getTenantId()));
    }

}
