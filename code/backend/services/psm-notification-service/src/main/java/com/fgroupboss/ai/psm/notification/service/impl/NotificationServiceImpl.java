package com.fgroupboss.ai.psm.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.notification.config.DeliveryStatus;
import com.fgroupboss.ai.psm.notification.config.NotificationChannel;
import com.fgroupboss.ai.psm.notification.mapper.NotificationDeliveryLogMapper;
import com.fgroupboss.ai.psm.notification.mapper.NotificationMessageMapper;
import com.fgroupboss.ai.psm.notification.model.dto.NotificationSendRequest;
import com.fgroupboss.ai.psm.notification.model.entity.NotificationDeliveryLogEntity;
import com.fgroupboss.ai.psm.notification.model.entity.NotificationMessageEntity;
import com.fgroupboss.ai.psm.notification.model.vo.NotificationHealthVO;
import com.fgroupboss.ai.psm.notification.model.vo.NotificationMessageVO;
import com.fgroupboss.ai.psm.notification.service.NotificationService;
import com.fgroupboss.ai.psm.notification.support.NotificationTemplateSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMessageMapper messageMapper;
    private final NotificationDeliveryLogMapper deliveryLogMapper;

    @Override
    public NotificationHealthVO health() {
        NotificationHealthVO vo = new NotificationHealthVO();
        vo.setService("psm-notification-service");
        vo.setModule("notification");
        vo.setVersion("1.0.0");
        vo.setMessageCount(messageMapper.selectCount(null));
        return vo;
    }

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
        NotificationMessageEntity message = new NotificationMessageEntity();
        message.setTenantId(request.getTenantId());
        message.setUserId(request.getUserId());
        message.setRequestId(trim(request.getRequestId()));
        message.setChannel(NotificationChannel.IN_APP);
        message.setTemplateCode(request.getTemplateCode().trim());
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
        logEntity.setChannel(NotificationChannel.IN_APP);
        logEntity.setStatus(DeliveryStatus.SENT);
        logEntity.setSentAt(new Date());
        logEntity.setCreatedAt(new Date());
        deliveryLogMapper.insert(logEntity);

        log.info("notification sent id={} tenantId={} userId={} template={}",
                message.getId(), message.getTenantId(), message.getUserId(), message.getTemplateCode());
        return toVo(message);
    }

    @Override
    public PageResult<NotificationMessageVO> inbox(Long tenantId, Long userId, Boolean read, int pageNo, int pageSize) {
        requireTenantUser(tenantId, userId);
        int safePageNo = Math.max(pageNo, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        LambdaQueryWrapper<NotificationMessageEntity> wrapper = new LambdaQueryWrapper<NotificationMessageEntity>()
                .eq(NotificationMessageEntity::getTenantId, tenantId)
                .eq(NotificationMessageEntity::getUserId, userId);
        if (read != null) {
            wrapper.eq(NotificationMessageEntity::getReadFlag, read ? 1 : 0);
        }
        wrapper.orderByDesc(NotificationMessageEntity::getCreatedAt);
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
