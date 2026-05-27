package com.fgroupboss.ai.psm.alarm.service;

import com.fgroupboss.ai.psm.alarm.mapper.AlarmActionRecordMapper;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmEventMapper;
import com.fgroupboss.ai.psm.alarm.mapper.AlarmOccurrenceMapper;
import com.fgroupboss.ai.psm.alarm.model.dto.AlarmActionRequest;
import com.fgroupboss.ai.psm.alarm.model.dto.AlarmFalseCloseRequest;
import com.fgroupboss.ai.psm.alarm.model.entity.AlarmEventEntity;
import com.fgroupboss.ai.psm.alarm.model.vo.AlarmEventVO;
import com.fgroupboss.ai.psm.alarm.service.impl.AlarmServiceImpl;
import com.fgroupboss.ai.psm.alarm.support.AlarmAuditSupport;
import com.fgroupboss.ai.psm.alarm.support.AlarmDedupSupport;
import com.fgroupboss.ai.psm.common.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AlarmLifecycleTest {

    private AlarmEventMapper alarmEventMapper;
    private AlarmOccurrenceMapper occurrenceMapper;
    private AlarmActionRecordMapper actionRecordMapper;
    private AlarmAuditSupport auditSupport;
    private AlarmService service;

    @BeforeEach
    void setUp() {
        alarmEventMapper = mock(AlarmEventMapper.class);
        occurrenceMapper = mock(AlarmOccurrenceMapper.class);
        actionRecordMapper = mock(AlarmActionRecordMapper.class);
        auditSupport = mock(AlarmAuditSupport.class);
        AlarmDedupSupport dedupSupport = mock(AlarmDedupSupport.class);
        service = new AlarmServiceImpl(alarmEventMapper, occurrenceMapper, actionRecordMapper, auditSupport, dedupSupport);
    }

    @Test
    void confirmShouldMoveNewToConfirmed() {
        stubEvent(1L, "NEW");
        AlarmEventVO result = service.confirm(1L, 1L, new AlarmActionRequest(), "admin");
        assertEquals("CONFIRMED", result.getStatus());
        verify(auditSupport).writeAction(eq(1L), eq(1L), eq("CONFIRM"), any(), eq("admin"), eq("NEW"), eq("CONFIRMED"));
    }

    @Test
    void fullLifecycleShouldReachClosed() {
        stubEvent(2L, "NEW");
        service.confirm(1L, 2L, new AlarmActionRequest(), "admin");
        stubEvent(2L, "CONFIRMED");
        service.dispatch(1L, 2L, assignRequest("张三"), "admin");
        stubEvent(2L, "IN_PROGRESS");
        service.feedback(1L, 2L, contentRequest("现场已处置"), "admin");
        stubEvent(2L, "PENDING_REVIEW");
        AlarmEventVO closed = service.close(1L, 2L, contentRequest("复核通过"), "admin");
        assertEquals("CLOSED", closed.getStatus());
    }

    @Test
    void confirmShouldRejectConfirmedStatus() {
        stubEvent(3L, "CONFIRMED");
        assertThrows(BusinessException.class, () -> service.confirm(1L, 3L, new AlarmActionRequest(), "admin"));
    }

    @Test
    void dispatchShouldRejectNewStatus() {
        stubEvent(4L, "NEW");
        assertThrows(BusinessException.class, () -> service.dispatch(1L, 4L, new AlarmActionRequest(), "admin"));
    }

    @Test
    void feedbackShouldRejectNewStatus() {
        stubEvent(5L, "NEW");
        assertThrows(BusinessException.class, () -> service.feedback(1L, 5L, new AlarmActionRequest(), "admin"));
    }

    @Test
    void closeShouldRejectInProgressStatus() {
        stubEvent(6L, "IN_PROGRESS");
        assertThrows(BusinessException.class, () -> service.close(1L, 6L, new AlarmActionRequest(), "admin"));
    }

    @Test
    void falseCloseShouldRequireReasonInControllerValidation() {
        stubEvent(7L, "NEW");
        AlarmFalseCloseRequest request = new AlarmFalseCloseRequest();
        request.setReason("仪表误报");
        AlarmEventVO result = service.falseClose(1L, 7L, request, "admin");
        assertEquals("FALSE_CLOSED", result.getStatus());
        verify(auditSupport).writeAction(eq(1L), eq(7L), eq("FALSE_CLOSE"), eq("仪表误报"),
                eq("admin"), eq("NEW"), eq("FALSE_CLOSED"));
    }

    @Test
    void falseCloseShouldRejectClosedStatus() {
        stubEvent(8L, "CLOSED");
        AlarmFalseCloseRequest request = new AlarmFalseCloseRequest();
        request.setReason("重复关闭");
        assertThrows(BusinessException.class, () -> service.falseClose(1L, 8L, request, "admin"));
    }

    @Test
    void confirmShouldRejectClosedAlarm() {
        stubEvent(9L, "CLOSED");
        assertThrows(BusinessException.class, () -> service.confirm(1L, 9L, new AlarmActionRequest(), "admin"));
    }

    private void stubEvent(Long id, String status) {
        AlarmEventEntity entity = new AlarmEventEntity();
        entity.setId(id);
        entity.setTenantId(1L);
        entity.setAlarmNo("ALM-" + id);
        entity.setSourceType("GDS");
        entity.setTitle("测试");
        entity.setAlarmLevel("LEVEL_1");
        entity.setStatus(status);
        entity.setDeleted(0);
        when(alarmEventMapper.selectById(id)).thenReturn(entity);
        when(alarmEventMapper.updateById(any(AlarmEventEntity.class))).thenAnswer(invocation -> {
            AlarmEventEntity updated = invocation.getArgument(0);
            entity.setStatus(updated.getStatus());
            return 1;
        });
    }

    private AlarmActionRequest contentRequest(String content) {
        AlarmActionRequest request = new AlarmActionRequest();
        request.setContent(content);
        return request;
    }

    private AlarmActionRequest assignRequest(String assignee) {
        AlarmActionRequest request = new AlarmActionRequest();
        request.setAssignee(assignee);
        return request;
    }
}
