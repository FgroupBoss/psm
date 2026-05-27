package com.fgroupboss.ai.psm.masterdata.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.masterdata.config.MasterDataType;
import com.fgroupboss.ai.psm.masterdata.model.dto.MasterDataRequest;
import com.fgroupboss.ai.psm.masterdata.model.vo.MasterDataRecordVO;
import com.fgroupboss.ai.psm.masterdata.service.MasterDataService;
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
 * 主数据管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/master-data")
public class MasterDataController {

    private final MasterDataService service;

    /**
     * 接口用途：创建业务数据。
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
     * 接口用途：更新业务数据。
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
     * 接口用途：处理接口请求。
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
     * 接口用途：删除业务数据。
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
     * 接口用途：查询详情。
     */
    @GetMapping("/{type}/{id}")
    public ResponseVO<MasterDataRecordVO> get(@PathVariable String type,
                                              @PathVariable Long id,
                                              @RequestParam Long tenantId) {
        return ResponseVO.success(service.get(MasterDataType.fromPath(type), tenantId, id));
    }

    /**
     * 接口用途：分页查询业务数据。
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
     * 接口用途：处理接口请求。
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
