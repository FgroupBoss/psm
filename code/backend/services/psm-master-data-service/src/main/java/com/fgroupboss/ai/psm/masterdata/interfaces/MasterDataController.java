package com.fgroupboss.ai.psm.masterdata.interfaces;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.masterdata.config.MasterDataType;
import com.fgroupboss.ai.psm.masterdata.model.MasterDataRecord;
import com.fgroupboss.ai.psm.masterdata.model.MasterDataRequest;
import com.fgroupboss.ai.psm.masterdata.service.MasterDataService;
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

import java.util.List;

@RestController
@RequestMapping("/api/master-data")
public class MasterDataController {

    private final MasterDataService service;

    public MasterDataController(MasterDataService service) {
        this.service = service;
    }

    @PostMapping("/{type}")
    public ResponseVO<MasterDataRecord> create(@PathVariable String type,
                                               @RequestBody MasterDataRequest request,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.create(MasterDataType.fromPath(type), request, UserContextResolver.operator(userId, username, operator)));
    }

    @PutMapping("/{type}/{id}")
    public ResponseVO<MasterDataRecord> update(@PathVariable String type,
                                               @PathVariable Long id,
                                               @RequestBody MasterDataRequest request,
                                               @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                               @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                               @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.update(MasterDataType.fromPath(type), id, request, UserContextResolver.operator(userId, username, operator)));
    }

    @PostMapping("/{type}/{id}/disable")
    public ResponseVO<Void> disable(@PathVariable String type,
                                    @PathVariable Long id,
                                    @RequestParam Long tenantId,
                                    @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                    @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.disable(MasterDataType.fromPath(type), tenantId, id, UserContextResolver.operator(userId, username, operator));
        return ResponseVO.success();
    }

    @DeleteMapping("/{type}/{id}")
    public ResponseVO<Void> delete(@PathVariable String type,
                                   @PathVariable Long id,
                                   @RequestParam Long tenantId,
                                   @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                   @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.delete(MasterDataType.fromPath(type), tenantId, id, UserContextResolver.operator(userId, username, operator));
        return ResponseVO.success();
    }

    @GetMapping("/{type}/{id}")
    public ResponseVO<MasterDataRecord> get(@PathVariable String type,
                                            @PathVariable Long id,
                                            @RequestParam Long tenantId) {
        return ResponseVO.success(service.get(MasterDataType.fromPath(type), tenantId, id));
    }

    @GetMapping("/{type}")
    public ResponseVO<PageResult<MasterDataRecord>> page(@PathVariable String type,
                                                         @RequestParam Long tenantId,
                                                         @RequestParam(required = false) String keyword,
                                                         @RequestParam(defaultValue = "1") int pageNo,
                                                         @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.page(MasterDataType.fromPath(type), tenantId, keyword, pageNo, pageSize));
    }

    @GetMapping("/{type}/tree")
    public ResponseVO<List<MasterDataRecord>> tree(@PathVariable String type,
                                                   @RequestParam Long tenantId) {
        return ResponseVO.success(service.tree(MasterDataType.fromPath(type), tenantId));
    }
}
