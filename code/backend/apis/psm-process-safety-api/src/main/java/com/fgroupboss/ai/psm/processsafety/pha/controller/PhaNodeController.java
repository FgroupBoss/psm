package com.fgroupboss.ai.psm.processsafety.pha.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.processsafety.pha.model.dto.PhaNodeRequest;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.PhaNodeVO;
import com.fgroupboss.ai.psm.processsafety.pha.service.PhaNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pha/projects/{projectId}/nodes")
public class PhaNodeController {

    private final PhaNodeService phaNodeService;

    @GetMapping
    public ResponseVO<List<PhaNodeVO>> list(@PathVariable Long projectId, @RequestParam Long tenantId) {
        return ResponseVO.success(phaNodeService.listByProject(tenantId, projectId));
    }

    @PostMapping
    public ResponseVO<PhaNodeVO> create(@PathVariable Long projectId,
                                        @Valid @RequestBody PhaNodeRequest request,
                                        @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(phaNodeService.create(projectId, request, operator));
    }
}
