package com.fgroupboss.ai.psm.pha.service;

import com.fgroupboss.ai.psm.pha.model.dto.PhaNodeRequest;
import com.fgroupboss.ai.psm.pha.model.vo.PhaNodeVO;
import java.util.List;

public interface PhaNodeService {
    List<PhaNodeVO> listByProject(Long tenantId, Long projectId);
    PhaNodeVO create(Long projectId, PhaNodeRequest request, String operator);
}
