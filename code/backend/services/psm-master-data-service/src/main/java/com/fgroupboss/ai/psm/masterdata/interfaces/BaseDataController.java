package com.fgroupboss.ai.psm.masterdata.interfaces;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.masterdata.config.BaseDataType;
import com.fgroupboss.ai.psm.masterdata.model.BaseDataRecord;
import com.fgroupboss.ai.psm.masterdata.model.BaseDataRequest;
import com.fgroupboss.ai.psm.masterdata.service.BaseDataService;
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
@RequestMapping("/api")
public class BaseDataController {

    private final BaseDataService service;

    public BaseDataController(BaseDataService service) {
        this.service = service;
    }

    @PostMapping("/{type:areas|units|equipments|monitor-points}")
    public ResponseVO<BaseDataRecord> create(@PathVariable String type,
                                             @RequestBody BaseDataRequest request,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.create(BaseDataType.fromPath(type), request, operator(userId, username, operator)));
    }

    @PutMapping("/{type:areas|units|equipments|monitor-points}/{id}")
    public ResponseVO<BaseDataRecord> update(@PathVariable String type,
                                             @PathVariable Long id,
                                             @RequestBody BaseDataRequest request,
                                             @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                             @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(service.update(BaseDataType.fromPath(type), id, request, operator(userId, username, operator)));
    }

    @PostMapping("/{type:areas|units|equipments|monitor-points}/{id}/enable")
    public ResponseVO<Void> enable(@PathVariable String type,
                                   @PathVariable Long id,
                                   @RequestParam Long tenantId,
                                   @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                   @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.enable(BaseDataType.fromPath(type), tenantId, id, operator(userId, username, operator));
        return ResponseVO.success();
    }

    @PostMapping("/{type:areas|units|equipments|monitor-points}/{id}/disable")
    public ResponseVO<Void> disable(@PathVariable String type,
                                    @PathVariable Long id,
                                    @RequestParam Long tenantId,
                                    @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                    @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                    @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.disable(BaseDataType.fromPath(type), tenantId, id, operator(userId, username, operator));
        return ResponseVO.success();
    }

    @DeleteMapping("/{type:areas|units|equipments|monitor-points}/{id}")
    public ResponseVO<Void> delete(@PathVariable String type,
                                   @PathVariable Long id,
                                   @RequestParam Long tenantId,
                                   @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                   @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        service.delete(BaseDataType.fromPath(type), tenantId, id, operator(userId, username, operator));
        return ResponseVO.success();
    }

    @GetMapping("/{type:areas|units|equipments|monitor-points}/{id}")
    public ResponseVO<BaseDataRecord> get(@PathVariable String type,
                                          @PathVariable Long id,
                                          @RequestParam Long tenantId) {
        return ResponseVO.success(service.get(BaseDataType.fromPath(type), tenantId, id));
    }

    @GetMapping("/{type:areas|units|equipments|monitor-points}")
    public ResponseVO<PageResult<BaseDataRecord>> page(@PathVariable String type,
                                                       @RequestParam Long tenantId,
                                                       @RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) String status,
                                                       @RequestParam(defaultValue = "1") int pageNo,
                                                       @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(service.page(BaseDataType.fromPath(type), tenantId, keyword, status, pageNo, pageSize));
    }

    @GetMapping("/{type:areas|units|equipments|monitor-points}/tree")
    public ResponseVO<List<BaseDataRecord>> tree(@PathVariable String type,
                                                 @RequestParam Long tenantId) {
        return ResponseVO.success(service.tree(BaseDataType.fromPath(type), tenantId));
    }

    private String operator(String userId, String username, String fallback) {
        return UserContextResolver.operator(userId, username, fallback);
    }
}
