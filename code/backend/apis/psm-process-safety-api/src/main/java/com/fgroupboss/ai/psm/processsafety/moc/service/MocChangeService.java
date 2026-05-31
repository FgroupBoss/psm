package com.fgroupboss.ai.psm.processsafety.moc.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.processsafety.moc.model.dto.MocApproveRequest;
import com.fgroupboss.ai.psm.processsafety.moc.model.dto.MocChangeRequest;
import com.fgroupboss.ai.psm.processsafety.moc.model.dto.MocImpactAnalysisRequest;
import com.fgroupboss.ai.psm.processsafety.moc.model.dto.MocImplementationTaskRequest;
import com.fgroupboss.ai.psm.processsafety.moc.model.dto.MocVerifyRequest;
import com.fgroupboss.ai.psm.processsafety.moc.model.vo.MocChangeVO;
import com.fgroupboss.ai.psm.processsafety.moc.model.vo.MocImpactAnalysisVO;
import com.fgroupboss.ai.psm.processsafety.moc.model.vo.MocImplementationTaskVO;

import java.util.List;

/**
 * MOC 变更全流程服务。
 */
public interface MocChangeService {

    PageResult<MocChangeVO> page(Long tenantId, String keyword, String status, int pageNo, int pageSize);

    MocChangeVO getById(Long tenantId, Long id);

    MocChangeVO create(MocChangeRequest request, String operator);

    MocChangeVO update(Long id, MocChangeRequest request, String operator);

    void delete(Long tenantId, Long id, String operator);

    MocChangeVO submit(Long tenantId, Long id, String operator);

    List<MocImpactAnalysisVO> listImpactAnalysis(Long tenantId, Long changeId);

    MocImpactAnalysisVO saveImpactAnalysis(Long changeId, MocImpactAnalysisRequest request, String operator);

    MocChangeVO approve(Long changeId, MocApproveRequest request, String operator);

    List<MocImplementationTaskVO> listImplementationTasks(Long tenantId, Long changeId);

    MocImplementationTaskVO createImplementationTask(Long changeId, MocImplementationTaskRequest request, String operator);

    MocChangeVO verify(Long changeId, MocVerifyRequest request, String operator);

    MocChangeVO close(Long tenantId, Long changeId, String operator);
}
