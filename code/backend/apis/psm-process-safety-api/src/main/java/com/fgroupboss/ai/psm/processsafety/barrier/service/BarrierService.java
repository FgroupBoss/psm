package com.fgroupboss.ai.psm.processsafety.barrier.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.processsafety.barrier.model.dto.BarrierDegradeRequest;
import com.fgroupboss.ai.psm.processsafety.barrier.model.dto.BarrierRequest;
import com.fgroupboss.ai.psm.processsafety.barrier.model.dto.BarrierRestoreRequest;
import com.fgroupboss.ai.psm.processsafety.barrier.model.dto.BarrierRuleCheckRequest;
import com.fgroupboss.ai.psm.processsafety.barrier.model.vo.BarrierHealthVO;
import com.fgroupboss.ai.psm.processsafety.barrier.model.vo.BarrierRuleCheckVO;
import com.fgroupboss.ai.psm.processsafety.barrier.model.vo.BarrierVO;

/**
 * 安全屏障台账与健康度管理。
 */
public interface BarrierService {

    PageResult<BarrierVO> page(Long tenantId, String keyword, String status, int pageNo, int pageSize);

    BarrierVO getById(Long tenantId, Long id);

    BarrierVO create(BarrierRequest request, String operator);

    BarrierVO update(Long id, BarrierRequest request, String operator);

    void delete(Long tenantId, Long id, String operator);

    BarrierHealthVO getHealth(Long tenantId, Long id);

    BarrierVO degrade(Long id, BarrierDegradeRequest request, String operator);

    BarrierVO restore(Long id, BarrierRestoreRequest request, String operator);

    BarrierRuleCheckVO ruleCheck(BarrierRuleCheckRequest request);
}
