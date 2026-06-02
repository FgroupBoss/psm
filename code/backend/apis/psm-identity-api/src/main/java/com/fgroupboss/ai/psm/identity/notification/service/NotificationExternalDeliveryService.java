package com.fgroupboss.ai.psm.identity.notification.service;

import com.fgroupboss.ai.psm.identity.notification.config.DeliveryStatus;
import com.fgroupboss.ai.psm.identity.notification.config.NotificationChannel;
import com.fgroupboss.ai.psm.identity.notification.mapper.NotificationDeliveryLogMapper;
import com.fgroupboss.ai.psm.identity.notification.model.entity.NotificationDeliveryLogEntity;
import com.fgroupboss.ai.psm.identity.notification.outbound.NotificationOutboundRouter;
import com.fgroupboss.ai.psm.identity.notification.outbound.OutboundSendRequest;
import com.fgroupboss.ai.psm.identity.notification.outbound.OutboundSendResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationExternalDeliveryService {

    private final NotificationDeliveryLogMapper deliveryLogMapper;
    private final NotificationOutboundRouter outboundRouter;

    @Async
    public void deliverAsync(Long tenantId, Long messageId, String channel, String deliveryRequestId,
                             OutboundSendRequest outboundRequest) {
        NotificationDeliveryLogEntity existing = findDeliveryLog(tenantId, deliveryRequestId, channel);
        if (existing != null && DeliveryStatus.SENT.equals(existing.getStatus())) {
            return;
        }
        NotificationDeliveryLogEntity logEntity = existing != null ? existing : newLog(tenantId, messageId, channel, deliveryRequestId);
        logEntity.setStatus(DeliveryStatus.PENDING);
        if (existing == null) {
            deliveryLogMapper.insert(logEntity);
        } else {
            deliveryLogMapper.updateById(logEntity);
        }

        if (NotificationChannel.SMS.equals(channel) && !StringUtils.hasText(outboundRequest.getRecipientPhone())) {
            finish(logEntity, OutboundSendResult.skipped("recipientPhone missing"));
            return;
        }
        if (NotificationChannel.EMAIL.equals(channel) && !StringUtils.hasText(outboundRequest.getRecipientEmail())) {
            finish(logEntity, OutboundSendResult.skipped("recipientEmail missing"));
            return;
        }

        OutboundSendResult result = outboundRouter.dispatch(outboundRequest);
        finish(logEntity, result);
    }

    private void finish(NotificationDeliveryLogEntity logEntity, OutboundSendResult result) {
        logEntity.setStatus(result.getStatus());
        logEntity.setProviderMsgId(result.getProviderMsgId());
        logEntity.setErrorCode(result.getErrorCode());
        logEntity.setErrorMessage(result.getErrorMessage());
        if (DeliveryStatus.SENT.equals(result.getStatus())) {
            logEntity.setSentAt(new Date());
        }
        deliveryLogMapper.updateById(logEntity);
    }

    private NotificationDeliveryLogEntity newLog(Long tenantId, Long messageId, String channel, String requestId) {
        NotificationDeliveryLogEntity logEntity = new NotificationDeliveryLogEntity();
        logEntity.setTenantId(tenantId);
        logEntity.setMessageId(messageId);
        logEntity.setChannel(channel);
        logEntity.setRequestId(requestId);
        logEntity.setCreatedAt(new Date());
        return logEntity;
    }

    private NotificationDeliveryLogEntity findDeliveryLog(Long tenantId, String requestId, String channel) {
        if (!StringUtils.hasText(requestId)) {
            return null;
        }
        return deliveryLogMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<NotificationDeliveryLogEntity>()
                        .eq(NotificationDeliveryLogEntity::getTenantId, tenantId)
                        .eq(NotificationDeliveryLogEntity::getRequestId, requestId)
                        .eq(NotificationDeliveryLogEntity::getChannel, channel)
                        .last("limit 1"));
    }
}
