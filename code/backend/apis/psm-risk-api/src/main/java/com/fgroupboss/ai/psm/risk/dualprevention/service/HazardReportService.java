package com.fgroupboss.ai.psm.risk.dualprevention.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.HazardAreaOpenCheckRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.HazardConfirmRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.HazardEscalateRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.HazardOverdueCheckRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.HazardRectifyRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.HazardReportRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.dto.HazardReviewRequest;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.HazardAreaOpenCheckVO;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.HazardOverdueCheckVO;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.HazardReportVO;
import com.fgroupboss.ai.psm.risk.dualprevention.model.vo.HazardStatisticsVO;

public interface HazardReportService {

    PageResult<HazardReportVO> page(Long tenantId, String keyword, String status, String hazardLevel,
                                    Long areaId, Long riskUnitId, Integer overdueFlag,
                                    int pageNo, int pageSize);

    HazardReportVO getById(Long tenantId, Long id);

    HazardReportVO create(HazardReportRequest request, String operator);

    HazardReportVO confirm(Long id, HazardConfirmRequest request, String operator);

    HazardReportVO rectify(Long id, HazardRectifyRequest request, String operator);

    HazardReportVO review(Long id, HazardReviewRequest request, String operator);

    HazardOverdueCheckVO overdueCheck(HazardOverdueCheckRequest request);

    HazardAreaOpenCheckVO areaOpenCheck(HazardAreaOpenCheckRequest request);

    HazardReportVO escalate(Long id, HazardEscalateRequest request, String operator);

    HazardStatisticsVO statistics(Long tenantId, Long areaId);
}
