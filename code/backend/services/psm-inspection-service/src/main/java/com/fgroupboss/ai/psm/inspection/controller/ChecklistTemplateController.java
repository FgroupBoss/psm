package com.fgroupboss.ai.psm.inspection.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.inspection.model.dto.ChecklistTemplateRequest;
import com.fgroupboss.ai.psm.inspection.model.vo.ChecklistTemplateVO;
import com.fgroupboss.ai.psm.inspection.service.ChecklistTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 检查表模板接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inspection/checklist-templates")
public class ChecklistTemplateController {

    private final ChecklistTemplateService templateService;

    @GetMapping
    public ResponseVO<PageResult<ChecklistTemplateVO>> page(@RequestParam Long tenantId,
                                                            @RequestParam(required = false) String keyword,
                                                            @RequestParam(defaultValue = "1") int pageNo,
                                                            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(templateService.page(tenantId, keyword, pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseVO<ChecklistTemplateVO> get(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(templateService.getById(tenantId, id));
    }

    @PostMapping
    public ResponseVO<ChecklistTemplateVO> create(@Valid @RequestBody ChecklistTemplateRequest request) {
        return ResponseVO.success(templateService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseVO<ChecklistTemplateVO> update(@PathVariable Long id,
                                                  @Valid @RequestBody ChecklistTemplateRequest request) {
        return ResponseVO.success(templateService.update(id, request));
    }
}
