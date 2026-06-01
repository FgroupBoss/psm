package com.fgroupboss.ai.psm.realtime.alarm.controller;

import com.fgroupboss.ai.psm.realtime.alarm.model.dto.AlarmRuleSaveRequest;
import com.fgroupboss.ai.psm.realtime.alarm.model.vo.AlarmRuleVO;
import com.fgroupboss.ai.psm.realtime.alarm.service.AlarmRuleService;
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
 * AlarmRule 模块 HTTP API。
 * <p>实时告警事件与规则配置。</p>
 * <p>基础路径：{@code /api/alarms/rules}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms/rules")
public class AlarmRuleController {

    private final AlarmRuleService alarmRuleService;

    /**
     * 查询列表。
     * <p>HTTP GET {@code /api/alarms/rules}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param ruleType ruleType 参数
     * @return 列表数据，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping
    public ResponseVO<List<AlarmRuleVO>> list(@RequestParam Long tenantId,
                                              @RequestParam(required = false) String ruleType) {
        return ResponseVO.success(alarmRuleService.list(tenantId, ruleType));
    }

    /**
     * 查询作业票详情。
     * <p>HTTP GET {@code /api/alarms/rules/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<AlarmRuleVO> detail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(alarmRuleService.get(tenantId, id));
    }

    /**
     * 新建记录。
     * <p>HTTP POST {@code /api/alarms/rules}</p>
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping
    public ResponseVO<AlarmRuleVO> create(@Valid @RequestBody AlarmRuleSaveRequest request) {
        return ResponseVO.success(alarmRuleService.create(request));
    }

    /**
     * 更新记录。
     * <p>HTTP PUT {@code /api/alarms/rules/{id}}</p>
     * @param id 资源主键 ID
     * @param request 请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PutMapping("/{id}")
    public ResponseVO<AlarmRuleVO> update(@PathVariable Long id, @Valid @RequestBody AlarmRuleSaveRequest request) {
        return ResponseVO.success(alarmRuleService.update(id, request));
    }
}
