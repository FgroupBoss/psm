package com.fgroupboss.ai.psm.identity.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.identity.notification.config.DeliveryStatus;
import com.fgroupboss.ai.psm.identity.notification.config.NotificationChannel;
import com.fgroupboss.ai.psm.identity.notification.config.NotificationChannelProperties;
import com.fgroupboss.ai.psm.identity.notification.mapper.NotificationDeliveryLogMapper;
import com.fgroupboss.ai.psm.identity.notification.mapper.NotificationMessageMapper;
import com.fgroupboss.ai.psm.identity.notification.model.dto.NotificationSendRequest;
import com.fgroupboss.ai.psm.identity.notification.model.entity.NotificationDeliveryLogEntity;
import com.fgroupboss.ai.psm.identity.notification.model.entity.NotificationMessageEntity;
import com.fgroupboss.ai.psm.identity.notification.model.vo.NotificationChannelStatusVO;
import com.fgroupboss.ai.psm.identity.notification.model.vo.NotificationHealthVO;
import com.fgroupboss.ai.psm.identity.notification.model.vo.NotificationMessageVO;
import com.fgroupboss.ai.psm.identity.notification.model.vo.NotificationUnreadCountVO;
import com.fgroupboss.ai.psm.identity.notification.outbound.OutboundSendRequest;
import com.fgroupboss.ai.psm.identity.notification.service.NotificationExternalDeliveryService;
import com.fgroupboss.ai.psm.identity.notification.service.NotificationService;
import com.fgroupboss.ai.psm.identity.notification.support.NotificationRecipientResolver;
import com.fgroupboss.ai.psm.identity.notification.support.NotificationTemplateSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 消息通知服务实现。
 *
 * <p>站内信落库；短信/邮件/企微/钉钉经 {@link NotificationExternalDeliveryService} 异步外送并记投递日志。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final List<String> EXTERNAL_CHANNELS = Arrays.asList(
            NotificationChannel.SMS,
            NotificationChannel.EMAIL,
            NotificationChannel.WECHAT,
            NotificationChannel.DINGTALK);

    private final NotificationMessageMapper messageMapper;
    private final NotificationDeliveryLogMapper deliveryLogMapper;
    private final NotificationExternalDeliveryService externalDeliveryService;
    private final NotificationChannelProperties channelProperties;

    @Override
    public NotificationHealthVO health() {
        NotificationHealthVO vo = new NotificationHealthVO();
        vo.setService("psm-notification-service");
        vo.setModule("notification");
        vo.setVersion("1.0.0");
        vo.setMessageCount(messageMapper.selectCount(null));
        return vo;
    }

    /**
     * 实现方式：校验入参、按 requestId 幂等；写站内信并异步派发已启用的外通道。
     */
    @Override
    @Transactional
    public NotificationMessageVO send(NotificationSendRequest request) {
        validateSend(request);
        NotificationMessageEntity existing = findByRequestId(request);
        if (existing != null) {
            return toVo(existing);
        }
        String title = request.getTitle();
        String content = request.getContent();
        if (!StringUtils.hasText(title) || !StringUtils.hasText(content)) {
            NotificationTemplateSupport.RenderedTemplate rendered =
                    NotificationTemplateSupport.render(request.getTemplateCode(), request.getVariables());
            if (!StringUtils.hasText(title)) {
                title = rendered.getTitle();
            }
            if (!StringUtils.hasText(content)) {
                content = rendered.getContent();
            }
        }

        List<String> channels = resolveChannels(request);
        NotificationMessageVO result = null;
        if (channels.contains(NotificationChannel.IN_APP)) {
            NotificationMessageEntity message = new NotificationMessageEntity();
            message.setTenantId(request.getTenantId());
            message.setUserId(request.getUserId());
            message.setRequestId(trim(request.getRequestId()));
            message.setChannel(NotificationChannel.IN_APP);
            message.setTemplateCode(trim(request.getTemplateCode()));
            message.setTitle(title.trim());
            message.setContent(content.trim());
            message.setBizType(trim(request.getBizType()));
            message.setBizId(request.getBizId());
            message.setReadFlag(0);
            message.setCreatedAt(new Date());
            messageMapper.insert(message);

            NotificationDeliveryLogEntity logEntity = new NotificationDeliveryLogEntity();
            logEntity.setTenantId(message.getTenantId());
            logEntity.setMessageId(message.getId());
            logEntity.setRequestId(buildDeliveryRequestId(request.getRequestId(), NotificationChannel.IN_APP));
            logEntity.setChannel(NotificationChannel.IN_APP);
            logEntity.setStatus(DeliveryStatus.SENT);
            logEntity.setSentAt(new Date());
            logEntity.setCreatedAt(new Date());
            deliveryLogMapper.insert(logEntity);

            log.info("notification sent id={} tenantId={} userId={} template={}",
                    message.getId(), message.getTenantId(), message.getUserId(), message.getTemplateCode());
            result = toVo(message);
            dispatchExternalChannels(request, channels, title, content, message.getId());
        } else {
            dispatchExternalChannels(request, channels, title, content, null);
            NotificationMessageVO externalOnly = new NotificationMessageVO();
            externalOnly.setTenantId(request.getTenantId());
            externalOnly.setUserId(request.getUserId());
            externalOnly.setTitle(title);
            externalOnly.setContent(content);
            result = externalOnly;
        }
        return result;
    }

    private void dispatchExternalChannels(NotificationSendRequest request, List<String> channels,
                                          String title, String content, Long messageId) {
        OutboundSendRequest outbound = new OutboundSendRequest();
        outbound.setTenantId(request.getTenantId());
        outbound.setUserId(request.getUserId());
        outbound.setTemplateCode(trim(request.getTemplateCode()));
        outbound.setTitle(title);
        outbound.setContent(content);
        outbound.setRecipientPhone(NotificationRecipientResolver.resolvePhone(request));
        outbound.setRecipientEmail(NotificationRecipientResolver.resolveEmail(request));
        outbound.setVariables(request.getVariables());

        for (String channel : channels) {
            if (!EXTERNAL_CHANNELS.contains(channel)) {
                continue;
            }
            outbound.setChannel(channel);
            String deliveryRequestId = buildDeliveryRequestId(request.getRequestId(), channel);
            outbound.setRequestId(deliveryRequestId);
            externalDeliveryService.deliverAsync(
                    request.getTenantId(), messageId, channel, deliveryRequestId, outbound);
        }
    }

    /**
     * 实现方式：按租户、用户分页查询站内信，支持已读状态与 bizType 过滤。
     */
    @Override
    public PageResult<NotificationMessageVO> inbox(Long tenantId, Long userId, Boolean read, String bizType,
                                          int pageNo, int pageSize) {
        requireTenantUser(tenantId, userId);
        int safePageNo = Math.max(pageNo, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        LambdaQueryWrapper<NotificationMessageEntity> wrapper = new LambdaQueryWrapper<NotificationMessageEntity>()
                .eq(NotificationMessageEntity::getTenantId, tenantId)
                .eq(NotificationMessageEntity::getUserId, userId);
        if (read != null) {
            wrapper.eq(NotificationMessageEntity::getReadFlag, read ? 1 : 0);
        }
        if (StringUtils.hasText(bizType)) {
            wrapper.eq(NotificationMessageEntity::getBizType, bizType.trim());
        }
        wrapper.orderByAsc(NotificationMessageEntity::getReadFlag)
                .orderByDesc(NotificationMessageEntity::getCreatedAt);
        Page<NotificationMessageEntity> page = messageMapper.selectPage(
                new Page<NotificationMessageEntity>(safePageNo, safePageSize), wrapper);
        List<NotificationMessageVO> records = new ArrayList<NotificationMessageVO>();
        for (NotificationMessageEntity entity : page.getRecords()) {
            records.add(toVo(entity));
        }
        return new PageResult<NotificationMessageVO>(page.getTotal(), safePageNo, safePageSize, records);
    }

    @Override
    public NotificationMessageVO get(Long tenantId, Long userId, Long id) {
        NotificationMessageEntity entity = requireMessage(tenantId, userId, id);
        return toVo(entity);
    }

    @Override
    @Transactional
    public void markRead(Long tenantId, Long userId, Long id) {
        NotificationMessageEntity entity = requireMessage(tenantId, userId, id);
        if (entity.getReadFlag() != null && entity.getReadFlag() == 1) {
            return;
        }
        entity.setReadFlag(1);
        entity.setReadAt(new Date());
        messageMapper.updateById(entity);
    }

    @Override
    @Transactional
    public int markAllRead(Long tenantId, Long userId) {
        requireTenantUser(tenantId, userId);
        NotificationMessageEntity patch = new NotificationMessageEntity();
        patch.setReadFlag(1);
        patch.setReadAt(new Date());
        return messageMapper.update(patch, new LambdaUpdateWrapper<NotificationMessageEntity>()
                .eq(NotificationMessageEntity::getTenantId, tenantId)
                .eq(NotificationMessageEntity::getUserId, userId)
                .eq(NotificationMessageEntity::getReadFlag, 0));
    }

    /**
     * 实现方式：按租户与用户统计 readFlag=0 的站内信条数，供角标展示。
     */
    @Override
    public NotificationUnreadCountVO unreadCount(Long tenantId, Long userId) {
        requireTenantUser(tenantId, userId);
        Long count = messageMapper.selectCount(new LambdaQueryWrapper<NotificationMessageEntity>()
                .eq(NotificationMessageEntity::getTenantId, tenantId)
                .eq(NotificationMessageEntity::getUserId, userId)
                .eq(NotificationMessageEntity::getReadFlag, 0));
        NotificationUnreadCountVO vo = new NotificationUnreadCountVO();
        vo.setUnreadCount(count == null ? 0L : count.longValue());
        return vo;
    }

    /**
     * 实现方式：读取 {@code psm.notification.*} 配置，返回各外通道启用/模式/是否已配网关（不含密钥）。
     */
    @Override
    public List<NotificationChannelStatusVO> channelStatus() {
        List<NotificationChannelStatusVO> list = new ArrayList<NotificationChannelStatusVO>();
        list.add(buildChannelStatus(NotificationChannel.SMS, channelProperties.getSms()));
        list.add(buildChannelStatus(NotificationChannel.EMAIL, channelProperties.getEmail()));
        list.add(buildChannelStatus(NotificationChannel.WECHAT, channelProperties.getWechat()));
        list.add(buildChannelStatus(NotificationChannel.DINGTALK, channelProperties.getDingtalk()));
        return list;
    }

    private NotificationChannelStatusVO buildChannelStatus(String channel,
                                                           NotificationChannelProperties.ChannelSettings settings) {
        NotificationChannelStatusVO vo = new NotificationChannelStatusVO();
        vo.setChannel(channel);
        vo.setEnabled(settings.isEnabled());
        vo.setMode(settings.getMode());
        vo.setConfigured(settings.isEnabled()
                && ("HTTP".equalsIgnoreCase(settings.getMode()) && StringUtils.hasText(settings.getHttpUrl())));
        return vo;
    }

    private List<String> resolveChannels(NotificationSendRequest request) {
        Set<String> resolved = new LinkedHashSet<String>();
        if (request.getChannels() != null) {
            for (String channel : request.getChannels()) {
                if (StringUtils.hasText(channel)) {
                    resolved.add(channel.trim().toUpperCase());
                }
            }
        }
        if (resolved.isEmpty()) {
            resolved.add(NotificationChannel.IN_APP);
            if (channelProperties.isAutoExternal()) {
                appendEnabledExternal(resolved);
            }
        }
        if (!resolved.contains(NotificationChannel.IN_APP) && resolved.isEmpty()) {
            resolved.add(NotificationChannel.IN_APP);
        }
        return new ArrayList<String>(resolved);
    }

    private void appendEnabledExternal(Set<String> resolved) {
        if (channelProperties.getSms().isEnabled()) {
            resolved.add(NotificationChannel.SMS);
        }
        if (channelProperties.getEmail().isEnabled()) {
            resolved.add(NotificationChannel.EMAIL);
        }
        if (channelProperties.getWechat().isEnabled()) {
            resolved.add(NotificationChannel.WECHAT);
        }
        if (channelProperties.getDingtalk().isEnabled()) {
            resolved.add(NotificationChannel.DINGTALK);
        }
    }

    private String buildDeliveryRequestId(String baseRequestId, String channel) {
        String base = StringUtils.hasText(baseRequestId) ? baseRequestId.trim() : "auto";
        return base + ":" + channel;
    }

    private NotificationMessageEntity findByRequestId(NotificationSendRequest request) {
        if (!StringUtils.hasText(request.getRequestId())) {
            return null;
        }
        return messageMapper.selectOne(new LambdaQueryWrapper<NotificationMessageEntity>()
                .eq(NotificationMessageEntity::getTenantId, request.getTenantId())
                .eq(NotificationMessageEntity::getUserId, request.getUserId())
                .eq(NotificationMessageEntity::getRequestId, request.getRequestId().trim())
                .last("limit 1"));
    }

    private NotificationMessageEntity requireMessage(Long tenantId, Long userId, Long id) {
        requireTenantUser(tenantId, userId);
        if (id == null) {
            throw new BusinessException(400, "id is required");
        }
        NotificationMessageEntity entity = messageMapper.selectOne(new LambdaQueryWrapper<NotificationMessageEntity>()
                .eq(NotificationMessageEntity::getTenantId, tenantId)
                .eq(NotificationMessageEntity::getUserId, userId)
                .eq(NotificationMessageEntity::getId, id)
                .last("limit 1"));
        if (entity == null) {
            throw new BusinessException(404, "notification not found");
        }
        return entity;
    }

    private void validateSend(NotificationSendRequest request) {
        if (request == null) {
            throw new BusinessException(400, "request is required");
        }
        requireTenantUser(request.getTenantId(), request.getUserId());
        if (!StringUtils.hasText(request.getTemplateCode())
                && (!StringUtils.hasText(request.getTitle()) || !StringUtils.hasText(request.getContent()))) {
            throw new BusinessException(400, "templateCode or title/content is required");
        }
    }

    private void requireTenantUser(Long tenantId, Long userId) {
        if (tenantId == null) {
            throw new BusinessException(400, "tenantId is required");
        }
        if (userId == null) {
            throw new BusinessException(400, "userId is required");
        }
    }

    private NotificationMessageVO toVo(NotificationMessageEntity entity) {
        NotificationMessageVO vo = new NotificationMessageVO();
        vo.setId(entity.getId());
        vo.setTenantId(entity.getTenantId());
        vo.setUserId(entity.getUserId());
        vo.setChannel(entity.getChannel());
        vo.setTemplateCode(entity.getTemplateCode());
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setBizType(entity.getBizType());
        vo.setBizId(entity.getBizId());
        vo.setRead(entity.getReadFlag() != null && entity.getReadFlag() == 1);
        vo.setReadAt(entity.getReadAt());
        vo.setCreatedAt(entity.getCreatedAt());
        return vo;
    }

    private String trim(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
