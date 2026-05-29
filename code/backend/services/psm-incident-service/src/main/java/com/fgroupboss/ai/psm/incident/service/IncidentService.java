package com.fgroupboss.ai.psm.incident.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.incident.model.dto.CapaVerifyRequest;
import com.fgroupboss.ai.psm.incident.model.dto.IncidentCapaRequest;
import com.fgroupboss.ai.psm.incident.model.dto.IncidentEvidenceRequest;
import com.fgroupboss.ai.psm.incident.model.dto.IncidentFromSourceRequest;
import com.fgroupboss.ai.psm.incident.model.dto.IncidentReportRequest;
import com.fgroupboss.ai.psm.incident.model.dto.IncidentRootCauseRequest;
import com.fgroupboss.ai.psm.incident.model.dto.IncidentTimelineRequest;
import com.fgroupboss.ai.psm.incident.model.dto.LessonLearnedRequest;
import com.fgroupboss.ai.psm.incident.model.dto.StartInvestigationRequest;
import com.fgroupboss.ai.psm.incident.model.vo.IncidentCapaVO;
import com.fgroupboss.ai.psm.incident.model.vo.IncidentEvidenceVO;
import com.fgroupboss.ai.psm.incident.model.vo.IncidentReportVO;
import com.fgroupboss.ai.psm.incident.model.vo.IncidentRootCauseVO;
import com.fgroupboss.ai.psm.incident.model.vo.IncidentTimelineVO;
import com.fgroupboss.ai.psm.incident.model.vo.LessonLearnedVO;

import java.util.List;

/**
 * 事故调查与 CAPA 闭环服务。
 */
public interface IncidentService {

    PageResult<IncidentReportVO> page(Long tenantId, String keyword, String status,
                                      String incidentType, String incidentLevel,
                                      int pageNo, int pageSize);

    IncidentReportVO getById(Long tenantId, Long id);

    IncidentReportVO create(IncidentReportRequest request, String operator);

    IncidentReportVO update(Long id, IncidentReportRequest request, String operator);

    IncidentReportVO createFromSource(IncidentFromSourceRequest request, String operator);

    IncidentReportVO startInvestigation(Long id, StartInvestigationRequest request, String operator);

    List<IncidentTimelineVO> listTimeline(Long tenantId, Long incidentId);

    IncidentTimelineVO addTimeline(Long incidentId, IncidentTimelineRequest request, String operator);

    List<IncidentEvidenceVO> listEvidence(Long tenantId, Long incidentId);

    IncidentEvidenceVO addEvidence(Long incidentId, IncidentEvidenceRequest request, String operator);

    List<IncidentRootCauseVO> listRootCauses(Long tenantId, Long incidentId);

    IncidentRootCauseVO addRootCause(Long incidentId, IncidentRootCauseRequest request, String operator);

    List<IncidentCapaVO> listCapa(Long tenantId, Long incidentId);

    IncidentCapaVO addCapa(Long incidentId, IncidentCapaRequest request, String operator);

    IncidentCapaVO verifyCapa(Long capaId, CapaVerifyRequest request, String operator);

    LessonLearnedVO addLessonLearned(Long incidentId, LessonLearnedRequest request, String operator);
}
