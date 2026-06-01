package com.fgroupboss.ai.psm.identity.notification.controller;

import com.fgroupboss.ai.psm.common.PageResult;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.identity.notification.model.dto.NotificationSendRequest;
import com.fgroupboss.ai.psm.identity.notification.model.vo.NotificationHealthVO;
import com.fgroupboss.ai.psm.identity.notification.model.vo.NotificationMessageVO;
import com.fgroupboss.ai.psm.identity.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 消息中心：站内信发送与收件箱。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 接口用途：查询服务健康状态。
     */
    @GetMapping("/health")
    public ResponseVO<NotificationHealthVO> health() {
        return ResponseVO.success(notificationService.health());
    }

    /**
     * 接口用途：发送站内信（业务/内部调用，支持 requestId 幂等）。
     */
    @PostMapping("/send")
    public ResponseVO<NotificationMessageVO> send(@Valid @RequestBody NotificationSendRequest request) {
        return ResponseVO.success(notificationService.send(request));
    }

    /**
     * 接口用途：分页查询用户收件箱。
     */
    @GetMapping("/inbox")
    public ResponseVO<PageResult<NotificationMessageVO>> inbox(@RequestParam Long tenantId,
                                                               @RequestParam Long userId,
                                                               @RequestParam(required = false) Boolean read,
                                                               @RequestParam(defaultValue = "1") int pageNo,
                                                               @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseVO.success(notificationService.inbox(tenantId, userId, read, pageNo, pageSize));
    }

    /**
     * 接口用途：查询消息详情。
     */
    @GetMapping("/{id}")
    public ResponseVO<NotificationMessageVO> detail(@PathVariable Long id,
                                                    @RequestParam Long tenantId,
                                                    @RequestParam Long userId) {
        return ResponseVO.success(notificationService.get(tenantId, userId, id));
    }

    /**
     * 接口用途：标记单条消息已读。
     */
    @PostMapping("/{id}/read")
    public ResponseVO<Void> markRead(@PathVariable Long id,
                                     @RequestParam Long tenantId,
                                     @RequestParam Long userId) {
        notificationService.markRead(tenantId, userId, id);
        return ResponseVO.success(null);
    }

    /**
     * 接口用途：标记全部消息已读。
     */
    @PostMapping("/read-all")
    public ResponseVO<Integer> markAllRead(@RequestParam Long tenantId,
                                           @RequestParam Long userId) {
        return ResponseVO.success(notificationService.markAllRead(tenantId, userId));
    }
}
