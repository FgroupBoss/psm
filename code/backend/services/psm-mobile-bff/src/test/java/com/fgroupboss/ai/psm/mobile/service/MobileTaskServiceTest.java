package com.fgroupboss.ai.psm.mobile.service;

import com.fgroupboss.ai.psm.mobile.client.AlarmClient;
import com.fgroupboss.ai.psm.mobile.client.WorkPermitClient;
import com.fgroupboss.ai.psm.operation.api.workpermit.vo.WorkPermitVO;
import com.fgroupboss.ai.psm.mobile.config.MobileRole;
import com.fgroupboss.ai.psm.mobile.model.vo.MobileTaskVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MobileTaskServiceTest {

    @Mock
    private WorkPermitClient workPermitClient;

    @Mock
    private AlarmClient alarmClient;

    @InjectMocks
    private MobileTaskService mobileTaskService;

    @Test
    void listTasks_filtersGuardianPermits() {
        WorkPermitVO mine = permit(1L, "IN_PROGRESS", 100L);
        WorkPermitVO others = permit(2L, "IN_PROGRESS", 200L);
        when(workPermitClient.page(eq(1L), eq("IN_PROGRESS"), eq(1), anyInt()))
                .thenReturn(Arrays.asList(mine, others));
        when(workPermitClient.page(eq(1L), eq("SUSPENDED"), eq(1), anyInt()))
                .thenReturn(Collections.<WorkPermitVO>emptyList());
        when(alarmClient.listPending(eq(1L), anyInt())).thenReturn(Collections.emptyList());

        List<MobileTaskVO> tasks = mobileTaskService.listTasks(1L, 100L, MobileRole.GUARDIAN);

        assertEquals(1, tasks.size());
        assertEquals("MONITOR", tasks.get(0).getTaskType());
        assertEquals(1L, tasks.get(0).getBizId().longValue());
    }

    @Test
    void listTasks_includesSitePermitForAllRole() {
        WorkPermitVO permit = permit(10L, "PENDING_SITE_PERMIT", 50L);
        when(workPermitClient.page(eq(2L), eq("PENDING_SITE_PERMIT"), eq(1), anyInt()))
                .thenReturn(Collections.singletonList(permit));
        when(workPermitClient.page(eq(2L), eq("IN_PROGRESS"), eq(1), anyInt()))
                .thenReturn(Collections.<WorkPermitVO>emptyList());
        when(workPermitClient.page(eq(2L), eq("SUSPENDED"), eq(1), anyInt()))
                .thenReturn(Collections.<WorkPermitVO>emptyList());
        when(workPermitClient.page(eq(2L), eq("PENDING_ACCEPTANCE"), eq(1), anyInt()))
                .thenReturn(Collections.<WorkPermitVO>emptyList());
        when(alarmClient.listPending(eq(2L), anyInt())).thenReturn(Collections.emptyList());

        List<MobileTaskVO> tasks = mobileTaskService.listTasks(2L, null, MobileRole.ALL);

        assertTrue(tasks.stream().anyMatch(t -> "SITE_PERMIT".equals(t.getTaskType())));
    }

    private WorkPermitVO permit(Long id, String status, Long guardianUserId) {
        WorkPermitVO vo = new WorkPermitVO();
        vo.setId(id);
        vo.setStatus(status);
        vo.setTitle("WP-" + id);
        vo.setGuardianUserId(guardianUserId);
        vo.setPermitIssuerUserId(guardianUserId);
        vo.setSupervisorUserId(guardianUserId);
        return vo;
    }
}
