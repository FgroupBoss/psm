package com.fgroupboss.ai.psm.processsafety.pssr.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.processsafety.pssr.model.dto.PssrTemplateRequest;
import com.fgroupboss.ai.psm.processsafety.pssr.model.vo.PssrTemplateVO;
import com.fgroupboss.ai.psm.processsafety.pssr.service.PssrTemplateService;
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

/**
 * PSSR 清单模板接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pssr/templates")
public class PssrTemplateController {

    private final PssrTemplateService pssrTemplateService;

    @GetMapping
    public ResponseVO<PageResult<PssrTemplateVO>> page(@RequestParam Long tenantId,
                                                     @RequestParam(required = false) String keyword,
                                                     @RequestParam(defaultValue = "1") int pageNo,
                                                     @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(pssrTemplateService.page(tenantId, keyword, pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseVO<PssrTemplateVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(pssrTemplateService.getById(tenantId, id));
    }

    @PostMapping
    public ResponseVO<PssrTemplateVO> create(@Valid @RequestBody PssrTemplateRequest request,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(pssrTemplateService.create(request, operator));
    }

    @PutMapping("/{id}")
    public ResponseVO<PssrTemplateVO> update(@PathVariable Long id,
                                             @Valid @RequestBody PssrTemplateRequest request,
                                             @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(pssrTemplateService.update(id, request, operator));
    }

    @DeleteMapping("/{id}")
    public ResponseVO<Void> delete(@PathVariable Long id,
                                   @RequestParam Long tenantId,
                                   @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        pssrTemplateService.delete(tenantId, id, operator);
        return ResponseVO.success();
    }
}
