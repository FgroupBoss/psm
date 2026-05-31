package com.fgroupboss.ai.psm.processsafety.pha.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.processsafety.pha.model.dto.PhaRecommendationRequest;
import com.fgroupboss.ai.psm.processsafety.pha.model.dto.RecommendationActionRequest;
import com.fgroupboss.ai.psm.processsafety.pha.model.vo.PhaRecommendationVO;
import java.util.List;

public interface PhaRecommendationService {
    PageResult<PhaRecommendationVO> page(Long tenantId, Long projectId, String status, int pageNo, int pageSize);
    List<PhaRecommendationVO> list(Long tenantId, Long projectId, String status);
    PhaRecommendationVO create(PhaRecommendationRequest request, String operator);
    PhaRecommendationVO assign(Long tenantId, Long id, RecommendationActionRequest request, String operator);
    PhaRecommendationVO rectify(Long tenantId, Long id, RecommendationActionRequest request, String operator);
    PhaRecommendationVO verify(Long tenantId, Long id, RecommendationActionRequest request, String operator);
    PhaRecommendationVO close(Long tenantId, Long id, RecommendationActionRequest request, String operator);
}
