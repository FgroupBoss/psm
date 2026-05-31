package com.fgroupboss.ai.psm.risk.dualprevention.service;

import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.ControlMeasureRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.RiskEventRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.ControlMeasureVO;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.RiskEventVO;

import java.util.List;

public interface RiskEventService {

    RiskEventVO update(Long eventId, RiskEventRequest request, String operator);

    void delete(Long tenantId, Long eventId, String operator);

    List<ControlMeasureVO> listMeasures(Long tenantId, Long eventId);

    ControlMeasureVO createMeasure(Long eventId, ControlMeasureRequest request, String operator);
}
