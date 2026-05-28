package com.fgroupboss.ai.psm.workpermit.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.workpermit.config.PermitCheckPoint;
import com.fgroupboss.ai.psm.workpermit.model.dto.SimopsConflictRuleRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.SimopsCoordinateRequest;
import com.fgroupboss.ai.psm.workpermit.model.dto.SimopsScanRequest;
import com.fgroupboss.ai.psm.workpermit.model.entity.WorkPermitEntity;
import com.fgroupboss.ai.psm.workpermit.model.vo.PreCheckResultVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.SimopsConflictRuleVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.SimopsCoordinationRecordVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.SimopsScanResultVO;
import com.fgroupboss.ai.psm.workpermit.model.vo.SimopsStatisticsVO;

import java.util.List;

/**
 * SIMOPS 交叉作业冲突识别与协调。
 */
public interface SimopsService {

    List<SimopsConflictRuleVO> listRules(Long tenantId, Boolean enabledOnly);

    SimopsConflictRuleVO createRule(SimopsConflictRuleRequest request);

    SimopsConflictRuleVO updateRule(Long id, SimopsConflictRuleRequest request);

    void deleteRule(Long tenantId, Long id);

    SimopsScanResultVO scan(SimopsScanRequest request);

    PageResult<SimopsScanResultVO> listConflicts(Long tenantId, Long workPermitId, String scanStage,
                                                 int pageNo, int pageSize);

    SimopsCoordinationRecordVO coordinate(Long scanResultId, SimopsCoordinateRequest request, String operator);

    SimopsStatisticsVO statistics(Long tenantId);

    /**
     * 作业票前置校验集成：SUBMIT、SITE_PERMIT 环节执行扫描，BLOCK 策略写入阻断原因。
     */
    void applyPreCheck(WorkPermitEntity permit, PermitCheckPoint checkPoint, PreCheckResultVO result);
}
