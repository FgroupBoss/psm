package com.fgroupboss.ai.psm.identity.masterdata.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.identity.masterdata.config.MasterDataType;
import com.fgroupboss.ai.psm.identity.masterdata.model.dto.MasterDataRequest;
import com.fgroupboss.ai.psm.identity.masterdata.model.vo.MasterDataRecordVO;
import com.fgroupboss.ai.psm.identity.masterdata.service.MasterDataService;
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
 * MasterData 模块 HTTP API。
 * <p>基础路径：{@code /api/master-data}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/master-data")
public class MasterDataController {

    private final MasterDataService service;

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/master-data/{type}}</p>
     * @param type type 参数
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{type}")
    public ResponseVO<MasterDataRecordVO> create(@PathVariable String type,
                                                 @Valid @RequestBody MasterDataRequest request,
                                                 @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                 @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.create(MasterDataType.fromPath(type), request, operator(userId, username, operator)));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/master-data/{type}/{id}}</p>
     * @param type type 参数
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{type}/{id}")
    public ResponseVO<MasterDataRecordVO> update(@PathVariable String type,
                                                 @PathVariable Long id,
                                                 @Valid @RequestBody MasterDataRequest request,
                                                 @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                                 @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                                 @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.update(MasterDataType.fromPath(type), id, request, operator(userId, username, operator)));
    }

    /**
     * 新增disable或触发disable相关动作。
     * <p>HTTP POST {@code /api/master-data/{type}/{id}/disable}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param type type 参数
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/{type}/{id}/disable")
    public ResponseVO<Void> disable(@PathVariable String type,
                                    @PathVariable Long id,
                                    @RequestParam Long tenantId,
                                    @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                    @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.disable(MasterDataType.fromPath(type), tenantId, id, operator(userId, username, operator));
        return ResponseVO.success();
    }

    /**
     * 删除记录。
     * <p>HTTP DELETE {@code /api/master-data/{type}/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param type type 参数
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 无业务载荷（成功即可），统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @DeleteMapping("/{type}/{id}")
    public ResponseVO<Void> delete(@PathVariable String type,
                                   @PathVariable Long id,
                                   @RequestParam Long tenantId,
                                   @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                   @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.delete(MasterDataType.fromPath(type), tenantId, id, operator(userId, username, operator));
        return ResponseVO.success();
    }

    /**
     * 查询单条详情。
     * <p>HTTP GET {@code /api/master-data/{type}/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param type type 参数
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{type}/{id}")
    public ResponseVO<MasterDataRecordVO> get(@PathVariable String type,
                                              @PathVariable Long id,
                                              @RequestParam Long tenantId) {
        return ResponseVO.success(service.get(MasterDataType.fromPath(type), tenantId, id));
    }

    /**
     * 分页查询列表。
     * <p>HTTP GET {@code /api/master-data/{type}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param type type 参数
     * @param tenantId 租户 ID，多租户隔离必填
     * @param keyword 模糊搜索关键字
     * @param pageNo 页码，从 1 开始
     * @param pageSize 每页条数，默认 20
     * @return 分页数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{type}")
    public ResponseVO<PageResult<MasterDataRecordVO>> page(@PathVariable String type,
                                                           @RequestParam Long tenantId,
                                                           @RequestParam(required = false) String keyword,
                                                           @RequestParam(defaultValue = "1") int pageNo,
                                                           @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.page(MasterDataType.fromPath(type), tenantId, keyword, pageNo, pageSize));
    }

    /**
     * 查询tree。
     * <p>HTTP GET {@code /api/master-data/{type}/tree}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param type type 参数
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{type}/tree")
    public ResponseVO<List<MasterDataRecordVO>> tree(@PathVariable String type,
                                                     @RequestParam Long tenantId) {
        return ResponseVO.success(service.tree(MasterDataType.fromPath(type), tenantId));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
