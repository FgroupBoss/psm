package com.fgroupboss.ai.psm.identity.notification.service;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.identity.notification.model.dto.NotificationSendRequest;
import com.fgroupboss.ai.psm.identity.notification.model.vo.NotificationChannelStatusVO;
import com.fgroupboss.ai.psm.identity.notification.model.vo.NotificationHealthVO;
import com.fgroupboss.ai.psm.identity.notification.model.vo.NotificationMessageVO;
import com.fgroupboss.ai.psm.identity.notification.model.vo.NotificationUnreadCountVO;

import java.util.List;

public interface NotificationService {

    NotificationHealthVO health();

    NotificationMessageVO send(NotificationSendRequest request);

    PageResult<NotificationMessageVO> inbox(Long tenantId, Long userId, Boolean read, String bizType,
                                          int pageNo, int pageSize);

    NotificationMessageVO get(Long tenantId, Long userId, Long id);

    void markRead(Long tenantId, Long userId, Long id);

    int markAllRead(Long tenantId, Long userId);

    NotificationUnreadCountVO unreadCount(Long tenantId, Long userId);

    List<NotificationChannelStatusVO> channelStatus();
}
