package com.fgroupboss.ai.psm.realtime.alarm.service;

import com.fgroupboss.ai.psm.realtime.api.alarm.dto.AlarmActionRequest;
import com.fgroupboss.ai.psm.realtime.api.alarm.dto.AlarmAreaActiveCheckRequest;
import com.fgroupboss.ai.psm.realtime.alarm.model.dto.AlarmFalseCloseRequest;
import com.fgroupboss.ai.psm.realtime.alarm.model.dto.AlarmIngestRequest;
import com.fgroupboss.ai.psm.realtime.alarm.model.dto.AlarmToHazardRequest;
import com.fgroupboss.ai.psm.realtime.api.alarm.vo.AlarmAreaActiveCheckVO;
import com.fgroupboss.ai.psm.realtime.alarm.client.RemoteHazardReportVO;
import com.fgroupboss.ai.psm.realtime.alarm.model.vo.AlarmDetailVO;
import com.fgroupboss.ai.psm.realtime.api.alarm.vo.AlarmEventVO;
import com.fgroupboss.ai.psm.realtime.alarm.model.vo.AlarmHealthVO;
import com.fgroupboss.ai.psm.common.PageResult;

import java.util.Date;

public interface AlarmService {

    AlarmHealthVO health();

    AlarmEventVO ingest(AlarmIngestRequest request);

    PageResult<AlarmEventVO> page(Long tenantId, String keyword, String status, String alarmLevel,
                                  Long areaId, Long hazardId, String sourceType, Date occurredFrom, Date occurredTo,
                                  int pageNo, int pageSize);

    AlarmDetailVO getDetail(Long tenantId, Long id);

    AlarmEventVO confirm(Long tenantId, Long id, AlarmActionRequest request, String operator);

    AlarmEventVO dispatch(Long tenantId, Long id, AlarmActionRequest request, String operator);

    AlarmEventVO feedback(Long tenantId, Long id, AlarmActionRequest request, String operator);

    AlarmEventVO close(Long tenantId, Long id, AlarmActionRequest request, String operator);

    AlarmEventVO falseClose(Long tenantId, Long id, AlarmFalseCloseRequest request, String operator);

    AlarmAreaActiveCheckVO areaActiveCheck(AlarmAreaActiveCheckRequest request);

    RemoteHazardReportVO toHazard(Long tenantId, Long id, AlarmToHazardRequest request);
}

