package com.fgroupboss.ai.psm.alarm.controller;

import com.fgroupboss.ai.psm.alarm.model.dto.AlarmRuleSaveRequest;
import com.fgroupboss.ai.psm.alarm.model.vo.AlarmRuleVO;
import com.fgroupboss.ai.psm.alarm.service.AlarmRuleService;
import com.fgroupboss.ai.psm.common.ResponseVO;
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
import java.util.List;

/**
 * 报警规则最小 CRUD（批次 4）。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms/rules")
public class AlarmRuleController {

    private final AlarmRuleService alarmRuleService;

    /**
     * 接口用途：查询列表数据。
     */
    @GetMapping
    public ResponseVO<List<AlarmRuleVO>> list(@RequestParam Long tenantId,
                                              @RequestParam(required = false) String ruleType) {
        return ResponseVO.success(alarmRuleService.list(tenantId, ruleType));
    }

    /**
     * 接口用途：查询详情。
     */
    @GetMapping("/{id}")
    public ResponseVO<AlarmRuleVO> detail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(alarmRuleService.get(tenantId, id));
    }

    /**
     * 接口用途：创建业务数据。
     */
    @PostMapping
    public ResponseVO<AlarmRuleVO> create(@Valid @RequestBody AlarmRuleSaveRequest request) {
        return ResponseVO.success(alarmRuleService.create(request));
    }

    /**
     * 接口用途：更新业务数据。
     */
    @PutMapping("/{id}")
    public ResponseVO<AlarmRuleVO> update(@PathVariable Long id, @Valid @RequestBody AlarmRuleSaveRequest request) {
        return ResponseVO.success(alarmRuleService.update(id, request));
    }
}
