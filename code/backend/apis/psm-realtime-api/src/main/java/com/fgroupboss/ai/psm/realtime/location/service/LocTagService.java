package com.fgroupboss.ai.psm.realtime.location.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.realtime.location.model.dto.LocTagBindRequest;
import com.fgroupboss.ai.psm.realtime.location.model.dto.LocTagRequest;
import com.fgroupboss.ai.psm.realtime.location.model.vo.LocTagBindingVO;
import com.fgroupboss.ai.psm.realtime.location.model.vo.LocTagVO;

public interface LocTagService {

    PageResult<LocTagVO> page(Long tenantId, String keyword, String status, int pageNo, int pageSize);

    LocTagVO getByTagNo(Long tenantId, String tagNo);

    LocTagVO create(LocTagRequest request);

    LocTagBindingVO bind(String tagNo, LocTagBindRequest request);

    LocTagBindingVO unbind(String tagNo, Long tenantId);
}
