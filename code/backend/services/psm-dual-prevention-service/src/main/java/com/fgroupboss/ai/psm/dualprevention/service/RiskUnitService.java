package com.fgroupboss.ai.psm.dualprevention.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.dualprevention.model.dto.RiskEventRequest;
import com.fgroupboss.ai.psm.dualprevention.model.dto.RiskUnitRequest;
import com.fgroupboss.ai.psm.dualprevention.model.vo.RiskColorStatVO;
import com.fgroupboss.ai.psm.dualprevention.model.vo.RiskEventVO;
import com.fgroupboss.ai.psm.dualprevention.model.vo.RiskUnitTreeNodeVO;
import com.fgroupboss.ai.psm.dualprevention.model.vo.RiskUnitVO;

import java.util.List;
import java.util.Map;

public interface RiskUnitService {

    PageResult<RiskUnitVO> page(Long tenantId, String keyword, String status, Long areaId, int pageNo, int pageSize);

    RiskUnitVO getById(Long tenantId, Long id);

    RiskUnitVO create(RiskUnitRequest request, String operator);

    RiskUnitVO update(Long id, RiskUnitRequest request, String operator);

    void delete(Long tenantId, Long id, String operator);

    List<RiskUnitTreeNodeVO> tree(Long tenantId, Long areaId);

    List<RiskEventVO> listEvents(Long tenantId, Long unitId);

    RiskEventVO createEvent(Long unitId, RiskEventRequest request, String operator);

    Map<String, Long> colorMap(Long tenantId, Long areaId);

    List<RiskColorStatVO> colorStats(Long tenantId, Long areaId);
}
